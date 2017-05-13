package nl.cerios.demo.service;


import reactor.core.publisher.Flux;

public class PurchaseResponse {
	private PurchaseRequest purchaseRequest; 
	private Flux<CharSequence> document;

	public Flux<CharSequence> getDocument() {
		return document;
	}
	public void setDocument(Flux<CharSequence> document) {
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		PurchaseResponse that = (PurchaseResponse) o;

		if (!purchaseRequest.equals(that.purchaseRequest)) return false;
		return document.equals(that.document);
	}

	@Override
	public int hashCode() {
		int result = purchaseRequest.hashCode();
		result = 31 * result + document.hashCode();
		return result;
	}
}
