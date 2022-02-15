package br.ufes.edu.compiladores.ast;

public class EmptyData implements Data {
    
    public Integer getIndex() {
        return null;
    }
    public void setIndex(Integer index) {
        throw new Error("Empty data cannot set index");
    }
}
