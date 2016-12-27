package nl.cerios.demo.synchrononous;

import nl.cerios.demo.CustomerData;

public class TransactionService {

	public TransactionValidation validate(PurchaseRequest purchaseRequest, CustomerData customerData) {
		return new TransactionValidation();
	}

}
