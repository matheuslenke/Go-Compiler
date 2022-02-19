package br.ufes.edu.compiladores.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import br.ufes.edu.compiladores.tables.StrTable;
import br.ufes.edu.compiladores.tables.VarTable;
import br.ufes.edu.compiladores.typing.Type;

public class AST {
	private final Data data; // Data
	private final Type type;
	private final NodeKind kind;
	private final List<AST> children;

	// Cria um nó com um dado do tipo ValNode
	public AST(final NodeKind kind, final Data data, final Type type) {
		this.data = data;
		this.type = type;
		this.kind = kind;
		this.children = new ArrayList<>();
	}

	// Adiciona um novos filhos ao nó.
	public void addChildren(final AST... children) {
		Collections.addAll(this.children, children);
	}

	public List<AST> getChildren() {
		return this.children;
	}

	public Data getData() {
		return this.data;
	}

	public Type getType() {
		return this.type;
	}

	// Cria um nó e pendura todos os filhos passados como argumento.
	public static AST newSubtree(final NodeKind kind, final Type type, final AST... children) {
		final AST node = new AST(kind, null, type);

		node.addChildren(children);

		return node;
	}

	public Optional<AST> getChild(int idx) {

		Integer listSize = this.children.size();
		if (idx > 0 && idx < listSize) {
			return Optional.of(this.children.get(idx));
		}

		return Optional.empty();
	}

	// Variáveis internas usadas para geração da saída em DOT.
	// Estáticas porque só precisamos de uma instância.
	private static int nr;
	private static VarTable vt;
	private static StrTable st;

	// Imprime recursivamente a codificação em DOT da subárvore começando no nó
	// atual.
	// Usa stderr como saída para facilitar o redirecionamento, mas isso é só um
	// hack.
	private int printNodeDot() {
		int myNr = nr++;

		System.err.printf("node%d[label=\"", myNr);
		if (this.type != Type.NO_TYPE) {
			System.err.printf("(%s) ", this.type.toString());
		}
		if (this.kind == NodeKind.FUNC_DECL_NODE || this.kind == NodeKind.VAR_DECL_NODE
				|| this.kind == NodeKind.VAR_USE_NODE) {
			VariableData varData = (VariableData) this.data;
			System.err.printf("%s@%d", vt.getName(varData.getIndex()), varData.getIndex());
		} else {
			System.err.printf("%s", this.kind.toString());
		}
		if (NodeKind.hasData(this.kind)) {
			switch (this.kind) {
				case REAL_VAL_NODE:
					RealData realData = (RealData) this.data;
					System.err.print("@" + realData.getValue());
					break;

				case STR_VAL_NODE:
					StringData strData = (StringData) this.data;
					System.err.print("@" + st.get(strData.getValue()));
					break;

				case INT_VAL_NODE:
					IntData intData = (IntData) this.data;
					System.err.print("@" + intData.getValue());
					break;

				case BOOL_VAL_NODE:
					BoolData boolData = (BoolData) this.data;
					System.err.print("@" + boolData.getValue());
					break;

				default:
					break;
			}
		}
		System.err.printf("\"];\n");

		for (int i = 0; i < this.children.size(); i++) {
			int childNr = this.children.get(i).printNodeDot();
			System.err.printf("node%d -> node%d;\n", myNr, childNr);
		}
		return myNr;
	}

	// Imprime a árvore toda em stderr.
	public static void printDot(AST tree, VarTable table, StrTable stringTable) {
		nr = 0;
		vt = table;
		st = stringTable;
		System.err.printf("digraph {\ngraph [ordering=\"out\"];\n");
		tree.printNodeDot();
		System.err.printf("}\n");
	}

}
