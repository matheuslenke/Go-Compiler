package br.ufes.edu.compiladores.ast.nodes;

import java.util.ArrayList;
import java.util.List;

import br.ufes.edu.compiladores.ast.Node;
import lombok.Getter;

@Getter
public class SourceFileNode implements Node {
    private final List<DeclarationNode> declarations = new ArrayList<>();
    private final List<FunctionDeclarationNode> functions = new ArrayList<>();

    public void addDeclaration(DeclarationNode declarationNode) {
        this.declarations.add(declarationNode);
    }

    public void addFunction(FunctionDeclarationNode functionDeclarationNode) {
        this.functions.add(functionDeclarationNode);
    }
}
