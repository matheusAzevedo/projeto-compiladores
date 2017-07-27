package compiler.exceptions;

public class InvalidVariableException extends Exception {
	private static final long serialVersionUID = 1L;

	public InvalidVariableException(String message){
		super(message);
	}
}