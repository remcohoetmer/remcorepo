package nl.cerios.demo.common;

public class TransactionService {

	public TransactionValidation validate(PurchaseRequest purchaseRequest, CustomerData customerData) {
		TransactionValidation validation= new TransactionValidation();
		if (customerData.getCustomerId()==0) {
			validation.setStatus( Status.NOT_OK);
		} else {
			validation.setStatus( Status.NOT_OK);
		}
		return validation;
	}

	public Status linkOrderToTransaction(PurchaseRequest purchaseRequest) {
		return null;
	}

}
