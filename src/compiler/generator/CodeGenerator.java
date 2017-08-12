package compiler.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import compiler.analysis.SemanticsImpl;
import compiler.core.Expression;
import compiler.core.Function;
import compiler.core.Register;
import compiler.core.Type;
import compiler.core.Variable;
import util.Log;

public class CodeGenerator {
	private int labels;
	private int register;
	private int lastRegisterUsed;
	private String assemblyCode;
	private Register[] registers;
	private Map<String, Integer> functionAddres;

	public CodeGenerator() {
		this.labels = 100;
		this.register = -1;
		this.lastRegisterUsed = 0;
		this.registers = Register.values();
		this.assemblyCode = initAssemblyCode();
		this.functionAddres = new HashMap<String, Integer>();
	}

	private String initAssemblyCode() {
		return "100: LD SP, #4000" + System.getProperty("line.separator");
	}

	public void addCode(String assemblyString) {
		assemblyCode = assemblyCode.concat(assemblyString + System.getProperty("line.separator"));
		Log.log("############################################### \n");
		Log.log(assemblyCode);
		Log.log("############################################### \n");
	}

	public void assignmentDeclaration(Variable var, Object obj) {
		if (obj instanceof Expression) {
			generateSTCode(var);
		}
		if (obj instanceof Function) {
			Function f = (Function) obj;
			generateLDCode(new Expression(f.getName()));
			generateSTCode(var);
		}
	}

	public void generateADDCode() {
		incrementLabel();
		Register one = registers[register - 1];
		Register two = allocateRegister();

		register++;
		Register result = allocateRegister();
		addCode(labels + ": ADD " + result + ", " + one + ", " + two);
	}

	public void generateADDCode(String cons) {
		incrementLabel();
		Register one = registers[register];
		register++;
		Register result = allocateRegister();
		addCode(labels + ": ADD " + result + ", " + one + ", #" + cons);
	}

	public void generateADDCode(Register result, Register one, String cons) {
		incrementLabel();
		addCode(labels + ": ADD " + result + ", " + one + ", " + cons);
	}

	public void generateADDCode(Register result, Register one, Expression exp) {
		incrementLabel();
		addCode(labels + ": ADD " + result + ", " + one + ", #" + exp.getAssemblyValue());
	}

	public void generateSUBCode() {
		incrementLabel();
		Register one = registers[register - 1];
		Register two = allocateRegister();

		register++;
		Register result = allocateRegister();
		addCode(labels + ": SUB " + result + ", " + one + ", " + two);
	}

	public void generateSUBCode(Register result, Register one, Expression exp) {
		incrementLabel();
		addCode(labels + ": SUB " + result + ", " + one + ", #" + exp.getAssemblyValue());
	}

	public void generateSUBCode(String cons) {
		incrementLabel();
		Register one = registers[register];
		register++;
		Register result = allocateRegister();
		addCode(labels + ": SUB " + result + ", " + one + ", #" + cons);
	}

	public void generateSUBCode(Register result, Register one, String cons) {
		incrementLabel();
		addCode(labels + ": SUB " + result + ", " + one + ", " + cons);
	}

	public void generateMULCode() {
		incrementLabel();

		Register one = registers[register - 1];
		Register two = allocateRegister();

		register++;
		Register result = allocateRegister();
		addCode(labels + ": MUL " + result + ", " + one + ", " + two);
	}

	public void generateDIVCode() {
		incrementLabel();

		Register one = registers[register - 1];
		Register two = allocateRegister();

		register++;
		Register result = allocateRegister();
		addCode(labels + ": DIV " + result + ", " + one + ", " + two);
	}

	public void generateMULCode(Register result, Register one, Expression exp) {
		incrementLabel();
		addCode(labels + ": MUL " + result + ", " + one + ", #" + exp.getValue());
	}

	public Register generateLDCode(Expression expression) {
		Register r = null;
		if ((expression.getAssemblyValue() != null) && (expression.getValue() != null)) {
			register++;
			incrementLabel();
			r = allocateRegister();
			addCode(labels + ": LD " + r + ", #" + expression.getAssemblyValue());
		}
		return r;
	}

	public void generateBREAKcode() {
		labels += 8;
		addCode(labels + ": BR " +"forSTRINGCHAVEQUENAOVAIEXISTIRNOUTROCANTOTOP"+ SemanticsImpl.getInstance().nfor);
	}

	public void generateBEQCode(int br) {
		labels += 8;
		Register r1 = registers[register-1];
		Register r2 = allocateRegister();

		int jump = (br * 8) + labels;

		addCode(labels + ": BEQ " + r1 + ", " + r2 + ", " + jump);
	}

	public void generateBNEQZCode(int br) {
		labels += 8;
		int jump = (br * 8) + labels;

		Register current = allocateRegister();
		addCode(labels + ": BNEQZ " + current + ", " + jump);
	}

	public void generateForCondition(String op, String jump){
		labels += 8;
		Register current = allocateRegister();
		addCode(labels + ": " + op + " " + current + ", " + jump);
	}

	public void generateBGEQZCode(int br) {
		labels += 8;
		int jump = (br * 8) + labels;

		Register current = allocateRegister();
		addCode(labels + ": BGEQZ " + current + ", " + jump);
	}

	public void generateBLEQZCode(int br) {
		labels += 8;
		int jump = (br * 8) + labels;

		Register current = allocateRegister();
		addCode(labels + ": BLEQZ " + current + ", " + jump);
	}

	public void generateBLTZCode(int br) {
		labels += 8;
		int jump = (br * 8) + labels;

		Register current = allocateRegister();
		addCode(labels + ": BLTZ " + current + ", " + jump);
	}

