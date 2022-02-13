package br.ufes.edu.compiladores.ast.val;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BooleanNode implements ValNode {
    private final Boolean data;

    public BooleanNode(Boolean data) {
        this.data = data;
    }

    @Override
    public String getValueAsString() {
        return data.toString();
    }

    @Getter
    @Builder
    public static class ImportNode implements ValNode {
        private String data;

        public ImportNode(String name) {
            this.data = name;
        }

        @Override
        public String getValueAsString() {
            return data;
        }
    }
}
