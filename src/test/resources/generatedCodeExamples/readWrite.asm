.data:
floatVar0: .float 15.2
floatVar1: .float 20.35
textostr: .asciiz  "                              "
flutuante: .float 0.0
inteiro: .word 0
verdadeiro: .word 0
texto: .word 0
string0: .asciiz "Hello world!\n"
string1: .asciiz "String direta!\n"
string2: .asciiz "Digite um float:"
string3: .asciiz "Digite um inteiro:"
string4: .asciiz "Digite um booleano (1 para true ou 0 para false):"
string5: .asciiz "Digite uma string:"
 .globl main
 .text:
 j main
main:
 l.s $f0, floatVar0
 swc1 $f0, flutuante
 l.s $f12, flutuante
 li $v0, 2
 syscall
 l.s $f1, floatVar1
 l.s $f12, floatVar1
 li $v0, 2
 syscall
 li $8, 55
 sw $8, inteiro
 lw $a0, inteiro
 li $v0, 1
 syscall
 li $a0, 99
 li $v0, 1
 syscall
 li $9, 1
 sw $9, verdadeiro
 lw $a0, verdadeiro
 li $v0, 1
 syscall
 li $a0, 0
 li $v0, 1
 syscall
 la $10, string0
 sw $10, texto
 lw $a0, texto
 li $v0, 4
 syscall
 la $a0, string1
 li $v0, 4
 syscall
 la $a0, string2
 li $v0, 4
 syscall
 li $v0, 6
 syscall
 swc1 $f0, flutuante
 la $a0, string3
 li $v0, 4
 syscall
 li $v0, 5
 syscall
 sw $v0, inteiro
 la $a0, string4
 li $v0, 4
 syscall
 li $v0, 6
 syscall
 sw $v0, verdadeiro
 la $a0, string5
 li $v0, 4
 syscall
 li $v0, 8
 li $a1, 30
 la $a0, textostr
 syscall
 sw $a0, texto
 lw $a0, texto
 li $v0, 4
 syscall
endmain:
