package funcoes

import "fmt"

func funcao1() string {
	var str = "Hello World"

	return str
}

func somaDoisNumeros(a, b int) int {
	var c int = a + b

	return c
}

func funcao2() {
	funcao1()
	var a int = 1
	var b int = 2
	var c float32 = 5.5

	somaDoisNumeros(a, c)
}
