package sample

import "fmt"

func func1() {
	var s string = "func1"
	fmt.Println(s)
	return
}

func func2() string {
	func1()
	var s2 string = "func1"
	fmt.Println(s2)

	return s2
}


func func3(a, b string) (string, string) {
	func1()
	var s3, s4 string = "func1", "Hello world" + "t"
	return s3, s4
}

func func4() {
	var s5 = 1
}