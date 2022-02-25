package br.ufes.edu.compiladores.typing;

import br.ufes.edu.compiladores.ast.NodeKind;

import br.ufes.edu.compiladores.ast.AST;

public enum Conv {
    I2F, // Int to Float
    NONE; // No type conversion

    // Cria e retorna um novo nó de conversão da AST segundo o parâmetro 'conv'
    // passado.
    // O parâmetro 'n' é o nó que será pendurado como filho do nó de conversão.
    // Caso a conversão indicada seja 'NONE', a função simplesmente retorna o
    // próprio
    // nó passado como argumento.
    public static AST createConvNode(Conv conv, AST n) {
        switch (conv) {
            case I2F:
                return AST.newSubtree(NodeKind.I2F_NODE, Type.FLOAT_TYPE, n);
            case NONE:
                return n;
            default:
                System.err.printf("INTERNAL ERROR: invalid conversion of types!\n");
                // A partir de agora vou abortar no primeiro erro para facilitar.
                System.exit(1);
                return null; // Never reached...
        }
    }

    // Classe que define as informações de unificação para os tipos em expressões.
    public static final class Unif {

        // Declarei tudo como final para não precisar de getter/setter.
        public final Type type; // Tipo unificado
        public final Conv lc; // Conversão do lado esquerdo
        public final Conv rc; // Conversão do lado direito

        public Unif(Type type, Conv lc, Conv rc) {
            this.type = type;
            this.lc = lc;
            this.rc = rc;
        }

    }
}
