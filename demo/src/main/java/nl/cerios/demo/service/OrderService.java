package nl.cerios.demo.service;

import java.util.concurrent.CompletableFuture;

import io.reactivex.Single;

public class OrderService {

	public OrderData createOrder(PurchaseRequest purchaseRequest) {
		OrderData orderData= new OrderData();
		orderData.setId( 90);
		return orderData;
	}
	
	public CompletableFuture<OrderData> createOrder_CF(PurchaseRequest purchaseRequest) {
		return CompletableFuture.supplyAsync( ()-> createOrder(purchaseRequest));
	}

	public Single<OrderData> createOrder_Rx(PurchaseRequest purchaseRequest) {
		return Single.defer( ()->Single.just( createOrder(purchaseRequest)));
	}
}
