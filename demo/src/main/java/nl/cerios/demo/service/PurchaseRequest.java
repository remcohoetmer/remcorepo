package nl.cerios.demo.service;

public class PurchaseRequest {
	Integer purchaseRequestId;
	Integer locationId;
	Integer customerId;
	Integer orderId;
	Integer transactionId;
	
	public Integer getPurchaseRequestId() {
		return purchaseRequestId;
	}
	public void setPurchaseRequestId(Integer purchaseRequestId) {
		this.purchaseRequestId = purchaseRequestId;
	}
	public Integer getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(Integer transactionId) {
		this.transactionId = transactionId;
	}
	public Integer getLocationId() {
		return locationId;
	}
	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}
	public Integer getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}
	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}


	public PurchaseRequest() {
	}
	public PurchaseRequest(CustomerData customerData, LocationConfig locationData) {
	}
	
	@Override
    public String toString() {
		StringBuffer sb= new StringBuffer();
		sb.append( " customerId ").append( customerId);
		sb.append( " orderId ").append( orderId);
		sb.append( " transactionId ").append( transactionId);
		return sb.toString();
	}
}
