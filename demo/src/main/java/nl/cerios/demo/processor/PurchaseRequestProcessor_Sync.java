package nl.cerios.demo.processor;
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


public class PurchaseRequestProcessor_Sync extends BaseProcessor {

	public void handle(HttpRequestData requestData, PurchaseHttpHandler purchaseHandler)
	{
		PurchaseRequest purchaseRequest;
		try {		
			purchaseRequest = purchaseRequestController.getPurchaseRequest_Sync( requestData.getPurchaseRequestId());
		} catch (ValidationException e) {
			purchaseHandler.notifyError( e);
			return;
		}
		CustomerData customerData;
		try {		
			customerData = customerService.getCustomerData_Sync( purchaseRequest.getCustomerId());
		} catch (ValidationException e) {
			purchaseHandler.notifyError( e);
			return;
		}

		LocationConfig locationData= locationService_Sync.getLocationConfig(purchaseRequest.getLocationId());

		CustomerValidation customerValidation = customerService.validateCustomer( customerData, locationData);
		if (customerValidation.getStatus() != Status.OK) {
			purchaseHandler.notifyError( new ValidationException( customerValidation.getMessage()));
			return;
		}

		TransactionValidation transactionValidation = transactionService.validate_Sync( purchaseRequest, customerData);
		if (transactionValidation.getStatus() != Status.OK) {
			purchaseHandler.notifyError( new ValidationException( transactionValidation.getMessage()));
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
