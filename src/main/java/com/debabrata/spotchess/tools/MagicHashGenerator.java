package com.debabrata.spotchess.tools;

import com.debabrata.spotchess.utils.BitUtil;
import com.debabrata.spotchess.utils.RookAndBishopMovesUtil;
import com.debabrata.spotchess.types.enums.PieceType;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class MagicHashGenerator {
    /* Following fields are for magicSearchRunner. They configure how many threads are set up, after how many runs they
    *  check up on other threads for completions and how long to search for before timing out the search. The last two
    *  are for benchmarking the performance of our MagicHashGenerator for testing optimizations. The result in
    *  TOOL_BENCHMARKING_TIME_SPENT is the combined processor time so every thread's execution time counts separately.
    *
    *  I should probably write code to make the all this configurable via command line either as arguments or by prompts
    *  but let's admit it I am probably the only one who's ever going to use it, and I just prefer editing code. */
    private static final int NUMBER_OF_THREADS = 4;
    private static final int TRY_AT_ONCE_COUNT = 100000;
    private static final int ATTEMPT_TIMEOUT = 10000;
    private static final boolean TOOL_BENCHMARKING_ENABLED = true;
    private static final AtomicLong TOOL_BENCHMARKING_TIME_SPENT = new AtomicLong();
    private static final AtomicLong TOOL_BENCHMARKING_MAGICS_TRIED = new AtomicLong();
    private static final AtomicLong TOOL_BENCHMARKING_CUTOFF_CROSSED = new AtomicLong();

    public static void main(String[] args) {
        //runForSinglePosition();
        runForAllPositions();
    }

    private static void runForSinglePosition() {
        PieceType pieceType = PieceType.BISHOP;
        boolean pinningType = false;
        int position = 0;
        int targetedShiftNumber = 58;
        int earlyFailureCheckPosition = 0;
        int earlyExpectedMoveConvergence = 0;
        MagicGenerator generator = new RandomMagicGenerator();

        SearchConfiguration searchConfig = new SearchConfiguration(earlyFailureCheckPosition, earlyExpectedMoveConvergence);

        long mask = RookAndBishopMovesUtil.getPieceMask(pieceType, pinningType, position);
        long[] positionCombinations = BitUtil.getAllPossibleBitCombinations(mask);
        long[] associatedMoves = RookAndBishopMovesUtil.getAllPossibleMovesCombinations(pieceType, position, positionCombinations);

        SearchScope searchScope = new SearchScope(positionCombinations, associatedMoves, targetedShiftNumber);

        /* If you want to use anything other than Random Numbers for tying as magic hash just implement your own
        *  version of MagicGenerator and pass it here. Otherwise, you can go into the code directly and change the
        *  implementation where we are calling nextPotentialMagic() and replace it with whatever you prefer. MagicGenerator
        *  is slower obviously, but it's "neater" speaking in Java terms. */
        long magic = initiateSearch(searchConfig, searchScope, generator);

        if ( pieceType == PieceType.ROOK ) {
            System.out.println("Mask  : " + formatLongToHexLiteral(RookAndBishopMovesUtil.getRookMask(position, pinningType)));
        } else {
            System.out.println("Mask  : " + formatLongToHexLiteral(RookAndBishopMovesUtil.getBishopMask(position, pinningType)));
        }
        if ( magic != 0L ) {
            System.out.println("Magic : " + formatLongToHexLiteral(magic));
            System.out.println("Utilization : "
                    + getUtilization(magic, positionCombinations, targetedShiftNumber) + "/" + positionCombinations.length);
        } else {
            System.out.println("Timed out/invalid search.");
        }

        printBenchmarkingResult();
    }

    /* Just run this method if you need a quick set of values. Or set a higher shifts, but set ATTEMPT_TIMEOUT accordingly. */
    public static void runForAllPositions() {
        int[] magicNumberBishopShiftTargets = {
                58,59,59,59,59,59,59,58,59,59,59,59,59,59,59,59,
                59,59,57,57,57,57,59,59,59,59,57,55,55,57,59,59,
                59,59,57,55,55,57,59,59,59,59,57,57,57,57,59,59,
                59,59,59,59,59,59,59,59,58,59,59,59,59,59,59,58
        };

        int[] magicNumberRookShiftTargets = {
                52,53,53,53,53,53,53,52,53,54,54,54,54,54,54,53,
                53,54,54,54,54,54,54,53,53,54,54,54,54,54,54,53,
                53,54,54,54,54,54,54,53,53,54,54,54,54,54,54,53,
                53,54,54,54,54,54,54,53,52,53,53,53,53,53,53,52
        };
        findAndDisplayMagicsFor(PieceType.BISHOP, false, magicNumberBishopShiftTargets);
        findAndDisplayMagicsFor(PieceType.ROOK, false, magicNumberRookShiftTargets);
        findAndDisplayMagicsFor(PieceType.BISHOP, true, magicNumberBishopShiftTargets);
        findAndDisplayMagicsFor(PieceType.ROOK, true, magicNumberRookShiftTargets);
    }

    private static void findAndDisplayMagicsFor(PieceType piece, boolean pinningType, int[] magicNumberShiftTargets) {
        resetBenchmarkingData();

        String capitalCasePieceName = piece == PieceType.BISHOP ? "Bishop" : "Rook";
        if (pinningType) {
            capitalCasePieceName = capitalCasePieceName + "Pinning";
        }

        long[] magics = new long[64];

        /* Printing shift and occupancy arrays. */
        StringBuilder shiftArray = new StringBuilder("magicNumberShifts" + capitalCasePieceName + "[] = {");
        StringBuilder occupancyArray = new StringBuilder("occupancyMask" + capitalCasePieceName + "[] = {");
        StringBuilder occupancySelectorArray = new StringBuilder("selectorMask" + capitalCasePieceName + "[] = {");

        StringBuilder timedOutPositions = new StringBuilder();

        for (int i = 0; i < magicNumberShiftTargets.length; i++ ) {
            shiftArray.append(magicNumberShiftTargets[i]).append(",");
            if ( piece == PieceType.ROOK ) {
                occupancyArray.append(formatLongToHexLiteral(RookAndBishopMovesUtil.getRookMask(i, false))).append(", ");
                occupancySelectorArray.append(formatLongToHexLiteral(RookAndBishopMovesUtil.getRookMask(i, true))).append(", ");
            } else {
                occupancyArray.append(formatLongToHexLiteral(RookAndBishopMovesUtil.getBishopMask(i, false))).append(", ");
                occupancySelectorArray.append(formatLongToHexLiteral(RookAndBishopMovesUtil.getBishopMask(i, true))).append(", ");
            }
        }
        System.out.println(shiftArray + "\b};");
        System.out.println(occupancyArray + "\b\b};");
        if (pinningType) {
            System.out.println(occupancySelectorArray + "\b\b};");
        }

        System.out.print("magicNumber" + capitalCasePieceName + "[] = {");

        /* Finding magics. */
        for (int position = 0; position < 64; position++) {
            int targetedShiftNumber = magicNumberShiftTargets[position];

            long mask = RookAndBishopMovesUtil.getPieceMask(piece, false, position);
            long[] positionCombinations = BitUtil.getAllPossibleBitCombinations(mask);
            long[] associatedMoves;
            if (pinningType) {
                associatedMoves = RookAndBishopMovesUtil.getAllPossiblePinCombinations(piece, position, positionCombinations);
            } else {
                associatedMoves = RookAndBishopMovesUtil.getAllPossibleMovesCombinations(piece, position, positionCombinations);
            }

            SearchScope searchScope = new SearchScope(positionCombinations, associatedMoves, targetedShiftNumber);

            long magic = initiateSearch(new SearchConfiguration(), searchScope, new RandomMagicGenerator());
            if (magic == 0) {
                timedOutPositions.append(position).append(",");
            } else {
                magics[position] = magic;
            }
            System.out.print(formatLongToHexLiteral(magics[position]) + ", ");
        }
        System.out.println("\b\b};");

        if ( timedOutPositions.length() > 0 ){
            System.out.println("Couldn't figure for the following positions. " + timedOutPositions + "\b. Timed out/not possible.");
        }

        printBenchmarkingResult();
        System.out.println("\n");
    }

    /**
     * @return Returns a magic number if one is found. Returns zero if the search times out or is invalid.
     * */
    private static long initiateSearch(SearchConfiguration searchConfig, SearchScope searchScope, MagicGenerator generator) {
        HashMap<Long,Integer> compressibilityMap = new HashMap<>();

        /* Use associatedMove as a key to determine how many unique move are there in the position. */
        for (long associatedMove : searchScope.associatedMoves) {
            compressibilityMap.merge(associatedMove, 1, Integer::sum); /* We count how many times a move occurs. */
        }

        int addressSize = addressSizeForItems(compressibilityMap.keySet().size());
        if ( searchScope.targetedShiftNumber + addressSize > 64 ) {
            return 0L; /* The requester is trying to compress the moves into a smaller array than possible. */
        }

        if ( searchConfig.earlyChecksEnabled ) {
            List<Map.Entry<Long, Integer>> list = new LinkedList<>(compressibilityMap.entrySet());
            Comparator<Map.Entry<Long, Integer>> comparator = Map.Entry.comparingByValue();
            list.sort(comparator); /* Getting an ordered list of all the associated moves. */

            long[] sortedPositions = new long[searchScope.positionCombinations.length];
            long[] sortedMoves = new long[searchScope.associatedMoves.length];

            int sortedArrayLoc = 0;
            for ( Map.Entry<Long, Integer> move : list ) {
                for ( int i = 0; i < searchScope.associatedMoves.length; i++ ) {
                    if ( searchScope.associatedMoves[i] == move.getKey() ) {
                        sortedPositions[sortedArrayLoc] = searchScope.positionCombinations[i];
                        sortedMoves[sortedArrayLoc++] = searchScope.associatedMoves[i];
                    }
                }
            }
            searchScope = new SearchScope(sortedPositions, sortedMoves, searchScope.targetedShiftNumber);
        }

        return magicSearchRunner(searchConfig, searchScope, generator);
    }

    /**
     * @return Returns a magic number if one is found. Returns zero if the search times out.
     * */
    public static long magicSearchRunner(SearchConfiguration searchConfig, SearchScope searchScope, MagicGenerator generator) {
        AtomicBoolean commonStop = new AtomicBoolean();
        AtomicLong returnMagic = new AtomicLong();

        /* Adding shutdown hook for handling kill signals. */
        Runtime.getRuntime().addShutdownHook(new Thread(() -> commonStop.set(true)));

        class MagicLookup implements Runnable {
            @Override
            public void run() {
                ThreadLocal<Long> startTime = new ThreadLocal<>();
                startTime.set(System.currentTimeMillis());

                int threadNumber = Integer.parseInt(Thread.currentThread().getName());

                outer: while ( ! commonStop.get() ) {
                    for ( int i = 0; i < TRY_AT_ONCE_COUNT; i++ ) {
                        /* We want to avoid large sparse regions in our number. That's known to work best, but I don't
                         * understand the mathematics behind it. Refer, www.chessprogramming.org/Looking_for_Magics */
                        long potentialMagic = generator.nextPotentialMagic(threadNumber);

                        boolean isMagic = isNumberMagic(searchConfig, searchScope , potentialMagic);
                        if ( isMagic ) {
                            returnMagic.set(potentialMagic);
                            commonStop.set(true); /* Letting other threads know we have found a magic.*/
                            if ( TOOL_BENCHMARKING_ENABLED ) {
                                TOOL_BENCHMARKING_MAGICS_TRIED.addAndGet(i + 1);
                            }
                            break outer;
                        }
                    }
                    if ( TOOL_BENCHMARKING_ENABLED ) {
                        TOOL_BENCHMARKING_MAGICS_TRIED.addAndGet(TRY_AT_ONCE_COUNT);
                    }
                }
                if ( TOOL_BENCHMARKING_ENABLED ) {
                    TOOL_BENCHMARKING_TIME_SPENT.addAndGet(System.currentTimeMillis() - startTime.get());
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
                    if ( System.currentTimeMillis() - startTime >= ATTEMPT_TIMEOUT ) {
                        commonStop.set(true);
                    }
                }
            } catch (InterruptedException ignored) {}
        }
    }

    /**
     * Tries out provided magic number for hash based lookup for all the pieceCombinations to see if there are any
     * collisions. Collisions where the return value is the same are good as it can potentially reduce lookup array
     * size. However, if here are collisions where the return value is not the same the number is not a magic.
     */
    public static boolean isNumberMagic(SearchConfiguration searchConfig, SearchScope searchScope , long potentialMagic) {
        if (searchScope.targetedShiftNumber < 40 || searchScope.targetedShiftNumber >= 64)
            return false;
        long[] occupations = new long[1 << (64 - searchScope.targetedShiftNumber)];
        int moveConvergence = 0;
        for (int i = 0; i < searchScope.positionCombinations.length; i++) {
            if ( searchConfig.earlyChecksEnabled && searchConfig.earlyFailureCheckPosition == i ) {
                if ( moveConvergence < searchConfig.earlyExpectedMoveConvergence ) {
                    return false;
                }
                TOOL_BENCHMARKING_CUTOFF_CROSSED.addAndGet(1);
            }
            int position = (int)((searchScope.positionCombinations[i] * potentialMagic) >>> searchScope.targetedShiftNumber);
            if (position >= occupations.length) {
                return false;
            }
            if (occupations[position] == 0) {
                occupations[position] = searchScope.associatedMoves[i];
            } else if (occupations[position] == searchScope.associatedMoves[i]) {
                moveConvergence++;
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * @return The max utilized position of the storage space.
     **/
    public static int getUtilization(long magic, long[] positionCombinations, int shift) {
        int maxPosition = 0;
        for (long positionCombination : positionCombinations){
            int position = (int)((positionCombination * magic) >>> shift);
            if (position > maxPosition) {
                maxPosition = position;
            }
        }
        return maxPosition;
    }

    /* Returns how many bits of addresses are required to hold n items. */
    public static int addressSizeForItems(int n) {
        if ( n == 1 ) {
            return 1;
        }
        int count = 0;
        if (n > 0 && (n & (n - 1)) == 0) {
            count --;
        }
        while(n != 0) {
            n >>= 1;
            count += 1;
        }
        return count;
    }

    private static String formatLongToHexLiteral(long number ) {
        return "0x" + String.format("%016X", number) + "L";
    }

    private static void resetBenchmarkingData() {
        TOOL_BENCHMARKING_TIME_SPENT.set(0);
        TOOL_BENCHMARKING_MAGICS_TRIED.set(0);
        TOOL_BENCHMARKING_CUTOFF_CROSSED.set(0);
    }

    private static void printBenchmarkingResult() {
        if (TOOL_BENCHMARKING_ENABLED) {
            System.out.println("Benchmarking information : ");
            System.out.println("Processor time for the set = " + TOOL_BENCHMARKING_TIME_SPENT.get());
            System.out.println("Total number of magics tried = " + TOOL_BENCHMARKING_MAGICS_TRIED.get());
            System.out.printf("Magics tried per milli second = %.2f\n",
                    (double) TOOL_BENCHMARKING_MAGICS_TRIED.get()/TOOL_BENCHMARKING_TIME_SPENT.get());
            System.out.println("Early cut-off crossed = " + TOOL_BENCHMARKING_CUTOFF_CROSSED.get());
        }
    }

    private interface MagicGenerator {
        long nextPotentialMagic(int threadNumber);
    }

    private static class RandomMagicGenerator implements MagicGenerator {
        @Override
        public final long nextPotentialMagic(int threadNumber) {
            return ThreadLocalRandom.current().nextLong()
                    & ThreadLocalRandom.current().nextLong()
                    & ThreadLocalRandom.current().nextLong();
        }
    }

    private static class SearchScope {
        final long[] positionCombinations;
        final long[] associatedMoves;
        final int targetedShiftNumber;

        /**
         * @param positionCombinations All the possible piece combination that need to be hashed by the magic.
         * @param associatedMoves The corresponding move sets for all the position combinations.
         * @param targetedShiftNumber In the magic hashing this is the value that indicates how small the lookup array is.
         *                            It is the value by which after magic multiplication the result is shifted to get the position.
         */
        public SearchScope(long[] positionCombinations, long[] associatedMoves, int targetedShiftNumber) {
            this.positionCombinations = positionCombinations;
            this.associatedMoves = associatedMoves;
            this.targetedShiftNumber = targetedShiftNumber;
        }
    }

    /**
     * When targetedShiftNumber is high positive hash collisions become necessary for the search to be successful. If we
     * sort our positionCombinations by the matching associatedMoves i.e. positions combinations that result in the same
     * moves go together and those that have the most matches go first, it becomes possible for us to do an early check
     * if the search is going to fail. This class provides the configuration for such checks to the runner.
     * */
    private static class SearchConfiguration {
        final boolean earlyChecksEnabled;
        final int earlyFailureCheckPosition;
        final int earlyExpectedMoveConvergence;

        /**
         * @param earlyFailureCheckPosition The position at which it becomes possible for us to check if search will fail.
         * @param earlyExpectedMoveConvergence The minimum number of moveConvergence we expect to have happened by the time
         *                                     we reach earlyFailureCheckPosition.
         */
        public SearchConfiguration(int earlyFailureCheckPosition, int earlyExpectedMoveConvergence) {
            this.earlyChecksEnabled = true;
            this.earlyFailureCheckPosition = earlyFailureCheckPosition;
            this.earlyExpectedMoveConvergence = earlyExpectedMoveConvergence;
        }

        public SearchConfiguration() {
            this.earlyChecksEnabled = false;
            this.earlyFailureCheckPosition = 0;
            this.earlyExpectedMoveConvergence = 0;
        }
    }
}
