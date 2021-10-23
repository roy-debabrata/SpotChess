package com.debabrata.spotchess.utils;

import com.debabrata.spotchess.types.Position;
import com.debabrata.spotchess.types.enums.Colour;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static com.debabrata.spotchess.support.SpotTestSupport.*;

public class MoveUtilTest {
    @Nested
    class KingMovesTest {
        @Test
        public void pawnsBlockedMoves() {
            /* Making sure pawns on the other side of the board doesn't affect this side. */
            Position position = position(white(k(b5)),black(k(a8),p(h3,h4,h5,h6,h7)),Colour.WHITE);
            assertOnlyMoves(position,"Ka4 Ka5 Ka6 Kb6 Kc6 Kc5 Kc4 Kb4");

            position = position(white(k(g5)),black(k(a8),p(a3,a4,a5,a6,a7)),Colour.WHITE);
            assertOnlyMoves(position,"Kf4 Kf5 Kf6 Kg6 Kh6 Kh5 Kh4 Kg4");

            position = position(white(k(a8),p(h3,h4,h5,h6,h7)),black(k(b5)),Colour.BLACK);
            assertOnlyMoves(position,"Ka4 Ka5 Ka6 Kb6 Kc6 Kc5 Kc4 Kb4");

            position = position(white(k(a8),p(a3,a4,a5,a6,a7)),black(k(g5)),Colour.BLACK);
            assertOnlyMoves(position,"Kf4 Kf5 Kf6 Kg6 Kh6 Kh5 Kh4 Kg4");

            /* Normal blocking moves. */
            position = position(white(k(d4)),black(k(a8),p(b4,b5,b6,c6,d6,f5,f4)),Colour.WHITE);
            assertOnlyMoves(position,"Kd3");

            position = position(white(k(d4)),black(k(a8),p(d5,c4,e4,d3,b6,f6)),Colour.WHITE);
            assertOnlyMoves(position,"Kc3 Ke3 Kxd5");

            position = position(white(k(a1),p(e3,d3,c3,c4,c5,g4,g5)),black(k(e5)),Colour.BLACK);
            assertOnlyMoves(position,"Ke6");

            position = position(white(k(a1),p(e6,d5,f5,e4,c3,g3)),black(k(e5)),Colour.BLACK);
            assertOnlyMoves(position,"Kd6 Kf6 Kxe4");
        }

        @Test
        public void knightBlockedMoves() {
            Position position = position(white(k(d4)),black(k(a1),n(c3,e5,f3)),Colour.WHITE);
            assertOnlyMoves(position,"Kxc3 Ke3 Kc5");

            position = position(white(k(a1),n(c3,e5,f3)),black(k(d4)),Colour.BLACK);
            assertOnlyMoves(position,"Kxc3 Ke3 Kc5");
        }

        @Test
        public void bishopBlockedMoves() {
            Position position = position(white(k(d4)),black(k(a1),b(e2,e3,f3)),Colour.WHITE);
            assertOnlyMoves(position,"Kc3 Ke5 Kxe3");

            position = position(white(k(d4)),black(k(a1),b(b2,e2,f2,f3)),Colour.WHITE);
            assertNoLegalMoves(position);

            position = position(white(k(a1),b(e2,e3,f3)),black(k(d4)),Colour.BLACK);
            assertOnlyMoves(position,"Kc3 Ke5 Kxe3");

            position = position(white(k(a1),b(b2,e2,f2,f3)),black(k(d4)),Colour.BLACK);
            assertNoLegalMoves(position);
        }

        @Test
        public void rookBlockedMoves() {
            Position position = position(white(k(d4)),black(k(a1),r(c1,e1)),Colour.WHITE);
            assertOnlyMoves(position,"Kd3 Kd5");

            position = position(white(k(d4)),black(k(a1),r(a3,a5)),Colour.WHITE);
            assertOnlyMoves(position,"Kc4 Ke4");

            position = position(white(k(d4)),black(k(a1),r(a3,a5,c4)),Colour.WHITE);
            assertOnlyMoves(position,"Kxc4");

            position = position(white(k(d4)),black(k(a1),r(a3,a5,a4)),Colour.WHITE);
            assertNoLegalMoves(position);

            position = position(white(k(a1),r(c1,e1)),black(k(d4)),Colour.BLACK);
            assertOnlyMoves(position,"Kd3 Kd5");

            position = position(white(k(a1),r(a3,a5)),black(k(d4)),Colour.BLACK);
            assertOnlyMoves(position,"Kc4 Ke4");

            position = position(white(k(a1),r(a3,a5,c4)),black(k(d4)),Colour.BLACK);
            assertOnlyMoves(position,"Kxc4");

            position = position(white(k(a1),r(a3,a5,a4)),black(k(d4)),Colour.BLACK);
            assertNoLegalMoves(position);
        }

        @Test
        public void queenBlockedMoves() {
            Position position = position(white(k(d4)),black(k(a1),q(f3)),Colour.WHITE);
            assertOnlyMoves(position,"Kc4 Ke5 Kc5");

            position = position(white(k(d4)),black(k(a1),q(c5,f3)),Colour.WHITE);
            assertOnlyMoves(position,"Kxc5");

            position = position(white(k(d4)),black(k(a1),q(b5,f3)),Colour.WHITE);
            assertNoLegalMoves(position);

            position = position(white(k(a1),q(f3)),black(k(d4)),Colour.BLACK);
            assertOnlyMoves(position,"Kc4 Ke5 Kc5");

            position = position(white(k(a1),q(c5,f3)),black(k(d4)),Colour.BLACK);
            assertOnlyMoves(position,"Kxc5");

            position = position(white(k(a1),q(b5,f3)),black(k(d4)),Colour.BLACK);
            assertNoLegalMoves(position);
        }

        @Test
        public void kingBlockedMoves() {
            Position position = position(white(k(d4)),black(k(b4)),Colour.WHITE);
            assertOnlyMoves(position,"Kd5 Kd3 Ke3 Ke4 Ke5");

            position = position(white(k(b4)),black(k(d4)),Colour.BLACK);
            assertOnlyMoves(position,"Kd5 Kd3 Ke3 Ke4 Ke5");
        }
    }

    @Nested
    class CastlingTest {
        @Test
        public void testCastling() {
            /* Should have castling rights. */
            Position position = position(white(k(e1),r(a1,h1)), black(k(e8)), Colour.WHITE);
            assertHasMoves(position, "O-O O-O-O");

            position = position(white(k(e1)), black(k(e8),r(a8,h8)), Colour.BLACK);
            assertHasMoves(position, "O-O O-O-O");
        }

