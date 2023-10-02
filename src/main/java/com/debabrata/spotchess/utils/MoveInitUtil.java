package com.debabrata.spotchess.utils;

import com.debabrata.spotchess.types.Position;
import com.debabrata.spotchess.types.enums.PieceType;

/**
 * @version 3.0
 * @author Debabrata Roy
 * comment: Positions are basically represented as a form-two bit pairs for the regular positions. Special moves have
 *          a different layout and listed below:
 * <p>
 *          This util to create and understand the move stored in the long. The methods should be inlined by JIT compiler.
 *          The storage details of he fields are as follows:
 * <p>
 *          0xFF00000000000000 indicates it's a special move.
 *          0x00000000000000FF part of it encodes the nature of the special move.
 * <p>
 *          0xFF00000000000001 Left castle.
 *          0xFF00000000000002 Right castle.
 *          0xFF00000000000004 En passant move.
 *          0xFF00000000000008 Double pawn move.
 *          0xFF00000000000010 Pawn promotes to Queen.
 *          0xFF00000000000020 Pawn promotes to Knight.
 *          0xFF00000000000030 Pawn promotes to Bishop.
 *          0xFF00000000000040 Pawn promotes to Rook.
 * <p>
 *          For pawn promotions we shift the from/to right/left by 8 in order to accomodate the space for the flag.
 *          To understand board orientation read the comments in Position. 0 can stand as an uninitialized move.
 */
public class MoveInitUtil {
    public static long getFrom(long move, Position position) {
        if (isSpecialMove(move)) {
            if (isPromotion(move)) {
                move = getPromotionMove(move, position.whiteToMove());
            } else if(isCastle(move)) {
                move = position.getKings();
            } else {
                move = move & 0x00FFFFFFFFFFFF00L;
            }
        }
        long side = position.whiteToMove() ? position.getWhitePieces() : position.getBlackPieces();
        return move & side;
    }

    public static long getTo(long move, Position position) {
        if (isSpecialMove(move)) {
            if (isPromotion(move)) {
                move = getPromotionMove(move, position.whiteToMove());
            } else if(isCastle(move)) {
                if (isLeftCastle(move)) {
                    move = position.getKings() << 2;
                } else {
                    move = position.getKings() >> 2;
                }
            } else {
                move = move & 0x00FFFFFFFFFFFF00L;
            }
        }
        long side = position.whiteToMove() ? position.getWhitePieces() : position.getBlackPieces();
        return move & ~side;
    }

    /**
     * Call isSpecialMove before calling this method.
     */
    public static boolean isCastle(long move) {
        return (move & 0x0000000000000003L) != 0;
    }

    /**
     * Call isSpecialMove before calling this method.
     */
    public static boolean isLeftCastle(long move) {
        return (move & 0x0000000000000001L) != 0;
    }

    /**
     * Call isSpecialMove before calling this method.
     */
    public static boolean isRightCastle(long move) {
        return (move & 0x0000000000000002L) != 0;
    }

    /**
     * Call isSpecialMove before calling this method.
     */
    public static boolean isPromotion(long move) {
        return (move & 0x0000000000000070L) != 0;
    }

    /**
     * Call isSpecialMove before calling this method.
     */
    public static long getPromotionMove(long move, boolean whiteToMove) {
        return whiteToMove ? (move & 0x00FFFFFFFFFFFF00L) << 8 : (move & 0x00FFFFFFFFFFFF00L) >>> 8;
    }

    /**
     * Call isSpecialMove before calling this method.
     */
    public static PieceType promotesTo(long move) {
        switch ((int) (move & 0x00000000000000F0L)) {
            case 0x00000010 : return PieceType.QUEEN;
            case 0x00000020 : return PieceType.KNIGHT;
            case 0x00000030 : return PieceType.BISHOP;
            case 0x00000040 : return PieceType.ROOK;
        }
        return null;
    }

    /**
     * Call isSpecialMove before calling this method.
     */
    public static boolean isEnPassant(long move) {
        return (move & 0x0000000000000004L) != 0;
    }

    /**
     * Call isSpecialMove before calling this method.
     */
    public static boolean isDoublePawnMove(long move) {
        return (move & 0x0000000000000008L) != 0;
    }

    public static boolean isSpecialMove(long move) {
        return (move & 0xFF00000000000000L) == 0xFF00000000000000L;
    }

    public static long newMove(long from, long to) {
        return from | to;
    }

    public static long newLeftCastle() {
        return 0xFF00000000000001L;
    }

    public static long newRightCastle() {
        return 0xFF00000000000002L;
    }

    public static long newEnPassant(long from, long to) {
        return to | from | 0xFF00000000000004L;
    }

    public static long newPawnDoubleMove(long from, long to) {
        return to | from | 0xFF00000000000008L;
    }

    public static long newPawnPromotion(long from, long to, boolean whiteToMove, PieceType promoteTo) {
        long move = whiteToMove ? (from | to) >>> 8 : (from | to) << 8;
        switch ( promoteTo ) {
            case QUEEN  : return move | 0xFF00000000000010L;
            case KNIGHT : return move | 0xFF00000000000020L;
            case BISHOP : return move | 0xFF00000000000030L;
            case ROOK   : return move | 0xFF00000000000040L;
            case PAWN   :
            case KING   : throw new RuntimeException("A pawn cannot promote to " + promoteTo.name());
        }
        throw new RuntimeException("Invalid promotion choice " + promoteTo);
    }
}
