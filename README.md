# Pré-requisitos:
 - Java 11 nas variáveis de ambiente; (sudo apt-get install openjdk-11-jdk)
 - Maven 3.1 nas variáveis de ambiente; (sudo apt install maven)
 - Acesso a internet.

# Arquivos importantes
Todos os arquivos da gramática se encontram na pasta golang. Os casos de teste se encontram dentro da pasta examples, e a pasta Java contem a base do Parser que é necessária para compilação

# Compilação:
A compilação utiliza da ferramenta maven para definir as dependências do projeto, para executar deve ser utilizar o comando abaixo na raiz do projeto:
```sh
 mvn clean install 
```

# Testes:
Para adicionar novos testes, novos arquivos com extensão .go devem ser adicionados a pasta examples ou subpastas. 
Um arquivo de mesmo nome e extensão com sufixo .errors deve ser adicionado em caso de erros.
```sh
 mvn test
```

 # Rodando com o Makefile

 * Para rodar a aplicação com o makefile e testar um arquivo específico de cada vez, você precisa ter o Antlr instalado. Após isso, modifique a variável `ANTLR_PATH` para o path da instalação do seu antlr.

 ## Comandos
 ```sh
    # Build da aplicação
    make
_
    # Para rodar um arquivo só
    make run FILE=caminho_para_arquivo.go

    # Para rodar todos os casos de testes especificados dentro da pasta /examples
    make runall

    # Limpar os arquivos gerados
    make clean
 ```

