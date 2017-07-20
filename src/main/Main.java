package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Paths;

import compiler.generated.Parser;
import compiler.generated.Scanner;
import java_cup.runtime.Symbol;
import util.Log;

public class Main {

	public static void main(String[] args) {
		Long initialTime = System.currentTimeMillis();
		
		String rootPath = Paths.get("").toAbsolutePath().toString();
		String filePath = "/examples/";
		String sourcecode = rootPath + filePath + "code.txt";
		Log.log(sourcecode);
		try {
			Scanner scanner = new Scanner(new BufferedReader(new FileReader(sourcecode)));
			
			Parser parser = new Parser(scanner);
			Symbol s = null;
			try {
				s = parser.parse();
		    } catch (Exception e) {
		        System.out.println(e.getMessage());
		        return;
		    }
			System.out.println(s);
		} catch (Exception e) {
			Log.logErro(e.getMessage());
			e.printStackTrace();
		}
			
		Long totalTime = System.currentTimeMillis() - initialTime;
		Log.log("Tempo: " + totalTime + " ms");
		
		Log.log("Compilaçãoo efetuada com sucesso.");
	}

}
