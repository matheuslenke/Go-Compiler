package br.ufes.edu.compiladores.typing;

// Enumeração dos tipos nativos que podem existir em GO.
public enum Type {
	INT_TYPE {
        @Override
		public String toString() {
			return "int";
		}
	},
	FLOAT_TYPE {
        @Override
		public String toString() {
			return "float";
		}
	},
	BOOL_TYPE {
        @Override
		public String toString() {
			return "bool";
		}
	},
	STR_TYPE {
        @Override
		public String toString() {
			return "string";
		}
	},
	NIL_TYPE {
        @Override
		public String toString() {
			return "nil";
		}
	},
	FUNC_TYPE {
        @Override
		public String toString() {
			return "func";
		}
	},
	NO_TYPE { // Indica um erro de tipos.
        @Override
		public String toString() {
			return "no_type";
		}
	};

	// Tabela de unificação de tipos primitivos para o
	// operador '+'.
	private static Type[][] plus= {
			{ INT_TYPE, NO_TYPE, NO_TYPE, NO_TYPE },
			{ NO_TYPE, FLOAT_TYPE, NO_TYPE, NO_TYPE },
			{ NO_TYPE, NO_TYPE, NO_TYPE, NO_TYPE },
			{ NO_TYPE, NO_TYPE, NO_TYPE, STR_TYPE }
	};

	public Type unifyPlus(Type that) {
		return plus[this.ordinal()][that.ordinal()];
	}

	// Tabela de unificação de tipos primitivos para os
	// demais operadores aritméticos.
	private static Type[][] other = {
			{ INT_TYPE, NO_TYPE, NO_TYPE, NO_TYPE },
			{ NO_TYPE, FLOAT_TYPE, NO_TYPE, NO_TYPE },
			{ NO_TYPE, NO_TYPE, NO_TYPE, NO_TYPE },
			{ NO_TYPE, NO_TYPE, NO_TYPE, NO_TYPE }
	};

	public Type unifyOtherArith(Type that) {
		return other[this.ordinal()][that.ordinal()];
	}

	// Tabela de unificação de tipos primitivos para os
	// operadores de comparação.
	private static Type[][] comp = {
			{ BOOL_TYPE, NO_TYPE, NO_TYPE, NO_TYPE },
			{ NO_TYPE, BOOL_TYPE, NO_TYPE, NO_TYPE },
			{ NO_TYPE, NO_TYPE, NO_TYPE, NO_TYPE },
			{ NO_TYPE, NO_TYPE, NO_TYPE, BOOL_TYPE }
	};

	public Type unifyComp(Type that) {
		return comp[this.ordinal()][that.ordinal()];
	}

	// Tabela de unificação de tipos primitivos para os
	// operadores de comparação de igualdade.
	private static Type[][] eq = {
			{ BOOL_TYPE, NO_TYPE, NO_TYPE, NO_TYPE },
			{ NO_TYPE, BOOL_TYPE, NO_TYPE, NO_TYPE },
			{ NO_TYPE, NO_TYPE, BOOL_TYPE, NO_TYPE },
			{ NO_TYPE, NO_TYPE, NO_TYPE, BOOL_TYPE }
	};

	public Type unifyEq(Type that) {
		return eq[this.ordinal()][that.ordinal()];
	}

	// Tabela de unificação de tipos primitivos para os
	// operadores de atribuição.
	private static Type[][] attr = {
			{ INT_TYPE, NO_TYPE, NO_TYPE, NO_TYPE },
			{ FLOAT_TYPE, FLOAT_TYPE, NO_TYPE, NO_TYPE },
			{ NO_TYPE, NO_TYPE, BOOL_TYPE, NO_TYPE },
			{ NO_TYPE, NO_TYPE, NO_TYPE, STR_TYPE }
	};

	public Type unifyAttr(Type that) {
		return attr[this.ordinal()][that.ordinal()];
	}

}
