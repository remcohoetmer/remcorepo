package nl.cerios.demo.common;

public class PurchaseRequestService {

	public PurchaseRequest getPurchaseRequest(Integer purchaseRequestId) throws ValidationException{
		if (purchaseRequestId>0) {
			PurchaseRequest purchaseRequest= new PurchaseRequest();
			purchaseRequest.setCustomerId( 1);
			return purchaseRequest;
		}
		throw new ValidationException( "No purchase");
	}

}
