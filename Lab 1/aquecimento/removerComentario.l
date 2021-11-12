%{  /* Reconhece numeros naturais arbitrarios. */
  #include <stdio.h>
%}

%%
#.*\n        {}
^\n {}
.*\n      {printf("%s", yytext);}
%%

int main() {
  yylex();
  return 0;
}

/* Definindo esta funcao nao precisa compilar com -lfl */
int yywrap() {
  return 1;
}
