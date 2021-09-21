package com.debabrata.spotchess.magics;

import com.debabrata.spotchess.types.PieceType;

public class RookAndBishopMovesUtil {
    public static long[] getAllPossibleMovesCombinations(PieceType pieceType, int placeValue ){
        long mask = getPieceMask(pieceType, placeValue);
        long [] pieceCombinations = getAllPossiblepieceCombinations(mask);
        return getAllPossibleMovesCombinations(pieceType, placeValue, pieceCombinations);
    }

    public static long[] getAllPossibleMovesCombinations(PieceType pieceType, int placeValue, long[] pieceCombinations){
        long [] moves  = new long[pieceCombinations.length];
        for ( int i = 0; i < pieceCombinations.length; i++ ){
            moves[i] = getMoves(pieceType, placeValue, pieceCombinations[i]);
        }
        return moves;
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
    public static long[] getAllPossiblepieceCombinations(long preMask){
        int bitCount = Long.bitCount(preMask);
        int [] bitToPositionMapping = new int[bitCount];
        int i = 0;
        while ( preMask != 0 ){
            bitToPositionMapping[i++] = BitPositionUtil.getLastBitPlaceValue(preMask);
            preMask = preMask & (preMask - 1);
        }
        int comboCount = 1 << bitCount;
        long [] possibleBoardPositions = new long[comboCount];
        for ( int permutation = 0; permutation < comboCount; permutation ++){
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

    public static long getMoves(PieceType type, int placeValue, long pieceCombinations){
        long northMask = 0xFF00000000000000L;
        long eastMask  = 0x0101010101010101L;
        long southMask = 0x00000000000000FFL;
        long westMask  = 0x8080808080808080L;
        if ( type == PieceType.ROOK ){
            return rayFill( placeValue, pieceCombinations, 1, westMask)
                    ^ rayFill( placeValue, pieceCombinations, 8, northMask)
                    ^ rayFill( placeValue, pieceCombinations, -1, eastMask)
                    ^ rayFill( placeValue, pieceCombinations, -8, southMask);
        } else if ( type == PieceType.BISHOP ){
            return rayFill( placeValue, pieceCombinations, 9, northMask | westMask)
                    ^ rayFill( placeValue, pieceCombinations, 7, northMask | eastMask)
                    ^ rayFill( placeValue, pieceCombinations, -9, southMask | eastMask)
                    ^ rayFill( placeValue, pieceCombinations, -7, southMask | westMask);
        }
        return 0;
    }

    /* Works by shifting 1 bit at "placeValue" by "shift" bits (this essentially give the ray its direction) and
     * it keeps going till it hits either an edge defined by "rayEdgeMask" or a piece in the "pieceCombinations". If
     * correct edge mask is provided it will return from within the while loop. */
    public static long rayFill(int placeValue, long pieceCombinations, int shift, long rayEdgeMask){
        long moves = 0;
        long currentPosition = 1L << placeValue;
        while ( currentPosition != 0 ){
            if ((currentPosition & rayEdgeMask) != 0 || (currentPosition & pieceCombinations) != 0){
                moves = moves | currentPosition;
                return moves;
            }
            moves = moves | currentPosition;
            if ( shift < 0 ){
                currentPosition = currentPosition >>> (-shift);
            } else {
                currentPosition = currentPosition << shift;
            }
        }
        throw new RuntimeException("Check: " + placeValue + ", " + pieceCombinations + ", " + shift + ", " + rayEdgeMask);
    }

    public static long getPieceMask(PieceType pieceType, int placeValue){
        if ( pieceType == PieceType.ROOK ) {
            return getRookMask(placeValue);
        }
        return getBishopMask(placeValue);
    }

    public static long getRookMask(int placeValue){
        long fileH = 0x0101010101010101L;
        long rank0 = 0x00000000000000FFL;
        long fileHWithoutEdges = 0x0001010101010100L;
        long rank0WithoutEdges = 0x000000000000007EL;
        long border = 0xFF818181818181FFL;
        int file = placeValue % 8;
        int rank = placeValue / 8;
        long mask = ((fileH << file ) ^ (rank0 << (rank * 8)));
        mask = mask & (~ border);
        if ( file == 0 || file == 7 ) {
            mask = mask | (fileHWithoutEdges << file);
        }
        if ( rank == 0 || rank == 7 ) {
            mask = mask | (rank0WithoutEdges << (rank * 8));
        }
        mask = mask & ~ (1L << placeValue);
        return mask;
    }

    public static long getBishopMask(int placeValue){
        long position = 1L << placeValue;
        long mask = 0;
        long leftDiagonalSeed  = 0x80;
        long rightDiagonalSeed = 0x01;
        long border = 0xFF818181818181FFL;
        long leftDiagonalSelector = leftDiagonalSeed;
        long rightDiagonalSelector = rightDiagonalSeed;
        for ( int i = 0; i < 15; i++ ){
            if ((leftDiagonalSelector & position) != 0 ){
                mask ^= leftDiagonalSelector;
            }
            if ((rightDiagonalSelector & position) != 0 ){
                mask ^= rightDiagonalSelector;
            }
            leftDiagonalSeed = leftDiagonalSeed >>> 1;
            rightDiagonalSeed = (rightDiagonalSeed << 1) & 0xFF; /* To nullify after 7 shifts. */
            leftDiagonalSelector = (leftDiagonalSelector << 8) | leftDiagonalSeed;
            rightDiagonalSelector = (rightDiagonalSelector << 8) | rightDiagonalSeed;
        }
        return mask & ~border;
    }
}
