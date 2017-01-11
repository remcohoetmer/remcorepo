package nl.cerios.demo.service;

import java.util.concurrent.CompletableFuture;

import io.reactivex.Single;

public class OrderService {

	public OrderData executeOrder_Sync(PurchaseRequest purchaseRequest) {
		OrderData orderData= new OrderData();
		orderData.setId( 90);
		return orderData;
	}
	
	public CompletableFuture<OrderData> executeOrder_CF(PurchaseRequest purchaseRequest) {
		return CompletableFuture.supplyAsync( ()-> executeOrder_Sync(purchaseRequest));
	}


	public Single<OrderData> executeOrder_Rx(PurchaseRequest purchaseRequest) {
		return Single.defer( ()->Single.just( executeOrder_Sync(purchaseRequest)));
	}
}
