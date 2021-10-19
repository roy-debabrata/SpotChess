package com.debabrata.spotchess.utils;

import com.debabrata.spotchess.types.Position;
import com.debabrata.spotchess.types.enums.Colour;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static com.debabrata.spotchess.support.SpotTestSupport.*;

public class MoveUtilTest {
    @Nested
    class MoveUtilCastlingTest {
        @Test
        public void testCastling() {
            /* Should have castling rights. */
            Position position = position(white(k(e1),r(a1,h1)), black(k(e8)), Colour.WHITE);
            List<Integer> moves = MoveUtil.getMovesInPosition(position);
            assertHasMoves(position, "O-O O-O-O", moves);

            position = position(white(k(e1)), black(k(e8),r(a8,h8)), Colour.BLACK);
            moves = MoveUtil.getMovesInPosition(position);
            assertHasMoves(position, "O-O O-O-O", moves);
        }

        @Test
        public void testKnightsPreventingCastling() {
            /* White should not have castling rights. */
            Position position = position(white(k(e1), r(a1, h1)), black(k(e8), n(e3)), Colour.WHITE);
            List<Integer> moves = MoveUtil.getMovesInPosition(position);
            assertDoesNotHaveMoves(position, "O-O O-O-O", moves);

            position = position(white(k(e1), r(a1, h1)), black(k(e8), n(e2)), Colour.WHITE);
            moves = MoveUtil.getMovesInPosition(position);
            assertDoesNotHaveMoves(position, "O-O O-O-O", moves);

            /* Checking left castle. */
            position = position(white(k(e1), r(a1, h1)), black(k(e8), n(f2)), Colour.WHITE);
            moves = MoveUtil.getMovesInPosition(position);
            assertHasMoves(position, "O-O", moves);
            assertDoesNotHaveMoves(position, "O-O-O", moves);

            position = position(white(k(e1), r(a1, h1)), black(k(e8), n(d3)), Colour.WHITE);
            moves = MoveUtil.getMovesInPosition(position); /* This should break when we start checking for checks. */
            assertHasMoves(position, "O-O", moves);
            assertDoesNotHaveMoves(position, "O-O-O", moves);

            position = position(white(k(e1), r(a1, h1)), black(k(e8), n(c3)), Colour.WHITE);
            moves = MoveUtil.getMovesInPosition(position);
            assertHasMoves(position, "O-O", moves);
            assertDoesNotHaveMoves(position, "O-O-O", moves);

            position = position(white(k(e1), r(a1, h1)), black(k(e8), n(b3)), Colour.WHITE);
            moves = MoveUtil.getMovesInPosition(position);
            assertHasMoves(position, "O-O", moves);
            assertDoesNotHaveMoves(position, "O-O-O", moves);

            position = position(white(k(e1), r(a1, h1)), black(k(e8), n(b2)), Colour.WHITE);
            moves = MoveUtil.getMovesInPosition(position);
            assertHasMoves(position, "O-O", moves);
            assertDoesNotHaveMoves(position, "O-O-O", moves);

            position = position(white(k(e1), r(a1, h1)), black(k(e8), n(a2)), Colour.WHITE);
            moves = MoveUtil.getMovesInPosition(position);
            assertHasMoves(position, "O-O", moves);
            assertDoesNotHaveMoves(position, "O-O-O", moves);

            position = position(white(k(e1), r(a1, h1)), black(k(e8), n(a2, b2, b3, c3, d3, f2)), Colour.WHITE);
            moves = MoveUtil.getMovesInPosition(position);
            assertHasMoves(position, "O-O", moves); /* This should break when we start checking for checks. */
            assertDoesNotHaveMoves(position, "O-O-O", moves);

            /* Checking right castle. */
            position = position(white(k(e1), r(a1, h1)), black(k(e8), n(d2)), Colour.WHITE);
            moves = MoveUtil.getMovesInPosition(position);
            assertHasMoves(position, "O-O-O", moves);
            assertDoesNotHaveMoves(position, "O-O", moves);

            position = position(white(k(e1), r(a1, h1)), black(k(e8), n(f3)), Colour.WHITE);
            moves = MoveUtil.getMovesInPosition(position); /* This should break when we start checking for checks. */
            assertHasMoves(position, "O-O-O", moves);
            assertDoesNotHaveMoves(position, "O-O", moves);

            position = position(white(k(e1), r(a1, h1)), black(k(e8), n(g3)), Colour.WHITE);
            moves = MoveUtil.getMovesInPosition(position);
            assertHasMoves(position, "O-O-O", moves);
            assertDoesNotHaveMoves(position, "O-O", moves);

            position = position(white(k(e1), r(a1, h1)), black(k(e8), n(h3)), Colour.WHITE);
            moves = MoveUtil.getMovesInPosition(position);
            assertHasMoves(position, "O-O-O", moves);
            assertDoesNotHaveMoves(position, "O-O", moves);

            position = position(white(k(e1), r(a1, h1)), black(k(e8), n(h2)), Colour.WHITE);
            moves = MoveUtil.getMovesInPosition(position);
            assertHasMoves(position, "O-O-O", moves);
            assertDoesNotHaveMoves(position, "O-O", moves);

            position = position(white(k(e1), r(a1, h1)), black(k(e8), n(d2, f3, g3, h3, h2)), Colour.WHITE);
            moves = MoveUtil.getMovesInPosition(position);
            assertHasMoves(position, "O-O-O", moves); /* This should break when we start checking for checks. */
            assertDoesNotHaveMoves(position, "O-O", moves);

            /* Black should not have castling rights. */
            position = position(white(k(e1), n(e6)), black(k(e8), r(a8, h8)), Colour.BLACK);
            moves = MoveUtil.getMovesInPosition(position);
            assertDoesNotHaveMoves(position, "O-O O-O-O", moves);

            position = position(white(k(e1), n(e7)), black(k(e8), r(a8, h8)), Colour.BLACK);
            moves = MoveUtil.getMovesInPosition(position);
            assertDoesNotHaveMoves(position, "O-O O-O-O", moves);

            /* Checking left castle. */
            position = position(white(k(e1), n(f7)), black(k(e8), r(a8, h8)), Colour.BLACK);
            moves = MoveUtil.getMovesInPosition(position); /* This should break when we start checking for checks. */
            assertHasMoves(position, "O-O", moves);
            assertDoesNotHaveMoves(position, "O-O-O", moves);

            position = position(white(k(e1), n(d6)), black(k(e8), r(a8, h8)), Colour.BLACK);
            moves = MoveUtil.getMovesInPosition(position); /* This should break when we start checking for checks. */
            assertHasMoves(position, "O-O", moves);
            assertDoesNotHaveMoves(position, "O-O-O", moves);

            position = position(white(k(e1), n(c6)), black(k(e8), r(a8, h8)), Colour.BLACK);
            moves = MoveUtil.getMovesInPosition(position);
            assertHasMoves(position, "O-O", moves);
            assertDoesNotHaveMoves(position, "O-O-O", moves);

            position = position(white(k(e1), n(b6)), black(k(e8), r(a8, h8)), Colour.BLACK);
            moves = MoveUtil.getMovesInPosition(position);
            assertHasMoves(position, "O-O", moves);
            assertDoesNotHaveMoves(position, "O-O-O", moves);

            position = position(white(k(e1), n(b7)), black(k(e8), r(a8, h8)), Colour.BLACK);
            moves = MoveUtil.getMovesInPosition(position);
            assertHasMoves(position, "O-O", moves);
            assertDoesNotHaveMoves(position, "O-O-O", moves);

            position = position(white(k(e1), n(a7)), black(k(e8), r(a8, h8)), Colour.BLACK);
            moves = MoveUtil.getMovesInPosition(position);
            assertHasMoves(position, "O-O", moves);
            assertDoesNotHaveMoves(position, "O-O-O", moves);

            position = position(white(k(e1), n(a7, b7, b6, c6, d6, f7)), black(k(e8), r(a8, h8)), Colour.BLACK);
            moves = MoveUtil.getMovesInPosition(position);
            assertHasMoves(position, "O-O", moves); /* This should break when we start checking for checks. */
            assertDoesNotHaveMoves(position, "O-O-O", moves);

            /* Checking right castle. */
            position = position(white(k(e1), n(d7)), black(k(e8), r(a8, h8)), Colour.BLACK);
            moves = MoveUtil.getMovesInPosition(position);
            assertHasMoves(position, "O-O-O", moves);
            assertDoesNotHaveMoves(position, "O-O", moves);

            position = position(white(k(e1), n(f6)), black(k(e8), r(a8, h8)), Colour.BLACK);
            moves = MoveUtil.getMovesInPosition(position); /* This should break when we start checking for checks. */
            assertHasMoves(position, "O-O-O", moves);
            assertDoesNotHaveMoves(position, "O-O", moves);

            position = position(white(k(e1), n(g6)), black(k(e8), r(a8, h8)), Colour.BLACK);
            moves = MoveUtil.getMovesInPosition(position);
            assertHasMoves(position, "O-O-O", moves);
            assertDoesNotHaveMoves(position, "O-O", moves);

            position = position(white(k(e1), n(h6)), black(k(e8), r(a8, h8)), Colour.BLACK);
            moves = MoveUtil.getMovesInPosition(position);
            assertHasMoves(position, "O-O-O", moves);
            assertDoesNotHaveMoves(position, "O-O", moves);

            position = position(white(k(e1), n(h7)), black(k(e8), r(a8, h8)), Colour.BLACK);
            moves = MoveUtil.getMovesInPosition(position);
            assertHasMoves(position, "O-O-O", moves);
            assertDoesNotHaveMoves(position, "O-O", moves);

            position = position(white(k(e1), n(d7, f6, g6, h6, h7)), black(k(e8), r(a8, h8)), Colour.BLACK);
            moves = MoveUtil.getMovesInPosition(position);
            assertHasMoves(position, "O-O-O", moves); /* This should break when we start checking for checks. */
            assertDoesNotHaveMoves(position, "O-O", moves);

            /* Negative checks knight not-preventing castling. */
            position = position(white(k(e1), r(a1)),
                    black(k(e8), n(except(a1, b1, c1, d1, e1, e8, a2, b2, b3, c3, d3, e2, e3, f2))
                    ), Colour.WHITE);
            moves = MoveUtil.getMovesInPosition(position);
            assertHasMoves(position, "O-O-O", moves); /* This should break when we start checking for checks. */

            position = position(white(k(e1), r(h1)),
                    black(k(e8), n(except(e1, f1, g1, h1, e8, d2, e2, e3, f3, g3, h3, h2))
                    ), Colour.WHITE);
            moves = MoveUtil.getMovesInPosition(position);
            assertHasMoves(position, "O-O", moves); /* This should break when we start checking for checks. */

            position = position(white(k(e1), n(except(a8, b8, c8, d8, e8, e1, a7, b7, b6, c6, d6, e6, e7, f7))), black(k(e8), r(a8)), Colour.BLACK);
            moves = MoveUtil.getMovesInPosition(position);
            assertHasMoves(position, "O-O-O", moves); /* This should break when we start checking for checks. */

            position = position(white(k(e1), n(except(e8, f8, g8, h8, e1, d7, e7, e6, f6, g6, h6, h7))), black(k(e8), r(h8)), Colour.BLACK);
            moves = MoveUtil.getMovesInPosition(position);
            assertHasMoves(position, "O-O", moves); /* This should break when we start checking for checks. */
        }

