package br.ufes.edu.compiladores.ast.val;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RealNode extends ValNode {
    private final Double data;
}