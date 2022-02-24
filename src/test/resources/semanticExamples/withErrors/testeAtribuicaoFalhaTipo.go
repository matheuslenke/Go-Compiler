package samples

import fmt "fmt"



func TesteAtribuicaoFalhaTipo() {
	var e float32 = -1.0 // +
	var numero int = 300

	var test string = "Teste"

	fmt.Println(test)

	var readVar string
	fmt.Scanln(readVar)
	// numero = e
	// fmt.Println(numero)

}
