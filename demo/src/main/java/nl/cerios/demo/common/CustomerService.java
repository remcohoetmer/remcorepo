package nl.cerios.demo.common;

import java.util.concurrent.CompletableFuture;

import nl.cerios.demo.CF_Utils;

public class CustomerService {


	public CustomerData getCustomerData_Sync(Integer customerId) throws ValidationException {

		return new CustomerData(customerId);
	}
		
	public CustomerValidation validateCustomer(CustomerData customerData, LocationConfig locationData) {
		CustomerValidation validation= new CustomerValidation();
		validation.status= customerData.getCustomerId()==null?Status.NOT_OK:Status.OK;
		switch (validation.status){
		case NOT_OK: validation.setMessage("Customer validation failed"); break;
		case OK: validation.setMessage("Customer validation failed");
		}
		return validation;
	}
	
	public CompletableFuture<CustomerData> getCustomerData_CF(Integer customerId) {
		return CompletableFuture.supplyAsync( CF_Utils.transportException( ()-> getCustomerData_Sync(customerId)));
	}
	public CompletableFuture<CustomerValidation> validateCustomer_CF(CustomerData customerData, LocationConfig locationData) {
		return CompletableFuture.supplyAsync( ()-> validateCustomer(customerData, locationData));
	}
	
}

