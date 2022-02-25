package sample

import "fmt"

func func1() {
	var s string = "func1"
	fmt.Println(s)
	return
}

func func2() string {
	func1()
	var s string = "func1"
	fmt.Println(s)

	return s
}

func func3(a, b string) int {
	// var resultF1 string = func2()
	var s3, s4 string = "func1", "Hello world" + "t"

	var returnVar int = 5
	return returnVar
}

func func4() {
	var s5 = 1
}