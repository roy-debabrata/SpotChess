package com.debabrata.spotchess.utils;

public class BitUtil {
    private static final long deBruijnMagic = 0x0218A3A4DAB967EFL;
    private static final int [] magicTranslate = {
            0,  1,  2,  7,  3, 13,  8, 19,
            4, 27, 14, 45,  9, 30, 20, 51,
            5, 17, 25, 28, 15, 37, 46, 39,
            10, 48, 34, 31, 41, 21, 59, 52,
            63,  6, 12, 18, 26, 44, 29, 50,
            16, 24, 36, 38, 47, 33, 40, 58,
            62, 11, 43, 49, 23, 35, 32, 57,
            61, 42, 22, 56, 60, 55, 54, 53,
    };

    public static int getBitPlaceValue(long singleBit){
        return magicTranslate[(int)((singleBit * deBruijnMagic) >>> 58)];
    }

    public static int getLastBitPlaceValue(long board){
        return magicTranslate[(int)(((board & -board) * deBruijnMagic) >>> 58)];
    }

    /**
     * Generate all possible combination of piece placements on the bit positions marked by the preMask.
     * Counting up from 0 and using least significant n bits as unique combinations, we map each of those bits to a bit
     * on the mask. Using an int to count up we can map up to 32 bits which is more than enough for our purposes.
     * No single piece attack as many positions.
     *
     * @param preMask gives the region for which to generate all possible combinations of values.
     *                Must not contain more than 32 active bits.
     * @return an array containing bitboards of all combinations of bits of the preMask. */
    public static long[] getAllPossibleBitCombinations(long preMask){
        return getAllPossibleBitCombinations(preMask, 0);
    }

    /**
     * Generate all possible combination of piece placements on the bit positions marked by the preMask.
     * Counting up from 0 and using least significant n bits as unique combinations, we map each of those bits to a bit
     * on the mask. Using an int to count up we can map up to 32 bits which is more than enough for our purposes.
     * No single piece attack as many positions.
     *
     * @param preMask gives the region for which to generate all possible combinations of values.
     *                Must not contain more than 32 active bits.
     * @param limitBitCountTo how many maximum number of bits to use.
     * @return an array containing bitboards of all combinations of bits of the preMask. */
    public static long[] getAllPossibleBitCombinations(long preMask, int limitBitCountTo){
        int bitCount = Long.bitCount(preMask);
        int [] bitToPositionMapping = new int[bitCount];
        int i = 0;
        while ( preMask != 0 ){
            bitToPositionMapping[i++] = getLastBitPlaceValue(preMask);
            preMask = preMask & (preMask - 1);
        }
        int comboCount = 1 << bitCount;
        long [] possibleBoardPositions = new long[comboCount];
        for ( int permutation = 0; permutation < comboCount; permutation ++){
            if (limitBitCountTo > 0 && Integer.bitCount(permutation) > limitBitCountTo) {
                continue;
            }
            long newBoardPosition = 0;
            int selector = 1;
            for ( int position : bitToPositionMapping ){
                if ((selector & permutation) != 0){
                    newBoardPosition = newBoardPosition | (1L << position);
                }
                selector = selector << 1;
            }
            possibleBoardPositions[permutation] = newBoardPosition;
        }
        return possibleBoardPositions;
    }
}
