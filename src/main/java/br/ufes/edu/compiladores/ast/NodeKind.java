package br.ufes.edu.compiladores.ast;

import java.util.NoSuchElementException;

public enum NodeKind {
    // Operadores Básicos
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
    ASSIGN_LIST_NODE("assign_list_node"),

    // Import e Package
    SOURCE_FILE("source_file"),
    IMPORT_LIST_NODE("import_list"),
    IMPORT_DECL("import_decl"),
    IMPORT_SPEC("import_spec"),
    PACKAGE_NODE("package"),

    // Variáveis
    VAR_DECL_NODE("var_decl"),
    VAR_USE_NODE("var_use"),
    VAR_LIST_NODE("var_list"),
    EXPRESSION_LIST_NODE("expression_list"),
    FUNC_DECL_NODE("func_decl"),
    DECLARATION("declaration"),
    IDENTIFIER_LIST_NODE("identifier_list"),
    IDENTIFIER("identifier"),
    QUALIFIED_IDENTIFIER_NODE("qualified_identifier"),
    CONVERSION_NODE("conversion"),

    // Signature
    SIGNATURE_NODE("signature"),
    PARAMETERS_NODE("parameters"),
    PARAMETER_DECLARATION("parameter_declaration"),

    // Declarações de Tipo
    TYPE_DECLARATION("type_declaration"),
    TYPE_USE("type_use"),
    ARRAY_TYPE("array_type"),
    STRUCT_TYPE("struct_type"),
    FUNCTION_TYPE("function_type"),
    INTERFACE_TYPE("interface_type"),
    SLICE_TYPE("slice_type"),
    MAP_TYPE("map_type"),
    CHANNEL_TYPE("channel_type"),
    NIL_NODE("nil"),
    CODE_BLOCK("block");

    private String token;

    private NodeKind(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return this.token;
    }

    public static NodeKind fromValue(String token) {

        if (token == null) {
            throw new NoSuchElementException("Invalid NodeKind!");
        }
        for (final NodeKind v : values()) {
            if (v.token.equals(token)) {
                return v;
            }
        }
        throw new NoSuchElementException("Invalid NodeKind!");
    }

    public static boolean hasData(NodeKind kind) {
        switch (kind) {
            case BOOL_VAL_NODE:
            case INT_VAL_NODE:
            case REAL_VAL_NODE:
            case STR_VAL_NODE:
            case VAR_DECL_NODE:
            case VAR_USE_NODE:
                return true;
            default:
                return false;
        }
    }
}
