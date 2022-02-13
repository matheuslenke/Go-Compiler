package br.ufes.edu.compiladores.ast.val;

import lombok.Getter;

@Getter
public class LongNode implements ValNode {
    private final Long data;

    public LongNode(Long data) {
        this.data = data;
    }

    @Override
    public String getValueAsString() {
        return data.toString();
    }
}
