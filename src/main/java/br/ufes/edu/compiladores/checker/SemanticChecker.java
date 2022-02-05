package br.ufes.edu.compiladores.checker;

import java.util.List;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.ufes.edu.compiladores.GoParser.DeclarationContext;
import br.ufes.edu.compiladores.GoParser.FunctionDeclContext;
import br.ufes.edu.compiladores.GoParser.IntegerContext;
import br.ufes.edu.compiladores.GoParser.SourceFileContext;
import br.ufes.edu.compiladores.GoParser.String_Context;
import br.ufes.edu.compiladores.GoParser.TypeNameContext;
import br.ufes.edu.compiladores.GoParser.VarDeclExplTypeContext;
import br.ufes.edu.compiladores.GoParser.VarDeclImplTypeContext;
import br.ufes.edu.compiladores.GoParserBaseVisitor;
import br.ufes.edu.compiladores.ast.AbstractSyntaxTree;
import br.ufes.edu.compiladores.ast.NodeKind;
import br.ufes.edu.compiladores.tables.StrTable;
import br.ufes.edu.compiladores.tables.VarTable;
import br.ufes.edu.compiladores.tables.VarTable.Entry;
import br.ufes.edu.compiladores.typing.Type;
import br.ufes.edu.compiladores.utils.TypeUtil;

public class SemanticChecker extends GoParserBaseVisitor<AbstractSyntaxTree> {

    protected Logger logger = LogManager.getLogger(SemanticChecker.class);
    protected StrTable st = new StrTable(); // Tabela de strings.
    protected VarTable vt = new VarTable(); // Tabela de variáveis.

    protected Type lastDeclType; // Variável "global" com o último tipo declarado.
    AbstractSyntaxTree root;

    // Testa se o dado token foi declarado antes.
    private AbstractSyntaxTree checkVar(final Token token) {
        final String text = token.getText();
        final int line = token.getLine();
        final Entry entry = vt.lookupVar(text);
        if (entry == null) {
            this.logger.error(
                    "SEMANTIC ERROR ({}): variable '{}' was not declared.\n",
                    line, text);
            System.exit(1);
        }
        return new AbstractSyntaxTree(NodeKind.VAR_USE_NODE, entry, entry.getType());
    }

    // ----------------------------------------------------------------------------
    // Type checking and inference.

    protected void typeError(final int lineNo, final String op, final Type t1, final Type t2) {
        this.logger.error("SEMANTIC ERROR ({}): incompatible types for operator '{}', LHS is '{}' and RHS is '{}'.\n",
                lineNo, op, t1, t2);
        System.exit(1);
    }

    public void printTables() {
        this.logger.info(st);
        this.logger.info(vt);
    }

    private void checkBoolExpr(final int lineNo, final String cmd, final Type t) {
        if (t != Type.BOOL_TYPE) {
            final String typeText = t.toString();
            final String boolString = Type.BOOL_TYPE.toString();
            this.logger.error("SEMANTIC ERROR ({}): conditional expression in '{}' is '{}' instead of '{}'.\n",
                    lineNo, cmd, typeText, boolString);
            System.exit(1);
        }
    }

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

    // Visita a regra sourceFile: packageClause eos (importDecl eos)* ((functionDecl
    // | methodDecl | declaration) eos)* EOF
    @Override
    public AbstractSyntaxTree visitSourceFile(final SourceFileContext ctx) {
        // Visita recursivamente os filhos para construir a AST.
        final AbstractSyntaxTree functionDeclTree = AbstractSyntaxTree.newSubtree(NodeKind.FUNCTION_DECLARATION,
                Type.NO_TYPE);
        for (final FunctionDeclContext functionDeclContext : ctx.functionDecl()) {
            functionDeclTree.addChildren(this.visit(functionDeclContext));
        }

        final AbstractSyntaxTree declTree = AbstractSyntaxTree.newSubtree(NodeKind.DECLARATION,
                Type.NO_TYPE);

        for (final DeclarationContext declContext : ctx.declaration()) {
            declTree.addChildren(this.visit(declContext));
        }
        // Como esta é a regra inicial, chegamos na raiz da AST.
        this.root = AbstractSyntaxTree.newSubtree(NodeKind.SOURCE_FILE, Type.NO_TYPE, functionDeclTree, declTree);
        return this.root;
    }

