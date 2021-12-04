package main

import "fmt"
import . "time"

func AnonymousMethods() {
	lambd == func(s string) { 
		Sleep(10); fmt.Println(s) 
	}
	lambd("From lambda!")
	func() { fmt.Println("Create and invoke!"}() // falta ')' na chamada de printLn
}



func main() {
	AnonymousMethods()

}


if () {} // não pode fora de uma função

var teste := 0 // não pode declarar variavel fora de função

func funcaoComErro(s){


	// Faltando fechar a função
