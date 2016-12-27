package nl.cerios.demo.common;

import java.util.concurrent.CompletableFuture;

public class CustomerService {

	public CompletableFuture<CustomerData> getCustomerData_CF(Integer customerId) {

		return CompletableFuture.supplyAsync( ()-> new CustomerData(customerId));
	}
	
	public CustomerData getCustomerData_Sync(Integer customerId) throws ValidationException {

		return new CustomerData(customerId);
	}
		
	public CustomerValidation validateCustomer(CustomerData customerData, LocationConfig locationData) {
		return new CustomerValidation();
	}
}

