package nl.cerios.demo.service;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import io.reactivex.Single;
import nl.cerios.demo.CF_Utils;

public class CustomerService {
	private static final Logger LOG = Logger.getLogger(CustomerService.class.getName());

	public CustomerData getCustomerData_Sync(Integer customerId) throws ValidationException {
		/*
		LOG.info( Thread.currentThread().getName());
		new Exception().printStackTrace();
		if (true) {
			String address="http://localhost:8080/http-1/im";
			//new HttpServiceClient().send(address);
		}
		*/
		return new CustomerData(customerId);
	}

	public CustomerValidation validateCustomer_Sync(CustomerData customerData, LocationConfig locationData) {
		LOG.info( Thread.currentThread().getName());
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
		return CompletableFuture.supplyAsync( ()-> validateCustomer_Sync(customerData, locationData));
	}

	public Single<CustomerData> getCustomerData_Rx(Integer customerId) {
		return Single.defer( ()->Single.just( getCustomerData_Sync(customerId))); 
	}

	public Single<CustomerValidation> validateCustomer_Rx(CustomerData customerData, LocationConfig locationData) {
		return Single.defer( ()->Single.just( validateCustomer_Sync(customerData, locationData)));
	}

}
