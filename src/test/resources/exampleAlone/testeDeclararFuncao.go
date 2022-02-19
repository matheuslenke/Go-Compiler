package sample

import "fmt"

func func1() {
	var s string = "func1"
	fmt.Println(s)
}

func func2(){
	func1()
	var s string = "func2"
	fmt.Println(s)
}