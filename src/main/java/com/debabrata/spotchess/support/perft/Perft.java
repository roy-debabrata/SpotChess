package com.debabrata.spotchess.support.perft;

import com.debabrata.spotchess.logic.MoveProcessor;
import com.debabrata.spotchess.types.Position;
import com.debabrata.spotchess.types.Square;
import com.debabrata.spotchess.utils.MoveInitUtil;

public class Perft {
    private static final long [] moveBuffer = new long[300 * 20];
    private static final MoveProcessor processor = new MoveProcessor(moveBuffer);
    private static final boolean ENHANCED_STATS = false;

    /* Enhanced statistics. */
    private static long captures;
    private static long enPassant;
    private static long castles;
    private static long promotions;
    private static long checks;
    private static long discoveryChecks;
    private static long doubleChecks;
    private static long checkmates;


    public static long perftRunner(Position position, int maxDepth, boolean printDivide) {
        /* I had not thought of a make-unmake move when I started writing this program, so we'll revisit this when we
         * have that feature. Right now we'll just use the good ol' create as many objects as you like strategy. */
        if (maxDepth <= 0) {
            return 1;
        }
        initialize();
        long startTime = System.currentTimeMillis();
        long result;
        if (ENHANCED_STATS) {
            result = enhancedPerft(position, 0, maxDepth, printDivide);
        } else {
            result = perft(position, 0, maxDepth, printDivide);
        }
        System.out.println(maxDepth + " : " + result + " : time=" + (System.currentTimeMillis() - startTime) + "\n");
        if (ENHANCED_STATS) {
            printAdditionStats();
        }
        return result;
    }

    private static long perft(Position position, int startWritingAt, int depth, boolean printDivide) {
        if (depth < 1) {
            return 1;
        }
        int newWritingPosition = processor.addMovesToBuffer(position, startWritingAt);
        if (depth == 1) {
            if (printDivide) {
                for (int i = startWritingAt; i < newWritingPosition; i++) {
                    printDivide(i, 1, position);
                }
            }
            return newWritingPosition - startWritingAt;
        }
        long result = 0;
        int flag = position.getFlags();
        for (int i = startWritingAt; i < newWritingPosition; i++) {
            int taken = position.makeMove(moveBuffer[i]);
            long newResults = perft(position, newWritingPosition, depth - 1, false);
            position.unmakeMove(moveBuffer[i], taken, flag);
            if (printDivide) {
                printDivide(i, newResults, position);
            }
            result = result + newResults;
        }
        return  result; /* We are only counting leaf nodes.*/
    }

    private static long enhancedPerft(Position position, int startWritingAt, int depth, boolean printDivide) {
        if (depth < 1) {
            return 1;
        }
        int newWritingPosition = processor.addMovesToBuffer(position, startWritingAt);
        if (depth == 1) {
            /* Move Stats. */
            int flag = position.getFlags();
            for (int i = startWritingAt; i < newWritingPosition; i++) {
                captures += position.getPieceType(MoveInitUtil.getTo(moveBuffer[i], position)) != null ? 1 : 0;
                if (MoveInitUtil.isSpecialMove(moveBuffer[i])) {
                    enPassant += MoveInitUtil.isEnPassant(moveBuffer[i]) ? 1 : 0;
                    castles += MoveInitUtil.isCastle(moveBuffer[i]) ? 1 : 0;
                    promotions += MoveInitUtil.isPromotion(moveBuffer[i]) ? 1 : 0;
                }

                int taken = position.makeMove(moveBuffer[i]);

                /* Finding check stats. */
                int checker = MoveProcessor.getPositionCheckers(position);
                if (checker != -2) {
                    checks++;
                    if (checker == -1) {
                        doubleChecks++;
                    } else {
                        if (MoveInitUtil.getTo(moveBuffer[i], position) != checker) {
                            discoveryChecks++;
                        }
                    }
                    if ( processor.addMovesToBuffer(position, newWritingPosition) == newWritingPosition ) {
                        checkmates++;
                    }
                }

                position.unmakeMove(moveBuffer[i], taken, flag);
            }
            if (printDivide) {
                for (int i = startWritingAt; i < newWritingPosition; i++) {
                    printDivide(i, 1, position);
                }
            }
            return newWritingPosition - startWritingAt;
        }
        long result = 0;
        int flag = position.getFlags();
        for (int i = startWritingAt; i < newWritingPosition; i++) {
            int taken = position.makeMove(moveBuffer[i]);
            long newResults = enhancedPerft(position, newWritingPosition, depth - 1, false);
            position.unmakeMove(moveBuffer[i], taken, flag);
            if (printDivide) {
                printDivide(i, newResults, position);
            }
            result = result + newResults;
        }
        return result; /* We are only counting leaf nodes.*/
    }

    private static void initialize() {
        captures = 0;
        enPassant = 0;
        castles = 0;
        promotions = 0;
        checks = 0;
        discoveryChecks = 0;
        doubleChecks = 0;
        checkmates = 0;
    }

    private static void printAdditionStats() {
        System.out.println("Captures          : " + (captures + enPassant));
        System.out.println("En Passant        : " + enPassant);
        System.out.println("Castles           : " + castles);
        System.out.println("Promotions        : " + promotions);
        System.out.println("Checks            : " + checks);
        System.out.println("Discovered Checks : " + discoveryChecks);
        System.out.println("Double checks     : " + doubleChecks);
        System.out.println("Check Mates       : " + checkmates);
        System.out.println("\n");
    }

    private static void printDivide(int i, long result, Position position) {
        Square squareFrom = new Square(MoveInitUtil.getFrom(moveBuffer[i], position));
        Square squareTo = new Square(MoveInitUtil.getTo(moveBuffer[i], position));
        if (MoveInitUtil.isSpecialMove(moveBuffer[i]) && MoveInitUtil.isPromotion(moveBuffer[i])) {
            char promotion = MoveInitUtil.promotesTo(moveBuffer[i]).name().toLowerCase().charAt(0);
            System.out.println(squareFrom.toString() + squareTo + promotion + ": " + result);
        } else {
            System.out.println(squareFrom.toString() + squareTo + ": " + result);
        }
    }
}
