package br.ufes.edu.compiladores.ast;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import br.ufes.edu.compiladores.typing.Type;
import lombok.Getter;

@Getter
public class AbstractSyntaxTree {
    private final NodeData data;
    private final Type type;
    private final NodeKind kind;
    private final List<AbstractSyntaxTree> children;

    public AbstractSyntaxTree(NodeKind kind, NodeData data, Type type) {
        this.data = data;
        this.type = type;
        this.kind = kind;
        this.children = new ArrayList<>();
    }

    // Adiciona um novos filhos ao nó.
    public void addChildren(AbstractSyntaxTree... children) {
        Collections.addAll(this.children, children);
    }

    // Cria um nó e pendura todos os filhos passados como argumento.
    public static AbstractSyntaxTree newSubtree(NodeKind kind, Type type, AbstractSyntaxTree... children) {
        AbstractSyntaxTree node = new AbstractSyntaxTree(kind, null, type);

        node.addChildren(children);

        return node;
    }
}
