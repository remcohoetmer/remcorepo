package nl.cerios.demo.common;

public class PurchaseRequestController {

	public void store(PurchaseRequest purchaseRequest) {
		
	}

	public void update(PurchaseRequest purchaseRequest, OrderData orderData) {
		purchaseRequest.setOrderId( orderData.getId());
	}


}
