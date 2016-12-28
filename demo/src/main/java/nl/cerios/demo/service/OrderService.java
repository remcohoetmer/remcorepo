package nl.cerios.demo.service;

import java.util.concurrent.CompletableFuture;

import io.reactivex.Flowable;

public class OrderService {

	public OrderData createOrder(PurchaseRequest purchaseRequest) {
		return new OrderData();
	}
	
	public CompletableFuture<OrderData> createOrder_CF(PurchaseRequest purchaseRequest) {
		return CompletableFuture.supplyAsync( ()-> createOrder(purchaseRequest));
	}

	public Flowable<OrderData> createOrder_Rx(PurchaseRequest purchaseRequest) {
		return Flowable.defer( ()->Flowable.just( createOrder(purchaseRequest)));
	}
}