    @Override
    public AbstractSyntaxTree visitInteger(final IntegerContext ctx) {
        final Integer data = Integer.parseInt(ctx.getText());
        lastDeclType = Type.INT_TYPE;
        return new AbstractSyntaxTree(NodeKind.INT_VAL_NODE, data, Type.INT_TYPE);
    }

    @Override
    public AbstractSyntaxTree visitString_(final String_Context ctx) {
        final String data = st.add(ctx.getText());
        lastDeclType = Type.STR_TYPE;
        return new AbstractSyntaxTree(NodeKind.STR_VAL_NODE, data, Type.STR_TYPE);
    }

    @Override
    public AbstractSyntaxTree visitTypeName(final TypeNameContext ctx) {
        checkTypeValid(ctx.IDENTIFIER().getSymbol());
        return null;
    }

    // Cria uma nova variável a partir do dado token.
    private AbstractSyntaxTree newVar(Token token) {

        String text = token.getText();
        int currentLine = token.getLine();
        Entry entry = vt.lookupVar(text);
        if (entry != null) {
            int originalLine = entry.getLine();
            logger.error(
                    "SEMANTIC ERROR ({}): variable '{}' already declared at line {}.\n",
                    currentLine, text, originalLine);
            System.exit(1);
        }
        entry = vt.addVar(text, currentLine, lastDeclType);
        return new AbstractSyntaxTree(NodeKind.VAR_USE_NODE, entry, lastDeclType);
    }

    @Override
    public AbstractSyntaxTree visitVarDeclExplType(VarDeclExplTypeContext ctx) {

        this.visit(ctx.type_());
        Type declType = lastDeclType;
        AbstractSyntaxTree node = AbstractSyntaxTree.newSubtree(NodeKind.VAR_DECL_NODE, Type.NO_TYPE);

        List<TerminalNode> identifierList = ctx.identifierList().IDENTIFIER();
        for (TerminalNode identifier : identifierList) {
            node.addChildren(this.newVar(identifier.getSymbol()));
        }

        if (ctx.expressionList() != null) {
            int quantIdentifier = ctx.identifierList().IDENTIFIER().size();

            int quantExpression = ctx.expressionList().expression().size();

            checkWrongAssignCount(ctx.start.getLine(), quantIdentifier, quantExpression);

            for (int i = 0; i < quantIdentifier; i++) {
                node.addChildren(this.visit(ctx.expressionList().expression(i)));
                if (declType != lastDeclType) {
                    typeError(ctx.start.getLine(), "=", declType, lastDeclType);
                }

            }

        }

        return node;

    }

    @Override
    public AbstractSyntaxTree visitVarDeclImplType(VarDeclImplTypeContext ctx) {
        int quantIdentifier = ctx.identifierList().IDENTIFIER().size();

        int quantExpression = ctx.expressionList().expression().size();

        checkWrongAssignCount(ctx.start.getLine(), quantIdentifier, quantExpression);

        AbstractSyntaxTree node = AbstractSyntaxTree.newSubtree(NodeKind.VAR_DECL_NODE, Type.NO_TYPE);
        for (int i = 0; i < quantIdentifier; i++) {
            AbstractSyntaxTree expressionTree = this.visit(ctx.expressionList().expression(i));

            node.addChildren(this.newVar(ctx.identifierList().IDENTIFIER(i).getSymbol()));
            node.addChildren(expressionTree);
        }
        return node;
    }

    private void checkWrongAssignCount(int lineNo, int quantExpected, int quantReal) {
        if (quantExpected != quantReal) {
            logger.error("SEMANTIC ERROR ({}): cannot initialize '{}' variables with '{}' values",
                    lineNo, quantExpected, quantReal);
            System.exit(1);
        }
    }

    private void checkAssign(int lineNo, Type l, Type r) {
        if (l != r) {
            typeError(lineNo, ":=", l, r);
        }

    }

}
