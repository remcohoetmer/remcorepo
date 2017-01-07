package nl.cerios.demo.http;

import nl.cerios.demo.service.PurchaseResponse;

public interface PurchaseHttpHandler {
	void notifyError(Throwable exception);
	void notifyComplete(PurchaseResponse purchaseResponse);
}
