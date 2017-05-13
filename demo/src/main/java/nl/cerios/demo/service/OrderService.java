package nl.cerios.demo.service;

import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

public class OrderService {

	public OrderData executeOrder_Sync(PurchaseRequest purchaseRequest) {
		OrderData orderData= new OrderData();
		orderData.setId( 90);
		return orderData;
	}
	
	public CompletableFuture<OrderData> executeOrder_CF(PurchaseRequest purchaseRequest) {
		return CompletableFuture.supplyAsync( ()-> executeOrder_Sync(purchaseRequest));
	}


	public Mono<OrderData> executeOrder_Rx(PurchaseRequest purchaseRequest) {
		return Mono.defer( ()->Mono.just( executeOrder_Sync(purchaseRequest)));
	}
}