        @Test
        public void testKingPreventingCastling() {
            /* White should not have castling rights. */
            Position position = position(white(k(e1), r(a1, h1)),
                    black(k(b2)),
                    Colour.WHITE);
            List<Integer> moves = MoveUtil.getMovesInPosition(position);
            assertDoesNotHaveMoves(position, "O-O-O", moves);
            assertHasMoves(position, "O-O", moves);

            position = position(white(k(e1), r(a1, h1)),
                    black(k(c2)),
                    Colour.WHITE);
            moves = MoveUtil.getMovesInPosition(position);
            assertDoesNotHaveMoves(position, "O-O-O", moves);
            assertHasMoves(position, "O-O", moves);

            position = position(white(k(e1), r(a1, h1)),
                    black(k(g2)),
                    Colour.WHITE);
            moves = MoveUtil.getMovesInPosition(position);
            assertDoesNotHaveMoves(position, "O-O", moves);
            assertHasMoves(position, "O-O-O", moves);

            position = position(white(k(e1), r(a1, h1)),
                    black(k(h2)),
                    Colour.WHITE);
            moves = MoveUtil.getMovesInPosition(position);
            assertDoesNotHaveMoves(position, "O-O", moves);
            assertHasMoves(position, "O-O-O", moves); /* Cannot happen but we check anyway. */

            /* Black should not have castling rights. */
            position = position(white(k(b7)),
                    black(k(e8), r(a8, h8)),
                    Colour.BLACK);
            moves = MoveUtil.getMovesInPosition(position);
            assertDoesNotHaveMoves(position, "O-O-O", moves);
            assertHasMoves(position, "O-O", moves);

            position = position(white(k(c7)),
                    black(k(e8), r(a8, h8)),
                    Colour.BLACK);
            moves = MoveUtil.getMovesInPosition(position);
            assertDoesNotHaveMoves(position, "O-O-O", moves);
            assertHasMoves(position, "O-O", moves);

            position = position(white(k(g7)),
                    black(k(e8), r(a8, h8)),
                    Colour.BLACK);
            moves = MoveUtil.getMovesInPosition(position);
            assertDoesNotHaveMoves(position, "O-O", moves);
            assertHasMoves(position, "O-O-O", moves);

            position = position(white(k(h7)),
                    black(k(e8), r(a8, h8)),
                    Colour.BLACK);
            moves = MoveUtil.getMovesInPosition(position);
            assertDoesNotHaveMoves(position, "O-O", moves);
            assertHasMoves(position, "O-O-O", moves); /* Cannot happen but we check anyway. */

            /* Negative checks opponent's king not-preventing castling. */
            squaresStream(IntStream.range(15, 64)).forEach(
                    sq -> {
                        Position p = position(white(k(e1), r(a1, h1)),
                                black(k(sq)),
                                Colour.WHITE);
                        List<Integer> m = MoveUtil.getMovesInPosition(p);
                        assertHasMoves(p, "O-O O-O-O", m);
                    }
            );

            squaresStream(IntStream.concat(IntStream.range(0, 48), IntStream.of(55))).forEach(
                    sq -> {
                        Position p = position(white(k(sq)),
                                black(k(e8), r(a8, h8)),
                                Colour.BLACK);
                        List<Integer> m = MoveUtil.getMovesInPosition(p);
                        assertHasMoves(p, "O-O O-O-O", m);
                    }
            );
        }

