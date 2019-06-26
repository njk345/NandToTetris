// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

// Put your code here.

//while (1) {
//	if (M[KBD] > 0) {
//		color = black;	
//	} else {
//		color = white;	
//	}
//	for (i = SCREEN; i < KBD; i++) {
//		M[i] = color;	
//	}
//}

(LOOP)
	@KBD
	D = M
	@BLACK
	D;JGT
	@color
	M = 0
	@PAINT
	0;JMP
(BLACK)
	@color
	M = -1
(PAINT)
	@SCREEN
	D = A
	@i
	M = D
(PAINTLOOP)
	@i
	D = M
	@KBD
	D = D - A
	@LOOP
	D;JGE
	@color
	D = M
	@i
	A = M
	M = D
	@i
	M = M + 1
	@PAINTLOOP
	0;JMP