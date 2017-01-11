package nl.cerios.demo.service;

import io.reactivex.Observable;

public class PurchaseResponse {
	private Integer orderId;
	private PurchaseRequest purchaseRequest; 
	private Observable<CharSequence> document;
	
	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

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
		sb.append( " orderId ").append( orderId);
		return sb.toString();
	}
}
