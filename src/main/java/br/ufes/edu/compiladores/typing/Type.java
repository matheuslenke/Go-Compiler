package br.ufes.edu.compiladores.typing;

// Enumeração dos tipos nativos que podem existir em GO.
public enum Type {
    INT_TYPE("int"),
    FLOAT_TYPE("float"),
    BOOL_TYPE("bool"),
    STR_TYPE("string"),
    NIL_TYPE("nil"),
    FUNC_TYPE("func"),
    ARRAY_TYPE("array"),
    NO_TYPE("no_type"); // Indica um erro de tipos.

    private String value;

    private Type(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    // Tabela de unificação de tipos primitivos para o
    // operador '+'.
    private static Type[][] plus = {
            { INT_TYPE, NO_TYPE,    NO_TYPE, NO_TYPE },
            { NO_TYPE,  FLOAT_TYPE, NO_TYPE, NO_TYPE },
            { NO_TYPE,  NO_TYPE,    NO_TYPE, NO_TYPE },
            { NO_TYPE,  NO_TYPE,    NO_TYPE, STR_TYPE }
    };

    public Type unifyPlus(Type that) {
        return plus[this.ordinal()][that.ordinal()];
    }

    // Tabela de unificação de tipos primitivos para os
    // demais operadores aritméticos.
    private static Type[][] other = {
            { INT_TYPE, NO_TYPE,    NO_TYPE, NO_TYPE },
            { NO_TYPE,  FLOAT_TYPE, NO_TYPE, NO_TYPE },
            { NO_TYPE,  NO_TYPE,    NO_TYPE, NO_TYPE },
            { NO_TYPE,  NO_TYPE,    NO_TYPE, NO_TYPE }
    };

    public Type unifyOtherArith(Type that) {
        return other[this.ordinal()][that.ordinal()];
    }

    // Tabela de unificação de tipos primitivos para os
    // operadores de comparação.
    private static Type[][] comp = {
            { BOOL_TYPE, NO_TYPE,   NO_TYPE, NO_TYPE },
            { NO_TYPE,   BOOL_TYPE, NO_TYPE, NO_TYPE },
            { NO_TYPE,   NO_TYPE,   NO_TYPE, NO_TYPE },
            { NO_TYPE,   NO_TYPE,   NO_TYPE, BOOL_TYPE }
    };

    public Type unifyComp(Type that) {
        return comp[this.ordinal()][that.ordinal()];
    }

    // Tabela de unificação de tipos primitivos para os
    // operadores de comparação de igualdade.
    private static Type[][] eq = {
            { BOOL_TYPE, NO_TYPE,   NO_TYPE,   NO_TYPE },
            { NO_TYPE,   BOOL_TYPE, NO_TYPE,   NO_TYPE },
            { NO_TYPE,   NO_TYPE,   BOOL_TYPE, NO_TYPE },
            { NO_TYPE,   NO_TYPE,   NO_TYPE,   BOOL_TYPE }
    };

    public Type unifyEq(Type that) {
        return eq[this.ordinal()][that.ordinal()];
    }

    // Tabela de unificação de tipos primitivos para os
    // operadores de atribuição.
    private static Type[][] attr = {
            { INT_TYPE,    NO_TYPE,    NO_TYPE,   NO_TYPE },
            { FLOAT_TYPE,  FLOAT_TYPE, NO_TYPE,   NO_TYPE },
            { NO_TYPE,     NO_TYPE,    BOOL_TYPE, NO_TYPE },
            { NO_TYPE,     NO_TYPE,    NO_TYPE,   STR_TYPE }
    };

    public Type unifyAttr(Type that) {
        return attr[this.ordinal()][that.ordinal()];
    }

}
