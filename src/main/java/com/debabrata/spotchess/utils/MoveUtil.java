package com.debabrata.spotchess.utils;

import com.debabrata.spotchess.types.Position;
import com.debabrata.spotchess.types.enums.PieceType;

import java.util.ArrayList;
import java.util.List;

public class MoveUtil {
    /* Right now we want to think about getting an application with a simple min-max search with alpha-beta pruning.
       We'll have to re-organize some of this for move ordering and other considerations. */
    public static int addMovesToBuffer(Position position, int[] moveBuffer, int startWritingAt ) {
        // TODO: We need a isUnderCheck and addCheckedMovesToBuffer.
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
        long allPawns = position.getPawns();
        long pawns = allPawns & ourPieces;
        if (whiteToMove) {
            long rank7Pawns = pawns & 0x00FF000000000000L; /* These pawns are about to promote. */
            pawns = pawns ^ rank7Pawns;
            /* Take care of pawn promotions. */
            while ( rank7Pawns != 0 ) {
                long pawn = rank7Pawns & -rank7Pawns;
                int from = BitUtil.getBitPlaceValue(pawn);
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
                int toPlaceValue = BitUtil.getLastBitPlaceValue(rightCaptures);
                moveBuffer[startWritingAt++] = MoveInitUtil.newMove(toPlaceValue - 7, toPlaceValue);
                rightCaptures = rightCaptures & ( rightCaptures - 1 );
            }
            while ( leftCaptures != 0 ) {
                int toPlaceValue = BitUtil.getLastBitPlaceValue(leftCaptures);
                moveBuffer[startWritingAt++] = MoveInitUtil.newMove(toPlaceValue - 9, toPlaceValue);
                leftCaptures = leftCaptures & ( leftCaptures - 1 );
            }
            while ( straightMoves != 0 ) {
                int toPlaceValue = BitUtil.getLastBitPlaceValue(straightMoves);
                moveBuffer[startWritingAt++] = MoveInitUtil.newMove(toPlaceValue - 8, toPlaceValue);
                straightMoves = straightMoves & ( straightMoves - 1 );
            }
            /* Taking care of white's double moves. */
            while ( doubleMoves != 0 ) {
                int toPlaceValue = BitUtil.getLastBitPlaceValue(doubleMoves);
                moveBuffer[startWritingAt++] = MoveInitUtil.newPawnDoubleMove(toPlaceValue - 16, toPlaceValue);
                doubleMoves = doubleMoves & ( doubleMoves - 1 );
            }
        } else {
            long rank7Pawns = pawns & 0x000000000000FF00L; /* These pawns are about to promote. */
            pawns = pawns ^ rank7Pawns;

            /* Take care of pawn promotions. */
            while ( rank7Pawns != 0 ) {
                long pawn = rank7Pawns & -rank7Pawns;
                int from = BitUtil.getBitPlaceValue(pawn);
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
                int toPlaceValue = BitUtil.getLastBitPlaceValue(rightCaptures);
                moveBuffer[startWritingAt++] = MoveInitUtil.newMove(toPlaceValue + 9, toPlaceValue);
                rightCaptures = rightCaptures & ( rightCaptures - 1 );
            }
            while ( leftCaptures != 0 ) {
                int toPlaceValue = BitUtil.getLastBitPlaceValue(leftCaptures);
                moveBuffer[startWritingAt++] = MoveInitUtil.newMove(toPlaceValue + 7, toPlaceValue);
                leftCaptures = leftCaptures & ( leftCaptures - 1 );
            }
            while ( straightMoves != 0 ) {
                int toPlaceValue = BitUtil.getLastBitPlaceValue(straightMoves);
                moveBuffer[startWritingAt++] = MoveInitUtil.newMove(toPlaceValue + 8, toPlaceValue);
                straightMoves = straightMoves & ( straightMoves - 1 );
            }
            /* Taking care of white's double moves. */
            while ( doubleMoves != 0 ) {
                int toPlaceValue = BitUtil.getLastBitPlaceValue(doubleMoves);
                moveBuffer[startWritingAt++] = MoveInitUtil.newPawnDoubleMove(toPlaceValue + 16, toPlaceValue);
                doubleMoves = doubleMoves & ( doubleMoves - 1 );
            }
        }

        /* Taking care of en-passant moves. */
        if ( position.enPassantAvailable() ) {
            long fromPos = position.getPawnsThatCanCaptureEnPassant(whiteToMove);
            int from = BitUtil.getLastBitPlaceValue(fromPos);
            int to = BitUtil.getBitPlaceValue(position.getPawnLocationAfterEnPassant(whiteToMove));
            moveBuffer[startWritingAt++] = MoveInitUtil.newEnPassant(from, to);
            fromPos = fromPos & (fromPos - 1);
            if ( fromPos != 0 ) {
                from = BitUtil.getLastBitPlaceValue(fromPos);
                moveBuffer[startWritingAt++] = MoveInitUtil.newEnPassant(from, to);
            }
        }

        /* Queen and bishop moves. */
        long allQueensAndBishops = position.getQueensAndBishops();
        long queensAndBishops = allQueensAndBishops & ourPieces;
        while (queensAndBishops != 0) {
            int fromPlaceValue = BitUtil.getLastBitPlaceValue(queensAndBishops);
            long moves = RookAndBishopMovesUtil.getBishopMoves(fromPlaceValue, allPieces);
            moves = moves & notOurPieces;
            while ( moves != 0 ) {
                int toPlaceValue = BitUtil.getLastBitPlaceValue(moves);
                moveBuffer[startWritingAt++] = MoveInitUtil.newMove(fromPlaceValue, toPlaceValue);
                moves = moves & ( moves - 1 );
            }
            queensAndBishops = queensAndBishops & (queensAndBishops - 1);
        }

        /* Rooks and queens moves. */
        long allRooksAndQueens = position.getRooksAndQueens();
        long rooksAndQueens = allRooksAndQueens & ourPieces;
        while ( rooksAndQueens != 0 ) {
            int fromPlaceValue = BitUtil.getLastBitPlaceValue(rooksAndQueens);
            long moves = RookAndBishopMovesUtil.getRookMoves(fromPlaceValue, allPieces);
            moves = moves & notOurPieces;
            while ( moves != 0 ) {
                int toPlaceValue = BitUtil.getLastBitPlaceValue(moves);
                moveBuffer[startWritingAt++] = MoveInitUtil.newMove(fromPlaceValue, toPlaceValue);
                moves = moves & ( moves - 1 );
            }
            rooksAndQueens = rooksAndQueens & (rooksAndQueens - 1);
        }

        /* Knight moves. */
        long allKnights = position.getKnights();
        long knights = allKnights & ourPieces;
        while ( knights != 0 ) {
            int fromPlaceValue = BitUtil.getLastBitPlaceValue(knights);
            long moves = KingAndKnightMovesUtil.getKnightMoves(fromPlaceValue);
            moves = moves & notOurPieces;
            while ( moves != 0 ) {
                int toPlaceValue = BitUtil.getLastBitPlaceValue(moves);
                moveBuffer[startWritingAt++] = MoveInitUtil.newMove(fromPlaceValue, toPlaceValue);
                moves = moves & ( moves - 1 );
            }
            knights = knights & (knights - 1);
        }

        /* King moves. */
        long allKings = position.getKings();
        long king = allKings & ourPieces;
        int kingFrom = BitUtil.getLastBitPlaceValue(king);
        long moves = KingAndKnightMovesUtil.getKingMoves(kingFrom);
        moves = moves & notOurPieces;
        while (moves != 0) {
            int toPlaceValue = BitUtil.getLastBitPlaceValue(moves);
            moveBuffer[startWritingAt++] = MoveInitUtil.newMove(kingFrom, toPlaceValue);
            moves = moves & (moves - 1);
        }

        if (position.canPotentiallyCastle(whiteToMove)) {
            if (whiteToMove) {
                if (position.canPotentiallyCastleLeft(true)) {
                    if ((allPieces & 0x0000000000000070L) == 0) {
                        /* No pieces in the path. */
                        if ((allKnights & enemyPieces & 0x000000000078CC00L) == 0) {
                            /* No knights attack the path. */
                            if ((allKings & allPawns & enemyPieces & 0x0000000000007800L) == 0) {
                                /* No kings or pawns attack the path. */
                                long attackers = RookAndBishopMovesUtil.getBishopMoves(4, allPieces);
                                attackers = attackers | RookAndBishopMovesUtil.getBishopMoves(5, allPieces);

                                if ((allQueensAndBishops & attackers & enemyPieces) == 0) {
                                    /* No diagonal attackers. */
                                    attackers = RookAndBishopMovesUtil.getRookMoves(4, allPieces);
                                    attackers = attackers | RookAndBishopMovesUtil.getRookMoves(5, allPieces);
                                    if ((allRooksAndQueens & attackers & enemyPieces) == 0) {
                                        /* No lateral attackers. All checks done. */
                                        moveBuffer[startWritingAt++] = MoveInitUtil.newLeftCastle(3, 5);
                                    }
                                }
                            }
                        }
                    }
                }
                if (position.canPotentiallyCastleRight(true)) {
                    if ((allPieces & 0x0000000000000006L) == 0) {
                        if ((allKnights & enemyPieces & 0x00000000000F1900L) == 0) {
                            if ((allKings & allPawns & enemyPieces & 0x0000000000000F00L) == 0) {
                                long attackers = RookAndBishopMovesUtil.getBishopMoves(2, allPieces);
                                attackers = attackers | RookAndBishopMovesUtil.getBishopMoves(1, allPieces);

                                if ((allQueensAndBishops & attackers & enemyPieces) == 0) {
                                    attackers = RookAndBishopMovesUtil.getRookMoves(2, allPieces);
                                    attackers = attackers | RookAndBishopMovesUtil.getRookMoves(1, allPieces);
                                    if ((allRooksAndQueens & attackers & enemyPieces) == 0) {
                                        moveBuffer[startWritingAt++] = MoveInitUtil.newRightCastle(3, 1);
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                if (position.canPotentiallyCastleLeft(false)) {
                    if ((allPieces & 0x7000000000000000L) == 0) {
                        if ((allKnights & enemyPieces & 0x00CC780000000000L) == 0) {
                            if ((allKings & allPawns & enemyPieces & 0x0078000000000000L) == 0) {
                                long attackers = RookAndBishopMovesUtil.getBishopMoves(60, allPieces);
                                attackers = attackers | RookAndBishopMovesUtil.getBishopMoves(61, allPieces);

                                if ((allQueensAndBishops & attackers & enemyPieces) == 0) {
                                    attackers = RookAndBishopMovesUtil.getRookMoves(60, allPieces);
                                    attackers = attackers | RookAndBishopMovesUtil.getRookMoves(61, allPieces);
                                    if ((allRooksAndQueens & attackers & enemyPieces) == 0) {
                                        moveBuffer[startWritingAt++] = MoveInitUtil.newLeftCastle(59, 61);
                                    }
                                }
                            }
                        }
                    }
                }
                if (position.canPotentiallyCastleRight(false)) {
                    if ((allPieces & 0x0600000000000000L) == 0) {
                        if ((allKnights & enemyPieces & 0x00190F0000000000L) == 0) {
                            if ((allKings & allPawns & enemyPieces & 0x000F000000000000L) == 0) {
                                long attackers = RookAndBishopMovesUtil.getBishopMoves(4, allPieces);
                                attackers = attackers | RookAndBishopMovesUtil.getBishopMoves(5, allPieces);

                                if ((allQueensAndBishops & attackers & enemyPieces) == 0) {
                                    attackers = RookAndBishopMovesUtil.getRookMoves(4, allPieces);
                                    attackers = attackers | RookAndBishopMovesUtil.getRookMoves(4, allPieces);
                                    if ((allRooksAndQueens & attackers & enemyPieces) == 0) {
                                        moveBuffer[startWritingAt++] = MoveInitUtil.newRightCastle(59, 57);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

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
