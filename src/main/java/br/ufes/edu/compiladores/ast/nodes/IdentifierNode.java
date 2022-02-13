package br.ufes.edu.compiladores.ast.nodes;

import br.ufes.edu.compiladores.ast.val.ValNode;
import lombok.Getter;

@Getter
public class IdentifierNode implements ValNode {
    private String name;

    public IdentifierNode(String name) {
        this.name = name;
    }

    @Override
    public String getValueAsString() {
        return name;
    }
}
