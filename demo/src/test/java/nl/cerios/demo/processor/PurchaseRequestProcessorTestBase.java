package nl.cerios.demo.synchrononous;

import nl.cerios.demo.common.PurchaseRequest;
import nl.cerios.demo.common.PurchaseRequestController;
import nl.cerios.demo.http.PurchaseHttpHandler;

public abstract class PurchaseRequestProcessorTestBase
{
	static class PurchaseHttpHandlerStub implements PurchaseHttpHandler {
		PurchaseRequest purchaseRequest;

		@Override
		public void notifyValidationError(String string) {
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
