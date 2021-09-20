package com.debabrata.spotchess.types;

/**
 * @version 2.0
 * @author Debabrata Roy
 * comment Storing Rooks, Queens and Bishops in two variable seems like a good idea. I am sure a lot of
 * engines must use this as it seems so basic. Not sure if it's smart to jumble up the Knights, Kings and Pawns,
 * but it did save 4 bytes which seems like a lot at this moment though I might regret it later.
 * That said, progress is change.
 *
 * Making class final to please the JVM inlining gods. Ideally we would like to make all the methods "inline" but that's
 * not a thing in java (even in C/C++ it's just a suggestion to the compiler that can be ignored), so I right now I'll
 * just make GameState final. In case we do make GameState non-final we'll make all methods in it final.
 */
public final class GameState {
    /* Piece positions represent where the pieces are placed. */
    private long whitePieces;
    private long blackPieces;
    private long pawnsAndKnights;
    private long knightsAndKings;
    private long rooksAndQueens;
    private long queensAndBishops;

    /* Game state stores other state variables associated with the game. */
    private int gameState;

    /* Last 8 bits (mask 0x000000FF) are for storing the count of reversible half moves. Read up on 50 move rule.
     * Next 8 bits (mask 0x0000FF00) are to store the pawns eligible to take some pawn en-passant.
     *                            So, say a white pawn moved from rank 2 to 4. These 8 bits will hold all the 4th rank
     *                            black pawns that can take that pawn. So say c4 was played and there are black pawns on
     *                            both b4 and d4 then the 8 bits will contain 01010000. If it's white's move it will
     *                            have a similar content but for white's 5th rank pawns.
     * Next 8 bits (mask 0x00FF0000) are to store the pawn that will be taken as a result of the en-passant move.
     *                            So, in the last example that pawn was c4, so these 8 bits will contain 00100000.
     *                            When it's white's move it will contain black's 5th rank pawn it can take and when
     *                            it's black's move it will contain white's 4th rank pawn that can be taken by black.
     * For the following positions 0 will represent can castle and 1 represents can't castle.
     * 0x01000000 = white can castle with left rook
     * 0x02000000 = black can castle with left rook
     * 0x04000000 = white can castle with right rook
     * 0x08000000 = black can castle with right rook
     * 0x10000000 = Sets whose move it is. 0 at this position means it's white's turn, 1 means it's black's turn.
     *
     * At beginning of any regular chess game the gameState will have 0. This sets all flags to their initial state. */

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
            /* The default position of flags are such that at the beginning of a regular chess game the flags are 0. */
            gameState = 0;
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
        return (gameState & 0x000000FF) >= 100;
    }

    public int getReversibleHalfMoveCount(){
        return gameState & 0x000000FF;
    }

    public void incrementReversibleHalfMoveCount(){
        gameState ++;
    }

    public void resetReversibleHalfMoveCount(){
        gameState = gameState & 0xFFFFFF00;
    }

    /**
     * @return pawns that can capture some pawn en-passant.
     */
    public long getPawnsThatCanCaptureEnPassant(){
        if ( moveOf() == Colour.WHITE ) {
            return (0x0000FF00L & gameState) << 24 ; /* The L causes an implicit cast to long. */
        } else {
            return (0x0000FF00L & gameState) << 16 ;
        }
    }

    /**
     * @return pawn that can be captured en-passant.
     */
    public long getPawnToBeCapturedEnPassant(){
        if ( moveOf() == Colour.WHITE ) {
            return (0x00FF0000L & gameState) << 16 ; /* The L causes an implicit cast to long. */
        } else {
            return (0x00FF0000L & gameState) << 8 ;
        }
    }

    /**
     * @param rowNum 0 for no pawn can be taken en passant; 1 to 8 for h to a file.
     *               This method will misbehave for values outside (0,8).
     */
    public void setEnPassantFile(int rowNum){
        gameState = ((gameState & 0xFFFFF87F) | ((rowNum & 0xF) << 7 ));
    }

    public void resetEnPassantFile(){
        gameState = gameState & 0x00FFFF00;
    }

    public boolean canPotentiallyCastleLeft(Colour colour){
        if ( colour == Colour.WHITE ){
            return (gameState & 0x01000000) == 0;
        }
        return (gameState & 0x02000000) == 0;
    }

    public boolean canPotentiallyCastleRight(Colour colour){
        if ( colour == Colour.WHITE ){
            return (gameState & 0x04000000) == 0;
        }
        return (gameState & 0x08000000) == 0;
    }

    public void leftRookMoved(Colour colour){
        if ( colour == Colour.WHITE ){
            gameState = gameState | 0x01000000;
        } else {
            gameState = gameState | 0x02000000;
        }
    }

    public void rightRookMoved(Colour colour){
        if ( colour == Colour.WHITE ){
            gameState = gameState | 0x04000000;
        } else {
            gameState = gameState | 0x08000000;
        }
    }

    public void kingMoved(Colour colour){
        if ( colour == Colour.WHITE ){
            gameState = gameState | 0x05000000;
        } else {
            gameState = gameState | 0x0A000000;
        }
    }

    public Colour moveOf(){
        return (gameState & 0x10000000) == 0 ? Colour.WHITE : Colour.BLACK;
    }

    public void toggleMoveOf(){
        gameState = gameState ^ 0x10000000;
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

    /* Avoids an explicit for check no piece as we know there is a piece at the position. */
    public PieceType getPieceTypeOfKnownPiece(long position) {
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
        return PieceType.KING;
    }

    public PieceType getPieceType(long position) {
        if (((position & (whitePieces | blackPieces)) == 0L)) {
            return null;
        }
        return getPieceTypeOfKnownPiece(position);
    }
}