package funcoes

import "fmt"

func funcao1() string {
	var str = "Hello World"

	return str
}

func somaDoisNumeros(a, b int) float32 {
	var c int = a + b
	var d float32 = 1.0
	return d
}

func funcao2() {
	funcao1()
	var a int = 1
	var b int = 2
	var c float32 = 5.5
	var result float32
	result = somaDoisNumeros(a, b)
}
