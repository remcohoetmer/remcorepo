package nl.cerios.demo.synchrononous;
import javax.servlet.http.HttpServletResponse;

import nl.cerios.demo.CustomerData;
import nl.cerios.demo.LocationConfig;
import nl.cerios.demo.Request;


public class RequestProcessor {
	LocationService locationService = new LocationService();
	CustomerService customerService = new CustomerService();
	PurchaseRequestService purchaseRequestService = new PurchaseRequestService();
	TransactionService transactionService= new TransactionService();
	void compose(Request request, HttpResponse httpResponse)
	{
		
		PurchaseRequest purchaseRequest;
		try {		
			purchaseRequest = purchaseRequestService.getPurchaseRequest( request.getPurchaseRequestId());
		} catch (ValidationException e) {
			notifyError( e, httpResponse);
			return;
		}
		CustomerData customerData;
		try {		
			customerData = customerService.getCustomerData( purchaseRequest.getCustomerId());
		} catch (ValidationException e) {
			notifyError( e, httpResponse);
			return;
		}

		LocationConfig locationData = locationService.getLocationConfig(purchaseRequest.getLocationId());
		// Now there is a customer, we can store it in the speed layer
		PurchaseRequestPersister.store( purchaseRequest);
		
		CustomerValidation customerValidation = customerService.validateCustomer( customerData,locationData);
		if (customerValidation.getStatus() != Status.OK) {
			purchaseRequest.setValidationError( customerValidation);
			notifyValidationError( purchaseRequest, httpResponse);
		}

		TransactionValidation transactionValidation = transactionService.validate( purchaseRequest, customerData);
		if (transactionValidation.getStatus() != Status.OK) {
			purchaseRequest.setValidationError( transactionValidation);
			notifyValidationError( purchaseRequest, httpResponse);
		}
		/*
		 * TODO:
		orderPickingService.createOrderPickup( purchaseRequest);
		orderTransportService.createOrderTransport( purchaseRequest);
		*/
		
		
	}
	private void notifyValidationError(PurchaseRequest purchaseRequest, HttpResponse httpResponse) {
		
	}
	private void notifyError(ValidationException e, HttpResponse httpResponse) {
		
	}
	public static final void main(String[] args) throws Exception
	{
		new RequestProcessor().compose( new Request(), null);
	}
}
