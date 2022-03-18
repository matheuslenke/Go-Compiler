package br.ufes.edu.compiladores.code;

import static br.ufes.edu.compiladores.code.Instruction.INSTR_MEM_SIZE;

import br.ufes.edu.compiladores.ast.AST;
import br.ufes.edu.compiladores.tables.StrTable;
import br.ufes.edu.compiladores.tables.VarTable;
import br.ufes.edu.compiladores.typing.Type;
import br.ufes.edu.compiladores.ast.ASTBaseVisitor;
import br.ufes.edu.compiladores.ast.NodeKind;
import br.ufes.edu.compiladores.ast.VariableData;

import static br.ufes.edu.compiladores.code.OpCode.HALT;
import static br.ufes.edu.compiladores.code.OpCode.SYSCALL;
import static br.ufes.edu.compiladores.code.OpCode.MOVE;
import static br.ufes.edu.compiladores.code.OpCode.LOAD_INTEGER;
import static br.ufes.edu.compiladores.code.OpCode.LOAD_ADDRESS;
import static br.ufes.edu.compiladores.code.OpCode.CODE;

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
	    dumpStrTable();
        emit(CODE);
        emitData(OpCode.DATA, "");
	    visit(root);
	    dumpProgram();
	}
	
	// ----------------------------------------------------------------------------
	// Prints ---------------------------------------------------------------------

	void dumpProgram() {
	    for (int addr = 0; addr < nextData; addr++) {
	    	System.out.printf("%s\n", dataSection[addr].toString());
	    }
	    for (int addr = 0; addr < nextInstr; addr++) {
	    	System.out.printf("%s\n", code[addr].toString());
	    }
	}

	void dumpStrTable() {
	    for (int i = 0; i < st.getSize(); i++) {
	        System.out.printf(".asciiz %s\n", st.get(i));
	    }
	}
	
	// ----------------------------------------------------------------------------
	// Emits ----------------------------------------------------------------------

    private void emitData(OpCode op, String data) {
        MipsData newData = new MipsData(op, data);
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

    @Override
    protected Integer visitSourceFile(AST node) {
        Optional<AST> childOpt = node.getChild(0);
        visit(childOpt.get()); // ImportSpec
        // Visitando cada uma das funções
        Integer idx;

        for(idx = 1; idx < node.getChildren().size(); idx++ )  {
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
        Optional<AST> childOpt = node.getChild(0);
        visit(childOpt.get()); // Visitando os parâmetros

        childOpt = node.getChild(1);
        visit(childOpt.get()); // Visitando o bloco de código
        return -1;
    }

    @Override
    protected Integer visitFuncDecl(AST node) {
        Optional<AST> childOpt = node.getChild(0);
        visit(childOpt.get()); // Visitando os parâmetros

        childOpt = node.getChild(1);
        visit(childOpt.get()); // Visitando o bloco de código
        return -1;
    }

    @Override
    protected Integer visitParameters(AST node) {
        return -1;
    }

    @Override
    protected Integer visitAssign(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitEq(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitBlock(AST node) {
        Integer idx;
        Optional<AST> childOpt;

        for(idx = 1; idx < node.getChildren().size(); idx++ )  {
            childOpt = node.getChild(idx);
            visit(childOpt.get());
        }
        return -1;
    }

    @Override
    protected Integer visitBoolVal(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitIf(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitIntVal(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitLt(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitMinus(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitOver(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitPlus(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitRead(AST node) {
        AST var = node.getChild(1).get().getChild(0).get();
        VariableData varData = (VariableData) var.getData();
	    int addr = varData.getIndex();
	    int x;
	    if (var.getType() == Type.INT_TYPE) {
	    	x = newIntReg();
            emit(LOAD_INTEGER, "$v0", "5");
	        emit(SYSCALL);
	        emit(MOVE, "$" + Integer.toString(x), "$v0");
	    }
        //  else if (var.getType() == Type.REAL_TYPE) {
	    //     x = newFloatReg();
	    //     emit(CALL, 1, x);
	    //     emit(STWf, addr, x);
	    // } else if (var.getType() == Type.BOOL_TYPE) {
	    // 	x = newIntReg();
	    //     emit(CALL, 2, x);
	    //     emit(STWi, addr, x);
	    // } else { // Must be STR_TYPE
	    // 	x = newIntReg();
	    //     emit(CALL, 3, x);
	    //     emit(STWi, addr, x);
	    // }
	    return -1;  // This is not an expression, hence no value to return.
    }

    @Override
    protected Integer visitRealVal(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitRepeat(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitStrVal(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitTimes(AST node) {
        // TODO Auto-generated method stub
        return null;
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

        for(idx = 1; idx < node.getChildren().size(); idx++ )  {
            childOpt = node.getChild(idx);

            if(childOpt.get().getKind() == NodeKind.ASSIGN_NODE ) {
                
            } else {
                visit(childOpt.get());
            }
        }

        return -1;
    }

    @Override
    protected Integer visitVarUse(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitWrite(AST node) {
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

}
