Pré-requisitos:
 - Java 11 nas variáveis de ambiente; (sudo apt-get install openjdk-11-jdk)
 - Maven 3.1 nas variáveis de ambiente; (sudo apt install maven)
 - Acesso a internet.

Compilação:
A compilação utiliza da ferramenta maven para definir as dependências do projeto, para executar deve ser utilizar o comando abaixo na raiz do projeto:
 - mvn clean install 


Testes:
Para adicionar novos testes, novos arquivos com extensão .go devem ser adicionados a pasta examples ou subpastas. 
Um arquivo de mesmo nome e extensão com sufixo .errors deve ser adicionado em caso de erros.
 - mvn test