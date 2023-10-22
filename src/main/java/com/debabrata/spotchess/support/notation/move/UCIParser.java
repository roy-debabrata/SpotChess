package com.debabrata.spotchess.support.notation.move;

import com.debabrata.spotchess.types.Position;

public class UCIParser implements MoveParser {
    @Override
    public long getMove(Position position, String notation) {
        return 0;
    }

    @Override
    public String getNotation(Position position, long move) {
        return null;
    }

    @Override
    public boolean confirmFormat(String moveNotation) {
        return false;
    }
}
