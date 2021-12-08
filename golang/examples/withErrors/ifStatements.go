package main

import (
	"fmt"
	"math"
	"time"
)

// <-- If statements -->

func sqrt(x float64) string {
	// If sem chaves
	if x < 0 
	return fmt.Sprint(math.Sqrt(x))
}


func main() {
	fmt.Println(sqrt(2), sqrt(-4))

	// <-- Switch statements --> 
	switch os := "macos"; os {
	case 1234:
	case "test" // Case sem :
		fmt.Println("Linux.")
	}

	today := time.Now().Weekday()
	time.Saturday switch { // Ordem invertida
	}

	t := time.Now()
	switch {
	case t.Hour() << 12: // Comparador inexistente
		fmt.Println("Good morning!")
	}
}
