package br.ufes.edu.compiladores.checker;

import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import br.ufes.edu.compiladores.GoParser.AddOpContext;
import br.ufes.edu.compiladores.GoParser.ArgumentsContext;
import br.ufes.edu.compiladores.GoParser.ArrayLengthContext;
import br.ufes.edu.compiladores.GoParser.ArrayTypeContext;
import br.ufes.edu.compiladores.GoParser.AssignmentContext;
import br.ufes.edu.compiladores.GoParser.BlockContext;
import br.ufes.edu.compiladores.GoParser.Boolean_Context;
import br.ufes.edu.compiladores.GoParser.CompositeLitContext;
import br.ufes.edu.compiladores.GoParser.DeclarationContext;
import br.ufes.edu.compiladores.GoParser.ElementTypeContext;
import br.ufes.edu.compiladores.GoParser.ExpressionContext;
import br.ufes.edu.compiladores.GoParser.ForStmtContext;
import br.ufes.edu.compiladores.GoParser.FunctionDeclContext;
import br.ufes.edu.compiladores.GoParser.IfStmtContext;
import br.ufes.edu.compiladores.GoParser.ImportDeclContext;
import br.ufes.edu.compiladores.GoParser.ImportSpecContext;
import br.ufes.edu.compiladores.GoParser.IndexContext;
import br.ufes.edu.compiladores.GoParser.IntegerContext;
import br.ufes.edu.compiladores.GoParser.MulOpContext;
import br.ufes.edu.compiladores.GoParser.NilTypeContext;
import br.ufes.edu.compiladores.GoParser.OperandContext;
import br.ufes.edu.compiladores.GoParser.OperandNameContext;
import br.ufes.edu.compiladores.GoParser.ParameterDeclContext;
import br.ufes.edu.compiladores.GoParser.ParametersContext;
import br.ufes.edu.compiladores.GoParser.RealContext;
import br.ufes.edu.compiladores.GoParser.RelOpContext;
import br.ufes.edu.compiladores.GoParser.ResultContext;
import br.ufes.edu.compiladores.GoParser.ReturnStmtContext;
import br.ufes.edu.compiladores.GoParser.ShortVarDeclContext;
import br.ufes.edu.compiladores.GoParser.SourceFileContext;
import br.ufes.edu.compiladores.GoParser.StatementContext;
import br.ufes.edu.compiladores.GoParser.String_Context;
import br.ufes.edu.compiladores.GoParser.TypeNameContext;
import br.ufes.edu.compiladores.GoParser.Type_Context;
import br.ufes.edu.compiladores.GoParser.VarDeclExplTypeContext;
import br.ufes.edu.compiladores.GoParser.VarDeclImplTypeContext;
import br.ufes.edu.compiladores.GoParser.VisitPrimaryExprCompostaContext;
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
    protected Type lastDeclSubType; // Variável "global" com o último subtipo declarado, para tipos compostos

    private void semanticError(final String message) {
        System.out.println(message);
        System.exit(1);
    }

    // Testa se o dado token foi declarado antes.
    private AST checkVar(final Token token) {
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
    private AST newVar(final Token token) {
        final String text = token.getText();
        final int currentLine = token.getLine();
        final Integer index = vt.lookupVar(text);
        if (index != -1) {
            System.out.println(String.format(
                    "SEMANTIC ERROR (%d): variable '%s' already declared at line %d.\n",
                    currentLine, text, currentLine));
            System.exit(1);
        }
        Integer idx;
        Type t;
        if (lastDeclType == Type.ARRAY_TYPE) {
            idx = vt.addVar(text, currentLine, lastDeclType, lastDeclSubType);
            t = vt.getType(idx);
        } else {
            idx = vt.addVar(text, currentLine, lastDeclType);
            t = vt.getType(idx);
        }

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
            case ARRAY_TYPE:
                return new AST(NodeKind.VAR_DECL_NODE, new VariableData(idx), lastDeclType);
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

    private void checkBoolExpr(final int lineNo, final String cmd, final Type t) {
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

    private void checkWrongAssignCount(final int lineNo, final int quantExpected, final int quantReal) {
        if (quantExpected != quantReal) {
            System.out.println(String.format("SEMANTIC ERROR (%d): cannot initialize '%d' variables with '%d' values",
                    lineNo, quantExpected, quantReal));
            System.exit(1);
        }
    }

    private void checkReturnCorrect(final int lineNo, final AST result, final AST block) {
        final AST returnBlock = block.getChildren().get(block.getChildren().size() - 1);
        int quantSignature = 0;

        if (result != null) {
            quantSignature = result.getChildren().size();
        }

        if (returnBlock.getKind() != NodeKind.RETURN_NODE && quantSignature == 0) {
            return;
        }

        if (returnBlock.getKind() != NodeKind.RETURN_NODE && quantSignature > 0) {
            System.out.println(String.format(
                    "SEMANTIC ERROR (%d): return expression missing, return should be the last statement of the function",
                    lineNo));
            System.exit(1);
        }
        final int quantReturnStmt = returnBlock.getChildren().size();

        if (quantSignature != quantReturnStmt) {
            System.out.println(String.format(
                    "SEMANTIC ERROR (%d): number of values returned (%d) different from specified on function signature (%d)",
                    lineNo, quantReturnStmt, quantSignature));
            System.exit(1);
        }
    }

    private void checkParametersTypeFromFunctionCall(final AST funcAST, final AST var) {
        for (AST child : funcAST.getChildren()) {
            if (child.getKind() == NodeKind.PARAMETERS_NODE) {
                Integer totalParameters = child.getChildren().size();
                Integer totalArguments = var.getChildren().size();
                if (totalParameters != totalArguments) {
                    final String message = String.format(
                            "SEMANTIC ERROR (%d): Wrong number of arguments from function call expected (%d) but got (%d)",
                            0, totalParameters, totalArguments);
                    semanticError(message);
                }

                for (int i = 0; i < totalArguments; i++) {
                    Optional<AST> ParameterNodeOpt = child.getChild(i);
                    Optional<AST> argumentNodeOpt = var.getChild(i);
                    if (ParameterNodeOpt.isPresent() && argumentNodeOpt.isPresent()) {
                        AST parameterNode = ParameterNodeOpt.get();
                        AST argumentNode = argumentNodeOpt.get();
                        
                        Type paramType = parameterNode.getType();
                        Type argumentType = argumentNode.getType();
                        
                        if(argumentNode.getType() == Type.ARRAY_TYPE) {
                            VariableData argumentVarData = (VariableData) argumentNode.getData();
                            argumentType = vt.getSubType(argumentVarData.getIndex());
                        }

                        if(paramType != argumentType) {
                            final String message = String.format(
                                    "SEMANTIC ERROR (%d): Wrong type of argument from function call. Expected (%s) but got (%s)",
                                    0, parameterNode.getType().toString(), argumentNode.getType().toString());
                            semanticError(message);
                        }
                    }
                }

            }
        }
    }

    private void checkReturnTypeFromFunctionCall(Integer index, Integer line, final AST leftExpressionAST, final AST functionUseAST) {
        for (AST funcChild : functionUseAST.getChildren()) {
            if (funcChild.getKind() == NodeKind.VAR_USE_NODE) {
                VariableData funcVarData = (VariableData) funcChild.getData();
                AST functionAST = vt.getAstNode(funcVarData.getIndex());

                for (AST functionDeclChild : functionAST.getChildren()) {
                    if (functionDeclChild.getKind() == NodeKind.RESULT_NODE) {
                        Optional<AST> resultOpt = functionDeclChild.getChild(index);
                        if (resultOpt.isPresent()) {
                            AST result = resultOpt.get();
                            Type resultType = result.getType();
                            Type varType = leftExpressionAST.getType();

                            if(varType == Type.ARRAY_TYPE) {
                                VariableData varData = (VariableData) leftExpressionAST.getData();
                                varType = vt.getSubType(varData.getIndex());
                            }
                            
                            if(resultType != varType) {
                                final String message = String.format(
                                "SEMANTIC ERROR (%d): Wrong type of return, expected (%s) but got (%s)",
                                0, resultType.toString(), varType);
                                semanticError(message);
                            }
                        } 
                    }
                }
            }
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

        for (final ImportDeclContext importDeclContext : ctx.importDecl()) {
            final AST importDecl = this.visit(importDeclContext);
            if (importDecl != null) {
                this.root.addChildren(importDecl);
            }
        }

        for (final DeclarationContext declarationCtx : ctx.declaration()) {
            final AST child = visit(declarationCtx.varDecl());
            if (child != null) {
                this.root.addChildren(child);
            }
        }

        for (final FunctionDeclContext functionDeclContext : ctx.functionDecl()) {
            final AST functionDecl = visit(functionDeclContext);
            if (functionDecl != null) {
                this.root.addChildren(functionDecl);
            }
        }

        return this.root;
    }

    // <---------------- Visitadores dos FUNÇÕES ---------------->
    @Override
    public AST visitFunctionDecl(final FunctionDeclContext ctx) {
        lastDeclType = Type.FUNC_TYPE;
        final AST funcVar = newVar(ctx.IDENTIFIER().getSymbol());

        vt.openScope(ctx.IDENTIFIER().getSymbol().getText());

        final VariableData varData = (VariableData) funcVar.getData();

        final AST decl = new AST(NodeKind.FUNC_DECL_NODE, new VariableData(varData.getIndex()),
                lastDeclType);

        final AST parameters = this.visit(ctx.signature().parameters());
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
            final AST block = this.visit(ctx.block());

            checkReturnCorrect(ctx.FUNC().getSymbol().getLine(), result, block);

            decl.addChildren(block);
        }

        vt.setAstNode(varData.getIndex(), decl);

        vt.closeScope();
        return decl;
    }

    @Override
    public AST visitParameters(final ParametersContext ctx) {
        final AST parameters = new AST(NodeKind.PARAMETERS_NODE, new EmptyData(),
                Type.NO_TYPE);
        for (final ParameterDeclContext paramterDeclCtx : ctx.parameterDecl()) {
            final AST child = visit(paramterDeclCtx);
            if (child != null) {
                final AST[] array = new AST[child.getChildren().size()];
                parameters.addChildren(child.getChildren().toArray(array));
            }
        }
        return parameters;
    }

    @Override
    public AST visitParameterDecl(final ParameterDeclContext ctx) {
        final AST parameterDecl = new AST(NodeKind.PARAMETER_DECLARATION, new EmptyData(), Type.NO_TYPE);
        final AST type = visit(ctx.type_());
        lastDeclType = type.getType();
        if (ctx.identifierList() != null) {
            final List<TerminalNode> identifierList = ctx.identifierList().IDENTIFIER();

            final int quantIdentifier = ctx.identifierList().IDENTIFIER().size();
            for (int i = 0; i < quantIdentifier; i++) {

                final Token identifierSymbol = identifierList.get(i).getSymbol();
                final AST newVar = this.newVar(identifierSymbol);

                parameterDecl.addChildren(newVar);
            }
        } else if (type != null) {
            parameterDecl.addChildren(new AST(NodeKind.TYPE_USE, new EmptyData(), lastDeclType));
        }
        return parameterDecl;
    }

    @Override
    public AST visitResult(final ResultContext ctx) {
        final AST result = new AST(NodeKind.RESULT_NODE, new EmptyData(), Type.NO_TYPE);
        if (ctx.parameters() != null) {
            final AST parameters = this.visit(ctx.parameters());
            if (parameters != null) {
                Integer resultSize = parameters.getChildren().size();
                if (resultSize > 1) {
                    Integer line = ctx.parameters().L_PAREN().getSymbol().getLine();
                    final String message = String.format(
                        "SEMANTIC ERROR (%d): Cannot have more than one return but got (%d)",
                        line, resultSize);
                    semanticError(message);
                }
                final AST[] children = new AST[result.getChildren().size()];
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
            return visit(ctx.typeLit());
        }
        if (ctx.typeName() != null) {
            return visit(ctx.typeName());
        }
        if (ctx.type_() != null) {
            return visit(ctx.type_());
        }
        return null;
    }

    // typeName: qualifiedIdent | IDENTIFIER
    @Override
    public AST visitTypeName(final TypeNameContext ctx) {
        final Token token = ctx.IDENTIFIER().getSymbol();
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
        final Integer strIndex = this.st.add(ctx.INTERPRETED_STRING_LIT().getText().replace("\"", ""));
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
    public AST visitBoolean_(final Boolean_Context ctx) {
        lastDeclType = Type.BOOL_TYPE;
        return new AST(NodeKind.BOOL_VAL_NODE, new BoolData(Boolean.valueOf(ctx.boolValue.getText())), Type.BOOL_TYPE);
    }

    /*
     * <-------- Operadores do Declaração de variáveis e assignment -------->
     */

    @Override
    public AST visitVarDeclExplType(final VarDeclExplTypeContext ctx) {
        final AST type = this.visit(ctx.type_()); // visita para definir qual o tipo das variáveis
        final AST variableDeclaration = new AST(NodeKind.VAR_LIST_NODE, new EmptyData(), Type.NO_TYPE);

        final List<TerminalNode> identifierList = ctx.identifierList().IDENTIFIER();

        if (ctx.expressionList() != null) {
            final int quantIdentifier = ctx.identifierList().IDENTIFIER().size();
            final int quantExpression = ctx.expressionList().expression().size();
            checkWrongAssignCount(ctx.start.getLine(), quantIdentifier, quantExpression);

            // Para cada variável declarada atribuímos novo nó com o valor declarado
            for (int i = 0; i < quantIdentifier; i++) {
                final AST assignNode = new AST(NodeKind.ASSIGN_NODE, new EmptyData(), Type.NO_TYPE);

                final Token identifierSymbol = identifierList.get(i).getSymbol();
                final AST newVar = this.newVar(identifierSymbol);

                final ExpressionContext value = ctx.expressionList().expression(i);
                final AST rightExpressionAST = this.visit(value);
                
                // Verificações de atribuição
                this.verifyAssignConstraints(identifierSymbol.getLine(), newVar, rightExpressionAST);
                
                // Tem que lidar com Arrays de alguma forma
                if (newVar.getType() == Type.ARRAY_TYPE || rightExpressionAST.getType() == Type.ARRAY_TYPE) {
                    this.visitArrayAssignment(i, identifierSymbol.getLine(), assignNode, variableDeclaration, newVar, rightExpressionAST);
                }  else if(rightExpressionAST.getKind() == NodeKind.FUNC_USE_NODE) { 
                    // Não tem array mas tem função!
                    this.visitVariableAssigmentWithFunctionReturn(i, identifierSymbol, newVar, rightExpressionAST, assignNode, variableDeclaration);
                } else {
                    // Somente variáveis base
                    this.visitVariableAssignment(i, identifierSymbol, newVar, rightExpressionAST, assignNode, variableDeclaration);
                }

            }
        } else {
            for (final TerminalNode identifier : identifierList) {
                final AST newVar = this.newVar(identifier.getSymbol());
                if (type.getType() == Type.ARRAY_TYPE) {
                    newVar.addChildren(type.getChildren().toArray(new AST[type.getChildren().size()]));
                }
                variableDeclaration.addChildren(newVar);
            }
        }
        return variableDeclaration;
    }

    @Override
    public AST visitVarDeclImplType(final VarDeclImplTypeContext ctx) {
        final AST variableDeclaration = new AST(NodeKind.VAR_LIST_NODE, new EmptyData(), Type.NO_TYPE);

        final List<TerminalNode> identifierList = ctx.identifierList().IDENTIFIER();

        if (ctx.expressionList() != null) {
            final int quantIdentifier = ctx.identifierList().IDENTIFIER().size();
            final int quantExpression = ctx.expressionList().expression().size();
            checkWrongAssignCount(ctx.start.getLine(), quantIdentifier, quantExpression);

            // Para cada variável declarada atribuímos novo nó com o valor declarado
            for (int i = 0; i < quantIdentifier; i++) {
                final AST assignNode = new AST(NodeKind.ASSIGN_NODE, new EmptyData(), Type.NO_TYPE);

                final Token identifierSymbol = identifierList.get(i).getSymbol();
                final AST value = this.visit(ctx.expressionList().expression(i));
                if (value.getType() == Type.ARRAY_TYPE) {
                    final String message = String.format("SEMANTIC ERROR (%d): Cannot declare variable implicitly with Array",
                    identifierSymbol.getLine());
                    semanticError(message);
                }
                if (value.getKind() == NodeKind.FUNC_USE_NODE) {
                    final String message = String.format("SEMANTIC ERROR (%d): Cannot declare variable implicitly with Function",
                    identifierSymbol.getLine());
                    semanticError(message);
                }

                final AST newVar = this.newVar(identifierSymbol);

                assignNode.addChildren(newVar, value);
                variableDeclaration.addChildren(assignNode);
            }
        }
        return variableDeclaration;
    }

    @Override
    public AST visitShortVarDecl(final ShortVarDeclContext ctx) {
        final AST variableDeclaration = new AST(NodeKind.VAR_LIST_NODE, new EmptyData(), Type.NO_TYPE);

        final List<TerminalNode> identifierList = ctx.identifierList().IDENTIFIER();

        if (ctx.expressionList() != null) {
            final int quantIdentifier = ctx.identifierList().IDENTIFIER().size();
            final int quantExpression = ctx.expressionList().expression().size();
            checkWrongAssignCount(ctx.start.getLine(), quantIdentifier, quantExpression);

            // Para cada variável declarada atribuímos novo nó com o valor declarado
            for (int i = 0; i < quantIdentifier; i++) {
                final AST assignNode = new AST(NodeKind.SHORT_VAR_DECL_NODE, new EmptyData(), Type.NO_TYPE);

                final Token identifierSymbol = identifierList.get(i).getSymbol();
                final AST value = this.visit(ctx.expressionList().expression(i));

                if (value.getType() == Type.ARRAY_TYPE) {
                    final String message = String.format("SEMANTIC ERROR (%d): Cannot declare variable implicitly with Array",
                    identifierSymbol.getLine());
                    semanticError(message);
                }
                if (value.getKind() == NodeKind.FUNC_USE_NODE) {
                    final String message = String.format("SEMANTIC ERROR (%d): Cannot declare variable implicitly with Function",
                    identifierSymbol.getLine());
                    semanticError(message);
                }

                final AST newVar = this.newVar(identifierSymbol);

                assignNode.addChildren(newVar, value);
                variableDeclaration.addChildren(assignNode);
            }
        }
        return variableDeclaration;
    }

    public void visitArrayAssignment(Integer index, Integer line, AST assignNode, AST assignmentListNode, AST leftExpressionAST, AST rightExpressionAST) {
        if (leftExpressionAST.getType() == Type.ARRAY_TYPE && rightExpressionAST.getType() == Type.ARRAY_TYPE) {
            final VariableData leftArrayData = (VariableData) leftExpressionAST.getData();
            if (!vt.varExists(leftArrayData.getIndex())) { // Variável inexistente
                final String message = String.format("SEMANTIC ERROR (%d): Variable not declared",
                        line);
                semanticError(message);
            }
            final Type LeftArrayVariableType = vt.getSubType(leftArrayData.getIndex());
            final VariableData rightArrayData = (VariableData) leftExpressionAST.getData();
            if (!vt.varExists(rightArrayData.getIndex())) { // Variável inexistente
                final String message = String.format("SEMANTIC ERROR (%d): Variable not declared",
                        line);
                semanticError(message);
            }
            final Type RightArrayVariableType = vt.getSubType(rightArrayData.getIndex());

            checkTypeError(line, NodeKind.ASSIGN_NODE.toString(), LeftArrayVariableType,
                    RightArrayVariableType);

            assignNode.addChildren(leftExpressionAST); // Array no lado esquerdo, setDados
            assignNode.addChildren(rightExpressionAST);
            assignmentListNode.addChildren(assignNode);
        }

        // Expressão Primária de Array na Esquerda
        else if (leftExpressionAST.getType() == Type.ARRAY_TYPE) {
            final VariableData arrayData = (VariableData) leftExpressionAST.getData();
            if (!vt.varExists(arrayData.getIndex())) { // Variável inexistente
                final String message = String.format("SEMANTIC ERROR (%d): Variable not declared",
                        line);
                semanticError(message);
            }
            final Type ArrayVariableType = vt.getSubType(arrayData.getIndex());
            if (rightExpressionAST.getKind() == NodeKind.FUNC_USE_NODE) {
                checkReturnTypeFromFunctionCall(index, line, leftExpressionAST, rightExpressionAST);
            } else {
                checkTypeError(line, NodeKind.ASSIGN_NODE.toString(), ArrayVariableType, rightExpressionAST.getType());
            }
            assignNode.addChildren(leftExpressionAST); // Array no lado esquerdo, setDados
            assignNode.addChildren(rightExpressionAST);
            assignmentListNode.addChildren(assignNode);

            // Expressão Primária de Array na Direita
        } else if (rightExpressionAST.getType() == Type.ARRAY_TYPE) {
            final VariableData arrayData = (VariableData) rightExpressionAST.getData();
            final Type ArrayVariableType = vt.getSubType(arrayData.getIndex());
            checkTypeError(line, NodeKind.ASSIGN_NODE.toString(), ArrayVariableType,
                    leftExpressionAST.getType());

            assignNode.addChildren(leftExpressionAST); // Array na direita, getDados
            assignNode.addChildren(rightExpressionAST);
            assignmentListNode.addChildren(assignNode);
        }
    }

    public void verifyAssignConstraints(Integer line, AST leftExpressionAST, AST rightExpressionAST) {
        final NodeKind leftKind = leftExpressionAST.getKind();
        if (!NodeKind.isVariable(leftKind)) {
            final String message = String.format(
                    "SEMANTIC ERROR (%d): Cannot assign to an expression that is not a variable",
                    line, leftExpressionAST.getData().toString());
            semanticError(message);
        }
        if (rightExpressionAST == null) {
            final String message = String.format("SEMANTIC ERROR (%d): expression invalid",
                    line);
            semanticError(message);
        }
    }

    public void visitVariableAssignment(Integer index, Token variable, AST leftExpressionAST, AST rightExpressionAST, AST assignNode, AST assignmentListNode) {
        final AST VariableAST = checkVar(variable);
        if (VariableAST.getKind() != NodeKind.VAR_DECL_NODE && VariableAST.getKind() != NodeKind.VAR_USE_NODE) {
            final String message = String.format("SEMANTIC ERROR (%d): Left expression should be a Variable",
                    variable.getLine());
            semanticError(message);
        }
        checkTypeError(variable.getLine(), NodeKind.ASSIGN_NODE.toString(), VariableAST.getType(),
                rightExpressionAST.getType());
        assignNode.addChildren(VariableAST, rightExpressionAST);
        assignmentListNode.addChildren(assignNode);
    }

    public void visitVariableAssigmentWithFunctionReturn(Integer index, Token variable, AST leftExpressionAST, AST rightExpressionAST, AST assignNode, AST assignmentListNode) {
        final AST VariableAST = checkVar(variable);
        checkReturnTypeFromFunctionCall(index, variable.getLine(), leftExpressionAST, rightExpressionAST);
        assignNode.addChildren(VariableAST, rightExpressionAST);
        assignmentListNode.addChildren(assignNode);
    }

    /**
     * Somente verifica atribuições identifier = expression
     */
    @Override
    public AST visitAssignment(final AssignmentContext ctx) {
        final AST assignmentListNode = new AST(NodeKind.ASSIGN_LIST_NODE, new EmptyData(), Type.NO_TYPE);

        final int quantExpressionLeft = ctx.expressionList(0).expression().size();
        final int quantExpressionRight = ctx.expressionList(1).expression().size();
        checkWrongAssignCount(ctx.start.getLine(), quantExpressionLeft, quantExpressionRight);
        
        for (int i = 0; i < quantExpressionLeft; i++) {
            final AST assignNode = new AST(NodeKind.ASSIGN_NODE, new EmptyData(), Type.NO_TYPE);
            
            final AST leftExpressionAST = this.visit(ctx.expressionList(0).expression(i));
            final ExpressionContext value = ctx.expressionList(1).expression(i);
            final AST rightExpressionAST = this.visit(value);
            final Token variable = ctx.expressionList(0).expression(i).getStop();
            
            // Verificações de atribuição
            this.verifyAssignConstraints(variable.getLine(), leftExpressionAST, rightExpressionAST);
            
            // Tem que lidar com Arrays de alguma forma
            if (leftExpressionAST.getType() == Type.ARRAY_TYPE || rightExpressionAST.getType() == Type.ARRAY_TYPE) {
                this.visitArrayAssignment(i, variable.getLine(), assignNode, assignmentListNode, leftExpressionAST, rightExpressionAST);
            }  else if(rightExpressionAST.getKind() == NodeKind.FUNC_USE_NODE) { 
                // Não tem array mas tem função!
                this.visitVariableAssigmentWithFunctionReturn(i, variable, leftExpressionAST, rightExpressionAST, assignNode, assignmentListNode);
            } else {
                // Somente variáveis base
                this.visitVariableAssignment(i, variable, leftExpressionAST, rightExpressionAST, assignNode, assignmentListNode);
            }
        }
        return assignmentListNode;
    }

    /*
     * <----------------- Operadores do Expression ----------------->
     */
    @Override
    public AST visitVisitPrimaryExprComposta(final VisitPrimaryExprCompostaContext ctx) {
        if (ctx.index() != null) { // Para o caso de acesso à espaço de um array
            final AST index = visit(ctx.index());
            final AST primaryExprNode = visit(ctx.primaryExpr());
            if (primaryExprNode.getType() == Type.ARRAY_TYPE) {

                primaryExprNode.addChildren(index);
                return primaryExprNode;
            }
            return primaryExprNode;
        } else if (ctx.arguments() != null) { // No caso de chamada de função
            final AST functionAST = this.visit(ctx.primaryExpr());
            final AST argumentsAST = this.visit(ctx.arguments());

            VariableData funcVarData = (VariableData) functionAST.getData();
            AST functionDeclNode = vt.getAstNode(funcVarData.getIndex());

            checkParametersTypeFromFunctionCall(functionDeclNode, argumentsAST);
            String textFunc = this.vt.getName(((VariableData) functionAST.getData()).getIndex());
            if ("Println".equals(textFunc)) {
                return AST.newSubtree(NodeKind.READ_OPERATION, Type.NO_TYPE, functionAST, argumentsAST);
            }
            if ("Scanln".equals(textFunc)) {
                return AST.newSubtree(NodeKind.WRITE_OPERATION, Type.NO_TYPE, functionAST, argumentsAST);
            }
            return AST.newSubtree(NodeKind.FUNC_USE_NODE, Type.NO_TYPE, functionAST, argumentsAST);

        } else {
            return this.visit(ctx);
        }
    }

    @Override
    public AST visitArguments(ArgumentsContext ctx) {

        AST argumentsAST = AST.newSubtree(NodeKind.ARGUMENTS_NODE, Type.NO_TYPE);

        if (ctx.expressionList() != null) {
            for (ExpressionContext expression : ctx.expressionList().expression()) {

                argumentsAST.addChildren(this.visit(expression));
            }
        }
        return argumentsAST;
    }

    @Override
    public AST visitOperand(final OperandContext ctx) {
        if (ctx.literal() != null) {
            return this.visit(ctx.literal());
        }
        if (ctx.operandName() != null) {
            return this.visit(ctx.operandName());
        }
        if (ctx.expression() != null) {
            final AST exp = this.visit(ctx.expression());
            final AST parenthesis = new AST(exp.getKind(), new EmptyData(), lastDeclType);
            final AST[] expChild = new AST[exp.getChildren().size()];
            parenthesis.addChildren(exp.getChildren().toArray(expChild));
            return parenthesis;
        }
        return null;
    }

    @Override
    public AST visitAddOp(final AddOpContext ctx) {

        final AST l = this.visit(ctx.expression(0));
        final AST r = this.visit(ctx.expression(1));
        final Token addOpToken = ctx.add_op;

        Type lType = l.getType();
        Type rType = r.getType();
        if (lType == Type.ARRAY_TYPE) {
            final VariableData varData = (VariableData) l.getData();
            lType = vt.getSubType(varData.getIndex());
        }
        if (rType == Type.ARRAY_TYPE) {
            final VariableData varData = (VariableData) r.getData();
            rType = vt.getSubType(varData.getIndex());
        }
        checkTypeError(addOpToken.getLine(), addOpToken.getText(), lType, rType);

        lastDeclType = lType;
        return AST.newSubtree(NodeKind.fromValue(addOpToken.getText()), lType, l, r);

    }

    @Override
    public AST visitMulOp(final MulOpContext ctx) {

        final AST l = this.visit(ctx.expression(0));
        final AST r = this.visit(ctx.expression(1));

        final Token mulOpToken = ctx.mul_op;

        Type lType = l.getType();
        Type rType = r.getType();

        if (lType == Type.ARRAY_TYPE) {
            final VariableData varData = (VariableData) l.getData();
            lType = vt.getSubType(varData.getIndex());
        }
        if (rType == Type.ARRAY_TYPE) {
            final VariableData varData = (VariableData) r.getData();
            rType = vt.getSubType(varData.getIndex());
        }

        checkTypeError(mulOpToken.getLine(), mulOpToken.getText(), lType, rType);
        lastDeclType = lType;
        return AST.newSubtree(NodeKind.fromValue(mulOpToken.getText()), lType, l, r);

    }

    @Override
    public AST visitRelOp(final RelOpContext ctx) {

        final AST l = this.visit(ctx.expression(0));
        final AST r = this.visit(ctx.expression(1));

        final Token relOpToken = ctx.rel_op;
        Type lType = l.getType();
        Type rType = r.getType();

        if (lType == Type.ARRAY_TYPE) {
            final VariableData varData = (VariableData) l.getData();
            lType = vt.getSubType(varData.getIndex());
        }
        if (rType == Type.ARRAY_TYPE) {
            final VariableData varData = (VariableData) r.getData();
            rType = vt.getSubType(varData.getIndex());
        }
        checkTypeError(relOpToken.getLine(), relOpToken.getText(), lType, rType);

        lastDeclType = Type.BOOL_TYPE;
        return AST.newSubtree(NodeKind.fromValue(relOpToken.getText()), Type.BOOL_TYPE, l, r);

    }

    @Override
    public AST visitOperandName(final OperandNameContext ctx) {
        if ("fmt".equals(ctx.IDENTIFIER(0).getSymbol().getText())) {
            return checkVar(ctx.IDENTIFIER(1).getSymbol());
        }
        return checkVar(ctx.IDENTIFIER(0).getSymbol());
    }

    @Override
    public AST visitImportSpec(final ImportSpecContext ctx) {
        Token token = ctx.alias;
        if (token == null) {
            token = ctx.importPath().string_().INTERPRETED_STRING_LIT().getSymbol();
        }
        final String text = token.getText().replace("\"", "");
        final int currentLine = token.getLine();
        final Integer index = vt.lookupVar(text);
        if (index != -1) {
            System.out.println(String.format(
                    "SEMANTIC ERROR (%d): alias '%s' already declared at line %d.\n",
                    currentLine, text, currentLine));
            System.exit(1);
        }

        final Integer idx = vt.addVar(text, currentLine, Type.NO_TYPE);
        AST importSpectAst = new AST(NodeKind.IMPORT_SPEC, new VariableData(idx), Type.NO_TYPE);

        declaraIO(text, currentLine, importSpectAst);
        return importSpectAst;
    }

    private void declaraIO(final String text, final int currentLine, AST importSpectAst) {
        if ("fmt".equals(text)) {

            int indexPrintLn = vt.addVar("Println", currentLine, Type.NO_TYPE);
            AST funcPrintAST = new AST(NodeKind.FUNC_DECL_NODE,
                    new VariableData(indexPrintLn),
                    Type.NO_TYPE);

            vt.openScope("Println");
            AST parametersAST = AST.newSubtree(NodeKind.PARAMETERS_NODE, Type.NO_TYPE);

            parametersAST.addChild(new AST(NodeKind.VAR_DECL_NODE,
                    new VariableData(vt.addVar("arg", currentLine, Type.STR_TYPE)), Type.STR_TYPE));

            AST resultAST = AST.newSubtree(NodeKind.RESULT_NODE, Type.NO_TYPE);

            funcPrintAST.addChildren(parametersAST, resultAST);
            vt.setAstNode(indexPrintLn, funcPrintAST);

            vt.closeScope();

            int indexScanln = vt.addVar("Scanln", currentLine, Type.NO_TYPE);
            AST funcScanAST = new AST(NodeKind.FUNC_DECL_NODE,
                    new VariableData(indexScanln),
                    Type.NO_TYPE);

            vt.openScope("Scanln");
            parametersAST = AST.newSubtree(NodeKind.PARAMETERS_NODE, Type.NO_TYPE);

            parametersAST.addChild(new AST(NodeKind.VAR_DECL_NODE,
                    new VariableData(vt.addVar("arg", currentLine, Type.STR_TYPE)), Type.STR_TYPE));

            resultAST = AST.newSubtree(NodeKind.RESULT_NODE, Type.NO_TYPE);

            funcScanAST.addChildren(parametersAST, resultAST);
            vt.setAstNode(indexScanln, funcScanAST);

            vt.closeScope();
            importSpectAst.addChildren(funcPrintAST, funcScanAST);
        }
    }

    /*
     * <----------------- Declaração de Tipos compostos ----------------->
     */

    @Override
    public AST visitArrayType(final ArrayTypeContext ctx) {

        final AST type = this.visit(ctx.elementType());
        lastDeclSubType = lastDeclType;

        final AST length = this.visit(ctx.arrayLength());
        lastDeclType = Type.ARRAY_TYPE;
        return AST.newSubtree(NodeKind.ARRAY_TYPE, Type.ARRAY_TYPE, length, type);
    }

    @Override
    public AST visitArrayLength(final ArrayLengthContext ctx) {
        final AST length = this.visit(ctx.expression());

        if (length == null) {
            System.out.println(String.format("SEMANTIC ERROR (%d): expression invalid",
                    ctx.getStart().getLine()));
            System.exit(1);
        }

        if (length.getType() != Type.INT_TYPE) {
            System.out.println(String.format("SEMANTIC ERROR (%d): array length needs to be of type int",
                    ctx.getStart().getLine()));
            System.exit(1);
        }

        return AST.newSubtree(NodeKind.ARRAY_LENGTH_NODE, Type.NO_TYPE, length);
    }

    @Override
    public AST visitIndex(final IndexContext ctx) {
        final AST exp = visit(ctx.expression());

        if (exp.getType() != Type.INT_TYPE) {
            System.out.println(String.format("SEMANTIC ERROR (%d): Index needs to be of type int",
                    ctx.getStart().getLine()));
            System.exit(1);
        }

        final AST index = new AST(NodeKind.INDEX_NODE, new EmptyData(), Type.INT_TYPE);

        index.addChildren(exp);
        return index;
    }

    @Override
    public AST visitElementType(final ElementTypeContext ctx) {

        final AST type = this.visit(ctx.type_());
        return AST.newSubtree(NodeKind.ARRAY_ELEMENT_TYPE_NODE, type.getType());
    }

    @Override
    public AST visitBlock(final BlockContext ctx) {
        final AST blockNode = AST.newSubtree(NodeKind.CODE_BLOCK, Type.NO_TYPE);

        for (final StatementContext statement : ctx.statementList().statement()) {
            final AST child = visit(statement);
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
    public AST visitIfStmt(final IfStmtContext ctx) {

        final AST exprNode = this.visit(ctx.expression());

        checkBoolExpr(ctx.IF().getSymbol().getLine(), NodeKind.IF_NODE.toString(), exprNode.getType());
        final AST ifNode = AST.newSubtree(NodeKind.IF_NODE, Type.NO_TYPE, exprNode);

        final AST thenNode = this.visit(ctx.block(0));

        ifNode.addChildren(thenNode);

        if (ctx.ELSE() != null) {

            final AST elseThenNode = this.visit(ctx.block(0));

            ifNode.addChildren(elseThenNode);

        }
        return ifNode;
    }

    /*
     * <----------------- Declaração de Loops ----------------->
     */

    @Override
    public AST visitForStmt(final ForStmtContext ctx) {
        if (ctx.expression() == null) {
            System.out.println(String.format("SEMANTIC ERROR (%d): expression invalid",
                    ctx.FOR().getSymbol().getLine()));
            System.exit(1);
        }

        final AST exprNode = this.visit(ctx.expression());

        final AST forNode = AST.newSubtree(NodeKind.FOR_NODE, Type.NO_TYPE, exprNode);

        forNode.addChildren(this.visit(ctx.block()));

        return forNode;
    }

    @Override
    public AST visitReturnStmt(final ReturnStmtContext ctx) {

        final AST returnAst = new AST(NodeKind.RETURN_NODE, new EmptyData(), Type.NO_TYPE);

        if (ctx.expressionList() != null) {
            for (final ExpressionContext expressionContext : ctx.expressionList().expression()) {
                returnAst.addChildren(this.visit(expressionContext));
            }
        }

        return returnAst;
    }

    @Override
    public AST visitCompositeLit(final CompositeLitContext ctx) {
        final AST type = this.visit(ctx.literalType());

        return AST.newSubtree(NodeKind.ASSIGN_NODE, Type.NO_TYPE, type);
    }

    public AST getAST() {
        return this.root;
    }

    public VarTable getVarTable() {
        return this.vt;
    }
    
    public StrTable getStrTable() {
        return this.st;
    }

}
