package br.ufes.edu.compiladores.checker;

import java.util.List;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import br.ufes.edu.compiladores.GoParser.AddOpContext;
import br.ufes.edu.compiladores.GoParser.ArrayTypeContext;
import br.ufes.edu.compiladores.GoParser.AssignmentContext;
import br.ufes.edu.compiladores.GoParser.BlockContext;
import br.ufes.edu.compiladores.GoParser.Boolean_Context;
import br.ufes.edu.compiladores.GoParser.DeclarationContext;
import br.ufes.edu.compiladores.GoParser.ExpressionContext;
import br.ufes.edu.compiladores.GoParser.ForStmtContext;
import br.ufes.edu.compiladores.GoParser.FunctionDeclContext;
import br.ufes.edu.compiladores.GoParser.IfStmtContext;
import br.ufes.edu.compiladores.GoParser.ImportDeclContext;
import br.ufes.edu.compiladores.GoParser.ImportSpecContext;
import br.ufes.edu.compiladores.GoParser.IntegerContext;
import br.ufes.edu.compiladores.GoParser.MulOpContext;
import br.ufes.edu.compiladores.GoParser.NilTypeContext;
import br.ufes.edu.compiladores.GoParser.OperandNameContext;
import br.ufes.edu.compiladores.GoParser.ParameterDeclContext;
import br.ufes.edu.compiladores.GoParser.ParametersContext;
import br.ufes.edu.compiladores.GoParser.RealContext;
import br.ufes.edu.compiladores.GoParser.RelOpContext;
import br.ufes.edu.compiladores.GoParser.ResultContext;
import br.ufes.edu.compiladores.GoParser.ShortVarDeclContext;
import br.ufes.edu.compiladores.GoParser.ReturnStmtContext;
import br.ufes.edu.compiladores.GoParser.SourceFileContext;
import br.ufes.edu.compiladores.GoParser.StatementContext;
import br.ufes.edu.compiladores.GoParser.String_Context;
import br.ufes.edu.compiladores.GoParser.TypeNameContext;
import br.ufes.edu.compiladores.GoParser.Type_Context;
import br.ufes.edu.compiladores.GoParser.VarDeclExplTypeContext;
import br.ufes.edu.compiladores.GoParser.VarDeclImplTypeContext;
import br.ufes.edu.compiladores.GoParserBaseVisitor;
import br.ufes.edu.compiladores.ast.AST;
import br.ufes.edu.compiladores.ast.BoolData;
import br.ufes.edu.compiladores.ast.EmptyData;
import br.ufes.edu.compiladores.ast.IntData;
import br.ufes.edu.compiladores.ast.NodeKind;
import br.ufes.edu.compiladores.ast.RealData;
import br.ufes.edu.compiladores.ast.StringData;
import br.ufes.edu.compiladores.ast.VariableData;
import br.ufes.edu.compiladores.tables.StrTable;
import br.ufes.edu.compiladores.tables.VarTable;
import br.ufes.edu.compiladores.typing.Type;
import br.ufes.edu.compiladores.utils.TypeUtil;

public class SemanticChecker extends GoParserBaseVisitor<AST> {

    protected StrTable st = new StrTable(); // Tabela de strings.
    protected VarTable vt = new VarTable(); // Tabela de variáveis.

    AST root; // Nó raiz da AST sendo construída.

    protected Type lastDeclType; // Variável "global" com o último tipo declarado.

    // Testa se o dado token foi declarado antes.
    AST checkVar(final Token token) {
        final String text = token.getText();
        final int line = token.getLine();
        final Integer index = vt.lookupVar(text);
        if (index == -1) {
            System.out.println(String.format(
                    "SEMANTIC ERROR (%d): variable '%s' was not declared.\n",
                    line, text));
            System.exit(1);
        }

        return new AST(NodeKind.VAR_USE_NODE, new VariableData(index), vt.getType(index));
    }