        @Test
        public void testPawnsPreventingCastling() {
            /* White should not have castling rights. */
            Position position = position(white(k(e1), r(a1, h1)),
                    black(k(e8), p(e2)),
                    Colour.WHITE);
            List<Integer> moves = MoveUtil.getMovesInPosition(position);
            assertDoesNotHaveMoves(position, "O-O O-O-O", moves);

            /* Checking left castle. */
            position = position(white(k(e1), r(a1, h1)),
                    black(k(e8), p(b2)),
                    Colour.WHITE);
            moves = MoveUtil.getMovesInPosition(position);
            assertDoesNotHaveMoves(position, "O-O-O", moves);
            assertHasMoves(position, "O-O", moves);

            position = position(white(k(e1), r(a1, h1)),
                    black(k(e8), p(c2)),
                    Colour.WHITE);
            moves = MoveUtil.getMovesInPosition(position);
            assertDoesNotHaveMoves(position, "O-O-O", moves);
            assertHasMoves(position, "O-O", moves);

            position = position(white(k(e1), r(a1, h1)),
                    black(k(e8), p(d2)),
                    Colour.WHITE);
            moves = MoveUtil.getMovesInPosition(position);
            assertDoesNotHaveMoves(position, "O-O-O", moves);
            assertHasMoves(position, "O-O", moves); /* This should break when we start checking for checks. */

            /* Checking right castle. */
            position = position(white(k(e1), r(a1, h1)),
                    black(k(e8), p(f2)),
                    Colour.WHITE);
            moves = MoveUtil.getMovesInPosition(position);
            assertDoesNotHaveMoves(position, "O-O", moves);
            assertHasMoves(position, "O-O-O", moves); /* This should break when we start checking for checks. */

            position = position(white(k(e1), r(a1, h1)),
                    black(k(e8), p(g2)),
                    Colour.WHITE);
            moves = MoveUtil.getMovesInPosition(position);
            assertDoesNotHaveMoves(position, "O-O", moves);
            assertHasMoves(position, "O-O-O", moves);

            position = position(white(k(e1), r(a1, h1)),
                    black(k(e8), p(h2)),
                    Colour.WHITE);
            moves = MoveUtil.getMovesInPosition(position);
            assertDoesNotHaveMoves(position, "O-O", moves);
            assertHasMoves(position, "O-O-O", moves);

            /* Black should not have castling rights. */
            position = position(white(k(e1), p(e7)),
                    black(k(e8), r(a8, h8)),
                    Colour.BLACK);
            moves = MoveUtil.getMovesInPosition(position);
            assertDoesNotHaveMoves(position, "O-O O-O-O", moves);

            /* Checking left castle. */
            position = position(white(k(e1), p(b7)),
                    black(k(e8), r(a8, h8)),
                    Colour.BLACK);
            moves = MoveUtil.getMovesInPosition(position);
            assertDoesNotHaveMoves(position, "O-O-O", moves);
            assertHasMoves(position, "O-O", moves);

            position = position(white(k(e1), p(c7)),
                    black(k(e8), r(a8, h8)),
                    Colour.BLACK);
            moves = MoveUtil.getMovesInPosition(position);
            assertDoesNotHaveMoves(position, "O-O-O", moves);
            assertHasMoves(position, "O-O", moves);

            position = position(white(k(e1), p(d7)),
                    black(k(e8), r(a8, h8)),
                    Colour.BLACK);
            moves = MoveUtil.getMovesInPosition(position);
            assertDoesNotHaveMoves(position, "O-O-O", moves);
            assertHasMoves(position, "O-O", moves); /* This should break when we start checking for checks. */

            /* Checking right castle. */
            position = position(white(k(e1), p(f7)),
                    black(k(e8), r(a8, h8)),
                    Colour.BLACK);
            moves = MoveUtil.getMovesInPosition(position);
            assertDoesNotHaveMoves(position, "O-O", moves);
            assertHasMoves(position, "O-O-O", moves); /* This should break when we start checking for checks. */

            position = position(white(k(e1), p(g7)),
                    black(k(e8), r(a8, h8)),
                    Colour.BLACK);
            moves = MoveUtil.getMovesInPosition(position);
            assertDoesNotHaveMoves(position, "O-O", moves);
            assertHasMoves(position, "O-O-O", moves);

            position = position(white(k(e1), p(h7)),
                    black(k(e8), r(a8, h8)),
                    Colour.BLACK);
            moves = MoveUtil.getMovesInPosition(position);
            assertDoesNotHaveMoves(position, "O-O", moves);
            assertHasMoves(position, "O-O-O", moves);

            /* Negative checks pawns not-preventing castling. */
            position = position(white(k(e1), r(a1)),
                    black(k(e8), p(except(a1, b1, c1, d1, e1, f1, g1, h1, b2, c2, d2, e2, a8, b8, c8, d8, e8, f8, g8, h8))),
                    Colour.WHITE);
            moves = MoveUtil.getMovesInPosition(position);
            assertHasMoves(position, "O-O-O", moves); /* This should break when we start checking for checks. */

            position = position(white(k(e1), r(h1)),
                    black(k(e8), p(except(a1, b1, c1, d1, e1, f1, g1, h1, e2, f2, g2, h2, a8, b8, c8, d8, e8, f8, g8, h8))),
                    Colour.WHITE);
            moves = MoveUtil.getMovesInPosition(position);
            assertHasMoves(position, "O-O", moves); /* This should break when we start checking for checks. */

            position = position(white(k(e1), p(except(a1, b1, c1, d1, e1, f1, g1, h1, b7, c7, d7, e7, a8, b8, c8, d8, e8, f8, g8, h8))),
                    black(k(e8), r(a8)),
                    Colour.BLACK);
            moves = MoveUtil.getMovesInPosition(position);
            assertHasMoves(position, "O-O-O", moves); /* This should break when we start checking for checks. */

            position = position(white(k(e1), p(except(a1, b1, c1, d1, e1, f1, g1, h1, e7, f7, g7, h7, a8, b8, c8, d8, e8, f8, g8, h8))),
                    black(k(e8), r(h8)),
                    Colour.BLACK);
            moves = MoveUtil.getMovesInPosition(position);
            assertHasMoves(position, "O-O", moves); /* This should break when we start checking for checks. */
        }
    }
}
