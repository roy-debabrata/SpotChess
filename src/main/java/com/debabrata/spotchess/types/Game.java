package com.debabrata.spotchess.types;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private final Position initialPosition;
    private final Position currentPosition;
    private final List<Move> moveHistory;

    public Position getInitialPosition() {
        return new Position(initialPosition);
    }

    public Position getCurrentPosition() {
        return new Position(currentPosition);
    }

    public List<Move> getMoveHistory() {
        return new ArrayList<>(moveHistory);
    }

    public int getMoveCount() {
        return 1 + moveHistory.size() / 2;
    }

    public void addMove(Move move) {
        moveHistory.add(move);
        currentPosition.makeMove(move.getMove());
    }

    public Position getPositionAfterMove(int moveNumber){
        if (moveNumber < 0 || moveNumber > moveHistory.size()) {
            return null;
        }
        Position position = new Position(initialPosition);
        if (moveNumber == 0) {
            return position;
        }
        int index = 0;
        while (index < moveNumber) {
            Move move = moveHistory.get(index++);
            position.makeMove(move.getMove());
        }
        return position;
    }

    public Game(Position position) {
        this.initialPosition = new Position(position);
        this.currentPosition = new Position(position);
        this.moveHistory = new ArrayList<>();
    }

    public Game(Position position, int unknownMovesCount) {
        this(position);
        /* We don't use "unknownMovesCount" right now. */
    }
}
