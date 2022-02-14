package br.ufes.edu.compiladores.checker;

import br.ufes.edu.compiladores.GoParser.ArrayTypeContext;
import br.ufes.edu.compiladores.GoParser.BlockContext;
import br.ufes.edu.compiladores.GoParser.FunctionDeclContext;
import br.ufes.edu.compiladores.GoParser.FunctionTypeContext;
import br.ufes.edu.compiladores.GoParser.IdentifierListContext;
import br.ufes.edu.compiladores.GoParser.ImportDeclContext;
import br.ufes.edu.compiladores.GoParser.ImportSpecContext;
import br.ufes.edu.compiladores.GoParser.IntegerContext;
import br.ufes.edu.compiladores.GoParser.NilTypeContext;
import br.ufes.edu.compiladores.GoParser.PackageClauseContext;
import br.ufes.edu.compiladores.GoParser.ParameterDeclContext;
import br.ufes.edu.compiladores.GoParser.ParametersContext;
import br.ufes.edu.compiladores.GoParser.QualifiedIdentContext;
import br.ufes.edu.compiladores.GoParser.RealContext;
import br.ufes.edu.compiladores.GoParser.ResultContext;
import br.ufes.edu.compiladores.GoParser.SignatureContext;
import br.ufes.edu.compiladores.GoParser.SourceFileContext;
import br.ufes.edu.compiladores.GoParser.StatementContext;
import br.ufes.edu.compiladores.GoParser.String_Context;
import br.ufes.edu.compiladores.GoParser.TypeLitContext;
import br.ufes.edu.compiladores.GoParser.TypeNameContext;
import br.ufes.edu.compiladores.GoParser.Type_Context;
import br.ufes.edu.compiladores.GoParserBaseVisitor;
import br.ufes.edu.compiladores.ast.AST;
import br.ufes.edu.compiladores.ast.Node;
import br.ufes.edu.compiladores.ast.NodeKind;
import br.ufes.edu.compiladores.ast.nodes.ArrayNode;
import br.ufes.edu.compiladores.ast.nodes.FunctionDeclNode;
import br.ufes.edu.compiladores.ast.nodes.FunctionTypeNode;
import br.ufes.edu.compiladores.ast.nodes.IdentifierListNode;
import br.ufes.edu.compiladores.ast.nodes.IdentifierNode;
import br.ufes.edu.compiladores.ast.nodes.NoTypeNode;
import br.ufes.edu.compiladores.ast.nodes.ParameterNode;
import br.ufes.edu.compiladores.ast.nodes.ParametersNode;
import br.ufes.edu.compiladores.ast.nodes.QualifiedIdentifierNode;
import br.ufes.edu.compiladores.ast.nodes.SignatureNode;
import br.ufes.edu.compiladores.ast.nodes.SourceFileNode;
import br.ufes.edu.compiladores.ast.nodes.TypeNode;
import br.ufes.edu.compiladores.ast.nodes.VarUseNode;
import br.ufes.edu.compiladores.ast.val.*;
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

        return new AST(NodeKind.VAR_USE_NODE, new VarUseNode(index), vt.getType(index));
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
        Integer idx =  vt.addVar(text, currentLine, lastDeclType);
        Type t = vt.getType(idx);
        System.out.println("Text da variável: " + text);
        switch (t) {
            case STR_TYPE:
                return new AST(NodeKind.VAR_DECL_NODE, new StringNode(text), lastDeclType);
            case BOOL_TYPE:
                return new AST(NodeKind.VAR_DECL_NODE, new BooleanNode(Boolean.valueOf(text)), lastDeclType);
            case FLOAT_TYPE:
                return new AST(NodeKind.VAR_DECL_NODE, new RealNode(Double.valueOf(text)), lastDeclType);
            case INT_TYPE:
                return new AST(NodeKind.VAR_DECL_NODE, new LongNode(Long.valueOf(text)), lastDeclType);
            case NO_TYPE:
                return new AST(NodeKind.VAR_DECL_NODE, new NoTypeNode(), lastDeclType);
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

    //  Checkers
    // ----------------------------------------------------------------------------

    // private void checkBoolExpr(final int lineNo, final String cmd, final Type t) {
    //     if (t != Type.BOOL_TYPE) {
    //         final String typeText = t.toString();
    //         final String boolString = Type.BOOL_TYPE.toString();
    //         this.logger.error("SEMANTIC ERROR ({}): conditional expression in '{}' is '{}' instead of '{}'.\n",
    //                 lineNo, cmd, typeText, boolString);
    //         System.exit(1);
    //     }
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

    // private void checkWrongAssignCount(int lineNo, int quantExpected, int quantReal) {
    //     if (quantExpected != quantReal) {
    //         logger.error("SEMANTIC ERROR ({}): cannot initialize '{}' variables with '{}' values",
    //                 lineNo, quantExpected, quantReal);
    //         System.exit(1);
    //     }
    // }

    // private void checkAssign(int lineNo, Type l, Type r) {
    //     if (l != r) {
    //         typeError(lineNo, ":=", l, r);
    //     }

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

        AST packageClause = visit(ctx.packageClause());
        AST importDeclClause = AST.newSubtree(NodeKind.IMPORT_LIST_NODE, Type.NO_TYPE);

        for (final ImportDeclContext importDeclContext : ctx.importDecl()) {
            AST child = visit(importDeclContext);
            if( child != null) {
                importDeclClause.addChildren(child);
            }
        }

        this.root = AST.newSubtree(NodeKind.SOURCE_FILE, Type.NO_TYPE, packageClause, importDeclClause);

        for (final FunctionDeclContext functionDeclContext : ctx.functionDecl()) {
            AST functionDecl = visit(functionDeclContext);
            if (functionDecl != null) {
                this.root.addChildren(functionDecl);
            }
        }

        // for (final DeclarationContext declContext : ctx.declaration()) {
        //     node.addDeclaration((DeclarationNode) this.visit(declContext));
        // }


        return this.root;
    }

    // packageClause: PACKAGE packageName = IDENTIFIER;
    @Override
    public AST visitPackageClause(PackageClauseContext ctx) {
        return new AST(NodeKind.PACKAGE_NODE, new PackageNode(ctx.packageName.getText()), Type.NO_TYPE);
    }

    // importDecl:
	// IMPORT (importSpec | L_PAREN (importSpec eos)* R_PAREN);
    @Override
    public AST visitImportDecl(final ImportDeclContext ctx) {

        AST node = AST.newSubtree(NodeKind.IMPORT_DECL, Type.NO_TYPE);
        for (final ImportSpecContext specCtx : ctx.importSpec()) {
            AST child = visit(specCtx);
            if (child != null) {
                node.addChildren(child);
            }
        }
        return node;
    }

    // importSpec: alias = (DOT | IDENTIFIER)? importPath;
    @Override
    public AST visitImportSpec(final ImportSpecContext ctx) {
        if (ctx.alias == null ) {
            return new AST(NodeKind.IMPORT_SPEC, new BooleanNode.ImportNode(ctx.importPath().getText()), Type.NO_TYPE);
        } else {
            return new AST(NodeKind.IMPORT_SPEC, new BooleanNode.ImportNode(ctx.alias.getText() + ctx.importPath().getText()), Type.NO_TYPE);
        }
    }

    // functionDecl: FUNC IDENTIFIER (signature block?);
    @Override
    public AST visitFunctionDecl(final FunctionDeclContext ctx) {
        String funcName = ctx.IDENTIFIER().getText();

        AST signature = visit(ctx.signature());

        AST decl = new AST(NodeKind.FUNCTION_DECLARATION, new FunctionDeclNode(funcName), Type.NO_TYPE);
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

    @Override
    public AST visitStatement( StatementContext ctx) {
        if (ctx.declaration() != null) {
            if (ctx.declaration().varDecl() != null) {
                ctx.declaration().varDecl().
            }
        }
    }

    // signature:
	// {noTerminatorAfterParams(1)}? parameters result
	// | parameters;
    @Override
    public AST visitSignature(final SignatureContext ctx) {
        AST parameters = null;
        AST result = null;
        if (ctx.parameters() != null) {
            parameters = visit(ctx.parameters());
        }
        if (ctx.result() != null) {
            result =  visit(ctx.result());
        }

        AST p = new AST(NodeKind.SIGNATURE_NODE, new SignatureNode(), Type.NO_TYPE);

        if(parameters != null) {
            p.addChildren(parameters);
        }
        if (result != null) {
            p.addChildren(result);
        }
        return p;
    }

    // parameters:
	// L_PAREN (parameterDecl (COMMA parameterDecl)* COMMA?)? R_PAREN;
    @Override
    public AST visitParameters(final ParametersContext ctx) {
        AST parameters = new AST(NodeKind.PARAMETERS_NODE, new ParametersNode(), Type.NO_TYPE);
        for (final ParameterDeclContext paramterDeclCtx : ctx.parameterDecl()) {
            AST child = visit(paramterDeclCtx);
            if (child != null) {
                parameters.addChildren(child);
            }
        }
        return parameters;
    }

    // parameterDecl: identifierList? ELLIPSIS? type_;
    @Override
    public AST visitParameterDecl(final ParameterDeclContext ctx) {
        AST type = visit(ctx.type_());
        lastDeclType = type.getType();
        AST identifiers = null;
        if(ctx.identifierList() != null) {
            identifiers = visit(ctx.identifierList());
        }

        AST parameterDecl = new AST(NodeKind.PARAMETER_DECLARATION, new ParameterNode(), Type.NO_TYPE);
        parameterDecl.addChildren(type, identifiers);
        return parameterDecl;
    }

    // result: parameters | type_;
    @Override
    public AST visitResult(final ResultContext ctx) {
        AST parameters = visit(ctx.parameters());
        if(parameters != null) {
            return parameters;
        } else {
            return visit(ctx.type_());
        }
    } 

    // type_: typeName | typeLit | L_PAREN type_ R_PAREN;
     @Override
     public AST visitType_(final Type_Context ctx) {

         AST typeName = null;
         if (ctx.typeName() != null) {
             typeName = visit(ctx.typeName());
         }
         if(typeName != null) {
             return typeName;
         }

         AST typeLit = visit(ctx.typeLit());
         if(typeLit != null) {
             return typeLit;
         }
         return visit(ctx.type_());
     }

    // typeName: qualifiedIdent | IDENTIFIER;
    @Override
    public AST visitTypeName(final TypeNameContext ctx) {
        Token token = ctx.IDENTIFIER().getSymbol();
        checkTypeValid(token);

        return new AST(NodeKind.TYPE_USE, new TypeNode(token.getText()), lastDeclType);
    }

    // typeLit:
	// arrayType| structType| pointerType| functionType| interfaceType| sliceType| mapType| channelType;
    @Override
    public AST visitTypeLit(final TypeLitContext ctx) {
        AST literal = null;

        if(ctx.arrayType() != null) {
            literal = visit(ctx.arrayType());
        } else if (ctx.structType() != null) {
            literal = visit(ctx.structType());
        } else if (ctx.pointerType() != null) {
            literal = visit(ctx.pointerType());
        } else if (ctx.functionType() != null) {
            literal = visit(ctx.functionType());
        } else if (ctx.interfaceType() != null) {
            literal = visit(ctx.interfaceType());
        } else if (ctx.sliceType() != null) {
            literal = visit(ctx.sliceType());
        } else if (ctx.mapType() != null) {
            literal = visit(ctx.mapType());
        } else if (ctx.channelType() != null) {
            literal = visit(ctx.channelType());
        }
        return literal;
    }

    // arrayType: L_BRACKET arrayLength R_BRACKET elementType;
    @Override
    public AST visitArrayType(final ArrayTypeContext ctx) {
        AST type = visit(ctx.elementType());

        AST size = visit(ctx.arrayLength().getChild(0));

        AST array = new AST(NodeKind.ARRAY_TYPE, new ArrayNode(), Type.NO_TYPE);

        array.addChildren(type, size);
        return array;
    }

    // functionType: FUNC signature;
    @Override
    public AST visitFunctionType(final FunctionTypeContext ctx) {
        AST signature = visit(ctx.signature());

        AST funcType = new AST(NodeKind.FUNCTION_TYPE, new FunctionTypeNode(), Type.NO_TYPE);
        if (signature != null) {
            funcType.addChildren(signature);
        }
        return funcType;
    }
    
    // qualifiedIdent: IDENTIFIER DOT IDENTIFIER;
    // @Override
    // public AST visitQualifiedIdent(final QualifiedIdentContext ctx) {
    //     AST parent = new AST(NodeKind.QUALIFIED_IDENTIFIER_NODE, new QualifiedIdentifierNode(), Type.NO_TYPE);

    //     AST child1 = new AST(NodeKind.IDENTIFIER, new IdentifierNode(ctx.IDENTIFIER(0).getText()), Type.STR_TYPE);
    //     AST child2 = new AST(NodeKind.IDENTIFIER, new IdentifierNode(ctx.IDENTIFIER(1).getText()), Type.STR_TYPE);
    //     lastDeclType = Type.STR_TYPE;
    //     parent.addChildren(child1, child2);
    //     return parent;
    // }


    @Override
    public AST visitIdentifierList(final IdentifierListContext ctx) {
        AST parent = new AST(NodeKind.IDENTIFIER_LIST_NODE, new IdentifierListNode(), Type.NO_TYPE);

        if (ctx.IDENTIFIER() == null) { return null; }

        for (final TerminalNode node : ctx.IDENTIFIER()) {
            AST id = new AST(NodeKind.IDENTIFIER, new IdentifierNode(node.getSymbol().getText()),  Type.STR_TYPE);
            if(id != null) {
                parent.addChildren(id);
                // newVar(node.getSymbol());
            }
        }
        return parent;
    }

    // @Override
    // public AST visitBlock(final BlockContext ctx) {


    // }

    // <---------------- Visitadores dos TIPOS ---------------->

    @Override
    public AST visitInteger(final IntegerContext ctx) {
        final Long data = Long.parseLong(ctx.getText());
        lastDeclType = Type.INT_TYPE;
        return new AST(NodeKind.INT_VAL_NODE, new LongNode(data), Type.INT_TYPE);
    }

    @Override
    public AST visitString_(final String_Context ctx) {
        final String data = st.add(ctx.getText());
        lastDeclType = Type.STR_TYPE;
        return new AST(NodeKind.STR_VAL_NODE, new StringNode(data), Type.STR_TYPE);
    }

    @Override
    public AST visitReal(final RealContext ctx) {
        final Double data = Double.parseDouble(ctx.getText());
        lastDeclType = Type.FLOAT_TYPE;
        return new AST(NodeKind.REAL_VAL_NODE, new RealNode(data), Type.FLOAT_TYPE);
    }

    @Override
    public AST visitNilType (final NilTypeContext ctx) {
        return new AST(NodeKind.NIL_NODE, new NilNode(), Type.NIL_TYPE);
    }


    // <----------------------------------------------->

    // @Override
    // public AST visitTypeName(final TypeNameContext ctx) {
    //     checkTypeValid(ctx.IDENTIFIER().getSymbol());
    //     return null;
    // }

  

    // @Override
    // public AST visitVarDeclExplType(VarDeclExplTypeContext ctx) {

    //     this.visit(ctx.type_());
    //     Type declType = lastDeclType;
    //     VarDeclNode node = new VarDeclNode();

    //     List<TerminalNode> identifierList = ctx.identifierList().IDENTIFIER();
    //     for (TerminalNode identifier : identifierList) {
    //         node.addVariable(this.newVar(identifier.getSymbol()));
    //     }

    //     if (ctx.expressionList() != null) {
    //         int quantIdentifier = ctx.identifierList().IDENTIFIER().size();

    //         int quantExpression = ctx.expressionList().expression().size();

    //         checkWrongAssignCount(ctx.start.getLine(), quantIdentifier, quantExpression);

    //         for (int i = 0; i < quantIdentifier; i++) {
    //             node.addValue((ValNode) this.visit(ctx.expressionList().expression(i)));
    //             if (declType != lastDeclType) {
    //                 typeError(ctx.start.getLine(), "=", declType, lastDeclType);
    //             }

    //         }

    //     }

    //     return node;

    // }

    // @Override
    // public VarDeclNode visitVarDeclImplType(VarDeclImplTypeContext ctx) {
    //     int quantIdentifier = ctx.identifierList().IDENTIFIER().size();

    //     int quantExpression = ctx.expressionList().expression().size();

    //     checkWrongAssignCount(ctx.start.getLine(), quantIdentifier, quantExpression);
    //     VarDeclNode node = new VarDeclNode();
    //     for (int i = 0; i < quantIdentifier; i++) {
    //         ValNode valueNode = (ValNode) this.visit(ctx.expressionList().expression(i));

    //         node.addVariable(this.newVar(ctx.identifierList().IDENTIFIER(i).getSymbol()));
    //         node.addValue(valueNode);
    //     }
    //     return node;
    // }



}
