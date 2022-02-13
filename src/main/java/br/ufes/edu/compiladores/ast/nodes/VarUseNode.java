package br.ufes.edu.compiladores.ast.nodes;


import br.ufes.edu.compiladores.ast.Node;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VarUseNode implements Node {
    private Integer varTableIndex;

    public VarUseNode(Integer index) {
        this.varTableIndex = index;
    }
}
