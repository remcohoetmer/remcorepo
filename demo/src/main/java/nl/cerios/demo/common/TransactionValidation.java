package nl.cerios.demo.common;

public class TransactionValidation {
	Status status= Status.OK;
	
	public void setStatus(Status status) {
		this.status = status;
	}
	public Status getStatus()
	{
		return status;
	}
	
	public String getMessage() {
		return "Missing money transfer";
	}

}
