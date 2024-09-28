package com.debabrata.spotchess.tools;

public class MagicNumberAnalizer {
    static int SIDE = 8;

    public static void main(String [] args) {
         System.out.println("Boo hoo!");
        int index = Integer.parseInt(args[0]);

        printPattern((SIDE * SIDE) - index - 1);
	printFormula(index);
    }

    static void printFormula(int index) {
        for (int x = 0; x < SIDE * SIDE; x++) {
            if (!relaventX(index, x)) {
                continue;
	    }
	    System.out.print(",". repeat(SIDE * SIDE - x));
            for (int m = SIDE * SIDE - 1; m >= 0; m--) {
                System.out.print(" m" + m + "x" + x + ",");
	    }
	    System.out.println("\b ");
	}
    }

    static void printPattern(int index) {
	for (int i = 0; i < SIDE * SIDE; i++) {
            if (i % SIDE == 0) {
                System.out.println();
            }
            var sym = relaventX(index, i) ? "X" : "O";
	    System.out.print(sym);
	}
	System.out.println();
    }

    static boolean relaventX(int index, int target) {
	if (index == target) {
	    return false;
	}
	return (((index / SIDE) == (target / SIDE) && (target % SIDE > 0 && target % SIDE < SIDE - 1))
                 || ((index % SIDE) == (target % SIDE)) && (target / SIDE > 0 && target / SIDE < SIDE - 1));
    }
}
