package nl.cerios.demo.processor;
import nl.cerios.demo.http.HttpRequestData;
import nl.cerios.demo.http.PurchaseHttpHandler;
import nl.cerios.demo.service.CustomerData;
import nl.cerios.demo.service.CustomerValidation;
import nl.cerios.demo.service.LocationConfig;
import nl.cerios.demo.service.OrderData;
import nl.cerios.demo.service.PurchaseRequest;
import nl.cerios.demo.service.PurchaseResponse;
import nl.cerios.demo.service.Status;
import nl.cerios.demo.service.TransactionValidation;
import nl.cerios.demo.service.ValidationException;


public class PurchaseRequestProcessor_Sync extends BaseProcessor {

	public void process(HttpRequestData requestData, PurchaseHttpHandler purchaseHandler)
	{
		try 
		{ 
			PurchaseRequest purchaseRequest = purchaseRequestController.getPurchaseRequest_Sync( requestData.getPurchaseRequestId());
			CustomerData customerData= customerService.getCustomerData_Sync( purchaseRequest.getCustomerId());
			LocationConfig locationData= locationService_Sync.getLocationConfig(purchaseRequest.getLocationId());

			CustomerValidation customerValidation = customerService.validateCustomer_Sync( customerData, locationData);
			if (customerValidation.getStatus() != Status.OK) {
				throw new ValidationException( customerValidation.getMessage());
			}
			TransactionValidation transactionValidation = transactionService.validate_Sync( purchaseRequest, customerData);
			if (transactionValidation.getStatus() != Status.OK) {
				throw new ValidationException( transactionValidation.getMessage());
			}

			OrderData orderData= orderService.createOrder_Sync( purchaseRequest);
			PurchaseResponse purchaseResponse= purchaseRequestController.update_Sync( purchaseRequest, orderData);

			Status status= transactionService.linkOrderToTransaction_Sync( purchaseRequest);
			if (status != Status.OK) {
				mailboxHandler.sendMessage_Sync( composeLinkingFailedMessage( purchaseResponse));
			}
			purchaseHandler.notifyComplete( purchaseResponse);
		} catch (ValidationException e) {
			purchaseHandler.notifyError( e);
		}
	}
}
