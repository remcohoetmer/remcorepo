package nl.cerios.demo.completablefuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import nl.cerios.demo.CustomerData;

public class CustomerService {

	public CompletionStage<CustomerData> getCustomerData(Object customerId) {

		return CompletableFuture.supplyAsync(()->
		{throw new IllegalStateException();}
		//new CustomerData(customerId)

				);
	}
}

