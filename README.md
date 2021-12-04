Pré-requisitos:
 - Java 11 nas variáveis de ambiente;
 - Maven 3.1 nas variáveis de ambiente;
 - Acesso a internet.

Compilação:
A compilação utiliza da ferramenta maven para definir as dependências do projeto, para executar deve ser utilizar o comando abaixo na raiz do projeto
 - mvn clean install 

Testes:
Para adicionar novos testes, um arquivo de mesmo nome e extensão com sufixo .errors deve ser adicionado em caso de erros.
Todos os arquivos de teste devem estar dentro da pasta examples ou suas subpastas.
 - mvn test