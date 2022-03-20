.data:
x: .word 0
op1: .word 0
op2: .word 0
result: .word 0
string0: .asciiz "Digite o primeiro operando: "
string1: .asciiz "\n"
string2: .asciiz "Digite o segundo operando: "
string3: .asciiz "\nEscolha a operacao: (1) +, (2) -, (3) *, (4) /\n"
string4: .asciiz "\nResultado:"
string5: .asciiz "\nDeseja fazer outra operacao? (1) Sim, (0) Nao\n"
string6: .asciiz "Obrigado! Calculadora de golang para Mips"
string7: .asciiz "Matheus Lenke, Pedro Victor Alves, Igor Sunderhus"
 .globl main
 .text:
 j main
main:
 li $8, 1
 sw $8, x
for6:
 lw $9, x
 li $10, 0
 seq $11, $9, $10
 bne $11, $zero, fimFor6
 la $a0, string0
 li $v0, 4
 syscall
 li $v0, 5
 syscall
 sw $v0, op1
 la $a0, string1
 li $v0, 4
 syscall
 la $a0, string2
 li $v0, 4
 syscall
 li $v0, 5
 syscall
 sw $v0, op2
 la $a0, string1
 li $v0, 4
 syscall
 la $a0, string3
 li $v0, 4
 syscall
 li $v0, 5
 syscall
 sw $v0, x
 lw $12, x
 li $13, 1
 seq $14, $12, $13
 beq $14, $zero, fimTrueBlock14
 lw $15, op1
 lw $16, op2
 add $17, $15, $16
 sw $17, result
fimTrueBlock14:
 bne $14, $zero, fimIf14
fimIf14:
 lw $18, x
 li $19, 2
 seq $20, $18, $19
 beq $20, $zero, fimTrueBlock20
 lw $21, op1
 lw $22, op2
 sub $23, $21, $22
 sw $23, result
fimTrueBlock20:
 bne $20, $zero, fimIf20
fimIf20:
 lw $24, x
 li $25, 3
 seq $8, $24, $25
 beq $8, $zero, fimTrueBlock8
 lw $8, op1
 lw $9, op2
 mul $10, $8, $9
 sw $10, result
fimTrueBlock8:
 bne $8, $zero, fimIf8
fimIf8:
 lw $11, x
 li $12, 4
 seq $13, $11, $12
 beq $13, $zero, fimTrueBlock13
 lw $14, op1
 lw $15, op2
 div $16, $14, $15
 sw $16, result
fimTrueBlock13:
 bne $13, $zero, fimIf13
fimIf13:
 la $a0, string4
 li $v0, 4
 syscall
 lw $a0, result
 li $v0, 1
 syscall
 la $a0, string5
 li $v0, 4
 syscall
 li $v0, 5
 syscall
 sw $v0, x
 j for6
fimFor6:
 la $a0, string6
 li $v0, 4
 syscall
 la $a0, string7
 li $v0, 4
 syscall
endmain:
