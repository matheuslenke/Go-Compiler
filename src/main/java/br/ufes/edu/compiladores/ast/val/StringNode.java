package br.ufes.edu.compiladores.ast.val;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class StringNode implements ValNode {
    private final String data;

    public StringNode(String data) {
        this.data = data;
    }

    @Override
    public String getValueAsString() {
        return data;
    }
}
