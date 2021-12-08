# Modifique as variaveis conforme o seu setup.

JAVA=java
JAVAC=javac

# Eu uso ROOT como o diretório raiz para os meus labs.
YEAR=$(shell pwd | grep -o '20..-.')

# Modifique aqui o path do seu antlr, ou só modifique a variavel ANTLR4 para o comando do seu antlr global
ANTLR_PATH=/usr/local/lib/antlr/antlr-4.9.3-complete.jar
CLASS_PATH_OPTION=-cp .:$(ANTLR_PATH)

# Comandos como descritos na página do ANTLR.
ANTLR4=$(JAVA) -jar $(ANTLR_PATH)
GRUN=$(JAVA) $(CLASS_PATH_OPTION) org.antlr.v4.gui.TestRig

# Diretório para aonde vão os arquivos gerados.
GEN_PATH=parser

# Diretório para os casos de teste
DATA=.
IN=$(DATA)/examples
OUT=out

all: antlr javac
	@echo "Done."

# Opção -no-listener foi usada para que o ANTLR não gere alguns arquivos
# desnecessários para o momento. Isto será explicado melhor nos próximos labs.
antlr: GoLexer.g GoParser.g
	$(ANTLR4) -no-listener -o $(GEN_PATH) GoLexer.g GoParser.g

javac:
	cp ./Java/GoParserBase.java ./${GEN_PATH}/GoParserBase.java
	$(JAVAC) $(CLASS_PATH_OPTION) $(GEN_PATH)/*.java

# 'sourceFile' é a regra inicial de GoParser.
run:
	cd $(GEN_PATH) && $(GRUN) Go sourceFile $(FILE)

runall:
	-for FILE in $(IN)/*.go; do \
	 	cd $(GEN_PATH) && \
	 	echo -e "\nRunning $${FILE}" && \
	 	$(GRUN) Go sourceFile ../$${FILE} && \
	 	cd .. ; \
	done;

clean:
	@rm -rf $(GEN_PATH)
