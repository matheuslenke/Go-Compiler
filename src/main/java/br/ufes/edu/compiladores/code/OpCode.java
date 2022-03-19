package br.ufes.edu.compiladores.code;

public enum OpCode {
    HALT("halt", 0),
    NOOP("nop", 0),
	SYSCALL("syscall", 0),
	LOAD_ADDRESS("la", 2),
	LOAD_INTEGER("li", 2),
	LOAD_FLOAT("l.s", 2),
	LOAD_WORD("lw", 2),
	MOVE("move", 2),
	CODE(".text:", 0),
	DATA(".data:", 0),
	STRING_DATA(".asciiz ", 2),
	WORD_DATA(".word", 2),
	FLOAT_DATA(".float", 2),
	SPACE_DATA(".space", 2),
	BRANCH_IF_EQUAL("beq", 3),
	BRANCH_IF_NOT_EQUAL("bne", 3),
	JUMP("j", 1),
	JUMP_R("jr", 1),
	LABEL("", 1),
	FUNC_CALL("jal", 1),
	LT("slt", 3),
    GT("sgt", 3),
    LTE("sle", 3),
    GTE("sge", 3),
    EQ("seq", 3),
	STORE_WORD("sw", 2),
	STORE_WORD_C1("swc1", 2),
    MUL_F("mul.d", 3),
    MUL_I("mul", 3),
    ADD_F("add.d", 3),
    ADD_I("add", 3),
    SUB_F("sub.d", 3),
    SUB_I("sub", 3),
    DIV_F("div.d", 3),
    DIV_I("div", 3),
	GLOBAL(".globl", 1)
	;
    
    public final String name;
	public final int opCount;
	
	private OpCode(String name, int opCount) {
		this.name = name;
		this.opCount = opCount;
	}
	
	public String toString() {
		return this.name;
	}
}
