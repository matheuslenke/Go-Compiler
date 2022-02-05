package br.ufes.edu.compiladores.ast;

public enum NodeKind {
    SHORT_VAR_DECL_NODE(":="),
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
    VAR_USE_NODE("var_use"),
    VAR_LIST_NODE("var_list"),
    EXPRESSION_LIST_NODE("expression_list"),
    SOURCE_FILE("source_file"),
    FUNCTION_DECLARATION("funct_decl"),
    DECLARATION("declaration");

    private String token;

    private NodeKind(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return this.token;
    }
}
