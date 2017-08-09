package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;

import compiler.generated.Parser;
import compiler.generated.Scanner;
import util.Log;

public class Main {
	public static Scanner scanner = null;
	
	public static void main(String[] args) {
		Long initialTime = System.currentTimeMillis();
	
		String rootPath = Paths.get("").toAbsolutePath().toString();
		String filePath = "/examples/";
		String sourcecode = rootPath + filePath + "code.txt";

		try {
			File file = new File(sourcecode);
			InputStream is = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			
			scanner = new Scanner(br);
			
			@SuppressWarnings("deprecation")
			Parser parser = new Parser(scanner);
			parser.parse();
		
			if (Parser.errors == 0) {
				Long totalTime = System.currentTimeMillis() - initialTime;
				Log.log("Tempo: " + totalTime + " ms");
				
				Log.log("Compila��oo efetuada com sucesso.");
			}
		} catch (Exception e) {
			Log.logErro(e.getMessage());
			e.printStackTrace();
		}
	}

}
