package com.debabrata.spotchess.types;

import com.debabrata.spotchess.constants.TypeConstants;
import com.debabrata.spotchess.exception.InvalidPositionException;
import com.debabrata.spotchess.types.enums.GameType;
import com.debabrata.spotchess.utils.MoveInitUtil;
import com.debabrata.spotchess.types.enums.Colour;
import com.debabrata.spotchess.types.enums.PieceType;

/**
 * @author Debabrata Roy
 * comment Storing Rooks, Queens and Bishops in two variable seems like a good idea. I am sure a lot of
 * engines must use this as it seems so basic. Not sure if it's smart to jumble up the Knights, Kings and Pawns,
 * but it did save 4 bytes which seems like a lot at this moment though I might regret it later.
 * That said, progress is change.
 * <p>
 * Making class final to please the JVM inlining gods. Ideally we would like to make all the methods "inline" but that's
 * not a thing in java (even in C/C++ it's just a suggestion to the compiler that can be ignored), so I right now I'll
 * just make Position final. In case we do make Position non-final we'll make all methods in it final.
 * <p>
 * The board representation at any position follows Little-Endian Rank, Big-Endian File Mapping (LERBEF).
 * For an illustration check out the following:
 * <a href="https://www.chessprogramming.org/Bibob">...</a> and look at the image labeled "LERBEF"
 * We use this scheme because it feels more intuitive to me rather than for any performance reasons.
 */
public final class Position {
    /* Piece positions represent where the pieces are placed. */
    private long whitePieces;
    private long blackPieces;
    private long pawnsAndKnights;
    private long knightsAndKings;
    private long rooksAndQueens;
    private long queensAndBishops;

    /* Flags store other state variables associated with the game.
     * Last 8 bits (mask 0x000000FF) are for storing the count of reversible half moves. Read up on 50 move rule.
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
     * 0x80000000 = Sets whose move it is. 1 at this position means it's white's turn, 0 means it's black's turn.
     *
     * The white to move flag defaults to 1 because flag < 0 is faster to compute than flags >= 0. */
    private int flags;

    private Position() {
        flags = 0x80000000; // White moves first by default.
    }

    public Position(GameType gameType) {
        switch (gameType) {
            case NO_CASTLE:
                flags = 0x0F000000; /* We disable castling with all four rooks. */
            case STANDARD:
                whitePieces = 0x000000000000FFFFL;
                blackPieces = 0xFFFF000000000000L;
                pawnsAndKnights = 0x42FF00000000FF42L;
                knightsAndKings = 0x4A0000000000004AL;
                rooksAndQueens = 0x9100000000000091L;
                queensAndBishops = 0x3400000000000034L;
                break;
            default:
                throw new UnsupportedOperationException("Unsupported game type selected");
        }
        flags = flags | 0x80000000; // White moves first by default.
    }

    public Position(Position position) {
        this.whitePieces = position.whitePieces;
        this.blackPieces = position.blackPieces;
        this.pawnsAndKnights = position.pawnsAndKnights;
        this.knightsAndKings = position.knightsAndKings;
        this.rooksAndQueens = position.rooksAndQueens;
        this.queensAndBishops = position.queensAndBishops;
        this.flags = position.flags;
    }

    /* We don't addPieces and removePieces in engine. We use this to only setup the board for trying out positions. */
    private boolean addPiece(Colour colour, PieceType piece, int placeValue) {
        if (null == colour || null == piece || placeValue < 0 || placeValue > 63) {
            return false;
        }
        removePiece(placeValue); /* If some piece already exists at the position we remove it. */
        long position = 1L << placeValue;
        if(colour == Colour.WHITE) {
            whitePieces = whitePieces ^ position;
        } else {
            blackPieces = blackPieces ^ position;
        }
        if(piece == PieceType.KNIGHT || piece == PieceType.PAWN) {
            pawnsAndKnights = pawnsAndKnights ^ position;
        }
        if(piece == PieceType.KNIGHT || piece == PieceType.KING) {
            knightsAndKings = knightsAndKings ^ position;
        }
        if(piece == PieceType.QUEEN || piece == PieceType.BISHOP) {
            queensAndBishops = queensAndBishops ^ position;
        }
        if(piece == PieceType.QUEEN || piece == PieceType.ROOK) {
            rooksAndQueens = rooksAndQueens ^ position;
        }
        return true;
    }

