package nl.cerios.demo.http;

import nl.cerios.demo.service.PurchaseRequest;

public interface PurchaseHttpHandler {
	void notifyValidationError(String string);
	void notifyComplete(PurchaseRequest purchaseRequest);
}
