package com.debabrata.spotchess.notation;

import com.debabrata.spotchess.types.Position;
import com.debabrata.spotchess.types.enums.Colour;
import com.debabrata.spotchess.types.enums.PieceType;
import com.debabrata.spotchess.utils.BitUtil;
import com.debabrata.spotchess.utils.KingAndKnightMovesUtil;
import com.debabrata.spotchess.utils.MoveInitUtil;
import com.debabrata.spotchess.utils.RookAndBishopMovesUtil;

public class StandardAlgebraicNotation implements NotationType {
    @Override
    public int getMove(Position position, String notation) {
        /* We don't do all checks for legality, only enough to avoid misinterpretation. */
        if (null == notation || null == position) {
            return 0;
        }
        notation = notation.replaceAll("\\s", "");
        if (notation.length() < 2) {
            return 0;
        }
        boolean whiteToMove = position.whiteToMove();
        if (notation.equalsIgnoreCase("O-O-O") || notation.equalsIgnoreCase("0-0-0")) {
            if (whiteToMove) {
                return MoveInitUtil.newLeftCastle(3, 5);
            }
            return MoveInitUtil.newLeftCastle(59, 61);
        } else if (notation.equalsIgnoreCase("O-O") || notation.equalsIgnoreCase("0-0")) {
            if (whiteToMove) {
                return MoveInitUtil.newRightCastle(3, 1);
            }
            return MoveInitUtil.newRightCastle(59, 57);
        }
        int endOfToCoOrdinate;
        int to;
        for (endOfToCoOrdinate = notation.length() - 1; endOfToCoOrdinate > 0; endOfToCoOrdinate--) {
            char rank = notation.charAt(endOfToCoOrdinate);
            if (rank >= '0' && rank <= '8') {
                break; /* We keep looking till we find the rank. */
            }
        }
        if (endOfToCoOrdinate == 0) {
            return 0; /* Not a valid message. All valid message have at least one number after first char. */
        }
        /* Reached the to position. */
        to = notation.charAt(endOfToCoOrdinate) - '1';
        char file = notation.charAt(endOfToCoOrdinate - 1);
        if (file >= 'a' && file <= 'h') {
            to = 8 * to + ('h' - file);
        } else {
            return 0; /* Invalid to file. */
        }
        /* Checking if there is a piece to be taken at that position. */
        boolean pieceTaken = false;
        long allPieces = position.getAllPieces();
        long sideToMove = whiteToMove ? position.getWhitePieces() : position.getBlackPieces();
        if ((sideToMove & (1L << to)) != 0) {
            return 0; /* Moves over own piece. */
        }
        if ((allPieces & (1L << to)) != 0) {
            pieceTaken = true;
        }
        /* Finding the 'from' position. */
        if (endOfToCoOrdinate > 1) {
            char startsWith = notation.charAt(0);
            if (Character.isUpperCase(startsWith)) {
                /*Dealing with pieces. */
                long attackers;
                switch (startsWith) {
                    case 'N':
                        attackers = KingAndKnightMovesUtil.getKnightMoves(to);
                        attackers = attackers & position.getKnights() & sideToMove;
                        break;
                    case 'B':
                        attackers = RookAndBishopMovesUtil.getBishopMoves(to, allPieces);
                        attackers = attackers & position.getBishops() & sideToMove;
                        break;
                    case 'R':
                        attackers = RookAndBishopMovesUtil.getRookMoves(to, allPieces);
                        attackers = attackers & position.getRooks() & sideToMove;
                        break;
                    case 'Q':
                        attackers = RookAndBishopMovesUtil.getBishopMoves(to, allPieces);
                        attackers |= RookAndBishopMovesUtil.getRookMoves(to, allPieces);
                        attackers = attackers & position.getQueens() & sideToMove;
                        break;
                    case 'K':
                        attackers = KingAndKnightMovesUtil.getKingMoves(to);
                        attackers = attackers & position.getKings() & sideToMove;
                        break;
                    default:
                        return 0; /* Not a known piece. */
                }
                boolean xEncountered = false;
                for (int i = 1; i < endOfToCoOrdinate - 1; i++) {
                    char disambiguator = notation.charAt(i);
                    if (disambiguator >= 'a' && disambiguator <= 'h') {
                        attackers = attackers & (0x0101010101010101L << ('h' - disambiguator));
                    } else if (disambiguator >= '1' && disambiguator <= '8') {
                        attackers = attackers & (0x00000000000000FFL << (8 * (disambiguator - '1')));
                    } else if (disambiguator == 'x' || disambiguator == 'X') {
                        xEncountered = true;
                    } else {
                        return 0; /* If it's not [a-h], [0-8] or 'x' we don't know what it is. */
                    }
                }
                if (pieceTaken != xEncountered) {
                    return 0; /* Piece taken but not notated or piece not taken but takes notated. */
                }
                if (Long.bitCount(attackers) != 1) {
                    return 0; /* Despite disambiguation there are multiple attackers or no attackers for the position. */
                }
                int from = BitUtil.getBitPlaceValue(attackers);
                return MoveInitUtil.newMove(from, to);
            }
        }
        /* All piece moves have been dealt with. We take care of pawns now. */
        int from = 0;
        if (endOfToCoOrdinate == 1) {
            /* It's a simple pawn move, like e4 or c5. e4 may be a double push though, so we check for those. */
            if (pieceTaken) {
                return 0; /* Takes a piece on a straight move. */
            }
            if (whiteToMove) {
                long whitePawns = position.getPawns() & position.getWhitePieces();
                if ((1L << (to - 8) & whitePawns) != 0) {
                    from = to - 8;
                } else if ((1L << (to - 16) & whitePawns) != 0 && (to - 16) / 8 == 1) {
                    return MoveInitUtil.newPawnDoubleMove(to - 16, to);
                } else {
                    return 0; /* No pawn exists on either of the preceding squares. */
                }
            } else {
                long blackPawns = position.getPawns() & position.getBlackPieces();
                if ((1L << (to + 8) & blackPawns) != 0) {
                    from = to + 8;
                } else if ((1L << (to + 16) & blackPawns) != 0 && (to + 16) / 8 == 6) {
                    return MoveInitUtil.newPawnDoubleMove(to + 16, to); /* Double pawn move taken care of. */
                } else {
                    return 0; /* No pawn exists on either of the preceding squares. */
                }
            }
        }
        /* Taking care of pawn takes. from == 0 works because there are no legal pawn moves from h1. */
        if (from == 0) {
            char takerFile = notation.charAt(0);
            if (takerFile >= 'a' && takerFile <= 'h') {
                if (Character.toLowerCase(notation.charAt(1)) != 'x' && Character.toLowerCase(notation.charAt(2)) != 'x') {
                    return 0; /* We expect a taker. We see no takes in notation. */
                }
                if (whiteToMove) {
                    from = ((to / 8) - 1) * 8 + 'h' - takerFile;
                } else {
                    from = ((to / 8) + 1) * 8 + 'h' - takerFile;
                }
            } else {
                return 0; /* Can't find taker's file. */
            }
            if (!pieceTaken && ((position.getPawnLocationAfterEnPassant(whiteToMove) & 1L << to) != 0)) {
                return MoveInitUtil.newEnPassant(from, to); /* En-passant move taken care of. */
            }
            if (!pieceTaken) {
                return 0; /* It's neither piece taken nor en-passant, yet we have a 'takes' in notation. */
            }
        }
        /* Checking for promotion. */
        boolean reachedLastRank = (whiteToMove && to / 8 == 7) || (!whiteToMove && to / 8 == 0);
        if (endOfToCoOrdinate + 1 < notation.length()) {
            /* There's more to read. It can be +, # etc. Or it could be promotion. We check for promotion. */
            if (notation.charAt(endOfToCoOrdinate + 1) == '=') {
                if (endOfToCoOrdinate + 2 < notation.length()) {
                    char promotesTo = Character.toUpperCase(notation.charAt(endOfToCoOrdinate + 2));
                    if (reachedLastRank) {
                        if ( promotesTo == 'Q' ) {
                            return MoveInitUtil.newPawnPromotion(from, to, PieceType.QUEEN);
                        } else if ( promotesTo == 'N' ) {
                            return MoveInitUtil.newPawnPromotion(from, to, PieceType.KNIGHT);
                        } else if ( promotesTo == 'R' ) {
                            return MoveInitUtil.newPawnPromotion(from, to, PieceType.ROOK);
                        } else if ( promotesTo == 'B' ) {
                            return MoveInitUtil.newPawnPromotion(from, to, PieceType.BISHOP);
                        } else {
                            return 0; /* Unknown promotion type. */
                        }
                    } else {
                        return 0; /* Promotes before last rank. Or the colour is wrong. */
                    }
                } else {
                    return 0; /* No piece type mentioned after indicating promotion. */
                }
            }
        }
        if (reachedLastRank) {
            return 0; /* Doesn't promote at last rank. */
        }
        /* Not takes/takes en-passant/double move/promotion. It's a simple move. */
        return MoveInitUtil.newMove(from, to);
    }

