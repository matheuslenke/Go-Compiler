package br.ufes.edu.compiladores;

import java.io.IOException;
import java.util.Arrays;

import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import br.ufes.edu.compiladores.checker.SemanticChecker;
import br.ufes.edu.compiladores.code.CodeGen;

/*
  Programa principal para funcionamento de compilador.
  
  Esta função espera um único argumento: o nome do
  programa a ser compilado. Em um código real certamente
  deveria haver alguma verificação de erro mas ela foi
  omitida aqui para simplificar o código e facilitar a leitura.
*/

public class GoCompiler {

  public static void main(String[] args) throws IOException {

    // Cria um CharStream que lê os caracteres de um arquivo.
    CharStream input = CharStreams
        .fromFileName(args[0]);

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

    System.out.println("PARSE SUCCESSFUL");
    // checker.printTables();
    // checker.printAST();

    // TreeViewer viewr = new TreeViewer(Arrays.asList(
    // parser.getRuleNames()), tree);
    // viewr.open();

		// Executa o gerador de código.
		CodeGen codeGen = new CodeGen(checker.getStrTable(), checker.getVarTable(), args[0]);
		codeGen.execute(checker.getAST());

  }
}
