package com.debabrata.spotchess.generators;

import com.debabrata.spotchess.console.GameStatePrinter;
import com.debabrata.spotchess.magics.RookAndBishopMovesUtil;
import com.debabrata.spotchess.types.PieceType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MagicHashGenerator {
    private static final int THREAD_POOL_SIZE = 4;

    public static void main(String[] args) {
        long mask = RookAndBishopMovesUtil.getRookMask(49);
        GameStatePrinter.printBitBoards(mask);
        System.out.println("\n\n\n");
        long[] combos = RookAndBishopMovesUtil.getAllPossiblePiecePlacements(
                RookAndBishopMovesUtil.getRookMask(49));
        long[] moves = RookAndBishopMovesUtil.getAllPossibleMovesCombinations(PieceType.ROOK, 49);
        System.out.println(isNumberMagic(combos, moves, 0x48FFFE99FECFAA00L, 55));
    }

    public static long searchForMagics(PieceType pieceType, int placeValue) {
        long mask = RookAndBishopMovesUtil.getPieceMask(pieceType, placeValue);
        long[] positionCombinations = RookAndBishopMovesUtil.getAllPossiblePiecePlacements(mask);
        long[] associatedMoves = RookAndBishopMovesUtil.getAllPossibleMovesCombinations(pieceType, placeValue);

        ExecutorService pool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        return 0;
    }

    public static boolean isNumberMagic(long [] pieceCombinations, long [] associatedMoves, long magic, int shiftCount) {
        if (shiftCount >= 64)
            return false;
        long[] occupations = new long[1 << (64 - shiftCount)];
        int happyCoincidences = 0;
        for (int i = 0; i < pieceCombinations.length; i++) {
            int position =  (int)((pieceCombinations[i] * magic) >>> shiftCount);
            if (occupations[position] == 0) {
                occupations[position] = associatedMoves[i];
            } else if (occupations[position] == associatedMoves[i]) {
                happyCoincidences++;
            } else {
                return false;
            }
        }
        System.out.println("Magic : " + Long.toHexString(magic)
                + "  Positive collisions : " + happyCoincidences
                + "  Bits Taken : " + (64 - shiftCount));
        return true;
    }
}