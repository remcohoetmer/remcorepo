package nl.cerios.demo.service;

public class CustomerValidation {
	Status status;
	String message;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public Status getStatus()
	{
		return status;
	}


}
