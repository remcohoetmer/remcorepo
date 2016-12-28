package nl.cerios.demo.processor;

import nl.cerios.demo.http.PurchaseHttpHandler;
import nl.cerios.demo.service.PurchaseRequest;
import nl.cerios.demo.service.PurchaseRequestController;

public abstract class PurchaseRequestProcessorTestBase
{
	static class PurchaseHttpHandlerStub implements PurchaseHttpHandler {
		PurchaseRequest purchaseRequest;
		String message;
		@Override
		public void notifyValidationError(String string) {
			message= string;
			System.out.println(string);
		}

		@Override
		public void notifyComplete(PurchaseRequest purchaseRequest) {
			this.purchaseRequest= purchaseRequest;
			System.out.println(purchaseRequest);
		}
	};
	
	void addPurchaseRequest(Integer purchaseRequestId, Integer customerId, Integer locationId)
	{
		PurchaseRequest purchaseRequest= new PurchaseRequest();
		purchaseRequest.setCustomerId( customerId);
		purchaseRequest.setLocationId( locationId);
		PurchaseRequestController.getInstance().add( purchaseRequestId, purchaseRequest);
	}
}
