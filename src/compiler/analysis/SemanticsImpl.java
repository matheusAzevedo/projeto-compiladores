package compiler.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import compiler.core.Expression;
import compiler.core.For;
import compiler.core.Function;
import compiler.core.Parameter;
import compiler.core.ScopedEntity;
import compiler.core.Type;
import compiler.core.Variable;
import compiler.exceptions.InvalidFunctionException;
import compiler.exceptions.InvalidOperationException;
import compiler.exceptions.InvalidParameterException;
import compiler.exceptions.InvalidTypeException;
import compiler.exceptions.InvalidVariableException;
import compiler.generator.CodeGenerator;
import util.Calculator;

public class SemanticsImpl implements Semantics {
	private static SemanticsImpl instance;
	private static List<Type> BASIC_TYPES;
	private List<Type> secondaryTypes;
	private List<Variable> tempVariables;
	private static Map<String, List<String>> compatibleTypes;
	private Stack<ScopedEntity> scopedEntities;
	private ArrayList<Function> functions;
	private HashMap<String, Variable> vars;
	private static String currentOperator;
	private static Calculator calculator;
	private static boolean isForExp;
	public int nfor = 0;
	public static CodeGenerator codeGenerator;

	public static SemanticsImpl getInstance() {
		if (instance == null) {
			instance = new SemanticsImpl();
			calculator = new Calculator();
			codeGenerator = new CodeGenerator();
			instance.setForExp(false);
		}
		return instance;
	}

	public SemanticsImpl() {
		this.secondaryTypes = new ArrayList<>();
		this.compatibleTypes = new HashMap<>();
		this.functions = new ArrayList<Function>();
		this.scopedEntities = new Stack<ScopedEntity>();
		this.vars = new HashMap<String, Variable>();
		this.tempVariables = new ArrayList<Variable>();
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
		if (leftType.equals(rightType)) {
			return true;
		} else {
			List<String> tipos = compatibleTypes.get(leftType.getTypeName());
			if (tipos == null) {
				return false;
			}
			return tipos.contains(rightType.getTypeName());
		}
	}

	@Override
	public void validateFunction(String functionName, ArrayList<Parameter> params, Type declaredType)
			throws InvalidFunctionException, InvalidParameterException, Exception {
		if (declaredType == null) {
			throw new InvalidFunctionException("ERRO: O método " + functionName
					+ " está sem declaração do tipo de retorno, ou se possui, não contem retorno no seu fim!");
		}
		Function temp = new Function(functionName, params);
		temp.setDeclaredReturnType(declaredType);

		if (checkFunctionExistence(temp)) {
			if (params != null) {
				checkExistingParameter(params);
			}

			String keyFunc = functionName + " ";
			if (params != null) {
				for (Parameter p : params) {
					keyFunc += p.getType().getTypeName();
				}
			}
			addFunctionAndNewScope(temp);
		}
	}

	@Override
	public void validateVariableName(String variableName) throws InvalidVariableException {
		if (!checkVariableExistence(variableName)) {
			throw new InvalidVariableException("ERROR: A variavel chamada " + variableName + " nÃ£o existe!");
		}
	}

