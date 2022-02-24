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

func retornaDobro(a float32) float32 {
	var c = a * 2.0
	return c
}

func funcao2() {
	funcao1()
	var a int = 1
	var b int = 2
	var c float32 = 5.5
	var result int
	var array [2]int
	array[1] = somaDoisNumeros(a, b)

	result = somaDoisNumeros(array[0], array[1])

	c = retornaDobro(c)
}
