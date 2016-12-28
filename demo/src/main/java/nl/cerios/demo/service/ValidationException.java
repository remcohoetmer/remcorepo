package nl.cerios.demo.service;

import java.util.Objects;

public class ValidationException extends Exception {
	private static final long serialVersionUID = 1L;

	public ValidationException(String string) {
		super( string);
	}
	@Override
	public boolean equals(Object o) {
	    if (this == o)
	        return true;
	    if (o == null)
	        return false;
	    if (getClass() != o.getClass())
	        return false;
	    ValidationException ex = (ValidationException) o;
	    return Objects.equals(getMessage(), ex.getMessage());
	}
}
