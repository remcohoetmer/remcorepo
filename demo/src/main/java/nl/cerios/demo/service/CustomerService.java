package nl.cerios.demo.service;

import java.util.concurrent.CompletableFuture;

import io.reactivex.Observable;
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

	public Observable<CustomerData> getCustomerData_Rx(Integer customerId) {
		return Observable.defer( ()->Observable.just( getCustomerData_Sync(customerId)));
	}

	public Observable<CustomerValidation> validateCustomer_Rx(CustomerData customerData, LocationConfig locationData) {
		return Observable.defer( ()->Observable.just( validateCustomer(customerData, locationData)));
	}

}
