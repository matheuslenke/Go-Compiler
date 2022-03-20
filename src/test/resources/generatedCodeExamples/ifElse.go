package samples

import fmt "fmt"

func main() {
	var booleano bool = true

	if booleano {
		fmt.Println("Booleano Verdadeiro")
	} else {
		fmt.Println("Booleano Falso")
	}

	if true {
		fmt.Println("Verdadeiro literal")
	}
	
	if false {
		fmt.Println("Falso literal")
	} else {
		fmt.Println("Falso literal vai para o else")
	}
}