    private boolean removePiece(int placeValue) {
        if (placeValue < 0 || placeValue > 63) {
            return false; /* PlaceValue outside board. */
        }
        long position = 1L << placeValue;
        if ((position & whitePieces) != 0 ) {
            whitePieces = whitePieces ^ position;
        } else if ((position & blackPieces) != 0){
            blackPieces = blackPieces ^ position;
        } else {
            return false; /* No piece to remove. */
        }
        if ((pawnsAndKnights & position) != 0) {
            pawnsAndKnights = pawnsAndKnights ^ position;
            if ((knightsAndKings & position) != 0) {
                knightsAndKings = knightsAndKings ^ position;
            }
        } else if ((rooksAndQueens & position) != 0) {
            rooksAndQueens = rooksAndQueens ^ position;
            if ((queensAndBishops & position) != 0) {
                queensAndBishops = queensAndBishops ^ position;
            }
        } else if ((queensAndBishops & position) != 0) {
            queensAndBishops = queensAndBishops ^ position;
        } else {
            knightsAndKings = knightsAndKings ^ position;
        }
        return true;
    }

    public long getQueens() {
        return queensAndBishops & rooksAndQueens;
    }

    public long getBishops() {
        return queensAndBishops & (~rooksAndQueens);
    }

    public long getRooks() {
        return rooksAndQueens & (~queensAndBishops);
    }

    public long getKnights() {
        return knightsAndKings & pawnsAndKnights;
    }

    public long getKings() {
        return knightsAndKings & (~pawnsAndKnights);
    }

    public long getPawns() {
        return pawnsAndKnights & (~knightsAndKings);
    }

    public long getWhitePieces() {
        return whitePieces;
    }

    public long getBlackPieces() {
        return blackPieces;
    }

