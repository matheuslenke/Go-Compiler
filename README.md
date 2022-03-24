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
O projeto será automaticamente compilado, mas caso deseje executar novamente apenas a compilação, é possível rodar o comando:

```sh
mvn compile
```

## Testes:

### Para análise sintática
> ⚠️ Atenção! Para a análise semântica, alguns casos de testes não funcionam mais devido às simplificações realizadas na linguagem. Os casos anteriores estão aqui apenas para referência. Além disso, os casos de testes para análise sintática não aceitam arquivos com o sufixo .errors


Para adicionar novos testes, novos arquivos com extensão .go devem ser adicionados a pasta examples ou subpastas. 

Duas formas são possíveis para se testar. Caso queira apenas validar se todos os casos de testes ainda estão todos funcionando, sem visualizar a AST gerada, basta rodar:

```sh
 mvn test
```

Para testar e visualizar o output de cada um dos casos de teste, tanto no terminal quanto a AST gerada, basta rodar o script de testes:

```sh
./runall.sh
```

## Testando somente um arquivo
Para testar somente um arquivo, é necessário utilizar o `exec:java` do Maven, passando o caminho para o arquivo como argumento, da seguinte forma:

```sh
   mvn compile exec:java -Dexec.args="caminho/para/arquivo.go"
```



# Features do Frontend do compilador
## Analisador Léxico e Sintático
Análise léxica e sintática feita utilizando de gramáticas disponíveis pela ferramenta ANTLR no [Github](https://github.com/antlr/grammars-v4/tree/master/golang) do projeto.

## Analisador Semântico e AST

- Operações aritméticas e de comparação básicas (+, ∗, <, ==, etc, etc).

- Comandos de atribuição: Apenas comandos de atribuição simples (=): ```identiferList = expressionList``` ou de declaração e atribuição ao mesmo tempo como citado abaixo.

- Declaração de variáveis apenas de forma explícita no formato: ```var identiferList type = expressionList``` para todos os casos, incluindo arrays e retorno de funções.

- Declaração de variáveis de forma implícita apenas para os timos mais básicos, do formato: ```identifierList := expressionList```

- Execução de blocos sequenciais de código.

- Pelo menos uma estrutura de escolha (if-then-else) e uma de repetição (while, for, etc): A estrutura de repetição é apenas o while comum, que no Go é usado com a palavra for também

- Tipos básicos como int, real, string e bool (quando aplicável à LP): Diferentes tipos de int (uint8, uint16, uint32, etc...) e float (float32 e float64) reduzidos apenas para o tipo int e float, apesar de manter os tokens float32 e float64. Os tipos básicos são: int, float32, float64, bool, string.

- Pelo menos um tipo composto (vetores, listas em Python, etc): Utilizado apenas arrays

- Declaração e execução correta de chamadas de função com número de parâmetros fixos (não precisa ser varargs), mas sem permitir retornos múltiplos

- Sistema de tipos que trata adequadamente todos os tipos permitidos. Em go, praticamente só é aceito typecasting explícito, e não foi feito o mesmo

- Operaçõs de IO básicas sobre stdin e stdout para permitir testes.

## Geração de código

- Geração de código MIPS não funciona com o script runall.sh

- Diversas simplificações realizadas, alguns exemplos possíveis se encontram na pasta `generatedCodeExamples`