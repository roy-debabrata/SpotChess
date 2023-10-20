package com.debabrata.spotchess.utils;

public class BitUtil {

    public static int getListBitPlaceValue(long singleBit){
        return Long.numberOfTrailingZeros(singleBit);
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
     * @param limitBitCountTo maximum number of bits to use in a permutation.
     * @return an array containing bitboards of all combinations of bits of the preMask. */
    public static long[] getAllPossibleBitCombinations(long preMask, int limitBitCountTo){
        int bitCount = Long.bitCount(preMask);
        int [] bitToPositionMapping = new int[bitCount];
        int i = 0;
        while ( preMask != 0 ){
            bitToPositionMapping[i++] = getListBitPlaceValue(preMask);
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
