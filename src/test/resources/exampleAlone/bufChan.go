package samples

import ("fmt"
	"reflect"
)

func ParamTest(a, b int) {
	var a int   // +
	var b, c float64 // + strange extra levels
	// var d = 1        // + doesn't show zero value
	var e, f float32 = -1, -2  // +
	// var (
	// 	g       int
	// 	h, i, j = 2.0, 3.0, "bar"
	// ) // + need to precise general text span
	// fmt.Println(a, b, c, d, e, f, g, h, i, j)

}