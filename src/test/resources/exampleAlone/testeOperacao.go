package samples

import fmt "fmt"


func testeOperacao() {
	var int1 int = -1 // +
	var int2 int = 300

	var add int = int1 + int2
	var sub int = int1 - int2
	var mul int = int1 * int2
	var div int = int1 / int2

	var eq bool = int1 == int2
	var lt bool = int1 < int2
	var gt bool = int1 > int2

	fmt.Println(add)
	fmt.Println(sub)
	fmt.Println(mul)
	fmt.Println(add)
	fmt.Println(div)
	fmt.Println(eq)
	fmt.Println(lt)
	fmt.Println(gt)
	
}
