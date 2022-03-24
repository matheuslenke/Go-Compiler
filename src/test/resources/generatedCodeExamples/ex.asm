.data:
welcome: .word 0
inteiro: .word 0
finalizarPrograma: .word 0
string0: .asciiz "Bem vindo ao código em Go!"
string1: .asciiz "Número negativo inválido!"
 .globl main
 .text:
 j main
main:
 la $8, string0
 sw $8, welcome
 lw $a0, welcome
 li $v0, 4
 syscall
 li $v0, 5
 syscall
 sw $v0, inteiro
 lw $9, inteiro
 li $10, 0
 slt $11, $9, $10
 beq $11, $zero, fimTrueBlock11
 la $a0, string1
 li $v0, 4
 syscall
fimTrueBlock11:
 bne $11, $zero, fimIf11
fimIf11:
for22:
 lw $12, inteiro
 li $13, 20
 slt $14, $12, $13
 beq $14, $zero, fimFor22
 lw $15, inteiro
 li $16, 1
 add $17, $15, $16
 sw $17, inteiro
 j for22
fimFor22:
 li $18, 1
 sw $18, finalizarPrograma
endmain:
