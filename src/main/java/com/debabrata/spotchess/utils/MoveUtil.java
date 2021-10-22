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

        /* We try to determine the pinned pieces. */
        long allPawns = position.getPawns();
        long allQueensAndBishops = position.getQueensAndBishops();
        long allRooksAndQueens = position.getRooksAndQueens();
        long allKnights = position.getKnights();
        long allKings = position.getKings();

        long ourQueensAndBishops = allQueensAndBishops & ourPieces;
        long ourRooksAndQueens = allRooksAndQueens & ourPieces;
        long ourPawns = allPawns & ourPieces;
        long ourKnights = allKnights & ourPieces;

        long enPassantTakers = 0;
        if (position.enPassantAvailable()){
            enPassantTakers = position.getPawnsThatCanCaptureEnPassant(whiteToMove);
        }

        long enemyQueensAndBishops = allQueensAndBishops & enemyPieces;
        long enemyRooksAndQueens = allRooksAndQueens & enemyPieces;

        long king = allKings & ourPieces;
        int  kingPlaceValue = BitUtil.getBitPlaceValue(king);

        long diagonalPinners = RookAndBishopMovesUtil.getBishopPins(kingPlaceValue, allPieces);
        if ((diagonalPinners & enemyQueensAndBishops) != 0) {
            /* There is at least one potential pinner piece. */
            long semiMask = RookAndBishopMovesUtil.getBishopSemiMask(kingPlaceValue);
            long diagonalOne = diagonalPinners & semiMask;
            long diagonalTwo = diagonalPinners ^ diagonalOne;
            while (diagonalOne != 0) {
                /* Pin data is stored in pairs of pinners and pinned pieces. We don't know the orientation. */
                long pinPair = diagonalOne ^ (diagonalOne - 1);
                diagonalOne = diagonalOne & (diagonalOne - 1);
                pinPair = pinPair | (diagonalOne ^ (diagonalOne - 1));
                diagonalOne = diagonalOne & (diagonalOne - 1);
                long pinned = pinPair & ourPieces;
                if (pinned != 0 && (pinPair & enemyQueensAndBishops) != 0) {
                    /* One of our pieces is pinned. */
                    if ((pinned & ourRooksAndQueens) != 0) {
                        ourRooksAndQueens = ourRooksAndQueens ^ pinned; /* We remove the rook/queen from the reckoning. */
                    } else if ((pinned & ourKnights) != 0) {
                        ourKnights = ourKnights ^ pinned; /* We remove the knight from the reckoning. */
                    } else if ((pinned & ourPawns) != 0) {
                        /* We check if our pawn attacks the pinner. */
                        if (whiteToMove) {
                            if (((pinned << 9) & pinPair) != 0) {
                                int pawnPosition = BitUtil.getBitPlaceValue(pinned);
                                moveBuffer[startWritingAt++] = MoveInitUtil.newMove(pawnPosition, pawnPosition + 9); /* We take the pinner. */
                            } else if ((enPassantTakers & pinned) != 0) {
                                long locationAfterEP = position.getPawnLocationAfterEnPassant(true);
                                if ((pinned << 9) == locationAfterEP) {
                                    /* We take the pawn that could be taken en-passant without breaking the pin. */
                                    int pawnPosition = BitUtil.getBitPlaceValue(pinned);
                                    moveBuffer[startWritingAt++] = MoveInitUtil.newEnPassant(pawnPosition, pawnPosition + 9 );
                                    enPassantTakers = enPassantTakers ^ pinned;
                                }
                            }
                        } else {
                            if (((pinned >>> 9) & pinPair) != 0) {
                                int pawnPosition = BitUtil.getBitPlaceValue(pinned);
                                moveBuffer[startWritingAt++] = MoveInitUtil.newMove(pawnPosition, pawnPosition - 9); /* We take the pinner. */
                            } else if ((enPassantTakers & pinned) != 0) {
                                long locationAfterEP = position.getPawnLocationAfterEnPassant(true);
                                if ((pinned >>> 9) == locationAfterEP) {
                                    /* We take the pawn that could be taken en-passant without breaking the pin. */
                                    int pawnPosition = BitUtil.getBitPlaceValue(pinned);
                                    moveBuffer[startWritingAt++] = MoveInitUtil.newEnPassant(pawnPosition, pawnPosition - 9 );
                                    enPassantTakers = enPassantTakers ^ pinned;
                                }
                            }
                        }
                    }
                    if ((pinned & ourQueensAndBishops) != 0) {
                        /* The pin is only partial. We can attack the pinner and all squares in between. */
                        int pinnedPosition = BitUtil.getBitPlaceValue(pinned);
                        int pinnerPosition = BitUtil.getBitPlaceValue(pinned ^ pinPair);

                        while (pinnedPosition != pinnerPosition) {
                            moveBuffer[startWritingAt++] = MoveInitUtil.newMove(pinnedPosition, pinnerPosition);
                            if (pinnedPosition > pinnerPosition) {
                                pinnerPosition = pinnerPosition + 9;
                            } else {
                                pinnerPosition = pinnerPosition - 9;
                            }
                        }
                    }
                }
            }
            while (diagonalTwo != 0) {
                /* Pin data is stored in pairs of pinners and pinned pieces. We don't know the orientation. */
                long pinPair = diagonalTwo ^ (diagonalTwo - 1);
                diagonalTwo = diagonalTwo & (diagonalTwo - 1);
                pinPair = pinPair | (diagonalTwo ^ (diagonalTwo - 1));
                diagonalTwo = diagonalTwo & (diagonalTwo - 1);
                long pinned = pinPair & ourPieces;
                if (pinned != 0 && (pinPair & enemyQueensAndBishops) != 0) {
                    /* One of our pieces is pinned. */
                    if ((pinned & ourRooksAndQueens) != 0) {
                        ourRooksAndQueens = ourRooksAndQueens ^ pinned; /* We remove the rook/queen from the reckoning. */
                    } else if ((pinned & ourKnights) != 0) {
                        ourKnights = ourKnights ^ pinned; /* We remove the knight from the reckoning. */
                    } else if ((pinned & ourPawns) != 0) {
                        /* We check if our pawn attacks the pinner. */
                        if (whiteToMove) {
                            if (((pinned << 7) & pinPair) != 0) {
                                int pawnPosition = BitUtil.getBitPlaceValue(pinned);
                                moveBuffer[startWritingAt++] = MoveInitUtil.newMove(pawnPosition, pawnPosition + 7); /* We take the pinner. */
                            } else if ((enPassantTakers & pinned) != 0) {
                                long locationAfterEP = position.getPawnLocationAfterEnPassant(true);
                                if ((pinned << 7) == locationAfterEP) {
                                    /* We take the pawn that could be taken en-passant without breaking the pin. */
                                    int pawnPosition = BitUtil.getBitPlaceValue(pinned);
                                    moveBuffer[startWritingAt++] = MoveInitUtil.newEnPassant(pawnPosition, pawnPosition + 7 );
                                    enPassantTakers = enPassantTakers ^ pinned;
                                }
                            }
                        } else {
                            if (((pinned >>> 7) & pinPair) != 0) {
                                int pawnPosition = BitUtil.getBitPlaceValue(pinned);
                                moveBuffer[startWritingAt++] = MoveInitUtil.newMove(pawnPosition, pawnPosition - 7); /* We take the pinner. */
                            } else if ((enPassantTakers & pinned) != 0) {
                                long locationAfterEP = position.getPawnLocationAfterEnPassant(true);
                                if ((pinned >>> 7) == locationAfterEP) {
                                    /* We take the pawn that could be taken en-passant without breaking the pin. */
                                    int pawnPosition = BitUtil.getBitPlaceValue(pinned);
                                    moveBuffer[startWritingAt++] = MoveInitUtil.newEnPassant(pawnPosition, pawnPosition - 7 );
                                    enPassantTakers = enPassantTakers ^ pinned;
                                }
                            }
                        }
                    }
                    if ((pinned & ourQueensAndBishops) != 0) {
                        /* The pin is only partial. We can attack the pinner and all squares in between. */
                        int pinnedPosition = BitUtil.getBitPlaceValue(pinned);
                        int pinnerPosition = BitUtil.getBitPlaceValue(pinned ^ pinPair);

                        while (pinnedPosition != pinnerPosition) {
                            moveBuffer[startWritingAt++] = MoveInitUtil.newMove(pinnedPosition, pinnerPosition);
                            if (pinnedPosition > pinnerPosition) {
                                pinnerPosition = pinnerPosition + 7;
                            } else {
                                pinnerPosition = pinnerPosition - 7;
                            }
                        }
                    }
                }
            }
        }
        long lateralPinners = RookAndBishopMovesUtil.getRookPins(kingPlaceValue, allPieces);
        if ((lateralPinners & enemyRooksAndQueens) != 0) {
            /* There is at least one potential pinner piece. */
            long semiMask = RookAndBishopMovesUtil.getRookSemiMask(kingPlaceValue);
            long vertical = lateralPinners & semiMask;
            long horizontal = lateralPinners ^ vertical;
            while (vertical != 0) {
                /* Pin data is stored in pairs of pinners and pinned pieces. We don't know the orientation. */
                long pinPair = vertical ^ (vertical - 1);
                vertical = vertical & (vertical - 1);
                pinPair = pinPair | (vertical ^ (vertical - 1));
                vertical = vertical & (vertical - 1);
                long pinned = pinPair & ourPieces;
                if (pinned != 0 && (pinPair & enemyRooksAndQueens) != 0) {
                    /* One of our pieces is pinned. */
                    if ((pinned & ourQueensAndBishops) != 0) {
                        ourQueensAndBishops = ourQueensAndBishops ^ pinned; /* We remove the queen/bishop from the reckoning. */
                    } else if ((pinned & ourKnights) != 0) {
                        ourKnights = ourKnights ^ pinned; /* We remove the knight from the reckoning. */
                    } else if ((pinned & ourPawns) != 0) {
                        /* We check if our pawn can move vertically. */
                        if (whiteToMove) {
                            if (((pinned << 8) & notPieces) == 0) {
                                int pawnPosition = BitUtil.getBitPlaceValue(pinned);
                                moveBuffer[startWritingAt++] = MoveInitUtil.newMove(pawnPosition, pawnPosition + 8); /* We push the pawn. */
                                if (((pinned & 0x000000000000FF00L) != 0) &&((pinned << 16) & notPieces) == 0) {
                                    moveBuffer[startWritingAt++] = MoveInitUtil.newPawnDoubleMove(pawnPosition, pawnPosition + 16); /* We double push the pawn. */
                                }
                            }
                        } else {
                            if (((pinned >>> 8) & notPieces) == 0) {
                                int pawnPosition = BitUtil.getBitPlaceValue(pinned);
                                moveBuffer[startWritingAt++] = MoveInitUtil.newMove(pawnPosition, pawnPosition - 8); /* We push the pawn. */
                                if (((pinned & 0x00FF000000000000L) != 0) &&((pinned >>> 16) & notPieces) == 0) {
                                    moveBuffer[startWritingAt++] = MoveInitUtil.newPawnDoubleMove(pawnPosition, pawnPosition - 16); /* We double push the pawn. */
                                }
                            }
                        }
                    }
                    if ((pinned & ourRooksAndQueens) != 0) {
                        /* The pin is only partial. We can attack the pinner and all squares in between. */
                        int pinnedPosition = BitUtil.getBitPlaceValue(pinned);
                        int pinnerPosition = BitUtil.getBitPlaceValue(pinned ^ pinPair);

                        while (pinnedPosition != pinnerPosition) {
                            moveBuffer[startWritingAt++] = MoveInitUtil.newMove(pinnedPosition, pinnerPosition);
                            if (pinnedPosition > pinnerPosition) {
                                pinnerPosition = pinnerPosition + 8;
                            } else {
                                pinnerPosition = pinnerPosition - 8;
                            }
                        }
                    }
                }
            }
            while (horizontal != 0) {
                /* Pin data is stored in pairs of pinners and pinned pieces. We don't know the orientation. */
                long pinPair = horizontal ^ (horizontal - 1);
                horizontal = horizontal & (horizontal - 1);
                pinPair = pinPair | (horizontal ^ (horizontal - 1));
                horizontal = horizontal & (horizontal - 1);
                long pinned = pinPair & ourPieces;
                if (pinned != 0 && (pinPair & enemyRooksAndQueens) != 0) {
                    /* One of our pieces is pinned. */
                    if ((pinned & ourQueensAndBishops) != 0) {
                        ourQueensAndBishops = ourQueensAndBishops ^ pinned; /* We remove the queen/bishop from the reckoning. */
                    } else if ((pinned & ourKnights) != 0) {
                        ourKnights = ourKnights ^ pinned; /* We remove the knight from the reckoning. */
                    } else if ((pinned & ourPawns) != 0) {
                        ourPawns = ourPawns ^ pinned; /* Pawn is horizontally pinned we cannot move it. */
                    }
                    if ((pinned & ourRooksAndQueens) != 0) {
                        /* The pin is only partial. We can attack the pinner and all squares in between. */
                        int pinnedPosition = BitUtil.getBitPlaceValue(pinned);
                        int pinnerPosition = BitUtil.getBitPlaceValue(pinned ^ pinPair);

                        while (pinnedPosition != pinnerPosition) {
                            moveBuffer[startWritingAt++] = MoveInitUtil.newMove(pinnedPosition, pinnerPosition);
                            if (pinnedPosition > pinnerPosition) {
                                pinnerPosition = pinnerPosition + 1;
                            } else {
                                pinnerPosition = pinnerPosition - 1;
                            }
                        }
                    }
                }
            }
        }

        /* We add castling moves. */
        if (position.canPotentiallyCastle(whiteToMove)) {
            if (whiteToMove) {
                if (position.canPotentiallyCastleLeft(true)) {
                    if ((allPieces & 0x0000000000000070L) == 0) {
                        /* No pieces in the path. */
                        if ((allKnights & enemyPieces & 0x000000000078CC00L) == 0) {
                            /* No knights attack the path. */
                            if (((allKings | allPawns) & enemyPieces & 0x0000000000007800L) == 0) {
                                /* No kings or pawns attack the path. */
                                long attackers = RookAndBishopMovesUtil.getBishopMoves(4, allPieces);
                                attackers = attackers | RookAndBishopMovesUtil.getBishopMoves(5, allPieces);

                                if ((enemyQueensAndBishops & attackers) == 0) {
                                    /* No diagonal attackers. */
                                    attackers = RookAndBishopMovesUtil.getRookMoves(4, allPieces);
                                    attackers = attackers | RookAndBishopMovesUtil.getRookMoves(5, allPieces);
                                    if ((enemyRooksAndQueens & attackers) == 0) {
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
                            if (((allKings | allPawns) & enemyPieces & 0x0000000000000F00L) == 0) {
                                long attackers = RookAndBishopMovesUtil.getBishopMoves(2, allPieces);
                                attackers = attackers | RookAndBishopMovesUtil.getBishopMoves(1, allPieces);

                                if ((enemyQueensAndBishops & attackers) == 0) {
                                    attackers = RookAndBishopMovesUtil.getRookMoves(2, allPieces);
                                    attackers = attackers | RookAndBishopMovesUtil.getRookMoves(1, allPieces);
                                    if ((enemyRooksAndQueens & attackers) == 0) {
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
                            if (((allKings | allPawns) & enemyPieces & 0x0078000000000000L) == 0) {
                                long attackers = RookAndBishopMovesUtil.getBishopMoves(60, allPieces);
                                attackers = attackers | RookAndBishopMovesUtil.getBishopMoves(61, allPieces);

                                if ((enemyQueensAndBishops & attackers) == 0) {
                                    attackers = RookAndBishopMovesUtil.getRookMoves(60, allPieces);
                                    attackers = attackers | RookAndBishopMovesUtil.getRookMoves(61, allPieces);
                                    if ((enemyRooksAndQueens & attackers) == 0) {
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
                            if (((allKings | allPawns) & enemyPieces & 0x000F000000000000L) == 0) {
                                long attackers = RookAndBishopMovesUtil.getBishopMoves(4, allPieces);
                                attackers = attackers | RookAndBishopMovesUtil.getBishopMoves(5, allPieces);

                                if ((enemyQueensAndBishops & attackers) == 0) {
                                    attackers = RookAndBishopMovesUtil.getRookMoves(4, allPieces);
                                    attackers = attackers | RookAndBishopMovesUtil.getRookMoves(4, allPieces);
                                    if ((enemyRooksAndQueens & attackers) == 0) {
                                        moveBuffer[startWritingAt++] = MoveInitUtil.newRightCastle(59, 57);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        /* Pawn moves. */
        if (whiteToMove) {
            long rank7Pawns = ourPawns & 0x00FF000000000000L; /* These pawns are about to promote. */
            ourPawns = ourPawns ^ rank7Pawns;
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

            long straightMoves = (ourPawns << 8) & notPieces;
            long doubleMoves   = ((straightMoves & 0x0000000000FF0000L) << 8) & notPieces;
            long rightCaptures = ((ourPawns << 7) & 0x7F7F7F7F7F7F7F7FL) & enemyPieces;
            long leftCaptures  = ((ourPawns << 9) & 0xFEFEFEFEFEFEFEFEL) & enemyPieces;
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
            long rank7Pawns = ourPawns & 0x000000000000FF00L; /* These pawns are about to promote. */
            ourPawns = ourPawns ^ rank7Pawns;

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

            long straightMoves = (ourPawns >>> 8) & notPieces;
            long doubleMoves   = ((straightMoves & 0x0000FF0000000000L) >>> 8) & notPieces;
            long rightCaptures = ((ourPawns >>> 9) & 0x7F7F7F7F7F7F7F7FL) & enemyPieces;
            long leftCaptures  = ((ourPawns >> 7) & 0xFEFEFEFEFEFEFEFEL) & enemyPieces;
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
        if (enPassantTakers != 0) {
            int from = BitUtil.getLastBitPlaceValue(enPassantTakers);
            int to = BitUtil.getBitPlaceValue(position.getPawnLocationAfterEnPassant(whiteToMove));
            moveBuffer[startWritingAt++] = MoveInitUtil.newEnPassant(from, to);
            enPassantTakers = enPassantTakers & (enPassantTakers - 1);
            if ( enPassantTakers != 0 ) {
                from = BitUtil.getLastBitPlaceValue(enPassantTakers);
                moveBuffer[startWritingAt++] = MoveInitUtil.newEnPassant(from, to);
            }
        }

        /* Queen and bishop moves. */
        while (ourQueensAndBishops != 0) {
            int fromPlaceValue = BitUtil.getLastBitPlaceValue(ourQueensAndBishops);
            long moves = RookAndBishopMovesUtil.getBishopMoves(fromPlaceValue, allPieces);
            moves = moves & notOurPieces;
            while ( moves != 0 ) {
                int toPlaceValue = BitUtil.getLastBitPlaceValue(moves);
                moveBuffer[startWritingAt++] = MoveInitUtil.newMove(fromPlaceValue, toPlaceValue);
                moves = moves & ( moves - 1 );
            }
            ourQueensAndBishops = ourQueensAndBishops & (ourQueensAndBishops - 1);
        }

        /* Rook and queen moves. */
        while ( ourRooksAndQueens != 0 ) {
            int fromPlaceValue = BitUtil.getLastBitPlaceValue(ourRooksAndQueens);
            long moves = RookAndBishopMovesUtil.getRookMoves(fromPlaceValue, allPieces);
            moves = moves & notOurPieces;
            while ( moves != 0 ) {
                int toPlaceValue = BitUtil.getLastBitPlaceValue(moves);
                moveBuffer[startWritingAt++] = MoveInitUtil.newMove(fromPlaceValue, toPlaceValue);
                moves = moves & ( moves - 1 );
            }
            ourRooksAndQueens = ourRooksAndQueens & (ourRooksAndQueens - 1);
        }

        /* Knight moves. */
        while ( ourKnights != 0 ) {
            int fromPlaceValue = BitUtil.getLastBitPlaceValue(ourKnights);
            long moves = KingAndKnightMovesUtil.getKnightMoves(fromPlaceValue);
            moves = moves & notOurPieces;
            while ( moves != 0 ) {
                int toPlaceValue = BitUtil.getLastBitPlaceValue(moves);
                moveBuffer[startWritingAt++] = MoveInitUtil.newMove(fromPlaceValue, toPlaceValue);
                moves = moves & ( moves - 1 );
            }
            ourKnights = ourKnights & (ourKnights - 1);
        }

        /* King moves. */
        long moves = KingAndKnightMovesUtil.getKingMoves(kingPlaceValue);
        moves = moves & notOurPieces;
        while (moves != 0) {
            int toPlaceValue = BitUtil.getLastBitPlaceValue(moves);
            //TODO: Check if the king can be attacked after the move.
            moveBuffer[startWritingAt++] = MoveInitUtil.newMove(kingPlaceValue, toPlaceValue);
            moves = moves & (moves - 1);
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
