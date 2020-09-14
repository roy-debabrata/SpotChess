package com.debabrata.spotchess.types;

public enum PieceType {
    KING('K'),
    QUEEN('Q'),
    BISHOP('B'),
    KNIGHT('N'),
    ROOK('R'),
    PAWN('\0'); /* We can trim this out of the notation. */

    private char notation;

    PieceType(char notation) {
        this.notation = notation;
    }

    public char getNotation(){
        return this.notation;
    }
}
