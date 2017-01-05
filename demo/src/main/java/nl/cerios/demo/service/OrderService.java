package nl.cerios.demo.service;

import java.util.concurrent.CompletableFuture;

import io.reactivex.Single;

public class OrderService {

	public OrderData createOrder90_Sync(PurchaseRequest purchaseRequest) {
		OrderData orderData= new OrderData();
		orderData.setId( 90);
		return orderData;
	}
	
	public OrderData createOrder100_Sync(PurchaseRequest purchaseRequest) {
		OrderData orderData= new OrderData();
		orderData.setId( 100);
		return orderData;
	}
	
	public CompletableFuture<OrderData> createOrder100_CF(PurchaseRequest purchaseRequest) {
		return CompletableFuture.supplyAsync( ()-> createOrder100_Sync(purchaseRequest));
	}

	public CompletableFuture<OrderData> createOrder90_CF(PurchaseRequest purchaseRequest) {
		return CompletableFuture.supplyAsync( ()-> createOrder90_Sync(purchaseRequest));
	}


	public Single<OrderData> createOrder90_Rx(PurchaseRequest purchaseRequest) {
		return Single.defer( ()->Single.just( createOrder90_Sync(purchaseRequest)));
	}
	public Single<OrderData> createOrder100_Rx(PurchaseRequest purchaseRequest) {
		return Single.defer( ()->Single.just( createOrder100_Sync(purchaseRequest)));
	}
}
