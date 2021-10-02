package com.debabrata.spotchess.utils;

import com.debabrata.spotchess.types.Position;
import com.debabrata.spotchess.types.enums.PieceType;

import java.util.ArrayList;
import java.util.List;

public class MoveUtil {
    /* Right now we want to think about getting an application with a simple min-max search with alpha-beta pruning.
       We'll have to re-organize some of this for move ordering and other considerations. */
    public static int addMovesToBuffer(Position position, int[] moveBuffer, int startWritingAt ) {
        boolean whiteToMove = position.whiteToMove();
        long ourPieces, enemyPieces;
        if ( whiteToMove ) {
            ourPieces = position.getWhitePieces();
            enemyPieces = position.getBlackPieces();
        } else {
            ourPieces = position.getBlackPieces();
            enemyPieces = position.getWhitePieces();
        }
        long allPieces      = ourPieces | enemyPieces;
        long notOurPieces   = ~ ourPieces;
        long notPieces      = ~ allPieces;

        /* Pawn moves. */
        long pawns = position.getPawns() & ourPieces;
        if (whiteToMove) {
            long rank7Pawns = pawns & 0x00FF000000000000L; /* These pawns are about to promote. */
            pawns = pawns ^ rank7Pawns;
            /* Take care of pawn promotions. */
            while ( rank7Pawns != 0 ) {
                long pawn = rank7Pawns & -rank7Pawns;
                int from = BitPositionUtil.getBitPlaceValue(pawn);
                if ( ((pawn << 7) & 0x7F7F7F7F7F7F7F7FL & enemyPieces) != 0 ) {
                    moveBuffer[startWritingAt++] = MoveInitUtil.newPawnPromotion(from, from + 7, PieceType.QUEEN);
                    moveBuffer[startWritingAt++] = MoveInitUtil.newPawnPromotion(from, from + 7, PieceType.KNIGHT);
                    moveBuffer[startWritingAt++] = MoveInitUtil.newPawnPromotion(from, from + 7, PieceType.BISHOP);
                    moveBuffer[startWritingAt++] = MoveInitUtil.newPawnPromotion(from, from + 7, PieceType.ROOK);
                }
                if ( ((pawn << 9) & enemyPieces) != 0 ) {
                    moveBuffer[startWritingAt++] = MoveInitUtil.newPawnPromotion(from, from + 9, PieceType.QUEEN);
                    moveBuffer[startWritingAt++] = MoveInitUtil.newPawnPromotion(from, from + 9, PieceType.KNIGHT);
                    moveBuffer[startWritingAt++] = MoveInitUtil.newPawnPromotion(from, from + 9, PieceType.BISHOP);
                    moveBuffer[startWritingAt++] = MoveInitUtil.newPawnPromotion(from, from + 9, PieceType.ROOK);
                }
                if ( ((pawn << 8) & notPieces) != 0 ) {
                    moveBuffer[startWritingAt++] = MoveInitUtil.newPawnPromotion(from, from + 8, PieceType.QUEEN);
                    moveBuffer[startWritingAt++] = MoveInitUtil.newPawnPromotion(from, from + 8, PieceType.KNIGHT);
                    moveBuffer[startWritingAt++] = MoveInitUtil.newPawnPromotion(from, from + 8, PieceType.BISHOP);
                    moveBuffer[startWritingAt++] = MoveInitUtil.newPawnPromotion(from, from + 8, PieceType.ROOK);
                }
                rank7Pawns = rank7Pawns & (rank7Pawns - 1);
            }

            long straightMoves = (pawns << 8) & notPieces;
            long doubleMoves   = ((straightMoves & 0x0000000000FF0000L) << 8) & notPieces;
            long rightCaptures = ((pawns << 7) & 0x7F7F7F7F7F7F7F7FL) & enemyPieces;
            long leftCaptures  = ((pawns << 9) & 0xFEFEFEFEFEFEFEFEL) & enemyPieces;
            while ( rightCaptures != 0 ) {
                int toPlaceValue = BitPositionUtil.getLastBitPlaceValue(rightCaptures);
                moveBuffer[startWritingAt++] = MoveInitUtil.newMove(toPlaceValue - 7, toPlaceValue);
                rightCaptures = rightCaptures & ( rightCaptures - 1 );
            }
            while ( leftCaptures != 0 ) {
                int toPlaceValue = BitPositionUtil.getLastBitPlaceValue(leftCaptures);
                moveBuffer[startWritingAt++] = MoveInitUtil.newMove(toPlaceValue - 9, toPlaceValue);
                leftCaptures = leftCaptures & ( leftCaptures - 1 );
            }
            while ( straightMoves != 0 ) {
                int toPlaceValue = BitPositionUtil.getLastBitPlaceValue(straightMoves);
                moveBuffer[startWritingAt++] = MoveInitUtil.newMove(toPlaceValue - 8, toPlaceValue);
                straightMoves = straightMoves & ( straightMoves - 1 );
            }
            /* Taking care of white's double moves. */
            while ( doubleMoves != 0 ) {
                int toPlaceValue = BitPositionUtil.getLastBitPlaceValue(doubleMoves);
                moveBuffer[startWritingAt++] = MoveInitUtil.newPawnDoubleMove(toPlaceValue - 16, toPlaceValue);
                doubleMoves = doubleMoves & ( doubleMoves - 1 );
            }
        } else {
            long rank7Pawns = pawns & 0x000000000000FF00L; /* These pawns are about to promote. */
            pawns = pawns ^ rank7Pawns;

            /* Take care of pawn promotions. */
            while ( rank7Pawns != 0 ) {
                long pawn = rank7Pawns & -rank7Pawns;
                int from = BitPositionUtil.getBitPlaceValue(pawn);
                if ( ((pawn >>> 7) & 0x7F7F7F7F7F7F7F7FL & enemyPieces) != 0 ) {
                    moveBuffer[startWritingAt++] = MoveInitUtil.newPawnPromotion(from, from - 7, PieceType.QUEEN);
                    moveBuffer[startWritingAt++] = MoveInitUtil.newPawnPromotion(from, from - 7, PieceType.KNIGHT);
                    moveBuffer[startWritingAt++] = MoveInitUtil.newPawnPromotion(from, from - 7, PieceType.BISHOP);
                    moveBuffer[startWritingAt++] = MoveInitUtil.newPawnPromotion(from, from - 7, PieceType.ROOK);
                }
                if ( ((pawn >>> 9) & enemyPieces) != 0 ) {
                    moveBuffer[startWritingAt++] = MoveInitUtil.newPawnPromotion(from, from - 9, PieceType.QUEEN);
                    moveBuffer[startWritingAt++] = MoveInitUtil.newPawnPromotion(from, from - 9, PieceType.KNIGHT);
                    moveBuffer[startWritingAt++] = MoveInitUtil.newPawnPromotion(from, from - 9, PieceType.BISHOP);
                    moveBuffer[startWritingAt++] = MoveInitUtil.newPawnPromotion(from, from - 9, PieceType.ROOK);
                }
                if ( ((pawn >>> 8) & notPieces) != 0 ) {
                    moveBuffer[startWritingAt++] = MoveInitUtil.newPawnPromotion(from, from - 8, PieceType.QUEEN);
                    moveBuffer[startWritingAt++] = MoveInitUtil.newPawnPromotion(from, from - 8, PieceType.KNIGHT);
                    moveBuffer[startWritingAt++] = MoveInitUtil.newPawnPromotion(from, from - 8, PieceType.BISHOP);
                    moveBuffer[startWritingAt++] = MoveInitUtil.newPawnPromotion(from, from - 8, PieceType.ROOK);
                }
                rank7Pawns = rank7Pawns & (rank7Pawns - 1);
            }

            long straightMoves = (pawns >>> 8) & notPieces;
            long doubleMoves   = ((straightMoves & 0x0000FF0000000000L) >>> 8) & notPieces;
            long rightCaptures = ((pawns >>> 9) & 0x7F7F7F7F7F7F7F7FL) & enemyPieces;
            long leftCaptures  = ((pawns >> 7) & 0xFEFEFEFEFEFEFEFEL) & enemyPieces;
            while ( rightCaptures != 0 ) {
                int toPlaceValue = BitPositionUtil.getLastBitPlaceValue(rightCaptures);
                moveBuffer[startWritingAt++] = MoveInitUtil.newMove(toPlaceValue + 9, toPlaceValue);
                rightCaptures = rightCaptures & ( rightCaptures - 1 );
            }
            while ( leftCaptures != 0 ) {
                int toPlaceValue = BitPositionUtil.getLastBitPlaceValue(leftCaptures);
                moveBuffer[startWritingAt++] = MoveInitUtil.newMove(toPlaceValue + 7, toPlaceValue);
                leftCaptures = leftCaptures & ( leftCaptures - 1 );
            }
            while ( straightMoves != 0 ) {
                int toPlaceValue = BitPositionUtil.getLastBitPlaceValue(straightMoves);
                moveBuffer[startWritingAt++] = MoveInitUtil.newMove(toPlaceValue + 8, toPlaceValue);
                straightMoves = straightMoves & ( straightMoves - 1 );
            }
            /* Taking care of white's double moves. */
            while ( doubleMoves != 0 ) {
                int toPlaceValue = BitPositionUtil.getLastBitPlaceValue(doubleMoves);
                moveBuffer[startWritingAt++] = MoveInitUtil.newPawnDoubleMove(toPlaceValue + 16, toPlaceValue);
                doubleMoves = doubleMoves & ( doubleMoves - 1 );
            }
        }

        /* Taking care of en-passant moves. */
        if ( position.enPassantAvailable() ) {
            int from = BitPositionUtil.getBitPlaceValue(position.getPawnsThatCanCaptureEnPassant(whiteToMove));
            long toPos = position.getPawnToBeCapturedEnPassant(whiteToMove);
            int to = BitPositionUtil.getLastBitPlaceValue(toPos);
            moveBuffer[startWritingAt++] = MoveInitUtil.newEnPassant(from, to);
            toPos = toPos & (toPos - 1);
            if ( toPos != 0 ) {
                to = BitPositionUtil.getLastBitPlaceValue(toPos);
                moveBuffer[startWritingAt++] = MoveInitUtil.newEnPassant(from, to);
            }
        }

        /* Queen and bishop moves. */
        long queensAndBishops = position.getQueensAndBishops() & ourPieces;
        while (queensAndBishops != 0) {
            int fromPlaceValue = BitPositionUtil.getLastBitPlaceValue(queensAndBishops);
            long moves = RookAndBishopMovesUtil.getBishopMoves(fromPlaceValue, allPieces);
            moves = moves & notOurPieces;
            while ( moves != 0 ) {
                int toPlaceValue = BitPositionUtil.getLastBitPlaceValue(moves);
                moveBuffer[startWritingAt++] = MoveInitUtil.newMove(fromPlaceValue, toPlaceValue);
                moves = moves & ( moves - 1 );
            }
            queensAndBishops = queensAndBishops & (queensAndBishops - 1);
        }

        /* Rooks and queens moves. */
        long rooksAndQueens = position.getRooksAndQueens() & ourPieces;
        while ( rooksAndQueens != 0 ) {
            int fromPlaceValue = BitPositionUtil.getLastBitPlaceValue(rooksAndQueens);
            long moves = RookAndBishopMovesUtil.getRookMoves(fromPlaceValue, allPieces);
            moves = moves & notOurPieces;
            while ( moves != 0 ) {
                int toPlaceValue = BitPositionUtil.getLastBitPlaceValue(moves);
                moveBuffer[startWritingAt++] = MoveInitUtil.newMove(fromPlaceValue, toPlaceValue);
                moves = moves & ( moves - 1 );
            }
            rooksAndQueens = rooksAndQueens & (rooksAndQueens - 1);
        }

        /* Knight moves. */
        long knights = position.getKnights() & ourPieces;
        while ( knights != 0 ) {
            int fromPlaceValue = BitPositionUtil.getLastBitPlaceValue(knights);
            long moves = KingAndKnightMovesUtil.getKnightMoves(fromPlaceValue);
            moves = moves & notOurPieces;
            while ( moves != 0 ) {
                int toPlaceValue = BitPositionUtil.getLastBitPlaceValue(moves);
                moveBuffer[startWritingAt++] = MoveInitUtil.newMove(fromPlaceValue, toPlaceValue);
                moves = moves & ( moves - 1 );
            }
            knights = knights & (knights - 1);
        }

        /* King moves. */
        long king = position.getKings() & ourPieces;
        int kingFrom = BitPositionUtil.getLastBitPlaceValue(king);
        long moves = KingAndKnightMovesUtil.getKingMoves(kingFrom);
        moves = moves & notOurPieces;
        while (moves != 0) {
            int toPlaceValue = BitPositionUtil.getLastBitPlaceValue(moves);
            moveBuffer[startWritingAt++] = MoveInitUtil.newMove(kingFrom, toPlaceValue);
            moves = moves & (moves - 1);
        }

        // TODO: Worry about checks and castling some day.

        /* Putting -1 to mark the end of position where */
        moveBuffer[startWritingAt++] = -1;

        /* Returns the next position from which someone could start writing moves. */
        return startWritingAt;
    }

    public static List<Integer> getMovesInPosition(Position position) {
        List<Integer> result = new ArrayList<>();
        int [] moves = new int[300];
        addMovesToBuffer(position, moves, 0);
        int pos = 0, move;
        while ((move = moves[pos++]) != -1) {
            if ( move == 0 ) {
                continue;
            }
            result.add(move);
        }
        return result;
    }
}
