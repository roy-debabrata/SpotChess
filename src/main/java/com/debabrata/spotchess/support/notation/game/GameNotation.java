package com.debabrata.spotchess.support.notation.game;

import com.debabrata.spotchess.types.Game;

public interface GameNotation {
    Game getGame(String gameContent);
    String getNotation(String game);
    boolean confirmFormat(String gameContent);
}
