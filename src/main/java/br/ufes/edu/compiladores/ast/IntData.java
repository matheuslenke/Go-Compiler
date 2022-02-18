package br.ufes.edu.compiladores.ast;

public class IntData implements Data {
    private Integer data;

    public IntData(Integer data) {
        this.data = data;
    }

    public Integer getValue() {
        return this.data;
    }

    public void setValue(Integer data) {
        this.data = data;
    }
}
