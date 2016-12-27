package nl.cerios.demo.synchrononous;

import nl.cerios.demo.CustomerData;
import nl.cerios.demo.LocationConfig;

public class PurchaseRequest {

	public PurchaseRequest() {
	}
	public PurchaseRequest(CustomerData customerData, LocationConfig locationData) {
	}

	public Integer getLocationId() {
		return 1;
	}

	public Integer getCustomerId() {
		return 1;
	}


	public void setValidationError(CustomerValidation customerValidation) {
		
	}
	public void setValidationError(TransactionValidation transactionValidation) {
		
	}
	
}
