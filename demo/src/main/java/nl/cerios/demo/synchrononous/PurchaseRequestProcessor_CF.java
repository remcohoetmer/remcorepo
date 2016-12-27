package nl.cerios.demo.synchrononous;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import javax.management.RuntimeErrorException;

import nl.cerios.demo.common.BaseProcessor;
import nl.cerios.demo.common.CustomerData;
import nl.cerios.demo.common.CustomerValidation;
import nl.cerios.demo.common.LocationConfig;
import nl.cerios.demo.common.OrderData;
import nl.cerios.demo.common.PurchaseRequest;
import nl.cerios.demo.common.Status;
import nl.cerios.demo.common.TransactionValidation;
import nl.cerios.demo.common.ValidationException;
import nl.cerios.demo.http.HttpRequestData;
import nl.cerios.demo.http.PurchaseHttpHandler;


public class PurchaseRequestProcessor_CF extends BaseProcessor {

	<T> T handleCFException( Throwable e, Function<Throwable, ? extends T> func){
		if (e instanceof IllegalStateException
				&& ((IllegalStateException)e).getCause()!= null
				&& ((IllegalStateException)e).getCause() instanceof ValidationException) {
			return func.apply( e);	
		}
		if (e instanceof RuntimeException) 
			throw (RuntimeException)e;
		throw new RuntimeException(e);
	}



	public void handle(HttpRequestData requestData, PurchaseHttpHandler purchaseHandler)
	{
		Function<Throwable,PurchaseRequest> validationExceptionHandlerPurchaseRequest = e-> handleCFException( e, f -> {
			purchaseHandler.notifyValidationError( f.getMessage());
			return (PurchaseRequest) null;
		});

		Function<Throwable,CustomerData> validationExceptionHandlerCustomerData = e-> handleCFException( e, f -> {
			purchaseHandler.notifyValidationError( f.getMessage());
			return (CustomerData) null;
		});
		Function<Throwable,CustomerValidation> validationExceptionHandlerCustomerValidation = e-> handleCFException( e, f -> {
			purchaseHandler.notifyValidationError( f.getMessage());
			return (CustomerValidation) null;
		});
		
		locationService_CF.getLocationConfig( LocationConfig.DEFAULT);
		locationService_CF.getLocationConfig( LocationConfig.DEFAULT);

		CompletableFuture<PurchaseRequest> purchaseRequestCF=
				purchaseRequestController.getPurchaseRequest_CF( requestData.getPurchaseRequestId());

		purchaseRequestCF.exceptionally( validationExceptionHandlerPurchaseRequest);

		CompletableFuture<CustomerData> customerDataCF= purchaseRequestCF.thenCompose((purchaseRequest) ->
		customerService.getCustomerData_CF( purchaseRequest.getCustomerId())
				).exceptionally( validationExceptionHandlerCustomerData);

		CompletableFuture<LocationConfig> locationDataCF= purchaseRequestCF.thenCompose((purchaseRequest) ->
		locationService_CF.getLocationConfig(purchaseRequest.getLocationId())
				);
		
		CompletableFuture<CustomerValidation> customerValidationCF=customerDataCF.
				thenCombineAsync(locationDataCF, 
						(customerData, locationData)-> customerService.validateCustomer( customerData, locationData));

		// het is niet mogelijk m switches te programeren
		CompletableFuture<CustomerValidation> customerValidationCF2=customerValidationCF.
				thenApplyAsync(  (customerValidation) ->
				{ if (customerValidation.getStatus() != Status.OK)
					throw new IllegalStateException(customerValidation.getMessage()); 
					else return customerValidation;});
		CompletableFuture<CustomerValidation> customerValidationCF3= customerValidationCF2.exceptionally( validationExceptionHandlerCustomerValidation);
		

		// Synchronise
		customerValidationCF3.join();
		PurchaseRequest purchaseRequest;
		CustomerData customerData;
		try {
			purchaseRequest = purchaseRequestCF.get();
			customerData= customerDataCF.get();
			}
		catch (InterruptedException | ExecutionException e1) {
			throw new RuntimeException(e1);
		}

		
		TransactionValidation transactionValidation = transactionService.validate( purchaseRequest, customerData);
		if (transactionValidation.getStatus() != Status.OK) {
			purchaseHandler.notifyValidationError( transactionValidation.getMessage());
		}

		OrderData orderData= orderService.createOrder( purchaseRequest);
		purchaseRequestController.update( purchaseRequest, orderData);

		Status status= transactionService.linkOrderToTransaction( purchaseRequest);
		if (status != Status.OK) {

			// Payment and order OK, however: automatic linking failed --> manual 
			mailboxHandler.sendMessage( composeLinkingFailedMessage( purchaseRequest));
		}

		purchaseHandler.notifyComplete( purchaseRequest);
	}

}
