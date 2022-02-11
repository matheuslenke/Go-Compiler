package br.ufes.edu.compiladores.ast.val;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LongNode extends ValNode {
    private final Long data;
}
