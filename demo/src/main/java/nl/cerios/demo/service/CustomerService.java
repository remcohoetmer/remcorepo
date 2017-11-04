package nl.cerios.demo.service;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import nl.cerios.demo.CF_Utils;
import reactor.core.publisher.Mono;

public class CustomerService {
    private static final Logger LOG = Logger.getLogger(CustomerService.class.getName());

    public CustomerData retrieveCustomerData_Sync(Integer customerId) throws ValidationException {
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
        LOG.info(Thread.currentThread().getName());
        CustomerValidation validation = new CustomerValidation();
        Status status = Status.OK;
        if (customerData.getCustomerId() != locationData.getLocationId()) {
            status = Status.NOT_OK;
        }
        validation.status = status;
        switch (status) {
            case NOT_OK:
                validation.setMessage("Customer validation failed");
                break;
            case OK:
                validation.setMessage("Customer OK");
                break;
        }
        return validation;
    }

    public CompletableFuture<CustomerData> retrieveCustomerData_CF(Integer customerId) {
        return CompletableFuture.supplyAsync(CF_Utils.transportException(() -> retrieveCustomerData_Sync(customerId)));
    }

    public CompletableFuture<CustomerValidation> validateCustomer_CF(CustomerData customerData, LocationConfig locationData) {
        return CompletableFuture.supplyAsync(() -> validateCustomer_Sync(customerData, locationData));
    }

    public Mono<CustomerData> getCustomerData_Reactor(Integer customerId) {
        return Mono.defer(() -> {
            try {
                return Mono.just(retrieveCustomerData_Sync(customerId));
            } catch (ValidationException e) {
                return Mono.error(e);
            }
        });
    }

    public Mono<CustomerValidation> validateCustomer_Reactor(CustomerData customerData, LocationConfig locationData) {
        return Mono.defer(() -> Mono.just(validateCustomer_Sync(customerData, locationData)));
    }

}
