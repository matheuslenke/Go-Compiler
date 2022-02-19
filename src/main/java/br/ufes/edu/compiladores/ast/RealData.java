package br.ufes.edu.compiladores.ast;

public class RealData implements Data {
    private Double data;

    public RealData(Double data) {
        this.data = data;
    }

    public Double getValue() {
        return this.data;
    }

    public void setValue(Double data) {
        this.data = data;
    }

}
