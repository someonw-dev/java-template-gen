all:
	javac -parameters *.java

run: all
	java Test
