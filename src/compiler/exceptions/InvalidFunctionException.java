package compiler.exceptions;

public class InvalidFunctionException  extends Exception {
	private static final long serialVersionUID = 1L;
	
	public InvalidFunctionException(String message){
		super(message);
	}
}