    public long getAllPieces() {
        return whitePieces | blackPieces;
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

    public int getFlags() {
        return flags;
    }

    public long selectWhitePieces(long pieces) {
        return this.whitePieces & pieces;
    }

    public long selectBlackPieces(long pieces) {
        return this.blackPieces & pieces;
    }

    public boolean isDrawByFiftyMoveRule() {
        return getReversibleHalfMoveCount() >= 100;
    }

    public int getReversibleHalfMoveCount() {
        return flags & 0x000000FF;
    }

    public void incrementReversibleHalfMoveCount() {
        flags++;
    }

    private void setHalfMoveCounter(int counter) {
        resetReversibleHalfMoveCount();
        flags = flags + counter;
    }

    private void resetReversibleHalfMoveCount() {
        flags = flags & 0xFFFFFF00;
    }

    public boolean enPassantAvailable() {
        return (0x00FFFF00L & flags) != 0;
    }

    /**
     * @return pawns that can capture some pawn en-passant.
     */
    public long getPawnsThatCanCaptureEnPassant(boolean whiteToMove) {
        if (whiteToMove) {
            return (0x0000FF00L & flags) << 24; /* The L causes an implicit cast to long. */
        } else {
            return (0x0000FF00L & flags) << 16;
        }
    }

    /**
     * @return pawn that can be captured en-passant.
     */
    public long getPawnToBeCapturedEnPassant(boolean whiteToMove) {
        if (whiteToMove) {
            return (0x00FF0000L & flags) << 16; /* The L causes an implicit cast to long. */
        } else {
            return (0x00FF0000L & flags) << 8;
        }
    }

    /**
     * @return the position your pawn will end up in after taking the pawn en-passant.
     */
    public long getPawnLocationAfterEnPassant(boolean whiteToMove) {
        if (whiteToMove) {
            return (0x00FF0000L & flags) << 24; /* The L causes an implicit cast to long. */
        } else {
            return (0x00FF0000L & flags);
        }
    }

    private void setEnPassantStatusData(long pawnMovedTo, boolean whiteToMove) {
        if (whiteToMove) {
            long adjacentPositions = ((pawnMovedTo << 1) | (pawnMovedTo >> 1)) & 0x00000000FF000000L;
            adjacentPositions = adjacentPositions & blackPieces & getPawns();
            if (adjacentPositions != 0) {
                flags = flags | (int) (adjacentPositions >>> 16);
                flags = flags | (int) (pawnMovedTo >>> 8);
            }
        } else {
            long adjacentPositions = ((pawnMovedTo << 1) | (pawnMovedTo >> 1)) & 0x000000FF00000000L;
            adjacentPositions = adjacentPositions & whitePieces & getPawns();
            if (adjacentPositions != 0) {
                flags = flags | (int) (adjacentPositions >>> 24);
                flags = flags | (int) (pawnMovedTo >>> 16);
            }
        }
    }

    private void resetEnPassantStatusData() {
        flags = flags & 0xFF0000FF;
    }

    public boolean canPotentiallyCastle(boolean whiteToMove) {
        if (whiteToMove) {
            return (flags & 0x05000000) != 0x05000000;
        }
        return (flags & 0x0A000000) != 0x0A000000;
    }

    public boolean canPotentiallyCastleLeft(boolean whiteToMove) {
        if (whiteToMove) {
            return (flags & 0x01000000) == 0;
        }
        return (flags & 0x02000000) == 0;
    }

    public boolean canPotentiallyCastleRight(boolean whiteToMove) {
        if (whiteToMove) {
            return (flags & 0x04000000) == 0;
        }
        return (flags & 0x08000000) == 0;
    }

    private void leftRookMoved(boolean whiteToMove) {
        if (whiteToMove) {
            flags = flags | 0x01000000;
        } else {
            flags = flags | 0x02000000;
        }
    }

    private void rightRookMoved(boolean whiteToMove) {
        if (whiteToMove) {
            flags = flags | 0x04000000;
        } else {
            flags = flags | 0x08000000;
        }
    }

    private void kingMoved(boolean whiteToMove) {
        if (whiteToMove) {
            flags = flags | 0x05000000;
        } else {
            flags = flags | 0x0A000000;
        }
    }

    /**
     * @return true if it's white's turn to move false if it's black's turn to move.
     */
    public boolean whiteToMove() {
        return flags < 0;
    }

    private void toggleWhiteToMove() {
        flags = flags ^ 0x80000000;
    }

    public Colour getPieceColour(int placeValue) {
        return getPieceColour(1L << placeValue);
    }

    public Colour getPieceColour(long position) {
        if ((position & whitePieces) != 0L) {
            return Colour.WHITE;
        } else if ((position & blackPieces) != 0L) {
            return Colour.BLACK;
        }
        return null;
    }

    public PieceType getPieceType(int placeValue) {
        return getPieceType(1L << placeValue);
    }

    /* Avoids an explicit for check no piece as we know there is a piece at the position. */
    public PieceType getPieceTypeOfKnownPiece(long position) {
        if ((position & pawnsAndKnights) != 0L) {
            if ((position & knightsAndKings) != 0L) {
                return PieceType.KNIGHT;
            }
            return PieceType.PAWN;
        }
        if ((position & queensAndBishops) != 0L) {
            if ((position & rooksAndQueens) != 0L) {
                return PieceType.QUEEN;
            }
            return PieceType.BISHOP;
        }
        if ((position & rooksAndQueens) != 0L) {
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

    private void castle(long move, boolean whiteToMove) {
        long colourFlag = whiteToMove ? 0x00000000000000FFL : 0xFF00000000000000L;
        long side = colourFlag & (MoveInitUtil.isLeftCastle(move) ? 0xF8000000000000F8L : 0x0F0000000000000FL);
        knightsAndKings = knightsAndKings ^ (0x2A0000000000002AL & side);
        rooksAndQueens  = rooksAndQueens  ^ (0x9500000000000095L & side);
        if (whiteToMove) {
            whitePieces = whitePieces ^ (0xBF000000000000BFL & side);
        } else {
            blackPieces = blackPieces ^ (0xBF000000000000BFL & side);
        }
    }

    private int promote(long move, boolean whiteToMove) {
        PieceType promoteTo = MoveInitUtil.promotesTo(move);
        move = MoveInitUtil.getPromotionMove(move, whiteToMove);
        int takes = 0;

        long to = move & 0xFF000000000000FFL;
        boolean captures = false;
        if (whiteToMove) {
            whitePieces = whitePieces ^ move;
            if ((to & blackPieces) != 0) {
                blackPieces = blackPieces & ~to;
                captures = true;
            }
        } else {
            blackPieces = blackPieces ^ move;
            if ((to & whitePieces) != 0) {
                whitePieces = whitePieces & ~to;
                captures = true;
            }
        }

        if (captures) {
            if ((queensAndBishops & to) != 0) {
                queensAndBishops = queensAndBishops ^ to;
                takes = TypeConstants.BISHOP_TAKEN;
                if ((rooksAndQueens & to) != 0) {
                    rooksAndQueens = rooksAndQueens ^ to;
                    takes = TypeConstants.QUEEN_TAKEN;
                }
            } else if ((rooksAndQueens & to) != 0) {
                rooksAndQueens = rooksAndQueens ^ to;
                takes = TypeConstants.ROOK_TAKEN;
                if ( whiteToMove ) {
                    if (0x8000000000000000L == to) {
                        leftRookMoved(false);
                    } else if (0x0100000000000000L == to) {
                        rightRookMoved(false);
                    }
                } else {
                    if (0x0000000000000080L == to) {
                        leftRookMoved(true);
                    } else if (0x0000000000000001L == to) {
                        rightRookMoved(true);
                    }
                }
            } else if ((knightsAndKings & to) != 0) {
                knightsAndKings = knightsAndKings ^ to;
                pawnsAndKnights = pawnsAndKnights ^ to;
                takes = TypeConstants.KNIGHT_TAKEN;
            }
        }

        pawnsAndKnights = pawnsAndKnights ^ (move ^ to); /* Removing the pawn from "from" position. */
        switch (promoteTo) {
            case QUEEN:
                queensAndBishops = queensAndBishops | to;
                rooksAndQueens = rooksAndQueens | to;
                break;
            case KNIGHT:
                knightsAndKings = knightsAndKings | to;
                pawnsAndKnights = pawnsAndKnights | to;
                break;
            case BISHOP:
                queensAndBishops = queensAndBishops | to;
                break;
            case ROOK:
                rooksAndQueens = rooksAndQueens | to;
        }
        toggleWhiteToMove();
        return takes;
    }

    private void enPassant(long move, boolean whiteToMove) {
        move = move & 0x00FFFFFFFFFFFF00L;

        long toBeTakenEP = getPawnToBeCapturedEnPassant(whiteToMove);
        if (whiteToMove) {
            whitePieces = whitePieces ^ move;
            blackPieces = blackPieces ^ toBeTakenEP;
        } else {
            blackPieces = blackPieces ^ move;
            whitePieces = whitePieces ^ toBeTakenEP;
        }
        pawnsAndKnights = pawnsAndKnights ^ toBeTakenEP;
        pawnsAndKnights = pawnsAndKnights ^ move;
    }

    private void doublePush(long move, boolean whiteToMove) {
        move = move & 0x00FF00FFFF00FF00L;
        pawnsAndKnights = pawnsAndKnights ^ move;
        if (whiteToMove) {
            whitePieces = whitePieces ^ move;
        } else {
            blackPieces = blackPieces ^ move;
        }
        setEnPassantStatusData(move & 0x000000FFFF000000L, whiteToMove);
    }

    /** We rely on passed moves to be legal. We do not perform any move sanity checks. To avoid inconsistent board states
     *  make sure:
     *  1. The piece you want to move exists.
     *  2. You're not taking a piece of your own colour.
     *  3. It's the move of the side you're trying to make the move for.
     *
     *  @param move Move to be made.
     *  @return the counter move that can be used to call {@link #unmakeMove} reverse of this move.
     *  */
    public int makeMove(long move) {
        long from, to;
        int takes = 0;
        boolean captures = false;
        boolean pawnMoves = false;
        boolean whiteToMove = whiteToMove();

        if(MoveInitUtil.isSpecialMove(move)) {
            if (MoveInitUtil.isCastle(move)) {
                castle(move, whiteToMove);
                kingMoved(whiteToMove);
                incrementReversibleHalfMoveCount();
                toggleWhiteToMove();
                resetEnPassantStatusData();
                return 0;
            } else if (MoveInitUtil.isPromotion(move)) {
                resetEnPassantStatusData();
                return promote(move, whiteToMove);
            } else if (MoveInitUtil.isEnPassant(move)) {
                enPassant(move, whiteToMove);
                resetEnPassantStatusData();
            } else {
                resetEnPassantStatusData();
                doublePush(move, whiteToMove);
            }
            resetReversibleHalfMoveCount();
            toggleWhiteToMove();
            return 0;
        }

        if(whiteToMove) {
            from = whitePieces & move;
            to = from ^ move;
            whitePieces = whitePieces ^ move;
            if ((to & blackPieces) != 0) {
                blackPieces = blackPieces ^ to;
                captures = true;
            }
        } else {
            from = blackPieces & move;
            to = from ^ move;
            blackPieces = blackPieces ^ move;
            if ((to & whitePieces) != 0) {
                whitePieces = whitePieces ^ to;
                captures = true;
            }
        }

        /* Removing piece to be captured. */
        if (captures) {
            if ((rooksAndQueens & to) != 0) {
                rooksAndQueens = rooksAndQueens ^ to;
                if ((queensAndBishops & to) != 0) {
                    queensAndBishops = queensAndBishops ^ to;
                    takes = TypeConstants.QUEEN_TAKEN;
                } else {
                    takes = TypeConstants.ROOK_TAKEN;
                    /* We check if one of the unmoved rooks and update castling flags accordingly. */
                    if ( whiteToMove ) {
                        if (0x8000000000000000L == to) {
                            leftRookMoved(false);
                        } else if (0x0100000000000000L == to) {
                            rightRookMoved(false);
                        }
                    } else {
                        if (0x0000000000000080L == to) {
                            leftRookMoved(true);
                        } else if (0x0000000000000001L == to) {
                            rightRookMoved(true);
                        }
                    }
                }
            } else if ((pawnsAndKnights & to) != 0) {
                pawnsAndKnights = pawnsAndKnights ^ to;
                if ((knightsAndKings & to) != 0) {
                    knightsAndKings = knightsAndKings ^ to;
                    takes = TypeConstants.KNIGHT_TAKEN;
                } else {
                    takes = TypeConstants.PAWN_TAKEN;
                }
            } else {
                queensAndBishops = queensAndBishops ^ to; /* Can't take king in chess, taken piece must be a bishop. */
                takes = TypeConstants.BISHOP_TAKEN;
            }
        }
        /* Actually making the move for the piece. */
        if ((pawnsAndKnights & from) != 0) {
            /* It's a pawn/knight. */
            pawnsAndKnights = pawnsAndKnights ^ move;
            if ((knightsAndKings & from) != 0) {
                /* It's a knight. */
                knightsAndKings = knightsAndKings ^ move;
            } else {
                /* It's a pawn. */
                pawnMoves = true;
            }
        } else if ((rooksAndQueens & from) != 0) {
            /* It's a rook/queen. */
            rooksAndQueens = rooksAndQueens ^ move;
            if ((queensAndBishops & from) != 0) {
                /* It's a queen. */
                queensAndBishops = queensAndBishops ^ move;
            } else {
                /* It's a rook. We update rook castling flags. */
                if ( whiteToMove ) {
                    if (0x0000000000000080L == from) {
                        leftRookMoved(true);
                    } else if (0x0000000000000001L == from) {
                        rightRookMoved(true);
                    }
                } else {
                    if (0x8000000000000000L == from) {
                        leftRookMoved(false);
                    } else if (0x0100000000000000L == from) {
                        rightRookMoved(false);
                    }
                }
            }
        } else if ((queensAndBishops & from) != 0) {
            /* It's a bishop. */
            queensAndBishops = queensAndBishops ^ move;
        } else {
            /* It's a king. */
            knightsAndKings = knightsAndKings ^ move;
            /* We update king castling flags. */
            kingMoved(whiteToMove);
        }

        resetEnPassantStatusData();

        /* Updating reversible half-move count. */
        if (captures || pawnMoves) {
            resetReversibleHalfMoveCount();
        } else {
            incrementReversibleHalfMoveCount();
        }
        /* Toggling player to move. */
        toggleWhiteToMove();

        return takes;
    }

    public void unmakeMove(long move, int pieceTaken, int restoreFlags) {
        long from, to;
        flags = restoreFlags;
        boolean whiteToMove = whiteToMove();
        boolean enPassant = false;

        PieceType promoteTo = null;
        if (MoveInitUtil.isSpecialMove(move)) {
            if (MoveInitUtil.isPromotion(move)) {
                promoteTo = MoveInitUtil.promotesTo(move);
                move = MoveInitUtil.getPromotionMove(move, whiteToMove);
            } else if (MoveInitUtil.isCastle(move)) {
                castle(move, whiteToMove);
                return;
            } else if (MoveInitUtil.isEnPassant(move)) {
                enPassant = true;
                move = move & 0x00FFFFFFFFFFFF00L;
            } else {
                move = move & 0x00FFFFFFFFFFFF00L;
            }
        }

        if (whiteToMove) {
            to = whitePieces & move;
            from = to ^ move;
            whitePieces = whitePieces ^ move;
            if (pieceTaken != 0) {
                /* Black piece put back. */
                blackPieces = blackPieces ^ to;
            }
        } else {
            to = blackPieces & move;
            from = to ^ move;
            blackPieces = blackPieces ^ move;
            if (pieceTaken != 0) {
                /* White piece put back. */
                whitePieces = whitePieces ^ to;
            }
        }

        /* Reversing promotion. */
        if (null != promoteTo) {
            /* Dealing with prawn promotions. */
            switch (promoteTo) {
                case QUEEN:
                    queensAndBishops = queensAndBishops ^ to;
                    rooksAndQueens = rooksAndQueens ^ to;
                    pawnsAndKnights = pawnsAndKnights ^ to;
                    break;
                case KNIGHT:
                    knightsAndKings = knightsAndKings ^ to;
                    break;
                case BISHOP:
                    queensAndBishops = queensAndBishops ^ to;
                    pawnsAndKnights = pawnsAndKnights ^ to;
                    break;
                case ROOK:
                    rooksAndQueens = rooksAndQueens ^ to;
                    pawnsAndKnights = pawnsAndKnights ^ to;
            }
        }

        /* Moving back our pieces. */
        if ((pawnsAndKnights & to) != 0) {
            /* It's a pawn/knight. */
            if ((knightsAndKings & to) != 0) {
                /* It's a knight. */
                knightsAndKings = knightsAndKings ^ move;
                pawnsAndKnights = pawnsAndKnights ^ move;
            } else {
                /* It's a pawn. */
                pawnsAndKnights = pawnsAndKnights ^ move; /* Taking back the pawn. */
                /* Deal with en-passant. */
                if (enPassant) {
                    long toBeTakenEP = getPawnToBeCapturedEnPassant(whiteToMove);
                    if (whiteToMove) {
                        /* Black pawn restored. */
                        blackPieces = blackPieces ^ toBeTakenEP;
                    } else {
                        /* White pawn restored. */
                        whitePieces = whitePieces ^ toBeTakenEP;
                    }
                    pawnsAndKnights = pawnsAndKnights ^ toBeTakenEP;
                }
            }
        } else if ((rooksAndQueens & to) != 0) {
            /* It's a rook/queen. */
            rooksAndQueens = rooksAndQueens ^ move;
            if ((queensAndBishops & to) != 0) {
                /* It's a queen. */
                queensAndBishops = queensAndBishops ^ move;
            }
        } else if ((queensAndBishops & to) != 0) {
            /* It's a bishop. */
            queensAndBishops = queensAndBishops ^ move;
        } else {
            /* It's a king. */
            knightsAndKings = knightsAndKings ^ move;
        }
        /* We put back captured pieces. */
        if (pieceTaken != 0) {
            if ((pieceTaken & TypeConstants.QUEEN_TAKEN) != 0) {
                if ((pieceTaken & TypeConstants.BISHOP_TAKEN) != 0) {
                    queensAndBishops = queensAndBishops ^ to;
                }
                if ((pieceTaken & TypeConstants.ROOK_TAKEN) != 0) {
                    rooksAndQueens = rooksAndQueens ^ to;
                }
            } else {
                if ((pieceTaken & TypeConstants.KNIGHT_TAKEN) != 0) {
                    knightsAndKings = knightsAndKings ^ to;
                }
                pawnsAndKnights = pawnsAndKnights ^ to; /* Only pawns remain. */
            }
        }
    }

    /* Checks that the board state is not inconsistent. It's a useful method for testing. */
    public boolean validate() {
        if ((whitePieces & blackPieces) != 0) {
            return false; /* No piece can be both black and white. */
        }
        long allPiecesByColour = whitePieces | blackPieces;
        long allPiecesByType = pawnsAndKnights | knightsAndKings | rooksAndQueens | queensAndBishops;
        if (allPiecesByColour != allPiecesByType) {
            return false; /* Mismatch in pieces by colour and by piece type. */
        }
        long pawnsKnightsAndKings = pawnsAndKnights | knightsAndKings;
        long rooksQueensAndBishops = rooksAndQueens | queensAndBishops;
        if ((pawnsKnightsAndKings & rooksQueensAndBishops) != 0) {
            return false; /* These groups of pieces don't belong together. */
        }
        long kings = knightsAndKings & (~pawnsAndKnights);
        if (Long.bitCount(kings & whitePieces) != 1 || Long.bitCount(kings & blackPieces) != 1) {
            return false; /* More or less than 1 king for a side. */
        }
        long pawnsToBeCapturedEP = getPawnToBeCapturedEnPassant(whiteToMove());
        long pawnsThatCanCaptureEP = getPawnsThatCanCaptureEnPassant(whiteToMove());
        if (Long.bitCount(pawnsToBeCapturedEP) > 1) {
            return false; /* Multiple pawns cannot be captured en passant at once. */
        }
        if (Long.bitCount(pawnsThatCanCaptureEP) > 2) {
            return false; /* More than two pawns cannot capture a pawn en passant at once. */
        }
        if ((pawnsToBeCapturedEP == 0) != (pawnsThatCanCaptureEP == 0)) {
            return false; /* When pawns can be taken en passant takers must be available and vice versa. */
        }
        if ((getPawns() & 0xFF000000000000FFL) != 0) {
            return false; /* Pawn on the last rank. */
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return  whitePieces == position.whitePieces &&
                blackPieces == position.blackPieces &&
                pawnsAndKnights == position.pawnsAndKnights &&
                knightsAndKings == position.knightsAndKings &&
                rooksAndQueens == position.rooksAndQueens &&
                queensAndBishops == position.queensAndBishops &&
                flags == position.flags;
    }

    public static class Builder {
        private Position position;
        Square enPassantSquare = null;
        InvalidPositionException exception;

        public Builder() {
            this.position = new Position();
        }

        public Builder(GameType gameType) {
            this.position = new Position(gameType);
        }

        public Builder(Position position) {
            this.position = new Position(position);
        }

        public Builder withPiece(Colour colour, PieceType piece, Square square) {
            boolean success = position.addPiece(colour, piece, square.placeValue);
            if (!success) {
                exception = new InvalidPositionException("Couldn't add the following piece to position "
                        + colour + " " + piece + " to " + square);
            }
            return this;
        }

        public Builder withoutPiece(Square square){
            boolean success = position.removePiece(square.placeValue);
            if (!success) {
                exception = new InvalidPositionException("Couldn't remove piece from position");
            }
            return this;
        }

        public Builder halfMovesCount(int halfMovesCount) {
            position.setHalfMoveCounter(halfMovesCount);
            return this;
        }

        public Builder leftRookMoved(Colour colour) {
            position.leftRookMoved(colour == Colour.WHITE);
            return this;
        }

        public Builder rightRookMoved(Colour colour) {
            position.rightRookMoved(colour == Colour.WHITE);
            return this;
        }

        public Builder kingMoved(Colour colour) {
            position.kingMoved(colour == Colour.WHITE);
            return this;
        }

        public Builder toMove(Colour colour) {
            if (position.whiteToMove() != (colour == Colour.WHITE)) {
                position.toggleWhiteToMove();
            }
            return this;
        }

        public Builder enPassantSquare(Square square) {
            this.enPassantSquare = square;
            return this;
        }

        public Position build() throws InvalidPositionException {
            if (null != exception) {
                throw exception;
            }
            if (null != enPassantSquare) {
                position.setEnPassantStatusData(1L << enPassantSquare.placeValue, !position.whiteToMove());
            }
            position.validate();
            return position;
        }
    }
}