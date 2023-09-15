package com.debabrata.spotchess;

import com.debabrata.spotchess.console.PositionPrinter;
import com.debabrata.spotchess.logic.MoveProcessor;
import com.debabrata.spotchess.support.notation.game.FENParser;
import com.debabrata.spotchess.types.Position;

public class POC {
    public static void main(String[] args) {
        Position position = new FENParser().getGame("rnb1qkr1/pp1Pbpp1/2p4p/8/2B3K1/8/PPP1N1PP/RNBQ3R w - - 2 11").getCurrentPosition();
        PositionPrinter.printPosition(position);
        new MoveProcessor(new int[100]).addMovesToBuffer(position, 0);
    }
}
