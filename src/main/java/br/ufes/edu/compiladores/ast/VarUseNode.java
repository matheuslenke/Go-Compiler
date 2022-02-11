package br.ufes.edu.compiladores.ast;

import java.util.ArrayList;
import java.util.List;

import br.ufes.edu.compiladores.tables.VarTable.Entry;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VarUseNode implements Node {
    private final List<Entry> entry = new ArrayList<>();
}