    // Cria uma nova variável a partir do dado token.
    AST newVar(Token token) {
        String text = token.getText();
        int currentLine = token.getLine();
        Integer index = vt.lookupVar(text);
        if (index != -1) {
            System.out.println(String.format(
                    "SEMANTIC ERROR (%d): variable '%s' already declared at line %d.\n",
                    currentLine, text, currentLine));
            System.exit(1);
        }
        Integer idx = vt.addVar(text, currentLine, lastDeclType);
        Type t = vt.getType(idx);

        // Constrói o nó para a nova variável
        switch (t) {
            case STR_TYPE:
                return new AST(NodeKind.VAR_DECL_NODE, new VariableData(idx), lastDeclType);
            case BOOL_TYPE:
                return new AST(NodeKind.VAR_DECL_NODE, new VariableData(idx), lastDeclType);
            case FLOAT_TYPE:
                return new AST(NodeKind.VAR_DECL_NODE, new VariableData(idx), lastDeclType);
            case INT_TYPE:
                return new AST(NodeKind.VAR_DECL_NODE, new VariableData(idx), lastDeclType);
            case FUNC_TYPE:
                return new AST(NodeKind.FUNC_DECL_NODE, new VariableData(idx), lastDeclType);
            case NO_TYPE:
                return new AST(NodeKind.VAR_DECL_NODE, new EmptyData(), lastDeclType);
            default:
                throw new Error("Type invalid.");
        }
    }

    // ----------------------------------------------------------------------------
    // Type checking and inference.

    private void checkTypeError(final int lineNo, final String op, final Type t1, final Type t2) {
        if (t1 != t2) {
            System.out.println(String.format(
                    "SEMANTIC ERROR (%d): incompatible types for operator '%s', LHS is '%s' and RHS is '%s'.\n",
                    lineNo, op, t1, t2));
            System.exit(1);
        }
    }

    private void checkBoolExpr(int lineNo, String cmd, Type t) {
        if (t != Type.BOOL_TYPE) {
            System.out.printf("SEMANTIC ERROR (%d): conditional expression in '%s' is '%s' instead of '%s'.\n",
                    lineNo, cmd, t.toString(), Type.BOOL_TYPE.toString());
            System.exit(1);
        }
    }

    // Exibe o conteúdo das tabelas em stdout.
    public void printTables() {
        System.out.println("\n\n");
        System.out.println(st);
        System.out.println("\n\n");
        System.out.println(vt);
        System.out.println("\n\n");
    }

    // Exibe a AST no formato DOT em stderr.
    public void printAST() {
        AST.printDot(root, vt, st);
    }

    // Checkers
    // ----------------------------------------------------------------------------
    private void checkTypeValid(final Token token) {
        final String text = token.getText();
        final int line = token.getLine();
        final Type t = TypeUtil.getTypeByIdentifier(text);
        if (t == null) {
            System.out.println(String.format("SEMANTIC ERROR (%d): Type '%s' doesn't exist.\n",
                    line, text));
            System.exit(1);
        }
        this.lastDeclType = t;
    }

    private void checkWrongAssignCount(int lineNo, int quantExpected, int quantReal) {
        if (quantExpected != quantReal) {
            System.out.println(String.format("SEMANTIC ERROR (%d): cannot initialize '%d' variables with '%d' values",
                    lineNo, quantExpected, quantReal));
            System.exit(1);
        }
    }

    // ----------------------------------------------------------------------------
    // Visitadores.

    // Regra inicial!
    // Visita a regra
    // sourceFile: packageClause eos (importDecl eos)* ( (functionDecl| methodDecl |
    // declaration) eos)* EOF
    @Override
    public AST visitSourceFile(final SourceFileContext ctx) {
        // Visita recursivamente os filhos para construir a AST.
        this.root = AST.newSubtree(NodeKind.SOURCE_FILE, Type.NO_TYPE);

        for (ImportDeclContext importDeclContext : ctx.importDecl()) {
            AST importDecl = this.visit(importDeclContext);
            if (importDecl != null) {
                this.root.addChildren(importDecl);
            }
        }

        for (final FunctionDeclContext functionDeclContext : ctx.functionDecl()) {
            AST functionDecl = visit(functionDeclContext);
            if (functionDecl != null) {
                this.root.addChildren(functionDecl);
            }
        }
        for (DeclarationContext declarationCtx : ctx.declaration()) {
            AST child = visit(declarationCtx.varDecl());
            if (child != null) {
                this.root.addChildren(child);
            }
        }

        return this.root;
    }

