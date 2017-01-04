package nl.cerios.demo.processor;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.logging.Logger;

import nl.cerios.demo.http.HttpRequestData;
import nl.cerios.demo.http.PurchaseHttpHandler;
import nl.cerios.demo.service.CustomerData;
import nl.cerios.demo.service.CustomerValidation;
import nl.cerios.demo.service.LocationConfig;
import nl.cerios.demo.service.PurchaseRequest;
import nl.cerios.demo.service.Status;
import nl.cerios.demo.service.TransactionValidation;
import nl.cerios.demo.service.ValidationException;


public class PurchaseRequestProcessor_CF extends BaseProcessor {
	private static final Logger LOG = Logger.getLogger(PurchaseRequestProcessor_CF.class.getName());


	public CompletableFuture<Object> handle(HttpRequestData requestData, PurchaseHttpHandler purchaseHandler)
	{
		CompletableFuture<PurchaseRequest> purchaseRequestCF=
				purchaseRequestController.getPurchaseRequest_CF( requestData.getPurchaseRequestId());

		CompletableFuture<CustomerData> customerDataCF= purchaseRequestCF.thenComposeAsync(
				purchaseRequest ->
				customerService.getCustomerData_CF( purchaseRequest.getCustomerId()));

		CompletableFuture<LocationConfig> locationDataCF= purchaseRequestCF.thenComposeAsync(
				purchaseRequest -> {
					if (purchaseRequest.getLocationId() == null)
						throw new IllegalStateException(new ValidationException( "Invalid location")); 
					return locationService_CF.getLocationConfig(purchaseRequest.getLocationId());
				});

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
				.thenComposeAsync( dummy -> purchaseRequestCF)  
				.thenCombineAsync( customerDataCF,
						(purchaseRequest, customerData) -> transactionService.validate_CF( purchaseRequest, customerData))
				.thenComposeAsync( Function.identity())
				.thenApplyAsync(  transactionValidation
						-> { 
							LOG.info( " " + transactionValidation.getStatus());
							if (transactionValidation.getStatus() != Status.OK)
								throw new IllegalStateException(new ValidationException(transactionValidation.getMessage())); 
							else return transactionValidation;
						});

		CompletableFuture<PurchaseRequest> updatedPurchaseRequestCF=
				transactionValidationCF                   // don't use the result, only the error and the trigger
				.thenComposeAsync( dummy -> purchaseRequestCF)
				.thenComposeAsync( purchaseRequest-> orderService.createOrder_CF( purchaseRequest))
				.thenCombineAsync( purchaseRequestCF, (orderData, purchaseRequest) 
						-> purchaseRequestController.update_CF( purchaseRequest, orderData))
				.thenComposeAsync( Function.identity());

		return updatedPurchaseRequestCF
				.thenComposeAsync(
						purchaseRequest -> transactionService.linkOrderToTransaction_CF( purchaseRequest))
				.thenCombineAsync( updatedPurchaseRequestCF,
						(status, purchaseRequest) ->
				{
					if (status != Status.OK) {
						// Payment and order OK, however: automatic linking failed --> manual 
						mailboxHandler.sendMessage( composeLinkingFailedMessage( purchaseRequest));
					} 
					return purchaseRequest;

				})
				.handle(
						(purchaseRequest, throwable)-> {
							if (throwable!= null) {
								extractCheckedException( throwable, f -> {
									purchaseHandler.notifyError( f);
									return null;
								});
							} 
							else { 
								purchaseHandler.notifyComplete( purchaseRequest);
							}
							return new Object();
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


