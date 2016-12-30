package nl.cerios.demo.service;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import nl.cerios.demo.CF_Utils;
import nl.cerios.demo.backend.HttpServiceClient;

public class CustomerService {
	private static final Logger LOG = Logger.getLogger(CustomerService.class.getName());

	public CustomerData getCustomerData_Sync(Integer customerId) throws ValidationException {
		LOG.info( Thread.currentThread().getName());
		new Exception().printStackTrace();
		if (true) {
			String address="http://localhost:8080/http-1/im";
			new HttpServiceClient().send(address);
		}
		return new CustomerData(customerId);
	}

	public CustomerValidation validateCustomer(CustomerData customerData, LocationConfig locationData) {
		LOG.info( Thread.currentThread().getName());
		new Exception().printStackTrace();
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
		return customerIdObs.observeOn(Schedulers.newThread()).map( this::getCustomerData_Sync);
	}

	public Single<CustomerValidation> validateCustomer_Rx(CustomerData customerData, LocationConfig locationData) {
		return Single.defer( ()->Single.just( validateCustomer(customerData, locationData)));
	}

}
