package compiler.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import compiler.core.Expression;
import compiler.core.Function;
import compiler.core.Register;
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
	
	public void generateFinalAssemblyCode() throws IOException {
		String rootPath = Paths.get("").toAbsolutePath().toString();
		String filePath = "/code/";
		String generatedcode = rootPath + filePath + "generated-assembly.txt";
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(generatedcode)));
        
        writer.write(assemblyCode);
        writer.close();
    }
}