        @Test
        public void testKnightsPreventingCastling() {
            /* White should not have castling rights. */
            Position position = position(white(k(e1), r(a1, h1)), black(k(e8), n(e3)), Colour.WHITE);
            assertDoesNotHaveMoves(position, "O-O O-O-O");

            position = position(white(k(e1), r(a1, h1)), black(k(e8), n(e2)), Colour.WHITE);
            assertDoesNotHaveMoves(position, "O-O O-O-O");

            /* Checking left castle. */
            position = position(white(k(e1), r(a1, h1)), black(k(e8), n(f2)), Colour.WHITE);
            assertHasMoves(position, "O-O");
            assertDoesNotHaveMoves(position, "O-O-O");

            position = position(white(k(e1), r(a1, h1)), black(k(e8), n(d3)), Colour.WHITE); /* This should break when we start checking for checks. */
            assertHasMoves(position, "O-O");
            assertDoesNotHaveMoves(position, "O-O-O");

            position = position(white(k(e1), r(a1, h1)), black(k(e8), n(c3)), Colour.WHITE);
            assertHasMoves(position, "O-O");
            assertDoesNotHaveMoves(position, "O-O-O");

            position = position(white(k(e1), r(a1, h1)), black(k(e8), n(b3)), Colour.WHITE);
            assertHasMoves(position, "O-O");
            assertDoesNotHaveMoves(position, "O-O-O");

            position = position(white(k(e1), r(a1, h1)), black(k(e8), n(b2)), Colour.WHITE);
            assertHasMoves(position, "O-O");
            assertDoesNotHaveMoves(position, "O-O-O");

            position = position(white(k(e1), r(a1, h1)), black(k(e8), n(a2)), Colour.WHITE);
            assertHasMoves(position, "O-O");
            assertDoesNotHaveMoves(position, "O-O-O");

            position = position(white(k(e1), r(a1, h1)), black(k(e8), n(a2, b2, b3, c3, d3, f2)), Colour.WHITE);
            assertHasMoves(position, "O-O"); /* This should break when we start checking for checks. */
            assertDoesNotHaveMoves(position, "O-O-O");

            /* Checking right castle. */
            position = position(white(k(e1), r(a1, h1)), black(k(e8), n(d2)), Colour.WHITE);
            assertHasMoves(position, "O-O-O");
            assertDoesNotHaveMoves(position, "O-O");

            position = position(white(k(e1), r(a1, h1)), black(k(e8), n(f3)), Colour.WHITE); /* This should break when we start checking for checks. */
            assertHasMoves(position, "O-O-O");
            assertDoesNotHaveMoves(position, "O-O");

            position = position(white(k(e1), r(a1, h1)), black(k(e8), n(g3)), Colour.WHITE);
            assertHasMoves(position, "O-O-O");
            assertDoesNotHaveMoves(position, "O-O");

            position = position(white(k(e1), r(a1, h1)), black(k(e8), n(h3)), Colour.WHITE);
            assertHasMoves(position, "O-O-O");
            assertDoesNotHaveMoves(position, "O-O");

            position = position(white(k(e1), r(a1, h1)), black(k(e8), n(h2)), Colour.WHITE);
            assertHasMoves(position, "O-O-O");
            assertDoesNotHaveMoves(position, "O-O");

            position = position(white(k(e1), r(a1, h1)), black(k(e8), n(d2, f3, g3, h3, h2)), Colour.WHITE);
            assertHasMoves(position, "O-O-O"); /* This should break when we start checking for checks. */
            assertDoesNotHaveMoves(position, "O-O");

            /* Black should not have castling rights. */
            position = position(white(k(e1), n(e6)), black(k(e8), r(a8, h8)), Colour.BLACK);
            assertDoesNotHaveMoves(position, "O-O O-O-O");

            position = position(white(k(e1), n(e7)), black(k(e8), r(a8, h8)), Colour.BLACK);
            assertDoesNotHaveMoves(position, "O-O O-O-O");

            /* Checking left castle. */
            position = position(white(k(e1), n(f7)), black(k(e8), r(a8, h8)), Colour.BLACK); /* This should break when we start checking for checks. */
            assertHasMoves(position, "O-O");
            assertDoesNotHaveMoves(position, "O-O-O");

            position = position(white(k(e1), n(d6)), black(k(e8), r(a8, h8)), Colour.BLACK); /* This should break when we start checking for checks. */
            assertHasMoves(position, "O-O");
            assertDoesNotHaveMoves(position, "O-O-O");

            position = position(white(k(e1), n(c6)), black(k(e8), r(a8, h8)), Colour.BLACK);
            assertHasMoves(position, "O-O");
            assertDoesNotHaveMoves(position, "O-O-O");

            position = position(white(k(e1), n(b6)), black(k(e8), r(a8, h8)), Colour.BLACK);
            assertHasMoves(position, "O-O");
            assertDoesNotHaveMoves(position, "O-O-O");

            position = position(white(k(e1), n(b7)), black(k(e8), r(a8, h8)), Colour.BLACK);
            assertHasMoves(position, "O-O");
            assertDoesNotHaveMoves(position, "O-O-O");

            position = position(white(k(e1), n(a7)), black(k(e8), r(a8, h8)), Colour.BLACK);
            assertHasMoves(position, "O-O");
            assertDoesNotHaveMoves(position, "O-O-O");

            position = position(white(k(e1), n(a7, b7, b6, c6, d6, f7)), black(k(e8), r(a8, h8)), Colour.BLACK);
            assertHasMoves(position, "O-O"); /* This should break when we start checking for checks. */
            assertDoesNotHaveMoves(position, "O-O-O");

            /* Checking right castle. */
            position = position(white(k(e1), n(d7)), black(k(e8), r(a8, h8)), Colour.BLACK);
            assertHasMoves(position, "O-O-O");
            assertDoesNotHaveMoves(position, "O-O");

            position = position(white(k(e1), n(f6)), black(k(e8), r(a8, h8)), Colour.BLACK); /* This should break when we start checking for checks. */
            assertHasMoves(position, "O-O-O");
            assertDoesNotHaveMoves(position, "O-O");

            position = position(white(k(e1), n(g6)), black(k(e8), r(a8, h8)), Colour.BLACK);
            assertHasMoves(position, "O-O-O");
            assertDoesNotHaveMoves(position, "O-O");

            position = position(white(k(e1), n(h6)), black(k(e8), r(a8, h8)), Colour.BLACK);
            assertHasMoves(position, "O-O-O");
            assertDoesNotHaveMoves(position, "O-O");

            position = position(white(k(e1), n(h7)), black(k(e8), r(a8, h8)), Colour.BLACK);
            assertHasMoves(position, "O-O-O");
            assertDoesNotHaveMoves(position, "O-O");

            position = position(white(k(e1), n(d7, f6, g6, h6, h7)), black(k(e8), r(a8, h8)), Colour.BLACK);
            assertHasMoves(position, "O-O-O"); /* This should break when we start checking for checks. */
            assertDoesNotHaveMoves(position, "O-O");

            /* Negative checks knight not-preventing castling. */
            position = position(white(k(e1), r(a1)),
                    black(k(e8), n(except(a1, b1, c1, d1, e1, e8, a2, b2, b3, c3, d3, e2, e3, f2))
                    ), Colour.WHITE);
            assertHasMoves(position, "O-O-O"); /* This should break when we start checking for checks. */

            position = position(white(k(e1), r(h1)),
                    black(k(e8), n(except(e1, f1, g1, h1, e8, d2, e2, e3, f3, g3, h3, h2))
                    ), Colour.WHITE);
            assertHasMoves(position, "O-O"); /* This should break when we start checking for checks. */

            position = position(white(k(e1), n(except(a8, b8, c8, d8, e8, e1, a7, b7, b6, c6, d6, e6, e7, f7))), black(k(e8), r(a8)), Colour.BLACK);
            assertHasMoves(position, "O-O-O"); /* This should break when we start checking for checks. */

            position = position(white(k(e1), n(except(e8, f8, g8, h8, e1, d7, e7, e6, f6, g6, h6, h7))), black(k(e8), r(h8)), Colour.BLACK);
            assertHasMoves(position, "O-O"); /* This should break when we start checking for checks. */
        }

        @Test
        public void testKingPreventingCastling() {
            /* White should not have castling rights. */
            Position position = position(white(k(e1), r(a1, h1)),
                    black(k(b2)),
                    Colour.WHITE);
            assertDoesNotHaveMoves(position, "O-O-O");
            assertHasMoves(position, "O-O");

            position = position(white(k(e1), r(a1, h1)),
                    black(k(c2)),
                    Colour.WHITE);
            assertDoesNotHaveMoves(position, "O-O-O");
            assertHasMoves(position, "O-O");

            position = position(white(k(e1), r(a1, h1)),
                    black(k(g2)),
                    Colour.WHITE);
            assertDoesNotHaveMoves(position, "O-O");
            assertHasMoves(position, "O-O-O");

            position = position(white(k(e1), r(a1, h1)),
                    black(k(h2)),
                    Colour.WHITE);
            assertDoesNotHaveMoves(position, "O-O");
            assertHasMoves(position, "O-O-O"); /* Cannot happen but we check anyway. */

            /* Black should not have castling rights. */
            position = position(white(k(b7)),
                    black(k(e8), r(a8, h8)),
                    Colour.BLACK);
            assertDoesNotHaveMoves(position, "O-O-O");
            assertHasMoves(position, "O-O");

            position = position(white(k(c7)),
                    black(k(e8), r(a8, h8)),
                    Colour.BLACK);
            assertDoesNotHaveMoves(position, "O-O-O");
            assertHasMoves(position, "O-O");

            position = position(white(k(g7)),
                    black(k(e8), r(a8, h8)),
                    Colour.BLACK);
            assertDoesNotHaveMoves(position, "O-O");
            assertHasMoves(position, "O-O-O");

            position = position(white(k(h7)),
                    black(k(e8), r(a8, h8)),
                    Colour.BLACK);
            assertDoesNotHaveMoves(position, "O-O");
            assertHasMoves(position, "O-O-O"); /* Cannot happen but we check anyway. */

            /* Negative checks opponent's king not-preventing castling. */
            squaresStream(IntStream.range(15, 64)).forEach(
                    sq -> {
                        Position p = position(white(k(e1), r(a1, h1)),
                                black(k(sq)),
                                Colour.WHITE);
                        assertHasMoves(p, "O-O O-O-O");
                    }
            );

            squaresStream(IntStream.concat(IntStream.range(0, 48), IntStream.of(55))).forEach(
                    sq -> {
                        Position p = position(white(k(sq)),
                                black(k(e8), r(a8, h8)),
                                Colour.BLACK);
                        assertHasMoves(p, "O-O O-O-O");
                    }
            );
        }

