package br.ufes.edu.compiladores;

import java.io.IOException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import br.ufes.edu.compiladores.checker.SemanticChecker;

/*
  Programa principal para funcionamento de compilador.
  
  Esta função espera um único argumento: o nome do
  programa a ser compilado. Em um código real certamente
  deveria haver alguma verificação de erro mas ela foi
  omitida aqui para simplificar o código e facilitar a leitura.
*/

public class GoCompiler {
    private static Logger logger = LogManager.getLogger(GoCompiler.class);

    public static void main(String[] args) throws IOException {
        // Cria um CharStream que lê os caracteres de um arquivo.
        CharStream input = CharStreams
                .fromFileName("D:\\Projetos\\Compiladores\\Go-Compiler\\src\\test\\resources\\examples\\varDecl.go");

        // Cria um lexer que consome a entrada do CharStream.
        GoLexer lexer = new GoLexer(input);

        // Cria um buffer de tokens vindos do lexer.
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // Cria um parser que consome os tokens do buffer.
        GoParser parser = new GoParser(tokens);

        // Começa o processo de parsing na regra 'sourceFile'.
        ParseTree tree = parser.sourceFile();

        if (parser.getNumberOfSyntaxErrors() != 0) {
            // Houve algum erro sintático. Termina a compilação aqui.
            return;
        }

        // Cria o analisador semântico e visita a ParseTree para
        // fazer a análise.
        SemanticChecker checker = new SemanticChecker();
        checker.visit(tree);

        logger.info("PARSE SUCCESSFUL");
        checker.printTables();

    }
}
