package nl.cerios.demo.service;

import io.reactivex.Observable;

public class PurchaseResponse {
	private PurchaseRequest purchaseRequest; 
	private Observable<CharSequence> document;

	public Observable<CharSequence> getDocument() {
		return document;
	}
	public void setDocument(Observable<CharSequence> document) {
		this.document = document;
	}
	public PurchaseRequest getPurchaseRequest() {
		return purchaseRequest;
	}
	public void setPurchaseRequest(PurchaseRequest purchaseRequest) {
		this.purchaseRequest = purchaseRequest;
	}
	@Override
	public String toString() {
		StringBuffer sb= new StringBuffer();
		if (purchaseRequest!=  null) {
			sb.append(purchaseRequest);
		}
		return sb.toString();
	}
}