        @Test
        public void testPawnsPreventingCastling() {
            /* White should not have castling rights. */
            Position position = position(white(k(e1), r(a1, h1)),
                    black(k(e8), p(e2)),
                    Colour.WHITE);
            assertDoesNotHaveMoves(position, "O-O O-O-O");

            /* Checking left castle. */
            position = position(white(k(e1), r(a1, h1)),
                    black(k(e8), p(b2)),
                    Colour.WHITE);
            assertDoesNotHaveMoves(position, "O-O-O");
            assertHasMoves(position, "O-O");

            position = position(white(k(e1), r(a1, h1)),
                    black(k(e8), p(c2)),
                    Colour.WHITE);
            assertDoesNotHaveMoves(position, "O-O-O");
            assertHasMoves(position, "O-O");

            position = position(white(k(e1), r(a1, h1)),
                    black(k(e8), p(d2)),
                    Colour.WHITE);
            assertDoesNotHaveMoves(position, "O-O-O");
            assertHasMoves(position, "O-O"); /* This should break when we start checking for checks. */

            /* Checking right castle. */
            position = position(white(k(e1), r(a1, h1)),
                    black(k(e8), p(f2)),
                    Colour.WHITE);
            assertDoesNotHaveMoves(position, "O-O");
            assertHasMoves(position, "O-O-O"); /* This should break when we start checking for checks. */

            position = position(white(k(e1), r(a1, h1)),
                    black(k(e8), p(g2)),
                    Colour.WHITE);
            assertDoesNotHaveMoves(position, "O-O");
            assertHasMoves(position, "O-O-O");

            position = position(white(k(e1), r(a1, h1)),
                    black(k(e8), p(h2)),
                    Colour.WHITE);
            assertDoesNotHaveMoves(position, "O-O");
            assertHasMoves(position, "O-O-O");

            /* Black should not have castling rights. */
            position = position(white(k(e1), p(e7)),
                    black(k(e8), r(a8, h8)),
                    Colour.BLACK);
            assertDoesNotHaveMoves(position, "O-O O-O-O");

            /* Checking left castle. */
            position = position(white(k(e1), p(b7)),
                    black(k(e8), r(a8, h8)),
                    Colour.BLACK);
            assertDoesNotHaveMoves(position, "O-O-O");
            assertHasMoves(position, "O-O");

            position = position(white(k(e1), p(c7)),
                    black(k(e8), r(a8, h8)),
                    Colour.BLACK);
            assertDoesNotHaveMoves(position, "O-O-O");
            assertHasMoves(position, "O-O");

            position = position(white(k(e1), p(d7)),
                    black(k(e8), r(a8, h8)),
                    Colour.BLACK);
            assertDoesNotHaveMoves(position, "O-O-O");
            assertHasMoves(position, "O-O"); /* This should break when we start checking for checks. */

            /* Checking right castle. */
            position = position(white(k(e1), p(f7)),
                    black(k(e8), r(a8, h8)),
                    Colour.BLACK);
            assertDoesNotHaveMoves(position, "O-O");
            assertHasMoves(position, "O-O-O"); /* This should break when we start checking for checks. */

            position = position(white(k(e1), p(g7)),
                    black(k(e8), r(a8, h8)),
                    Colour.BLACK);
            assertDoesNotHaveMoves(position, "O-O");
            assertHasMoves(position, "O-O-O");

            position = position(white(k(e1), p(h7)),
                    black(k(e8), r(a8, h8)),
                    Colour.BLACK);
            assertDoesNotHaveMoves(position, "O-O");
            assertHasMoves(position, "O-O-O");

            /* Negative checks pawns not-preventing castling. */
            position = position(white(k(e1), r(a1)),
                    black(k(e8), p(except(a1, b1, c1, d1, e1, f1, g1, h1, b2, c2, d2, e2, a8, b8, c8, d8, e8, f8, g8, h8))),
                    Colour.WHITE);
            assertHasMoves(position, "O-O-O"); /* This should break when we start checking for checks. */

            position = position(white(k(e1), r(h1)),
                    black(k(e8), p(except(a1, b1, c1, d1, e1, f1, g1, h1, e2, f2, g2, h2, a8, b8, c8, d8, e8, f8, g8, h8))),
                    Colour.WHITE);
            assertHasMoves(position, "O-O"); /* This should break when we start checking for checks. */

            position = position(white(k(e1), p(except(a1, b1, c1, d1, e1, f1, g1, h1, b7, c7, d7, e7, a8, b8, c8, d8, e8, f8, g8, h8))),
                    black(k(e8), r(a8)),
                    Colour.BLACK);
            assertHasMoves(position, "O-O-O"); /* This should break when we start checking for checks. */

            position = position(white(k(e1), p(except(a1, b1, c1, d1, e1, f1, g1, h1, e7, f7, g7, h7, a8, b8, c8, d8, e8, f8, g8, h8))),
                    black(k(e8), r(h8)),
                    Colour.BLACK);
            assertHasMoves(position, "O-O"); /* This should break when we start checking for checks. */
        }

        @Test
        public void testRookPreventingCastling() {
            /* Black preventing white from castling. */
            Position position = position(white(k(e1),r(a1,h1)),black(k(h8),r(a8)),Colour.WHITE);
            assertHasMoves(position,"O-O-O O-O");

            position = position(white(k(e1),r(a1,h1)),black(k(a8),r(b8)),Colour.WHITE);
            assertHasMoves(position,"O-O-O O-O");

            position = position(white(k(e1),r(a1,h1)),black(k(a8),r(c8)),Colour.WHITE);
            assertHasMoves(position,"O-O");
            assertDoesNotHaveMoves(position,"O-O-O");

            position = position(white(k(e1),r(a1,h1)),black(k(a8),r(d8)),Colour.WHITE);
            assertHasMoves(position,"O-O");
            assertDoesNotHaveMoves(position,"O-O-O");

            position = position(white(k(e1),r(a1,h1)),black(k(a8),r(e8)),Colour.WHITE);
            assertHasMoves(position,"O-O-O O-O"); /* This should break when we start checking for checks. */

            position = position(white(k(e1),r(a1,h1)),black(k(a8),r(f8)),Colour.WHITE);
            assertHasMoves(position,"O-O-O");
            assertDoesNotHaveMoves(position,"O-O");

            position = position(white(k(e1),r(a1,h1)),black(k(a8),r(g8)),Colour.WHITE);
            assertHasMoves(position,"O-O-O");
            assertDoesNotHaveMoves(position,"O-O");

            /* White preventing black from castling. */
            position = position(white(k(h1),r(a1)),black(k(e8),r(a8,h8)),Colour.BLACK);
            assertHasMoves(position,"O-O-O O-O");

            position = position(white(k(a1),r(b1)),black(k(e8),r(a8,h8)),Colour.BLACK);
            assertHasMoves(position,"O-O-O O-O");

            position = position(white(k(a1),r(c1)),black(k(e8),r(a8,h8)),Colour.BLACK);
            assertHasMoves(position,"O-O");
            assertDoesNotHaveMoves(position,"O-O-O");

            position = position(white(k(a1),r(d1)),black(k(e8),r(a8,h8)),Colour.BLACK);
            assertHasMoves(position,"O-O");
            assertDoesNotHaveMoves(position,"O-O-O");

            position = position(white(k(a1),r(e1)),black(k(e8),r(a8,h8)),Colour.BLACK);
            assertHasMoves(position,"O-O-O O-O"); /* This should break when we start checking for checks. */

            position = position(white(k(a1),r(f1)),black(k(e8),r(a8,h8)),Colour.BLACK);
            assertHasMoves(position,"O-O-O");
            assertDoesNotHaveMoves(position,"O-O");

            position = position(white(k(a1),r(g1)),black(k(e8),r(a8,h8)),Colour.BLACK);
            assertHasMoves(position,"O-O-O");
            assertDoesNotHaveMoves(position,"O-O");

            position = position(white(k(a1),r(h1)),black(k(e8),r(a8,h8)),Colour.BLACK);
            assertHasMoves(position,"O-O-O O-O");
        }

        @Test
        public void testBishopPreventingCastling() {
            /* Black preventing white from castling. */
            Position position = position(white(k(e1),r(a1,h1)),black(k(a8),b(e2)),Colour.WHITE);
            assertDoesNotHaveMoves(position,"O-O-O O-O");

            position = position(white(k(e1),r(a1,h1)),black(k(a8),b(e3)),Colour.WHITE);
            assertDoesNotHaveMoves(position,"O-O-O O-O");

            position = position(white(k(e1),r(a1,h1)),black(k(a8),b(e4)),Colour.WHITE);
            assertHasMoves(position,"O-O-O O-O");

            position = position(white(k(e1),r(a1,h1)),black(k(a8),b(e5)),Colour.WHITE);
            assertHasMoves(position,"O-O-O O-O");

            /* White preventing black from castling. */
            position = position(white(k(a1),b(e7)),black(k(e8),r(a8,h8)),Colour.BLACK);
            assertDoesNotHaveMoves(position,"O-O-O O-O");

            position = position(white(k(a1),b(e6)),black(k(e8),r(a8,h8)),Colour.BLACK);
            assertDoesNotHaveMoves(position,"O-O-O O-O");

            position = position(white(k(a1),b(e5)),black(k(e8),r(a8,h8)),Colour.BLACK);
            assertHasMoves(position,"O-O-O O-O");

            position = position(white(k(a1),b(e4)),black(k(e8),r(a8,h8)),Colour.BLACK);
            assertHasMoves(position,"O-O-O O-O");
        }
    }

