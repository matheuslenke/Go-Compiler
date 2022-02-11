package br.ufes.edu.compiladores.checker;

import java.util.List;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.ufes.edu.compiladores.GoParser.DeclarationContext;
import br.ufes.edu.compiladores.GoParser.FunctionDeclContext;
import br.ufes.edu.compiladores.GoParser.IntegerContext;
import br.ufes.edu.compiladores.GoParser.RealContext;
import br.ufes.edu.compiladores.GoParser.SourceFileContext;
import br.ufes.edu.compiladores.GoParser.String_Context;
import br.ufes.edu.compiladores.GoParser.TypeNameContext;
import br.ufes.edu.compiladores.GoParser.VarDeclExplTypeContext;
import br.ufes.edu.compiladores.GoParser.VarDeclImplTypeContext;
import br.ufes.edu.compiladores.GoParserBaseVisitor;
import br.ufes.edu.compiladores.ast.AbstractSyntaxTree;
import br.ufes.edu.compiladores.ast.DeclarationNode;
import br.ufes.edu.compiladores.ast.FunctionDeclarationNode;
import br.ufes.edu.compiladores.ast.Node;
import br.ufes.edu.compiladores.ast.NodeKind;
import br.ufes.edu.compiladores.ast.SourceFileNode;
import br.ufes.edu.compiladores.ast.VarDeclNode;
import br.ufes.edu.compiladores.ast.val.LongNode;
import br.ufes.edu.compiladores.ast.val.RealNode;
import br.ufes.edu.compiladores.ast.val.StringNode;
import br.ufes.edu.compiladores.ast.val.ValNode;
import br.ufes.edu.compiladores.tables.StrTable;
import br.ufes.edu.compiladores.tables.VarTable;
import br.ufes.edu.compiladores.tables.VarTable.Entry;
import br.ufes.edu.compiladores.typing.Type;
import br.ufes.edu.compiladores.utils.TypeUtil;

public class SemanticChecker extends GoParserBaseVisitor<Node> {

    protected Logger logger = LogManager.getLogger(SemanticChecker.class);
    protected StrTable st = new StrTable(); // Tabela de strings.
    protected VarTable vt = new VarTable(); // Tabela de variáveis.

    protected Type lastDeclType; // Variável "global" com o último tipo declarado.

    // Testa se o dado token foi declarado antes.
    private Entry checkVar(final Token token) {
        final String text = token.getText();
        final int line = token.getLine();
        final Entry entry = vt.lookupVar(text);
        if (entry == null) {
            this.logger.error(
                    "SEMANTIC ERROR ({}): variable '{}' was not declared.\n",
                    line, text);
            System.exit(1);
        }
        return entry;

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
    public SourceFileNode visitSourceFile(final SourceFileContext ctx) {
        // Visita recursivamente os filhos para construir a AST.
        final SourceFileNode node = new SourceFileNode();
        for (final FunctionDeclContext functionDeclContext : ctx.functionDecl()) {
            node.addFunction((FunctionDeclarationNode) this.visit(functionDeclContext));
        }

        for (final DeclarationContext declContext : ctx.declaration()) {
            node.addDeclaration((DeclarationNode) this.visit(declContext));
        }

        return node;
    }

    @Override
    public LongNode visitInteger(final IntegerContext ctx) {
        final Long data = Long.parseLong(ctx.getText());
        lastDeclType = Type.INT_TYPE;
        return new LongNode(data);
    }

    @Override
    public StringNode visitString_(final String_Context ctx) {
        final String data = st.add(ctx.getText());
        lastDeclType = Type.STR_TYPE;
        return new StringNode(data);
    }

    @Override
    public RealNode visitReal(final RealContext ctx) {
        final Double data = Double.parseDouble(ctx.getText());
        lastDeclType = Type.FLOAT64_TYPE;
        return new RealNode(data);
    }

    @Override
    public Node visitTypeName(final TypeNameContext ctx) {
        checkTypeValid(ctx.IDENTIFIER().getSymbol());
        return null;
    }

    // Cria uma nova variável a partir do dado token.
    private Entry newVar(Token token) {

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
        return vt.addVar(text, currentLine, lastDeclType);

    }

    @Override
    public VarDeclNode visitVarDeclExplType(VarDeclExplTypeContext ctx) {

        this.visit(ctx.type_());
        Type declType = lastDeclType;
        VarDeclNode node = new VarDeclNode();

        List<TerminalNode> identifierList = ctx.identifierList().IDENTIFIER();
        for (TerminalNode identifier : identifierList) {
            node.addVariable(this.newVar(identifier.getSymbol()));
        }

        if (ctx.expressionList() != null) {
            int quantIdentifier = ctx.identifierList().IDENTIFIER().size();

            int quantExpression = ctx.expressionList().expression().size();

            checkWrongAssignCount(ctx.start.getLine(), quantIdentifier, quantExpression);

            for (int i = 0; i < quantIdentifier; i++) {
                node.addValue((ValNode) this.visit(ctx.expressionList().expression(i)));
                if (declType != lastDeclType) {
                    typeError(ctx.start.getLine(), "=", declType, lastDeclType);
                }

            }

        }

        return node;

    }

    @Override
    public VarDeclNode visitVarDeclImplType(VarDeclImplTypeContext ctx) {
        int quantIdentifier = ctx.identifierList().IDENTIFIER().size();

        int quantExpression = ctx.expressionList().expression().size();

        checkWrongAssignCount(ctx.start.getLine(), quantIdentifier, quantExpression);
        VarDeclNode node = new VarDeclNode();
        for (int i = 0; i < quantIdentifier; i++) {
            ValNode valueNode = (ValNode) this.visit(ctx.expressionList().expression(i));

            node.addVariable(this.newVar(ctx.identifierList().IDENTIFIER(i).getSymbol()));
            node.addValue(valueNode);
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