	public void generateBGTZCode(int br) {
		labels += 8;
		int jump = (br * 8) + labels;

		Register current = allocateRegister();
		addCode(labels + ": BGTZ " + current + ", " + jump);
	}

	public void generateBRCode(int br) {
		labels += 8;
		int jump = (br * 8) + labels;
		addCode(labels + ": BR " + jump);
	}

	public Register generateLDCode(Variable var) {

		Register r = null;
		if (var.getIdentifier() != null) {
			register++;
			incrementLabel();
			r = allocateRegister();
			addCode(labels + ": LD " + r + ", " + var.getIdentifier());
		}
		return r;
	}

	public Register generateLDCode(Register r, Expression expression) {
		if ((expression.getAssemblyValue() != null) && (expression.getValue() != null)) {
			incrementLabel();
			addCode(labels + ": LD " + r + ", #" + expression.getAssemblyValue());
		}
		return r;
	}

	public void generateSTCode(Variable variable) {
		incrementLabel();
		addCode(labels + ": ST " + variable.getIdentifier() + ", " + allocateRegister());
		this.register = this.lastRegisterUsed;
	}

	public void generateSTCode(Register one, Expression exp) {
		incrementLabel();
		addCode(labels + ": ST " + one + ", " + exp.getAssemblyValue());
		this.register = this.lastRegisterUsed;
	}

	public void generateSTCode(Expression exp) {
		incrementLabel();
		addCode(labels + ": ST " + exp.getAssemblyValue() + ", " + allocateRegister());
		this.register = this.lastRegisterUsed;
	}

	public void generateCallFunction(String functionName) {
		Expression blockSize = new Expression("size");
		Integer addressFunction = functionAddres.get(functionName);

		generateADDCode(Register.SP, Register.SP, blockSize);

		int jump = (3 * 8) + labels;
		generateSTCode(Register._SP, new Expression(new Type("int"), Integer.toString(jump)));
		generateBRCode(addressFunction);
		generateSUBCode(Register._SP, Register.SP, blockSize);
	}

	public void generateBRCode(Integer address) {
		incrementLabel();
		addCode(labels + ": BR " + address);
	}

	public void generateBRCode(Register register) {
		incrementLabel();
		addCode(labels + ": BR " + register);
	}

    public void generateBRCode(String s) {
    	incrementLabel();
        addCode(labels + ": BR " + s);
    }
	
	public void generateMODCode() {
        incrementLabel();
        Register one = registers[register - 1];
        Register two = allocateRegister();

        register++;
        Register result = allocateRegister();
        addCode(labels + ": MOD " + result + ", " + one + ", " + two);
    }
	
	public void generateHalt() {
		incrementLabel();
		addCode(labels + ": halt");
	}

	public String getAssemblyCode() {
		return assemblyCode;
	}

	public void setAssemblyCode(String assemblyCode) {
		this.assemblyCode = assemblyCode;
	}

	public void addFunctionAddress(String name) {
		labels = (labels +100)/100 * 100 - 8;
		functionAddres.put(name.trim(), labels + 8);
		addCode(System.getProperty("line.separator"));
	}

	public void addBRSP(String funcname) {
		if(funcname.equals("main")){
			generateHalt();
		}else{
			incrementLabel();
			addCode(labels + ": BR *0(SP)");
		}

	}

	public void generateCodeFunctionCall(String name){

		Integer addressfuction = functionAddres.get(name);

		String assa = SemanticsImpl.getInstance().getCurrentScope().getName();
		generateADDCode(Register.SP, Register.SP, "#" + assa + "size");
		generateSTCode(Register.SP0, new Expression(new Type("int"), "#"+(labels+24)));
		generateBRCode(addressfuction);
		generateSUBCode(Register.SP, Register.SP, "#" + assa + "size");
	}

	public void generateCodeFunctionCall(String name, ArrayList<Expression> args){
		String key = name + " ";
		for (Expression e: args
				) {
			key += e.getType().getTypeName();
		}
		Integer addressfuction = functionAddres.get(key);

		String assa = SemanticsImpl.getInstance().getCurrentScope().getName();
		generateADDCode(Register.SP, Register.SP, "#" + assa + "size");
		generateSTCode(Register.SP0, new Expression(new Type("int"), "#" + (labels+24)));

		generateBRCode(addressfuction);
		generateSUBCode(Register.SP, Register.SP, "#" + assa + "size");
	}

	public void StorageReturnedType(Function function, Expression returnedExpression) {
		if (returnedExpression.getValue() != null) {
			generateLDCode(returnedExpression);
			generateSTCode(new Expression(function.getName()));

			if(function.getName().equals("main")){
				generateHalt();
			} else {
				generateBRCode(Register._SP);
			}
		} else {
			if (returnedExpression.getValue() != null) {
				generateLDCode(returnedExpression);
				generateSTCode(new Expression(function.getName()));
			} else {
				generateSTCode(new Expression(function.getName()));
			}
			generateBRCode(Register._SP);
		}
	}

	public Register allocateRegister(){
		try {
			Register allocated = registers[register];
			return allocated;
		} catch (Exception e) {
			register++;
			return allocateRegister();
		}
	}
	
	public void lastRegisterUsed(int register) {
		this.lastRegisterUsed = register;
	}

	private int incrementLabel() {
		labels += 8;
		return labels;
	}

	public void generateFinalAssemblyCode() throws IOException {
		String rootPath = Paths.get("").toAbsolutePath().toString();
		String filePath = "/code/";
		String generatedcode = rootPath + filePath + "generated-assembly.txt";

		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(generatedcode)));

		writer.write(assemblyCode);
		writer.close();
	}
}