    @Nested
    class PinsTest {
        @Test
        public void testPinnedPawns() {
            /* Black bishop/queen pins pawn a8-h1-diagonally. */
            Position position = position(white(k(h1),p(e4)),black(k(a8),b(d5)),Colour.WHITE);
            assertHasMoves(position, "exd5");
            assertDoesNotHaveMoves(position, "e5 exf5");

            position = position(white(k(h1),p(e4)),black(k(a8),b(c6)),Colour.WHITE);
            assertDoesNotHaveMoves(position, "e5 exd5 exf5");

            position.removePiece(c6.placeValue);
            assertHasMoves(position, "e5");

            position = position(white(k(d4),p(e3)),black(k(a8),b(f2)),Colour.WHITE);
            assertDoesNotHaveMoves(position, "e4");

            position.removePiece(f2.placeValue);
            assertHasMoves(position, "e4");

            position = position(white(k(d4),p(e5)),black(k(a8),p(d7),b(h8)),Colour.BLACK);
            move(position, "d5");
            assertDoesNotHaveMoves(position, "exd6"); /* En-passant. */

            position.removePiece(h8.placeValue);
            assertHasMoves(position, "exd6");

            /* Black bishop/queen pins pawn a1-h8-diagonally. */
            position = position(white(k(a1),p(d4)),black(k(a8),b(e5)),Colour.WHITE);
            assertHasMoves(position, "dxe5");
            assertDoesNotHaveMoves(position, "d5 dxf5");

            position = position(white(k(a1),p(d4)),black(k(a8),b(g7)),Colour.WHITE);
            assertDoesNotHaveMoves(position, "d5 dxe5 dxf5");

            position.removePiece(g7.placeValue);
            assertHasMoves(position, "d5");

            position = position(white(k(e5),p(b2)),black(k(a8),b(a1)),Colour.WHITE);
            assertDoesNotHaveMoves(position, "b3 b4");

            position.removePiece(a1.placeValue);
            assertHasMoves(position, "b3 b4");

            position = position(white(k(e4),p(d5)),black(k(a1),p(e7),b(a8)),Colour.BLACK);
            move(position, "e5");
            assertDoesNotHaveMoves(position, "dxe6");

            position.removePiece(a8.placeValue);
            assertHasMoves(position, "dxe6");

            /* Black rook/queen pins pawn horizontally. */
            position = position(white(k(c2),p(f2)),black(k(a8),r(h2)),Colour.WHITE);
            assertDoesNotHaveMoves(position, "f3 f4 fxe3 fxg3");

            position.removePiece(h2.placeValue);
            assertHasMoves(position, "f3 f4");

            position = position(white(k(f3),p(c3)),black(k(a8),r(a3)),Colour.WHITE);
            assertDoesNotHaveMoves(position, "c4 cxb4 cxd4");

            position.removePiece(a3.placeValue);
            assertHasMoves(position, "c4");

            /* En-passant take exposes king to enemy rook. */
            position = position(white(k(c5),p(d5)),black(k(h8),p(e7),r(h5)),Colour.BLACK);
            move(position,"e5");
            assertDoesNotHaveMoves(position, "dxe6");

            position = position(white(k(f5),p(e5)),black(k(a8),p(d7),r(a5)),Colour.BLACK);
            move(position,"d5");
            assertDoesNotHaveMoves(position, "exd6");

            /* Black rook/queen pins pawn vertically. */
            position = position(white(k(d1),p(d2)),black(k(a8),r(d8)),Colour.WHITE);
            assertHasMoves(position, "d3 d4");

            position = position(white(k(d8),p(d2)),black(k(a8),r(d1)),Colour.WHITE);
            assertHasMoves(position, "d3 d4");

            position = position(white(k(d4),p(d2)),black(k(a8),r(d1)),Colour.WHITE);
            assertHasMoves(position, "d3");
            assertDoesNotHaveMoves(position, "d4");

            position = position(white(k(d3),p(d2)),black(k(a8),r(d1)),Colour.WHITE);
            assertDoesNotHaveMoves(position, "d3 d4");

            position = position(white(k(d8),p(d2)),black(k(a8),p(c3,e3),r(d1)),Colour.WHITE);
            assertHasMoves(position, "d3 d4");
            assertDoesNotHaveMoves(position, "dxc3 dxe3");

            position.removePiece(d1.placeValue);
            assertHasMoves(position, "d3 d4 dxc3 dxe3");

            /* White bishop/queen pins pawn a8-h1-diagonally. */
            position = position(white(k(a1),b(d4)),black(k(h8),p(e5)),Colour.BLACK);
            assertHasMoves(position, "exd4");
            assertDoesNotHaveMoves(position, "e4 exf4");

            position = position(white(k(a1),b(c3)),black(k(h8),p(e5)),Colour.BLACK);
            assertDoesNotHaveMoves(position, "e4 exd4 exf4");

            position.removePiece(c3.placeValue);
            assertHasMoves(position, "e4");

            position = position(white(k(a1),b(f7)),black(k(d5),p(e6)),Colour.BLACK);
            assertDoesNotHaveMoves(position, "e5");

            position.removePiece(f7.placeValue);
            assertHasMoves(position, "e5");

            position = position(white(k(a1),p(d2),b(h1)),black(k(d5),p(e4)),Colour.WHITE);
            move(position, "d4");
            assertDoesNotHaveMoves(position, "exd3"); /* En-passant. */

            position.removePiece(h1.placeValue);
            assertHasMoves(position, "exd3");

            /* White bishop/queen pins pawn a1-h8-diagonally. */
            position = position(white(k(a1),b(e4)),black(k(a8),p(d5)),Colour.BLACK);
            assertHasMoves(position, "dxe4");
            assertDoesNotHaveMoves(position, "d4 dxf4");

            position = position(white(k(a1),b(g2)),black(k(a8),p(d5)),Colour.BLACK);
            assertDoesNotHaveMoves(position, "d4 dxe4 dxf4");

            position.removePiece(g2.placeValue);
            assertHasMoves(position, "d4");

            position = position(white(k(a1),b(a8)),black(k(e4),p(b7)),Colour.BLACK);
            assertDoesNotHaveMoves(position, "b6 b5");

            position.removePiece(a8.placeValue);
            assertHasMoves(position, "b6 b5");

            position = position(white(k(a8),p(e2),b(a1)),black(k(e5),p(d4)),Colour.WHITE);
            move(position, "e4");
            assertDoesNotHaveMoves(position, "dxe3");

            position.removePiece(a1.placeValue);
            assertHasMoves(position, "dxe3");

            /* White rook/queen pins pawn horizontally. */
            position = position(white(k(a1),r(h7)),black(k(c7),p(f7)),Colour.BLACK);
            assertDoesNotHaveMoves(position, "f6 f5 fxe6 fxg6");

            position.removePiece(h7.placeValue);
            assertHasMoves(position, "f6 f5");

            position = position(white(k(a1),r(a6)),black(k(f6),p(c6)),Colour.BLACK);
            assertDoesNotHaveMoves(position, "c5 cxb5 cxd5");

            position.removePiece(a6.placeValue);
            assertHasMoves(position, "c5");

            /* En-passant take exposes king to enemy rook. */
            position = position(white(k(h1),p(e2),r(h4)),black(k(c4),p(d4)),Colour.WHITE);
            move(position,"e4");
            assertDoesNotHaveMoves(position, "dxe3");

            position = position(white(k(a1),p(d2),r(a4)),black(k(f4),p(e4)),Colour.WHITE);
            move(position,"d4");
            assertDoesNotHaveMoves(position, "exd3");

            /* White rook/queen pins pawn vertically. */
            position = position(white(k(a1),r(d1)),black(k(d8),p(d7)),Colour.BLACK);
            assertHasMoves(position, "d6 d5");

            position = position(white(k(a1),r(d8)),black(k(d1),p(d7)),Colour.BLACK);
            assertHasMoves(position, "d6 d5");

            position = position(white(k(a1),r(d8)),black(k(d5),p(d7)),Colour.BLACK);
            assertHasMoves(position, "d6");
            assertDoesNotHaveMoves(position, "d5");

            position = position(white(k(a1),r(d8)),black(k(d6),p(d7)),Colour.BLACK);
            assertDoesNotHaveMoves(position, "d5 d6");

            position = position(white(k(a1),p(c6,e6),r(d8)),black(k(d1),p(d7)),Colour.BLACK);
            assertHasMoves(position, "d6 d5");
            assertDoesNotHaveMoves(position, "dxc6 dxe6");

            position.removePiece(d8.placeValue);
            assertHasMoves(position, "d6 d5 dxc6 dxe6");
        }

