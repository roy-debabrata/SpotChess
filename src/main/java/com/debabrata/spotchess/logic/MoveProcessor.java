package com.debabrata.spotchess.logic;

import com.debabrata.spotchess.types.Position;
import com.debabrata.spotchess.types.enums.PieceType;
import com.debabrata.spotchess.utils.BitUtil;
import com.debabrata.spotchess.utils.KingAndKnightMovesUtil;
import com.debabrata.spotchess.utils.MoveInitUtil;
import com.debabrata.spotchess.utils.RookAndBishopMovesUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class MoveProcessor {
    private final int[] moveBuffer;
    private int writePosition;

    /* Frequently reused values. */
    private boolean whiteToMove;
    private long ourPieces;
    private long enemyPieces;
    private long allPieces;
    private long bishopType;
    private long rookType;
    private long knights;
    private long pawns;
    private long kings;
    private long ourKing;
    private int kingPlace;

    private long pawnPushedTwice;
    private long epTakers;
    private long epTo;

    /* Related to checks. */
    private boolean isCheck;
    private long checkBlock; /* Positions where if you can place your piece it blocks your check.
                                Also includes the attacker position as well. Is zero if multiple pieces check.*/
    private boolean canLeftCastle;
    boolean canRightCastle;

    /* Relates to pinned pieces. */
    private long pinnedPieces;
    int pinCount = 0;
    private final long [] pinPairList = new long[8];
    private final boolean [] bishopPin = new boolean[8];

    public MoveProcessor(int[] moveBuffer) {
        this.moveBuffer = moveBuffer;
    }

    /**
     * Writes all moves to the moveBuffer starting with "writePosition". At the end it returns a new writePosition for
     * where new moves can be written. If check/stalemated returns same writePosition as was given.
     *
     * @return position on moveBuffer where new moves can be written.
     */
    public int addMovesToBuffer(Position position, int writePos) {
        prepare(position, writePos);
        processChecks();
        processPinnedPieces();
        if(isCheck) {
            addKingMoves();
            addBlockingMoves();
            return writePosition;
        }
        addKingMoves();
        addPawnMoves();
        addPieceMoves();
        addPinnedPieceMoves();
        return writePosition;
    }

    public boolean isCheck() {
        return isCheck;
    }

    /**
     * Returns a list of moves for any given position. It spawns a new instance of the move processor to do so. This
     * method should be avoided wherever performance is important.
     *
     * @return list of moves in the given position.
     * */
    public static List<Integer> getMovesInPosition(Position position) {
        int [] moves = new int[300];
        MoveProcessor processor = new MoveProcessor(moves);
        int endPosition = processor.addMovesToBuffer(position, 0);

        return Arrays.stream(moves).boxed()
                .limit(endPosition)
                .filter(x -> x != 0)
                .collect(Collectors.toList());
    }

    /**
     * Returns if the king is under check in the given position. This method should be avoided wherever performance is
     * important.
     *
     * @return returns true if the king is under check, otherwise false.
     * */
    public static boolean isKingUnderCheck(Position position) {
        int [] moves = new int[300];
        MoveProcessor processor = new MoveProcessor(moves);
        processor.addMovesToBuffer(position, 0);

        return processor.isCheck();
    }

    /* Fetches basic information about the position. */
    private void prepare(Position position, int writePos) {
        /* This is where the moves will be written. */
        this.writePosition = writePos;

        whiteToMove = position.whiteToMove();
        if (whiteToMove) {
            ourPieces = position.getWhitePieces();
            enemyPieces = position.getBlackPieces();
        } else {
            ourPieces = position.getBlackPieces();
            enemyPieces = position.getWhitePieces();
        }
        allPieces = ourPieces | enemyPieces;

        bishopType = position.getQueensAndBishops();
        rookType = position.getRooksAndQueens();

        long pawnsAndKnights = position.getPawnsAndKnights();
        long knightsAndKings = position.getKnightsAndKings();

        knights = pawnsAndKnights & knightsAndKings;
        pawns = pawnsAndKnights ^ knights;
        kings = knightsAndKings ^ knights;

        isCheck = false;
        checkBlock = 0;
        ourKing = kings & ourPieces;
        kingPlace = BitUtil.getBitPlaceValue(ourKing);
        canLeftCastle = position.canPotentiallyCastleLeft(whiteToMove);
        canRightCastle = position.canPotentiallyCastleRight(whiteToMove);

        if(position.enPassantAvailable()) {
            epTakers = position.getPawnsThatCanCaptureEnPassant(whiteToMove);
            pawnPushedTwice = position.getPawnToBeCapturedEnPassant(whiteToMove);
            epTo = position.getPawnLocationAfterEnPassant(whiteToMove);
        } else {
            epTakers = 0;
            pawnPushedTwice = 0;
            epTo = 0;
        }
    }

    /**
     * Populates two values: isCheck and checkBlock.
     * Check block is basically the positions at which a friendly piece can move to block a check. This may be the
     * position of the attacker as well, so effectively also a list of pieces that can be captured to stop the check.
     * <p>
     * If checkBlock is 0 then the king is bound to move as there are multiple checks.
     **/
    private void processChecks() {
        /* Knight Checks. */
        long attacker = KingAndKnightMovesUtil.getKnightMoves(kingPlace) & knights & enemyPieces;
        if (attacker != 0) {
            isCheck = true;
            checkBlock = checkBlock | attacker;
        }

        /* Lateral Checks.
         * There can be more than one lateral checks at a time. Ex. axb8=Q where the king is at a8 and a queen at a6. */
        long kingAttack = RookAndBishopMovesUtil.getRookMoves(kingPlace, allPieces);
        attacker = kingAttack & rookType & enemyPieces;
        if (attacker != 0) {
            if (isCheck) {
                /* We avoid unnecessary checks as already in check by another piece. */
                checkBlock = 0;
                return;
            }
            isCheck = true;
            long semiMask = RookAndBishopMovesUtil.getRookSemiMask(kingPlace);
            if ((attacker & semiMask) != 0 && (attacker & ~semiMask) != 0) {
                return; /* Attacks are from two different pieces. Check block is already zero. */
            } else {
                kingAttack = kingAttack & ((attacker & semiMask) == 0 ? ~semiMask : semiMask); /* Limiting blocks to attack lateral. */
                if ((attacker > ourKing || attacker < 0) && ourKing > 0) {
                    checkBlock = checkBlock | (kingAttack & -ourKing); /* Adds all positions from king up to attacker to blocks. */
                } else {
                    checkBlock = checkBlock | (kingAttack & (ourKing - 1));
                }
            }
        }

        /* Diagonal Checks. */
        kingAttack = RookAndBishopMovesUtil.getBishopMoves(kingPlace, allPieces);
        attacker = kingAttack & bishopType & enemyPieces; /* There can only be one at max. */
        if (attacker != 0) {
            if (isCheck) {
                checkBlock = 0;
                return;
            }
            isCheck = true;
            long semiMask = RookAndBishopMovesUtil.getBishopSemiMask(kingPlace);
            kingAttack = kingAttack & ((attacker & semiMask) == 0 ? ~semiMask : semiMask); /* Limiting blocks to attack diagonal. */

            if ((attacker > ourKing || attacker < 0) && ourKing > 0) { /* Simply "attacker > ourKing" if java supported unsigned. */
                checkBlock = checkBlock | (kingAttack & -ourKing);
            } else {
                checkBlock = checkBlock | (kingAttack & (ourKing - 1));
            }
            /* I cannot think of a way in which we can have a double bishop and pawn check. So we just return. (In an actual game). #FAITH */
            return;
        }

        /* Pawn Checks. */
        if (whiteToMove) {
            /* Selecting black pawns attack the position. */
            kingAttack = (((ourKing << 7) & 0x7F7F7F7F7F7F7F7FL) | ((ourKing << 9) & 0xFEFEFEFEFEFEFEFEL)) & pawns & enemyPieces;
        } else {
            /* Selecting white pawns attack the position. */
            kingAttack = (((ourKing >>> 9) & 0x7F7F7F7F7F7F7F7FL) | ((ourKing >>> 7) & 0xFEFEFEFEFEFEFEFEL)) & pawns & enemyPieces;
        }
        if (kingAttack != 0) {
            if (isCheck) {
                /* We avoid unnecessary checks as already in check by another piece. */
                checkBlock = 0;
                return;
            }
            isCheck = true;
            checkBlock = checkBlock | kingAttack;
        }
    }

    /**
     * Populates "pinnedPieces", also adds any possible moves by those pieces to the moveBuffer.
     * There are a few assumptions in this logic. Before this is called the {@link #processChecks} needs to be called.
     */
    private void processPinnedPieces() {
        pinnedPieces = 0;
        pinCount = 0;

        /* Diagonal pins. */
        long diagonallyClosePairs = RookAndBishopMovesUtil.getBishopPins(kingPlace, allPieces);

        long nonCheckingEnemyBishops = diagonallyClosePairs & bishopType & enemyPieces & ~checkBlock; /* Enemy bishop currently not giving check, in pair adjacent pieces. */
        if (nonCheckingEnemyBishops != 0) {
            long semiMask = RookAndBishopMovesUtil.getBishopSemiMask(kingPlace);
            long diagonal1 = semiMask & diagonallyClosePairs;
            long diagonal2 = diagonal1 ^ diagonallyClosePairs;

            separateToPairsAndProcess(diagonal1, bishopType, true);
            separateToPairsAndProcess(diagonal2, bishopType, true);
        }

        /* Lateral pins. */
        long laterallyClosePairs = RookAndBishopMovesUtil.getRookPins(kingPlace, allPieces);

        long nonCheckingEnemyRooks = laterallyClosePairs & rookType & enemyPieces & ~checkBlock;
        if (nonCheckingEnemyRooks != 0) { /* Enemy rook currently not giving check, among the pair adjacent pieces. */
            long semiMask = RookAndBishopMovesUtil.getRookSemiMask(kingPlace);
            long lateral1 = semiMask & laterallyClosePairs;
            long lateral2 = lateral1 ^ laterallyClosePairs;

            separateToPairsAndProcess(lateral1, rookType, false);
            separateToPairsAndProcess(lateral2, rookType, false);
        }

        /* Lateral en-passant pin. Edge case.
         * White king at a5, white pawn at b5, black rook at d5. Black moves c5 (double push); b4 legal; bxc4 illegal. */
        if (epTakers != 0 && !isCheck) {
            long epRank = whiteToMove ? 0x000000FF00000000L : 0x00000000FF000000L;
            if ((ourKing & epRank) != 0 && (epTakers & (epTakers - 1)) == 0) {
                /* Our king is on the rank and only one pawn can take en passant. */
                long relevantRank = epRank & (ourKing > epTakers ? (ourKing - 1) : (-ourKing ^ ourKing));
                long relevantRooks = rookType & enemyPieces & relevantRank;
                long otherPieces = (relevantRank & allPieces) ^ pawnPushedTwice ^ epTakers ^ relevantRooks;
                if (relevantRooks != 0 && (ourKing > epTakers ? relevantRooks >= otherPieces : ((-otherPieces & relevantRooks) != relevantRooks))) {
                    /* Nothing in between rooks and the king other than the two en-passant related pieces. Pin en-passant. */
                    epTakers = 0;
                    pawnPushedTwice = 0;
                }
            }
        }
    }

    private void separateToPairsAndProcess(long diagonal, long attacker, boolean attackerType) {
        if (diagonal == 0) {
            return;
        }
        long pair1 = diagonal & -ourKing;
        long pair2 = diagonal ^ pair1;

        handlePinPairs(pair1, attacker, attackerType);
        handlePinPairs(pair2, attacker, attackerType);
    }

    private void handlePinPairs(long pair, long attacker, boolean attackerType) {
        long pinned = pair & ourPieces;
        if(pinned != 0 && (pair & enemyPieces & attacker & ~checkBlock) != 0){
            /* One of our pieces is pinned by an enemy bishop/rook piece. */
            pinnedPieces = pinnedPieces | pinned;
            pinPairList[pinCount] = pair;
            bishopPin[pinCount++] = attackerType;
        }
    }

    private long enemyAttacks() {
        long enemyAttacks = 0;
        long board = allPieces ^ ourKing;

        for(long bishops = bishopType & enemyPieces; bishops != 0; bishops &= (bishops - 1)) {
            int place = BitUtil.getLastBitPlaceValue(bishops);
            enemyAttacks |= RookAndBishopMovesUtil.getBishopMoves(place, board);
        }
        for(long rooks = rookType & enemyPieces; rooks != 0; rooks &= (rooks - 1)) {
            int place = BitUtil.getLastBitPlaceValue(rooks);
            enemyAttacks |= RookAndBishopMovesUtil.getRookMoves(place, board);
        }
        for(long kinghts = knights & enemyPieces; kinghts != 0; kinghts &= (kinghts - 1)) {
            int place = BitUtil.getLastBitPlaceValue(kinghts);
            enemyAttacks |= KingAndKnightMovesUtil.getKnightMoves(place);
        }
        int place = BitUtil.getBitPlaceValue(kings & enemyPieces);
        enemyAttacks |= KingAndKnightMovesUtil.getKingMoves(place);

        long enemyPawns = pawns & enemyPieces;
        if (whiteToMove) {
            enemyAttacks |= ((enemyPawns >>> 7) & 0xFEFEFEFEFEFEFEFEL) | ((enemyPawns >>> 9) & 0x7F7F7F7F7F7F7F7FL);
        } else {
            enemyAttacks |= ((enemyPawns << 7) & 0x7F7F7F7F7F7F7F7FL) | ((enemyPawns << 9) & 0xFEFEFEFEFEFEFEFEL);
        }
        return enemyAttacks;
    }

    private void addMoves(int from, long tos) {
        for(; tos != 0; tos &= (tos - 1)) {
            int to = BitUtil.getLastBitPlaceValue(tos);
            moveBuffer[writePosition++] = MoveInitUtil.newMove(from, to);
        }
    }

    private void addKingMoves() {
        long enemyAttacks = enemyAttacks();

        /* Adding regular moves. */
        long reachable = ~ourPieces & ~enemyAttacks & KingAndKnightMovesUtil.getKingMoves(kingPlace);
        addMoves(kingPlace, reachable);
        if (isCheck) return;

        /* Adding castling moves. */
        long leftCastleBits, rightCastleBits;
        if (whiteToMove) {
            leftCastleBits = 0x0000000000000070L; rightCastleBits = 0x0000000000000006L;
        } else {
            leftCastleBits = 0x7000000000000000L; rightCastleBits = 0x0600000000000000L;
        }
        if (canLeftCastle && (leftCastleBits & allPieces) == 0 && (leftCastleBits & 0x3000000000000030L & enemyAttacks) == 0) {
            moveBuffer[writePosition++] = MoveInitUtil.newLeftCastle(kingPlace, kingPlace + 2);
        }
        if (canRightCastle && (rightCastleBits & allPieces) == 0 && (rightCastleBits & enemyAttacks) == 0) {
            moveBuffer[writePosition++] = MoveInitUtil.newRightCastle(kingPlace, kingPlace - 2);
        }
    }

    private void addBlockingMoves() {
        if (checkBlock == 0) {
            return;
        }
        addPawnMoves(checkBlock);
        addPieceMoves(checkBlock);
    }

    private void addPawnMoves() {
        addPawnMoves(0xFFFFFFFFFFFFFFFFL);
    }

    private void addPawnMoves(long to, int displacement) {
        for(; to != 0; to &= (to - 1)) {
            int toInt = BitUtil.getLastBitPlaceValue(to);
            int from = toInt + displacement;
            addPawnMoves(from, toInt);
        }
    }

    private void addPawnMoves(int from, int to) {
        if ( to > 55 || to < 8) {
            moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(from, to, PieceType.QUEEN);
            moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(from, to, PieceType.KNIGHT);
            moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(from, to, PieceType.BISHOP);
            moveBuffer[writePosition++] = MoveInitUtil.newPawnPromotion(from, to, PieceType.ROOK);
        } else {
            moveBuffer[writePosition++] = MoveInitUtil.newMove(from, to);
        }
    }

    private void addEnPassantMoves() {
        int to = BitUtil.getBitPlaceValue(epTo);
        long nonPinnedEp = epTakers & (~pinnedPieces);
        for(long epTaker = nonPinnedEp; epTaker != 0; epTaker &= (epTaker -1)) {
            int from = BitUtil.getLastBitPlaceValue(epTaker);
            moveBuffer[writePosition++] = MoveInitUtil.newEnPassant(from, to);
        }
    }

    private void addPawnDoubleMoves(long to, int displacement) {
        for(; to != 0; to &= (to - 1)) {
            int toInt = BitUtil.getLastBitPlaceValue(to);
            int from = toInt + displacement;
            moveBuffer[writePosition++] = MoveInitUtil.newPawnDoubleMove(from, toInt);
        }
    }

    private void addPawnMoves(long range) {
        long ourPawns = pawns & ourPieces & ~pinnedPieces;
        long enemyInRange = range & enemyPieces;
        long freeRange = range & ~allPieces;

        if (whiteToMove) {
            long step1 = ourPawns << 8;
            long step2 = (step1 & ~allPieces & 0x0000000000FF0000L) << 8;
            addPawnMoves(step1 & freeRange, -8);
            addPawnDoubleMoves(step2 & freeRange, -16);

            long take1 = (ourPawns << 9) & 0xFEFEFEFEFEFEFEFEL & enemyInRange;
            long take2 = (ourPawns << 7) & 0x7F7F7F7F7F7F7F7FL & enemyInRange;
            addPawnMoves(take1, -9);
            addPawnMoves(take2, -7);
        } else {
            long step1 = ourPawns >>> 8;
            long step2 = (step1 & ~allPieces & 0x0000FF0000000000L) >>> 8;
            addPawnMoves(step1 & freeRange, 8);
            addPawnDoubleMoves(step2 & freeRange, 16);

            long take1 = (ourPawns >>> 9) & 0x7F7F7F7F7F7F7F7FL & enemyInRange;
            long take2 = (ourPawns >>> 7) & 0xFEFEFEFEFEFEFEFEL & enemyInRange;
            addPawnMoves(take1, 9);
            addPawnMoves(take2, 7);
        }
        if ((enemyInRange & pawnPushedTwice) != 0) {
            addEnPassantMoves();
        }
    }

    private void addPieceMoves() {
        addPieceMoves(0xFFFFFFFFFFFFFFFFL);
    }

    private void addPieceMoves(long range) {
        for(long bishops = bishopType & ourPieces & ~pinnedPieces; bishops != 0; bishops &= (bishops - 1)) {
            int place = BitUtil.getLastBitPlaceValue(bishops);
            long blocks = range & ~ourPieces & RookAndBishopMovesUtil.getBishopMoves(place, allPieces);
            addMoves(place, blocks);
        }
        for(long rooks = rookType & ourPieces & ~pinnedPieces; rooks != 0; rooks &= (rooks - 1)) {
            int place = BitUtil.getLastBitPlaceValue(rooks);
            long blocks = range & ~ourPieces & RookAndBishopMovesUtil.getRookMoves(place, allPieces);
            addMoves(place, blocks);
        }
        for(long kinghts = knights & ourPieces & ~pinnedPieces; kinghts != 0; kinghts &= (kinghts - 1)) {
            int place = BitUtil.getLastBitPlaceValue(kinghts);
            long blocks = range & ~ourPieces & KingAndKnightMovesUtil.getKnightMoves(place);
            addMoves(place, blocks);
        }
    }

    private void addPinnedPieceMoves() {
        for(int i = 0; i < pinCount; i++) {
            boolean bishopTypePin = bishopPin[i];
            long pair = pinPairList[i];
            long pinned = pair & ourPieces;
            long pinner = pair & enemyPieces;

            if ((pair & rookType) == pair && !bishopTypePin) { /* Both are rook type. */
                int from = BitUtil.getBitPlaceValue(pinned);
                int upto = BitUtil.getBitPlaceValue(pinner);

                int shift = ((from ^ upto) & 7) == 0 ? (from > upto ? -8 : 8) : (from > upto ? -1 : 1);
                for (int to = from + shift; to != upto; to += shift) {
                    moveBuffer[writePosition++] = MoveInitUtil.newMove(from, to);
                }
                for (int to = kingPlace + shift; to != from; to += shift) {
                    moveBuffer[writePosition++] = MoveInitUtil.newMove(from, to);
                }
                moveBuffer[writePosition++] = MoveInitUtil.newMove(from, upto);
            } else if ((pair & bishopType) == pair && bishopTypePin) { /* Both are bishop type. */
                int from = BitUtil.getBitPlaceValue(pinned);
                int upto = BitUtil.getBitPlaceValue(pinner);

                int shift = ((from & 7)-(upto & 7)) == ((from >>> 3) - (upto >>> 3)) ?
                                                          (from > upto ? -9 : 9) : (from > upto ? -7 : 7);
                for (int to = from + shift; to != upto; to += shift) {
                    moveBuffer[writePosition++] = MoveInitUtil.newMove(from, to);
                }
                for (int to = kingPlace + shift; to != from; to += shift) {
                    moveBuffer[writePosition++] = MoveInitUtil.newMove(from, to);
                }
                moveBuffer[writePosition++] = MoveInitUtil.newMove(from, upto);
            } else if ((pinned & pawns) != 0) { /* Pinned piece is a pawn. */
                int from = BitUtil.getBitPlaceValue(pinned);
                int upto = BitUtil.getBitPlaceValue(pinner);

                if (!bishopTypePin) {
                    if (((from ^ upto) & 7) == 0) { /* Same column. Adding pawn forward moves. */
                        if (whiteToMove) {
                            if (((pinned << 8) & allPieces) == 0) {
                                moveBuffer[writePosition++] = MoveInitUtil.newMove(from, from + 8);
                                if ((((pinned & 0x000000000000FF00L) << 16) & ~allPieces) != 0) {
                                    moveBuffer[writePosition++] = MoveInitUtil.newPawnDoubleMove(from, from + 16);
                                }
                            }
                        } else {
                            if (((pinned >>> 8) & allPieces) == 0) {
                                moveBuffer[writePosition++] = MoveInitUtil.newMove(from, from - 8);
                                if ((((pinned & 0x00FF000000000000L ) >>> 16) & ~allPieces) != 0) {
                                    moveBuffer[writePosition++] = MoveInitUtil.newPawnDoubleMove(from, from - 16);
                                }
                            }
                        }
                    }
                } else { /* Pinned by bishop. */
                    if (whiteToMove) {
                        if ((pinned << 7) == pinner || (pinned << 9) == pinner) {
                            moveBuffer[writePosition++] = MoveInitUtil.newMove(from, upto);
                        }
                    } else {
                        if ((pinned >>> 7) == pinner || (pinned >>> 9) == pinner) {
                            moveBuffer[writePosition++] = MoveInitUtil.newMove(from, upto);
                        }
                    }
                    if (pawnPushedTwice != 0 && (epTakers & pinned) != 0) {
                        int to = BitUtil.getBitPlaceValue(epTo);
                        if (((upto & 7) - (to & 7)) == ((upto >> 3) - (to >> 3)) || ((upto - to) & 7) + ((upto >> 3) - (to >> 3)) == 0) {
                            /* The en-passant to position shares a diagonal with the pinner as well. */
                            moveBuffer[writePosition++] = MoveInitUtil.newEnPassant(from, to);
                        }
                    }
                }
            }
        }
    }
}