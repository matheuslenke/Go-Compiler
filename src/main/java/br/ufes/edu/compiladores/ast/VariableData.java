package br.ufes.edu.compiladores.ast;

public class VariableData implements Data {
    private Integer index;

    public VariableData(Integer index) {
        this.index = index;
    }

    public Integer getIndex() {
        return this.index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
