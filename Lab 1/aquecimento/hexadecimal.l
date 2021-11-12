%{  /* Reconhece numeros naturais arbitrarios. */
  #include <stdio.h>
%}

%%
^(0x)[0-9A-Fa-f]{0,8}      {ECHO; printf(" -> é HexaDecimal\n");}
.*     {ECHO; printf(" -> não é HexaDecimal\n");}
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
