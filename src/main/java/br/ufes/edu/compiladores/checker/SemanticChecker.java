package br.ufes.edu.compiladores.checker;

import br.ufes.edu.compiladores.GoParser.*;
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

import java.util.List;

import br.ufes.edu.compiladores.utils.TypeUtil;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SemanticChecker extends GoParserBaseVisitor<AST> {

    protected Logger logger = LogManager.getLogger(SemanticChecker.class);
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
            this.logger.error(
                    "SEMANTIC ERROR ({}): variable '{}' was not declared.\n",
                    line, text);
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
            // int originalLine = entry.getLine();
            logger.error(
                    "SEMANTIC ERROR ({}): variable '{}' already declared at line {}.\n",
                    currentLine, text, currentLine);
            System.exit(1);
        }
        Integer idx = vt.addVar(text, currentLine, lastDeclType);
        // System.out.println("Index criado da variável: " + idx);
        Type t = vt.getType(idx);
        // System.out.println("Text da variável: " + text);
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
                throw new Error();
        }
    }

    // ----------------------------------------------------------------------------
    // Type checking and inference.

    protected void typeError(final int lineNo, final String op, final Type t1, final Type t2) {
        this.logger.error("SEMANTIC ERROR ({}): incompatible types for operator '{}', LHS is '{}' and RHS is '{}'.\n",
                lineNo, op, t1, t2);
        System.exit(1);
    }

    // Exibe o conteúdo das tabelas em stdout.
    public void printTables() {
        System.out.print("\n\n");
        System.out.print(st);
        System.out.print("\n\n");
        System.out.print(vt);
        System.out.print("\n\n");
    }

    // Exibe a AST no formato DOT em stderr.
    public void printAST() {
        AST.printDot(root, vt);
    }

    // Checkers
    // ----------------------------------------------------------------------------

    // private void checkBoolExpr(final int lineNo, final String cmd, final Type t)
    // {
    // if (t != Type.BOOL_TYPE) {
    // final String typeText = t.toString();
    // final String boolString = Type.BOOL_TYPE.toString();
    // this.logger.error("SEMANTIC ERROR ({}): conditional expression in '{}' is
    // '{}' instead of '{}'.\n",
    // lineNo, cmd, typeText, boolString);
    // System.exit(1);
    // }
    // }

    private void checkTypeValid(final Token token) {
        final String text = token.getText();
        final int line = token.getLine();
        final Type t = TypeUtil.getTypeByIdentifier(text);
        if (t == null) {
            this.logger.error("SEMANTIC ERROR ({}): Type '{}' doesn't exist.\n",
                    line, text);
            System.exit(1);
        }
        this.lastDeclType = t;
    }

    private void checkWrongAssignCount(int lineNo, int quantExpected, int quantReal) {
        if (quantExpected != quantReal) {
            logger.error("SEMANTIC ERROR ({}): cannot initialize '{}' variables with '{}' values",
                    lineNo, quantExpected, quantReal);
            System.exit(1);
        }
    }

    // private void checkAssign(int lineNo, Type l, Type r) {
    // if (l != r) {
    // typeError(lineNo, ":=", l, r);
    // }

    // }

    // ----------------------------------------------------------------------------
    // Visitadores.

    // Regra inicial!
    // Visita a regra
    // sourceFile: packageClause eos (importDecl eos)* (
    // (functionDecl| methodDecl | declaration) eos)* EOF;
    @Override
    public AST visitSourceFile(final SourceFileContext ctx) {
        // Visita recursivamente os filhos para construir a AST.
        this.root = AST.newSubtree(NodeKind.SOURCE_FILE, Type.NO_TYPE);

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

        // for (final DeclarationContext declContext : ctx.declaration()) {
        // node.addDeclaration((DeclarationNode) this.visit(declContext));
        // }

        return this.root;
    }

    // functionDecl: FUNC IDENTIFIER (signature block?);
    @Override
    public AST visitFunctionDecl(final FunctionDeclContext ctx) {
        AST signature = visit(ctx.signature());
        lastDeclType = Type.FUNC_TYPE;
        AST funcVar = newVar(ctx.IDENTIFIER().getSymbol());

        VariableData varData = (VariableData) funcVar.getData();
        AST decl = new AST(NodeKind.FUNC_DECL_NODE, new VariableData(varData.getIndex()),
                Type.NO_TYPE);
        if (signature != null) {
            decl.addChildren(signature);
        }

        AST block = new AST(NodeKind.CODE_BLOCK, null, Type.NO_TYPE);

        for (StatementContext statement : ctx.block().statementList().statement()) {
            AST child = visit(statement);
            if (child != null) {
                block.addChildren(child);
            }
        }
        decl.addChildren(block);
        // Falta adicionar bloco de código
        return decl;
    }

    // @Override
    // public AST visitStatement( StatementContext ctx) {
    // if (ctx.declaration() != null) {
    // if (ctx.declaration().varDecl() != null) {
    // // ctx.declaration().varDecl().
    // }
    // }
    // }

    // signature:
    // {noTerminatorAfterParams(1)}? parameters result
    // | parameters;
    // @Override
    // public AST visitSignature(final SignatureContext ctx) {
    // AST parameters = null;
    // AST result = null;
    // if (ctx.parameters() != null) {
    // parameters = visit(ctx.parameters());
    // }
    // if (ctx.result() != null) {
    // result = visit(ctx.result());
    // }

    // AST p = new AST(NodeKind.SIGNATURE_NODE, new SignatureNode(), Type.NO_TYPE);

    // if(parameters != null) {
    // p.addChildren(parameters);
    // }
    // if (result != null) {
    // p.addChildren(result);
    // }
    // return p;
    // }

    // parameters:
    // L_PAREN (parameterDecl (COMMA parameterDecl)* COMMA?)? R_PAREN;
    // @Override
    // public AST visitParameters(final ParametersContext ctx) {
    // AST parameters = new AST(NodeKind.PARAMETERS_NODE, new ParametersNode(),
    // Type.NO_TYPE);
    // for (final ParameterDeclContext paramterDeclCtx : ctx.parameterDecl()) {
    // AST child = visit(paramterDeclCtx);
    // if (child != null) {
    // parameters.addChildren(child);
    // }
    // }
    // return parameters;
    // }

    // parameterDecl: identifierList? ELLIPSIS? type_;
    // @Override
    // public AST visitParameterDecl(final ParameterDeclContext ctx) {
    // AST type = visit(ctx.type_());
    // lastDeclType = type.getType();
    // AST identifiers = null;
    // if(ctx.identifierList() != null) {
    // identifiers = visit(ctx.identifierList());
    // }

    // AST parameterDecl = new AST(NodeKind.PARAMETER_DECLARATION, new
    // ParameterNode(), Type.NO_TYPE);
    // parameterDecl.addChildren(type, identifiers);
    // return parameterDecl;
    // }

    // result: parameters | type_;
    // @Override
    // public AST visitResult(final ResultContext ctx) {
    // AST parameters = visit(ctx.parameters());
    // if(parameters != null) {
    // return parameters;
    // } else {
    // return visit(ctx.type_());
    // }
    // }

    // type_: typeName | typeLit | L_PAREN type_ R_PAREN;
    @Override
    public AST visitType_(final Type_Context ctx) {
        if (ctx.typeLit() != null) {
            visit(ctx.typeLit());
        }

        AST typeName = null;
        if (ctx.typeName() != null) {
            typeName = visit(ctx.typeName());
        }
        if (typeName != null) {
            return typeName;
        }
        if (ctx.type_() != null) {
            visit(ctx.type_());
        }
        return null;
    }

    // AST typeLit = visit(ctx.typeLit());
    // if(typeLit != null) {
    // return typeLit;
    // }
    // return visit(ctx.type_());
    // }

    // typeName: qualifiedIdent | IDENTIFIER;
    @Override
    public AST visitTypeName(final TypeNameContext ctx) {
        Token token = ctx.IDENTIFIER().getSymbol();
        checkTypeValid(token);

        return new AST(NodeKind.TYPE_USE, new EmptyData(), lastDeclType);
    }

    // typeLit:
    // arrayType| structType| pointerType| functionType| interfaceType| sliceType|
    // mapType| channelType;
    // @Override
    // public AST visitTypeLit(final TypeLitContext ctx) {
    // AST literal = null;

    // if(ctx.arrayType() != null) {
    // literal = visit(ctx.arrayType());
    // } else if (ctx.structType() != null) {
    // literal = visit(ctx.structType());
    // } else if (ctx.pointerType() != null) {
    // literal = visit(ctx.pointerType());
    // } else if (ctx.functionType() != null) {
    // literal = visit(ctx.functionType());
    // } else if (ctx.interfaceType() != null) {
    // literal = visit(ctx.interfaceType());
    // } else if (ctx.sliceType() != null) {
    // literal = visit(ctx.sliceType());
    // } else if (ctx.mapType() != null) {
    // literal = visit(ctx.mapType());
    // } else if (ctx.channelType() != null) {
    // literal = visit(ctx.channelType());
    // }
    // return literal;
    // }

    // arrayType: L_BRACKET arrayLength R_BRACKET elementType;
    // @Override
    // public AST visitArrayType(final ArrayTypeContext ctx) {
    // AST type = visit(ctx.elementType());

    // AST size = visit(ctx.arrayLength()); //

    // AST array = new AST(NodeKind.ARRAY_TYPE, new EmptyData(), Type.NO_TYPE);

    // array.addChildren(type, size);
    // return array;
    // }

    // functionType: FUNC signature;
    // @Override
    // public AST visitFunctionType(final FunctionTypeContext ctx) {
    // AST signature = visit(ctx.signature());

    // AST funcType = new AST(NodeKind.FUNCTION_TYPE, new EmptyData(),
    // Type.NO_TYPE);
    // if (signature != null) {
    // funcType.addChildren(signature);
    // }
    // return funcType;
    // }

    // qualifiedIdent: IDENTIFIER DOT IDENTIFIER;
    // @Override
    // public AST visitQualifiedIdent(final QualifiedIdentContext ctx) {
    // AST parent = new AST(NodeKind.QUALIFIED_IDENTIFIER_NODE, new
    // QualifiedIdentifierNode(), Type.NO_TYPE);

    // AST child1 = new AST(NodeKind.IDENTIFIER, new
    // IdentifierNode(ctx.IDENTIFIER(0).getText()), Type.STR_TYPE);
    // AST child2 = new AST(NodeKind.IDENTIFIER, new
    // IdentifierNode(ctx.IDENTIFIER(1).getText()), Type.STR_TYPE);
    // lastDeclType = Type.STR_TYPE;
    // parent.addChildren(child1, child2);
    // return parent;
    // }

    // @Override
    // public AST visitIdentifierList(final IdentifierListContext ctx) {
    // AST parent = new AST(NodeKind.IDENTIFIER_LIST_NODE, new EmptyData(),
    // Type.NO_TYPE);

    // if (ctx.IDENTIFIER() == null) { return null; }

    // for (final TerminalNode node : ctx.IDENTIFIER()) {
    // AST id = new AST(NodeKind.IDENTIFIER, new
    // IdentifierNode(node.getSymbol().getText()), Type.STR_TYPE);
    // if(id != null) {
    // parent.addChildren(id);
    // // newVar(node.getSymbol());
    // }
    // }
    // return parent;
    // }

    // @Override
    // public AST visitBlock(final BlockContext ctx) {

    // }

    // <---------------- Visitadores dos TIPOS ---------------->

    @Override
    public AST visitInteger(final IntegerContext ctx) {
        // lastDeclType = Type.INT_TYPE;
        return new AST(NodeKind.INT_VAL_NODE, new IntData(Integer.parseInt(ctx.DECIMAL_LIT().getText())),
                Type.INT_TYPE);
    }

    @Override
    public AST visitString_(final String_Context ctx) {
        // lastDeclType = Type.STR_TYPE;
        return new AST(NodeKind.STR_VAL_NODE, new StringData(0), Type.STR_TYPE);
    }

    @Override
    public AST visitReal(final RealContext ctx) {
        // lastDeclType = Type.FLOAT_TYPE;
        return new AST(NodeKind.REAL_VAL_NODE, new RealData(Double.parseDouble(ctx.FLOAT_LIT().getText())),
                Type.FLOAT_TYPE);
    }

    @Override
    public AST visitNilType(final NilTypeContext ctx) {
        return new AST(NodeKind.NIL_NODE, new EmptyData(), Type.NIL_TYPE);
    }

    @Override
    public AST visitBoolean_(Boolean_Context ctx) {
        return new AST(NodeKind.BOOL_VAL_NODE, new BoolData(Boolean.valueOf(ctx.boolValue.getText())), Type.BOOL_TYPE);
    }

    // <----------------------------------------------->

    // @Override
    // public AST visitTypeName(final TypeNameContext ctx) {
    // checkTypeValid(ctx.IDENTIFIER().getSymbol());
    // return null;
    // }

    @Override
    public AST visitVarDeclExplType(VarDeclExplTypeContext ctx) {
        this.visit(ctx.type_());
        Type declType = lastDeclType;
        AST variableDeclaration = new AST(NodeKind.VAR_LIST_NODE, new EmptyData(), Type.NO_TYPE);

        List<TerminalNode> identifierList = ctx.identifierList().IDENTIFIER();

        if (ctx.expressionList() != null) {
            int quantIdentifier = ctx.identifierList().IDENTIFIER().size();
            int quantExpression = ctx.expressionList().expression().size();
            checkWrongAssignCount(ctx.start.getLine(), quantIdentifier, quantExpression);

            for (int i = 0; i < quantIdentifier; i++) {
                AST assignNode = new AST(NodeKind.ASSIGN_NODE, new EmptyData(), Type.NO_TYPE);

                AST var = this.newVar(identifierList.get(i).getSymbol());
                AST value = this.visit(ctx.expressionList().expression(i));

                assignNode.addChildren(var, value);
                variableDeclaration.addChildren(assignNode);
                // node.addValue((ValNode) this.visit(ctx.expressionList().expression(i)));
                // if (declType != lastDeclType) {
                // typeError(ctx.start.getLine(), "=", declType, lastDeclType);
                // }
            }
        } else {
            for (TerminalNode identifier : identifierList) {
                variableDeclaration.addChildren(this.newVar(identifier.getSymbol()));
            }
        }
        return variableDeclaration;
    }

    @Override
    public AST visitAssignment(AssignmentContext ctx) {
        AST assignmentListNode = new AST(NodeKind.ASSIGN_LIST_NODE, new EmptyData(), Type.NO_TYPE);

        int quantExpressionLeft = ctx.expressionList(0).expression().size();
        int quantExpressionRight = ctx.expressionList(1).expression().size();
        checkWrongAssignCount(ctx.start.getLine(), quantExpressionLeft, quantExpressionRight);

        for (int i = 0; i < quantExpressionLeft; i++) {
            AST assignNode = new AST(NodeKind.ASSIGN_NODE, new EmptyData(), Type.NO_TYPE);

            Token operandName = ctx.expressionList(0).expression(i).getStop();
            Token value = ctx.expressionList(1).expression(i).getStop();

            AST variable = checkVar(operandName);
            AST valueAST = new AST(NodeKind.INT_VAL_NODE, new IntData(Integer.valueOf(value.getText())),
                    Type.INT_TYPE);

            assignNode.addChildren(variable, valueAST);
            assignmentListNode.addChildren(assignNode);
        }

        return assignmentListNode;
    }

    // @Override
    // public AST visitPrimaryOp(PrimaryOpContext ctx) {
    // if (ctx.primaryExpr() != null && ctx.primaryExpr().operand().literal() !=
    // null) {
    // if (ctx.primaryExpr().operand().literal().basicLit() != null) {
    // return this.visit(ctx.primaryExpr().operand().literal().basicLit());
    // } else if (ctx.primaryExpr().operand().operandName() != null) {
    // return
    // checkVar(ctx.primaryExpr().operand().operandName().IDENTIFIER(0).getSymbol());
    // }
    // }
    // return visit(ctx.primaryExpr());
    // }

    // @Override
    // public VarDeclNode visitVarDeclImplType(VarDeclImplTypeContext ctx) {
    // int quantIdentifier = ctx.identifierList().IDENTIFIER().size();

    // int quantExpression = ctx.expressionList().expression().size();

    // checkWrongAssignCount(ctx.start.getLine(), quantIdentifier, quantExpression);
    // VarDeclNode node = new VarDeclNode();
    // for (int i = 0; i < quantIdentifier; i++) {
    // ValNode valueNode = (ValNode) this.visit(ctx.expressionList().expression(i));

    // node.addVariable(this.newVar(ctx.identifierList().IDENTIFIER(i).getSymbol()));
    // node.addValue(valueNode);
    // }
    // return node;
    // }

}
