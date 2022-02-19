# Pré-requisitos:
 - Java 11 nas variáveis de ambiente; (sudo apt-get install openjdk-11-jdk)
 - Maven 3.1 nas variáveis de ambiente; (sudo apt install maven)
 - Acesso a internet.

# Arquivos importantes
Todos os arquivos da gramática se encontram na pasta golang. Os casos de teste se encontram dentro da pasta examples, e a pasta Java contem a base do Parser que é necessária para compilação


 # Rodando com o Maven
## Compilação:
A compilação utiliza da ferramenta maven para definir as dependências do projeto, para executar deve ser utilizar o comando abaixo na raiz do projeto:
```sh
 mvn clean install 
```

## Testes:
Para adicionar novos testes, novos arquivos com extensão .go devem ser adicionados a pasta examples ou subpastas. 
Um arquivo de mesmo nome e extensão com sufixo .errors deve ser adicionado em caso de erros.
```sh
 mvn test
```

 ## Rodando com o Makefile

 * Para rodar a aplicação com o makefile e testar um arquivo específico de cada vez, você precisa ter o Antlr instalado. Após isso, modifique a variável `ANTLR_PATH` para o path da instalação do seu antlr.

 ## Comandos
 ```sh
    # Build da aplicação
    make
    
    # Para rodar um arquivo só
    make run FILE=caminho_para_arquivo.go

    # Para rodar todos os casos de testes especificados dentro da pasta /examples
    make runall

    # Limpar os arquivos gerados
    make clean
 ```


# Front End 
## Analisador Léxico e Sintático
Análise léxica e sintática feita utilizando de gramáticas disponíveis pela ferramenta ANTLR no [Github](https://github.com/antlr/grammars-v4/tree/master/golang) do projeto.

## Analisador Semântico e AST

- Operações aritméticas e de comparação básicas (+, ∗, <, ==, etc, etc).

- Comandos de atribuição: Apenas comandos de atribuição simples (=): ```identiferList = expressionList```

- Execução de blocos sequenciais de código.

- Pelo menos uma estrutura de escolha (if-then-else) e uma de repetição (while, for, etc): A estrutura de repetição é apenas o while comum, que no Go é usado com a palavra for também

- Declaração de variáveis apenas de forma explícita no formato: ```var identiferList type = expressionList```

- Tipos básicos como int, real, string e bool (quando aplicável à LP): Diferentes tipos de int (uint8, uint16, uint32, etc...) e float (float32 e float64) reduzidos apenas para o tipo int e float. 

- Pelo menos um tipo composto (vetores, listas em Python, etc): Utilizado apenas arrays

- Declaração e execução correta de chamadas de função com número de parâmetros fixos (não precisa ser varargs).

- Sistema de tipos que trata adequadamente todos os tipos permitidos.

- Operaçõs de IO básicas sobre stdin e stdout para permitir testes.