    // <---------------- Visitadores dos FUNÇÕES ---------------->
    @Override
    public AST visitFunctionDecl(final FunctionDeclContext ctx) {
        lastDeclType = Type.FUNC_TYPE;
        AST funcVar = newVar(ctx.IDENTIFIER().getSymbol());

        VariableData varData = (VariableData) funcVar.getData();

        AST decl = new AST(NodeKind.FUNC_DECL_NODE, new VariableData(varData.getIndex()),
                lastDeclType);

        AST parameters = this.visit(ctx.signature().parameters());
        AST result = null;
        if (ctx.signature().result() != null) {
            result = visit(ctx.signature().result());
        }
        if (parameters != null) {
            decl.addChildren(parameters);
        }
        if (result != null) {
            decl.addChildren(result);
        }

        if (ctx.block() != null) {
            AST block = this.visit(ctx.block());

            checkReturnCorrect(ctx.FUNC().getSymbol().getLine(), result, block);

            decl.addChildren(block);
        }

        return decl;
    }

    private void checkReturnCorrect(int lineNo, AST result, AST block) {

        AST returnBlock = block.getChildren().get(block.getChildren().size() - 1);
        
        int quantSignature = 0;

        if (result != null) {
            quantSignature = result.getChildren().size();
        }
        if (returnBlock.getKind() != NodeKind.RETURN_NODE && quantSignature > 0) {
            System.out.println(String.format(
                    "SEMANTIC ERROR (%d): return expression missing, return should be the last statement of the function",
                    lineNo));
            System.exit(1);
        }
        int quantReturnStmt = returnBlock.getChildren().size();

        if (quantSignature != quantReturnStmt) {
            System.out.println(String.format(
                    "SEMANTIC ERROR (%d): number of values returned (%d) different from specified on function signature (%d)",
                    lineNo, quantReturnStmt, quantSignature));
            System.exit(1);
        }

    }

    @Override
    public AST visitParameters(final ParametersContext ctx) {
        AST parameters = new AST(NodeKind.PARAMETERS_NODE, new EmptyData(),
                Type.NO_TYPE);
        for (final ParameterDeclContext paramterDeclCtx : ctx.parameterDecl()) {
            AST child = visit(paramterDeclCtx);
            if (child != null) {
                AST[] array = new AST[child.getChildren().size()];
                parameters.addChildren(child.getChildren().toArray(array));
            }
        }
        return parameters;
    }

    @Override
    public AST visitParameterDecl(final ParameterDeclContext ctx) {
        AST parameterDecl = new AST(NodeKind.PARAMETER_DECLARATION, new EmptyData(), Type.NO_TYPE);
        AST type = visit(ctx.type_());
        lastDeclType = type.getType();
        if (ctx.identifierList() != null) {
            List<TerminalNode> identifierList = ctx.identifierList().IDENTIFIER();

            int quantIdentifier = ctx.identifierList().IDENTIFIER().size();
            for (int i = 0; i < quantIdentifier; i++) {

                Token identifierSymbol = identifierList.get(i).getSymbol();
                AST newVar = this.newVar(identifierSymbol);

                parameterDecl.addChildren(newVar);
            }
        } else if (type != null) {
            parameterDecl.addChildren(new AST(NodeKind.TYPE_USE, new EmptyData(), lastDeclType));
        }
        return parameterDecl;
    }

    @Override
    public AST visitResult(final ResultContext ctx) {
        AST result = new AST(NodeKind.RESULT_NODE, new EmptyData(), Type.NO_TYPE);
        if (ctx.parameters() != null) {
            AST parameters = this.visit(ctx.parameters());
            if (parameters != null) {

                AST[] children = new AST[result.getChildren().size()];
                result.addChildren(parameters.getChildren().toArray(children));
                return result;
            }
        }
        if (ctx.type_() != null) {
            result.addChildren(this.visit(ctx.type_()));
        }
        return result;
    }

    // type_: typeName | typeLit | L_PAREN type_ R_PAREN
    @Override
    public AST visitType_(final Type_Context ctx) {
        if (ctx.typeLit() != null) {
            AST t = visit(ctx.typeLit());
            return t;
        }

        AST typeName = null;
        if (ctx.typeName() != null) {
            typeName = visit(ctx.typeName());
        }
        if (typeName != null) {
            return typeName;
        }
        if (ctx.type_() != null) {
            return visit(ctx.type_());
        }
        return null;
    }

