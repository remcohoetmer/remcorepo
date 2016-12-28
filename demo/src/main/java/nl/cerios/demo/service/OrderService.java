package nl.cerios.demo.service;

import java.util.concurrent.CompletableFuture;

import io.reactivex.Observable;

public class OrderService {

	public OrderData createOrder(PurchaseRequest purchaseRequest) {
		return new OrderData();
	}
	
	public CompletableFuture<OrderData> createOrder_CF(PurchaseRequest purchaseRequest) {
		return CompletableFuture.supplyAsync( ()-> createOrder(purchaseRequest));
	}

	public Observable<OrderData> createOrder_Rx(PurchaseRequest purchaseRequest) {
		return Observable.defer( ()->Observable.just( createOrder(purchaseRequest)));
	}
}
