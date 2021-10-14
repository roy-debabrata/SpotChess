package com.debabrata.spotchess;

import com.debabrata.spotchess.types.Position;
import com.debabrata.spotchess.types.enums.Colour;
import com.debabrata.spotchess.types.enums.GameType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;
import static com.debabrata.spotchess.support.SpotTestSupport.*;

@RunWith(JUnit4.class)
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
}
