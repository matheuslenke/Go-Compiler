package br.ufes.edu.compiladores.typing;

// Enumeração dos tipos nativos que podem existir em GO.
public enum PrimitiveType {
    // Set of Boolean truth values denoted by the predeclared constants true and
    // false
    BOOL_TYPE("bool"),
    
    NUMERIC_TYPE("numeric"),

    // Set of string values. A string value is a (possibly empty) sequence of bytes.
    STR_TYPE("string");

    private String name;

    private PrimitiveType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