    // typeName: qualifiedIdent | IDENTIFIER
    @Override
    public AST visitTypeName(final TypeNameContext ctx) {
        Token token = ctx.IDENTIFIER().getSymbol();
        checkTypeValid(token);

        return new AST(NodeKind.TYPE_USE, new EmptyData(), lastDeclType);
    }

    // <---------------- Visitadores dos TIPOS ---------------->

    /**
     * Método apenas valida notação decimal de inteiro
     */
    @Override
    public AST visitInteger(final IntegerContext ctx) {
        lastDeclType = Type.INT_TYPE;
        return new AST(NodeKind.INT_VAL_NODE, new IntData(Integer.parseInt(ctx.DECIMAL_LIT().getText())),
                Type.INT_TYPE);
    }

    @Override
    public AST visitString_(final String_Context ctx) {
        lastDeclType = Type.STR_TYPE;
        Integer strIndex = this.st.add(ctx.INTERPRETED_STRING_LIT().getText().replace("\"", ""));
        return new AST(NodeKind.STR_VAL_NODE, new StringData(strIndex), Type.STR_TYPE);
    }

    @Override
    public AST visitReal(final RealContext ctx) {
        lastDeclType = Type.FLOAT_TYPE;
        return new AST(NodeKind.REAL_VAL_NODE, new RealData(Double.parseDouble(ctx.FLOAT_LIT().getText())),
                Type.FLOAT_TYPE);
    }

    @Override
    public AST visitNilType(final NilTypeContext ctx) {
        return new AST(NodeKind.NIL_NODE, new EmptyData(), Type.NIL_TYPE);
    }

    @Override
    public AST visitBoolean_(Boolean_Context ctx) {
        lastDeclType = Type.BOOL_TYPE;
        return new AST(NodeKind.BOOL_VAL_NODE, new BoolData(Boolean.valueOf(ctx.boolValue.getText())), Type.BOOL_TYPE);
    }

    /*
     * <----------------- Operadores do Declaração de variáveis e assignment
     * ----------------->
     */

    @Override
    public AST visitVarDeclExplType(VarDeclExplTypeContext ctx) {
        AST type = this.visit(ctx.type_()); // visita para definir qual o tipo das variáveis
        AST variableDeclaration = new AST(NodeKind.VAR_LIST_NODE, new EmptyData(), Type.NO_TYPE);

        List<TerminalNode> identifierList = ctx.identifierList().IDENTIFIER();

        if (ctx.expressionList() != null) {
            int quantIdentifier = ctx.identifierList().IDENTIFIER().size();
            int quantExpression = ctx.expressionList().expression().size();
            checkWrongAssignCount(ctx.start.getLine(), quantIdentifier, quantExpression);

            // Para cada variável declarada atribuímos novo nó com o valor declarado
            for (int i = 0; i < quantIdentifier; i++) {
                AST assignNode = new AST(NodeKind.ASSIGN_NODE, new EmptyData(), Type.NO_TYPE);

                Token identifierSymbol = identifierList.get(i).getSymbol();
                AST newVar = this.newVar(identifierSymbol);

                AST value = this.visit(ctx.expressionList().expression(i));

                checkTypeError(identifierSymbol.getLine(), NodeKind.EQ_NODE.toString(), newVar.getType(),
                        value.getType());

                assignNode.addChildren(newVar, value);
                variableDeclaration.addChildren(assignNode);
            }
        } else {
            for (TerminalNode identifier : identifierList) {
                AST newVar = this.newVar(identifier.getSymbol());
                if (type.getType() == Type.ARRAY_TYPE) {
                    type.addChildren(newVar);
                    variableDeclaration.addChildren(type);
                } else {
                    variableDeclaration.addChildren(newVar);
                }
            }
        }
        return variableDeclaration;
    }

    @Override
    public AST visitVarDeclImplType(VarDeclImplTypeContext ctx) {
        AST variableDeclaration = new AST(NodeKind.VAR_LIST_NODE, new EmptyData(), Type.NO_TYPE);

        List<TerminalNode> identifierList = ctx.identifierList().IDENTIFIER();

        if (ctx.expressionList() != null) {
            int quantIdentifier = ctx.identifierList().IDENTIFIER().size();
            int quantExpression = ctx.expressionList().expression().size();
            checkWrongAssignCount(ctx.start.getLine(), quantIdentifier, quantExpression);

            // Para cada variável declarada atribuímos novo nó com o valor declarado
            for (int i = 0; i < quantIdentifier; i++) {
                AST assignNode = new AST(NodeKind.ASSIGN_NODE, new EmptyData(), Type.NO_TYPE);

                Token identifierSymbol = identifierList.get(i).getSymbol();
                AST value = this.visit(ctx.expressionList().expression(i));

                AST newVar = this.newVar(identifierSymbol);

                assignNode.addChildren(newVar, value);
                variableDeclaration.addChildren(assignNode);
            }
        }
        return variableDeclaration;
    }

