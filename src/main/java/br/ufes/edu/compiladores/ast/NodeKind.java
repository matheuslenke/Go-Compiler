package br.ufes.edu.compiladores.ast;

import java.util.NoSuchElementException;

public enum NodeKind {
    // Operadores Básicos
    SHORT_VAR_DECL_NODE(":="),
    ASSIGN_NODE("="),
    EQ_NODE("=="),
    BOOL_VAL_NODE("bool"),
    IF_NODE("if"),
    FOR_NODE("for"),
    INT_VAL_NODE("int"),
    LT_NODE("<"),
    GT_NODE(">"),
    LTE_NODE("<="),
    GTE_NODE(">="),
    DIFF_NODE("!="),
    MINUS_NODE("-"),
    OVER_NODE("/"),
    PLUS_NODE("+"),
    REAL_VAL_NODE("real"),
    STR_VAL_NODE("string"),
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
    PRIMARY_EXPRESSION_NODE("primary_expression"),
    FUNC_DECL_NODE("func_decl"),
    FUNC_USE_NODE("func_use"),
    ARGUMENTS_NODE("arguments"),
    DECLARATION("declaration"),
    IDENTIFIER_LIST_NODE("identifier_list"),
    IDENTIFIER("identifier"),
    QUALIFIED_IDENTIFIER_NODE("qualified_identifier"),
    CONVERSION_NODE("conversion"),


    // Signature
    SIGNATURE_NODE("signature"),
    PARAMETERS_NODE("parameters"),
    RESULT_NODE("result"),
    PARAMETER_DECLARATION("parameter_declaration"),
    RETURN_NODE("return"),
    READ_OPERATION("read"),
    WRITE_OPERATION("write"),

    // Declarações de Tipo
    TYPE_DECLARATION("type_declaration"),
    TYPE_USE("type_use"),
    ARRAY_TYPE("array_type"),
    ARRAY_LENGTH_NODE("array_length"), 
    ARRAY_ELEMENT_TYPE_NODE("element_type"),
    ARRAY_USE_NODE("array_use"),
    INDEX_NODE("index"),
    STRUCT_TYPE("struct_type"),
    FUNCTION_TYPE("function_type"),
    INTERFACE_TYPE("interface_type"),
    SLICE_TYPE("slice_type"),
    MAP_TYPE("map_type"),
    CHANNEL_TYPE("channel_type"),
    NIL_NODE("nil"),
    CODE_BLOCK("block"),


    // Conversões de tipo
    I2F_NODE("I2F");

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
            case ARRAY_USE_NODE:
                return true;
            default:
                return false;
        }
    }

    public static boolean isVariable(NodeKind kind) {
        switch (kind) {
            case VAR_DECL_NODE:
            case VAR_USE_NODE:
            case ARRAY_USE_NODE:
            case VAR_LIST_NODE:
            case ARRAY_TYPE:
                return true;
            default:
                return false;
        }
    }
}
