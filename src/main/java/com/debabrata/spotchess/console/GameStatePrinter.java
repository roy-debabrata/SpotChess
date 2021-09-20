package com.debabrata.spotchess.console;

import com.debabrata.spotchess.types.GameState;
import com.debabrata.spotchess.types.Colour;
import com.debabrata.spotchess.types.PieceType;

import java.util.ArrayList;
import java.util.List;

public class GameStatePrinter {

    private static boolean SHOW_BITBOARDS = true;
    private static boolean SHOW_ADDITIONAL_INFO = true;
    private static boolean SWITCH_COLOURS = true;
    private static boolean USE_UNICODE_CHESS_PIECES = true;

    private static final String BUFFER_BETWEEN_BOARDS = "          ";

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

    public static void printGameStateBitBoards( GameState gameState ){
        System.out.println();
        System.out.println( "White Pieces   " + BUFFER_BETWEEN_BOARDS
                + "Pawns and Knights" + BUFFER_BETWEEN_BOARDS + "\b\b"
                + "Knights and Kings");
        printBitBoards(
                gameState.getWhitePieces(),
                gameState.getPawnsAndKnights(),
                gameState.getKnightsAndKings()
                );
        System.out.println("\n");
        System.out.println( "Black Pieces   " + BUFFER_BETWEEN_BOARDS
                + "Queens and Bishops" + BUFFER_BETWEEN_BOARDS + "\b\b\b"
                + "Rooks and Queens");
        printBitBoards(
                gameState.getBlackPieces(),
                gameState.getQueensAndBishops(),
                gameState.getRooksAndQueens()
        );
        System.out.println("\n");
    }

    public static List<String> getAdditionalBoardStateInfo(GameState gameState){
        List<String> stateInfo = new ArrayList<>();
        stateInfo.add("To move : " + gameState.moveOf().name());
        stateInfo.add("Reversible Half-Move Count: " + gameState.getReversibleHalfMoveCount());
        String castleInfo = gameState.canPotentiallyCastleLeft(Colour.WHITE) ?
                (gameState.canPotentiallyCastleRight(Colour.WHITE) ? "both sides." : "left side.")
                : (gameState.canPotentiallyCastleRight(Colour.WHITE) ? "right side." : "neither side.");
        stateInfo.add("White can potentially castle " + castleInfo);
        castleInfo = gameState.canPotentiallyCastleLeft(Colour.BLACK) ?
                (gameState.canPotentiallyCastleRight(Colour.BLACK) ? "both sides." : "left side.")
                : (gameState.canPotentiallyCastleRight(Colour.BLACK) ? "right side." : "neither side.");
        stateInfo.add("Black can potentially castle " + castleInfo);
        /* En-passant */
        int enPassantFlag = (0x0000FF00 & gameState.getGameState()) >> 8;
        stateInfo.add("En-passant takers: " + eightBinaryDigitsToString(enPassantFlag));
        enPassantFlag = (0x00FF0000 & gameState.getGameState()) >> 16;
        stateInfo.add("En-passant taken : " + eightBinaryDigitsToString(enPassantFlag));
        return stateInfo;
    }

    public static String getConsolePrintableBoard(GameState gameState){
        StringBuilder board = new StringBuilder();
        for ( int i = 63; i >= 0; i-- ){
            Colour colour = gameState.getPieceColour(i);
            PieceType type = gameState.getPieceType(i);

            getChessSymbol(colour, type);
            board.append(getChessSymbol(colour, type));
            board.append(" ");

            if ( i % 8 == 0 ){
                board.append("\n");
            }
        }
        return board.toString();
    }

    public static boolean bitboardDisplaySet(){ return SHOW_BITBOARDS; }

    public static void toggleBitboardDisplay(){
        SHOW_BITBOARDS = !SHOW_BITBOARDS;
    }

    public static boolean additionalInformationDisplaySet(){ return SHOW_ADDITIONAL_INFO; }

    public static void toggleAdditionalInformationDisplay(){
        SHOW_ADDITIONAL_INFO = !SHOW_ADDITIONAL_INFO;
    }

    public static void printGameState(GameState gameState){

        if ( SHOW_BITBOARDS ){
            printGameStateBitBoards(gameState);
        }
        String board = getConsolePrintableBoard(gameState);
        List<String> additionalInfo = getAdditionalBoardStateInfo(gameState);

        String [] lines = board.split("\\n");
        int select = additionalInfo.size() - lines.length;
        for ( String line : lines ){
            System.out.print( line
                    + BUFFER_BETWEEN_BOARDS
            );
            if ( SHOW_ADDITIONAL_INFO && select >= 0 ){
                System.out.print(additionalInfo.get(select));
            }
            select++;
            System.out.println();
        }
    }

    private static char getChessSymbol(Colour colour, PieceType piece ) {
        if ( colour == null && piece == null ) {
            return '.';
        }
        if (USE_UNICODE_CHESS_PIECES) {
            if ((colour == Colour.WHITE) ^ SWITCH_COLOURS) {
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
}