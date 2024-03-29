package com.debabrata.spotchess.console;

import com.debabrata.spotchess.settings.ConsoleDisplaySettings;
import com.debabrata.spotchess.types.Position;
import com.debabrata.spotchess.types.enums.Colour;
import com.debabrata.spotchess.types.enums.PieceType;

import java.util.ArrayList;
import java.util.List;

public class PositionPrinter {

    private static final String BUFFER_BETWEEN_BOARDS = "          ";

    public static void printPosition(Position position){
        if ( ConsoleDisplaySettings.showBitboards() ){
            printPositionBitBoards(position);
        }
        String board = getConsolePrintableBoard(
                position,
                ConsoleDisplaySettings.useUnicodeChessPieces(),
                ConsoleDisplaySettings.switchPieceIconColours()
        );
        List<String> additionalInfo = getAdditionalStateInfo(position);

        String [] lines = board.split("\\n");
        int select = additionalInfo.size() - lines.length;
        for ( String line : lines ){
            System.out.print( line
                    + BUFFER_BETWEEN_BOARDS
            );
            if ( ConsoleDisplaySettings.showAdditionalInfo() && select >= 0 ){
                System.out.print(additionalInfo.get(select));
            }
            select++;
            System.out.println();
        }
    }

    public static void printPositionBitBoards(Position position){
        System.out.println();
        System.out.println( "White Pieces   " + BUFFER_BETWEEN_BOARDS
                + "Pawns and Knights" + BUFFER_BETWEEN_BOARDS + "\b\b"
                + "Knights and Kings");
        printBitBoards(
                position.getWhitePieces(),
                position.getPawnsAndKnights(),
                position.getKnightsAndKings()
                );
        System.out.println("\n");
        System.out.println( "Black Pieces   " + BUFFER_BETWEEN_BOARDS
                + "Queens and Bishops" + BUFFER_BETWEEN_BOARDS + "\b\b\b"
                + "Rooks and Queens");
        printBitBoards(
                position.getBlackPieces(),
                position.getQueensAndBishops(),
                position.getRooksAndQueens()
        );
        System.out.println("\n");
    }

    public static List<String> getAdditionalStateInfo(Position position){
        List<String> stateInfo = new ArrayList<>();
        stateInfo.add("To move : " + (position.whiteToMove()? Colour.WHITE.name() : Colour.BLACK.name()));
        stateInfo.add("Reversible Half-Move Count: " + position.getReversibleHalfMoveCount());
        String castleInfo = position.canPotentiallyCastleLeft(true) ?
                (position.canPotentiallyCastleRight(true) ? "both sides." : "left side.")
                : (position.canPotentiallyCastleRight(true) ? "right side." : "neither side.");
        stateInfo.add("White can potentially castle " + castleInfo);
        castleInfo = position.canPotentiallyCastleLeft(false) ?
                (position.canPotentiallyCastleRight(false) ? "both sides." : "left side.")
                : (position.canPotentiallyCastleRight(false) ? "right side." : "neither side.");
        stateInfo.add("Black can potentially castle " + castleInfo);
        /* En-passant */
        int enPassantFlag = (0x0000FF00 & position.getFlags()) >> 8;
        stateInfo.add("En-passant takers: " + eightBinaryDigitsToString(enPassantFlag));
        enPassantFlag = (0x00FF0000 & position.getFlags()) >> 16;
        stateInfo.add("En-passant taken : " + eightBinaryDigitsToString(enPassantFlag));
        return stateInfo;
    }

    public static void printBoard(Position position) {
        System.out.println(getConsolePrintableBoard(position, true, true));
    }

    public static String getConsolePrintableBoard(Position position, boolean useUnicodeSymbols, boolean switchPieceColours){
        StringBuilder board = new StringBuilder();
        for ( int i = 63; i >= 0; i-- ){
            Colour colour = position.getPieceColour(i);
            PieceType type = position.getPieceType(i);

            getChessSymbol(colour, type, useUnicodeSymbols, switchPieceColours);
            board.append(getChessSymbol(colour, type, useUnicodeSymbols, switchPieceColours));
            board.append(" ");

            if ( i % 8 == 0 ){
                board.append("\n");
            }
        }
        return board.toString();
    }

    private static char getChessSymbol(Colour colour, PieceType piece, boolean useUnicodeSymbols, boolean switchPieceColours) {
        if ( colour == null && piece == null ) {
            return '.';
        }
        if (useUnicodeSymbols) {
            if ((colour == Colour.WHITE) ^ switchPieceColours) {
                switch (piece) {
                    case PAWN:
                        return '♙';
                    case KNIGHT:
                        return '♘';
                    case BISHOP:
                        return '♗';
                    case ROOK:
                        return '♖';
                    case QUEEN:
                        return '♕';
                    case KING:
                        return '♔';
                }
            } else {
                switch (piece) {
                    case PAWN:
                        return '♟';
                    case KNIGHT:
                        return '♞';
                    case BISHOP:
                        return '♝';
                    case ROOK:
                        return '♜';
                    case QUEEN:
                        return '♛';
                    case KING:
                        return '♚';
                }
            }
        } else {
            char charSymbol;
            if ( piece == PieceType.PAWN ){
                charSymbol = 'P';
            } else {
                charSymbol = piece.getNotation();
            }
            if ( colour == Colour.WHITE ){
                return Character.toUpperCase(charSymbol);
            } else {
                return Character.toLowerCase(charSymbol);
            }
        }
        return '\0';
    }

    public static void printBitBoards(long ... boards){
        long mask = 0xFF00000000000000L;
        long shift = 56;
        for ( int i = 0; i < 8; i++ ){
            for (long board : boards) {
                long eightDigitBinary = (board & mask) >>> shift;
                String formattedString = eightBinaryDigitsToString((int)eightDigitBinary);
                System.out.print(formattedString);
                System.out.print(BUFFER_BETWEEN_BOARDS);
            }
            System.out.println();
            mask = mask >>> 8;
            shift -= 8;
        }
    }

    public static String eightBinaryDigitsToString (int eightDigitBinary){
        return String.format("%8s", Integer.toBinaryString(eightDigitBinary))
                .replace(" ", "0")
                .replace("", " ")
                .trim();
    }
}