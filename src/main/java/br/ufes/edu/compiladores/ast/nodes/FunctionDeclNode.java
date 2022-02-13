package br.ufes.edu.compiladores.ast.nodes;

import br.ufes.edu.compiladores.ast.Node;
import lombok.Getter;

@Getter
public class FunctionDeclNode implements Node {
    private String name;

    public FunctionDeclNode(String name) {
        this.name = name;
    }

}
