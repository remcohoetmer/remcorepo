package nl.cerios.demo.processor;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.logging.Logger;

import nl.cerios.demo.http.HttpRequestData;
import nl.cerios.demo.http.PurchaseHttpHandler;
import nl.cerios.demo.service.CustomerValidation;
import nl.cerios.demo.service.LocationConfig;
import nl.cerios.demo.service.PurchaseRequest;
import nl.cerios.demo.service.Status;
import nl.cerios.demo.service.TransactionValidation;
import nl.cerios.demo.service.ValidationException;


public class PurchaseRequestProcessor_CF extends BaseProcessor {
	private static final Logger LOG = Logger.getLogger(PurchaseRequestProcessor_CF.class.getName());

	public CompletableFuture<Void> process(HttpRequestData requestData, PurchaseHttpHandler purchaseHandler)
	{
		CompletableFuture<PurchaseRequest> purchaseRequestCF=
				purchaseRequestController.retrievePurchaseRequest_CF( requestData.getPurchaseRequestId());

		return purchaseRequestCF.thenCompose( purchaseRequest -> {
			return customerService.retrieveCustomerData_CF( purchaseRequest.retrieveCustomerId())
					.thenCompose( customerData-> {

						if (purchaseRequest.getLocationId() == null)
							throw new IllegalStateException(new ValidationException( "Invalid location"));

						CompletableFuture<LocationConfig> locationDataCF= locationService_CF.getLocationConfig(purchaseRequest.getLocationId());

						CompletableFuture<CustomerValidation> customerValidationCF=
								locationDataCF.
								thenCompose( locationData
										-> customerService.validateCustomer_CF( customerData, locationData))
								.thenApply(  customerValidation 
										-> {
											if (customerValidation.getStatus() != Status.OK)
												throw new IllegalStateException(new ValidationException(customerValidation.getMessage())); 
											return customerValidation;
										});

						CompletableFuture<TransactionValidation> transactionValidationCF=
								transactionService.validate_CF( purchaseRequest, customerData)
								.thenApply(  transactionValidation -> { 
									if (transactionValidation.getStatus() != Status.OK)
										throw new IllegalStateException(new ValidationException(transactionValidation.getMessage())); 
									return transactionValidation;
								});

						return CompletableFuture.allOf( customerValidationCF, transactionValidationCF)
								.thenCompose( dummy -> orderService.executeOrder_CF( purchaseRequest))
								.thenCompose( orderData -> purchaseRequestController.update_CF( purchaseRequest, orderData))
								.thenCompose( purchaseResponse -> {
									return transactionService.linkOrderToTransaction_CF( purchaseRequest)
											.thenCompose( status -> {
												CompletableFuture<Void> result;
												if (status != Status.OK) {
													// Payment and order OK, however: automatic linking failed --> manual 
													result= mailboxHandler.sendMessage_CF( composeLinkingFailedMessage( purchaseResponse));
												} else {
													result= CompletableFuture.completedFuture(null);
												}
												return result.thenApply( dummy->purchaseResponse);
											});
								});
					});
		})
				.handle((purchaseResponse, throwable)-> {
					if (throwable!= null) {
						extractCheckedException( throwable, f -> {
							purchaseHandler.notifyError( f);
							return null;
						});
					} 
					else {
						purchaseHandler.notifyComplete( purchaseResponse);
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


