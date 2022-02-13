package br.ufes.edu.compiladores.ast.val;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class RealNode implements ValNode {
    private final Double data;

    public RealNode(Double data) {
        this.data = data;
    }

    @Override
    public String getValueAsString() {
        return data.toString();
    }
}
