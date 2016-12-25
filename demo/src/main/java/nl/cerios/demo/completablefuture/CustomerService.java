package nl.cerios.demo.completablefuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import nl.cerios.demo.CustomerData;

public class CustomerService {

	public CompletionStage<CustomerData> getCustomerData(Integer customerId) {

		return CompletableFuture.supplyAsync(()->
		{
			if (customerId==0) {

				throw new IllegalStateException();
			} else 
				return new CustomerData(customerId);
		}
				);
	}
}

