package com.debabrata.spotchess.support;

import com.debabrata.spotchess.types.Position;
import com.debabrata.spotchess.types.enums.Colour;
import com.debabrata.spotchess.types.enums.GameType;
import org.junit.jupiter.api.Test;

import static com.debabrata.spotchess.support.SpotTestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

public class SpotTestSupportTest{
    @Test
    public void testPieceSetup() {
        Position position = position(
                white(p(a2, b2, c2, d2, e2, f2, g2, h2), q(d1), k(e1), r(a1, h1), n(b1, g1), b(c1, f1)),
                black(p(a7, b7, c7, d7, e7, f7, g7, h7), q(d8), k(e8), r(a8, h8), n(b8, g8), b(c8, f8)),
                Colour.WHITE);
        Position standardPosition = new Position(GameType.STANDARD);
        assertEquals(standardPosition, position);
    }

    @Test
    public void testPieceRemove() {
        Position position = position(white(k(a1),p(d4)),black(k(a8)),Colour.WHITE);
        Position positionWithoutPawn = position(white(k(a1)),black(k(a8)),Colour.WHITE);

        position = position(position,withoutPieceAt(d4));
        assertEquals(positionWithoutPawn, position);
    }

    @Test
    public void testToMoveSetup() {
        Position position = position(white(k(e2)), black(k(e7)), Colour.WHITE);
        assertTrue(position.whiteToMove());

        position = position(white(k(e2)), black(k(e7)), Colour.BLACK);
        assertFalse(position.whiteToMove());
    }

    @Test
    public void testMove() {
        Position position = position(white(p(d5),k(a1)), black(p(e7),k(a3)), Colour.BLACK);
        assertFalse(move(position, "Bxd5"));

        assertTrue(move(position, "e5"));
        assertEquals(position(white(p(d5),k(a1)), black(p(e5),k(a3)), Colour.WHITE), position);

        assertTrue(move(position, "dxe6"));
        assertEquals(position(white(p(e6),k(a1)), black(k(a3)), Colour.BLACK), position);
    }

    @Test
    public void testCastlingFlagSetup() {
        Position position = position(white(k(e2)), black(k(e7)), Colour.WHITE);
        assertFalse(position.canPotentiallyCastle(true));
        assertFalse(position.canPotentiallyCastle(false));
        assertFalse(position.canPotentiallyCastleLeft(true));
        assertFalse(position.canPotentiallyCastleLeft(false));
        assertFalse(position.canPotentiallyCastleRight(true));
        assertFalse(position.canPotentiallyCastleRight(false));

        position = position(white(k(e1)), black(k(e8)), Colour.WHITE);
        assertFalse(position.canPotentiallyCastle(true));
        assertFalse(position.canPotentiallyCastle(false));
        assertFalse(position.canPotentiallyCastleLeft(true));
        assertFalse(position.canPotentiallyCastleLeft(false));
        assertFalse(position.canPotentiallyCastleRight(true));
        assertFalse(position.canPotentiallyCastleRight(false));

        position = position(white(k(e1), r(a1)), black(k(e8), r(a8)), Colour.WHITE);
        assertTrue(position.canPotentiallyCastle(true));
        assertTrue(position.canPotentiallyCastle(false));
        assertTrue(position.canPotentiallyCastleLeft(true));
        assertTrue(position.canPotentiallyCastleLeft(false));
        assertFalse(position.canPotentiallyCastleRight(true));
        assertFalse(position.canPotentiallyCastleRight(false));

        position = position(white(k(e1), r(h1)), black(k(e8), r(h8)), Colour.WHITE);
        assertTrue(position.canPotentiallyCastle(true));
        assertTrue(position.canPotentiallyCastle(false));
        assertFalse(position.canPotentiallyCastleLeft(true));
        assertFalse(position.canPotentiallyCastleLeft(false));
        assertTrue(position.canPotentiallyCastleRight(true));
        assertTrue(position.canPotentiallyCastleRight(false));

        position = position(white(k(e1), r(a1,h1)), black(k(e8), r(a8,h8)), Colour.WHITE);
        assertTrue(position.canPotentiallyCastle(true));
        assertTrue(position.canPotentiallyCastle(false));
        assertTrue(position.canPotentiallyCastleLeft(true));
        assertTrue(position.canPotentiallyCastleLeft(false));
        assertTrue(position.canPotentiallyCastleRight(true));
        assertTrue(position.canPotentiallyCastleRight(false));
    }

    @Test
    public void assertExceptions() {
        Position position = position(white(k(a1)),black(k(a3)),Colour.WHITE);
        Position samePosition = position(white(k(a1)),black(k(a3)),Colour.WHITE);
        Position differentPosition = position(white(k(a3)),black(k(a1)),Colour.WHITE);
        Position positionWithDifferentFlag = position(white(k(a1)),black(k(a3)),Colour.BLACK);
        positionWithDifferentFlag.incrementReversibleHalfMoveCount();

        assertDoesNotThrow(() -> assertEquals((Position) null, null));

        Throwable error = assertThrows(AssertionError.class, () -> assertEquals(position, null));
        assertEquals("Actual position is null, expected is not.", error.getMessage());

        error = assertThrows(AssertionError.class, () -> assertEquals(null, position));
        assertEquals("Expected position is null, actual is not.", error.getMessage());

        error = assertThrows(AssertionError.class, () -> assertEquals(position, differentPosition));
        assertEquals("Actual and expected have different piece positions.", error.getMessage());

        assertDoesNotThrow(() -> assertEquals(position, positionWithDifferentFlag));

        assertDoesNotThrow(() -> assertStrictlyEquals(position, samePosition));

        error = assertThrows(AssertionError.class, () -> assertStrictlyEquals(position, positionWithDifferentFlag));
        assertEquals("To move : WHITE vs To move : BLACK; Reversible Half-Move Count: 0 vs Reversible Half-Move Count: 1", error.getMessage());
    }
}
