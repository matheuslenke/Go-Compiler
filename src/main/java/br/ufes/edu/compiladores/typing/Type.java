package br.ufes.edu.compiladores.typing;

import static br.ufes.edu.compiladores.typing.Conv.I2F;
import static br.ufes.edu.compiladores.typing.Conv.NONE;

import br.ufes.edu.compiladores.typing.Conv.Unif;

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
        private static Unif[][] plus = {
                        { new Unif(INT_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE),
                                        new Unif(NO_TYPE, NONE, NONE) },
                        { new Unif(NO_TYPE, NONE, NONE), new Unif(FLOAT_TYPE, NONE, NONE),
                                        new Unif(NO_TYPE, NONE, NONE),
                                        new Unif(NO_TYPE, NONE, NONE) },
                        { new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE),
                                        new Unif(NO_TYPE, NONE, NONE) },
                        { new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE),
                                        new Unif(STR_TYPE, NONE, NONE) }
        };

        public Unif unifyPlus(Type that) {
                return plus[this.ordinal()][that.ordinal()];
        }

        // Tabela de unificação de tipos primitivos para os
        // demais operadores aritméticos.
        private static Unif[][] other = {
                        { new Unif(INT_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE),
                                        new Unif(NO_TYPE, NONE, NONE) },
                        { new Unif(NO_TYPE, NONE, NONE), new Unif(FLOAT_TYPE, NONE, NONE),
                                        new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE) },
                        { new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE),
                                        new Unif(NO_TYPE, NONE, NONE) },
                        { new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE),
                                        new Unif(NO_TYPE, NONE, NONE) }
        };

        public Unif unifyOtherArith(Type that) {
                return other[this.ordinal()][that.ordinal()];
        }

        // Tabela de unificação de tipos primitivos para os
        // operadores de comparação.
        private static Unif[][] comp = {
                        { new Unif(BOOL_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE),
                                        new Unif(NO_TYPE, NONE, NONE) },
                        { new Unif(NO_TYPE, NONE, NONE), new Unif(BOOL_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE),
                                        new Unif(NO_TYPE, NONE, NONE) },
                        { new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE),
                                        new Unif(NO_TYPE, NONE, NONE) },
                        { new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE),
                                        new Unif(BOOL_TYPE, NONE, NONE) }
        };

        public Unif unifyComp(Type that) {
                return comp[this.ordinal()][that.ordinal()];
        }

        // Tabela de unificação de tipos primitivos para os
        // operadores de comparação de igualdade.
        private static Unif[][] eq = {
                        { new Unif(BOOL_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE),
                                        new Unif(NO_TYPE, NONE, NONE) },
                        { new Unif(NO_TYPE, NONE, NONE), new Unif(BOOL_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE),
                                        new Unif(NO_TYPE, NONE, NONE) },
                        { new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE), new Unif(BOOL_TYPE, NONE, NONE),
                                        new Unif(NO_TYPE, NONE, NONE) },
                        { new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE),
                                        new Unif(BOOL_TYPE, NONE, NONE) }
        };

        public Unif unifyEq(Type that) {
                return eq[this.ordinal()][that.ordinal()];
        }

        // Tabela de unificação de tipos primitivos para os
        // operadores de atribuição.
        private static Unif[][] attr = {
                        { new Unif(INT_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE),
                                        new Unif(NO_TYPE, NONE, NONE) },
                        { new Unif(FLOAT_TYPE, NONE, I2F), new Unif(FLOAT_TYPE, NONE, NONE),
                                        new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE) },
                        { new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE), new Unif(BOOL_TYPE, NONE, NONE),
                                        new Unif(NO_TYPE, NONE, NONE) },
                        { new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE),
                                        new Unif(STR_TYPE, NONE, NONE) }
        };

        public Unif unifyAttr(Type that) {
                return attr[this.ordinal()][that.ordinal()];
        }

}
