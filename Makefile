# Modifique as variaveis conforme o seu setup.

JAVA=java
JAVAC=javac



# Eu uso ROOT_JAVA como o diretório raiz para os meus labs.
ROOT=$(shell pwd)

# Modifique aqui o path do seu antlr, ou só modifique a variavel ANTLR4 para o comando do seu antlr global
ANTLR_PATH=/usr/local/lib/antlr-4.9-complete.jar
CLASS_PATH_OPTION=-cp .:$(ANTLR_PATH) 

# Comandos como descritos na página do ANTLR.
ANTLR4=$(JAVA) -jar $(ANTLR_PATH)
GRUN=$(JAVA) $(CLASS_PATH_OPTION) org.antlr.v4.gui.TestRig

# Diretório para aonde vão os arquivos gerados.
ROOT_JAVA=$(ROOT)/src/main/java/br/ufes/edu/compiladores
ROOT_ANTLR=$(ROOT)/src/main/antlr4/br/ufes/edu/compiladores

# Diretório para os arquivos .class
BIN_PATH=$(ROOT)/bin
GEN_PATH=$(BIN_PATH)/generated

# Diretório para os casos de teste
DATA=$(ROOT)/src/test/resources
IN=$(DATA)/examples
OUT=out

all: clean antlr javac
	@echo "Done."

# Opção -no-listener foi usada para que o ANTLR não gere alguns arquivos
# desnecessários para o momento. Isto será explicado melhor nos próximos labs.
antlr:
	@echo $(ROOT)
	$(ANTLR4) -no-listener -visitor -o $(GEN_PATH) $(ROOT_ANTLR)/GoLexer.g4 $(ROOT_ANTLR)/GoParser.g4

javac:	
	@mkdir -p $(BIN_PATH)
	$(JAVAC) $(CLASS_PATH_OPTION) -d $(BIN_PATH) */*.java


run:
	$(JAVA) $(CLASS_PATH_OPTION):$(BIN_PATH) $(MAIN_PATH)/GoCompiler $(FILE)

runall:
	-for FILE in $(IN)/*.go; do \
	 	echo -e "\nRunning $${FILE}" && \
	 	$(JAVA) $(CLASS_PATH_OPTION):$(BIN_PATH) $(MAIN_PATH)/GoCompiler $${FILE}; \
	done;

clean:
	@rm -rf $(BIN_PATH)
