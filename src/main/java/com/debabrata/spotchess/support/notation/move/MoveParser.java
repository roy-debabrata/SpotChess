package com.debabrata.spotchess.support.notation.move;

import com.debabrata.spotchess.types.Position;

public interface MoveParser {
    int getMove(Position position, String notation);
    String getNotation(Position position, int move);
    boolean confirmFormat(String moveNotation);
}