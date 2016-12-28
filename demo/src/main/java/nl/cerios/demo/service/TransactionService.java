package nl.cerios.demo.service;

import java.util.concurrent.CompletableFuture;

import io.reactivex.Flowable;
import io.reactivex.Single;

public class TransactionService {

	public TransactionValidation validate_Sync(PurchaseRequest purchaseRequest, CustomerData customerData) {
		TransactionValidation validation= new TransactionValidation();
		if (customerData.getCustomerId()!=0) {
			validation.setStatus( Status.OK);
		} else {
			validation.setStatus( Status.NOT_OK);
		}
		return validation;
	}

	public Status linkOrderToTransaction_Sync(PurchaseRequest purchaseRequest) {
		return Status.OK;
	}

	public CompletableFuture<TransactionValidation> validate_CF(PurchaseRequest purchaseRequest, CustomerData customerData) {
		return CompletableFuture.supplyAsync( ()-> validate_Sync(purchaseRequest, customerData));

	}
	public CompletableFuture<Status> linkOrderToTransaction_CF(PurchaseRequest purchaseRequest) {
		return CompletableFuture.supplyAsync( ()-> linkOrderToTransaction_Sync(purchaseRequest));
	}

	public Single<TransactionValidation> validate_Rx(PurchaseRequest purchaseRequest, CustomerData customerData) {
		return Single.defer( ()->Single.just( validate_Sync(purchaseRequest, customerData)));
	}
	
	public Single<Status> linkOrderToTransaction_Rx(PurchaseRequest purchaseRequest) {
		return Single.defer( ()->Single.just( linkOrderToTransaction_Sync(purchaseRequest)));
	}
}
