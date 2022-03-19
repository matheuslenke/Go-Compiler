package br.ufes.edu.compiladores.code;

import static br.ufes.edu.compiladores.code.Instruction.INSTR_MEM_SIZE;

import br.ufes.edu.compiladores.ast.AST;
import br.ufes.edu.compiladores.tables.StrTable;
import br.ufes.edu.compiladores.tables.VarTable;
import br.ufes.edu.compiladores.typing.Type;
import br.ufes.edu.compiladores.ast.ASTBaseVisitor;
import br.ufes.edu.compiladores.ast.BoolData;
import br.ufes.edu.compiladores.ast.IntData;
import br.ufes.edu.compiladores.ast.NodeKind;
import br.ufes.edu.compiladores.ast.RealData;
import br.ufes.edu.compiladores.ast.StringData;
import br.ufes.edu.compiladores.ast.VariableData;

import br.ufes.edu.compiladores.code.OpCode;

import java.util.Optional;

/*
 * Visitador da AST para geração básica de código. Funciona de
 * forma muito similar ao interpretador do laboratório anterior,
 * mas agora estamos trabalhando com um ambiente de execução 
 * com código de 3 endereços. Isto quer dizer que não existe mais
 * pilha e todas as operações são realizadas via registradores.
 * 
 * Note que não há uma área de memória de dados no código abaixo.
 * Esta área fica agora na TM, que a "arquitetura" de execução.
 */
public class CodeGen extends ASTBaseVisitor<Integer> {
    private final Instruction code[]; // Code memory
    private final MipsData dataSection[];
    private final StrTable st;
    private final VarTable vt;

    // Contadores para geração de código.
    // Próxima posição na memória de código para emit.
    private static int nextInstr;
    private static int nextData;
    // Número de registradores temporários já utilizados.
    // Usamos um valor arbitrário, mas depois seria necessário
    // fazer o processo de alocação de registradores. Isto está
    // fora do escopo da disciplina.
    private static int intRegsCount;
    private static int floatRegsCount;

    public CodeGen(StrTable st, VarTable vt) {
        this.code = new Instruction[INSTR_MEM_SIZE];
        this.dataSection = new MipsData[INSTR_MEM_SIZE];
        this.st = st;
        this.vt = vt;
    }

    // Função principal para geração de código.
    @Override
    public void execute(AST root) {
        nextInstr = 0;
        intRegsCount = 4;
        floatRegsCount = 0;
        nextData = 0;
        emitData(OpCode.DATA, "", "");
        emit(OpCode.GLOBAL, "main");
        emit(OpCode.CODE);
        emit(OpCode.JUMP, "main");
        visit(root);
        dumpVarTable();
        dumpStrTable();
        dumpProgram();
    }

    // ----------------------------------------------------------------------------
    // Prints ---------------------------------------------------------------------

    void dumpProgram() {
        System.out.printf("\n");
        for (int addr = 0; addr < nextInstr; addr++) {
            System.out.printf("%s\n", code[addr].toString());
        }
    }

    void dumpStrTable() {
        for (int i = 0; i < st.getSize(); i++) {
            System.out.printf("string" + i + ": .asciiz \"%s\"\n", st.get(i));
        }
    }

    void dumpVarTable() {
        checkVarTable();
        for (int addr = 0; addr < nextData; addr++) {
            System.out.printf("%s\n", dataSection[addr].toString());
        }
    }

    void checkVarTable() {
        for (int idx = 5; idx < vt.getSize(); idx++) {
            if (vt.getType(idx) == Type.INT_TYPE) {
                MipsData newData = new MipsData(OpCode.WORD_DATA, vt.getName(idx), "0");
                dataSection[nextData] = newData;
                nextData++;
            } else if (vt.getType(idx) == Type.FLOAT_TYPE) {
                MipsData newData = new MipsData(OpCode.FLOAT_DATA, vt.getName(idx), "0.0");
                dataSection[nextData] = newData;
                nextData++;
            } else if (vt.getType(idx) == Type.BOOL_TYPE) {
                MipsData newData = new MipsData(OpCode.WORD_DATA, vt.getName(idx), "0");
                dataSection[nextData] = newData;
                nextData++;
            } else if (vt.getType(idx) == Type.STR_TYPE) {
                MipsData newData = new MipsData(OpCode.WORD_DATA, vt.getName(idx), "0");
                dataSection[nextData] = newData;
                nextData++;
            }
        }
    }

