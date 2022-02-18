package br.ufes.edu.compiladores.ast;

public class StringData implements Data {
    private Integer index;

    public StringData(Integer index) {
        this.index = index;
    }

    public Integer getValue() {
        return this.index;
    }

    public void setValue(Integer index) {
        this.index = index;
    }
}
