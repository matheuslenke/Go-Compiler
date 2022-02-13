package br.ufes.edu.compiladores.ast;

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
    FUNCTION_DECLARATION("funct_decl"),
    DECLARATION("declaration"),
    IDENTIFIER_LIST_NODE("identifier_list"),
    IDENTIFIER("identifier"),
    QUALIFIED_IDENTIFIER_NODE("qualified_identifier"),

    // Signature
    SIGNATURE_NODE("signature"),
    PARAMETERS_NODE("parameters"),
    PARAMETER_DECLARATION("parameter_declaration"),

    // Declarações de Tipo
    TYPE_DECLARATION("type_declaration"),
    ARRAY_TYPE("array_type"),
    STRUCT_TYPE("struct_type"),
    FUNCTION_TYPE("function_type"),
    INTERFACE_TYPE("interface_type"),
    SLICE_TYPE("slice_type"),
    MAP_TYPE("map_type"),
    CHANNEL_TYPE("channel_type")
    ;

    private String token;

    private NodeKind(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return this.token;
    }

    public static boolean hasData(NodeKind kind) {
		switch(kind) {
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
