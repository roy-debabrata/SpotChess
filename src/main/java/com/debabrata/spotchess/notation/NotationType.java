package com.debabrata.spotchess.notation;

import com.debabrata.spotchess.types.Position;

public interface NotationType {
    int getMove(Position position, String notation);
    String getNotation(Position position, int move);

}
