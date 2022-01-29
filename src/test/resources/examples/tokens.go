package main

import (
	"fmt"
	"math"
)
type tipoPersonalizado struct { // TYPE, IDENTIFIER, STRUCT
	
}

func main() {
	fmt.Println("Tokens.go")
	var x int = 0; // VAR, TYPE, ASSIGN, IDENTIFIER, SEMI
	y := 0; // DECLARE_ASSIGN
	
	if x < 0 { // IF, LESS, L_CURLY

	} else if x > 0 || x == 0 && x >= 1 {
		// ELSE, GREATER, LOGICAL_OR, LOGICAL_AND, EQUALS, GREATER_OR_EQUALS
	} else if x != 2 { // NOT_EQUALS

	} // R_CURLY

	
	z := true // IDENTIFIER, DECLARE_ASSIGN, TYPE_
	switch z { // SWITCH
	case true: // CASE, COLON
		fallthrough // FALLTHROUGH
	case false:
		break
	default: // DEFAULT
		break // BREAK
	}

	const string = "Test" // CONST, RAW_STRING_LITERAL

	defer fmt.Println("Algo") // DEFER, DOT, L_PAREN, R_PAREN

	p1 := &[]int{} // AMPERSAND, L_BRACKET, R_BRACKET, L_CURLY, R_CURLY

	

}

func sqrt(x float64) string {
	if x < 0 {
		return sqrt(-x) + "i"
	}
	return fmt.Sprint(math.Sqrt(x))
}