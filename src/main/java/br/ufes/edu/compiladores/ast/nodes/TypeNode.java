package br.ufes.edu.compiladores.ast.nodes;

import br.ufes.edu.compiladores.ast.Node;

public class TypeNode implements Node {
    private String typeName;

    public TypeNode(String name) {
        this.typeName = name;
    }
}
