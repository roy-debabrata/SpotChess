package com.debabrata.spotchess.notation;

import com.debabrata.spotchess.types.Position;
import com.debabrata.spotchess.types.enums.Colour;
import com.debabrata.spotchess.types.enums.PieceType;
import com.debabrata.spotchess.utils.KingAndKnightMovesUtil;
import com.debabrata.spotchess.utils.MoveIntUtil;
import com.debabrata.spotchess.utils.RookAndBishopMovesUtil;

public class StandardAlgebraicNotation implements NotationType {
    @Override
    public int getMove(Position position, String notation) {
        return 0;
    }

    @Override
    public String getNotation(Position position, int move) {
        if (MoveIntUtil.isCastle(move)) {
            return MoveIntUtil.isRightCastle(move) ? "O-O" : "O-O-O";
        }

        int to = MoveIntUtil.getTo(move);
        String notation = "" + (char)('h' - (to % 8));
        notation = notation + (to / 8 + 1);

        int from = MoveIntUtil.getFrom(move);
        PieceType pieceType = position.getPieceType(from);

        if ( position.getPieceType(to) != null || MoveIntUtil.isEnPassant(move) ) {
            notation = "x" + notation;
            if ( pieceType == PieceType.PAWN ) {
                notation = (char)('h' - (from % 8)) + notation;
            }
        }
        if ( pieceType == PieceType.PAWN ) {
            if (MoveIntUtil.isPromotion(move)){
                notation = notation + "=" + MoveIntUtil.promotesTo(move).getNotation();
            }
        } else {
            /* We calculate as if the attacker is at the attacked position. Then we find all legal moves it can make
             * from the attacked position and see if it hits another piece of the same kind. */
            long otherAttackers = 0;
            long allPieces = position.getAllPieces();
            switch (pieceType) {
                case KNIGHT:
                    otherAttackers = KingAndKnightMovesUtil.getKnightMoves(to);
                    otherAttackers = otherAttackers & position.getKnights();
                    break;
                case ROOK:
                    otherAttackers = RookAndBishopMovesUtil.getRookMoves(to, allPieces);
                    otherAttackers = otherAttackers & position.getRooks();
                    break;
                case QUEEN:
                    otherAttackers = RookAndBishopMovesUtil.getBishopMoves(to, allPieces);
                    otherAttackers |= RookAndBishopMovesUtil.getRookMoves(to, allPieces);
                    otherAttackers = otherAttackers & position.getQueens();
                    break;
                case BISHOP:
                    otherAttackers = RookAndBishopMovesUtil.getBishopMoves(to, allPieces);
                    otherAttackers = otherAttackers & position.getBishops();
                    break;
            }
            /* Removing our own attacker from consideration. */
            otherAttackers = otherAttackers ^ (1L << from);
            /* We only care about attacking pieces of the same colour. */
            Colour moversColour = position.getPieceColour(from);
            if (moversColour == Colour.BLACK) {
                otherAttackers = otherAttackers & position.getBlackPieces();
            } else {
                otherAttackers = otherAttackers & position.getWhitePieces();
            }
            if (otherAttackers != 0) {
                /* There is ambiguity with attackers. */
                if ((otherAttackers & (0x0101010101010101L << (from % 8))) == 0) {
                    /* Attackers are not in the same column. */
                    notation = (char) ('h' - (from % 8)) + notation;
                } else if ((otherAttackers & (0x00000000000000FFL << (from / 8))) == 0) {
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
