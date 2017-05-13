package nl.cerios.demo.service;

public class PurchaseRequest {


  Integer purchaseRequestId;
  Integer locationId;
  Integer customerId;
  private Integer orderId;
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
    StringBuffer sb = new StringBuffer();
    sb.append(" customerId ").append(customerId);
    sb.append(" transactionId ").append(transactionId);
    sb.append(" orderId ").append(orderId);
    return sb.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PurchaseRequest that = (PurchaseRequest) o;

    if (purchaseRequestId != null ? !purchaseRequestId.equals(that.purchaseRequestId) : that.purchaseRequestId != null)
      return false;
    if (locationId != null ? !locationId.equals(that.locationId) : that.locationId != null) return false;
    if (customerId != null ? !customerId.equals(that.customerId) : that.customerId != null) return false;
    if (orderId != null ? !orderId.equals(that.orderId) : that.orderId != null) return false;
    return transactionId != null ? transactionId.equals(that.transactionId) : that.transactionId == null;
  }

  @Override
  public int hashCode() {
    int result = purchaseRequestId != null ? purchaseRequestId.hashCode() : 0;
    result = 31 * result + (locationId != null ? locationId.hashCode() : 0);
    result = 31 * result + (customerId != null ? customerId.hashCode() : 0);
    result = 31 * result + (orderId != null ? orderId.hashCode() : 0);
    result = 31 * result + (transactionId != null ? transactionId.hashCode() : 0);
    return result;
  }

}
