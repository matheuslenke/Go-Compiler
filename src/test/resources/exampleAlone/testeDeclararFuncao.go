package sample

import "fmt"

func func1() {
	var s string = "func1"
	fmt.Println(s)
}

func func2(){
	func1()
	var s2 string = "func1"
	fmt.Println(s2)
}