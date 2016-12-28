package nl.cerios.demo.service;

import static nl.cerios.demo.CF_Utils.transportException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import io.reactivex.Observable;
public class PurchaseRequestController {
	private PurchaseRequestController(){}
	private final static PurchaseRequestController purchaseRequestController= 
			new PurchaseRequestController();
	public static PurchaseRequestController getInstance() { return purchaseRequestController;}
	
	private Map<Integer,PurchaseRequest> purchaseRequests= new HashMap<>();

	public void add(Integer purchaseRequestId, PurchaseRequest purchaseRequest)
	{
		purchaseRequest.setPurchaseRequestId(purchaseRequestId);
		purchaseRequests.put(purchaseRequestId, purchaseRequest);
	}
	
	public void store(PurchaseRequest purchaseRequest) {
	}

	public void update(PurchaseRequest purchaseRequest, OrderData orderData) {
		purchaseRequest.setOrderId( orderData.getId());
	}

	private PurchaseRequest getPurchaseRequest(Integer purchaseRequestId) throws ValidationException
	{
		if (purchaseRequests.containsKey(purchaseRequestId)) {
			return purchaseRequests.get(purchaseRequestId);
		}
		throw new ValidationException( "No purchase request");
	}
	
	public PurchaseRequest getPurchaseRequest_Sync(Integer purchaseRequestId) throws ValidationException{
		return getPurchaseRequest( purchaseRequestId);
	}
	
	public CompletableFuture<PurchaseRequest> getPurchaseRequest_CF(Integer purchaseRequestId)
	{
		return CompletableFuture.supplyAsync( transportException( ()-> getPurchaseRequest( purchaseRequestId)));
	}

	public Observable<PurchaseRequest> getPurchaseRequest_Rx(final Integer purchaseRequestId)
	{
		// postpone the computation of the value until request time, not build time
		return Observable.defer( ()-> Observable.just( getPurchaseRequest( purchaseRequestId)));
	}


}
