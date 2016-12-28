package nl.cerios.demo.processor;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Logger;

import nl.cerios.demo.http.HttpRequestData;
import nl.cerios.demo.http.PurchaseHttpHandler;
import nl.cerios.demo.http.impl.HttpEventHandler;
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
	
	<T> T handleCFException( Throwable e, Function<ValidationException, ? extends T> func)
	{
		if (e instanceof java.util.concurrent.CompletionException) {
			e=e.getCause();
		}
		LOG.info("handleCFException "+ e);
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

	public void handle(HttpRequestData requestData, PurchaseHttpHandler purchaseHandler)
	{
		BiFunction<? super PurchaseRequest,Throwable,? extends PurchaseRequest> validationExceptionHandlerPurchaseRequest = (func,e)-> handleCFException( e, f -> {
			purchaseHandler.notifyValidationError( f.getMessage());
			return null;
		});
		Function<Throwable,CustomerData> validationExceptionHandlerCustomerData = e-> handleCFException( e, f -> {
			purchaseHandler.notifyValidationError( f.getMessage());
			return null;
		});
		Function<Throwable,CustomerValidation> validationExceptionHandlerCustomerValidation = e-> handleCFException( e, f -> {
			purchaseHandler.notifyValidationError( f.getMessage());
			return null;
		});
		Function<Throwable,TransactionValidation> validationExceptionHandlerTransactionValidation = e-> handleCFException( e, f -> {
			purchaseHandler.notifyValidationError( f.getMessage());
			return null;
		});		


		CompletableFuture<PurchaseRequest> purchaseRequestCF=
				purchaseRequestController.getPurchaseRequest_CF( requestData.getPurchaseRequestId())
		.handle( validationExceptionHandlerPurchaseRequest);

		
		CompletableFuture<CustomerData> customerDataCF= purchaseRequestCF.thenCompose((purchaseRequest) ->
		customerService.getCustomerData_CF( purchaseRequest.getCustomerId())
				);
		customerDataCF.exceptionally( validationExceptionHandlerCustomerData);

		CompletableFuture<LocationConfig> locationDataCF= purchaseRequestCF.thenCompose((purchaseRequest) ->
		locationService_CF.getLocationConfig(purchaseRequest.getLocationId())
				);

		class CustomerLocation {
			CustomerData customerData;
			LocationConfig locationData;
			CustomerLocation ( CustomerData customerData, LocationConfig locationData){
				this.customerData= customerData;
				this.locationData=locationData;
			}
		}

		CompletableFuture<CustomerLocation> customerValidationCF=customerDataCF.
				thenCombine(locationDataCF, // combine async?
						(customerData, locationData)-> new CustomerLocation(customerData, locationData));

		CompletableFuture<CustomerValidation> customerValidationCF2=
				customerValidationCF.thenCompose(
						customerLocation -> 
						customerService.validateCustomer_CF( customerLocation.customerData, customerLocation.locationData));


		// het is niet mogelijk om switches te programmeren
		CompletableFuture<CustomerValidation> customerValidationCF3=customerValidationCF2.
				thenApplyAsync(  (customerValidation) ->
				{ if (customerValidation.getStatus() != Status.OK)
					throw new IllegalStateException(new ValidationException(customerValidation.getMessage())); 
				else return customerValidation;});
		CompletableFuture<CustomerValidation> customerValidationCF4= customerValidationCF3.exceptionally( validationExceptionHandlerCustomerValidation);

		// Synchronise
		customerValidationCF4.join();
		PurchaseRequest purchaseRequest;
		CustomerData customerData;
		try {
			purchaseRequest = purchaseRequestCF.get();
			customerData= customerDataCF.get();
		}
		catch (InterruptedException | ExecutionException e1) {
			throw new RuntimeException(e1);
		}


		TransactionValidation transactionValidation = transactionService.validate_Sync( purchaseRequest, customerData);
		if (transactionValidation.getStatus() != Status.OK) {
			purchaseHandler.notifyValidationError( transactionValidation.getMessage());
		}

		OrderData orderData= orderService.createOrder( purchaseRequest);
		purchaseRequestController.update( purchaseRequest, orderData);

		Status status= transactionService.linkOrderToTransaction_Sync( purchaseRequest);
		if (status != Status.OK) {

			// Payment and order OK, however: automatic linking failed --> manual 
			mailboxHandler.sendMessage( composeLinkingFailedMessage( purchaseRequest));
		}

		purchaseHandler.notifyComplete( purchaseRequest);

	}

}
