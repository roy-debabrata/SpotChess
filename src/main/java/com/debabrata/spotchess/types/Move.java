package com.debabrata.spotchess.types;

import com.debabrata.spotchess.utils.MoveInitUtil;

/**
 * The actual move within the game is just a single int. Please refer to {@link MoveInitUtil} for understanding a move's
 * int representation. This Move is a wrapper class for the move which provides for some more meta-data that's not held
 * in the int itself.
 */
public class Move {
    private int move;
    private String comments;

    public int getMove() {
        return move;
    }

    public void setMove(int move) {
        this.move = move;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Move(int move, String comments) {
        this.move = move;
        this.comments = comments;
    }
}
