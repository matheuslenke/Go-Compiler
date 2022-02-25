package samples

import fmt "fmt"

func testeIf() {

	var a int = 2

	var b bool = true
	var c bool = false

	if (a > 0) {
		fmt.Println("A é maior que 0")
		if (a < 0) {			
			fmt.Println("A é menor que 0")
		} else {
			fmt.Println("A é maior que 0")
		}
	} else {
		fmt.Println("A é menor que 0")
	}

	if (b == true) {
		fmt.Println("True")
	} 
	
	if (b == false) {
		fmt.Println("False")
	}

	if a <= 2 {
		fmt.Println("A é menor igual a 2")
	} else {
			fmt.Println("A é maior igual a 2")
	}

	if (a >= 5) {
		fmt.Println("A maior igual a 5")
	} else {
		fmt.Println("A menor que 5")
	}
}