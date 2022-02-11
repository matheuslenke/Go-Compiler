package br.ufes.edu.compiladores.ast;

import java.util.ArrayList;
import java.util.List;
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
