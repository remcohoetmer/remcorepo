package nl.cerios.demo.service;

public class CustomerData {
	private Integer customerId;
	public Integer getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}
	public CustomerData(Integer customerId) {
		this.customerId=customerId;
	}

}
