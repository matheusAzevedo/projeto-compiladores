package compiler.exceptions;

public class InvalidParameterException extends Exception {
	private static final long serialVersionUID = 1L;

	public InvalidParameterException(String message) {
		super(message);
	}
}
