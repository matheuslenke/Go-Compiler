%{  /* Reconhece numeros naturais arbitrarios. */
  #include <stdio.h>
%}

%%
[A-Z]{3}\-[0-9]{4}      {ECHO; printf(" -> é placa\n");}
.*     {ECHO; printf(" -> não é placa\n");}
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
