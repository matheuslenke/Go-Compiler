generate_flex:
	flex -o $(file).c $(file).l

compile_flex:
	gcc -o $(file).out $(file).c

run:
	flex -o $(file).c $(file).l
	gcc -o $(file).out $(file).c
	./$(file).out < teste.txt

clean:
	rm *.out
	rm *.c
