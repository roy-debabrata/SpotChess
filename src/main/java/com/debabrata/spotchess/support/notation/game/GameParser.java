package com.debabrata.spotchess.support.notation.game;

import com.debabrata.spotchess.types.Game;

public interface GameParser {
    Game getGame(String gameContent);
    String getNotation(Game game);
    boolean confirmFormat(String gameContent);
}
