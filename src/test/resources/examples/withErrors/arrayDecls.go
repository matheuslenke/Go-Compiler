package samples

import (
	"fmt"
)

func ArrayDecls() {
	/*[32]byte
	[2*N] struct { x, y int32 }
	[1000]*float64
	[3][5]int
	[2][2][2]float64  // same as [2]([2]([2]float64))
	*/
	var a [2string // Erro na declaração de array, falta fechar o tamanho do array
	a[0] = "Hello"
	a[1] = "World"
	fmt.Println(a[0], a[1])

	primes := 6]int{2, 3, 5, 7, 11, 13} // Erro na declaração e atribuição de array, falta começar o tamanho do array
	fmt.Println(primes)

	twoD [2[4]int // erro na declaração de matriz, falta fechar o numero de linhas e falta definição de variavel
	for i := 0; i < 2; i++ { // incrementar variavel faltando um sinal +
		for j := 0; j < 3; j++ { // Faltando ponto vírgula
			twoD[i][j] = i + j 
		}
	}
	fmt.Println("2d: ", twoD)
} // falta do ponto vírgula quebra o fim das chaves