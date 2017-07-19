package main;

import java.io.BufferedReader;
import java.io.FileReader;
import compiler.generated.Scanner;
import util.Log;

public class Main {

	public static void main(String[] args) {
		String filePath = "examples/code.txt";
		try {
			Scanner scanner = new Scanner(new BufferedReader(new FileReader(filePath)));
		} catch (Exception e) {
			Log.logErro(e.getMessage());
			e.printStackTrace();
		}
			
		Log.log("Compilação efetuada com sucesso.");
	}

}
