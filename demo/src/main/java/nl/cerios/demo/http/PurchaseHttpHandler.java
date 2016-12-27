package nl.cerios.demo.http;

import nl.cerios.demo.common.PurchaseRequest;

public interface PurchaseHttpHandler {
	void notifyValidationError(String string);
	void notifyComplete(PurchaseRequest purchaseRequest);
}