    @Override
    public AST visitShortVarDecl(ShortVarDeclContext ctx) {
        AST variableDeclaration = new AST(NodeKind.VAR_LIST_NODE, new EmptyData(), Type.NO_TYPE);

        List<TerminalNode> identifierList = ctx.identifierList().IDENTIFIER();

        if (ctx.expressionList() != null) {
            int quantIdentifier = ctx.identifierList().IDENTIFIER().size();
            int quantExpression = ctx.expressionList().expression().size();
            checkWrongAssignCount(ctx.start.getLine(), quantIdentifier, quantExpression);

            // Para cada variável declarada atribuímos novo nó com o valor declarado
            for (int i = 0; i < quantIdentifier; i++) {
                AST assignNode = new AST(NodeKind.SHORT_VAR_DECL_NODE, new EmptyData(), Type.NO_TYPE);

                Token identifierSymbol = identifierList.get(i).getSymbol();
                AST value = this.visit(ctx.expressionList().expression(i));

                AST newVar = this.newVar(identifierSymbol);

                assignNode.addChildren(newVar, value);
                variableDeclaration.addChildren(assignNode);
            }
        }
        return variableDeclaration;
    }

    /**
     * Somente verifica atribuições identifier = expression
     */
    @Override
    public AST visitAssignment(AssignmentContext ctx) {
        AST assignmentListNode = new AST(NodeKind.ASSIGN_LIST_NODE, new EmptyData(), Type.NO_TYPE);

        int quantExpressionLeft = ctx.expressionList(0).expression().size();
        int quantExpressionRight = ctx.expressionList(1).expression().size();
        checkWrongAssignCount(ctx.start.getLine(), quantExpressionLeft, quantExpressionRight);

        for (int i = 0; i < quantExpressionLeft; i++) {
            AST assignNode = new AST(NodeKind.ASSIGN_NODE, new EmptyData(), Type.NO_TYPE);

            Token variable = ctx.expressionList(0).expression(i).getStop();
            AST variableAST = checkVar(variable);

            ExpressionContext value = ctx.expressionList(1).expression(i);
            AST valueAST = this.visit(value);

            if (valueAST == null) {
                System.out.println(String.format("SEMANTIC ERROR (%d): expression invalid",
                        variable.getLine()));
                System.exit(1);
            }

            checkTypeError(variable.getLine(), NodeKind.EQ_NODE.toString(), variableAST.getType(), valueAST.getType());

            assignNode.addChildren(variableAST, valueAST);
            assignmentListNode.addChildren(assignNode);
        }
        return assignmentListNode;
    }

    /*
     * <----------------- Operadores do Expression ----------------->
     */

    @Override
    public AST visitAddOp(AddOpContext ctx) {

        AST l = this.visit(ctx.expression(0));
        AST r = this.visit(ctx.expression(1));

        Token addOpToken = ctx.add_op;
        checkTypeError(addOpToken.getLine(), addOpToken.getText(), l.getType(), r.getType());

        return AST.newSubtree(NodeKind.fromValue(addOpToken.getText()), l.getType(), l, r);

    }

    @Override
    public AST visitMulOp(MulOpContext ctx) {

        AST l = this.visit(ctx.expression(0));
        AST r = this.visit(ctx.expression(1));

        Token mulOpToken = ctx.mul_op;
        checkTypeError(mulOpToken.getLine(), mulOpToken.getText(), l.getType(), r.getType());

        return AST.newSubtree(NodeKind.fromValue(mulOpToken.getText()), l.getType(), l, r);

    }

    @Override
    public AST visitRelOp(RelOpContext ctx) {

        AST l = this.visit(ctx.expression(0));
        AST r = this.visit(ctx.expression(1));

        Token relOpToken = ctx.rel_op;
        checkTypeError(relOpToken.getLine(), relOpToken.getText(), l.getType(), r.getType());

        return AST.newSubtree(NodeKind.fromValue(relOpToken.getText()), Type.BOOL_TYPE, l, r);

    }

