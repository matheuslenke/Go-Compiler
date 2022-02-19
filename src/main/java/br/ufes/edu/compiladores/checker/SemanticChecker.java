package br.ufes.edu.compiladores.checker;

import java.util.List;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import br.ufes.edu.compiladores.GoParser.AssignmentContext;
import br.ufes.edu.compiladores.GoParser.Boolean_Context;
import br.ufes.edu.compiladores.GoParser.DeclarationContext;
import br.ufes.edu.compiladores.GoParser.FunctionDeclContext;
import br.ufes.edu.compiladores.GoParser.IntegerContext;
import br.ufes.edu.compiladores.GoParser.NilTypeContext;
import br.ufes.edu.compiladores.GoParser.RealContext;
import br.ufes.edu.compiladores.GoParser.SourceFileContext;
import br.ufes.edu.compiladores.GoParser.StatementContext;
import br.ufes.edu.compiladores.GoParser.String_Context;
import br.ufes.edu.compiladores.GoParser.TypeNameContext;
import br.ufes.edu.compiladores.GoParser.Type_Context;
import br.ufes.edu.compiladores.GoParser.VarDeclExplTypeContext;
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

    private void typeError(final int lineNo, final String op, final Type t1, final Type t2) {
        System.out.println(String.format(
                "SEMANTIC ERROR (%d): incompatible types for operator '%s', LHS is '%s' and RHS is '%s'.\n",
                lineNo, op, t1, t2));
        System.exit(1);
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
        AST.printDot(root, vt);
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

    // functionDecl: FUNC IDENTIFIER (signature block?)
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

    // type_: typeName | typeLit | L_PAREN type_ R_PAREN
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
        return new AST(NodeKind.INT_VAL_NODE, new IntData(Integer.parseInt(ctx.DECIMAL_LIT().getText())),
                Type.INT_TYPE);
    }

    @Override
    public AST visitString_(final String_Context ctx) {
        return new AST(NodeKind.STR_VAL_NODE, new StringData(0), Type.STR_TYPE);
    }

    @Override
    public AST visitReal(final RealContext ctx) {
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

    @Override
    public AST visitVarDeclExplType(VarDeclExplTypeContext ctx) {
        this.visit(ctx.type_()); // visita para definir qual o tipo das variáveis
        AST variableDeclaration = new AST(NodeKind.VAR_LIST_NODE, new EmptyData(), Type.NO_TYPE);

        List<TerminalNode> identifierList = ctx.identifierList().IDENTIFIER();

        if (ctx.expressionList() != null) {
            int quantIdentifier = ctx.identifierList().IDENTIFIER().size();
            int quantExpression = ctx.expressionList().expression().size();
            checkWrongAssignCount(ctx.start.getLine(), quantIdentifier, quantExpression);

            // Para cada variável declarada atribuímos novo nó com o valor declarado
            for (int i = 0; i < quantIdentifier; i++) {
                AST assignNode = new AST(NodeKind.ASSIGN_NODE, new EmptyData(), Type.NO_TYPE);

                AST newVar = this.newVar(identifierList.get(i).getSymbol());
                AST value = this.visit(ctx.expressionList().expression(i));

                assignNode.addChildren(newVar, value);
                variableDeclaration.addChildren(assignNode);
            }
        } else {
            for (TerminalNode identifier : identifierList) {
                variableDeclaration.addChildren(this.newVar(identifier.getSymbol()));
            }
        }
        return variableDeclaration;
    }

    /**
     * Somente verifica atribuições expression = expression
     */
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

}
