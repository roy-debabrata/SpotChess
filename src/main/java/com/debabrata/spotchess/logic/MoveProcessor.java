package com.debabrata.spotchess.logic;

import com.debabrata.spotchess.types.Position;
import com.debabrata.spotchess.utils.BitUtil;
import com.debabrata.spotchess.utils.KingAndKnightMovesUtil;
import com.debabrata.spotchess.utils.RookAndBishopMovesUtil;

public class MoveProcessor {
    private Position position;
    private int[] moveBuffer;
    private int writePosition;

    /* Frequently reused values. */
    boolean whiteToMove;
    long ourPieces;
    long enemyPieces;
    long allPieces;
    long bishopType;
    long rookType;
    long knights;
    long pawns;
    long kings;
    long ourKing;
    int kingPlace;

    long pawnPushedTwice;
    long epTakers;

    /* Related to checks. */
    boolean isCheck;
    long checkBlock; /* Positions where if you can place your piece it blocks your check.
                        Also includes the attacker position as well. Is zero if multiple pieces check.*

    /* Relates to pinned pieces. */
    long pinnedPieces;
    int pinCount = 0;
    long [] pinnedList = new long[8];
    long [] pinnerList = new long[8];

    public MoveProcessor(int[] moveBuffer) {
        this.moveBuffer = moveBuffer;
    }

    /**
     * Writes all moves to the moveBuffer starting with "writePosition". At the end it returns a new writePosition for
     * where new moves can be written. If check/stalemated returns same writePosition as was given.
     *
     * @return position on moveBuffer where new moves can be written.
     */
    public int addMovesToBuffer(Position position, int writePosition) {
        prepare(position, writePosition);
        return 0;
    }

    /* Fetches basic information about the position. */
    private void prepare(Position position, int writePosition) {
        /* This is where the moves will be written. */
        this.writePosition = writePosition;

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

        epTakers = 0;
        pawnPushedTwice = 0;
        if(position.enPassantAvailable()) {
            epTakers = position.getPawnsThatCanCaptureEnPassant(whiteToMove);
            pawnPushedTwice = position.getPawnToBeCapturedEnPassant(whiteToMove);
        }

        ourKing = kings & ourPieces;
        kingPlace = BitUtil.getBitPlaceValue(ourKing);
    }

    /**
     * Populates two values: isCheck and checkBlock.
     * Check block is basically the positions at which a friendly piece can move to block a check. This may be the
     * position of the attacker as well, so effectively also a list of pieces that can be captured to stop the check.
     * <p>
     * If checkBlock is 0 then the king is bound to move as there are multiple checks.
     **/
    private void processChecks() {
        isCheck = false;
        checkBlock = 0;

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

            separateToPairsAndProcess(diagonal1, bishopType);
            separateToPairsAndProcess(diagonal2, bishopType);
        }

        /* Lateral pins. */
        long laterallyClosePairs = RookAndBishopMovesUtil.getRookPins(kingPlace, allPieces);

        long nonCheckingEnemyRooks = laterallyClosePairs & bishopType & enemyPieces & ~checkBlock;
        if (nonCheckingEnemyRooks != 0) { /* Enemy rook currently not giving check, among the pair adjacent pieces. */
            long semiMask = RookAndBishopMovesUtil.getRookSemiMask(kingPlace);
            long lateral1 = semiMask & laterallyClosePairs;
            long lateral2 = lateral1 ^ laterallyClosePairs;

            separateToPairsAndProcess(lateral1, rookType);
            separateToPairsAndProcess(lateral2, rookType);
        }

        /* Lateral en-passant pin. Edge case.
         * White king at a5, white pawn at b5, black rook at d5. Black moves c5 (double push); b4 legal; bxc4 illegal. */
        if (epTakers != 0 && !isCheck) {
            long epRank = whiteToMove ? 0x000000FF00000000L : 0x00000000FF000000L;
            if ((ourKing & epRank) != 0 && (epTakers & (epTakers - 1)) == 0) {
                /* Our king and only one pawn can take en passant. */
                long relevantRank = epRank & (ourKing > epTakers ? (ourKing - 1) : -ourKing);
                long relevantRooks = rookType & enemyPieces & relevantRank;
                long otherPieces = (relevantRank & allPieces) ^ pawnPushedTwice ^ epTakers;
                if (relevantRooks != 0 && ((ourKing > epTakers && relevantRooks >= otherPieces)
                        || ourKing < epTakers && relevantRooks <= otherPieces)) {
                    /* Nothing in between rooks and the king other than the two en-passant related pieces. Pin en-passant. */
                    epTakers = 0;
                    pawnPushedTwice = 0;
                }
            }
        }
    }

    private void separateToPairsAndProcess(long diagonal, long attackerType) {
        if (diagonal == 0) {
            return;
        }
        long pair1 = diagonal & -ourKing;
        long pair2 = diagonal ^ pair1;

        handlePairsPairs(pair1, attackerType);
        handlePairsPairs(pair2, attackerType);
    }

    private void handlePairsPairs(long pair, long attackerType) {
        long pinned = pair & ourPieces;
        if(pinned != 0 && (pair & enemyPieces & attackerType & ~checkBlock) != 0){
            /* One of our pieces is pinned by an enemy bishop type piece. */
            long pinner = pair ^ pinned;
            pinnedPieces = pinnedPieces | pinned;
            pinnedList[pinCount] = pinned;
            pinnerList[pinCount++] = pinner;
        }
    }
}
