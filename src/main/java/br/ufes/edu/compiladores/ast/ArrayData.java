package br.ufes.edu.compiladores.ast;

import java.util.ArrayList;

public class ArrayData implements Data {
    ArrayList<Object> list = new ArrayList<>();
    Integer size = 0;
    Integer index;

    public ArrayData(Integer index) {
        this.index = index;
    }

    public ArrayData(Integer index, Integer size) {
        this.index = index;
        this.size = size;
    }

    public Object getData(Integer position) {
        return list.get(position);
    }

    public boolean setData(Integer position, Object element) {
        if(position < 0 || position > size) {
            return false;
        }
        this.list.add(position, element);
        return true;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return this.index.toString();
    }
}
