package com.debabrata.spotchess.utils;

import com.debabrata.spotchess.types.Position;
import com.debabrata.spotchess.types.enums.PieceType;

import java.util.ArrayList;
import java.util.List;

/*  This class has turned out to be an eyesore, all in the pursuit of using the minimum number of instructions to
    fetch all legal moves. I was also trying to minimise ram hits. Once this starts working I'll use this as a
    benchmark for testing out other move generation class ideas to get rid of this ugly duckling. I also need to
    start giving more thoughts to move ordering. That's what will ultimately dictate search cut-offs.  I'll consider
    in the future replacing this with a stateful class. Right now I wish all processors had 128 registers per core.*/
public class MoveUtil {
    /**
     * @return true if the side to move in the position has its king under check.
     **/
    public static boolean isKingUnderCheck(Position position) {
        boolean whiteToMove = position.whiteToMove();
        long enemyPieces;
        long allPieces;
        if ( whiteToMove ) {
            enemyPieces = position.getBlackPieces();
            allPieces = enemyPieces | position.getWhitePieces();
        } else {
            enemyPieces = position.getWhitePieces();
            allPieces = enemyPieces | position.getBlackPieces();
        }
        long allPawnsAndKnights = position.getPawnsAndKnights();
        long allKnightsAndKings = position.getKnightsAndKings();

        long allKnights = allPawnsAndKnights & allKnightsAndKings;
        long allKings = allKnightsAndKings ^ allKnights;
        long allPawns = allPawnsAndKnights ^ allKnights;

        long ourKing = allKings & ~enemyPieces;
        int  kingPlaceValue = BitUtil.getBitPlaceValue(ourKing);

        if ((KingAndKnightMovesUtil.getKnightMoves(kingPlaceValue) & enemyPieces & allKnights) != 0) {
            /* Enemy knights attack the position. */
            return true;
        }
        long attacks = RookAndBishopMovesUtil.getBishopMoves(kingPlaceValue, allPieces);
        if ((attacks & enemyPieces & position.getQueensAndBishops()) != 0) {
            /* Enemy bishops type attackers attack the position. */
            return true;
        }
        attacks = RookAndBishopMovesUtil.getRookMoves(kingPlaceValue, allPieces);
        if ((attacks & enemyPieces & position.getRooksAndQueens()) != 0) {
            /* Enemy rook type attackers attack the position. */
            return true;
        }
        if (whiteToMove) {
            /* Black pawns attack the position. */
            return ((ourKing << 7) & enemyPieces & allPawns & 0x7F7F7F7F7F7F7F7FL) != 0
                    || ((ourKing << 9) & enemyPieces & allPawns & 0xFEFEFEFEFEFEFEFEL) != 0;
        } else {
            /* White pawns attack the position. */
            return ((ourKing >>> 9) & enemyPieces & allPawns & 0x7F7F7F7F7F7F7F7FL) != 0
                    || ((ourKing >>> 7) & enemyPieces & allPawns & 0xFEFEFEFEFEFEFEFEL) != 0;
        }
    }

