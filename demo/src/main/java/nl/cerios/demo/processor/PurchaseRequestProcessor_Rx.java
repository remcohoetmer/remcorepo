package nl.cerios.demo.processor;
import io.reactivex.Flowable;
import io.reactivex.processors.ReplayProcessor;
import nl.cerios.demo.http.HttpRequestData;
import nl.cerios.demo.http.PurchaseHttpHandler;
import nl.cerios.demo.service.CustomerData;
import nl.cerios.demo.service.CustomerValidation;
import nl.cerios.demo.service.LocationConfig;
import nl.cerios.demo.service.PurchaseRequest;
import nl.cerios.demo.service.Status;
import nl.cerios.demo.service.ValidationException;


public class PurchaseRequestProcessor_Rx extends BaseProcessor {

	public Flowable<CustomerData> handle(HttpRequestData requestData, PurchaseHttpHandler purchaseHandler)
	{

		Flowable<PurchaseRequest> purchaseRequestObs = 
				purchaseRequestController.getPurchaseRequest_Rx( requestData.getPurchaseRequestId())
				.onErrorResumeNext( (e) -> {
					if (e instanceof ValidationException) {
						purchaseHandler.notifyValidationError( e.getMessage());
					}
					return Flowable.error( e);
				});
		ReplayProcessor<PurchaseRequest> purchaseRequestPublisher= ReplayProcessor.create();
		purchaseRequestObs.subscribe( purchaseRequestPublisher);


		Flowable<CustomerData> customerDataObs= customerService.getCustomerData_Rx( purchaseRequestPublisher
				.map( purchaseRequest-> purchaseRequest.getCustomerId()));
		customerDataObs.onErrorResumeNext( (e) -> {
			if (e instanceof ValidationException) {
				purchaseHandler.notifyValidationError( e.getMessage());
			}
			return Flowable.error( e);
		});

		Flowable<LocationConfig> locationDataObs= purchaseRequestPublisher
				.map( purchaseRequest1-> purchaseRequest1.getLocationId())
				.flatMap( locationId -> locationService_Rx.getLocationConfig(locationId));

		Flowable<CustomerValidation> customerValidationObs= Flowable.zip( customerDataObs, locationDataObs,
				(customerData, locationData) -> customerService.validateCustomer( customerData, locationData))
				.map( customerValidation -> {
					if (customerValidation.getStatus() != Status.OK) {
						purchaseHandler.notifyValidationError( customerValidation.getMessage());
						throw new ValidationException("Validation Error");
					}
					return customerValidation;
				});

		return Flowable.merge( customerDataObs, customerValidationObs.flatMap( l->Flowable.empty()));
		/*
		// Now there is a customer, we can store it in the speed layer
		purchaseRequestController.store( purchaseRequest);
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
