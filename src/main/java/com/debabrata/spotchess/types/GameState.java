package com.debabrata.spotchess.types;

/**
 * @version 2.0
 * @author Debabrata Roy
 * comment Storing Rooks, Queens and Bishops in two variable seems like a good idea. I am sure a lot of
 * engines must use this as it seems so basic. Not sure if its smart to jumble up the Knights, Kings and Pawns
 * but it did save 4 bytes which seems like a lot at this moment but I might regret it later.
 * That said, progress is change.
 */
public class GameState {
    /* Piece positions represent where the pieces are placed. */
    private long whitePieces;
    private long blackPieces;
    private long pawnsAndKnights;
    private long knightsAndKings;
    private long rooksAndQueens;
    private long queensAndBishops;

    /* Game state stores other state variables associated with the game. */
    private int gameState;

    /* Last 7 bits (mask 0x0000007F) are for storing the count of reversible half moves. Read up on 50 move rule.
     * Next 4 bits (mask 0x00000780) are used to store the file for the last en-passant move.
     *                            1000 being a file and 0001 being h file.
     * For the following positions 1 will represent can castle and 0 represents can't castle.
     * 0x00000800 = white can castle with left rook
     * 0x00001000 = white can castle with right rook
     * 0x00002000 = black can castle with left rook
     * 0x00004000 = black can castle with right rook
     * 0x00008000 = white to move. 0 at this position means it's white's turn, 1 means it's black's turn. */

    public GameState( boolean empty ){
        /* Standard board representation for normal standard game.
         * For a better understanding of how the board is represented check out
         * https://www.chessprogramming.org/Bibob and look at the image labeled "LERBEF"
         * We use this scheme because it feels more intuitive to me rather than for any performance reasons.*/

        if ( ! empty ) {
            whitePieces = 0x000000000000FFFFL;
            blackPieces = 0xFFFF000000000000L;
            pawnsAndKnights = 0x42FF00000000FF42L;
            knightsAndKings = 0x4A0000000000004AL;
            rooksAndQueens = 0x9100000000000091L;
            queensAndBishops = 0x3400000000000034L;
            /* All flags are set to one. */
            gameState = 0x1F800;
        }
    }

    public GameState(GameState gameState) {
        this.whitePieces = gameState.whitePieces;
        this.blackPieces = gameState.blackPieces;
        this.pawnsAndKnights = gameState.pawnsAndKnights;
        this.knightsAndKings = gameState.knightsAndKings;
        this.rooksAndQueens = gameState.rooksAndQueens;
        this.queensAndBishops = gameState.queensAndBishops;
        this.gameState = gameState.gameState;
    }

    public long getBishops(){
        return queensAndBishops & ( ~ rooksAndQueens );
    }

    public long getQueens(){
        return queensAndBishops & rooksAndQueens;
    }

    public long getRooks(){
        return rooksAndQueens & ( ~ queensAndBishops );
    }

    public long getKnights(){
        return knightsAndKings & pawnsAndKnights;
    }

    public long getKings(){
        return knightsAndKings & ( ~ pawnsAndKnights );
    }

    public long getPawns(){
        return pawnsAndKnights & ( ~ knightsAndKings );
    }

    public long getWhitePieces() {
        return whitePieces;
    }

    public long getBlackPieces() {
        return blackPieces;
    }

    public long getPawnsAndKnights() {
        return pawnsAndKnights;
    }

    public long getKnightsAndKings() {
        return knightsAndKings;
    }

    public long getRooksAndQueens() {
        return rooksAndQueens;
    }

    public long getQueensAndBishops() {
        return queensAndBishops;
    }

    public int getGameState() {
        return gameState;
    }

    public void setGameState(int gameState) {
        this.gameState = gameState;
    }

    public void setWhitePieces(long whitePieces) {
        this.whitePieces = whitePieces;
    }

    public void setBlackPieces(long blackPieces) {
        this.blackPieces = blackPieces;
    }

    public void setPawnsAndKnights(long pawnsAndKnights) {
        this.pawnsAndKnights = pawnsAndKnights;
    }

