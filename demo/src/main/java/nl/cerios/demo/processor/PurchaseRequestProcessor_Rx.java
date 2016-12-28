package nl.cerios.demo.processor;
import io.reactivex.Flowable;
import io.reactivex.processors.ReplayProcessor;
import nl.cerios.demo.http.HttpRequestData;
import nl.cerios.demo.http.PurchaseHttpHandler;
import nl.cerios.demo.service.CustomerData;
import nl.cerios.demo.service.LocationConfig;
import nl.cerios.demo.service.PurchaseRequest;
import nl.cerios.demo.service.ValidationException;


public class PurchaseRequestProcessor_Rx extends BaseProcessor {

	public Flowable<CustomerData> handle(HttpRequestData requestData, PurchaseHttpHandler purchaseHandler)
	{

		Flowable<PurchaseRequest> purchaseRequestObs = purchaseRequestController.getPurchaseRequest_Rx( requestData.getPurchaseRequestId());
		purchaseRequestObs.onErrorResumeNext( (e) -> {
			if (e instanceof ValidationException) {
				purchaseHandler.notifyValidationError( e.getMessage());
			}
			return Flowable.error( e);
		});
		ReplayProcessor<PurchaseRequest> purchaseRequestPublisher= ReplayProcessor.create();
		purchaseRequestObs.subscribe( purchaseRequestPublisher);


		Flowable<CustomerData> customerData= customerService.getCustomerData_Rx( purchaseRequestPublisher
				.map( purchaseRequest-> purchaseRequest.getCustomerId()));
		/*} catch (ValidationException e) {
			purchaseHandler.notifyValidationError( e.getMessage());
			return;
		}*/
		Flowable<Integer> locationIdObs= purchaseRequestPublisher
				.map( purchaseRequest1-> purchaseRequest1.getLocationId());

		//Flowable<LocationConfig> locationDataObs= locationService_Rx.getLocationConfig(locationIdObs.blockingFirst());
		return customerData;

		/*
		// Now there is a customer, we can store it in the speed layer
		purchaseRequestController.store( purchaseRequest);

		CustomerValidation customerValidation = customerService.validateCustomer( customerData, locationData);
		if (customerValidation.getStatus() != Status.OK) {
			purchaseHandler.notifyValidationError( customerValidation.getMessage());
			return;
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
		 */
	}

}
