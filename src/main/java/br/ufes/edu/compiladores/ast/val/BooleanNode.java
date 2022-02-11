package br.ufes.edu.compiladores.ast.val;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BooleanNode extends ValNode {
    private final Boolean data;
}
