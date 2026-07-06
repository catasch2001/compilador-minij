/*Alumna : Luciana Scheid*/
/*package de.jflex.example.standalone;*/
import java_cup.runtime.*;
%%

%public
%class Scanner
%cup
%unicode
%line
%column


%{
  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline+1, yycolumn+1, value);
  }
  private Symbol symbol(int type){
  	return new Symbol(type, yyline+1, yycolumn+1); 	
  } 
%}

whitespace=[\r\n]|[ \t]

%%
/*comentarios*/
"/*" ~"*/" {/*ignore*/}
"//" [^\r\n]* {/*ignore*/}

"System" {whitespace}* "." {whitespace}* "out" {whitespace}* "." {whitespace}* "println" { return symbol(sym.PRINTLN, yytext()); }

/* operators */
"&&" { return symbol(sym.AND , yytext()); } 
"||" { return symbol(sym.OR , yytext()); } 
"==" { return symbol(sym.EQUAL , yytext()); } 
"!=" { return symbol(sym.NOTEQUAL , yytext()); } 
"<" { return symbol(sym.LESSTHAN , yytext()); }  
">" { return symbol(sym.GREATERTHAN , yytext()); } 
"+" { return symbol(sym.PLUS, yytext()); }
"-" { return symbol(sym.MINUS, yytext()); }
"*" { return symbol(sym.TIMES , yytext()); } 
"/" { return symbol(sym.DIV , yytext()); } 
"!" { return symbol(sym.NOT , yytext()); } 
"=" { return symbol(sym.EQ, yytext()); }


/* delimiters */
"(" { return symbol(sym.O_PAREN, yytext()); }
")" { return symbol(sym.C_PAREN, yytext()); }
";" { return symbol(sym.SEMICOLON, yytext()); }
"[" { return symbol(sym.LBRACKET, yytext()); }
"]" { return symbol(sym.RBRACKET, yytext()); }
"," { return symbol(sym.COMMA, yytext()); }
"." { return symbol(sym.DOT, yytext()); }

"public" { return symbol(sym.PUBLIC, yytext()); }
"static" { return symbol(sym.STATIC, yytext()); }
"void" { return symbol(sym.VOID, yytext()); }
"main" { return symbol(sym.MAIN, yytext()); }
"class" { return symbol(sym.CLASS, yytext()); }
"extends" { return symbol(sym.EXTENDS, yytext()); }
"String" { return symbol(sym.STRING , yytext()); }
"return" { return symbol(sym.RETURN , yytext()); }
"int" { return symbol(sym.INT , yytext()); }
"boolean" { return symbol(sym.BOOLEAN , yytext()); }
"if" { return symbol(sym.IF , yytext()); }
"else" { return symbol(sym.ELSE , yytext()); }
"while" { return symbol(sym.WHILE , yytext()); }
"true" { return symbol(sym.TRUE , yytext()); }
"false" { return symbol(sym.FALSE , yytext()); }
"this" { return symbol(sym.THIS , yytext()); }
"new" { return symbol(sym.NEW , yytext()); }
"length" { return symbol(sym.LENGTH , yytext()); }
"null" { return symbol(sym.NULL , yytext()); }


"{" { return symbol(sym.O_CBRACKET, yytext()); }
"}" { return symbol(sym.C_CBRACKET, yytext()); }

/* literales enteros */
[0-9]+                 { return symbol(sym.INTEGER_LITERAL,
                           Integer.parseInt(yytext())); }

/* identifiers */
[a-zA-Z_][a-zA-Z0-9_]* { return symbol(sym.IDENTIFIER, yytext()); }

{whitespace}+ {/*ignore*/}

/* string literals */
\"[^\"]*\"  { return symbol(sym.STRING_LITERAL, yytext()); }

. { System.err.println(
	"\nunexpected character in input: '" + yytext() + "' at line " +
	(yyline+1) + " column " + (yycolumn+1));
  }