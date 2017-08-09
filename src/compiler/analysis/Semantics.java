package compiler.analysis;

import compiler.core.Type;
import compiler.exceptions.InvalidFunctionException;
import compiler.exceptions.InvalidParameterException;
import compiler.exceptions.InvalidTypeException;
import compiler.exceptions.InvalidVariableException;

import java.util.ArrayList;

import compiler.core.*;

public interface Semantics {
	public boolean checkVariableExists(String variableName);
	public boolean checkMethodExists(String methodName);
	public boolean checkValidExistingType(Type type);
	public boolean checkTypeCompatibility(Type leftType, Type rightType);
	
	public void validateFunction(String functionName, ArrayList<Parameter> params, Type declaredType) throws InvalidFunctionException, InvalidParameterException, Exception;
	public void validateVariableName(String variableName) throws InvalidVariableException;
	
	public Variable findVariableByIdentifier(String variableName);
	
	public void exitCurrentScope() throws InvalidFunctionException;
	public void exitCurrentScope(Expression exp) throws InvalidFunctionException; 
	
	public void addType(Type type);
	public void addSuperType(String className, String superClassName) throws InvalidTypeException;
	public void addVariablesFromTempList(Type type) throws Exception;
	public void addVariableToTempList(Variable var);
}
