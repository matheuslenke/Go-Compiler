.data:
inteiro: .word 0
inteiro2: .word 0
 .globl main
 .text:
 j main
main:
 li $8, 5
 sw $8, inteiro
for6:
 lw $9, inteiro
 li $10, 0
 sgt $11, $9, $10
 beq $11, $zero, fimFor6
 lw $12, inteiro
 li $13, 1
 sub $14, $12, $13
 sw $14, inteiro
 lw $a0, inteiro
 li $v0, 1
 syscall
 j for6
fimFor6:
for20:
 lw $15, inteiro
 li $16, 20
 sle $17, $15, $16
 beq $17, $zero, fimFor20
 lw $18, inteiro
 li $19, 1
 add $20, $18, $19
 sw $20, inteiro
 lw $a0, inteiro
 li $v0, 1
 syscall
 j for20
fimFor20:
 li $21, 3
 sw $21, inteiro2
for36:
 lw $22, inteiro
 li $23, 3
 seq $24, $22, $23
 beq $24, $zero, fimFor36
 lw $25, inteiro
 li $8, 1
 add $8, $25, $8
 sw $8, inteiro
 lw $a0, inteiro
 li $v0, 1
 syscall
 j for36
fimFor36:
 li $9, 10
 sw $9, inteiro2
for52:
 lw $10, inteiro
 li $11, 5
 sgt $12, $10, $11
 beq $12, $zero, fimFor52
 lw $13, inteiro
 li $14, 2
 div $15, $13, $14
 sw $15, inteiro
 lw $a0, inteiro
 li $v0, 1
 syscall
 j for52
fimFor52:
endmain:
