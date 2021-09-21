package com.debabrata.spotchess.generators;

import com.debabrata.spotchess.magics.RookAndBishopMovesUtil;
import com.debabrata.spotchess.types.PieceType;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class MagicHashGenerator {
    private static final int NUMBER_OF_THREADS = 4;
    private static final int TRY_AT_ONCE_COUNT = 100000; /* How many to check before seeing if other threads found one */
    private static final int ATTEMPT_TIMEOUT_ROOK_MILLIS = 10000;
    private static final int ATTEMPT_TIMEOUT_BISHOP_MILLIS = 10000;

    public static void main(String[] args) {
        // TODO runForSinglePosition();
        runForAllPositions();
    }

    /* Just paste targets of any value you have +1 (larger the shift the smaller the array size).
    The system starts at this and tries larger and larger values as targeted shifts till it times out on all of them.
    Since we are essentially trying out random numbers you may get lucky by running multiple times. But if that
    doesn't work for you try a longer timeout. I could rewrite this thing for.  */
    public static void runForAllPositions() {

        int[] magicNumberBishopShiftTargets = {
                40,40,40,40,40,40,40,40,40,40,40,40,40,40,40,40,
                40,40,40,40,40,40,40,40,40,40,40,40,40,40,40,40,
                40,40,40,40,40,40,40,40,40,40,40,40,40,40,40,40,
                40,40,40,40,40,40,40,40,40,40,40,40,40,40,40,40
        };

        int[] magicNumberRookShiftTargets = {
                50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,
                50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,
                50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,
                50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50
        };

        findAndDisplayMagicsFor(PieceType.BISHOP, magicNumberBishopShiftTargets);
        findAndDisplayMagicsFor(PieceType.ROOK, magicNumberRookShiftTargets);
    }

    private static void findAndDisplayMagicsFor(PieceType piece, int[] magicNumberShiftTargets) {
        String capitalCasePieceName = piece == PieceType.BISHOP ? "Bishop" : "Rook";
        boolean[] timedOutPositions = new boolean[64];
        long[] magics = new long[64];

        boolean improvementsComplete = false;
        for ( int attemptNumber = 0; !improvementsComplete; attemptNumber ++ ){
            improvementsComplete = true;

            System.out.println("Attempting to find better magics: ");
            System.out.print("magicNumber" + capitalCasePieceName + "[] = {");

            for (int i = 0; i < 64; i++) {
                if (timedOutPositions[i]) {
                    System.out.print(formatHexNumber(magics[i]) + ", ");
                    magicNumberShiftTargets[i]--;
                    continue;
                }
                long magic = searchForMagics(piece, i, magicNumberShiftTargets[i] + attemptNumber);
                if (magic == 0) {
                    timedOutPositions[i] = true;
                    magicNumberShiftTargets[i]--;
                } else {
                    improvementsComplete = false;
                    magics[i] = magic;
                }
                System.out.print(formatHexNumber(magics[i]) + ", ");
            }
            System.out.println("\b\b};");

            System.out.print("occupancyMask" + capitalCasePieceName + "[] = {");
            for ( int mask : magicNumberShiftTargets) {
                System.out.print((mask + attemptNumber) + "," );
            }
            System.out.println("\b};\n\n");
        }
    }

    private static String formatHexNumber ( long number ) {
        return "0x" + String.format("%016x", number).toUpperCase() + "L";
    }

    public static long searchForMagics(PieceType pieceType, int placeValue, int targetShift) {
        long mask = RookAndBishopMovesUtil.getPieceMask(pieceType, placeValue);
        long[] positionCombinations = RookAndBishopMovesUtil.getAllPossiblepieceCombinations(mask);
        long[] associatedMoves = RookAndBishopMovesUtil.getAllPossibleMovesCombinations(pieceType, placeValue, positionCombinations);

        AtomicBoolean commonStop = new AtomicBoolean();
        AtomicLong returnMagic = new AtomicLong();

        class MagicLookup implements Runnable {
            @Override
            public void run() {
                while ( ! commonStop.get() ) {
                    for ( int i = 0; i < TRY_AT_ONCE_COUNT; i++ ) {
                        /* We want to avoid large sparse regions in our number. That's known to work best, but I don't
                         * understand the mathematics behind it. Refer, www.chessprogramming.org/Looking_for_Magics */
                        long potentialMagic = ThreadLocalRandom.current().nextLong()
                                                    & ThreadLocalRandom.current().nextLong()
                                                    & ThreadLocalRandom.current().nextLong();
                        boolean isMagic = isNumberMagic(positionCombinations, associatedMoves, potentialMagic, targetShift);
                        if ( isMagic ) {
                            returnMagic.set(potentialMagic);
                            commonStop.set(true); /* Letting other threads know we have found a magic.*/
                            break;
                        }
                    }
                }
            }
        }
        /* Noting starting time to enforce timeout. */
        long startTime = System.currentTimeMillis();

        /* Setting up and running the thread. */
        Thread[] threads = new Thread[NUMBER_OF_THREADS];
        commonStop.set(false);
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            threads[i] = new Thread(new MagicLookup(), Integer.toString(i));
            threads[i].start();
        }

        /* Waiting for threads and killing them if they can't find anything in too long. */
        while (true) {
            try {
                Thread.sleep(100);
                boolean running = false;
                for ( int i = 0; i < NUMBER_OF_THREADS; i++ ) {
                    running = running | threads[i].getState() != Thread.State.TERMINATED;
                }
                if ( ! running ) {
                    return returnMagic.get();
                } else {
                    int fail = pieceType == PieceType.ROOK ? ATTEMPT_TIMEOUT_ROOK_MILLIS : ATTEMPT_TIMEOUT_BISHOP_MILLIS;
                    if ( System.currentTimeMillis() - startTime > fail ) {
                        commonStop.set(true);
                    }
                }
            } catch (InterruptedException ignored) {}
        }
    }

    public static boolean isNumberMagic(long [] pieceCombinations, long [] associatedMoves, long magic, int shiftCount) {
        if (shiftCount < 40 || shiftCount >= 64)
            return false;
        long[] occupations = new long[1 << (64 - shiftCount)];
        int happyCoincidences = 0;
        for (int i = 0; i < pieceCombinations.length; i++) {
            int position =  (int)((pieceCombinations[i] * magic) >>> shiftCount);
            if (position < 0 || position >= occupations.length) {
                return false;
            }
            if (occupations[position] == 0) {
                occupations[position] = associatedMoves[i];
            } else if (occupations[position] == associatedMoves[i]) {
                happyCoincidences++;
            } else {
                return false;
            }
        }
        return true;
    }
}