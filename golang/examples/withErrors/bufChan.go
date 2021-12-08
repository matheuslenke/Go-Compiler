package samples

import "fmt"

func goRoutineA(a <-chan int) {
	val := <-a
	fmt.Println("goRoutineA received the data", val)
}

func BufChan() {
	c := make(chan int, 2) // a buffered channel
	c <- 3
	c  5 // falta operador entre identificador e literal
	close(c)
	fmt.Println(len(c), cap(c)) // 2 2
	x ok := <-c // falta separador entre identificadores em declaração/atribuição

	x, ok = < c // Falta hifen da seta

	x, ok = - c // Operacao de assign invalida
}
