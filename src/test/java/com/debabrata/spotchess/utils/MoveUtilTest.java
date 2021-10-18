package com.debabrata.spotchess.utils;

import com.debabrata.spotchess.console.PositionPrinter;
import com.debabrata.spotchess.types.Position;
import com.debabrata.spotchess.types.enums.Colour;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.debabrata.spotchess.support.SpotTestSupport.*;

public class MoveUtilTest {
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
    public void testNightsPreventingCastling(){
        /* White should not have castling rights. */
        Position position = position(white(k(e1),r(a1,h1)), black(k(e8),n(e3)), Colour.WHITE);
        List<Integer> moves = MoveUtil.getMovesInPosition(position);
        assertDoesNotHaveMoves(position, "O-O O-O-O", moves);

        position = position(white(k(e1),r(a1,h1)), black(k(e8),n(e2)), Colour.WHITE);
        moves = MoveUtil.getMovesInPosition(position);
        assertDoesNotHaveMoves(position, "O-O O-O-O", moves);

        /* Checking left castle. */
        position = position(white(k(e1),r(a1,h1)), black(k(e8),n(f2)), Colour.WHITE);
        moves = MoveUtil.getMovesInPosition(position);
        assertHasMoves(position, "O-O", moves);
        assertDoesNotHaveMoves(position, "O-O-O", moves);

        position = position(white(k(e1),r(a1,h1)), black(k(e8),n(d3)), Colour.WHITE);
        moves = MoveUtil.getMovesInPosition(position); /* This should break when we start checking for checks. */
        assertHasMoves(position, "O-O", moves);
        assertDoesNotHaveMoves(position, "O-O-O", moves);

        position = position(white(k(e1),r(a1,h1)), black(k(e8),n(c3)), Colour.WHITE);
        moves = MoveUtil.getMovesInPosition(position);
        assertHasMoves(position, "O-O", moves);
        assertDoesNotHaveMoves(position, "O-O-O", moves);

        position = position(white(k(e1),r(a1,h1)), black(k(e8),n(b3)), Colour.WHITE);
        moves = MoveUtil.getMovesInPosition(position);
        assertHasMoves(position, "O-O", moves);
        assertDoesNotHaveMoves(position, "O-O-O", moves);

        position = position(white(k(e1),r(a1,h1)), black(k(e8),n(b2)), Colour.WHITE);
        moves = MoveUtil.getMovesInPosition(position);
        assertHasMoves(position, "O-O", moves);
        assertDoesNotHaveMoves(position, "O-O-O", moves);

        position = position(white(k(e1),r(a1,h1)), black(k(e8),n(a2)), Colour.WHITE);
        moves = MoveUtil.getMovesInPosition(position);
        assertHasMoves(position, "O-O", moves);
        assertDoesNotHaveMoves(position, "O-O-O", moves);

        position = position(white(k(e1),r(a1,h1)), black(k(e8),n(a2,b2,b3,c3,d3,f2)), Colour.WHITE);
        moves = MoveUtil.getMovesInPosition(position);
        assertHasMoves(position, "O-O", moves); /* This should break when we start checking for checks. */
        assertDoesNotHaveMoves(position, "O-O-O", moves);

        /* Checking right castle. */
        position = position(white(k(e1),r(a1,h1)), black(k(e8),n(d2)), Colour.WHITE);
        moves = MoveUtil.getMovesInPosition(position);
        assertHasMoves(position, "O-O-O", moves);
        assertDoesNotHaveMoves(position, "O-O", moves);

        position = position(white(k(e1),r(a1,h1)), black(k(e8),n(f3)), Colour.WHITE);
        moves = MoveUtil.getMovesInPosition(position); /* This should break when we start checking for checks. */
        assertHasMoves(position, "O-O-O", moves);
        assertDoesNotHaveMoves(position, "O-O", moves);

        position = position(white(k(e1),r(a1,h1)), black(k(e8),n(g3)), Colour.WHITE);
        moves = MoveUtil.getMovesInPosition(position);
        assertHasMoves(position, "O-O-O", moves);
        assertDoesNotHaveMoves(position, "O-O", moves);

        position = position(white(k(e1),r(a1,h1)), black(k(e8),n(h3)), Colour.WHITE);
        moves = MoveUtil.getMovesInPosition(position);
        assertHasMoves(position, "O-O-O", moves);
        assertDoesNotHaveMoves(position, "O-O", moves);

        position = position(white(k(e1),r(a1,h1)), black(k(e8),n(h2)), Colour.WHITE);
        moves = MoveUtil.getMovesInPosition(position);
        assertHasMoves(position, "O-O-O", moves);
        assertDoesNotHaveMoves(position, "O-O", moves);

        position = position(white(k(e1),r(a1,h1)), black(k(e8),n(d2,f3,g3,h3,h2)), Colour.WHITE);
        moves = MoveUtil.getMovesInPosition(position);
        assertHasMoves(position, "O-O-O", moves); /* This should break when we start checking for checks. */
        assertDoesNotHaveMoves(position, "O-O", moves);

        /* Black should not have castling rights. */
        position = position(white(k(e1),n(e6)), black(k(e8),r(a8,h8)), Colour.BLACK);
        moves = MoveUtil.getMovesInPosition(position);
        assertDoesNotHaveMoves(position, "O-O O-O-O", moves);

        position = position(white(k(e1),n(e7)), black(k(e8),r(a8,h8)), Colour.BLACK);
        moves = MoveUtil.getMovesInPosition(position);
        assertDoesNotHaveMoves(position, "O-O O-O-O", moves);

        /* Checking left castle. */
        position = position(white(k(e1),n(f7)), black(k(e8),r(a8,h8)), Colour.BLACK);
        moves = MoveUtil.getMovesInPosition(position); /* This should break when we start checking for checks. */
        assertHasMoves(position, "O-O", moves);
        assertDoesNotHaveMoves(position, "O-O-O", moves);

        position = position(white(k(e1),n(d6)), black(k(e8),r(a8,h8)), Colour.BLACK);
        moves = MoveUtil.getMovesInPosition(position); /* This should break when we start checking for checks. */
        assertHasMoves(position, "O-O", moves);
        assertDoesNotHaveMoves(position, "O-O-O", moves);

        position = position(white(k(e1),n(c6)), black(k(e8),r(a8,h8)), Colour.BLACK);
        moves = MoveUtil.getMovesInPosition(position);
        assertHasMoves(position, "O-O", moves);
        assertDoesNotHaveMoves(position, "O-O-O", moves);

        position = position(white(k(e1),n(b6)), black(k(e8),r(a8,h8)), Colour.BLACK);
        moves = MoveUtil.getMovesInPosition(position);
        assertHasMoves(position, "O-O", moves);
        assertDoesNotHaveMoves(position, "O-O-O", moves);

        position = position(white(k(e1),n(b7)), black(k(e8),r(a8,h8)), Colour.BLACK);
        moves = MoveUtil.getMovesInPosition(position);
        assertHasMoves(position, "O-O", moves);
        assertDoesNotHaveMoves(position, "O-O-O", moves);

        position = position(white(k(e1),n(a7)), black(k(e8),r(a8,h8)), Colour.BLACK);
        moves = MoveUtil.getMovesInPosition(position);
        assertHasMoves(position, "O-O", moves);
        assertDoesNotHaveMoves(position, "O-O-O", moves);

        position = position(white(k(e1),n(a7,b7,b6,c6,d6,f7)), black(k(e8),r(a8,h8)), Colour.BLACK);
        moves = MoveUtil.getMovesInPosition(position);
        assertHasMoves(position, "O-O", moves); /* This should break when we start checking for checks. */
        assertDoesNotHaveMoves(position, "O-O-O", moves);

        /* Checking right castle. */
        position = position(white(k(e1),n(d7)), black(k(e8),r(a8,h8)), Colour.BLACK);
        moves = MoveUtil.getMovesInPosition(position);
        assertHasMoves(position, "O-O-O", moves);
        assertDoesNotHaveMoves(position, "O-O", moves);

        position = position(white(k(e1),n(f6)), black(k(e8),r(a8,h8)), Colour.BLACK);
        moves = MoveUtil.getMovesInPosition(position); /* This should break when we start checking for checks. */
        assertHasMoves(position, "O-O-O", moves);
        assertDoesNotHaveMoves(position, "O-O", moves);

        position = position(white(k(e1),n(g6)), black(k(e8),r(a8,h8)), Colour.BLACK);
        moves = MoveUtil.getMovesInPosition(position);
        assertHasMoves(position, "O-O-O", moves);
        assertDoesNotHaveMoves(position, "O-O", moves);

        position = position(white(k(e1),n(h6)), black(k(e8),r(a8,h8)), Colour.BLACK);
        moves = MoveUtil.getMovesInPosition(position);
        assertHasMoves(position, "O-O-O", moves);
        assertDoesNotHaveMoves(position, "O-O", moves);

        position = position(white(k(e1),n(h7)), black(k(e8),r(a8,h8)), Colour.BLACK);
        moves = MoveUtil.getMovesInPosition(position);
        assertHasMoves(position, "O-O-O", moves);
        assertDoesNotHaveMoves(position, "O-O", moves);

        position = position(white(k(e1),n(d7,f6,g6,h6,h7)), black(k(e8),r(a8,h8)), Colour.BLACK);
        moves = MoveUtil.getMovesInPosition(position);
        assertHasMoves(position, "O-O-O", moves); /* This should break when we start checking for checks. */
        assertDoesNotHaveMoves(position, "O-O", moves);

        /* Negative checks knight preventing castling. */
        position = position(white(k(e1),r(a1)),
                black(k(e8),n(except(a1,b1,c1,d1,e1,e8,a2,b2,b3,c3,d3,e2,e3,f2))
                ), Colour.WHITE);
        moves = MoveUtil.getMovesInPosition(position);
        assertHasMoves(position, "O-O-O", moves); /* This should break when we start checking for checks. */

        position = position(white(k(e1),r(h1)),
                black(k(e8),n(except(e1,f1,g1,h1,e8,d2,e2,e3,f3,g3,h3,h2))
                ), Colour.WHITE);
        moves = MoveUtil.getMovesInPosition(position);
        assertHasMoves(position, "O-O", moves); /* This should break when we start checking for checks. */

        position = position(white(k(e1),n(except(a8,b8,c8,d8,e8,e1,a7,b7,b6,c6,d6,e6,e7,f7))), black(k(e8),r(a8)), Colour.BLACK);
        moves = MoveUtil.getMovesInPosition(position);
        assertHasMoves(position, "O-O-O", moves); /* This should break when we start checking for checks. */

        position = position(white(k(e1),n(except(e8,f8,g8,h8,e1,d7,e7,e6,f6,g6,h6,h7))), black(k(e8),r(h8)), Colour.BLACK);
        moves = MoveUtil.getMovesInPosition(position);
        assertHasMoves(position, "O-O", moves); /* This should break when we start checking for checks. */
    }
}
