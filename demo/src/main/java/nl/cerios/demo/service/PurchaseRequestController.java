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
	
	private PurchaseResponse update(PurchaseRequest purchaseRequest, OrderData orderData) {
		purchaseRequest.setOrderId( orderData.getId());
		PurchaseResponse purchaseResponse= new PurchaseResponse();
		purchaseResponse.setPurchaseRequest(purchaseRequest);
		return purchaseResponse;
	}
	
	private PurchaseRequest getPurchaseRequest(Integer purchaseRequestId) throws ValidationException
	{
		LOG.info( Thread.currentThread().getName());
		if (purchaseRequests.containsKey(purchaseRequestId)) {
			return purchaseRequests.get(purchaseRequestId);
		}
		throw new ValidationException( "No purchase request");
	}
	
	
	
	public PurchaseResponse update_Sync(PurchaseRequest purchaseRequest, OrderData orderData)
	{
		return update( purchaseRequest, orderData);
	}
	
	public CompletableFuture<PurchaseResponse> update_CF(PurchaseRequest purchaseRequest, OrderData orderData)
	{
		return CompletableFuture.supplyAsync( transportException( ()-> update( purchaseRequest,orderData)));
	}

	public Single<PurchaseResponse> update_Rx(final PurchaseRequest purchaseRequest, OrderData orderData)
	{
		// postpone the computation of the value until request time, not build time
		return Single.defer( ()-> Single.just( update( purchaseRequest, orderData)));
	}

	public PurchaseRequest retrievePurchaseRequest_Sync(Integer purchaseRequestId) throws ValidationException{
		return getPurchaseRequest( purchaseRequestId);
	}
	
	public CompletableFuture<PurchaseRequest> retrievePurchaseRequest_CF(Integer purchaseRequestId)
	{
		return CompletableFuture.supplyAsync( transportException( ()-> getPurchaseRequest( purchaseRequestId)));
	}

	public Single<PurchaseRequest> retrievePurchaseRequest_Rx(final Integer purchaseRequestId)
	{
		// postpone the computation of the value until request time, not build time
		return Single.defer( ()-> Single.just( getPurchaseRequest( purchaseRequestId)));
	}


}