    // ----------------------------------------------------------------------------
    // Emits ----------------------------------------------------------------------

    private void emitData(OpCode op, String data, String value) {
        MipsData newData = new MipsData(op, data, value);
        dataSection[nextData] = newData;
        nextData++;
    }

    private void emit(OpCode op, String o1, String o2, String o3) {
        Instruction instr = new Instruction(op, o1, o2, o3);
        // Em um código para o produção deveria haver uma verificação aqui...
        code[nextInstr] = instr;
        nextInstr++;
    }

    private void emit(OpCode op) {
        emit(op, "", "", "");
    }

    private void emit(OpCode op, String o1) {
        emit(op, o1, "", "");
    }

    private void emit(OpCode op, String o1, String o2) {
        emit(op, o1, o2, "");
    }

    private void backpatchJump(String instrAddr, String jumpAddr) {
        code[Integer.parseInt(instrAddr)].o1 = jumpAddr;
    }

    private void backpatchBranch(String instrAddr, String offset) {
        code[Integer.parseInt(instrAddr)].o2 = offset;
    }

    // ----------------------------------------------------------------------------
    // AST Traversal --------------------------------------------------------------

    private int newIntReg() {
        return intRegsCount++;
    }

    private int newFloatReg() {
        return floatRegsCount++;
    }

    private void backpatchJump(int instrAddr, int jumpAddr) {
        code[instrAddr].o1 = "$" + jumpAddr;
    }

    private void backpatchBranch(int instrAddr, int offset) {
        code[instrAddr].o2 = "$" + offset;
    }

    @Override
    protected Integer visitSourceFile(AST node) {
        Optional<AST> childOpt = node.getChild(0);
        visit(childOpt.get()); // ImportSpec
        // Visitando cada uma das funções
        Integer idx;

        for (idx = 1; idx < node.getChildren().size(); idx++) {
            childOpt = node.getChild(idx);
            visit(childOpt.get());
        }
        return -1;
    }

    @Override
    protected Integer visitImportSpec(AST node) {
        return -1;
    }

    @Override
    protected Integer visitFuncUse(AST node) {
        Optional<AST> funcNameNode = node.getChild(0);
        VariableData varData = (VariableData) funcNameNode.get().getData();
        String funcName = vt.getName(varData.getIndex());
        emit(OpCode.FUNC_CALL, funcName);

        Optional<AST> childOpt = node.getChild(1);
        visit(childOpt.get()); // Visitando os parâmetros
        return -1;
    }

    @Override
    protected Integer visitArguments(AST node) {
        return -1;
    }

    @Override
    protected Integer visitFuncDecl(AST node) {
        // Pegando o nome da função
        VariableData varData = (VariableData) node.getData();
        String funcName = vt.getName(varData.getIndex());
        emit(OpCode.LABEL, funcName);

        Optional<AST> childOpt = node.getChild(0);
        visit(childOpt.get()); // Visitando os parâmetros

        childOpt = node.getChild(1);
        visit(childOpt.get()); // Visitando o bloco de código
        // emit(OpCode.JUMP_R, "$ra");
        emit(OpCode.LABEL, "end" + funcName);
        return -1;
    }

    @Override
    protected Integer visitParameters(AST node) {
        return -1;
    }

    @Override
    protected Integer visitAssign(AST node) {
        AST r = node.getChild(1).get();
        int x = visit(r);
        VariableData varData = (VariableData) node.getChild(0).get().getData();
        String varLabel = vt.getName(varData.getIndex());
        Type varType = vt.getType(varData.getIndex());
        if (varType == Type.INT_TYPE) {
            emit(OpCode.STORE_WORD, "$" + Integer.toString(x), varLabel);
        }  
        else if (varType == Type.FLOAT_TYPE) { 
            emit(OpCode.STORE_WORD_C1, "$f" + Integer.toString(x), varLabel);
        }
        else if (varType == Type.STR_TYPE || varType == Type.BOOL_TYPE) { 
            emit(OpCode.STORE_WORD, "$" + Integer.toString(x), varLabel);
        } 
        return -1; // This is not an expression, hence no value to return.

    }

    @Override
    protected Integer visitEq(AST node) {
        AST l = node.getChild(0).get();
        AST r = node.getChild(1).get();
        int y = visit(l);
        int z = visit(r);
        int x = newIntReg();
        emit(OpCode.EQ, "$" + x, "$" + y, "$" + z);
        return x;
    }

