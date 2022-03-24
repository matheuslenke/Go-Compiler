package samples

import fmt "fmt"

func read() {
	var flutuante float32 = 15.20
	fmt.Println(flutuante)
	fmt.Println(20.35)

	var inteiro int = 55
	fmt.Println(inteiro)
	fmt.Println(99)

	var verdadeiro bool = true
	fmt.Println(verdadeiro)
	fmt.Println(false)

	var texto string = "Hello world!\n"
	fmt.Println(texto)
	fmt.Println("String direta!\n")

	fmt.Println("Digite um float:")
	fmt.Scanln(flutuante)
	
	fmt.Println("Digite um inteiro:")
	fmt.Scanln(inteiro)

	fmt.Println("Digite um booleano (1 para true ou 0 para false):")
	fmt.Scanln(verdadeiro)

	fmt.Println("Digite uma string:")
	fmt.Scanln(texto)

	fmt.Println(texto)
}


