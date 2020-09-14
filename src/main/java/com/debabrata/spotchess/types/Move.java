package com.debabrata.spotchess.types;

/**
 * @version 2.0
 * @author Debabrata Roy
 * comment: Positions are basically represented as by how much does 0x1 need to be
 *          left shifted to be over the position of the piece on the board. Each variable basically stores the
 *          Least/Most Significant One Bit. The Least and the Most significant bits are the same of course.
 *          The vaules range from 0 to 63 to accomodate the 64 positions in a chess board. Basically the place value.
 *
 *          To understand the orientation of the board please read the comments in GameState
 */
public class Move {
    private int from;
    private int to;

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }
}
