package br.ufes.edu.compiladores.code;

import java.util.Formatter;

public class MipsData {
    // Público para não precisar de getter/setter.
	public final OpCode op;
	// Estes campos não podem ser final por causa do backpatching...
	public String name;	// Operands, which can be int or float registers,
	public String value;

	public MipsData(OpCode dataType, String name, String value) {
		this.op = dataType;
		this.name = name;
		this.value = value;

	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Formatter f = new Formatter(sb);
		if (this.op == OpCode.DATA) {
			f.format("%s", this.op);
		} else {
			f.format("%s: ", this.name);
			if(this.op.opCount == 1) {
	
			}
			else if (this.op.opCount == 2) {
				f.format("%s %s", this.op, this.value);
			}
		}
		f.close();
		return sb.toString();
	}
	
	// Constantes
	
	// Basic arch: 32 int registers and 32 float registers.
	public static final int INT_REGS_COUNT   = 32;  // i0 to i31: int registers.
	public static final int FLOAT_REGS_COUNT = 32;	// f0 to f31: float registers.
	// The machine also has a dedicated program counter (PC) register.

	// Memory is split between data and instruction memory.
	// This is called the Harvard architecture, in contrast to the von Neumann
	// (stored program) architecture.
	public static final int INSTR_MEM_SIZE = 1024;	// instr_mem[]
	public static final int DATA_MEM_SIZE  = 1024;  // data_mem[]
	// The machine also has a string table str_tab[] for storing strings with
	// the command SSTR. Maximum size for each string is 128 chars.
}
