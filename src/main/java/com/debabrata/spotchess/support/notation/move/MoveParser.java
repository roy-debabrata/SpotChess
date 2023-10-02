package com.debabrata.spotchess.support.notation.move;

import com.debabrata.spotchess.types.Position;

public interface MoveParser {
    long getMove(Position position, String notation);
    String getNotation(Position position, long move);
    boolean confirmFormat(String moveNotation);
}
