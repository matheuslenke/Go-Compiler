package main

import "fmt"

func main() {
	// Estrutura simples do for com iterador
	sum := 0
	for i := 0; i < 10; i++ {
		sum += i
	}

	// Primeiro e segundo parametros opcionais
	sum = 1
	for ; sum < 1000; {
		sum += sum
	}


	// Tambem utilizado como while
	for sum < 1000 {
		sum += sum
	}

	fmt.Println(sum)

	// Laco infinito
	for {

	}
}