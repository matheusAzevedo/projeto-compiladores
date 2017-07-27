package compiler.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import compiler.core.Expression;
import compiler.core.Parameter;
import compiler.core.Type;
import compiler.core.Variable;
import compiler.exceptions.InvalidFunctionException;
import compiler.exceptions.InvalidParameterException;
import compiler.exceptions.InvalidTypeException;
import compiler.exceptions.InvalidVariableException;

public class SemanticsImpl implements Semantics {
	private static SemanticsImpl instance;
	private static List<Type> BASIC_TYPES;
	private List<Type> secondaryTypes;
	private static Map<String, List<String>> compatibleTypes;

	public static SemanticsImpl getInstance() {
		if (instance == null) {
			instance = new SemanticsImpl();
		}
		return instance;
	}

	public SemanticsImpl() {
		this.secondaryTypes = new ArrayList<>();
		this.compatibleTypes = new HashMap<>();
		initBasicTypes();
		initCompatibleTypes();
	}

	@Override
	public boolean checkVariableExists(String variableName) {
		return false;
	}

	@Override
	public boolean checkMethodExists(String methodName) {
		return false;
	}

	@Override
	public boolean checkValidExistingType(Type type) {
		return BASIC_TYPES.contains(type) || secondaryTypes.contains(type);
	}

	@Override
	public boolean checkTypeCompatibility(Type leftType, Type rightType) {
		return false;
	}

	@Override
	public void validateFunction(String functionName, ArrayList<Parameter> params, Type declaredType)
			throws InvalidFunctionException, InvalidParameterException {
	}

	@Override
	public void validateVariableName(String variableName) throws InvalidVariableException {		
	}
	
	@Override
	public Variable findVariableByIdentifier(String variableName) {
		return null;
	}
	
	@Override
	public void exitCurrentScope() throws InvalidFunctionException {
	}

	@Override
	public void exitCurrentScope(Expression exp) throws InvalidFunctionException {
	}

	@Override
	public void addType(Type type) {
		if (!secondaryTypes.contains(type)) {
			secondaryTypes.add(type);

			List<String> types = new ArrayList<String>();
			types.add(type.getTypeName());
			compatibleTypes.put(type.getTypeName(), types);
		}
	}

	@Override
	public void addSuperType(String className, String superClassName) throws InvalidTypeException {
		if (superClassName != null) {
			if (compatibleTypes.containsKey(superClassName)) {
				compatibleTypes.get(superClassName).add(className);
				return;
			}
			throw new InvalidTypeException("A Superclasse não existe.");
		}
	}

	@Override
	public void addVariablesFromTempList(Type type) throws Exception {
	}

	@Override
	public void addVariableToTempList(Variable var) {
	}

	@SuppressWarnings("serial")
	private static void initBasicTypes() {
		BASIC_TYPES = new ArrayList<Type>() {
			{
				add(new Type("int"));
				add(new Type("float"));
				add(new Type("double"));
				add(new Type("long"));
				add(new Type("char"));
				add(new Type("void"));
				add(new Type("boolean"));
				add(new Type("Object"));
				add(new Type("Integer"));
				add(new Type("String"));
			}
		};
	}

	private static void initCompatibleTypes() {
		List<String> doubleCompTypes = new ArrayList<String>();
		doubleCompTypes.add("int");
		doubleCompTypes.add("float");
		doubleCompTypes.add("double");
		doubleCompTypes.add("long");

		List<String> floatCompTypes = new ArrayList<String>();
		floatCompTypes.add("int");
		floatCompTypes.add("float");
		floatCompTypes.add("long");

		List<String> longCompTypes = new ArrayList<String>();
		longCompTypes.add("long");
		longCompTypes.add("int");

		List<String> intCompTypes = new ArrayList<String>();
		intCompTypes.add("int");

		List<String> booleanCompTypes = new ArrayList<String>();
		booleanCompTypes.add("boolean");

		compatibleTypes.put("double", doubleCompTypes);
		compatibleTypes.put("float", floatCompTypes);
		compatibleTypes.put("long", longCompTypes);
		compatibleTypes.put("int", intCompTypes);
		compatibleTypes.put("boolean", booleanCompTypes);
	}
}
