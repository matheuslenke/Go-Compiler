package br.ufes.edu.compiladores.ast;

import lombok.Getter;

@Getter
public enum NodeKind {
    VAR_DECL_ASSIGN_NODE(":="),
    ASSIGN_NODE("="),
    EQ_NODE("=="),
    BOOL_VAL_NODE(""),
    IF_NODE("if"),
    WHILE_NODE("while"),
    INT_VAL_NODE(""),
    LT_NODE("<"),
    MINUS_NODE("-"),
    OVER_NODE("/"),
    PLUS_NODE("+"),
    REAL_VAL_NODE(""),
    STR_VAL_NODE(""),
    TIMES_NODE("*"),
    VAR_DECL_NODE("var_decl"),
    VAR_USE_NODE("var_use");

    private String token;

    private NodeKind(String token) {
        this.token = token;
    }
}
