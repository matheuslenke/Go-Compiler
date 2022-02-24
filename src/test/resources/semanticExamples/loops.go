package samples

import "fmt"


func ForStmts() {
	var a,b int
	var c [1]int
	a, b = 1, 2
	for a < b {
		fmt.Println("From condition-only ForStmt")
		a = a + 1
	}

	for a <= b {
		fmt.Println("For running")
		a = a + 1
	}

	for a == a {
		fmt.Println("Infinity loop")
	}
}
