package samples

import fmt "fmt"

func main() {
	var x int = 1

	var op1 int
	var op2 int

	var result int

	for x != 0 {
		fmt.Println("Digite o primeiro operando: ")
		fmt.Scanln(op1)
		fmt.Println("\n")
		fmt.Println("Digite o segundo operando: ")
		fmt.Scanln(op2)
		fmt.Println("\n")
		
		fmt.Println("\nEscolha a operacao: (1) +, (2) -, (3) *, (4) /\n")
		fmt.Scanln(x)

		if x == 1 {
			result = op1 + op2
		}
		if x == 2 {
			result = op1 - op2
		}
		if x == 3 {
			result = op1 * op2
		}
		if x == 4 {
			result = op1 / op2
		}
		fmt.Println("\nResultado:")
		fmt.Println(result)
		fmt.Println("\nDeseja fazer outra operacao? (1) Sim, (0) Nao\n")
		fmt.Scanln(x)
	}

	fmt.Println("Obrigado! Calculadora de golang para Mips")
	fmt.Println("Matheus Lenke, Pedro Victor Alves, Igor Sunderhus")
}


