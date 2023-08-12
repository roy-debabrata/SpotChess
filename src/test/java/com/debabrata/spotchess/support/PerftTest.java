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

    @Test
    public void position7() {
        perft("1k6/1b6/8/8/7R/8/8/4K2R b K - 0 1", 5, 1063513);
    }

    @Test
    public void moreEnPassantIssues() {
        // TalkChess PERFT Tests (by Martin Sedlak)
        perft("3k4/3p4/8/K1P4r/8/8/8/8 b - - 0 1", 6, 1134888);
        perft("8/8/4k3/8/2p5/8/B2P2K1/8 w - - 0 1", 6, 1015133);
        perft("8/8/1k6/2b5/2pP4/8/5K2/8 b - d3 0 1", 6, 1440467);
    }

    @Test
    public void moreChecksAndStalemates() {
        // TalkChess PERFT Tests (by Martin Sedlak)
        perft("5k2/8/8/8/8/8/8/4K2R w K - 0 1", 6, 661072);
        perft("3k4/8/8/8/8/8/8/R3K3 w Q - 0 1", 6, 803711);
        perft("r3k2r/1b4bq/8/8/8/8/7B/R3K2R w KQkq - 0 1", 4, 1274206);
        perft("r3k2r/8/3Q4/8/8/5q2/8/R3K2R b KQkq - 0 1", 4, 1720476);
        perft("2K2r2/4P3/8/8/8/8/8/3k4 w - - 0 1", 6, 3821001);
        perft("8/8/1P2K3/8/2n5/1q6/8/5k2 b - - 0 1", 5, 1004658);
        perft("4k3/1P6/8/8/8/8/K7/8 w - - 0 1", 6, 217342);
        perft("8/P1k5/K7/8/8/8/8/8 w - - 0 1", 6, 92683);
        perft("K1k5/8/P7/8/8/8/8/8 w - - 0 1", 6, 2217);
        perft("8/k1P5/8/1K6/8/8/8/8 w - - 0 1", 7, 567584);
        perft("8/8/2k5/5q2/5n2/8/5K2/8 b - - 0 1", 4, 23527);
    }

    private void perft(String fen, int depth, long expectedResult) {
        Game game = fenParser.getGame(fen);

        long result = Perft.perftRunner(game.getCurrentPosition(), depth, true);
        assertEquals(expectedResult, result);
    }
}