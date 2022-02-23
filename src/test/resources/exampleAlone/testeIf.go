package samples

import fmt "fmt"

func testeIf() {

	var a int = 2

	var b bool = true
	var c bool = false

	if (a > 0) {
		fmt.Println("A é maior que 0")
	} else {
		fmt.Println("A é menor que 0")
	}

	if (b == true) {
		fmt.Println("True")
	} else {
		fmt.Println("False")
	}

	if a <= 2 {
		fmt.Println("A é menor igual a 2")
	} else {
			fmt.Println("A é maior igual a 2")
	}
}