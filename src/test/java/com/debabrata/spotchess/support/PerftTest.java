package com.debabrata.spotchess.support;

import com.debabrata.spotchess.support.notation.game.FENParser;
import com.debabrata.spotchess.support.perft.Perft;
import com.debabrata.spotchess.types.Game;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Performance test, move path enumeration.
 * @see <a href="https://www.chessprogramming.org/Perft">PerftTest - Chess Programming Wiki</a>
 */
@Disabled
public class PerftTest {
    FENParser fenParser = new FENParser();

    @Test
    public void position1() {
        perft("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", 7, 3195901860L);
    }

    @Test
    public void position2() {
        perft("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -", 6, 8031647685L);
    }

    @Test
    public void position3() {
        perft("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -",8, 3009794393L);
    }

    @Test
    public void position4() {
        perft("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1", 6, 706045033L);
        perft("r2q1rk1/pP1p2pp/Q4n2/bbp1p3/Np6/1B3NBn/pPPP1PPP/R3K2R b KQ - 0 1", 6, 706045033L);
    }

    @Test
    public void position5() {
        perft("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8", 5,89941194);
    }

    @Test
    public void position6() {
        perft("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10", 6,6923051137L);
    }

    private void perft(String fen, int depth, long expectedResult) {
        Game game = fenParser.getGame(fen);

        long result = Perft.perftRunner(game.getCurrentPosition(), depth, true);
        assertEquals(expectedResult, result);
    }
}