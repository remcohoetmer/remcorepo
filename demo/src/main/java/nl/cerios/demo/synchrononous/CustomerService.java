package nl.cerios.demo.synchrononous;

import nl.cerios.demo.CustomerData;
import nl.cerios.demo.LocationConfig;

public class CustomerService {

	public CustomerData getCustomerData(Object customerId) throws ValidationException {

		return new CustomerData(customerId);
	}

	public CustomerValidation validateCustomer(CustomerData customerData, LocationConfig locationData) {
		return new CustomerValidation();
	}
}

