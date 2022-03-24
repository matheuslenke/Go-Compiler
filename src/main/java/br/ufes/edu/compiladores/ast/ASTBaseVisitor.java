package br.ufes.edu.compiladores.ast;

/*
 * Classe abstrata que define a interface do visitor para a AST.
 * Implementa o despacho do método 'visit' conforme o 'kind' do nó.
 * Com isso, basta herdar desta classe para criar um interpretador
 * ou gerador de código.
 */
public abstract class ASTBaseVisitor<T> {
    // Único método público. Começa a visita a partir do nó raiz
	// passado. Precisa ter outro nome porque tem a mesma assinatura
	// que o método "genérico" 'visit'.
	public void execute(AST root) {
		visit(root);
	}
	
	// Método "genérico" que despacha a visitação para os métodos
	// especializados conforme o 'kind' do nó atual. Igual ao código
	// em C. Novamente fica o argumento sobre usar OO ou não aqui.
	// Se tivéssemos trocentas classes especializando o nó da AST
	// esse despacho seria feito pela JVM. Aqui precisa fazer na mão.
	// Por outro lado, assim não precisa de trocentas classes com o
	// código todo espalhado entre elas...
	protected T visit(AST node) {
		switch(node.getKind()) {
	        case SOURCE_FILE:  return visitSourceFile(node);
			case ASSIGN_LIST_NODE: return visitAssignList(node);
	        case ASSIGN_NODE:   return visitAssign(node);
	        case CODE_BLOCK:    return visitBlock(node);
	        case BOOL_VAL_NODE: return visitBoolVal(node);
	        case IF_NODE:       return visitIf(node);
	        case INT_VAL_NODE:  return visitIntVal(node);
	        case EQ_NODE:       return visitEq(node);
	        case LT_NODE:       return visitLt(node);
	        case LTE_NODE:       return visitLte(node);
	        case GTE_NODE:       return visitGte(node);
	        case GT_NODE:       return visitGt(node);
	        case DIFF_NODE:       return visitDiff(node);
	        case MINUS_NODE:    return visitMinus(node);
	        case OVER_NODE:     return visitOver(node);
	        case PLUS_NODE:     return visitPlus(node);
	        case READ_OPERATION:     return visitRead(node);
	        case REAL_VAL_NODE: return visitRealVal(node);
	        case FOR_NODE:   return visitFor(node);
	        case STR_VAL_NODE:  return visitStrVal(node);
	        case TIMES_NODE:    return visitTimes(node);
	        case VAR_DECL_NODE: return visitVarDecl(node);
	        case VAR_LIST_NODE: return visitVarList(node);
	        case VAR_USE_NODE:  return visitVarUse(node);
	        case WRITE_OPERATION:    return visitWrite(node);
			case FUNC_USE_NODE:		return visitFuncUse(node);
			case FUNC_DECL_NODE:		return visitFuncDecl(node);
			case PARAMETERS_NODE:		return visitParameters(node);
			case IMPORT_SPEC:	return visitImportSpec(node);
			case ARGUMENTS_NODE: return visitArguments(node);
	
	        // case B2I_NODE:      return visitB2I(node);
	        // case B2R_NODE:      return visitB2R(node);
	        // case B2S_NODE:      return visitB2S(node);
	        // case I2R_NODE:      return visitI2R(node);
	        // case I2S_NODE:      return visitI2S(node);
	        // case R2S_NODE:      return visitR2S(node);
	
	        default:
	            System.err.printf("Invalid kind: %s!\n", node.getKind().toString());
	            System.exit(1);
	            return null;
		}
	}
	
	// Métodos especializados para visitar um nó com um certo 'kind'.

    protected abstract T visitSourceFile(AST node);

	protected abstract T visitImportSpec(AST node);
	
    protected abstract T visitFuncDecl(AST node);
    protected abstract T visitParameters(AST node);
	
  
	protected abstract T visitFuncUse(AST node);
	protected abstract T visitArguments(AST node);
		
	
	protected abstract T visitAssign(AST node);
	protected abstract T visitAssignList(AST node);

	// Comparações

	protected abstract T visitLt(AST node);
	protected abstract T visitGt(AST node);
	protected abstract T visitLte(AST node);
	protected abstract T visitGte(AST node);
	protected abstract T visitEq(AST node);
	protected abstract T visitDiff(AST node);
	

	protected abstract T visitIntVal(AST node);
	protected abstract T visitStrVal(AST node);

	protected abstract T visitBlock(AST node);

	protected abstract T visitBoolVal(AST node);

	protected abstract T visitIf(AST node);

	protected abstract T visitTimes(AST node);

	protected abstract T visitMinus(AST node);

	protected abstract T visitOver(AST node);

	protected abstract T visitPlus(AST node);

	protected abstract T visitRead(AST node);

	protected abstract T visitRealVal(AST node);

	protected abstract T visitFor(AST node);

	protected abstract T visitVarDecl(AST node);

	protected abstract T visitVarList(AST node);

	protected abstract T visitVarUse(AST node);

	protected abstract T visitWrite(AST node);

	protected abstract T visitB2I(AST node);

	protected abstract T visitB2R(AST node);

	protected abstract T visitB2S(AST node);

	protected abstract T visitI2R(AST node);

	protected abstract T visitI2S(AST node);

	protected abstract T visitR2S(AST node);
}
