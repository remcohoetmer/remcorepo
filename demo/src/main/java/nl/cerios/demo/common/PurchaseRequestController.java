package nl.cerios.demo.common;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import io.reactivex.Observable;
import nl.cerios.demo.LaunderThrowable;
import nl.cerios.demo.Supplier_Ex;

public class PurchaseRequestController {

	public void store(PurchaseRequest purchaseRequest) {	
	}

	public void update(PurchaseRequest purchaseRequest, OrderData orderData) {
		purchaseRequest.setOrderId( orderData.getId());
	}

	public PurchaseRequest getPurchaseRequest(Integer purchaseRequestId) throws ValidationException{
		if (purchaseRequestId>0) {
			PurchaseRequest purchaseRequest= new PurchaseRequest();
			purchaseRequest.setCustomerId( 1);
			purchaseRequest.setLocationId( 5);
			return purchaseRequest;
		}
		throw new ValidationException( "No purchase");
	}
	
	public PurchaseRequest getPurchaseRequestSync(Integer purchaseRequestId) throws ValidationException{
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

	public<T> Supplier<T> transportException(Supplier_Ex<T> func) {
		return new Supplier<T>() {
			@Override
			public T get() {
				try {
					return func.get();
				} catch (Exception e) {
					throw LaunderThrowable.launderThrowable(e);
				}
			}
		};
	}
}
