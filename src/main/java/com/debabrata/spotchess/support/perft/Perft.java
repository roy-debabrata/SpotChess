package com.debabrata.spotchess.support.perft;

import com.debabrata.spotchess.types.Position;
import com.debabrata.spotchess.types.Square;
import com.debabrata.spotchess.utils.MoveInitUtil;
import com.debabrata.spotchess.utils.MoveUtil;

public class Perft {
    public static long perftRunner(Position position, int maxDepth, boolean printDivide) {
        /* I had not thought of a make-unmake move when I started writing this program, so we'll revisit this when we
         * have that feature. Right now we'll just use the good ol' create as many objects as you like strategy. */
        int[] moveBuffer = new int[300 * maxDepth];
        if (maxDepth <= 0) {
            return 1;
        }

        long startTime = System.currentTimeMillis();
        long result = perft(position, moveBuffer, 0, maxDepth, printDivide);

        System.out.println(maxDepth + " : " + result + " : time=" + (System.currentTimeMillis() - startTime) + "\n");
        return result;
    }

    private static long perft(Position position, int[] moveBuffer, int startWritingAt, int depth, boolean printDivide) {
        if (depth < 1) {
            return 1;
        }
        int newWritingPosition = MoveUtil.addMovesToBuffer(position,moveBuffer,startWritingAt);
        if (depth == 1) {
            return newWritingPosition - startWritingAt;
        }
        long result = 0;
        int flag = position.getFlags();
        for (int i = startWritingAt; i < newWritingPosition; i++) {
            int restoreMove = position.makeMove(moveBuffer[i]);
            long newResults = perft(position, moveBuffer, newWritingPosition, depth - 1, false);
            position.unmakeMove(restoreMove, flag);
            if (printDivide) {
                Square squareFrom = new Square(MoveInitUtil.getFrom(moveBuffer[i]));
                Square squareTo = new Square(MoveInitUtil.getTo(moveBuffer[i]));
                System.out.println(squareFrom.toString() + squareTo + ": " + newResults);
            }
            result = result + newResults;
        }
        return  result; /* We are only counting leaf nodes.*/
    }
}