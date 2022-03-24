package samples

import "fmt"

func main() {
	var welcome = "Bem vindo ao código em Go!"
	fmt.Println(welcome)

	var inteiro int
	fmt.Scanln(inteiro)

	if inteiro < 0 {
		fmt.Println("Número negativo inválido!")
	}

	for inteiro < 20 {
		inteiro = inteiro + 1
	}

	var finalizarPrograma = true
}

