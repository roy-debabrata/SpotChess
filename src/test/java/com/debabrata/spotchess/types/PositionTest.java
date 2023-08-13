package com.debabrata.spotchess.types;

import com.debabrata.spotchess.types.enums.GameType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.debabrata.spotchess.support.test.SpotTestSupport.*;

public class PositionTest {
    @Nested
    class UndoMoveTest {
        @Test
        public void takeBackPawnMoves() {
            Position position = new Position(GameType.STANDARD);
            Position positionCopy = new Position(position);
            assertEquals(position, positionCopy);

            int flag1 = position.getFlags();
            int move1 = moveAndGetReverseMove(position, "d4");
            assert ! position.equals(positionCopy);

            int flag2 = position.getFlags();
            int move2 = moveAndGetReverseMove(position, "e5");
            assert ! position.equals(positionCopy);

            int flag3 = position.getFlags();
            int move3 = moveAndGetReverseMove(position, "dxe5");
            assert ! position.equals(positionCopy);

            int flag4 = position.getFlags();
            int move4 = moveAndGetReverseMove(position, "d5");
            assert ! position.equals(positionCopy);

            int flag5 = position.getFlags();
            int move5 = moveAndGetReverseMove(position, "exd6");
            assert ! position.equals(positionCopy);

            int flag6 = position.getFlags();
            int move6 = moveAndGetReverseMove(position, "a6");
            assert ! position.equals(positionCopy);

            int flag7 = position.getFlags();
            int move7 = moveAndGetReverseMove(position, "dxc7");
            assert ! position.equals(positionCopy);

            int flag8 = position.getFlags();
            int move8 = moveAndGetReverseMove(position, "a5");
            assert ! position.equals(positionCopy);

            int flag9 = position.getFlags();
            int move9 = moveAndGetReverseMove(position, "cxb8=Q");
            assert ! position.equals(positionCopy);

            position.unmakeMove(move9, flag9);
            position.unmakeMove(move8, flag8);

            // Branching out and reverting again.
            flag8 = position.getFlags();
            move8 = moveAndGetReverseMove(position, "Bf5");
            assert ! position.equals(positionCopy);

            flag9 = position.getFlags();
            move9 = moveAndGetReverseMove(position, "c8=Q");
            assert ! position.equals(positionCopy);

            position.unmakeMove(move9, flag9);
            position.unmakeMove(move8, flag8);
            position.unmakeMove(move7, flag7);
            position.unmakeMove(move6, flag6);
            position.unmakeMove(move5, flag5);
            position.unmakeMove(move4, flag4);
            position.unmakeMove(move3, flag3);
            position.unmakeMove(move2, flag2);
            position.unmakeMove(move1, flag1);

            assertEquals(position,positionCopy);
        }
    }
}
