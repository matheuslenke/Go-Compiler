package br.ufes.edu.compiladores.ast;

import java.util.ArrayList;
import java.util.List;

import br.ufes.edu.compiladores.ast.val.ValNode;
import br.ufes.edu.compiladores.tables.VarTable.Entry;
import lombok.Getter;

@Getter
public class VarDeclNode implements Node {
    private final List<Entry> variables = new ArrayList<>();
    private final List<ValNode> values = new ArrayList<>();

    public void addVariable(Entry variable) {
        this.variables.add(variable);
    }

    public void addValue(ValNode value) {
        this.values.add(value);
    }
}
