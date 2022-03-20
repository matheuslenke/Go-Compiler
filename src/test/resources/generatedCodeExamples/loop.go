package samples

import fmt "fmt"

func main() {
	var inteiro = 5

	for (inteiro > 0) {
		inteiro = inteiro - 1
		fmt.Println(inteiro)
	}

	for (inteiro <= 20) {
		inteiro = inteiro + 1
		fmt.Println(inteiro)
	}
	
	var inteiro2 = 3
	
	for (inteiro == 3) {
		inteiro = inteiro + 1
		fmt.Println(inteiro)
	}

	inteiro2 = 10
	
	for (inteiro > 5) {
		inteiro = inteiro / 2
		fmt.Println(inteiro)
	}

}


