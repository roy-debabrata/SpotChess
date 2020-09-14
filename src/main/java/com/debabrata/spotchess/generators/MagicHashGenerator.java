package com.debabrata.spotchess.generators;

import com.debabrata.spotchess.console.GameStatePrinter;
import com.debabrata.spotchess.magics.RookAndBishopMovesUtil;
import com.debabrata.spotchess.types.PieceType;

import java.util.stream.IntStream;

public class MagicHashGenerator {
    public static void main(String[] args) {
        long [] combos = RookAndBishopMovesUtil.getAllPossiblePiecePlacements(
                RookAndBishopMovesUtil.getRookMask(7, true));
        long [] moves  = new long[combos.length];
        for ( int i = 0; i < combos.length; i++ ){
            moves[i] = RookAndBishopMovesUtil.getMoves(PieceType.ROOK, 7, combos[i]);
        }
        GameStatePrinter.printBitBoards(RookAndBishopMovesUtil.getRookMask(7, true));
        System.out.println("\n\n\n");
        IntStream.rangeClosed(0, Math.min(200, combos.length - 1))
                .forEach(pos -> {
            GameStatePrinter.printBitBoards(combos[pos], moves[pos]);
            System.out.println();
        });
    }
}