	@Override
	public Variable findVariableByIdentifier(String variableName) {
		if (!scopedEntities.isEmpty() && getCurrentScope().getVars().get(variableName) != null) {
			return getCurrentScope().getVars().get(variableName);
		} else {
			return vars.get(variableName);
		}
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
			throw new InvalidTypeException("A Superclasse nÃ£o existe.");
		}
	}

	@Override
	public void addVariablesFromTempList(Type type) throws Exception {
		for (Variable variable : tempVariables) {
			variable.setType(type);
			addVariable(variable);
		}

		tempVariables = new ArrayList<Variable>();
	}

	@Override
	public void addVariableToTempList(Variable var) {
		tempVariables.add(var);
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

	public ScopedEntity getCurrentScope() {
		return scopedEntities.peek();
	}

	private void createScope(ScopedEntity scope) {
		scopedEntities.push(scope);
	}

	public void addFunctionAndNewScope(Function f) throws Exception {
		functions.add(f);
		createNewScope(f);
		if (f.getParams() != null) {
			for (Parameter p : f.getParams()) {
				addVariable((Variable) p);
			}
		}
	}

	private void createNewScope(ScopedEntity scope) {
		scopedEntities.push(scope);
	}

	public void checkIsBoolean(Type type) throws InvalidTypeException {
		if (!checkTypeCompatibility(new Type("boolean"), type)) {
			throw new InvalidTypeException("ExpressÃµes lÃ³gicas precisam ser entre booleanos.");
		}
	}

	public boolean checkFunctionExistence(Function temp) throws InvalidFunctionException {
		for (Function fun : functions) {
			if (fun.getName().equals(temp.getName())) {
				if (!fun.getDeclaredReturnType().getTypeName().equals(temp.getDeclaredReturnType().getTypeName())) {
					throw new InvalidFunctionException(
							"ERRO: O método " + temp.getName() + " ja foi declarado com um tipo de retorno diferente!");
				}
				if (temp.equals(fun)) {
					throw new InvalidFunctionException(
							"ERRO: O método " + temp.getName() + " ja foi declarado com esses mesmos parâmetros!");
				}

			}
		}
		return true;
	}

	private void checkExistingParameter(ArrayList<Parameter> params) throws InvalidParameterException {
		for (int i = 0; i < params.size(); i++) {
			for (int k = i + 1; k < params.size(); k++) {
				if (params.get(i).getIdentifier().equals(params.get(k).getIdentifier())) {
					throw new InvalidParameterException(
							"ERRO: O parâmetro: " + params.get(k).getIdentifier() + " ja foi definido.");
				}
			}
		}
	}

	public boolean checkVariableExistence(String variableName) {
		if (!scopedEntities.isEmpty() && getCurrentScope().getVars().get(variableName) != null) {
			return true;
		} else if (vars.get(variableName) != null) {
			return true;
		} else {
			return false;
		}
	}

	public boolean checkVariableExistenceLocal(String variableName) {
		if (!scopedEntities.isEmpty() && getCurrentScope().getVars().get(variableName) != null) {
			return true;
		} else {
			return false;
		}
	}

	public boolean checkVariableExistenceGlobal(String variableName) {
		return vars.get(variableName) != null ? true : false;
	}

	private void validateGlobalVar(Variable variable) throws Exception {
		if (checkVariableExistenceGlobal(variable.getIdentifier())) {
			throw new InvalidVariableException("ERROR: A variavel de nome " + variable.getIdentifier() + " e tipo "
					+ variable.getType().getTypeName() + " ja existem!");
		}
		if (!checkValidExistingType(variable.getType())) {
			if (!variable.getValue().getType().getTypeName().equals("null")) {
				throw new InvalidTypeException("ERROR: O tipo " + variable.getType().getTypeName() + " da variavel "
						+ variable.getIdentifier() + " nÃ£o existem!");
			}
		}
	}

	private void validateVariable(Variable variable) throws Exception {
		if (checkVariableExistenceLocal(variable.getIdentifier())) {
			throw new InvalidVariableException("ERROR: A variavel de nome " + variable.getIdentifier() + " e tipo "
					+ variable.getType().getTypeName() + " já existem!");
		}
		if (!checkValidExistingType(variable.getType())) {
			if (!variable.getValue().getType().getTypeName().equals("null")) {
				throw new InvalidTypeException("ERROR: O tipo " + variable.getType().getTypeName() + " da variavel "
						+ variable.getIdentifier() + " não existem!");
			}
		}
	}

	private void addVariable(Variable variable) throws Exception {
		if (scopedEntities.isEmpty()) {
			validateGlobalVar(variable);
			vars.put(variable.getIdentifier(), variable);
		} else {
			validateVariable(variable);
			getCurrentScope().addVariable(variable);
		}

		if (variable.getValue() != null) {
			checkVariableAttribution(variable.getIdentifier(), variable.getValue());
		}
	}

	public void createFunction(Function function) throws Exception {
		functions.add(function);
		createScope(function);
		if (function.getParams() != null) {
			for (Parameter p : function.getParams()) {
				addVariable((Variable) p);
			}
		}
	}

	public void createFor(Variable var, Expression e1, Expression e2)
			throws InvalidTypeException, InvalidOperationException {
		For f = new For("For");

		for (Variable v : getCurrentScope().getVars().values()) {
			f.addVariable(v);
		}

		if (var != null) {
			f.addVariable(var);
		}

		if (e1 != null) {
			if (!e1.getType().getTypeName().equals("boolean")) {
				throw new InvalidTypeException("ERROR: O valor para a expressão deveria ser boolean, porém é do tipo "
						+ e1.getType().getTypeName());
			}
			setForExp(false);
		}

		if (e2 != null) {
			if (e2.getType().getTypeName().equals("boolean")) {
				throw new InvalidTypeException(
						"ERROR: A expressão deveria ser aritimética, porém é uma expressão booleana");
			}
		}

		if (var != null) {
			getCurrentScope().getVars().remove(var.getIdentifier());
		}

		scopedEntities.push(f);
	}

	public void dismissCurrentFor(Expression aexp)
			throws InvalidFunctionException, InvalidOperationException, InvalidTypeException {
		nfor--;
		ScopedEntity scoped = scopedEntities.pop();
	}

	public boolean verifyCallMethod(String methodName, ArrayList<Expression> argList) throws InvalidFunctionException {
		boolean isCorrectParams = false;

		for (Function function : functions) {
			if (function.getName().equals(methodName)) {
				ArrayList<Parameter> parameters = (ArrayList<Parameter>) function.getParams();
				if (parameters.size() != argList.size()) {
					isCorrectParams = true;
					continue;
				}

				for (int i = 0; i < parameters.size(); i++) {
					if (!parameters.get(i).getType().getTypeName().equals(argList.get(i).getType().getTypeName())) {
						throw new InvalidFunctionException("ERROR: O mÃ©todo " + methodName + " esperava o tipo "
								+ parameters.get(i).getType().getTypeName() + " mas foi passado o tipo "
								+ argList.get(i).getType().getTypeName());
					}
				}
				return true;
			}
		}

		if (isCorrectParams) {
			throw new InvalidFunctionException(
					"ERROR: O mÃ©todo " + methodName + " possui uma quantidade invÃ¡lida de parÃ¢metros!");
		} else {
			throw new InvalidFunctionException("ERROR: O mÃ©todo " + methodName + " nÃ£o existe!");
		}
	}

	public void checkVariableAttribution(String id, Expression expression)
			throws InvalidVariableException, InvalidTypeException, InvalidFunctionException {
		if (!checkVariableExistence(id)) {
			throw new InvalidVariableException(
					"ERROR: A variavel chamada " + id + " e com valor " + expression.getValue() + " nÃ£o existe!");
		}
		if (!checkValidExistingType(expression.getType())) {
			if (!expression.getType().getTypeName().equals("null")) {
				throw new InvalidTypeException("ERROR: O tipo " + expression.getType().getTypeName()
						+ " atribuido a variavel " + id + " nÃ£o existe!");
			}
		}
		Type identifierType = findVariableByIdentifier(id).getType();
		if (!checkTypeCompatibility(identifierType, expression.getType())) {
			String exceptionMessage = String.format("ERROR: Tipos incompativeis! %s nÃ£o e  compativel com %s",
					identifierType, expression.getType());
			throw new InvalidFunctionException(exceptionMessage);
		}
	}

	public void checkVariableAttribution(String id, String function)
			throws InvalidVariableException, InvalidTypeException, InvalidFunctionException {
		if (!checkVariableExistence(id)) {
			throw new InvalidVariableException(
					"ERROR: A variavel chamada " + id + " atribuida a funÃ§Ã£o " + function + " nÃ£o existe!");
		}
		Type identifierType = findVariableByIdentifier(id).getType();

		for (Function f : functions) {
			if (f.getName().equals(function)) {
				if (!checkTypeCompatibility(identifierType, f.getDeclaredReturnType())) {
					String exceptionMessage = String.format("ERROR: Tipos incompativeis! %s nÃ£o Ã© compativel com %s",
							identifierType, f.getDeclaredReturnType());
					throw new InvalidFunctionException(exceptionMessage);
				}

			}
		}
	}

	public Expression getExpression(Expression le, String md, Expression re)
			throws InvalidTypeException, InvalidOperationException {
		if (checkTypeCompatibility(le.getType(), re.getType()) || checkTypeCompatibility(re.getType(), le.getType())) {
			Type newType = getMaxType(le.getType(), re.getType());
			String result;
			switch (md) {
			case "+":
				result = calculator.getSumNumericValue(le, re, md);
				return new Expression(newType, result);
			case "-":
				result = calculator.getSubNumericValue(le, re, md);
				return new Expression(newType, result);
			case "*":
				result = calculator.getMultNumericValue(le, re, md);
				return new Expression(newType, result);
			case "/":
				result = calculator.getDivNumericValue(le, re, md);
				return new Expression(newType, result);
			default:
				break;
			}
			return new Expression(newType);
		}
		throw new InvalidTypeException("Not allowed!");
	}

	private Type getMaxType(Type type1, Type type2) {
		return compatibleTypes.get(type1.getTypeName()).contains(type2.getTypeName()) ? type1 : type2;
	}

	public void setCurrentOperator(boolean newCurrentOperator) {
		currentOperator = newCurrentOperator + "";
	}

	public boolean isRelationalExpression(Expression le, Expression re) throws InvalidOperationException {
		if (!le.getType().equals(re.getType())) {
			throw new InvalidOperationException(
					"ERROR: A expressão não é relacional, que é formada pelas subexpressões de valor " + le.getValue()
							+ " do tipo " + le.getType().getTypeName() + " e de valor " + re.getValue() + " do tipo "
							+ re.getType().getTypeName());
		}
		return true;
	}

	public boolean isNumericExpression(Expression e) throws InvalidOperationException {
		if (!e.isNumeric()) {
			throw new InvalidOperationException(
					"ERROR: A expressão de tipo '" + e.getType() + "' e valor '" + e.getValue() + "' não é numérica");
		}
		return true;
	}

	public boolean isNumericExpression(Expression e1, Expression e2) throws InvalidOperationException {
		if (e1 != null && e1.isNumeric()) {
			if (e2 != null && e2.isNumeric()) {
				return true;
			}
		} else if (isStringExpression(e1, e2)) {
			return true;
		}
		throw new InvalidOperationException("ERROR: A expressão não é numérica ou entre strings,'" + e1.getValue()
				+ "' com tipo '" + e1.getType().getTypeName() + "' e/ou a expressão " + e2.getValue() + " com tipo '"
				+ e2.getType().getTypeName());
	}

	public boolean isStringExpression(Expression le, Expression re) throws InvalidOperationException {
		if (le != null && le.getType().getTypeName().equalsIgnoreCase("String")) {
			return true;
		}

		if (re != null && re.getType().getTypeName().equalsIgnoreCase("String")) {
			return true;
		}

		return false;
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
		compatibleTypes.put("long", longCompTypes)	;
		compatibleTypes.put("int", intCompTypes);
		compatibleTypes.put("boolean", booleanCompTypes);
	}

	public static boolean isForExp() {
		return isForExp;
	}

	public void setForExp(boolean isForExp) {
		SemanticsImpl.isForExp = isForExp;
	}
	
	public CodeGenerator getCodeGenerator() {
        return codeGenerator;
    }
}
