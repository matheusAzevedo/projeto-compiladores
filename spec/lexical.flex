package compiler.generated;
import java_cup.*;
import java_cup.runtime.*;
import compiler.core.*;

%%

%public
%class Scanner
/* 
	yyline 
*/
%line
/* 
	yycolumn 
*/
%column
/* 
	Compatibilidade com o parser gerado pelo CUP. 
*/
%cup
%{ 
	StringBuffer string = new StringBuffer();
	
	private Symbol symbol(int type) {
		return new Token(type, yyline, yycolumn);
	}
	
	private Symbol symbol(int type, Object value) {
		return new Token(type, yyline, yycolumn, value);
	}
	
	public String current_lexeme() {
	    int line =  yyline + 1;
	    int column = yycolumn + 1;
	    return " (line: " + line + " , column: "+ column + " , lexeme: '" + yytext() + "')";
	}
	  
	public int current_line() {
	    return yyline + 1;
	}
}%

/*
 	Macros - Essas declaraçoes sao ER que serao usadas posteriormente nas regras lexicas.
 */

%%


<YYINITIAL> {
	
}