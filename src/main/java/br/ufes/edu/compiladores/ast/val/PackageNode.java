package br.ufes.edu.compiladores.ast.val;

import org.antlr.v4.runtime.Token;

import lombok.Getter;

@Getter
public class PackageNode implements ValNode {
    private final String data;

    public PackageNode(String packageName) {
        this.data = packageName;
    }

    @Override
    public String getValueAsString() {
        return data;
    }
}
