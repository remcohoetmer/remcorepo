package nl.cerios.demo.service;

import static nl.cerios.demo.CF_Utils.transportException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import io.reactivex.Single;
public class PurchaseRequestController {
	private static final Logger LOG = Logger.getLogger(PurchaseRequestController.class.getName());
	
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
	
	private PurchaseRequest update(PurchaseRequest purchaseRequest, OrderData orderData) {
		purchaseRequest.setOrderId( orderData.getId());
		return purchaseRequest;
	}
	
	private PurchaseRequest getPurchaseRequest(Integer purchaseRequestId) throws ValidationException
	{
		LOG.info( Thread.currentThread().getName());
		if (purchaseRequests.containsKey(purchaseRequestId)) {
			return purchaseRequests.get(purchaseRequestId);
		}
		throw new ValidationException( "No purchase request");
	}
	
	
	
	public PurchaseRequest update_Sync(PurchaseRequest purchaseRequest, OrderData orderData)
	{
		return update( purchaseRequest, orderData);
	}
	
	public CompletableFuture<PurchaseRequest> update_CF(PurchaseRequest purchaseRequest, OrderData orderData)
	{
		return CompletableFuture.supplyAsync( transportException( ()-> update( purchaseRequest,orderData)));
	}

	public Single<PurchaseRequest> update_Rx(final PurchaseRequest purchaseRequest, OrderData orderData)
	{
		// postpone the computation of the value until request time, not build time
		return Single.defer( ()-> Single.just( update( purchaseRequest, orderData)));
	}

	public PurchaseRequest getPurchaseRequest_Sync(Integer purchaseRequestId) throws ValidationException{
		return getPurchaseRequest( purchaseRequestId);
	}
	
	public CompletableFuture<PurchaseRequest> getPurchaseRequest_CF(Integer purchaseRequestId)
	{
		return CompletableFuture.supplyAsync( transportException( ()-> getPurchaseRequest( purchaseRequestId)));
	}

	public Single<PurchaseRequest> getPurchaseRequest_Rx(final Integer purchaseRequestId)
	{
		// postpone the computation of the value until request time, not build time
		return Single.defer( ()-> Single.just( getPurchaseRequest( purchaseRequestId)));
	}


}
