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

/* Linhas em branco */
LineTerminator = \r | \n| \r\n
InputCharacter = [^\r\n]
WhiteSpace     = {LineTerminator} | [ \t\f]
		
/* Comentarios  TODO Fazer ainda */
Comment = {TraditionalComment} | {EndOfLineComment} | {DocumentationComment}
	
/* Literais inteiros TODO ver tipos suportados */
DecIntegerLiteral = 0 | [1-9][0-9]*
LongLiteral    = {DecimalLiteral} [lL]

/* Identificadores */
Identifier = [:jletter:] [:jletterdigit:]*

%state STRING, CHARLITERAL

%%

/* Regras Lexicas */
<YYINITIAL> {
	{Identifier}                   { return symbol(sym.IDENTIFIER, yytext()); }
	{DecIntegerLiteral}            { return symbol(sym.INTEGER_LITERAL, new Integer(yytext())); }
	
	/* Palavras Reservadas */
	"abstract"                      { return symbol(sym.ABSTRACT); }
    "boolean"                       { return symbol(sym.BOOLEAN); }
    "break"                         { return symbol(sym.BREAK); }
    "byte"                          { return symbol(sym.BYTE); }
    "case"                          { return symbol(sym.CASE); }
    "catch"                         { return symbol(sym.CATCH); }
    "char"                          { return symbol(sym.CHAR); }
    "class"                         { return symbol(sym.CLASS); }
    "continue"                      { return symbol(sym.CONTINUE); }
    "default"                       { return symbol(sym.DEFAULT); }
    "double"                        { return symbol(sym.DOUBLE); }
    "do"                            { return symbol(sym.DO); }
    "else"                          { return symbol(sym.ELSE); }
    "extends"                       { return symbol(sym.EXTENDS); }
    "finally"                       { return symbol(sym.FINALLY); }
    "final"                         { return symbol(sym.FINAL); }
    "float"                         { return symbol(sym.FLOAT); }
    "for"                           { return symbol(sym.FOR); }
    "if"                            { return symbol(sym.IF); }
    "implements"                    { return symbol(sym.IMPLEMENTS); }
    "import"                        { return symbol(sym.IMPORT); }
    "instanceof"                    { return symbol(sym.INSTANCEOF); }
    "interface"                     { return symbol(sym.INTERFACE); }
    "int"                           { return symbol(sym.INT); }
    "long"                          { return symbol(sym.LONG); }
    "native"                        { return symbol(sym.NATIVE); }
    "new"                           { return symbol(sym.NEW); }
    "null"                          { return symbol(sym.NULL_LITERAL); }
    "package"                       { return symbol(sym.PACKAGE); }
    "private"                       { return symbol(sym.PRIVATE); }
    "protected"                     { return symbol(sym.PROTECTED); }
    "public"                        { return symbol(sym.PUBLIC); }
    "return"                        { return symbol(sym.RETURN); }
    "short"                         { return symbol(sym.SHORT); }
    "static"                        { return symbol(sym.STATIC); }
    "super"                         { return symbol(sym.SUPER); }
    "switch"                        { return symbol(sym.SWITCH); }
    "synchronized"                  { return symbol(sym.SYNCHRONIZED); }
    "this"                          { return symbol(sym.THIS); }
    "threadsafe"    			    { return symbol(sym.THREADSAFE);}
    "throw"                         { return symbol(sym.THROW); }
    "transient"                     { return symbol(sym.TRANSIENT); }
    "try"                           { return symbol(sym.TRY); }
    "void"                          { return symbol(sym.VOID); }
    "while"                         { return symbol(sym.WHILE); }
	
    /* Literais Booleanos */
	"true" 						   { return symbol(sym.BOOLEAN_LITERAL, new Boolean(true)); }
	"false"                        { return symbol(sym.BOOLEAN_LITERAL, new Boolean(false)); }
	
	/* Separadores */
	"("                             { return symbol(sym.LPAREN); }
    ")"                             { return symbol(sym.RPAREN); }
    "{"                             { return symbol(sym.LBRACE); }
    "}"                             { return symbol(sym.RBRACE); }
    "["                             { return symbol(sym.LBRACK); }
    "]"                             { return symbol(sym.RBRACK); }
    ";"                             { return symbol(sym.SEMICOLON); }
    ","                             { return symbol(sym.COMMA); }
    "."   		  				    { return symbol(sym.DOT); }
	
    /* Atribuição */
    "="							    { return symbol(sym.EQ); }
	
    /* Operadores aritméticos */
    "+" 							{ return symbol(sym.PLUS); }
    "-" 						    { return symbol(sym.MINUS); }
    "*" 							{ return symbol(sym.MULT); }
    "/"							    { return symbol(sym.DIV); }
    "++"							{ return symbol(sym.PLUSPLUS); }
    "+="							{ return symbol(sym.PLUSEQ); }
    "-="							{ return symbol(sym.MINUSEQ); }
    "*="						    { return symbol(sym.MULTEQ);}
    "/="				            { return symbol(sym.DIVEQ); } 
    "--"							{ return symbol(sym.MINUSMINUS); }
    "%"							    { return symbol(sym.MOD); }
    "%="							{ return symbol(sym.MODEQ); }
    "<<"							{ return symbol(sym.LSHIFT); }
    ">>"							{ return symbol(sym.RSHIFT); }
    ">>>"							{ return symbol(sym.URSHIFT); }
	
    /* Operadores */
    ":"                             { return symbol(sym.COLON);}
    "~"                             { return symbol(sym.COMP); }
	
    /* Operadores Lógicos */
    "=="							{ return symbol(sym.EQEQ); }
    ">="							{ return symbol(sym.GTEQ); }
    "<="							{ return symbol(sym.LTEQ); }
    "<"							    { return symbol(sym.LT); }
    ">"							    { return symbol(sym.GT); }
    "||"							{ return symbol(sym.OROR); }
    "||="                           { return symbol(sym.OROREQ); }
    "&&"							{ return symbol(sym.ANDAND); }
    "&"							    { return symbol(sym.AND); }
    "!"							    { return symbol(sym.NOT); }
    "!="							{ return symbol(sym.NOTEQ); }
    "|"							    { return symbol(sym.OR); }
    "&="							{ return symbol(sym.ANDEQ); }
    "|="							{ return symbol(sym.OREQ); }
    "^"						        { return symbol(sym.XOR); }
    "^="                            { return symbol(sym.XOREQ); }
    ">>="							{ return symbol(sym.RSHIFTEQ); }
    "<<="							{ return symbol(sym.LSHIFTEQ); }
    "?"                             { return symbol(sym.QUESTION); }
	
    /* Comentarios */
    {Comment}                      { /* ignore */ }
    
    /* String literal */
    \"                             { yybegin(STRING); string.setLength(0); }

    /* Character literal */
    \'                             { yybegin(CHARLITERAL); }
    
    /* Espaços em branco */
    {WhiteSpace}                   { /* ignore */ }
}

<STRING> {
	\"                                { yybegin(YYINITIAL); return symbol(sym.STRING_LITERAL, string.toString()); }
}

<CHARLITERAL> {
	
}