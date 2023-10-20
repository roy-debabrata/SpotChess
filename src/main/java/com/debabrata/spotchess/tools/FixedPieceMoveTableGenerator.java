package com.debabrata.spotchess.tools;

import com.debabrata.spotchess.utils.BitUtil;
import com.debabrata.spotchess.utils.KingAndKnightMovesUtil;
import com.debabrata.spotchess.utils.RookAndBishopMovesUtil;

import java.util.function.Function;

public class FixedPieceMoveTableGenerator {
    public static void main(String[] args) {
        print("kingAttacks", generateKingMoves());
        print("knightAttacks", generateKnightMoves());
        print("crosses", generateKingAttackerPatters(RookAndBishopMovesUtil::getRookMask));
        print("chevrons", generateKingAttackerPatters(RookAndBishopMovesUtil::getBishopMask));
        print("circles", generateKingAttackerPatters(KingAndKnightMovesUtil::getKnightMoves));
    }

    private static long[] generateKingMoves() {
        long [] results = new long[64];
        for ( int position = 0, i = 0; position < 64; position++, i++ ) {
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

            results[i] = (vMap & hMap) ^ (1L << position);
        }
        return results;
    }

    private static long[] generateKnightMoves() {
        long [] results = new long[64];
        long knightAt18 = 0x0000000A1100110AL;
        for ( int position = 0, i = 0; position < 64; position++, i++ ) {
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
            results[i] = result;
        }
        return results;
    }

    private static long[] generateKingAttackerPatters(Function<Integer, Long> attacks) {
        long [] kingMovesList = generateKingMoves();
        long [] results = new long[64];
        for(int i=0; i < 64; i++) {
            long kingMoves = kingMovesList[i];
            if (i == 3) {
                kingMoves |= 0x0000000000000022L;
            } else if (i == 59) {
                kingMoves |= 0x2200000000000000L;
            }
            long result = 0;
            for (; kingMoves != 0; kingMoves &= (kingMoves - 1)) {
                int placeValue = BitUtil.getListBitPlaceValue(kingMoves);
                result |= attacks.apply(placeValue);
            }
            results[i] = result & ~(1L << i);
        }
        return results;
    }

    private static void print(String name, long [] result) {
        System.out.print("\nprivate static final long [] " + name + " = {\n  ");
        for(int i = 0; i < result.length; i++) {
            System.out.print("0x" + String.format("%016XL", result[i]));
            if ( i != 63 ) {
                System.out.print(", ");
            }
            if ( (i + 1) % 4 == 0 ) {
                System.out.print("\n  ");
            }
        }
        System.out.println("};");
    }
}
