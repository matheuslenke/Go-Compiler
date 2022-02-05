package br.ufes.edu.compiladores.checker;

import java.util.List;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.ufes.edu.compiladores.GoParser.ExpressionListContext;
import br.ufes.edu.compiladores.GoParser.IdentifierListContext;
import br.ufes.edu.compiladores.GoParser.TypeNameContext;
import br.ufes.edu.compiladores.GoParser.TypeSpecContext;
import br.ufes.edu.compiladores.GoParser.VarSpecContext;
import br.ufes.edu.compiladores.GoParserBaseVisitor;
import br.ufes.edu.compiladores.ast.AbstractSyntaxTree;
import br.ufes.edu.compiladores.ast.NodeKind;
import br.ufes.edu.compiladores.tables.StrTable;
import br.ufes.edu.compiladores.tables.VarTable;
import br.ufes.edu.compiladores.tables.VarTable.Entry;
import br.ufes.edu.compiladores.typing.Type;
import br.ufes.edu.compiladores.utils.TypeUtil;

public class SemanticChecker extends GoParserBaseVisitor<AbstractSyntaxTree> {

    private static Logger logger = LogManager.getLogger(SemanticChecker.class);
    private StrTable st = new StrTable(); // Tabela de strings.
    private VarTable vt = new VarTable(); // Tabela de variáveis.

    Type lastDeclType; // Variável "global" com o último tipo declarado.

    // Testa se o dado token foi declarado antes.
    private AbstractSyntaxTree checkVar(Token token) {
        String text = token.getText();
        int line = token.getLine();
        Entry entry = vt.lookupVar(text);
        if (entry == null) {
            logger.error(
                    "SEMANTIC ERROR (%d): variable '%s' was not declared.%n",
                    line, text);
            System.exit(1);
        }
        return new AbstractSyntaxTree(NodeKind.VAR_USE_NODE, entry, entry.getType());
    }

    // Cria uma nova variável a partir do dado token.
    private AbstractSyntaxTree newVar(Token token) {
        String text = token.getText();
        int currentLine = token.getLine();
        Entry entry = vt.lookupVar(text);
        if (entry != null) {
            int originalLine = entry.getLine();
            logger.error(
                    "SEMANTIC ERROR (%d): variable '%s' already declared at line %d.%n",
                    currentLine, text, originalLine);
            System.exit(1);
        }
        entry = vt.addVar(text, currentLine, lastDeclType);
        return new AbstractSyntaxTree(NodeKind.VAR_USE_NODE, entry, lastDeclType);
    }
    // ----------------------------------------------------------------------------
    // Type checking and inference.

    private void typeError(int lineNo, String op, Type t1, Type t2) {
        logger.error("SEMANTIC ERROR (%d): incompatible types for operator '%s', LHS is '%s' and RHS is '%s'.%n",
                lineNo, op, t1, t2);
        System.exit(1);
    }

    private void checkAssign(int lineNo, Type l, Type r) {
        if (l != r) {
            typeError(lineNo, ":=", l, r);
        }

    }

    public void printTables() {

        logger.info(st);
        logger.info(vt);
    }

    private void checkBoolExpr(int lineNo, String cmd, Type t) {
        if (t != Type.BOOL_TYPE) {
            String typeText = t.toString();
            String boolString = Type.BOOL_TYPE.toString();
            logger.error("SEMANTIC ERROR (%d): conditional expression in '%s' is '%s' instead of '%s'.%n",
                    lineNo, cmd, typeText, boolString);
            System.exit(1);
        }
    }

    private Type checkTypeValid(Token token) {
        String text = token.getText();
        int line = token.getLine();
        Type t = TypeUtil.getTypeByIdentifier(text);
        if (t == Type.NO_TYPE) {
            logger.error("SEMANTIC ERROR (%d): Tipo %s não existe.%n",
                    line, text);
            System.exit(1);
        }
        return t;
    }

    @Override
    public AbstractSyntaxTree visitTypeName(TypeNameContext ctx) {
        return checkTypeValid(ctx.IDENTIFIER().getSymbol());
    }

    @Override
    public AbstractSyntaxTree visitIdentifierList(IdentifierListContext ctx) {
        for (TerminalNode identifier : ctx.IDENTIFIER()) {
            this.checkVar(identifier.getSymbol());
        }
        return Type.NO_TYPE;
    }

    @Override
    public AbstractSyntaxTree visitVarSpec(VarSpecContext ctx) {

        Type rhsType = this.visit(ctx.type_());

        List<TerminalNode> identifierList = ctx.identifierList().IDENTIFIER();
        for (TerminalNode identifier : identifierList) {
            this.newVar(identifier.getSymbol());
        }

        ExpressionListContext expressionList = ctx.expressionList();
        Type lhsType = this.visit(expressionList);

        if (expressionList != null) {

            if (rhsType != lhsType) {
                typeError(ctx.getToken(0, 0).getSymbol().getLine(), "=", lhsType, lhsType);
            }
            int quantExpressionList = 0;
            if (expressionList.COMMA() != null) {
                quantExpressionList = expressionList.COMMA().size() + 1;
            }
            if (identifierList.size() != quantExpressionList) {

                logger.error("SEMANTIC ERROR (%d): cannot initialize %d variables with %d values%n",
                        ctx.getToken(0, 0).getSymbol().getLine(), identifierList.size(), quantExpressionList);
                System.exit(1);
            }
        }
        return Type.NO_TYPE;
    }

    @Override
    public AbstractSyntaxTree visitTypeSpec(TypeSpecContext ctx) {
        if (ctx.ASSIGN() == null) {

        } else {

        }
        return Type.NO_TYPE;
    }

    @Override
    public AbstractSyntaxTree visitExpr

}
