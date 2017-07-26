package compiler.analysis;

import compiler.core.Type;

public interface Semantics {
	public boolean checkVariableExists(String variableName);
	public boolean checkMethodExists(String methodName);
	public boolean checkValidExistingType(Type type);
	public boolean checkTypeCompatibility(Type leftType, Type rightType);
}