    @Override
    protected Integer visitBlock(AST node) {
        Integer idx;
        Optional<AST> childOpt;

        for (idx = 0; idx < node.getChildren().size(); idx++) {
            childOpt = node.getChild(idx);
            visit(childOpt.get());
        }
        return -1;
    }

    @Override
    protected Integer visitBoolVal(AST node) {
        int x = newIntReg();
        BoolData data = (BoolData) node.getData();
        if(data.getValue() == true) {
            emit(OpCode.LOAD_INTEGER, "$"+Integer.toString(x), "1");
        } else {
            emit(OpCode.LOAD_INTEGER, "$"+Integer.toString(x), "0");
        }
        return x;
    }

    @Override
    protected Integer visitIf(AST node) {
        // Code for test.
        int testReg = visit(node.getChild(0).get());

        emit(OpCode.BRANCH_IF_EQUAL, "$" + testReg, "$zero", "fimTrueBlock" + testReg);

        // Code for TRUE block.
        visit(node.getChild(1).get()); // Generate TRUE block.

        emit(OpCode.LABEL, "fimTrueBlock");

        // Code for FALSE block
        emit(OpCode.BRANCH_IF_NOT_EQUAL, "$" + testReg, "$zero", "fimIf" + testReg);

        if (node.getChild(2).isPresent()) { // We have an else.
            visit(node.getChild(2).get()); // Generate FALSE block.
        }

        emit(OpCode.LABEL, "fimIf" + testReg);

        return -1; // This is not an expression, hence no value to return.
    }

    @Override
    protected Integer visitIntVal(AST node) {
        int x = newIntReg();
        IntData data = (IntData) node.getData();
        emit(OpCode.LOAD_INTEGER, "$"+Integer.toString(x), Integer.toString(data.getValue()));
        return x;
    }

    @Override
    protected Integer visitLt(AST node) {
        AST l = node.getChild(0).get();
        AST r = node.getChild(1).get();
        int y = visit(l);
        int z = visit(r);
        int x = newIntReg();
        emit(OpCode.LT, "$" + x, "$" + y, "$" + z);
        return x;
    }

    @Override
    protected Integer visitLte(AST node) {
        AST l = node.getChild(0).get();
        AST r = node.getChild(1).get();
        int y = visit(l);
        int z = visit(r);
        int x = newIntReg();
        emit(OpCode.LTE, "$" + x, "$" + y, "$" + z);
        return x;
    }

    @Override
    protected Integer visitGt(AST node) {
        AST l = node.getChild(0).get();
        AST r = node.getChild(1).get();
        int y = visit(l);
        int z = visit(r);
        int x = newIntReg();
        emit(OpCode.GT, "$" + x, "$" + y, "$" + z);
        return x;
    }

    @Override
    protected Integer visitGte(AST node) {
        AST l = node.getChild(0).get();
        AST r = node.getChild(1).get();
        int y = visit(l);
        int z = visit(r);
        int x = newIntReg();
        emit(OpCode.GTE, "$" + x, "$" + y, "$" + z);
        return x;
    }

	@Override
	protected Integer visitTimes(AST node) {
		int x;
	    int y = visit(node.getChild(0).get());
	    int z = visit(node.getChild(1).get());
	    if (node.getType() == Type.FLOAT_TYPE) {
	        x = newFloatReg();
	        emit(OpCode.MUL_F,  "$" + x, "$" + y, "$" + z);
	    } else {
	        x = newIntReg();
	        emit(OpCode.MUL_I, "$" + x, "$" + y, "$" + z);
	    }
	    return x;
	}

    @Override
    protected Integer visitOver(AST node) {
		int x;
	    int y = visit(node.getChild(0).get());
	    int z = visit(node.getChild(1).get());
	    if (node.getType() == Type.FLOAT_TYPE) {
	        x = newFloatReg();
	        emit(OpCode.DIV_F,  "$" + x, "$" + y, "$" + z);
	    } else {
	        x = newIntReg();
	        emit(OpCode.DIV_I, "$" + x, "$" + y, "$" + z);
	    }
	    return x;
    }

    @Override
    protected Integer visitMinus(AST node) {
		int x;
	    int y = visit(node.getChild(0).get());
	    int z = visit(node.getChild(1).get());
	    if (node.getType() == Type.FLOAT_TYPE) {
	        x = newFloatReg();
	        emit(OpCode.SUB_F,  "$" + x, "$" + y, "$" + z);
	    } else {
	        x = newIntReg();
	        emit(OpCode.SUB_I, "$" + x, "$" + y, "$" + z);
	    }
	    return x;
    }
    
