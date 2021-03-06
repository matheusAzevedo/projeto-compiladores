package compiler.generated;
import java_cup.runtime.*;
import compiler.core.*;

%%

%public
%class Scanner
%unicode
%line
%column
%cup
%cupdebug

%{
   StringBuffer string = new StringBuffer();

  private Symbol symbol(int type) {
		return new Token(type, yyline, yycolumn);
	}
	
  private Symbol symbol(int type, Object value) {
		return new Token(type, yyline, yycolumn, value);
  }
  
  public String current_lexeme() {
    int l = yyline + 1;
    int c = yycolumn + 1;
    return " (line: " + l + " , column: " + c + " , lexeme: '" + yytext() + "')";
  }
  
  public int current_line() {
    return yyline + 1;
  }
  
  private long parseLong(int start, int end, int radix) {
	long result = 0;
	long digit;

	for (int i = start; i < end; i++) {
	  digit  = Character.digit(yycharat(i),radix);
	  result*= radix;
	  result+= digit;
	}

	return result;
  }
  
%}

/* White spaces*/
LineTerminator = \r|\n|\r\n
WhiteSpace = {LineTerminator} | [ \t\f]

/* Integer literals */
DecIntegerLiteral = 0 | [1-9][0-9]*
DecLongLiteral    = {DecIntegerLiteral} [lL]

HexIntegerLiteral = 0 [xX] 0* {HexDigit} {1,8}
HexLongLiteral    = 0 [xX] 0* {HexDigit} {1,16} [lL]
HexDigit          = [0-9a-fA-F]

OctIntegerLiteral = 0+ [1-3]? {OctDigit} {1,15}
OctLongLiteral    = 0+ 1? {OctDigit} {1,21} [lL]
OctDigit          = [0-7]
    
/* floating point literals */        
FloatLiteral  = ({FLit1}|{FLit2}|{FLit3}) {Exponent}? [fF]
DoubleLiteral = ({FLit1}|{FLit2}|{FLit3}) {Exponent}?

FLit1    = [0-9]+ \. [0-9]* 
FLit2    = \. [0-9]+ 
FLit3    = [0-9]+ 
Exponent = [eE] [+-]? [0-9]+


/* Identifiers */
Identifier = [:jletter:][:jletterdigit:]*

/* String and Character literals */
StringCharacter = [^\r\n\"\\]
SingleCharacter = [^\r\n\'\\]

%state STRING, CHARLITERAL


/* Comments */
Comment = "/**" ( [^*] | \*+ [^/*] )* "*"+ "/"

%%

<YYINITIAL> {	 
	/* Literais Booleanos */
	"true" 						   { return symbol(sym.BOOLEAN_LITERAL, new Boolean(true)); }
	"false"                        { return symbol(sym.BOOLEAN_LITERAL, new Boolean(false)); }
		
	
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
	
    /* Atribuicaoo */
    "="							    { return symbol(sym.EQ); }
	
    /* Operadores aritmeticos */
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
	
    /* Operadores Logicos */
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
	
    {Comment}                      { /* ignore */ }
    
    \"                             { yybegin(STRING); string.setLength(0); }

    \'                             { yybegin(CHARLITERAL); }
    
    {WhiteSpace}                   { /* ignore */ }
    {Identifier}                   { return symbol(sym.IDENTIFIER, yytext()); }
    {DecIntegerLiteral}            { return symbol(sym.INTEGER_LITERAL, new Integer(yytext())); }
    {DecLongLiteral}               { return symbol(sym.INTEGER_LITERAL, new Long(yytext().substring(0,yylength()-1))); }
  
  	{HexIntegerLiteral}            { return symbol(sym.INTEGER_LITERAL, new Integer((int) parseLong(2, yylength(), 16))); }
  	{HexLongLiteral}               { return symbol(sym.INTEGER_LITERAL, new Long(parseLong(2, yylength()-1, 16))); }
 
  	{OctIntegerLiteral}            { return symbol(sym.INTEGER_LITERAL, new Integer((int) parseLong(0, yylength(), 8))); }  
  	{OctLongLiteral}               { return symbol(sym.INTEGER_LITERAL, new Long(parseLong(0, yylength()-1, 8))); }
	  
  	{FloatLiteral}                 { return symbol(sym.FLOATING_POINT_LITERAL, new Float(yytext().substring(0,yylength()-1))); }
  	{DoubleLiteral}                { return symbol(sym.FLOATING_POINT_LITERAL, new Double(yytext())); }
  	{DoubleLiteral}[dD]            { return symbol(sym.FLOATING_POINT_LITERAL, new Double(yytext().substring(0,yylength()-1))); }
}

 <STRING> {
  \"                                { yybegin(YYINITIAL); return symbol(sym.STRING_LITERAL, string.toString()); }

  {StringCharacter}+                { string.append( yytext() ); }

  "\\b"                             { string.append( '\b' ); }
  "\\t"                             { string.append( '\t' ); }
  "\\n"                             { string.append( '\n' ); }
  "\\f"                             { string.append( '\f' ); }
  "\\r"                             { string.append( '\r' ); }
  "\\\""                            { string.append( '\"' ); }
  "\\'"                             { string.append( '\'' ); }
  "\\\\"                            { string.append( '\\' ); }
  
  \\.                               { throw new RuntimeException("Illegal escape sequence \""+yytext()+"\""); }
  {LineTerminator}                  { throw new RuntimeException("Unterminated string at end of line"); }

}

<CHARLITERAL> {
  {SingleCharacter}\'               { yybegin(YYINITIAL); return symbol(sym.CHARACTER_LITERAL, new Character(yytext().charAt(0))); }
  
  "\\b"\'                           { yybegin(YYINITIAL); return symbol(sym.CHARACTER_LITERAL, new Character('\b'));}
  "\\t"\'                           { yybegin(YYINITIAL); return symbol(sym.CHARACTER_LITERAL, new Character('\t'));}
  "\\n"\'                           { yybegin(YYINITIAL); return symbol(sym.CHARACTER_LITERAL, new Character('\n'));}
  "\\f"\'                           { yybegin(YYINITIAL); return symbol(sym.CHARACTER_LITERAL, new Character('\f'));}
  "\\r"\'                           { yybegin(YYINITIAL); return symbol(sym.CHARACTER_LITERAL, new Character('\r'));}
  "\\\""\'                          { yybegin(YYINITIAL); return symbol(sym.CHARACTER_LITERAL, new Character('\"'));}
  "\\'"\'                           { yybegin(YYINITIAL); return symbol(sym.CHARACTER_LITERAL, new Character('\''));}
  "\\\\"\'                          { yybegin(YYINITIAL); return symbol(sym.CHARACTER_LITERAL, new Character('\\')); }
  
  \\.                               { throw new RuntimeException("Illegal escape sequence \"" + yytext() + "\""); }
  {LineTerminator}                  { throw new RuntimeException("Unterminated character literal at end of line"); }

}
/* Erro de retorno */
[^]                              { throw new RuntimeException("Illegal character \""+ yytext() +
                                                              "\" at line "+ yyline +", column "+ yycolumn); }
<<EOF>>                          { return symbol(sym.EOF); }