        @Test
        public void testPinnedKnights() {
            /* White knight pinned by rook. */
            Position position = position(white(k(e4),n(d4)),black(k(a1),r(c4)),Colour.WHITE);
            assertDoesNotHaveMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position.removePiece(c4.placeValue);
            assertHasMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position = position(white(k(d5),n(d4)),black(k(a1),r(d3)),Colour.WHITE);
            assertDoesNotHaveMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position.removePiece(d3.placeValue);
            assertHasMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position = position(white(k(c4),n(d4)),black(k(a1),r(e4)),Colour.WHITE);
            assertDoesNotHaveMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position.removePiece(e4.placeValue);
            assertHasMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position = position(white(k(d3),n(d4)),black(k(a1),r(d5)),Colour.WHITE);
            assertDoesNotHaveMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position.removePiece(d5.placeValue);
            assertHasMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            /* White knight pinned by queen. */
            position = position(white(k(e4),n(d4)),black(k(a1),q(c4)),Colour.WHITE);
            assertDoesNotHaveMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position = position(white(k(d5),n(d4)),black(k(a1),q(d3)),Colour.WHITE);
            assertDoesNotHaveMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position = position(white(k(c4),n(d4)),black(k(a1),q(e4)),Colour.WHITE);
            assertDoesNotHaveMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position = position(white(k(d3),n(d4)),black(k(a1),q(d5)),Colour.WHITE);
            assertDoesNotHaveMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position = position(white(k(c3),n(d4)),black(k(a1),q(e5)),Colour.WHITE);
            assertDoesNotHaveMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position = position(white(k(c5),n(d4)),black(k(a1),q(e3)),Colour.WHITE);
            assertDoesNotHaveMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position = position(white(k(e5),n(d4)),black(k(a1),q(c3)),Colour.WHITE);
            assertDoesNotHaveMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position = position(white(k(e5),n(d4)),black(k(a1),q(c3)),Colour.WHITE);
            assertDoesNotHaveMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            /* White knight pinned by bishop. */
            position = position(white(k(c3),n(d4)),black(k(a1),b(e5)),Colour.WHITE);
            assertDoesNotHaveMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position.removePiece(e5.placeValue);
            assertHasMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position = position(white(k(c5),n(d4)),black(k(a1),b(e3)),Colour.WHITE);
            assertDoesNotHaveMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position.removePiece(e3.placeValue);
            assertHasMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position = position(white(k(e5),n(d4)),black(k(a1),b(c3)),Colour.WHITE);
            assertDoesNotHaveMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position.removePiece(c3.placeValue);
            assertHasMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position = position(white(k(e5),n(d4)),black(k(a1),b(c3)),Colour.WHITE);
            assertDoesNotHaveMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position.removePiece(c3.placeValue);
            assertHasMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            /* Black knight pinned by rook. */
            position = position(white(k(a1),r(c4)),black(k(e4),n(d4)),Colour.BLACK);
            assertDoesNotHaveMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position.removePiece(c4.placeValue);
            assertHasMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position = position(white(k(a1),r(d3)),black(k(d5),n(d4)),Colour.BLACK);
            assertDoesNotHaveMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position.removePiece(d3.placeValue);
            assertHasMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position = position(white(k(a1),r(e4)),black(k(c4),n(d4)),Colour.BLACK);
            assertDoesNotHaveMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position.removePiece(e4.placeValue);
            assertHasMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position = position(white(k(a1),r(d5)),black(k(d3),n(d4)),Colour.BLACK);
            assertDoesNotHaveMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position.removePiece(d5.placeValue);
            assertHasMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            /* Black knight pinned by queen. */
            position = position(white(k(a1),q(c4)),black(k(e4),n(d4)),Colour.BLACK);
            assertDoesNotHaveMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position = position(white(k(a1),q(d3)),black(k(d5),n(d4)),Colour.BLACK);
            assertDoesNotHaveMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position = position(white(k(a1),q(e4)),black(k(c4),n(d4)),Colour.BLACK);
            assertDoesNotHaveMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position = position(white(k(a1),q(d5)),black(k(d3),n(d4)),Colour.BLACK);
            assertDoesNotHaveMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position = position(white(k(a1),q(e5)),black(k(c3),n(d4)),Colour.BLACK);
            assertDoesNotHaveMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position = position(white(k(a1),q(e3)),black(k(c5),n(d4)),Colour.BLACK);
            assertDoesNotHaveMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position = position(white(k(a1),q(c3)),black(k(e5),n(d4)),Colour.BLACK);
            assertDoesNotHaveMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position = position(white(k(a1),q(c3)),black(k(e5),n(d4)),Colour.BLACK);
            assertDoesNotHaveMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            /* Black knight pinned by bishop. */
            position = position(white(k(a1),b(e5)),black(k(c3),n(d4)),Colour.BLACK);
            assertDoesNotHaveMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position.removePiece(e5.placeValue);
            assertHasMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position = position(white(k(a1),b(e3)),black(k(c5),n(d4)),Colour.BLACK);
            assertDoesNotHaveMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position.removePiece(e3.placeValue);
            assertHasMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position = position(white(k(a1),b(c3)),black(k(e5),n(d4)),Colour.BLACK);
            assertDoesNotHaveMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position.removePiece(c3.placeValue);
            assertHasMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position = position(white(k(a1),b(c3)),black(k(e5),n(d4)),Colour.BLACK);
            assertDoesNotHaveMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");

            position.removePiece(c3.placeValue);
            assertHasMoves(position,"Nc2 Nb3 Nb5 Nc6 Ne6 Nf5 Nf3 Ne2");
        }

