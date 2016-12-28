package nl.cerios.demo.service;

public class ValidationException extends Exception {
	private static final long serialVersionUID = 1L;

	public ValidationException(String string) {
		super( string);
	}
}
