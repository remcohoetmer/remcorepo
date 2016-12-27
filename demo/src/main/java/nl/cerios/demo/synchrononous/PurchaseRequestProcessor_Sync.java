package nl.cerios.demo.synchrononous;
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


public class PurchaseRequestProcessor_Sync extends BaseProcessor {

	public void handle(HttpRequestData requestData, PurchaseHttpHandler purchaseHandler)
	{

		locationService_Sync.getLocationConfig( LocationConfig.DEFAULT);
		locationService_Sync.getLocationConfig( LocationConfig.DEFAULT);

		PurchaseRequest purchaseRequest;
		try {		
			purchaseRequest = purchaseRequestController.getPurchaseRequest( requestData.getPurchaseRequestId());
		} catch (ValidationException e) {
			purchaseHandler.notifyValidationError( e.getMessage());
			return;
		}
		CustomerData customerData;
		try {		
			customerData = customerService.getCustomerData_Sync( purchaseRequest.getCustomerId());
		} catch (ValidationException e) {
			purchaseHandler.notifyValidationError( e.getMessage());
			return;
		}

		LocationConfig locationData= locationService_Sync.getLocationConfig(purchaseRequest.getLocationId());

		// Now there is a customer, we can store it in the speed layer
		purchaseRequestController.store( purchaseRequest);
		
		CustomerValidation customerValidation = customerService.validateCustomer( customerData, locationData);
		if (customerValidation.getStatus() != Status.OK) {
			purchaseHandler.notifyValidationError( customerValidation.getMessage());
			return;
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