    public void setKnightsAndKings(long knightsAndKings) {
        this.knightsAndKings = knightsAndKings;
    }

    public void setRooksAndQueens(long rooksAndQueens) {
        this.rooksAndQueens = rooksAndQueens;
    }

    public void setQueensAndBishops(long queensAndBishops) {
        this.queensAndBishops = queensAndBishops;
    }

    public long selectWhitePieces(long pieces){
        return this.whitePieces & pieces;
    }

    public long selectBlackPieces(long pieces) {
        return this.blackPieces & pieces;
    }

    public boolean isDrawByFiftyMoveRule(){
        return getReversibleHalfMoveCount() == 100;
    }

    public int getReversibleHalfMoveCount(){
        return gameState & 0x0000007F;
    }

    public void incrementReversibleHalfMoveCount(){
        gameState ++;
    }

    public void resetReversibleHalfMoveCount(){
        gameState = gameState & 0xFFFFFF80;
    }

    /**
     * @return 0 for no pawn can be taken en passant file; 1 to 8 for h to a file.
     */
    public int getEnPassantFile(){
        return (0x00000780 & gameState) >>> 7;
    }

    /**
     * @param rowNum 0 for no pawn can be taken en passant; 1 to 8 for h to a file.
     *               This method will misbehave for values outside (0,8).
     */
    public void setEnPassantFile(int rowNum){
        gameState = ((gameState & 0xFFFFF87F) | ((rowNum & 0xF) << 7 ));
    }

    public void resetEnPassantFile(){
        gameState = gameState & 0xFFFFF87F;
    }

    public boolean canPotentiallyCastleLeft(Colour colour){
        if ( colour == Colour.WHITE ){
            return (gameState & 0x00000800) != 0;
        }
        return (gameState & 0x00002000) != 0;
    }

    public boolean canPotentiallyCastleRight(Colour colour){
        if ( colour == Colour.WHITE ){
            return (gameState & 0x00001000) != 0;
        }
        return (gameState & 0x00004000) != 0;
    }

    public void leftRookMoved(Colour colour){
        if ( colour == Colour.WHITE ){
            gameState = gameState ^ 0x00000800;
        }
        gameState = gameState ^ 0x00002000;
    }

    public void rightRookMoved(Colour colour){
        if ( colour == Colour.WHITE ){
            gameState = gameState ^ 0x00001000;
        }
        gameState = gameState ^ 0x00004000;
    }


    public void kingMoved(Colour colour){
        if ( colour == Colour.WHITE ){
            gameState = gameState & 0xFFFFE7FF;
        }
        gameState = gameState & 0xFFFF9FFF;
    }

    public Colour moveOf(){
        return (gameState & 0x00008000) == 0 ? Colour.WHITE : Colour.BLACK;
    }

    public void toggleMoveOf(){
        gameState = gameState ^ 0x00008000;
    }

    public Colour getPieceColour(int placeValue) {
        return getPieceColour( 1L << placeValue );
    }

    public Colour getPieceColour(long position) {
        if ( (position & whitePieces) != 0L ){
            return Colour.WHITE;
        } else if ( (position & blackPieces) != 0L ){
            return Colour.BLACK;
        }
        return null;
    }

    public PieceType getPieceType(int placeValue) {
        return getPieceType( 1L << placeValue );
    }

    public PieceType getPieceType(long position) {
        if ( (position & pawnsAndKnights) != 0L ){
            if ( (position & knightsAndKings) != 0L ){
                return PieceType.KNIGHT;
            }
            return PieceType.PAWN;
        }
        if ( (position & queensAndBishops) != 0L ){
            if ( (position & rooksAndQueens) != 0L ){
                return PieceType.QUEEN;
            }
            return PieceType.BISHOP;
        }
        if ( (position & rooksAndQueens) != 0L ){
            return PieceType.ROOK;
        }
        if ( (position & knightsAndKings) != 0L ){
            return PieceType.KING;
        }
        return null;
    }
}