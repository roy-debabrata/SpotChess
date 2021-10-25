package com.debabrata.spotchess.support.notation.game;

import com.debabrata.spotchess.exception.InvalidPositionException;
import com.debabrata.spotchess.types.Game;
import com.debabrata.spotchess.types.Position;
import com.debabrata.spotchess.types.Square;
import com.debabrata.spotchess.types.enums.Colour;
import com.debabrata.spotchess.types.enums.PieceType;

/** Parses and generates Forsyth–Edwards Notation game file format. */
public class FENParser implements GameParser {
    @Override
    public Game getGame(String gameContent) {
        if (!confirmFormat(gameContent)) {
            return null;
        }
        gameContent = adjustSpaces(gameContent);

        Position.Builder positionBuilder = new Position.Builder();
        int readPosition = 0;
        int skip = 0;
        for(int i = 0; i < 64; i++) {
            if (skip > 0) {
                skip--;
                continue;
            }
            char read = gameContent.charAt(readPosition++);
            if(i > 0 & (i % 8) == 0) {
                if(read != '/') {
                    throw new RuntimeException("Something is wrong with the  FEN format validator 2!");
                }
                read = gameContent.charAt(readPosition++);
            }
            if(Character.isDigit(read)) {
                skip = read - '1'; /* Because we are already skipping '1' position due to this char. */
            } else if (Character.isLetter(read)) {
                Colour colour = Character.isUpperCase(read) ? Colour.WHITE : Colour.BLACK;
                PieceType piece = PieceType.getPiece(read);
                positionBuilder.withPiece(colour,piece,new Square(63 - i));
            } else {
                throw new RuntimeException("Something is wrong with the  FEN format validator 2!");
            }
        }

        /* We expect to have the following information next: <space>side to move<space> */
        readPosition++;
        char sideToMove = gameContent.charAt(readPosition++);
        if (Character.toLowerCase(sideToMove) == 'b') {
            positionBuilder.toMove(Colour.BLACK);
        }

        /* We expect the castling flags next. */
        readPosition++;
        boolean whiteCanCastleLeft = false;
        boolean whiteCanCastleRight = false;
        boolean blackCanCastleLeft = false;
        boolean blackCanCastleRight = false;
        char castleFlag;
        do {
            castleFlag = gameContent.charAt(readPosition++);
            if(castleFlag == '-') {
                readPosition++;
                break;
            }
            switch (castleFlag) {
                case 'K' :
                    whiteCanCastleRight = true;
                    break;
                case 'Q' :
                    whiteCanCastleLeft = true;
                    break;
                case 'k' :
                    blackCanCastleRight = true;
                    break;
                case 'q' :
                    blackCanCastleLeft = true;
                    break;
            }
        } while (castleFlag != ' ');
        if (! whiteCanCastleLeft) {
            positionBuilder.leftRookMoved(Colour.WHITE);
        }
        if (! whiteCanCastleRight) {
            positionBuilder.rightRookMoved(Colour.WHITE);
        }
        if (! blackCanCastleLeft) {
            positionBuilder.leftRookMoved(Colour.BLACK);
        }
        if (! blackCanCastleRight) {
            positionBuilder.rightRookMoved(Colour.BLACK);
        }
        /* We expect to have the following information next: <space>en-passant square<space> */
        char epFile = gameContent.charAt(readPosition++);
        if (epFile != '-') {
            char epRank = gameContent.charAt(readPosition++);
            Square enPassantSquare = new Square(String.valueOf(epFile) + epRank);
            positionBuilder.enPassantSquare(enPassantSquare);
        }
        readPosition ++;
        /* We next expect to have the following information next: half move count */
        int halfMoveCounter = 0;
        char halfMoveChar;
        do {
            halfMoveChar = gameContent.charAt(readPosition++);
            if (Character.isDigit(halfMoveChar)) {
                halfMoveCounter = halfMoveCounter * 10 + (halfMoveChar - '0');
            } else {
                break;
            }
        } while (readPosition < gameContent.length());
        positionBuilder.halfMovesCount(halfMoveCounter);
        if (!(readPosition < gameContent.length())) {
            throw new RuntimeException("Something is wrong with the  FEN format validator 3!");
        }

        /* We next expect to have the following information next: half move count */
        int moveCounter = 0;
        while (readPosition < gameContent.length()) {
            char moveCounterChar = gameContent.charAt(readPosition++);
            moveCounter = moveCounter * 10 + (moveCounterChar - '0');
        }
        Position position;
        try {
            position = positionBuilder.build();
        } catch (InvalidPositionException e) {
            return null;
        }
        return new Game(position, moveCounter);
    }

    @Override
    public String getNotation(Game game) {
        return null;
    }

    @Override
    public boolean confirmFormat(String gameContent) {
        gameContent = adjustSpaces(gameContent);

        /* Counting pieces. */
        return true;
    }

    private String adjustSpaces(String fen) {
        return fen.replaceAll("\\s", " ").trim();
    }
}
