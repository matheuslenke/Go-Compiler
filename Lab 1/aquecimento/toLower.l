%{  /* Reconhece numeros naturais arbitrarios. */
  #include <stdio.h>
%}

%%
[A-Z]       {printf("%c", *yytext + 32);}
<<EOF>> {printf("\n");return 0;}
%%

int main() {
  yylex();
  return 0;
}

/* Definindo esta funcao nao precisa compilar com -lfl */
int yywrap() {
  return 1;
}
