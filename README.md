# Compilador de Java para Assembly.
Escopo do Projeto.

  - Procedures/Funções
  - Polimorfismo e Sobrecarga
  - Expressões booleanas, literais (inteiros, string, booleanos)
  - Comandos iterativos: **for**

Gramática Java sugerida para o projeto - [link](http://cui.unige.ch/isi/bnf/JAVA/BNFindex.html)

Ferramentas para criar o compilador
 - [CUP](http://www2.cs.tum.edu/projects/cup/)
 - [Jflex](http://jflex.de/)

Plugin do Eclipse [link](http://www2.in.tum.de/projects/cup/eclipse)

# Gerar Analisador Léxico

```sh
$ java -jar lib/jflex-1.6.1.jar --noinputstreamctor -d ./src/compiler/generated ./spec/lexical.flex
```

# Gerar Analisador semântico

```sh
$ java -jar lib/java-cup-11b.jar -compact_red -expect 10000 -package compiler.generated -destdir ./src/compiler/generated -parser Parser ./spec/parser.cup
```
