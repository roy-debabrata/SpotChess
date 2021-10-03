package com.debabrata.spotchess.types;

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
 * @version 2.0
 */
public final class Position {
    /* Piece positions represent where the pieces are placed. */
    private long whitePieces;
    private long blackPieces;
    private long pawnsAndKnights;
    private long knightsAndKings;
    private long rooksAndQueens;
    private long queensAndBishops;

    /* Flags store other state variables associated with the game. */
    private int flags;

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
     * At beginning of any regular chess game the flags will have 0. This sets all flags to their initial state. */

    public Position() {
        /* Standard board representation for normal standard game. The board representation is
         * Little-Endian Rank, Big-Endian File Mapping (LERBEF). For an illustration check out the following:
         * https://www.chessprogramming.org/Bibob and look at the image labeled "LERBEF"
         * We use this scheme because it feels more intuitive to me rather than for any performance reasons. */

        whitePieces = 0x000000000000FFFFL;
        blackPieces = 0xFFFF000000000000L;
        pawnsAndKnights = 0x42FF00000000FF42L;
        knightsAndKings = 0x4A0000000000004AL;
        rooksAndQueens = 0x9100000000000091L;
        queensAndBishops = 0x3400000000000034L;
        /* The default position of flags are such that at the beginning of a regular chess game the flags are 0. */
        flags = 0;
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

    public void resetReversibleHalfMoveCount() {
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
        long adjacentPositions = ((pawnMovedTo << 1) | (pawnMovedTo >> 1)) & 0x000000FFFF000000L;
        if (whiteToMove) {
            adjacentPositions = adjacentPositions & blackPieces & getPawns();
            if (adjacentPositions != 0) {
                flags = flags | (int) (adjacentPositions >>> 16);
                flags = flags | (int) (pawnMovedTo >>> 8);
            }
        } else {
            adjacentPositions = adjacentPositions & whitePieces & getPawns();
            if (adjacentPositions != 0) {
                flags = flags | (int) (adjacentPositions >>> 24);
                flags = flags | (int) (pawnMovedTo >>> 16);
            }
        }
    }

    public void resetEnPassantStatusData() {
        flags = flags & 0xFF0000FF;
    }

    public boolean canPotentiallyCastleLeft(Colour colour) {
        if (colour == Colour.WHITE) {
            return (flags & 0x01000000) == 0;
        }
        return (flags & 0x02000000) == 0;
    }

    public boolean canPotentiallyCastleRight(Colour colour) {
        if (colour == Colour.WHITE) {
            return (flags & 0x04000000) == 0;
        }
        return (flags & 0x08000000) == 0;
    }

    public void leftRookMoved(boolean whiteToMove) {
        if (whiteToMove) {
            flags = flags | 0x01000000;
        } else {
            flags = flags | 0x02000000;
        }
    }

    public void rightRookMoved(boolean whiteToMove) {
        if (whiteToMove) {
            flags = flags | 0x04000000;
        } else {
            flags = flags | 0x08000000;
        }
    }

    public void kingMoved(boolean whiteToMove) {
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
        return (flags & 0x10000000) == 0;
    }

    public void toggleWhiteToMove() {
        flags = flags ^ 0x10000000;
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

    /* We rely on passed moves to be legal. We do not perform any move sanity checks. To avoid inconsistent board states
     *  make sure:
     *  1. The piece you want to move exists.
     *  2. You're not taking a piece of your own colour.
     *  3. It's the move of the side you're trying to make the move for. */
    public void makeMove(int move) {
        long from = 1L << MoveInitUtil.getFrom(move);
        long to = 1L << MoveInitUtil.getTo(move);
        long fromOrTo = to | from;
        boolean captures = false;
        boolean pawnMoves = false;
        boolean whiteToMove = whiteToMove();
        if (whiteToMove) {
            whitePieces = whitePieces ^ fromOrTo;
            if ((blackPieces & to) != 0) {
                /* Black piece captured. */
                blackPieces = blackPieces ^ to;
                captures = true;
            }
        } else {
            blackPieces = blackPieces ^ fromOrTo;
            if ((whitePieces & to) != 0) {
                /* White piece captured. */
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
                } else {
                    /* A rook was taken. We check if one of the unmoved rooks and update castling flags accordingly. */
                    if ( whiteToMove ) {
                        if ((0x8000000000000000L & to) != 0) {
                            leftRookMoved(false);
                        } else if ((0x0100000000000000L & to) != 0) {
                            rightRookMoved(false);
                        }
                    } else {
                        if ((0x0000000000000080L & to) != 0) {
                            leftRookMoved(true);
                        } else if ((0x0000000000000001L & to) != 0) {
                            rightRookMoved(true);
                        }
                    }
                }
            } else if ((pawnsAndKnights & to) != 0) {
                pawnsAndKnights = pawnsAndKnights ^ to;
                if ((knightsAndKings & to) != 0) {
                    knightsAndKings = knightsAndKings ^ to;
                }
            } else {
                queensAndBishops = queensAndBishops ^ to; /* Can't take king in chess, taken piece must be a bishop. */
            }
        }
        /* Actually making the move for the piece. */
        if ((pawnsAndKnights & from) != 0) {
            /* It's a pawn/knight. */
            pawnsAndKnights = pawnsAndKnights ^ fromOrTo;
            if ((knightsAndKings & from) != 0) {
                /* It's a knight. */
                knightsAndKings = knightsAndKings ^ fromOrTo;
            } else {
                /* It's a pawn. */
                pawnMoves = true;
                /* Deal with en-passant. */
                if (MoveInitUtil.isEnPassant(move)) {
                    long toBeTakenEP = getPawnToBeCapturedEnPassant(whiteToMove);
                    if (whiteToMove) {
                        /* Black pawn captured en passant. */
                        blackPieces = blackPieces ^ toBeTakenEP;
                    } else {
                        /* White pawn captured en passant. */
                        whitePieces = whitePieces ^ toBeTakenEP;
                    }
                    pawnsAndKnights = pawnsAndKnights ^ toBeTakenEP;
                } else if (MoveInitUtil.isPromotion(move)) {
                    /* Dealing with prawn promotions. */
                    PieceType promoteTo = MoveInitUtil.promotesTo(move);
                    pawnsAndKnights = pawnsAndKnights ^ to; /* Removing the pawn. Next we place a piece. */
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
                }
            }
        } else if ((rooksAndQueens & from) != 0) {
            /* It's a rook/queen. */
            rooksAndQueens = rooksAndQueens ^ fromOrTo;
            if ((queensAndBishops & from) != 0) {
                /* It's a queen. */
                queensAndBishops = queensAndBishops ^ fromOrTo;
            } else {
                /* It's a rook. We update rook castling flags. */
                if ( whiteToMove ) {
                    if ((0x0000000000000080L & from) != 0) {
                        leftRookMoved(true);
                    } else if ((0x0000000000000001L & from) != 0) {
                        rightRookMoved(true);
                    }
                } else {
                    if ((0x8000000000000000L & from) != 0) {
                        leftRookMoved(false);
                    } else if ((0x0100000000000000L & from) != 0) {
                        rightRookMoved(false);
                    }
                }
            }
        } else if ((queensAndBishops & from) != 0) {
            /* It's a bishop. */
            queensAndBishops = queensAndBishops ^ fromOrTo;
        } else {
            /* It's a king. */
            knightsAndKings = knightsAndKings ^ fromOrTo;
            /* We update king castling flags. */
            kingMoved(whiteToMove);
            if (MoveInitUtil.isCastle(move)) {
                if (MoveInitUtil.isLeftCastle(move)) {
                    if (whiteToMove) {
                        whitePieces = whitePieces ^ 0x0000000000000090L;
                        rooksAndQueens = rooksAndQueens ^ 0x0000000000000090L;
                    } else {
                        blackPieces = blackPieces ^ 0x9000000000000000L;
                        rooksAndQueens = rooksAndQueens ^ 0x9000000000000000L;
                    }
                } else {
                    if (whiteToMove) {
                        whitePieces = whitePieces ^ 0x0000000000000005L;
                        rooksAndQueens = rooksAndQueens ^ 0x0000000000000005L;
                    } else {
                        blackPieces = blackPieces ^ 0x0500000000000000L;
                        rooksAndQueens = rooksAndQueens ^ 0x0500000000000000L;
                    }
                }
            }
        }

        resetEnPassantStatusData();
        if (pawnMoves && MoveInitUtil.isDoublePawnMove(move)) {
            setEnPassantStatusData(to, whiteToMove);
        }

        /* Updating reversible half-move count. */
        if (captures || pawnMoves) {
            resetReversibleHalfMoveCount();
        } else {
            incrementReversibleHalfMoveCount();
        }
        /* Toggling player to move. */
        toggleWhiteToMove();
    }

    /* Checks that the board state is not inconsistent. It's a useful method for testing. */
    public boolean checkSanity() {
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
        return true;
    }
}