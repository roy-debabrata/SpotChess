package com.debabrata.spotchess.support.notation.game;

import com.debabrata.spotchess.types.Game;

/** Parses and generates Portable Game Notation game file format. */
public class PGNParser implements GameParser {
    @Override
    public Game getGame(String gameContent) {
        return null;
    }

    @Override
    public String getNotation(Game game) {
        return null;
    }

    @Override
    public boolean confirmFormat(String gameContent) {
        return false;
    }
}
