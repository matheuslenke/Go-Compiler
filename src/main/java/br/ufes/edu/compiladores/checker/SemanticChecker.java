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
import br.ufes.edu.compiladores.GoParser.VarDeclContext;
import br.ufes.edu.compiladores.GoParser.VarSpecContext;
import br.ufes.edu.compiladores.GoParserBaseVisitor;
import br.ufes.edu.compiladores.tables.StrTable;
import br.ufes.edu.compiladores.tables.VarTable;
import br.ufes.edu.compiladores.typing.IType;
import br.ufes.edu.compiladores.typing.PrimitiveType;
import br.ufes.edu.compiladores.utils.TypeUtil;

public class SemanticChecker extends GoParserBaseVisitor<IType> {

    private static Logger logger = LogManager.getLogger(SemanticChecker.class);
    private StrTable st = new StrTable(); // Tabela de strings.
    private VarTable vt = new VarTable(); // Tabela de variáveis.

    private boolean passed = true;

    IType lastDeclType; // Variável "global" com o último tipo declarado.

    public boolean hasPassed() {
        return passed;
    }

    // Testa se o dado token foi declarado antes.
    IType checkVar(Token token) {
        String text = token.getText();
        int line = token.getLine();
        if (!vt.lookupVar(text)) {
            logger.error(
                    "SEMANTIC ERROR (%d): variable '%s' was not declared.%n",
                    line, text);
            passed = false;
            return PrimitiveType.NO_TYPE;
        }
        return vt.getType(text);
    }

    // Cria uma nova variável a partir do dado token.
    void newVar(Token token) {
        String text = token.getText();
        int currentLine = token.getLine();
        if (vt.lookupVar(text)) {
            int originalLine = vt.getLine(text);
            logger.error(
                    "SEMANTIC ERROR (%d): variable '%s' already declared at line %d.%n",
                    currentLine, text, originalLine);
            passed = false;
            return;
        }
        vt.addVar(text, currentLine, lastDeclType);
    }
    // ----------------------------------------------------------------------------
    // Type checking and inference.

    private void typeError(int lineNo, String op, IType t1, IType t2) {
        logger.error("SEMANTIC ERROR (%d): incompatible types for operator '%s', LHS is '%s' and RHS is '%s'.%n",
                lineNo, op, t1, t2);
        passed = false;
    }

    // Essa função também poderia virar uma tabela de unificação dos tipos,
    // igual às que estão em IType, mas fica aqui como uma outra forma de
    // implementar
    // a verificação de tipos.
    private void checkAssign(int lineNo, IType l, IType r) {
        if (l != r) {
            typeError(lineNo, ":=", l, r);
        }

    }

    public void printTables() {

        logger.info(st);
        logger.info(vt);
    }

    private void checkBoolExpr(int lineNo, String cmd, IType t) {
        if (t != PrimitiveType.BOOL_TYPE) {
            String typeText = t.toString();
            String boolString = PrimitiveType.BOOL_TYPE.toString();
            logger.error("SEMANTIC ERROR (%d): conditional expression in '%s' is '%s' instead of '%s'.%n",
                    lineNo, cmd, typeText, boolString);
            passed = false;
        }
    }

    private IType checkTypeValid(Token token) {
        String text = token.getText();
        int line = token.getLine();
        IType t = TypeUtil.getTypeByIdentifier(text);
        if (t == PrimitiveType.NO_TYPE) {
            logger.error("SEMANTIC ERROR (%d): Tipo %s não existe.%n",
                    line, text);
            passed = false;
        }
        return t;
    }

    @Override
    public IType visitTypeName(TypeNameContext ctx) {
        return checkTypeValid(ctx.IDENTIFIER().getSymbol());
    }

    @Override
    public IType visitIdentifierList(IdentifierListContext ctx) {
        for (TerminalNode identifier : ctx.IDENTIFIER()) {
            this.checkVar(identifier.getSymbol());
        }
        return PrimitiveType.NO_TYPE;
    }

    @Override
    public IType visitVarSpec(VarSpecContext ctx) {

        IType rhsType = this.visit(ctx.type_());

        List<TerminalNode> identifierList = ctx.identifierList().IDENTIFIER();
        for (TerminalNode identifier : identifierList) {
            this.newVar(identifier.getSymbol());
        }

        ExpressionListContext expressionList = ctx.expressionList();
        IType lhsType = this.visit(expressionList);

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
                this.passed = false;
            }
        }
        return PrimitiveType.NO_TYPE;
    }

    @Override
    public IType visitTypeSpec(TypeSpecContext ctx) {
        if (ctx.ASSIGN() == null) {

        } else {

        }
        return PrimitiveType.NO_TYPE;
    }

}
