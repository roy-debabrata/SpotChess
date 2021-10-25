package com.debabrata.spotchess.types.enums;

public enum PieceType {
    KING('K'),
    QUEEN('Q'),
    BISHOP('B'),
    KNIGHT('N'),
    ROOK('R'),
    PAWN('P');

    private final char notation;

    PieceType(char notation) {
        this.notation = notation;
    }

    public char getNotation(){
        return this.notation;
    }

    public static PieceType getPiece(char notation) {
        notation = Character.toUpperCase(notation);
        for (PieceType piece : values()) {
            if (piece.getNotation() == notation) {
                return piece;
            }
        }
        return null;
    }
}
