package br.ufes.edu.compiladores.typing;

// Enumeração dos tipos nativos que podem existir em GO.
public enum Type {
    INT_TYPE {
		public String toString() {
            return "int";
        }
	},
    FLOAT_TYPE {
		public String toString() {
			return "float";
		}
	},
    BOOL_TYPE {
		public String toString() {
            return "bool";
        }
	},
    STR_TYPE {
		public String toString() {
            return "string";
        }
	},
	RUNE_TYPE {
		public String toString() {
			return "rune";
		}
	},
	NIL_TYPE {
		public String toString() {
			return "nil";
		}
	},
	NO_TYPE { // Indica um erro de tipos.
		public String toString() {
            return "no_type";
        }
	};

	// Tabela de unificação de tipos primitivos para o
	// operador '+'.
	private static Type plus[][] = {
		{ INT_TYPE,  FLOAT_TYPE, INT_TYPE,  STR_TYPE },
		{ FLOAT_TYPE, FLOAT_TYPE, FLOAT_TYPE, STR_TYPE },
		{ INT_TYPE,  FLOAT_TYPE, BOOL_TYPE, STR_TYPE },
		{ STR_TYPE,  STR_TYPE,  STR_TYPE,  STR_TYPE }
	};
	
	public Type unifyPlus(Type that) {
		return plus[this.ordinal()][that.ordinal()];
	}
	
	// Tabela de unificação de tipos primitivos para os
	// demais operadores aritméticos.
	private static Type other[][] = {
		{ INT_TYPE,  FLOAT_TYPE, NO_TYPE, NO_TYPE },
		{ FLOAT_TYPE, FLOAT_TYPE, NO_TYPE, NO_TYPE },
		{ NO_TYPE,   NO_TYPE,   NO_TYPE, NO_TYPE },
		{ NO_TYPE,   NO_TYPE,   NO_TYPE, NO_TYPE }
	};

	public Type unifyOtherArith(Type that) {
	    return other[this.ordinal()][that.ordinal()];
	}

	// Tabela de unificação de tipos primitivos para os
	// operadores de comparação.
	private static Type comp[][] = {
		{ BOOL_TYPE, BOOL_TYPE, NO_TYPE, NO_TYPE },
		{ BOOL_TYPE, BOOL_TYPE, NO_TYPE, NO_TYPE },
		{ NO_TYPE,   NO_TYPE,   NO_TYPE, NO_TYPE },
		{ NO_TYPE,   NO_TYPE,   NO_TYPE, BOOL_TYPE}
	};

	public Type unifyComp(Type that) {
		return comp[this.ordinal()][that.ordinal()];
	}

}
