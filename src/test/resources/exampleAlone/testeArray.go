package arrays

func testeArrays() {

	var a [2]string
	var b [2]int
	var c [2]float32
	var d [2]float64
	var e [2]bool


	var IntVar int = 2
	b[0] = IntVar
	IntVar = b[0];

	var float32Var float32 = 5.0
	c[0] = float32Var
	float32Var = c[0]

	var float64Var float64 = 10.0
	d[0] = float64Var
	float64Var = d[0]

	var trueVar = true
	var falseVar = false
	e[0] = trueVar
	e[1] = true
	falseVar = e[1]


	var somaFloat = c[0]+ c[1]
}