    @Override
    public String getNotation(Position position, int move) {
        if (MoveInitUtil.isCastle(move)) {
            return MoveInitUtil.isRightCastle(move) ? "O-O" : "O-O-O";
        }

        int to = MoveInitUtil.getTo(move);
        String notation = "" + (char) ('h' - (to % 8));
        notation = notation + (to / 8 + 1);

        int from = MoveInitUtil.getFrom(move);
        PieceType pieceType = position.getPieceType(from);

        if (position.getPieceType(to) != null || MoveInitUtil.isEnPassant(move)) {
            notation = "x" + notation;
            if (pieceType == PieceType.PAWN) {
                notation = (char) ('h' - (from % 8)) + notation;
            }
        }
        if (pieceType == PieceType.PAWN) {
            if (MoveInitUtil.isPromotion(move)) {
                PieceType promotesTo = MoveInitUtil.promotesTo(move);
                if (null == promotesTo) {
                    throw new RuntimeException("Invalid promotion!");
                }
                notation = notation + "=" + promotesTo.getNotation();
            }
        } else {
            /* We calculate as if the attacker is at the attacked position. Then we find all legal moves it can make
             * from the attacked position and see if it hits another piece of the same kind. */
            long attackers = 0;
            long allPieces = position.getAllPieces();
            switch (pieceType) {
                case KNIGHT:
                    attackers = KingAndKnightMovesUtil.getKnightMoves(to);
                    attackers = attackers & position.getKnights();
                    break;
                case ROOK:
                    attackers = RookAndBishopMovesUtil.getRookMoves(to, allPieces);
                    attackers = attackers & position.getRooks();
                    break;
                case QUEEN:
                    attackers = RookAndBishopMovesUtil.getBishopMoves(to, allPieces);
                    attackers |= RookAndBishopMovesUtil.getRookMoves(to, allPieces);
                    attackers = attackers & position.getQueens();
                    break;
                case BISHOP:
                    attackers = RookAndBishopMovesUtil.getBishopMoves(to, allPieces);
                    attackers = attackers & position.getBishops();
                    break;
            }
            /* Removing our own attacker from consideration. */
            attackers = attackers & ~(1L << from);
            /* We only care about attacking pieces of the same colour. */
            Colour moversColour = position.getPieceColour(from);
            if (moversColour == Colour.BLACK) {
                attackers = attackers & position.getBlackPieces();
            } else {
                attackers = attackers & position.getWhitePieces();
            }
            if (attackers != 0) {
                /* There is ambiguity with attackers. */
                if ((attackers & (0x0101010101010101L << (from % 8))) == 0) {
                    /* Attackers are not in the same column. */
                    notation = (char) ('h' - (from % 8)) + notation;
                } else if ((attackers & (0x00000000000000FFL << (8 * (from / 8)))) == 0) {
                    /* Attackers are in the same column but not same row. */
                    notation = (from / 8 + 1) + notation;
                } else {
                    /* We share both row and column with some attacker. */
                    notation = (from / 8 + 1) + notation;
                    notation = (char) ('h' - (from % 8)) + notation;
                }
            }
        }

        notation = pieceType.getNotation() + notation;
        return notation.trim();
    }

    @Override
    public boolean confirmFormat(String moveNotation) {
        return false;
    }
}
