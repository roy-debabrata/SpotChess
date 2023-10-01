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
            var move1 = moveAndGetReverseMove(position, "d4");
            assert ! position.equals(positionCopy);

            int flag2 = position.getFlags();
            var move2 = moveAndGetReverseMove(position, "e5");
            assert ! position.equals(positionCopy);

            int flag3 = position.getFlags();
            var move3 = moveAndGetReverseMove(position, "dxe5");
            assert ! position.equals(positionCopy);

            int flag4 = position.getFlags();
            var move4 = moveAndGetReverseMove(position, "d5");
            assert ! position.equals(positionCopy);

            int flag5 = position.getFlags();
            var move5 = moveAndGetReverseMove(position, "exd6");
            assert ! position.equals(positionCopy);

            int flag6 = position.getFlags();
            var move6 = moveAndGetReverseMove(position, "a6");
            assert ! position.equals(positionCopy);

            int flag7 = position.getFlags();
            var move7 = moveAndGetReverseMove(position, "dxc7");
            assert ! position.equals(positionCopy);

            int flag8 = position.getFlags();
            var move8 = moveAndGetReverseMove(position, "a5");
            assert ! position.equals(positionCopy);

            int flag9 = position.getFlags();
            var move9 = moveAndGetReverseMove(position, "cxb8=Q");
            assert ! position.equals(positionCopy);

            position.unmakeMove(move9.t, move9.u, flag9);
            position.unmakeMove(move8.t, move8.u, flag8);

            // Branching out and reverting again.
            flag8 = position.getFlags();
            move8 = moveAndGetReverseMove(position, "Bf5");
            assert ! position.equals(positionCopy);

            flag9 = position.getFlags();
            move9 = moveAndGetReverseMove(position, "c8=Q");
            assert ! position.equals(positionCopy);

            position.unmakeMove(move9.t, move9.u, flag9);
            position.unmakeMove(move8.t, move8.u, flag8);
            position.unmakeMove(move7.t, move7.u, flag7);
            position.unmakeMove(move6.t, move6.u, flag6);
            position.unmakeMove(move5.t, move5.u, flag5);
            position.unmakeMove(move4.t, move4.u, flag4);
            position.unmakeMove(move3.t, move3.u, flag3);
            position.unmakeMove(move2.t, move2.u, flag2);
            position.unmakeMove(move1.t, move1.u, flag1);

            assertEquals(position,positionCopy);
        }
    }
}
