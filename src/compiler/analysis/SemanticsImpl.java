package compiler.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import compiler.core.Expression;
import compiler.core.For;
import compiler.core.Function;
import compiler.core.Operation;
import compiler.core.Parameter;
import compiler.core.Register;
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
    private Stack<Integer> condLabel;
	private ArrayList<Function> functions;
	private HashMap<String, Variable> vars;
	private static String currentOperator;
	private static Calculator calculator;
	public static boolean isForExp;
	public int nfor = 0;
	public static CodeGenerator codeGenerator;

	public static SemanticsImpl getInstance() {
		if (instance == null) {
			instance = new SemanticsImpl();
			codeGenerator = new CodeGenerator();
			instance.setForExp(false);
		}
		initCompatibleTypes();
		return instance;
		
	}

	protected SemanticsImpl() {
		this.secondaryTypes = new ArrayList<>();
		this.compatibleTypes = new HashMap<>();
		this.functions = new ArrayList<Function>();
		this.scopedEntities = new Stack<ScopedEntity>();
		this.condLabel = new Stack<Integer>();
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
			throw new InvalidFunctionException("ERRO: O m�todo " + functionName
					+ " est� sem declara��o do tipo de retorno, ou se possui, n�o contem retorno no seu fim!");
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
            codeGenerator.addFunctionAddress(keyFunc);
			addFunctionAndNewScope(temp);
		}
	}

	@Override
	public void validateVariableName(String variableName) throws InvalidVariableException {
		if (!checkVariableExistence(variableName)) {
			throw new InvalidVariableException(" A variavel chamada " + variableName + " n�o existe!");
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
		ScopedEntity scopedEntity = scopedEntities.pop();
		checkDeclaredAndReturnedType(scopedEntity.getName(), ((Function) scopedEntity).getDeclaredReturnType(), null);
	}

	@Override
	public void exitCurrentScope(Expression exp) throws InvalidFunctionException {
		ScopedEntity scoped = scopedEntities.pop();
		if (scoped instanceof Function) {
			if (exp != null) {
				checkDeclaredAndReturnedType(scoped.getName(), ((Function) scoped).getDeclaredReturnType(), exp);
			} else {
				if (!((Function) scoped).getDeclaredReturnType().equals(new Type("void"))) {
					throw new InvalidFunctionException(
							"ERROR: A fun��o " + scoped.getName() + " precisa retornar o tipo da vari�vel.");
				}
			}
		}
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
			throw new InvalidTypeException("A Superclasse n�o existe.");
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

	public String getFunctionType(String variableName) {
		for (Function f : functions) {
			if (f.getName().equals(variableName)) {
				return f.getDeclaredReturnType().getTypeName();
			}
		}
		return null;
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

	private void checkDeclaredAndReturnedType(String functionName, Type declaredType, Expression exp)
			throws InvalidFunctionException {
		if (exp == null && declaredType.equals(new Type("void"))) {
			return;
		}

		if (exp == null && !declaredType.equals(new Type("void"))) {
			throw new InvalidFunctionException("A fun��o '" + functionName + "' n�o tem retorno.");
		}

		boolean isReturn = exp.getContext().equalsIgnoreCase("return");

		if (!declaredType.equals(new Type("void"))) {
			if (!isReturn) {
				throw new InvalidFunctionException("A fun��o '" + functionName + "' n�o tem retorno.");
			}

			if (!declaredType.equals(exp.getType()) && !checkTypeCompatibility(declaredType, exp.getType())) {
				throw new InvalidFunctionException(
						"A fun��o " + functionName + " n�o retornou o tipo esperado: " + declaredType);
			}

		} else {
			if (isReturn) {
				if (exp.getType() != null) {
					throw new InvalidFunctionException(
							"A fun��o '" + functionName + "' � 'void' e n�o deve ter retorno.");
				}
			}
		}
	}

	public void checkIsBoolean(Type type) throws InvalidTypeException {
		if (!checkTypeCompatibility(new Type("boolean"), type)) {
			throw new InvalidTypeException("Expressões lógicas precisam ser entre booleanos.");
		}
	}

	public boolean checkFunctionExistence(Function temp) throws InvalidFunctionException {
		for (Function fun : functions) {
			if (fun.getName().equals(temp.getName())) {
				if (!fun.getDeclaredReturnType().getTypeName().equals(temp.getDeclaredReturnType().getTypeName())) {
					throw new InvalidFunctionException(
							"ERRO: O m�todo " + temp.getName() + " ja foi declarado com um tipo de retorno diferente!");
				}
				if (temp.equals(fun)) {
					throw new InvalidFunctionException(
							"ERRO: O m�todo " + temp.getName() + " ja foi declarado com esses mesmos par�metros!");
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
							"ERRO: O par�metro: " + params.get(k).getIdentifier() + " ja foi definido.");
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
						+ variable.getIdentifier() + " não existem!");
			}
		}
	}

	private void validateVariable(Variable variable) throws Exception {
		if (checkVariableExistenceLocal(variable.getIdentifier())) {
			throw new InvalidVariableException("ERROR: A variavel de nome " + variable.getIdentifier() + " e tipo "
					+ variable.getType().getTypeName() + " j� existem!");
		}
		if (!checkValidExistingType(variable.getType())) {
			if (!variable.getValue().getType().getTypeName().equals("null")) {
				throw new InvalidTypeException("ERROR: O tipo " + variable.getType().getTypeName() + " da variavel "
						+ variable.getIdentifier() + " n�o existem!");
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
				throw new InvalidTypeException("ERROR: O valor para a express�o deveria ser boolean, por�m � do tipo "
						+ e1.getType().getTypeName());
			}
			
			String[] e1_parts = e1.getValue().split(" ");
            if(e1_parts.length > 1) {
                codeGenerator.generateLDCode(findVariableByIdentifier(e1_parts[0]));
                condLabel.push(codeGenerator.getLabels());
                codeGenerator.generateLDCode(new Expression(new Type("int") ,e1_parts[2]));
                switch (Operation.valueOf(e1_parts[1])) {
                    case GT:
                        codeGenerator.generateSUBCode();
                        codeGenerator.generateForCondition("BLEQZ", "forSTRINGCHAVEQUENAOVAIEXISTIRNOUTROCANTOTOP" + nfor);
                        break;
                    case LTEQ:
                        codeGenerator.generateSUBCode();
                        codeGenerator.generateForCondition("BGTZ", "forSTRINGCHAVEQUENAOVAIEXISTIRNOUTROCANTOTOP" + nfor);
                        break;
                    case LT:
                        codeGenerator.generateSUBCode();
                        codeGenerator.generateForCondition("BGEQZ", "forSTRINGCHAVEQUENAOVAIEXISTIRNOUTROCANTOTOP" + nfor);
                        break;
                    case GTEQ:
                        codeGenerator.generateSUBCode();
                        codeGenerator.generateForCondition("BLTZ", "forSTRINGCHAVEQUENAOVAIEXISTIRNOUTROCANTOTOP" + nfor);
                        break;
                    case EQEQ:
                        codeGenerator.generateForCondition("BNEQ", "forSTRINGCHAVEQUENAOVAIEXISTIRNOUTROCANTOTOP" + nfor);
                        break;
                    case NOTEQ:
                        codeGenerator.generateSUBCode();
                        codeGenerator.generateForCondition("BEQ", "forSTRINGCHAVEQUENAOVAIEXISTIRNOUTROCANTOTOP" + nfor);
                        break;
                }
            } else {
                if (e1_parts[0].equals("false")) {
                    codeGenerator.generateBRCode("forSTRINGCHAVEQUENAOVAIEXISTIRNOUTROCANTOTOP" + nfor);
                    condLabel.push(codeGenerator.getLabels());
                } else if (e1_parts[0].equals("true")) {
                    condLabel.push(codeGenerator.getLabels() + 8);
                }
            }
			
			setForExp(false);
		}

		if (e2 != null) {
			if (e2.getType().getTypeName().equals("boolean")) {
				throw new InvalidTypeException(
						"ERROR: A express�o deveria ser aritim�tica, por�m � uma express�o booleana");
			}
		}

		if (var != null) {
			getCurrentScope().getVars().remove(var.getIdentifier());
		}

		scopedEntities.push(f);
	}

	public void dismissCurrentFor(Expression eo)
			throws InvalidFunctionException, InvalidOperationException, InvalidTypeException {
		if (eo != null) {
			String[] eo_parts = eo.getValue().split(" ");
			switch (Operation.valueOf(eo_parts[1])) {
			case PLUSPLUS:
				codeGenerator.generateLDCode(findVariableByIdentifier(eo_parts[0]));
				codeGenerator.generateSUBCode("1");
				codeGenerator.generateSTCode(findVariableByIdentifier(eo_parts[0]));
				break;

			case MINUSMINUS:
				codeGenerator.generateLDCode(findVariableByIdentifier(eo_parts[0]));
				codeGenerator.generateADDCode("1");
				codeGenerator.generateSTCode(findVariableByIdentifier(eo_parts[0]));
				break;

			default:
				Expression le = new Expression(new Type("int"), eo_parts[0]);
				Expression re = new Expression(new Type("int"), eo_parts[2]);
				codeGenerator.generateLDCode(le);
				codeGenerator.generateLDCode(re);
				getExpression(le, Operation.valueOf(eo_parts[1]), re);
				break;
			}

		}
		
		String x = codeGenerator.getAssemblyCode().replace("forSTRINGCHAVEQUENAOVAIEXISTIRNOUTROCANTOTOP"+nfor, ""+(codeGenerator.getLabels()+8));
		nfor--;
        codeGenerator.setAssemblyCode(x);
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
						throw new InvalidFunctionException("ERROR: O método " + methodName + " esperava o tipo "
								+ parameters.get(i).getType().getTypeName() + " mas foi passado o tipo "
								+ argList.get(i).getType().getTypeName());
					}
				}
				return true;
			}
		}

		if (isCorrectParams) {
			throw new InvalidFunctionException(
					"ERROR: O m�todo " + methodName + " possui uma quantidade inv�lida de par�metros!");
		} else {
			throw new InvalidFunctionException("ERROR: O m�todo " + methodName + " n�oo existe!");
		}
	}

	public void checkVariableAttribution(String id, Expression expression)
			throws InvalidVariableException, InvalidTypeException, InvalidFunctionException {
		if (!checkVariableExistence(id)) {
			throw new InvalidVariableException(
					"ERROR: A variavel chamada " + id + " e com valor " + expression.getValue() + " n�o existe!");
		}
		if (!checkValidExistingType(expression.getType())) {
			if (!expression.getType().getTypeName().equals("null")) {
				throw new InvalidTypeException("ERROR: O tipo " + expression.getType().getTypeName()
						+ " atribuido a variavel " + id + " n�o existe!");
			}
		}
		Type identifierType = findVariableByIdentifier(id).getType();
		if (!checkTypeCompatibility(identifierType, expression.getType())) {
			String exceptionMessage = String.format("ERROR: Tipos incompativeis! %s n�o � compativel com %s",
					identifierType, expression.getType());
			throw new InvalidFunctionException(exceptionMessage);
		}
	}

	public void checkVariableAttribution(String id, String function)
			throws InvalidVariableException, InvalidTypeException, InvalidFunctionException {
		if (!checkVariableExistence(id)) {
			throw new InvalidVariableException(
					"ERROR: A variavel chamada " + id + " atribuida a fun��o " + function + " n�o existe!");
		}
		Type identifierType = findVariableByIdentifier(id).getType();

		for (Function f : functions) {
			if (f.getName().equals(function)) {
				if (!checkTypeCompatibility(identifierType, f.getDeclaredReturnType())) {
					String exceptionMessage = String.format("ERROR: Tipos incompativeis! %s n�o � compativel com %s",
							identifierType, f.getDeclaredReturnType());
					throw new InvalidFunctionException(exceptionMessage);
				}

			}
		}
	}

	public Expression getExpression(Expression le, Operation md, Expression re)
			throws InvalidTypeException, InvalidOperationException {
		Register register, r1, r2;

		initCompatibleTypes();
			
		if (re == null || checkTypeCompatibility(le.getType(), re.getType())
				|| checkTypeCompatibility(re.getType(), le.getType())) {

			switch (md) {
			case AND:
				if (!isForExp) {
					r1 = codeGenerator.generateLDCode(new Expression(new Type("boolean"), le.getValue()));
					r2 = codeGenerator.generateLDCode(new Expression(new Type("boolean"), re.getValue()));

					codeGenerator.generateANDCode(r1, r2);
				}
				return new Expression(new Type("boolean"));
			case OR:
				if (!isForExp) {
					r1 = codeGenerator.generateLDCode(new Expression(new Type("boolean"), le.getValue()));
					r2 = codeGenerator.generateLDCode(new Expression(new Type("boolean"), re.getValue()));
					
					codeGenerator.generateORCode(r1, r2);
				}
				
				return new Expression(new Type("boolean"));
	
			case GTEQ:
				if (!isForExp) {
					codeGenerator.generateSUBCode();
					codeGenerator.generateBLTZCode(3);
					register = codeGenerator.generateLDCode(new Expression(new Type("boolean"), "1"));
					codeGenerator.generateBRCode(2);
					codeGenerator.generateLDCode(register, new Expression(new Type("boolean"), "0"));
				}

				return new Expression(new Type("boolean"), le.getValue() + " " + md + " " + re.getValue());
			case EQEQ:
				if (!isForExp) {
					codeGenerator.generateBEQCode(3);
					register = codeGenerator.generateLDCode(new Expression(new Type("boolean"), "0"));
					codeGenerator.generateBRCode(2);
					codeGenerator.generateLDCode(register, new Expression(new Type("boolean"), "1"));
				}
				return new Expression(new Type("boolean"), le.getValue() + " " + md + " " + re.getValue());

			case LTEQ:
				if (!isForExp) {
					codeGenerator.generateSUBCode();
					codeGenerator.generateBGTZCode(3);
					register = codeGenerator.generateLDCode(new Expression(new Type("boolean"), "1"));
					codeGenerator.generateBRCode(2);
					codeGenerator.generateLDCode(register, new Expression(new Type("boolean"), "0"));
				}
				return new Expression(new Type("boolean"), le.getValue() + " " + md + " " + re.getValue());
			case LT:
				if (!isForExp) {
					codeGenerator.generateSUBCode();
					if (le.getContext() == "for") {
						codeGenerator.generateForCondition("BGEQZ",
								"forSTRINGCHAVEQUENAOVAIEXISTIRNOUTROCANTOTOP" + nfor);
					}
					codeGenerator.generateBGEQZCode(3);
					register = codeGenerator.generateLDCode(new Expression(new Type("boolean"), "1"));
					codeGenerator.generateBRCode(2);
					codeGenerator.generateLDCode(register, new Expression(new Type("boolean"), "0"));
				}
				return new Expression(new Type("boolean"), le.getValue() + " " + md + " " + re.getValue());
			case GT:
				if (!isForExp) {
					codeGenerator.generateSUBCode();
					codeGenerator.generateBLEQZCode(3);
					register = codeGenerator.generateLDCode(new Expression(new Type("boolean"), "1"));
					codeGenerator.generateBRCode(2);
					codeGenerator.generateLDCode(register, new Expression(new Type("boolean"), "0"));

				}
				return new Expression(new Type("boolean"), le.getValue() + " " + md + " " + re.getValue());
			case NOTEQ:
				if (!isForExp) {
					codeGenerator.generateBEQCode(3);
					register = codeGenerator.generateLDCode(new Expression(new Type("boolean"), "1"));
					codeGenerator.generateBRCode(2);
					codeGenerator.generateLDCode(register, new Expression(new Type("boolean"), "0"));
				}
				return new Expression(new Type("boolean"), le.getValue() + " " + md + " " + re.getValue());
			case NOT:
				return new Expression(new Type("boolean"));
			case XOREQ:
				return new Expression(new Type("boolean"));
			case XOR:
				return new Expression(new Type("boolean"));
			case OROR:
				return new Expression(new Type("boolean"));
			case ANDAND:
				return new Expression(new Type("boolean"));
			case ANDEQ:
				if (!isForExp) {
					codeGenerator.generateANDCode();
					codeGenerator.generateBEQCode(3);
					register = codeGenerator.generateLDCode(new Expression(new Type("boolean"), "1"));
					codeGenerator.generateBRCode(2);
					codeGenerator.generateLDCode(register, new Expression(new Type("boolean"), "0"));
				}
				return new Expression(new Type("boolean"), le.getValue() + " " + md + " " + re.getValue());
			case OREQ:
				if (!isForExp) {
					codeGenerator.generateORCode();
					codeGenerator.generateBEQCode(3);
					register = codeGenerator.generateLDCode(new Expression(new Type("boolean"), "1"));
					codeGenerator.generateBRCode(2);
					codeGenerator.generateLDCode(register, new Expression(new Type("boolean"), "0"));
				}
				return new Expression(new Type("boolean"), le.getValue() + " " + md + " " + re.getValue());
			case OROREQ:
				return new Expression(new Type("boolean"));
			case MINUS:
				if (!isForExp) {
					codeGenerator.generateSUBCode();
				}
				return new Expression(getMaxType(le.getType(), re.getType()),
						le.getValue() + " " + md + " " + re.getValue());
			case MULT:
				if (!isForExp) {
					codeGenerator.generateMULCode();
				}
				return new Expression(getMaxType(le.getType(), re.getType()),
						le.getValue() + " " + md + " " + re.getValue());
			case MOD:
				if (!isForExp)
					codeGenerator.generateMODCode();
				return new Expression(getMaxType(le.getType(), re.getType()),
						le.getValue() + " " + md + " " + re.getValue());
			case PLUS:
				if (!isForExp) {
					codeGenerator.generateADDCode();
				}
				return new Expression(getMaxType(le.getType(), re.getType()),
						le.getValue() + " " + md + " " + re.getValue());
			case DIV:
				if (!isForExp)
					codeGenerator.generateDIVCode();
				return new Expression(getMaxType(le.getType(), re.getType()),
						le.getValue() + " " + md + " " + re.getValue());
			case DIVEQ:
				return new Expression(getMaxType(le.getType(), re.getType()));
			case PLUSEQ:
				return new Expression(getMaxType(le.getType(), re.getType()));
			case MINUSEQ:
				return new Expression(getMaxType(le.getType(), re.getType()));
			case MULTEQ:
				return new Expression(getMaxType(le.getType(), re.getType()));
			case PLUSPLUS:
				if (!isForExp) {
					codeGenerator.generateADDCode("1");
					codeGenerator.generateSTCode(le);
				}
				return new Expression(le.getType(), le.getValue() + " " + md);
			case MINUSMINUS:
				if (!isForExp) {
					codeGenerator.generateSUBCode("1");
					codeGenerator.generateSTCode(le);

				}
				return new Expression(le.getType(), le.getValue() + " " + md);
			default:
				throw new InvalidOperationException("ERRO: A opera��o '" + md.toString() + "' n�o existe!");

			}
		}

		throw new InvalidTypeException("ERROR: Opera��o formada pela express�o '" + le.getValue() + " " + md + " "
				+ re.getValue() + "' n�o � permitida!");
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
					"ERROR: A express�o n�o � relacional, que � formada pelas subexpress�es de valor " + le.getValue()
							+ " do tipo " + le.getType().getTypeName() + " e de valor " + re.getValue() + " do tipo "
							+ re.getType().getTypeName());
		}
		return true;
	}

	public boolean isNumericExpression(Expression e) throws InvalidOperationException {
		if (!e.isNumeric()) {
			throw new InvalidOperationException(
					"ERROR: A express�o de tipo '" + e.getType() + "' e valor '" + e.getValue() + "' n�o � num�rica");
		}
		return true;
	}

	public boolean isNumericExpression(Expression e1, Expression e2) throws InvalidOperationException {
		if (isStringExpression(e1, e2)) {
			return true;
		} else if (e1 != null && e1.isNumeric()) {
			if (e2 != null && e2.isNumeric()) {
				return true;
			}
		}
		throw new InvalidOperationException("ERROR: A express�o n�o � num�rica ou entre strings,'" + e1.getValue()
				+ "' com tipo '" + e1.getType().getTypeName() + "' e/ou a express�o " + e2.getValue() + " com tipo '"
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
		intCompTypes.add("Integer");

		List<String> booleanCompTypes = new ArrayList<String>();
		booleanCompTypes.add("boolean");

		List<String> stringCompTypes = new ArrayList<String>();
		stringCompTypes.add("int");
		stringCompTypes.add("double");
		stringCompTypes.add("long");
		stringCompTypes.add("float");
		stringCompTypes.add("char");
		stringCompTypes.add("null");
		stringCompTypes.add("boolean");

		compatibleTypes.put("double", doubleCompTypes);
		compatibleTypes.put("float", floatCompTypes);
		compatibleTypes.put("long", longCompTypes);
		compatibleTypes.put("int", intCompTypes);
		compatibleTypes.put("boolean", booleanCompTypes);
		compatibleTypes.put("Integer", intCompTypes);
		compatibleTypes.put("String", stringCompTypes);
	}

	public static boolean isForExp() {
		return isForExp;
	}

	public void setForExp(boolean isForExp) {
		SemanticsImpl.isForExp = isForExp;
	}

	public static CodeGenerator getCodeGenerator() {
		return codeGenerator;
	}
}
