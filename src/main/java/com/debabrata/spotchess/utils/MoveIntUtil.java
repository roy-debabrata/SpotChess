package com.debabrata.spotchess.utils;

/**
 * @version 2.0
 * @author Debabrata Roy
 * comment: Positions are basically represented as by how much does 0x1 need to be left shifted to be over the position
 *          of the piece on the board. Each move is two positions (a from and to) and a couple of flags stored in a
 *          single int value. This is so that we can avoid the overhead of creating objects for moves. May need to run
 *          tests. Breaks OOP but hey, this is a resource intensive standalone application, performance above everything.
 *          We'll test alternatives once we have progressed far enough in our development to test overall performance.
 *
 *          This util to create and understand the move stored in the int. The methods should be inlined by JIT compiler.
 *          The storage details of he fields are as follows:
 *
 *          0x000000FF Last 8 bits "from" position.
 *          0x0000FF00 Next 8 bits "to" position.
 *          0x00010000 Left castle.
 *          0x00020000 Right castle.
 *          0x00040000 En passant move.
 *          0x00080000 Double pawn move.
 *
 *          0 can stand as an uninitialized move as a move from h1 to h1 doesn't make any sense in chess. To understand
 *          the orientation of the board please read the comments in GameState.
 */
public class MoveIntUtil {
    public static int getFrom(int move) {
        return move & 0x000000FF;
    }

    public static int getTo(int move) {
        return (move >> 8) & 0x000000FF;
    }

    public static boolean isCastle(int move) {
        return (move & 0x00030000) != 0;
    }

    public static boolean isLeftCastle(int move) {
        return (move & 0x00010000) != 0;
    }

    public static boolean isRightCastle(int move) {
        return (move & 0x00020000) != 0;
    }

    public static boolean isEnPassant(int move) {
        return (move & 0x00040000) != 0;
    }

    public static boolean isDoublePawnMove(int move) {
        return (move & 0x00080000) != 0;
    }

    public static int newMove(int from, int to) {
        return (to << 8) | from;
    }

    public static int newLeftCastle(int from, int to) {
        return (to << 8) | from | 0x00010000;
    }

    public static int newRightCastle(int from, int to) {
        return (to << 8) | from | 0x00020000;
    }

    public static int newEnPassant(int from, int to) {
        return (to << 8) | from | 0x00040000;
    }

    public static int newPawnDoubleMove(int from, int to) {
        return (to << 8) | from | 0x00080000;
    }
}
