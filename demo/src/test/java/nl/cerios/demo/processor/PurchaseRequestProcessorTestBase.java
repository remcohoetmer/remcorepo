package nl.cerios.demo.processor;

import java.util.logging.Logger;

import nl.cerios.demo.http.PurchaseHttpHandler;
import nl.cerios.demo.service.PurchaseRequest;
import nl.cerios.demo.service.PurchaseRequestController;
import nl.cerios.demo.service.PurchaseResponse;

public abstract class PurchaseRequestProcessorTestBase
{
	private static final Logger LOG = Logger.getLogger(PurchaseRequestProcessorTestBase.class.getName());
	static class PurchaseHttpHandlerStub implements PurchaseHttpHandler {
		PurchaseResponse purchaseResponse;
		String message;
		@Override
		public void notifyError(Throwable throwable) {
			message= throwable.getMessage();
			LOG.info( throwable.toString());
			//throwable.printStackTrace(System.out);
		}

		@Override
		public void notifyComplete(PurchaseResponse purchaseResponse) {
			this.purchaseResponse= purchaseResponse;
			LOG.info(purchaseResponse.toString());
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
