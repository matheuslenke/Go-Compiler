package br.ufes.edu.compiladores.code;

public enum OpCode {
    HALT("halt", 0),
    NOOP("nop", 0),
	SYSCALL("syscall", 0),
	LOAD_ADDRESS("la", 2),
	LOAD_INTEGER("li", 2),
	MOVE("move", 2),
	CODE(".text:\n", 0),
	DATA(".data:\n", 0),
	STRING_DATA(".asciiz ", 1),
	WORD_DATA(".word", 1),
	SPACE_DATA(".space", 1);
    
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