    @Override
    public AST visitOperandName(OperandNameContext ctx) {

        return checkVar(ctx.IDENTIFIER(0).getSymbol());
    }

    @Override
    public AST visitImportSpec(ImportSpecContext ctx) {
        Token token = ctx.alias;
        if (token == null) {
            token = ctx.importPath().string_().INTERPRETED_STRING_LIT().getSymbol();
        }
        String text = token.getText().replace("\"", "");
        int currentLine = token.getLine();
        Integer index = vt.lookupVar(text);
        if (index != -1) {
            System.out.println(String.format(
                    "SEMANTIC ERROR (%d): alias '%s' already declared at line %d.\n",
                    currentLine, text, currentLine));
            System.exit(1);
        }
        Integer idx = vt.addVar(text, currentLine, Type.NO_TYPE);
        return new AST(NodeKind.IMPORT_SPEC, new VariableData(idx), Type.NO_TYPE);
    }

    /*
     * <----------------- Declaração de Tipos compostos ----------------->
     */

    @Override
    public AST visitArrayType(ArrayTypeContext ctx) {
        AST length = this.visit(ctx.arrayLength().expression());
        if (length == null) {
            System.out.println(String.format("SEMANTIC ERROR (%d): expression invalid",
                    ctx.L_BRACKET().getSymbol().getLine()));
            System.exit(1);
        }

        AST type = this.visit(ctx.elementType());
        if (type == null) {
            System.out.println(String.format("SEMANTIC ERROR (%d): expression invalid",
                    ctx.L_BRACKET().getSymbol().getLine()));
            System.exit(1);
        }
        if (length.getType() != Type.INT_TYPE) {
            System.out.println(String.format("SEMANTIC ERROR (%d): array length needs to be of type int",
                    ctx.L_BRACKET().getSymbol().getLine()));
            System.exit(1);
        }

        AST arrayDecl = new AST(NodeKind.ARRAY_TYPE, new EmptyData(), Type.ARRAY_TYPE);
        arrayDecl.addChildren(length, type);
        return arrayDecl;
    }

    @Override
    public AST visitBlock(BlockContext ctx) {
        AST blockNode = AST.newSubtree(NodeKind.CODE_BLOCK, Type.NO_TYPE);

        for (StatementContext statement : ctx.statementList().statement()) {
            AST child = visit(statement);
            if (child != null) {
                blockNode.addChildren(child);
            }
        }
        return blockNode;
    }

    /*
     * <----------------- Declaração de If Else ----------------->
     */

    @Override
    public AST visitIfStmt(IfStmtContext ctx) {

        AST exprNode = this.visit(ctx.expression());

        checkBoolExpr(ctx.IF().getSymbol().getLine(), NodeKind.IF_NODE.toString(), exprNode.getType());
        AST ifNode = AST.newSubtree(NodeKind.IF_NODE, Type.NO_TYPE, exprNode);

        AST thenNode = this.visit(ctx.block(0));

        ifNode.addChildren(thenNode);

        if (ctx.ELSE() != null) {

            AST elseThenNode = this.visit(ctx.block(0));

            ifNode.addChildren(elseThenNode);

        }
        return ifNode;
    }

    /*
     * <----------------- Declaração de Loops ----------------->
     */

    @Override
    public AST visitForStmt(ForStmtContext ctx) {
        if (ctx.expression() == null) {
            System.out.println(String.format("SEMANTIC ERROR (%d): expression invalid",
                    ctx.FOR().getSymbol().getLine()));
            System.exit(1);
        }

        AST exprNode = this.visit(ctx.expression());

        AST forNode = AST.newSubtree(NodeKind.FOR_NODE, Type.NO_TYPE, exprNode);

        forNode.addChildren(this.visit(ctx.block()));

        return forNode;
    }

    @Override
    public AST visitReturnStmt(ReturnStmtContext ctx) {

        AST returnAst = new AST(NodeKind.RETURN_NODE, new EmptyData(), Type.NO_TYPE);


        if (ctx.expressionList() != null) {
        for (ExpressionContext expressionContext : ctx.expressionList().expression()) {
            returnAst.addChildren(this.visit(expressionContext));
        }
        }

        return returnAst;
    }
}
