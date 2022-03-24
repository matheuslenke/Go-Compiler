.data:
booleano: .word 0
string0: .asciiz "Booleano Verdadeiro"
string1: .asciiz "Booleano Falso"
string2: .asciiz "Verdadeiro literal"
string3: .asciiz "Falso literal deu verdadeiro"
string4: .asciiz "Falso literal deu falso"
 .globl main
 .text:
 j main
main:
 li $8, 1
 sw $8, booleano
 lw $9, booleano
 beq $9, $zero, fimTrueBlock9
 la $a0, string0
 li $v0, 4
 syscall
fimTrueBlock9:
 bne $9, $zero, fimIf9
 la $a0, string1
 li $v0, 4
 syscall
fimIf9:
 li $10, 1
 beq $10, $zero, fimTrueBlock10
 la $a0, string2
 li $v0, 4
 syscall
fimTrueBlock10:
 bne $10, $zero, fimIf10
fimIf10:
 li $11, 0
 beq $11, $zero, fimTrueBlock11
 la $a0, string3
 li $v0, 4
 syscall
fimTrueBlock11:
 bne $11, $zero, fimIf11
 la $a0, string4
 li $v0, 4
 syscall
fimIf11:
endmain:
