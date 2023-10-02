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

            position.unmakeMove(move9.move, move9.taken, flag9);
            position.unmakeMove(move8.move, move8.taken, flag8);

            // Branching out and reverting again.
            flag8 = position.getFlags();
            move8 = moveAndGetReverseMove(position, "Bf5");
            assert ! position.equals(positionCopy);

            flag9 = position.getFlags();
            move9 = moveAndGetReverseMove(position, "c8=Q");
            assert ! position.equals(positionCopy);

            position.unmakeMove(move9.move, move9.taken, flag9);
            position.unmakeMove(move8.move, move8.taken, flag8);
            position.unmakeMove(move7.move, move7.taken, flag7);
            position.unmakeMove(move6.move, move6.taken, flag6);
            position.unmakeMove(move5.move, move5.taken, flag5);
            position.unmakeMove(move4.move, move4.taken, flag4);
            position.unmakeMove(move3.move, move3.taken, flag3);
            position.unmakeMove(move2.move, move2.taken, flag2);
            position.unmakeMove(move1.move, move1.taken, flag1);

            assertEquals(position,positionCopy);
        }
    }
}
