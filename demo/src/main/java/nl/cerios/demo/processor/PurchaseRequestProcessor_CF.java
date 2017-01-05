package nl.cerios.demo.processor;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.logging.Logger;

import nl.cerios.demo.http.HttpRequestData;
import nl.cerios.demo.http.PurchaseHttpHandler;
import nl.cerios.demo.service.CustomerData;
import nl.cerios.demo.service.CustomerValidation;
import nl.cerios.demo.service.LocationConfig;
import nl.cerios.demo.service.OrderData;
import nl.cerios.demo.service.PurchaseRequest;
import nl.cerios.demo.service.Status;
import nl.cerios.demo.service.TransactionValidation;
import nl.cerios.demo.service.ValidationException;


public class PurchaseRequestProcessor_CF extends BaseProcessor {
	private static final Logger LOG = Logger.getLogger(PurchaseRequestProcessor_CF.class.getName());


	public CompletableFuture<Void> handle(HttpRequestData requestData, PurchaseHttpHandler purchaseHandler)
	{
		CompletableFuture<PurchaseRequest> purchaseRequestCF=
				purchaseRequestController.getPurchaseRequest_CF( requestData.getPurchaseRequestId());

		return purchaseRequestCF.thenComposeAsync(
				purchaseRequest -> {
					CompletableFuture<CustomerData> customerDataCF= customerService.getCustomerData_CF( purchaseRequest.getCustomerId());

					if (purchaseRequest.getLocationId() == null)
						throw new IllegalStateException(new ValidationException( "Invalid location"));
					CompletableFuture<LocationConfig> locationDataCF= locationService_CF.getLocationConfig(purchaseRequest.getLocationId());

					CompletableFuture<CustomerValidation> customerValidationCF=
							customerDataCF.
							thenCombineAsync(locationDataCF, (customerData, locationData)
									-> customerService.validateCustomer_CF( customerData, locationData))
							.thenComposeAsync( Function.identity())
							.thenApplyAsync(  customerValidation 
									-> {
										LOG.info( " " + customerValidation.getStatus());
										if (customerValidation.getStatus() != Status.OK)
											throw new IllegalStateException(new ValidationException(customerValidation.getMessage())); 
										else return customerValidation;
									});


					CompletableFuture<TransactionValidation> transactionValidationCF=
							customerValidationCF                   // don't use the result, only the error and the trigger
							.thenComposeAsync( dummy -> customerDataCF)  
							.thenComposeAsync( 
									customerData -> transactionService.validate_CF( purchaseRequest, customerData))
							.thenApplyAsync(  transactionValidation
									-> { 
										LOG.info( " " + transactionValidation.getStatus());
										if (transactionValidation.getStatus() != Status.OK)
											throw new IllegalStateException(new ValidationException(transactionValidation.getMessage())); 
										else return transactionValidation;
									});

					Function<TransactionValidation,CompletableFuture<Void>> order90Filter= dummy -> {
							if (purchaseRequest.getPurchaseRequestId()==10) 
								return CompletableFuture.completedFuture(null);
							else 
								return new CompletableFuture<Void>();
							};
					Function<TransactionValidation,CompletableFuture<Void>> order100Filter= dummy -> {
								if (purchaseRequest.getPurchaseRequestId()!=10) 
									return CompletableFuture.completedFuture(null);
								else 
									return new CompletableFuture<Void>();
								};

							
					CompletableFuture<OrderData> orderdataCF_1=
							transactionValidationCF                   // don't use the result, only the error and the trigger
							.thenComposeAsync( order90Filter)
							.thenComposeAsync( dummy -> orderService.createOrder90_CF( purchaseRequest));

					CompletableFuture<OrderData> orderdataCF_2=
							transactionValidationCF                   // don't use the result, only the error and the trigger
							.thenComposeAsync( order100Filter)
							.thenComposeAsync( dummy -> orderService.createOrder100_CF( purchaseRequest));
					
					CompletableFuture<Object> orderdataCF= CompletableFuture.anyOf( orderdataCF_1, orderdataCF_2);
					
					CompletableFuture<PurchaseRequest> updatedPurchaseRequestCF=
							orderdataCF.thenComposeAsync( orderData 
									-> purchaseRequestController.update_CF( purchaseRequest, (OrderData)orderData));

					CompletableFuture<PurchaseRequest> updatedPurchaseRequestCF2= updatedPurchaseRequestCF
							.thenComposeAsync( purchaseRequest1 -> {
								return transactionService.linkOrderToTransaction_CF( purchaseRequest1)
										.thenComposeAsync( status -> {
											CompletableFuture<Void> result;
											if (status != Status.OK) {
												// Payment and order OK, however: automatic linking failed --> manual 
												result= mailboxHandler.sendMessage_CF( composeLinkingFailedMessage( purchaseRequest1));
											} else {
												result= CompletableFuture.completedFuture(null);
											}
											return result.thenApply( voiddummy->purchaseRequest1);
										});
							});
					return updatedPurchaseRequestCF2;
				})
				.handle((purchaseRequest, throwable)-> {
					if (throwable!= null) {
						extractCheckedException( throwable, f -> {
							purchaseHandler.notifyError( f);
							return null;
						});
					} 
					else {
						purchaseHandler.notifyComplete( purchaseRequest);
					}
					return null;
				});
	}


	<T> T extractCheckedException( Throwable e, Function<ValidationException, ? extends T> func)
	{
		if (e instanceof java.util.concurrent.CompletionException) {
			e=e.getCause();
		}
		if (e instanceof IllegalStateException
				&& ((IllegalStateException)e).getCause()!= null
				&& ((IllegalStateException)e).getCause() instanceof ValidationException) {
			ValidationException ve= (ValidationException) ((IllegalStateException)e).getCause();
			LOG.info("Obtained business exception: "+ ve);
			return func.apply( ve);	
		}
		if (e instanceof RuntimeException) 
			throw (RuntimeException)e;
		throw new RuntimeException(e);
	}
}