    /**
     * Writes all moves to the moveBuffer starting with "writePosition". At the end it returns a new writePosition for
     * where new moves can be written. If check/stalemated returns same writePosition as was given.
     * @return position on moveBuffer where new moves can be written.
     * */
    public static int addMovesToBuffer(Position position, int[] moveBuffer, int writePosition) {
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

        long allQueensAndBishops = position.getQueensAndBishops();
        long allRooksAndQueens = position.getRooksAndQueens();
        long allPawnsAndKnights = position.getPawnsAndKnights();
        long allKnightsAndKings = position.getKnightsAndKings();

        long allKnights = allPawnsAndKnights & allKnightsAndKings;
        long allKings = allKnightsAndKings ^ allKnights;
        long allPawns = allPawnsAndKnights ^ allKnights;

        long ourQueensAndBishops = allQueensAndBishops & ourPieces;
        long ourRooksAndQueens = allRooksAndQueens & ourPieces;
        long ourPawns = allPawns & ourPieces;
        long ourKnights = allKnights & ourPieces;
        long ourKing = allKings & ourPieces;

        long enemyQueensAndBishops = allQueensAndBishops & enemyPieces;
        long enemyRooksAndQueens = allRooksAndQueens & enemyPieces;
        long enemyPawns = allPawns & enemyPieces;
        long enemyKnights = allKnights & enemyPieces;
        long enemyKing = allKings & enemyPieces;

        long enPassantTakers = 0;
        if (position.enPassantAvailable()){
            enPassantTakers = position.getPawnsThatCanCaptureEnPassant(whiteToMove);
        }

        /* Checking if king is under check and adding moves to get out of them. */
        int kingPlaceValue = BitUtil.getBitPlaceValue(ourKing);
        int kingCheckCount = 0;
        long interventionPoints = 0;

        long attacker = KingAndKnightMovesUtil.getKnightMoves(kingPlaceValue) & enemyKnights;
        if (attacker != 0) {
            /* Enemy knight attacks the position. */
            kingCheckCount++;
            interventionPoints = interventionPoints | attacker; /* Added knight as position to be intervened at. */
        }
        long kingAttack = RookAndBishopMovesUtil.getBishopMoves(kingPlaceValue, allPieces);
        attacker = kingAttack & enemyQueensAndBishops; /* There can only be one at max. */
        if (attacker != 0) {
            /* Enemy bishops type attacker attacks the position. */
            kingCheckCount++;
            long semiMask = RookAndBishopMovesUtil.getBishopSemiMask(kingPlaceValue);
            if ((attacker & semiMask) == 0) {
                kingAttack = kingAttack & ~semiMask;
            } else {
                kingAttack = kingAttack & semiMask;
            }
            if ((attacker > ourKing || attacker == 0x8000000000000000L) && (ourKing != 0x8000000000000000L)) {
                interventionPoints = interventionPoints | (kingAttack & -ourKing);
            } else {
                interventionPoints = interventionPoints | (kingAttack & (ourKing - 1));
            }
        }
        if (kingCheckCount < 2) {
            kingAttack = RookAndBishopMovesUtil.getRookMoves(kingPlaceValue, allPieces);
            /* In this case there can be more than one in case pawn takes and promotes to same type and reveals a check.
             * For ex. axb8=Q where the king is at a8 and a queen at a6. */
            attacker = kingAttack & enemyRooksAndQueens;
            if (attacker != 0) {
                /* Enemy rook type attacker attacks the position. */
                long semiMask = RookAndBishopMovesUtil.getRookSemiMask(kingPlaceValue);

                if ((attacker & semiMask) != 0 && (attacker & ~semiMask) != 0) {
                    kingCheckCount = kingCheckCount + 2;
                } else {
                    kingCheckCount++;
                    if ((attacker & semiMask) == 0) {
                        kingAttack = kingAttack & ~semiMask;
                    } else {
                        kingAttack = kingAttack & semiMask;
                    }
                    if ((attacker > ourKing || attacker == 0x8000000000000000L) && (ourKing != 0x8000000000000000L)) {
                        interventionPoints = interventionPoints | (kingAttack & -ourKing);
                    } else {
                        interventionPoints = interventionPoints | (kingAttack & (ourKing - 1));
                    }
                }
            }
        }
        if (kingCheckCount < 2) {
            if (whiteToMove) {
                /* Selecting black pawns attack the position. */
                kingAttack = (((ourKing << 7) & 0x7F7F7F7F7F7F7F7FL) | ((ourKing << 9) & 0xFEFEFEFEFEFEFEFEL)) & enemyPawns;
            } else {
                /* Selecting white pawns attack the position. */
                kingAttack = (((ourKing >>> 9) & 0x7F7F7F7F7F7F7F7FL) | ((ourKing >>> 7) & 0xFEFEFEFEFEFEFEFEL)) & enemyPawns;
            }
            if ( kingAttack != 0 ) {
                /* There are attacking pawns. */
                kingCheckCount++;
                interventionPoints = interventionPoints | kingAttack;
            }
        }

        /* Excluding pinned pieces. Adding their moves to buffer if they are any (and we are not in check). */
        long notInterventionPoint = ~ interventionPoints;
        long diagonalPinners = RookAndBishopMovesUtil.getBishopPins(kingPlaceValue, allPieces);
        if ((diagonalPinners & enemyQueensAndBishops) != 0) {
            /* There is at least one potential pinner piece. */
            long semiMask = RookAndBishopMovesUtil.getBishopSemiMask(kingPlaceValue);
            long diagonalOne = diagonalPinners & semiMask;
            long diagonalTwo = diagonalPinners ^ diagonalOne;
            while (diagonalOne != 0) {
                /* Pin data is stored in pairs of pinners and pinned pieces. We don't know the orientation. */
                long pinPair = diagonalOne & -diagonalOne;
                diagonalOne = diagonalOne & (diagonalOne - 1);
                pinPair = pinPair | (diagonalOne & -diagonalOne);
                diagonalOne = diagonalOne & (diagonalOne - 1);
                long pinned = pinPair & ourPieces;
                if (pinned != 0 && (pinPair & enemyQueensAndBishops & notInterventionPoint) != 0) {
                    /* One of our pieces is pinned. */
                    if ((pinned & ourRooksAndQueens) != 0) {
                        ourRooksAndQueens = ourRooksAndQueens ^ pinned; /* We remove the rook/queen from the reckoning. */
                    } else if ((pinned & ourKnights) != 0) {
                        ourKnights = ourKnights ^ pinned; /* We remove the knight from the reckoning. */
                    } else if ((pinned & ourPawns) != 0) {
                        /* We check if our pawn attacks the pinner. */
                        if (kingCheckCount == 0) {
                            /* We must verify that we are not in check. */
                            if (whiteToMove) {
                                if (((pinned << 9) & pinPair) != 0) {
                                    int pawnPosition = BitUtil.getBitPlaceValue(pinned);
                                    moveBuffer[writePosition++] = MoveInitUtil.newMove(pawnPosition, pawnPosition + 9); /* We take the pinner. */
                                } else if ((enPassantTakers & pinned) != 0) {
                                    long locationAfterEP = position.getPawnLocationAfterEnPassant(true);
                                    if ((pinned << 9) == locationAfterEP) {
                                        /* We take the pawn that could be taken en-passant without breaking the pin. */
                                        int pawnPosition = BitUtil.getBitPlaceValue(pinned);
                                        moveBuffer[writePosition++] = MoveInitUtil.newEnPassant(pawnPosition, pawnPosition + 9);
                                    }
                                    enPassantTakers = enPassantTakers ^ pinned;
                                }
                            } else {
                                if (((pinned >>> 9) & pinPair) != 0) {
                                    int pawnPosition = BitUtil.getBitPlaceValue(pinned);
                                    moveBuffer[writePosition++] = MoveInitUtil.newMove(pawnPosition, pawnPosition - 9); /* We take the pinner. */
                                } else if ((enPassantTakers & pinned) != 0) {
                                    long locationAfterEP = position.getPawnLocationAfterEnPassant(true);
                                    if ((pinned >>> 9) == locationAfterEP) {
                                        /* We take the pawn that could be taken en-passant without breaking the pin. */
                                        int pawnPosition = BitUtil.getBitPlaceValue(pinned);
                                        moveBuffer[writePosition++] = MoveInitUtil.newEnPassant(pawnPosition, pawnPosition - 9);
                                    }
                                }
                            }
                        }
                        /* We remove the pawn from further reckoning. */
                        ourPawns = ourPawns ^ pinned;
                        enPassantTakers = enPassantTakers & ourPawns;
                    }
                    if ((pinned & ourQueensAndBishops) != 0) {
                        /* The pin is only partial. We can attack the pinner and all squares in between. */
                        if (kingCheckCount == 0) {
                            /* We must verify that we are not in check. */
                            int pinnedPosition = BitUtil.getBitPlaceValue(pinned);
                            int pinnerPosition = BitUtil.getBitPlaceValue(pinned ^ pinPair);

                            if (pinnerPosition > kingPlaceValue) {
                                while (pinnerPosition != kingPlaceValue) {
                                    if (pinnedPosition != pinnerPosition) {
                                        moveBuffer[writePosition++] = MoveInitUtil.newMove(pinnedPosition, pinnerPosition);
                                    }
                                    pinnerPosition = pinnerPosition - 9;
                                }
                            } else {
                                while (pinnerPosition != kingPlaceValue) {
                                    if (pinnedPosition != pinnerPosition) {
                                        moveBuffer[writePosition++] = MoveInitUtil.newMove(pinnedPosition, pinnerPosition);
                                    }
                                    pinnerPosition = pinnerPosition + 9;
                                }
                            }
                        }
                        ourQueensAndBishops = ourQueensAndBishops ^ pinned;
                    }
                }
            }
            while (diagonalTwo != 0) {
                /* Pin data is stored in pairs of pinners and pinned pieces. We don't know the orientation. */
                long pinPair = diagonalTwo & -diagonalTwo;
                diagonalTwo = diagonalTwo & (diagonalTwo - 1);
                pinPair = pinPair | (diagonalTwo & -diagonalTwo);
                diagonalTwo = diagonalTwo & (diagonalTwo - 1);
                long pinned = pinPair & ourPieces;
                if (pinned != 0 && (pinPair & enemyQueensAndBishops & notInterventionPoint) != 0) {
                    /* One of our pieces is pinned. */
                    if ((pinned & ourRooksAndQueens) != 0) {
                        ourRooksAndQueens = ourRooksAndQueens ^ pinned; /* We remove the rook/queen from the reckoning. */
                    } else if ((pinned & ourKnights) != 0) {
                        ourKnights = ourKnights ^ pinned; /* We remove the knight from the reckoning. */
                    } else if ((pinned & ourPawns) != 0) {
                        /* We check if our pawn attacks the pinner. */
                        if (kingCheckCount == 0) {
                            /* We must verify that we are not in check. */
                            if (whiteToMove) {
                                if (((pinned << 7) & pinPair) != 0) {
                                    int pawnPosition = BitUtil.getBitPlaceValue(pinned);
                                    /* We take the pinner. */
                                    moveBuffer[writePosition++] = MoveInitUtil.newMove(pawnPosition, pawnPosition + 7);
                                } else if ((enPassantTakers & pinned) != 0) {
                                    long locationAfterEP = position.getPawnLocationAfterEnPassant(true);
                                    if ((pinned << 7) == locationAfterEP) {
                                        /* We take the pawn that could be taken en-passant without breaking the pin. */
                                        int pawnPosition = BitUtil.getBitPlaceValue(pinned);
                                        moveBuffer[writePosition++] = MoveInitUtil.newEnPassant(pawnPosition, pawnPosition + 7);
                                    }
                                    enPassantTakers = enPassantTakers ^ pinned;
                                }
                            } else {
                                if (((pinned >>> 7) & pinPair) != 0) {
                                    int pawnPosition = BitUtil.getBitPlaceValue(pinned);
                                    /* We take the pinner. */
                                    moveBuffer[writePosition++] = MoveInitUtil.newMove(pawnPosition, pawnPosition - 7);
                                } else if ((enPassantTakers & pinned) != 0) {
                                    long locationAfterEP = position.getPawnLocationAfterEnPassant(true);
                                    if ((pinned >>> 7) == locationAfterEP) {
                                        /* We take the pawn that could be taken en-passant without breaking the pin. */
                                        int pawnPosition = BitUtil.getBitPlaceValue(pinned);
                                        moveBuffer[writePosition++] = MoveInitUtil.newEnPassant(pawnPosition, pawnPosition - 7);
                                    }
                                }
                            }
                        }
                        /* We remove the pawn from further reckoning. */
                        ourPawns = ourPawns ^ pinned;
                        enPassantTakers = enPassantTakers & ourPawns;
                    }
                    if ((pinned & ourQueensAndBishops) != 0) {
                        /* The pin is only partial. We can attack the pinner and all squares in between. */
                        if (kingCheckCount == 0) {
                            /* We must verify that we are not in check. */
                            int pinnedPosition = BitUtil.getBitPlaceValue(pinned);
                            int pinnerPosition = BitUtil.getBitPlaceValue(pinned ^ pinPair);

                            if (pinnerPosition > kingPlaceValue) {
                                while (pinnerPosition != kingPlaceValue) {
                                    if (pinnedPosition != pinnerPosition) {
                                        moveBuffer[writePosition++] = MoveInitUtil.newMove(pinnedPosition, pinnerPosition);
                                    }
                                    pinnerPosition = pinnerPosition - 7;
                                }
                            } else {
                                while (pinnerPosition != kingPlaceValue) {
                                    if (pinnedPosition != pinnerPosition) {
                                        moveBuffer[writePosition++] = MoveInitUtil.newMove(pinnedPosition, pinnerPosition);
                                    }
                                    pinnerPosition = pinnerPosition + 7;
                                }
                            }
                        }
                        ourQueensAndBishops = ourQueensAndBishops ^ pinned;
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
                long pinPair = vertical & -vertical;
                vertical = vertical & (vertical - 1);
                pinPair = pinPair | (vertical & -vertical);
                vertical = vertical & (vertical - 1);
                long pinned = pinPair & ourPieces;
                if (pinned != 0 && (pinPair & enemyRooksAndQueens & notInterventionPoint) != 0) {
                    /* One of our pieces is pinned. */
                    if ((pinned & ourQueensAndBishops) != 0) {
                        ourQueensAndBishops = ourQueensAndBishops ^ pinned; /* We remove the queen/bishop from the reckoning. */
                    } else if ((pinned & ourKnights) != 0) {
                        ourKnights = ourKnights ^ pinned; /* We remove the knight from the reckoning. */
                    } else if ((pinned & ourPawns) != 0) {
                        /* We check if our pawn can move vertically. */
                        if (kingCheckCount == 0) {
                            /* We must verify that we are not in check. */
                            if (whiteToMove) {
                                if (((pinned << 8) & notPieces) != 0) {
                                    int pawnPosition = BitUtil.getBitPlaceValue(pinned);
                                    /* We push the pawn. */
                                    moveBuffer[writePosition++] = MoveInitUtil.newMove(pawnPosition, pawnPosition + 8);
                                    if (((pinned & 0x000000000000FF00L) != 0) && ((pinned << 16) & notPieces) != 0) {
                                        /* We double push the pawn. */
                                        moveBuffer[writePosition++] = MoveInitUtil.newPawnDoubleMove(pawnPosition, pawnPosition + 16);
                                    }
                                }
                            } else {
                                if (((pinned >>> 8) & notPieces) != 0) {
                                    int pawnPosition = BitUtil.getBitPlaceValue(pinned);
                                    /* We push the pawn. */
                                    moveBuffer[writePosition++] = MoveInitUtil.newMove(pawnPosition, pawnPosition - 8);
                                    if (((pinned & 0x00FF000000000000L) != 0) && ((pinned >>> 16) & notPieces) != 0) {
                                        /* We double push the pawn. */
                                        moveBuffer[writePosition++] = MoveInitUtil.newPawnDoubleMove(pawnPosition, pawnPosition - 16);
                                    }
                                }
                            }
                        }
                        /* We remove the pawn from further reckoning. */
                        ourPawns = ourPawns ^ pinned;
                        enPassantTakers = enPassantTakers & ourPawns;
                    }
                    if ((pinned & ourRooksAndQueens) != 0) {
                        /* The pin is only partial. We can attack the pinner and all squares in between. */
                        if (kingCheckCount == 0) {
                            /* We must verify that we are not in check. */
                            int pinnedPosition = BitUtil.getBitPlaceValue(pinned);
                            int pinnerPosition = BitUtil.getBitPlaceValue(pinned ^ pinPair);

                            if (pinnerPosition > kingPlaceValue) {
                                while (pinnerPosition != kingPlaceValue) {
                                    if (pinnedPosition != pinnerPosition) {
                                        moveBuffer[writePosition++] = MoveInitUtil.newMove(pinnedPosition, pinnerPosition);
                                    }
                                    pinnerPosition = pinnerPosition - 8;
                                }
                            } else {
                                while (pinnerPosition != kingPlaceValue) {
                                    if (pinnedPosition != pinnerPosition) {
                                        moveBuffer[writePosition++] = MoveInitUtil.newMove(pinnedPosition, pinnerPosition);
                                    }
                                    pinnerPosition = pinnerPosition + 8;
                                }
                            }
                        }
                        ourRooksAndQueens = ourRooksAndQueens ^ pinned;
                    }
                }
            }
            while (horizontal != 0) {
                /* Pin data is stored in pairs of pinners and pinned pieces. We don't know the orientation. */
                long pinPair = horizontal & -horizontal;
                horizontal = horizontal & (horizontal - 1);
                pinPair = pinPair | (horizontal & -horizontal);
                horizontal = horizontal & (horizontal - 1);
                long pinned = pinPair & ourPieces;
                if (pinned != 0 && (pinPair & enemyRooksAndQueens & notInterventionPoint) != 0) {
                    /* One of our pieces is pinned. */
                    if ((pinned & ourQueensAndBishops) != 0) {
                        /* We remove the queen/bishop from the reckoning. */
                        ourQueensAndBishops = ourQueensAndBishops ^ pinned;
                    } else if ((pinned & ourKnights) != 0) {
                        ourKnights = ourKnights ^ pinned; /* We remove the knight from the reckoning. */
                    } else if ((pinned & ourPawns) != 0) {
                        /* Pawn is horizontally pinned we cannot move it. */
                        ourPawns = ourPawns ^ pinned;
                        enPassantTakers = enPassantTakers & ourPawns;
                    }
                    if ((pinned & ourRooksAndQueens) != 0) {
                        /* The pin is only partial. We can attack the pinner and all squares in between. */
                        if (kingCheckCount == 0) {
                            /* We must verify that we are not in check. */
                            int pinnedPosition = BitUtil.getBitPlaceValue(pinned);
                            int pinnerPosition = BitUtil.getBitPlaceValue(pinned ^ pinPair);

                            if (pinnerPosition > kingPlaceValue) {
                                while (pinnerPosition != kingPlaceValue) {
                                    if (pinnedPosition != pinnerPosition) {
                                        moveBuffer[writePosition++] = MoveInitUtil.newMove(pinnedPosition, pinnerPosition);
                                    }
                                    pinnerPosition = pinnerPosition - 1;
                                }
                            } else {
                                while (pinnerPosition != kingPlaceValue) {
                                    if (pinnedPosition != pinnerPosition) {
                                        moveBuffer[writePosition++] = MoveInitUtil.newMove(pinnedPosition, pinnerPosition);
                                    }
                                    pinnerPosition = pinnerPosition + 1;
                                }
                            }
                        }
                        ourRooksAndQueens = ourRooksAndQueens ^ pinned;
                    }
                }
            }
        }
        /* Checking for extreme edge case where an en-passant take move can expose the king to a rook attack. */
        if (enPassantTakers != 0 && (lateralPinners & enPassantTakers) != 0 ) {
            long enPassantTaken = position.getPawnToBeCapturedEnPassant(whiteToMove);
            if ((enPassantTaken & lateralPinners) != 0) {
                /* Both the en-passant taker and pawn to be taken are in the same line with the King. */
                boolean enPassantMoveIsInvalid = false;
                long rank = whiteToMove? 0x000000FF00000000L : 0x00000000FF000000L;
                long enPassantTaker = lateralPinners & enPassantTakers;

                long selector = ourKing >> 1;
                while ((selector & rank) != 0) {
                    if (selector != enPassantTaken && selector != enPassantTaker && (selector & allPieces) != 0) {
                        /* We have hit a piece. */
                        if ((selector & enemyRooksAndQueens) != 0) {
                            enPassantMoveIsInvalid = true;
                        }
                        break;
                    }
                    selector = selector >> 1;
                }
                if (! enPassantMoveIsInvalid) {
                    selector = ourKing << 1;
                    while ((selector & rank) != 0) {
                        if (selector != enPassantTaken && selector != enPassantTaker && (selector & allPieces) != 0) {
                            /* We have hit a piece. */
                            if ((selector & enemyRooksAndQueens) != 0) {
                                enPassantMoveIsInvalid = true;
                            }
                            break;
                        }
                        selector = selector << 1;
                    }
                }
                if (enPassantMoveIsInvalid) {
                    enPassantTakers = enPassantTakers ^ enPassantTaker;
                }
            }
        }

        /* If we are in check, now that our pinned pieces have been removed, we look for pieces that can intercede. */
        if (kingCheckCount == 1) {
            /* In case of multiple attackers the only option is for the king to move. So, we checked it's not the case. */
            if (whiteToMove) {
                /* Pawn takes moves. */
                long takers = ((interventionPoints & enemyPieces) >>> 9) & ourPawns & 0x7F7F7F7F7F7F7F7FL;
                while (takers != 0) {
                    int moveFrom = BitUtil.getLastBitPlaceValue(takers);
                    if (moveFrom < 48) {
                        moveBuffer[writePosition++] = MoveInitUtil.newMove(moveFrom, moveFrom + 9);
                    } else {
                        moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(moveFrom, moveFrom + 9, PieceType.QUEEN);
                        moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(moveFrom, moveFrom + 9, PieceType.KNIGHT);
                        moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(moveFrom, moveFrom + 9, PieceType.BISHOP);
                        moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(moveFrom, moveFrom + 9, PieceType.ROOK);
                    }
                    takers = takers & (takers - 1);
                }
                takers = ((interventionPoints & enemyPieces) >>> 7) & ourPawns & 0xFEFEFEFEFEFEFEFEL;
                while (takers != 0) {
                    int moveFrom = BitUtil.getLastBitPlaceValue(takers);
                    if (moveFrom < 48) {
                        moveBuffer[writePosition++] = MoveInitUtil.newMove(moveFrom, moveFrom + 7);
                    } else {
                        moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(moveFrom, moveFrom + 7, PieceType.QUEEN);
                        moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(moveFrom, moveFrom + 7, PieceType.KNIGHT);
                        moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(moveFrom, moveFrom + 7, PieceType.BISHOP);
                        moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(moveFrom, moveFrom + 7, PieceType.ROOK);
                    }
                    takers = takers & (takers - 1);
                }
                /* Pawn pushes. */
                long pushers = ((interventionPoints & notPieces) >>> 8) & ourPawns;
                while (pushers != 0) {
                    int moveFrom = BitUtil.getLastBitPlaceValue(pushers);
                    if (moveFrom < 48) {
                        moveBuffer[writePosition++] = MoveInitUtil.newMove(moveFrom, moveFrom + 8);
                    } else {
                        moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(moveFrom, moveFrom + 8, PieceType.QUEEN);
                        moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(moveFrom, moveFrom + 8, PieceType.KNIGHT);
                        moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(moveFrom, moveFrom + 8, PieceType.BISHOP);
                        moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(moveFrom, moveFrom + 8, PieceType.ROOK);
                    }
                    pushers = pushers & (pushers - 1);
                }
                pushers = ((((interventionPoints & notPieces) >>> 8) & notPieces) >>> 8) & ourPawns & 0x000000000000FF00L;
                while (pushers != 0) {
                    int moveFrom = BitUtil.getLastBitPlaceValue(pushers);
                    moveBuffer[writePosition++] = MoveInitUtil.newPawnDoubleMove(moveFrom, moveFrom + 16);
                    pushers = pushers & (pushers - 1);
                }
            } else {
                /* Pawn takes moves. */
                long takers = ((interventionPoints & enemyPieces) << 7) & ourPawns & 0x7F7F7F7F7F7F7F7FL;
                while (takers != 0) {
                    int moveFrom = BitUtil.getLastBitPlaceValue(takers);
                    if (moveFrom > 15) {
                        moveBuffer[writePosition++] = MoveInitUtil.newMove(moveFrom, moveFrom - 7);
                    } else {
                        moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(moveFrom, moveFrom - 7, PieceType.QUEEN);
                        moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(moveFrom, moveFrom - 7, PieceType.KNIGHT);
                        moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(moveFrom, moveFrom - 7, PieceType.BISHOP);
                        moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(moveFrom, moveFrom - 7, PieceType.ROOK);
                    }
                    takers = takers & (takers - 1);
                }
                takers = ((interventionPoints & enemyPieces) << 9) & ourPawns & 0xFEFEFEFEFEFEFEFEL;
                while (takers != 0) {
                    int moveFrom = BitUtil.getLastBitPlaceValue(takers);
                    if (moveFrom > 15) {
                        moveBuffer[writePosition++] = MoveInitUtil.newMove(moveFrom, moveFrom - 9);
                    } else {
                        moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(moveFrom, moveFrom - 9, PieceType.QUEEN);
                        moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(moveFrom, moveFrom - 9, PieceType.KNIGHT);
                        moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(moveFrom, moveFrom - 9, PieceType.BISHOP);
                        moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(moveFrom, moveFrom - 9, PieceType.ROOK);
                    }
                    takers = takers & (takers - 1);
                }
                /* Pawn pushes. */
                long pushers = ((interventionPoints & notPieces) << 8) & ourPawns;
                while (pushers != 0) {
                    int moveFrom = BitUtil.getLastBitPlaceValue(pushers);
                    if (moveFrom > 15) {
                        moveBuffer[writePosition++] = MoveInitUtil.newMove(moveFrom, moveFrom - 8);
                    } else {
                        moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(moveFrom, moveFrom - 8, PieceType.QUEEN);
                        moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(moveFrom, moveFrom - 8, PieceType.KNIGHT);
                        moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(moveFrom, moveFrom - 8, PieceType.BISHOP);
                        moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(moveFrom, moveFrom - 8, PieceType.ROOK);
                    }
                    pushers = pushers & (pushers - 1);
                }
                pushers = ((((interventionPoints & notPieces) << 8) & notPieces) << 8) & ourPawns & 0x00FF000000000000L;
                while (pushers != 0) {
                    int moveFrom = BitUtil.getLastBitPlaceValue(pushers);
                    moveBuffer[writePosition++] = MoveInitUtil.newPawnDoubleMove(moveFrom, moveFrom - 16);
                    pushers = pushers & (pushers - 1);
                }
            }
            /* Pawn en-passant takes gets rid of check. */
            if ((position.getPawnToBeCapturedEnPassant(whiteToMove) & interventionPoints) != 0) {
                if (enPassantTakers != 0) {
                    int from = BitUtil.getLastBitPlaceValue(enPassantTakers);
                    int to = BitUtil.getBitPlaceValue(position.getPawnLocationAfterEnPassant(whiteToMove));
                    moveBuffer[writePosition++] = MoveInitUtil.newEnPassant(from, to);
                    enPassantTakers = enPassantTakers & (enPassantTakers - 1);
                    if ( enPassantTakers != 0 ) {
                        from = BitUtil.getLastBitPlaceValue(enPassantTakers);
                        moveBuffer[writePosition++] = MoveInitUtil.newEnPassant(from, to);
                    }
                }
            }
            while(interventionPoints != 0) {
                int moveTo = BitUtil.getLastBitPlaceValue(interventionPoints);

                /* Adding knight moves. */
                long attackers = KingAndKnightMovesUtil.getKnightMoves(moveTo) & ourKnights;
                attackers = attackers | RookAndBishopMovesUtil.getBishopMoves(moveTo,allPieces) & ourQueensAndBishops;
                attackers = attackers | RookAndBishopMovesUtil.getRookMoves(moveTo,allPieces) & ourRooksAndQueens;
                while(attackers != 0) {
                    int moveFrom = BitUtil.getLastBitPlaceValue(attackers);
                    moveBuffer[writePosition++] = MoveInitUtil.newMove(moveFrom, moveTo);
                    attackers = attackers & (attackers - 1);
                }
                interventionPoints = interventionPoints & (interventionPoints - 1);
            }
        }

        /* We see if the king can move to a position that's not attacked. */
        /* King moves. */
        long kingMoves = KingAndKnightMovesUtil.getKingMoves(kingPlaceValue);
        kingMoves = kingMoves & notOurPieces;
        while (kingMoves != 0) {
            long moveTo = kingMoves & -kingMoves;
            int toPlaceValue = BitUtil.getBitPlaceValue(moveTo);

            if ((KingAndKnightMovesUtil.getKingMoves(toPlaceValue) & enemyKing) == 0) {
                /* Enemy king doesn't attack the position. */
                if ((KingAndKnightMovesUtil.getKnightMoves(toPlaceValue) & enemyKnights) == 0) {
                    /* Enemy knights don't attack the position. */
                    long attacks = RookAndBishopMovesUtil.getBishopMoves(toPlaceValue, allPieces ^ ourKing);
                    if ((attacks & enemyQueensAndBishops) == 0) {
                        /* Enemy bishops type attackers don't attack the position. */
                        attacks = RookAndBishopMovesUtil.getRookMoves(toPlaceValue, allPieces ^ ourKing);
                        if ((attacks & enemyRooksAndQueens) == 0) {
                            /* Enemy rook type attackers don't attack the position. */
                            if (whiteToMove) {
                                if (((moveTo << 7) & enemyPawns & 0x7F7F7F7F7F7F7F7FL) == 0 &&
                                        ((moveTo << 9) & enemyPawns & 0xFEFEFEFEFEFEFEFEL) == 0) {
                                    /* Black pawns don't attack the position. */
                                    moveBuffer[writePosition++] = MoveInitUtil.newMove(kingPlaceValue, toPlaceValue);
                                }
                            } else {
                                if (((moveTo >>> 9) & enemyPawns & 0x7F7F7F7F7F7F7F7FL) == 0 &&
                                        ((moveTo >>> 7) & enemyPawns & 0xFEFEFEFEFEFEFEFEL) == 0) {
                                    /* White pawns don't attack the position. */
                                    moveBuffer[writePosition++] = MoveInitUtil.newMove(kingPlaceValue, toPlaceValue);
                                }
                            }
                        }
                    }
                }
            }
            kingMoves = kingMoves & (kingMoves - 1);
        }

        if (kingCheckCount > 0) {
            return writePosition;
        }

        /* We add castling moves. */
        if (position.canPotentiallyCastle(whiteToMove)) {
            if (whiteToMove) {
                if (position.canPotentiallyCastleLeft(true)) {
                    if ((allPieces & 0x0000000000000070L) == 0) {
                        /* No pieces in the path. */
                        if ((enemyKnights & 0x000000000078CC00L) == 0) {
                            /* No knights attack the path. */
                            if (((enemyKing | enemyPawns) & 0x0000000000007800L) == 0) {
                                /* No kings or pawns attack the path. */
                                long attackers = RookAndBishopMovesUtil.getBishopMoves(4, allPieces);
                                attackers = attackers | RookAndBishopMovesUtil.getBishopMoves(5, allPieces);

                                if ((enemyQueensAndBishops & attackers) == 0) {
                                    /* No diagonal attackers. */
                                    attackers = RookAndBishopMovesUtil.getRookMoves(4, allPieces);
                                    attackers = attackers | RookAndBishopMovesUtil.getRookMoves(5, allPieces);
                                    if ((enemyRooksAndQueens & attackers) == 0) {
                                        /* No lateral attackers. All checks done. */
                                        moveBuffer[writePosition++] = MoveInitUtil.newLeftCastle(3, 5);
                                    }
                                }
                            }
                        }
                    }
                }
                if (position.canPotentiallyCastleRight(true)) {
                    if ((allPieces & 0x0000000000000006L) == 0) {
                        /* No pieces in the path. */
                        if ((enemyKnights & 0x00000000000F1900L) == 0) {
                            /* No knights attack the path. */
                            if (((enemyKing | enemyPawns) & 0x0000000000000F00L) == 0) {
                                /* No kings or pawns attack the path. */
                                long attackers = RookAndBishopMovesUtil.getBishopMoves(2, allPieces);
                                attackers = attackers | RookAndBishopMovesUtil.getBishopMoves(1, allPieces);

                                if ((enemyQueensAndBishops & attackers) == 0) {
                                    /* No diagonal attackers. */
                                    attackers = RookAndBishopMovesUtil.getRookMoves(2, allPieces);
                                    attackers = attackers | RookAndBishopMovesUtil.getRookMoves(1, allPieces);
                                    if ((enemyRooksAndQueens & attackers) == 0) {
                                        /* No lateral attackers. All checks done. */
                                        moveBuffer[writePosition++] = MoveInitUtil.newRightCastle(3, 1);
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                if (position.canPotentiallyCastleLeft(false)) {
                    if ((allPieces & 0x7000000000000000L) == 0) {
                        /* No pieces in the path. */
                        if ((enemyKnights & 0x00CC780000000000L) == 0) {
                            /* No knights attack the path. */
                            if (((enemyKing | enemyPawns) & 0x0078000000000000L) == 0) {
                                /* No kings or pawns attack the path. */
                                long attackers = RookAndBishopMovesUtil.getBishopMoves(60, allPieces);
                                attackers = attackers | RookAndBishopMovesUtil.getBishopMoves(61, allPieces);

                                if ((enemyQueensAndBishops & attackers) == 0) {
                                    /* No diagonal attackers. */
                                    attackers = RookAndBishopMovesUtil.getRookMoves(60, allPieces);
                                    attackers = attackers | RookAndBishopMovesUtil.getRookMoves(61, allPieces);
                                    if ((enemyRooksAndQueens & attackers) == 0) {
                                        /* No lateral attackers. All checks done. */
                                        moveBuffer[writePosition++] = MoveInitUtil.newLeftCastle(59, 61);
                                    }
                                }
                            }
                        }
                    }
                }
                if (position.canPotentiallyCastleRight(false)) {
                    if ((allPieces & 0x0600000000000000L) == 0) {
                        /* No pieces in the path. */
                        if ((enemyKnights & 0x00190F0000000000L) == 0) {
                            /* No knights attack the path. */
                            if (((enemyKing | enemyPawns) & 0x000F000000000000L) == 0) {
                                /* No kings or pawns attack the path. */
                                long attackers = RookAndBishopMovesUtil.getBishopMoves(57, allPieces);
                                attackers = attackers | RookAndBishopMovesUtil.getBishopMoves(58, allPieces);

                                if ((enemyQueensAndBishops & attackers) == 0) {
                                    /* No diagonal attackers. */
                                    attackers = RookAndBishopMovesUtil.getRookMoves(57, allPieces);
                                    attackers = attackers | RookAndBishopMovesUtil.getRookMoves(58, allPieces);
                                    if ((enemyRooksAndQueens & attackers) == 0) {
                                        /* No lateral attackers. All checks done. */
                                        moveBuffer[writePosition++] = MoveInitUtil.newRightCastle(59, 57);
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
                    moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(from, from + 7, PieceType.QUEEN);
                    moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(from, from + 7, PieceType.KNIGHT);
                    moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(from, from + 7, PieceType.BISHOP);
                    moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(from, from + 7, PieceType.ROOK);
                }
                if ( ((pawn << 9) & enemyPieces) != 0 ) {
                    moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(from, from + 9, PieceType.QUEEN);
                    moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(from, from + 9, PieceType.KNIGHT);
                    moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(from, from + 9, PieceType.BISHOP);
                    moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(from, from + 9, PieceType.ROOK);
                }
                if ( ((pawn << 8) & notPieces) != 0 ) {
                    moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(from, from + 8, PieceType.QUEEN);
                    moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(from, from + 8, PieceType.KNIGHT);
                    moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(from, from + 8, PieceType.BISHOP);
                    moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(from, from + 8, PieceType.ROOK);
                }
                rank7Pawns = rank7Pawns & (rank7Pawns - 1);
            }

            long straightMoves = (ourPawns << 8) & notPieces;
            long doubleMoves   = ((straightMoves & 0x0000000000FF0000L) << 8) & notPieces;
            long rightCaptures = ((ourPawns << 7) & 0x7F7F7F7F7F7F7F7FL) & enemyPieces;
            long leftCaptures  = ((ourPawns << 9) & 0xFEFEFEFEFEFEFEFEL) & enemyPieces;
            while ( rightCaptures != 0 ) {
                int toPlaceValue = BitUtil.getLastBitPlaceValue(rightCaptures);
                moveBuffer[writePosition++] = MoveInitUtil.newMove(toPlaceValue - 7, toPlaceValue);
                rightCaptures = rightCaptures & ( rightCaptures - 1 );
            }
            while ( leftCaptures != 0 ) {
                int toPlaceValue = BitUtil.getLastBitPlaceValue(leftCaptures);
                moveBuffer[writePosition++] = MoveInitUtil.newMove(toPlaceValue - 9, toPlaceValue);
                leftCaptures = leftCaptures & ( leftCaptures - 1 );
            }
            while ( straightMoves != 0 ) {
                int toPlaceValue = BitUtil.getLastBitPlaceValue(straightMoves);
                moveBuffer[writePosition++] = MoveInitUtil.newMove(toPlaceValue - 8, toPlaceValue);
                straightMoves = straightMoves & ( straightMoves - 1 );
            }
            /* Taking care of white's double moves. */
            while ( doubleMoves != 0 ) {
                int toPlaceValue = BitUtil.getLastBitPlaceValue(doubleMoves);
                moveBuffer[writePosition++] = MoveInitUtil.newPawnDoubleMove(toPlaceValue - 16, toPlaceValue);
                doubleMoves = doubleMoves & ( doubleMoves - 1 );
            }
        } else {
            long rank7Pawns = ourPawns & 0x000000000000FF00L; /* These pawns are about to promote. */
            ourPawns = ourPawns ^ rank7Pawns;

            /* Take care of pawn promotions. */
            while ( rank7Pawns != 0 ) {
                long pawn = rank7Pawns & -rank7Pawns;
                int from = BitUtil.getBitPlaceValue(pawn);
                if ( ((pawn >>> 7) & 0xFEFEFEFEFEFEFEFEL & enemyPieces) != 0 ) {
                    moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(from, from - 7, PieceType.QUEEN);
                    moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(from, from - 7, PieceType.KNIGHT);
                    moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(from, from - 7, PieceType.BISHOP);
                    moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(from, from - 7, PieceType.ROOK);
                }
                if ( ((pawn >>> 9) & enemyPieces) != 0 ) {
                    moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(from, from - 9, PieceType.QUEEN);
                    moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(from, from - 9, PieceType.KNIGHT);
                    moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(from, from - 9, PieceType.BISHOP);
                    moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(from, from - 9, PieceType.ROOK);
                }
                if ( ((pawn >>> 8) & notPieces) != 0 ) {
                    moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(from, from - 8, PieceType.QUEEN);
                    moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(from, from - 8, PieceType.KNIGHT);
                    moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(from, from - 8, PieceType.BISHOP);
                    moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(from, from - 8, PieceType.ROOK);
                }
                rank7Pawns = rank7Pawns & (rank7Pawns - 1);
            }

            long straightMoves = (ourPawns >>> 8) & notPieces;
            long doubleMoves   = ((straightMoves & 0x0000FF0000000000L) >>> 8) & notPieces;
            long rightCaptures = ((ourPawns >>> 9) & 0x7F7F7F7F7F7F7F7FL) & enemyPieces;
            long leftCaptures  = ((ourPawns >> 7) & 0xFEFEFEFEFEFEFEFEL) & enemyPieces;
            while ( rightCaptures != 0 ) {
                int toPlaceValue = BitUtil.getLastBitPlaceValue(rightCaptures);
                moveBuffer[writePosition++] = MoveInitUtil.newMove(toPlaceValue + 9, toPlaceValue);
                rightCaptures = rightCaptures & ( rightCaptures - 1 );
            }
            while ( leftCaptures != 0 ) {
                int toPlaceValue = BitUtil.getLastBitPlaceValue(leftCaptures);
                moveBuffer[writePosition++] = MoveInitUtil.newMove(toPlaceValue + 7, toPlaceValue);
                leftCaptures = leftCaptures & ( leftCaptures - 1 );
            }
            while ( straightMoves != 0 ) {
                int toPlaceValue = BitUtil.getLastBitPlaceValue(straightMoves);
                moveBuffer[writePosition++] = MoveInitUtil.newMove(toPlaceValue + 8, toPlaceValue);
                straightMoves = straightMoves & ( straightMoves - 1 );
            }
            /* Taking care of white's double moves. */
            while ( doubleMoves != 0 ) {
                int toPlaceValue = BitUtil.getLastBitPlaceValue(doubleMoves);
                moveBuffer[writePosition++] = MoveInitUtil.newPawnDoubleMove(toPlaceValue + 16, toPlaceValue);
                doubleMoves = doubleMoves & ( doubleMoves - 1 );
            }
        }

        /* Taking care of en-passant moves. */
        if (enPassantTakers != 0) {
            int from = BitUtil.getLastBitPlaceValue(enPassantTakers);
            int to = BitUtil.getBitPlaceValue(position.getPawnLocationAfterEnPassant(whiteToMove));
            moveBuffer[writePosition++] = MoveInitUtil.newEnPassant(from, to);
            enPassantTakers = enPassantTakers & (enPassantTakers - 1);
            if ( enPassantTakers != 0 ) {
                from = BitUtil.getLastBitPlaceValue(enPassantTakers);
                moveBuffer[writePosition++] = MoveInitUtil.newEnPassant(from, to);
            }
        }

        /* Queen and bishop moves. */
        while (ourQueensAndBishops != 0) {
            int fromPlaceValue = BitUtil.getLastBitPlaceValue(ourQueensAndBishops);
            long moves = RookAndBishopMovesUtil.getBishopMoves(fromPlaceValue, allPieces);
            moves = moves & notOurPieces;
            while ( moves != 0 ) {
                int toPlaceValue = BitUtil.getLastBitPlaceValue(moves);
                moveBuffer[writePosition++] = MoveInitUtil.newMove(fromPlaceValue, toPlaceValue);
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
                moveBuffer[writePosition++] = MoveInitUtil.newMove(fromPlaceValue, toPlaceValue);
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
                moveBuffer[writePosition++] = MoveInitUtil.newMove(fromPlaceValue, toPlaceValue);
                moves = moves & ( moves - 1 );
            }
            ourKnights = ourKnights & (ourKnights - 1);
        }

        /* Returns the next position from which someone could start writing moves. */
        return writePosition;
    }

    public static List<Integer> getMovesInPosition(Position position) {
        List<Integer> result = new ArrayList<>();
        int [] moves = new int[300];
        int endPosition = addMovesToBuffer(position, moves, 0);
        int pos = 0, move;
        for(int i=0; i < endPosition; i++) {
            move = moves[pos++];
            if ( move == 0 ) {
                continue;
            }
            result.add(move);
        }
        return result;
    }
}
