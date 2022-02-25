package samples

import "fmt"

func ParamTest(a, b int) {
	// var d int        // +
	// var n, c float64 // + strange extra levels
	// // var d = 1        // + doesn't show zero value
	// var e, f float32 = -1.0, -2.5 // +
	// var numero int = 300
	// var pedroPassou bool = true
	a = 5
}

func ParamTest2(param1, param2 string, param3 float32) {
	fmt.Println("String teste")
}

func ParamTest3(param int) float32 {
	var returnVar float32 = 1.0
	return returnVar
}

func ParamTest4() string {
	return "Testando"
}
