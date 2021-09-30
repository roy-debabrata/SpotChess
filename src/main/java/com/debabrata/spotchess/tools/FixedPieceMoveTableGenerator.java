package com.debabrata.spotchess.tools;

public class FixedPieceMoveTableGenerator {
    public static void main(String[] args) {
        generateKingMoves();
        generateKnightMoves();
    }

    private static void generateKingMoves() {
        System.out.println("\nkingAttacks[] =");
        System.out.print("{ ");
        for ( int position = 0; position < 64; position++ ) {
            int hShift = position % 8;
            int vShift = position / 8;

            long vMap = 0;
            if ( hShift != 7 )
                vMap = vMap | 0x0101010101010101L << hShift + 1;
            if ( hShift != 0 )
                vMap = vMap | 0x0101010101010101L << hShift - 1;
            vMap = vMap | 0x0101010101010101L << hShift;

            long hMap = 0;
            if ( vShift != 7 )
                hMap = hMap | 0x00000000000000FFL << (8 * (vShift + 1));
            if ( vShift != 0 )
                hMap = hMap | 0x00000000000000FFL << (8 * (vShift - 1));
            hMap = hMap | 0x00000000000000FFL << (8 * vShift);

            long result = (vMap & hMap) ^ (1L << position);
            System.out.print("0x" + String.format("%016XL", result));
            if ( position != 63 ) {
                System.out.print(", ");
            }
            if ( (position + 1) % 4 == 0 ) {
                System.out.print("\n  ");
            }
        }
        System.out.print("\b\b};");
    }

    private static void generateKnightMoves() {
        System.out.println("\nknightAttacks[] =");
        System.out.print("{ ");
        long knightAt18 = 0x0000000A1100110AL;
        for ( int position = 0; position < 64; position++ ) {
            int hPos = position % 8;
            long result;
            if ( position < 18 ) {
                result = knightAt18 >>> (18 - position);
            } else {
                result = knightAt18 << (position - 18);
            }
            if ( hPos > 4) {
                result = result & 0xFCFCFCFCFCFCFCFCL;
            } else if ( hPos < 4) {
                result = result & 0x3F3F3F3F3F3F3F3FL;
            }

            System.out.print("0x" + String.format("%016XL", result));
            if ( position != 63 ) {
                System.out.print(", ");
            }
            if ( (position + 1) % 4 == 0 ) {
                System.out.print("\n  ");
            }
        }
        System.out.print("\b\b};");
    }
}
