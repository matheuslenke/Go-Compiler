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
}
