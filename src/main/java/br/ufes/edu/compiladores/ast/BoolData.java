package br.ufes.edu.compiladores.ast;

public class BoolData implements Data {
    private Boolean data;

    public BoolData(Boolean data) {
        this.data = data;
    }

    public Boolean getValue() {
        return this.data;
    }

    public void setValue(Boolean data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return Boolean.toString(this.data);
    }

}
