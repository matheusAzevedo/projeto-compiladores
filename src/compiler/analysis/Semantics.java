package compiler.analysis;

import compiler.core.Type;
import compiler.exceptions.InvalidTypeException;

public interface Semantics {
	public boolean checkVariableExists(String variableName);
	public boolean checkMethodExists(String methodName);
	public boolean checkValidExistingType(Type type);
	public boolean checkTypeCompatibility(Type leftType, Type rightType);
	
	public void addType(Type type);
	public void addSuperType(String className, String superClassName) throws InvalidTypeException;
}
