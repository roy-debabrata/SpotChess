package com.debabrata.spotchess.console;

import java.util.stream.IntStream;

public class ConsoleMode {
    public void runREPL(String [] args) {

    }

    public void clearConsole() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                Runtime.getRuntime().exec("cls");
            }
            else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (final Exception e) {
            IntStream.range(0,100).forEach(System.out::println);
        }
    }
}
