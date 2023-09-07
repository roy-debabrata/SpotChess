package com.debabrata.spotchess.tools;

import com.debabrata.spotchess.support.PerftTest;

import java.util.Arrays;

public class PerformanceReport {

    public static void main(String[] args) {
        int TEST_COUNT = 10;

        // Initializing the class that holds test.
        PerftTest perft = new PerftTest();

        long overallStartTime = System.currentTimeMillis();

        Runnable[] tests = {perft::position1, perft::position2, perft::position3, perft::position4, perft::position5, perft::position6};
        long [] avgTimes = new long[6];

        StringBuilder output = new StringBuilder();
        for(int i = 0; i < TEST_COUNT; i++) {
            for(int j = 0; j < tests.length; j++) {
                System.out.printf("Iteration : %d  Test : %d \n", i, j);
                long time = timeIt(tests[j]);
                avgTimes[j] += time;
                output.append(String.format("%d\t", time));
            }
            output.append("\n");
        }

        System.out.println("\n\n\nPerft Positions:\n1\t\t2\t\t3\t\t4\t\t5\t6");
        System.out.println(output);
        System.out.println("-----------------------------------------");
        Arrays.stream(avgTimes).map(t -> t/TEST_COUNT).forEach(t -> System.out.printf("%d\t",t));
        System.out.println("\nTotal Execution Time : " +
                (System.currentTimeMillis() - overallStartTime) +
                " millis \n\n\n");
    }

    private static long timeIt(Runnable runnable) {
        long startTime = System.currentTimeMillis();
        runnable.run();
        return System.currentTimeMillis() - startTime;
    }
}