        @Test
        public void testPinnedRooks() {
            /* Complete pin by bishop/queen. */
            /* Black bishop white rook. */
            Position position = position(white(k(d4),r(c3)), black(k(a8),b(a1)), Colour.WHITE);
            assertDoesNotHaveMoves(position, "Rc2 Rc4 Rb3 Rd3");

            position.removePiece(a1.placeValue);
            assertHasMoves(position, "Rc2 Rc4 Rb3 Rd3");

            position = position(white(k(d4),r(e5)), black(k(a8),b(h8)), Colour.WHITE);
            assertDoesNotHaveMoves(position, "Re4 Re6 Rd5 Rf5");

            position.removePiece(h8.placeValue);
            assertHasMoves(position, "Re4 Re6 Rd5 Rf5");

            position = position(white(k(d4),r(c5)), black(k(a8),b(a7)), Colour.WHITE);
            assertDoesNotHaveMoves(position, "Rc4 Rc6 Rc7 Rc8");

            position.removePiece(a7.placeValue);
            assertHasMoves(position, "Rc4 Rc6 Rc7 Rc8");

            position = position(white(k(d4),r(e3)), black(k(a8),b(g1)), Colour.WHITE);
            assertDoesNotHaveMoves(position, "Re2 Re4 Rd3 Rf3");

            position.removePiece(g1.placeValue);
            assertHasMoves(position, "Re2 Re4 Rd3 Rf3");

            /* Black queen white rook. */
            position = position(white(k(d4),r(c3)), black(k(a8),q(a1)), Colour.WHITE);
            assertDoesNotHaveMoves(position, "Rc2 Rc4 Rb3 Rd3");

            position = position(white(k(d4),r(e5)), black(k(a8),q(h8)), Colour.WHITE);
            assertDoesNotHaveMoves(position, "Re4 Re6 Rd5 Rf5");

            position = position(white(k(d4),r(c5)), black(k(a8),q(a7)), Colour.WHITE);
            assertDoesNotHaveMoves(position, "Rc4 Rc6 Rc7 Rc8");

            position = position(white(k(d4),r(e3)), black(k(a8),q(g1)), Colour.WHITE);
            assertDoesNotHaveMoves(position, "Re2 Re4 Rd3 Rf3");

            /* White bishop black rook. */
            position = position(white(k(a8),b(a1)), black(k(d4),r(c3)), Colour.BLACK);
            assertDoesNotHaveMoves(position, "Rc2 Rc4 Rb3 Rd3");

            position.removePiece(a1.placeValue);
            assertHasMoves(position, "Rc2 Rc4 Rb3 Rd3");

            position = position(white(k(a8),b(h8)), black(k(d4),r(e5)), Colour.BLACK);
            assertDoesNotHaveMoves(position, "Re4 Re6 Rd5 Rf5");

            position.removePiece(h8.placeValue);
            assertHasMoves(position, "Re4 Re6 Rd5 Rf5");

            position = position(white(k(a8),b(a7)), black(k(d4),r(c5)), Colour.BLACK);
            assertDoesNotHaveMoves(position, "Rc4 Rc6 Rc7 Rc8");

            position.removePiece(a7.placeValue);
            assertHasMoves(position, "Rc4 Rc6 Rc7 Rc8");

            position = position(white(k(a8),b(g1)), black(k(d4),r(e3)), Colour.BLACK);
            assertDoesNotHaveMoves(position, "Re2 Re4 Rd3 Rf3");

            position.removePiece(g1.placeValue);
            assertHasMoves(position, "Re2 Re4 Rd3 Rf3");

            /* White queen black rook. */
            position = position(white(k(a8),q(a1)), black(k(d4),r(c3)), Colour.BLACK);
            assertDoesNotHaveMoves(position, "Rc2 Rc4 Rb3 Rd3");

            position = position(white(k(a8),q(h8)), black(k(d4),r(e5)), Colour.BLACK);
            assertDoesNotHaveMoves(position, "Re4 Re6 Rd5 Rf5");

            position = position(white(k(a8),q(a7)), black(k(d4),r(c5)), Colour.BLACK);
            assertDoesNotHaveMoves(position, "Rc4 Rc6 Rc7 Rc8");

            position = position(white(k(a8),q(g1)), black(k(d4),r(e3)), Colour.BLACK);
            assertDoesNotHaveMoves(position, "Re2 Re4 Rd3 Rf3");

            /* Partial pin by rook/queen. */
            /* Black rook pins white rook. */
            position = position(white(k(e5),r(c5)), black(k(a8),r(a5)), Colour.WHITE);
            assertHasMoves(position, "Rxa5 Rb5 Rd5");
            assertDoesNotHaveMoves(position, "Rc4 Rc6");

            position = position(white(k(d4),r(f4)), black(k(a8),r(g4)), Colour.WHITE);
            assertHasMoves(position, "Rxg4 Re4");
            assertDoesNotHaveMoves(position, "Rf3 Rf5 Rh4");

            position = position(white(k(d4),r(d6)), black(k(a8),r(d8)), Colour.WHITE);
            assertHasMoves(position, "Rxd8 Rd7 Rd5");
            assertDoesNotHaveMoves(position, "Rc6 Re6");

            position = position(white(k(d6),r(d4)), black(k(a8),r(d2)), Colour.WHITE);
            assertHasMoves(position, "Rxd2 Rd3 Rd5");
            assertDoesNotHaveMoves(position, "Rc4 Re4 Rd1");

            /* Black queen pins white rook. */
            position = position(white(k(e5),r(c5)), black(k(a8),q(a5)), Colour.WHITE);
            assertHasMoves(position, "Rxa5 Rb5 Rd5");
            assertDoesNotHaveMoves(position, "Rc4 Rc6");

            position = position(white(k(d4),r(f4)), black(k(a8),q(g4)), Colour.WHITE);
            assertHasMoves(position, "Rxg4 Re4");
            assertDoesNotHaveMoves(position, "Rf3 Rf5 Rh4");

            position = position(white(k(d4),r(d6)), black(k(a8),q(d8)), Colour.WHITE);
            assertHasMoves(position, "Rxd8 Rd7 Rd5");
            assertDoesNotHaveMoves(position, "Rc6 Re6");

            position = position(white(k(d6),r(d4)), black(k(a8),q(d2)), Colour.WHITE);
            assertHasMoves(position, "Rxd2 Rd3 Rd5");
            assertDoesNotHaveMoves(position, "Rc4 Re4 Rd1");

            /* White rook pins black rook. */
            position = position(white(k(a8),r(a5)), black(k(e5),r(c5)), Colour.BLACK);
            assertHasMoves(position, "Rxa5 Rb5 Rd5");
            assertDoesNotHaveMoves(position, "Rc4 Rc6");

            position = position(white(k(a8),r(g4)), black(k(d4),r(f4)), Colour.BLACK);
            assertHasMoves(position, "Rxg4 Re4");
            assertDoesNotHaveMoves(position, "Rf3 Rf5 Rh4");

            position = position(white(k(a8),r(d8)), black(k(d4),r(d6)), Colour.BLACK);
            assertHasMoves(position, "Rxd8 Rd7 Rd5");
            assertDoesNotHaveMoves(position, "Rc6 Re6");

            position = position(white(k(a8),r(d2)), black(k(d6),r(d4)), Colour.BLACK);
            assertHasMoves(position, "Rxd2 Rd3 Rd5");
            assertDoesNotHaveMoves(position, "Rc4 Re4 Rd1");

            /* White queen pins black rook. */
            position = position(white(k(a8),q(a5)), black(k(e5),r(c5)), Colour.BLACK);
            assertHasMoves(position, "Rxa5 Rb5 Rd5");
            assertDoesNotHaveMoves(position, "Rc4 Rc6");

            position = position(white(k(a8),q(g4)), black(k(d4),r(f4)), Colour.BLACK);
            assertHasMoves(position, "Rxg4 Re4");
            assertDoesNotHaveMoves(position, "Rf3 Rf5 Rh4");

            position = position(white(k(a8),q(d8)), black(k(d4),r(d6)), Colour.BLACK);
            assertHasMoves(position, "Rxd8 Rd7 Rd5");
            assertDoesNotHaveMoves(position, "Rc6 Re6");

            position = position(white(k(a8),q(d2)), black(k(d6),r(d4)), Colour.BLACK);
            assertHasMoves(position, "Rxd2 Rd3 Rd5");
            assertDoesNotHaveMoves(position, "Rc4 Re4 Rd1");
        }

