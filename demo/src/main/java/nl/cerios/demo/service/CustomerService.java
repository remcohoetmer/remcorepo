package nl.cerios.demo.service;

import java.util.concurrent.CompletableFuture;

import io.reactivex.Flowable;
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
		case OK:     validation.setMessage("Customer OK");break;
		}
		return validation;
	}
	
	public CompletableFuture<CustomerData> getCustomerData_CF(Integer customerId) {
		return CompletableFuture.supplyAsync( CF_Utils.transportException( ()-> getCustomerData_Sync(customerId)));
	}

	public CompletableFuture<CustomerValidation> validateCustomer_CF(CustomerData customerData, LocationConfig locationData) {
		return CompletableFuture.supplyAsync( ()-> validateCustomer(customerData, locationData));
	}

	public Flowable<CustomerData> getCustomerData_Rx(Flowable<Integer> customerIdObs) {
		return customerIdObs.map( customerId -> getCustomerData_Sync(customerId));
	}

	public Flowable<CustomerValidation> validateCustomer_Rx(CustomerData customerData, LocationConfig locationData) {
		return Flowable.defer( ()->Flowable.just( validateCustomer(customerData, locationData)));
	}

}
