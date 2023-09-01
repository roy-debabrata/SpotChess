package com.debabrata.spotchess.console;

import com.debabrata.spotchess.support.notation.game.FENParser;
import com.debabrata.spotchess.support.perft.Perft;
import com.debabrata.spotchess.types.Game;

import java.util.stream.IntStream;

public class ConsoleMode {
    public void runREPL(String [] args) {
        perft(args[0], Integer.parseInt(args[1]), Long.parseLong(args[2]));
    }

    public void clearConsole() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                Runtime.getRuntime().exec("cls");
            }
            else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (final Exception e) {
            IntStream.range(0,100).forEach(System.out::println);
        }
    }

    private void perft(String fen, int depth, long expectedResult) {
        FENParser fenParser = new FENParser();
        Game game = fenParser.getGame(fen);

        long result = Perft.perftRunner(game.getCurrentPosition(), depth, true);
        if (expectedResult != result) {
            throw new AssertionError("Not equal!");
        }
    }
}