        @Test
        public void testPinnedBishops() {
            /* Partially pin by bishop/queen. */
            /* Black bishop pins white bishop. */
            Position position = position(white(k(a1),b(d4)), black(k(a8),b(g7)), Colour.WHITE);
            assertHasMoves(position,"Bxg7 Bf6 Be5 Bc3 Bb2");
            assertDoesNotHaveMoves(position, "Bc5 Be3 Bh8");

            position.removePiece(g7.placeValue);
            assertHasMoves(position, "Bc5 Be3 Bh8");

            position = position(white(k(a8),b(d5)), black(k(h8),b(g2)), Colour.WHITE);
            assertHasMoves(position,"Bxg2 Bf3 Be4 Bc6 Bb7");
            assertDoesNotHaveMoves(position, "Bc4 Be6 Bh1");

            position.removePiece(g2.placeValue);
            assertHasMoves(position, "Bc4 Be6 Bh1");

            position = position(white(k(h8),b(f6)), black(k(a8),b(d4)), Colour.WHITE);
            assertHasMoves(position,"Bxd4 Be5 Bg7");
            assertDoesNotHaveMoves(position, "Be7 Bg5 Ba1 Bb2 Bc3");

            position.removePiece(d4.placeValue);
            assertHasMoves(position, "Be7 Bg5 Ba1 Bb2 Bc3");

            position = position(white(k(h1),b(e4)), black(k(h8),b(b7)), Colour.WHITE);
            assertHasMoves(position,"Bxb7 Bc6 Bd5 Bf3 Bg2");
            assertDoesNotHaveMoves(position, "Bd3 Bf5 Ba8");

            position.removePiece(b7.placeValue);
            assertHasMoves(position, "Bd3 Bf5 Ba8");

            /* Black queen pins white bishop. */
            position = position(white(k(a1),b(d4)), black(k(a8),q(g7)), Colour.WHITE);
            assertHasMoves(position,"Bxg7 Bf6 Be5 Bc3 Bb2");
            assertDoesNotHaveMoves(position, "Bc5 Be3 Bh8");

            position = position(white(k(a8),b(d5)), black(k(h8),q(g2)), Colour.WHITE);
            assertHasMoves(position,"Bxg2 Bf3 Be4 Bc6 Bb7");
            assertDoesNotHaveMoves(position, "Bc4 Be6 Bh1");

            position = position(white(k(h8),b(f6)), black(k(a8),q(d4)), Colour.WHITE);
            assertHasMoves(position,"Bxd4 Be5 Bg7");
            assertDoesNotHaveMoves(position, "Be7 Bg5 Ba1 Bb2 Bc3");

            position = position(white(k(h1),b(e4)), black(k(h8),q(b7)), Colour.WHITE);
            assertHasMoves(position,"Bxb7 Bc6 Bd5 Bf3 Bg2");
            assertDoesNotHaveMoves(position, "Bd3 Bf5 Ba8");

            /* White bishop pins black bishop. */
            position = position(white(k(a8),b(g7)), black(k(a1),b(d4)), Colour.BLACK);
            assertHasMoves(position,"Bxg7 Bf6 Be5 Bc3 Bb2");
            assertDoesNotHaveMoves(position, "Bc5 Be3 Bh8");

            position.removePiece(g7.placeValue);
            assertHasMoves(position, "Bc5 Be3 Bh8");

            position = position(white(k(h8),b(g2)), black(k(a8),b(d5)), Colour.BLACK);
            assertHasMoves(position,"Bxg2 Bf3 Be4 Bc6 Bb7");
            assertDoesNotHaveMoves(position, "Bc4 Be6 Bh1");

            position.removePiece(g2.placeValue);
            assertHasMoves(position, "Bc4 Be6 Bh1");

            position = position(white(k(a8),b(d4)), black(k(h8),b(f6)), Colour.BLACK);
            assertHasMoves(position,"Bxd4 Be5 Bg7");
            assertDoesNotHaveMoves(position, "Be7 Bg5 Ba1 Bb2 Bc3");

            position.removePiece(d4.placeValue);
            assertHasMoves(position, "Be7 Bg5 Ba1 Bb2 Bc3");

            position = position(white(k(h8),b(b7)), black(k(h1),b(e4)), Colour.BLACK);
            assertHasMoves(position,"Bxb7 Bc6 Bd5 Bf3 Bg2");
            assertDoesNotHaveMoves(position, "Bd3 Bf5 Ba8");

            position.removePiece(b7.placeValue);
            assertHasMoves(position, "Bd3 Bf5 Ba8");

            /* White queen pins black bishop. */
            position = position(white(k(a8),q(g7)), black(k(a1),b(d4)), Colour.BLACK);
            assertHasMoves(position,"Bxg7 Bf6 Be5 Bc3 Bb2");
            assertDoesNotHaveMoves(position, "Bc5 Be3 Bh8");

            position = position(white(k(h8),q(g2)), black(k(a8),b(d5)), Colour.BLACK);
            assertHasMoves(position,"Bxg2 Bf3 Be4 Bc6 Bb7");
            assertDoesNotHaveMoves(position, "Bc4 Be6 Bh1");

            position = position(white(k(a8),q(d4)), black(k(h8),b(f6)), Colour.BLACK);
            assertHasMoves(position,"Bxd4 Be5 Bg7");
            assertDoesNotHaveMoves(position, "Be7 Bg5 Ba1 Bb2 Bc3");

            position = position(white(k(h8),q(b7)), black(k(h1),b(e4)), Colour.BLACK);
            assertHasMoves(position,"Bxb7 Bc6 Bd5 Bf3 Bg2");
            assertDoesNotHaveMoves(position, "Bd3 Bf5 Ba8");

            /* completely pin by rook/queen. */
            /* Black rook pins white bishop. */
            position = position(white(k(d4),b(b4)), black(k(a8),r(a4)), Colour.WHITE);
            assertDoesNotHaveMoves(position, "Ba3 Ba5 Bc3 Bc5");

            position = position(white(k(d4),b(d6)), black(k(a8),r(d8)), Colour.WHITE);
            assertDoesNotHaveMoves(position, "Bc5 Be5 Bc7 Be7");

            position = position(white(k(d4),b(e4)), black(k(a8),r(g4)), Colour.WHITE);
            assertDoesNotHaveMoves(position, "Bd3 Bd5 Bf3 Bf5");

            position = position(white(k(d4),b(d3)), black(k(a8),r(d1)), Colour.WHITE);
            assertDoesNotHaveMoves(position, "Bc2 Be2 Bc4 Be4");

            /* Black queen pins white bishop. */
            position = position(white(k(d4),b(b4)), black(k(a8),q(a4)), Colour.WHITE);
            assertDoesNotHaveMoves(position, "Ba3 Ba5 Bc3 Bc5");

            position = position(white(k(d4),b(d6)), black(k(a8),q(d8)), Colour.WHITE);
            assertDoesNotHaveMoves(position, "Bc5 Be5 Bc7 Be7");

            position = position(white(k(d4),b(e4)), black(k(a8),q(g4)), Colour.WHITE);
            assertDoesNotHaveMoves(position, "Bd3 Bd5 Bf3 Bf5");

            position = position(white(k(d4),b(d3)), black(k(a8),q(d1)), Colour.WHITE);
            assertDoesNotHaveMoves(position, "Bc2 Be2 Bc4 Be4");

            /* White rook pins black bishop. */
            position = position(white(k(a8),r(a4)), black(k(d4),b(b4)), Colour.BLACK);
            assertDoesNotHaveMoves(position, "Ba3 Ba5 Bc3 Bc5");

            position = position(white(k(a8),r(d8)), black(k(d4),b(d6)), Colour.BLACK);
            assertDoesNotHaveMoves(position, "Bc5 Be5 Bc7 Be7");

            position = position(white(k(a8),r(g4)), black(k(d4),b(e4)), Colour.BLACK);
            assertDoesNotHaveMoves(position, "Bd3 Bd5 Bf3 Bf5");

            position = position(white(k(a8),r(d1)), black(k(d4),b(d3)), Colour.BLACK);
            assertDoesNotHaveMoves(position, "Bc2 Be2 Bc4 Be4");

            /* White queen pins black bishop. */
            position = position(white(k(a8),q(a4)), black(k(d4),b(b4)), Colour.BLACK);
            assertDoesNotHaveMoves(position, "Ba3 Ba5 Bc3 Bc5");

            position = position(white(k(a8),q(d8)), black(k(d4),b(d6)), Colour.BLACK);
            assertDoesNotHaveMoves(position, "Bc5 Be5 Bc7 Be7");

            position = position(white(k(a8),q(g4)), black(k(d4),b(e4)), Colour.BLACK);
            assertDoesNotHaveMoves(position, "Bd3 Bd5 Bf3 Bf5");

            position = position(white(k(a8),q(d1)), black(k(d4),b(d3)), Colour.BLACK);
            assertDoesNotHaveMoves(position, "Bc2 Be2 Bc4 Be4");
        }