    @Override
    protected Integer visitPlus(AST node) {
		int x;
	    int y = visit(node.getChild(0).get());
	    int z = visit(node.getChild(1).get());
	    if (node.getType() == Type.FLOAT_TYPE) {
	        x = newFloatReg();
	        emit(OpCode.ADD_F,  "$" + x, "$" + y, "$" + z);
	    } else {
	        x = newIntReg();
	        emit(OpCode.ADD_I, "$" + x, "$" + y, "$" + z);
	    }
	    return x;
    }

    @Override
    protected Integer visitRead(AST node) {
        AST var = node.getChild(1).get().getChild(0).get();
        VariableData varData = (VariableData) var.getData();
        int addr = varData.getIndex();
        int x;
        if (var.getType() == Type.INT_TYPE) {
            String varName = vt.getName(addr);
            x = newIntReg();
            emit(OpCode.LOAD_INTEGER, "$v0", "5");
            emit(OpCode.SYSCALL);
            emit(OpCode.STORE_WORD,"$v0", varName);
        } else if (var.getType() == Type.FLOAT_TYPE) {
            String varName = vt.getName(addr);
            x = newIntReg();
            emit(OpCode.LOAD_INTEGER, "$v0", "6");
            emit(OpCode.SYSCALL);
            emit(OpCode.STORE_WORD_C1,"$f0", varName);
        } else if (var.getType() == Type.BOOL_TYPE) {

        } else if (var.getType() == Type.STR_TYPE) {

        }
        return -1; // This is not an expression, hence no value to return.
    }

    @Override
    protected Integer visitWrite(AST node) {
        AST var = node.getChild(1).get().getChild(0).get();
        if(var.getKind() == NodeKind.VAR_USE_NODE) {
            VariableData varData = (VariableData) var.getData();
            int addr = varData.getIndex();
            int x;
            if (var.getType() == Type.INT_TYPE) {
                String varName = vt.getName(addr);
                x = newIntReg();
                    emit(OpCode.LOAD_WORD,"$a0", varName);
                    emit(OpCode.LOAD_INTEGER, "$v0", "1");
                    emit(OpCode.SYSCALL);
            }
        } else {

        }
        return -1;
    }

    @Override
    protected Integer visitRealVal(AST node) {
        int x = newFloatReg();
        RealData data = (RealData) node.getData();
        emitData(OpCode.FLOAT_DATA, "floatVar"+Integer.toString(x), Double.toString(data.getValue()));
        emit(OpCode.LOAD_FLOAT, "$f"+Integer.toString(x), "floatVar"+Integer.toString(x) );
        return x;
    }

    @Override
    protected Integer visitFor(AST node) {
        // Code for test.
        int currentReg = nextInstr;
        emit(OpCode.LABEL, "for" + currentReg);
        int testReg = visit(node.getChild(0).get());

        emit(OpCode.BRANCH_IF_EQUAL, "$" + testReg, "$zero", "fimFor" + testReg);

        // Code for TRUE block.
        visit(node.getChild(1).get()); // Generate TRUE block.

        emit(OpCode.JUMP, "for" + currentReg);

        emit(OpCode.LABEL, "fimFor");

        return -1; // This is not an expression, hence no value to return.
    }


    @Override
    protected Integer visitVarDecl(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitVarList(AST node) {
        Integer idx;
        Optional<AST> childOpt;

        for (idx = 0; idx < node.getChildren().size(); idx++) {
            childOpt = node.getChild(idx);

            if (childOpt.get().getKind() == NodeKind.ASSIGN_NODE) {
                AST VarNode = childOpt.get().getChild(0).get();

                visit(childOpt.get());
            } else {
                visit(childOpt.get());
            }
        }

        return -1;
    }

    // protected void createVar(AST varNode) {
    //     Type varType = varNode.getType();
    //     if(varType == Type.STR_TYPE) {
    //         emitData(OpCode.STRING_DATA, data, value);
    //     }
    // }

    @Override
    protected Integer visitVarUse(AST node) {
        // TODO Auto-generated method stub
        return null;
    }



    @Override
    protected Integer visitB2I(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitB2R(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitB2S(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitI2R(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitI2S(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitR2S(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitStrVal(AST node) {
        StringData data = (StringData) node.getData();
        int register = newIntReg();
        emit(OpCode.LOAD_ADDRESS, "$" + register, "string"+data.getValue());
        return register;
    }

}
