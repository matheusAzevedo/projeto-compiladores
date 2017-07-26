package compiler.analysis;

import java.util.ArrayList;
import java.util.List;

import compiler.core.Type;

public class SemanticsImpl implements Semantics {
	private static SemanticsImpl instance;
	private static List<Type> BASIC_TYPES;

	public static SemanticsImpl getInstance() {
		if(instance == null) {
			instance = new SemanticsImpl();
		}
		return instance;
	}
	
	public SemanticsImpl() {
		initBasicTypes();
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
		return BASIC_TYPES.contains(type);
	}

	@Override
	public boolean checkTypeCompatibility(Type leftType, Type rightType) {
		return false;
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
				add(new Type("String"));
				add(new Type("boolean"));
				add(new Type("Object"));
				add(new Type("Integer"));
			}
		};
	}

}