        @Test
        public void testPinnedQueens() {
            /* Partial pin by bishop/queen. */
            /* Black bishop pins white Queen. */
            Position position = position(white(k(a1),q(d4)), black(k(a8),b(g7)), Colour.WHITE);
            assertHasMoves(position,"Qxg7 Qf6 Qe5 Qc3 Qb2");
            assertDoesNotHaveMoves(position, "Qc4 Qc5 Qd5 Qe4 Qe3 Qd3 Qh8");

            position = position(white(k(a8),q(d5)), black(k(h8),b(g2)), Colour.WHITE);
            assertHasMoves(position,"Qxg2 Qf3 Qe4 Qc6 Qb7");
            assertDoesNotHaveMoves(position, "Qd4 Qc4 Qc5 Qd6 Qe6 Qe5 Qh1");

            position = position(white(k(h8),q(f6)), black(k(a8),b(d4)), Colour.WHITE);
            assertHasMoves(position,"Qxd4 Qe5 Qg7");
            assertDoesNotHaveMoves(position, "Qe6 Qe7 Qf7 Qg6 Qg5 Qa1 Qb2 Qc3");

            position = position(white(k(h1),q(e4)), black(k(h8),b(b7)), Colour.WHITE);
            assertHasMoves(position,"Qxb7 Qc6 Qd5 Qf3 Qg2");
            assertDoesNotHaveMoves(position, "Qe3 Qd3 Qd4 Qe5 Qf5 Qf4 Qa8");

            /* Black queen pins white queen. */
            position = position(white(k(a1),q(d4)), black(k(a8),q(g7)), Colour.WHITE);
            assertHasMoves(position,"Qxg7 Qf6 Qe5 Qc3 Qb2");
            assertDoesNotHaveMoves(position, "Qc4 Qc5 Qd5 Qe4 Qe3 Qd3 Qh8");

            position = position(white(k(a8),q(d5)), black(k(h8),q(g2)), Colour.WHITE);
            assertHasMoves(position,"Qxg2 Qf3 Qe4 Qc6 Qb7");
            assertDoesNotHaveMoves(position, "Qd4 Qc4 Qc5 Qd6 Qe6 Qe5 Qh1");

            position = position(white(k(h8),q(f6)), black(k(a8),q(d4)), Colour.WHITE);
            assertHasMoves(position,"Qxd4 Qe5 Qg7");
            assertDoesNotHaveMoves(position, "Qe6 Qe7 Qf7 Qg6 Qg5 Qa1 Qb2 Qc3");

            position = position(white(k(h1),q(e4)), black(k(h8),q(b7)), Colour.WHITE);
            assertHasMoves(position,"Qxb7 Qc6 Qd5 Qf3 Qg2");
            assertDoesNotHaveMoves(position, "Qe3 Qd3 Qd4 Qe5 Qf5 Qf4 Qa8");

            /* White bishop pins black queen. */
            position = position(white(k(a8),b(g7)), black(k(a1),q(d4)), Colour.BLACK);
            assertHasMoves(position,"Qxg7 Qf6 Qe5 Qc3 Qb2");
            assertDoesNotHaveMoves(position, "Qc4 Qc5 Qd5 Qe4 Qe3 Qd3 Qh8");

            position = position(white(k(h8),b(g2)), black(k(a8),q(d5)), Colour.BLACK);
            assertHasMoves(position,"Qxg2 Qf3 Qe4 Qc6 Qb7");
            assertDoesNotHaveMoves(position, "Qd4 Qc4 Qc5 Qd6 Qe6 Qe5 Qh1");

            position = position(white(k(a8),b(d4)), black(k(h8),q(f6)), Colour.BLACK);
            assertHasMoves(position,"Qxd4 Qe5 Qg7");
            assertDoesNotHaveMoves(position, "Qe6 Qe7 Qf7 Qg6 Qg5 Qa1 Qb2 Qc3");

            position = position(white(k(h8),b(b7)), black(k(h1),q(e4)), Colour.BLACK);
            assertHasMoves(position,"Qxb7 Qc6 Qd5 Qf3 Qg2");
            assertDoesNotHaveMoves(position, "Qe3 Qd3 Qd4 Qe5 Qf5 Qf4 Qa8");

            /* White queen pins black bishop. */
            position = position(white(k(a8),q(g7)), black(k(a1),q(d4)), Colour.BLACK);
            assertHasMoves(position,"Qxg7 Qf6 Qe5 Qc3 Qb2");
            assertDoesNotHaveMoves(position, "Qc4 Qc5 Qd5 Qe4 Qe3 Qd3 Qh8");

            position = position(white(k(h8),q(g2)), black(k(a8),q(d5)), Colour.BLACK);
            assertHasMoves(position,"Qxg2 Qf3 Qe4 Qc6 Qb7");
            assertDoesNotHaveMoves(position, "Qd4 Qc4 Qc5 Qd6 Qe6 Qe5 Qh1");

            position = position(white(k(a8),q(d4)), black(k(h8),q(f6)), Colour.BLACK);
            assertHasMoves(position,"Qxd4 Qe5 Qg7");
            assertDoesNotHaveMoves(position, "Qe6 Qe7 Qf7 Qg6 Qg5 Qa1 Qb2 Qc3");

            position = position(white(k(h8),q(b7)), black(k(h1),q(e4)), Colour.BLACK);
            assertHasMoves(position,"Qxb7 Qc6 Qd5 Qf3 Qg2");
            assertDoesNotHaveMoves(position, "Qe3 Qd3 Qd4 Qe5 Qf5 Qf4 Qa8");

            /* Partial pin by rook/queen. */
            /* Black rook pins white queen. */
            position = position(white(k(e5),q(c5)), black(k(a8),r(a5)), Colour.WHITE);
            assertHasMoves(position, "Qxa5 Qb5 Qd5");
            assertDoesNotHaveMoves(position, "Qb4 Qb6 Qc4 Qc6 Qd4 Qd6");

            position = position(white(k(d4),q(f4)), black(k(a8),r(g4)), Colour.WHITE);
            assertHasMoves(position, "Qxg4 Qe4");
            assertDoesNotHaveMoves(position, "Qg3 Qg5 Qf3 Qf5 Qe3 Qe5 Qh4");

            position = position(white(k(d4),q(d6)), black(k(a8),r(d8)), Colour.WHITE);
            assertHasMoves(position, "Qxd8 Qd7 Qd5");
            assertDoesNotHaveMoves(position, "Qc7 Qe7 Qc6 Qe6 Qc5 Qe5");

            position = position(white(k(d6),q(d4)), black(k(a8),r(d2)), Colour.WHITE);
            assertHasMoves(position, "Qxd2 Qd3 Qd5");
            assertDoesNotHaveMoves(position, "Qc3 Qe3 Qc4 Qe4 Qc5 Qe5 Qd1");

            /* Black queen pins white queen. */
            position = position(white(k(e5),q(c5)), black(k(a8),q(a5)), Colour.WHITE);
            assertHasMoves(position, "Qxa5 Qb5 Qd5");
            assertDoesNotHaveMoves(position, "Qb4 Qb6 Qc4 Qc6 Qd4 Qd6");

            position = position(white(k(d4),q(f4)), black(k(a8),q(g4)), Colour.WHITE);
            assertHasMoves(position, "Qxg4 Qe4");
            assertDoesNotHaveMoves(position, "Qe3 Qe5 Qf3 Qf5 Qg3 Qg5 Qh4");

            position = position(white(k(d4),q(d6)), black(k(a8),q(d8)), Colour.WHITE);
            assertHasMoves(position, "Qxd8 Qd7 Qd5");
            assertDoesNotHaveMoves(position, "Qc5 Qe5 Qc6 Qe6 Qc7 Qe7");

            position = position(white(k(d6),q(d4)), black(k(a8),q(d2)), Colour.WHITE);
            assertHasMoves(position, "Qxd2 Qd3 Qd5");
            assertDoesNotHaveMoves(position, "Qc3 Qe3 Qc4 Qe4 Qc5 Qe5 Qd1");

            /* White rook pins black queen. */
            position = position(white(k(a8),r(a5)), black(k(e5),q(c5)), Colour.BLACK);
            assertHasMoves(position, "Qxa5 Qb5 Qd5");
            assertDoesNotHaveMoves(position, "Qb4 Qb6 Qc4 Qc6 Qd4 Qd6");

            position = position(white(k(a8),r(g4)), black(k(d4),q(f4)), Colour.BLACK);
            assertHasMoves(position, "Qxg4 Qe4");
            assertDoesNotHaveMoves(position, "Qe3 Qe5 Qf3 Qf5 Qh4 Qg3 Qg5");

            position = position(white(k(a8),r(d8)), black(k(d4),q(d6)), Colour.BLACK);
            assertHasMoves(position, "Qxd8 Qd7 Qd5");
            assertDoesNotHaveMoves(position, "Qc5 Qe5 Qc6 Qe6 Qc7 Qe7");

            position = position(white(k(a8),r(d2)), black(k(d6),q(d4)), Colour.BLACK);
            assertHasMoves(position, "Qxd2 Qd3 Qd5");
            assertDoesNotHaveMoves(position, "Qc3 Qe3 Qc4 Qe4 Qc5 Qe5 Qd1");

            /* White queen pins black queen. */
            position = position(white(k(a8),q(a5)), black(k(e5),q(c5)), Colour.BLACK);
            assertHasMoves(position, "Qxa5 Qb5 Qd5");
            assertDoesNotHaveMoves(position, "Qb4 Qb6 Qc4 Qc6 Qd4 Qd6");

            position = position(white(k(a8),q(g4)), black(k(d4),q(f4)), Colour.BLACK);
            assertHasMoves(position, "Qxg4 Qe4");
            assertDoesNotHaveMoves(position, "Qe3 Qe5 Qf3 Qf5 Qg3 Qg5 Qh4");

            position = position(white(k(a8),q(d8)), black(k(d4),q(d6)), Colour.BLACK);
            assertHasMoves(position, "Qxd8 Qd7 Qd5");
            assertDoesNotHaveMoves(position, "Qc5 Qe5 Qc6 Qe6 Qc7 Qe7");

            position = position(white(k(a8),q(d2)), black(k(d6),q(d4)), Colour.BLACK);
            assertHasMoves(position, "Qxd2 Qd3 Qd5");
            assertDoesNotHaveMoves(position, "Qc3 Qe3 Qc4 Qe4 Qc5 Qe5 Qd1");
        }

        @Test
        public void testMisc() {
            /* Some of these positions may not be legal. */
            Position position = position(
                    white(k(d5),p(b3,f3),n(b5,d3,f5),b(d7),r(b7,f7)),
                    black(k(e7),b(a8,g1),r(d2,d8),q(a2,a5,c1,e1,g2,g5,g8)),
                    Colour.WHITE);
            assertNoLegalMoves(position);

            position = position(
                    white(k(e2),b(a1,g8),r(d7,d1),q(a7,a4,c8,e8,g7,g4,g1)),
                    black(k(d4),p(b6,f6),n(b4,d6,f4),b(d2),r(b2,f2)),
                    Colour.BLACK);
            assertNoLegalMoves(position);

            position = position(
                    white(k(e4),p(a2,b5,c2,d6,f6,g2),n(c4,g4),b(e2,e5),r(b7,f5)),
                    black(k(c5),p(a3,b6,d7,f7),n(c1,g1),b(a8,h6),r(b4,e8),q(b1,e1,h1,h4,h7)),
                    Colour.WHITE);
            assertNoLegalMoves(position);

            position = position(
                    white(k(c4),p(a6,b3,d2,f2),n(c8,g8),b(a1,h3),r(b5,e1),q(b8,e8,h8,h5,h2)),
                    black(k(e5),p(a7,b4,c7,d3,f3,g7),n(c5,g5),b(e7,e4),r(b2,f4)),
                    Colour.BLACK);
            assertNoLegalMoves(position);
        }
    }
}
