package nl.cerios.demo.http;

import nl.cerios.demo.service.PurchaseRequest;

public interface PurchaseHttpHandler {
	void notifyError(Throwable exception);
	void notifyComplete(PurchaseRequest purchaseRequest);
}
