Compilador de Java para Assembly.

Escopo G
	• Procedures/Funções
	• Polimorfismo e sobrecarga
	• Expressões booleanas, literais (inteiros, string, booleanos)
	• Comandos iterativos: for

Gramatica Java sugerida para o projeto - http://cui.unige.ch/isi/bnf/JAVA/BNFindex.html
CUP - http://www2.cs.tum.edu/projects/cup/
Plugin eclipse - http://www2.in.tum.de/projects/cup/eclipse

Gerar o Analisador Lexico

java -jar lib/jflex-1.6.1.jar --noinputstreamctor -d ./src/compiler/generated ./spec/lexical.flex

Gerar o Analisador semantico

java -jar lib/java-cup-11b.jar -compact_red -expect 10000 -package compiler.generated -destdir ./src/compiler/generated -parser Parser ./spec/parser.cup