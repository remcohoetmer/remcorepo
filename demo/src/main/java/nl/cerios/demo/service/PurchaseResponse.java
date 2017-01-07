package nl.cerios.demo.service;

public class PurchaseResponse {
	Integer orderId;
	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	@Override
    public String toString() {
		StringBuffer sb= new StringBuffer();
		sb.append( " orderId ").append( orderId);
		return sb.toString();
	}

}
