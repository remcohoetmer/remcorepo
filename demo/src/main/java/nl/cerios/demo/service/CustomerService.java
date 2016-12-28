package nl.cerios.demo.service;

import java.util.concurrent.CompletableFuture;

import io.reactivex.Flowable;
import io.reactivex.Single;
import nl.cerios.demo.CF_Utils;

public class CustomerService {


	public CustomerData getCustomerData_Sync(Integer customerId) throws ValidationException {

		return new CustomerData(customerId);
	}
		
	public CustomerValidation validateCustomer(CustomerData customerData, LocationConfig locationData) {
		CustomerValidation validation= new CustomerValidation();
		Status status= Status.OK;
		if ( customerData.getCustomerId()!= locationData.getLocationId()) {
			status= Status.NOT_OK;
		}
		validation.status= status;
		switch (status){
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

	public Single<CustomerData> getCustomerData_Rx(Single<Integer> customerIdObs) {
		return customerIdObs.map( customerId -> getCustomerData_Sync(customerId));
	}

	public Single<CustomerValidation> validateCustomer_Rx(CustomerData customerData, LocationConfig locationData) {
		return Single.defer( ()->Single.just( validateCustomer(customerData, locationData)));
	}

}
