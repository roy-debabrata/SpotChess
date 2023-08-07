package com.debabrata.spotchess.support;

import com.debabrata.spotchess.console.PositionPrinter;
import com.debabrata.spotchess.logic.MoveProcessor;
import com.debabrata.spotchess.support.notation.game.FENParser;
import com.debabrata.spotchess.support.perft.Perft;
import com.debabrata.spotchess.types.Game;
import com.debabrata.spotchess.types.Position;
import com.debabrata.spotchess.types.Square;
import com.debabrata.spotchess.utils.MoveInitUtil;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

// This class is horrible. I want to refactor it, but refactoring testing code is a luxury I cannot afford.
public class DivideAndCheck {
    private static String pathToStockfishExe = "D:\\Projects\\OpenSource\\stockfish_15.1\\stockfish-windows-2022-x86-64.exe"; //Replace this with your local path.
    private static String perftFen = "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -"; //Replace this with your fen.
    private static int    perftDepth = 8; //Replace this with your depth.

    private static final FENParser parser = new FENParser();
    private static final int [] moveBuffer = new int[300 * 20];
    private static final ArrayList<String> moveNameChain= new ArrayList<>();
    private static final ArrayList<Integer> moveChain= new ArrayList<>();

    public static void main(String[] args) throws Exception {
        boolean defaultsNotSet = pathToStockfishExe.isEmpty() || perftFen.isEmpty() || perftDepth == 0;
        boolean defaultsOverridden = false;
        if (args.length == 3) {
            pathToStockfishExe = args[0];
            perftFen = args[1];
            perftDepth = Integer.parseInt(args[2]);
            defaultsOverridden = true;
        }
        if (defaultsNotSet && !defaultsOverridden) {
            System.err.println("Expected: </path/to/stockfish/exe> <fen> <depth>");
            System.exit(1);
        }
        testAgainstStockfish();
    }

    public static void testAgainstStockfish() throws Exception {
        outer: while(perftDepth > 0) {
            var spotResults = getSpotPerftData();
            var sfResults = getStockfishPerftData();

            // We check if stockfish has more moves than spot.
            if (sfResults.size() > spotResults.size()) {
                String notFound = sfResults.keySet().stream()
                        .filter(k -> !k.equals("Nodes searched"))
                        .filter(k -> spotResults.get(k) == null)
                        .collect(Collectors.joining(", "));
                if(notFound.length() > 0) {
                    printErrorAndExit("Moves not found : " + notFound);
                } else {
                    printErrorAndExit("Unexpected error");
                }
            }
            // We compare Spot moves with Stockfish.
            for (String move : spotResults.keySet()) {
                if (move.length() < 4) {
                    continue;
                }

                // Comparing results.
                var spotResult = spotResults.get(move);
                var sfResult = sfResults.get(move);
                if (Objects.equals(spotResult, sfResult)) {
                    continue;
                }

                // The results are not the same.
                if (spotResult <= 0) {
                    printErrorAndExit("Move With " + spotResult + " Outcomes : " + move);
                }
                if (null == sfResult) {
                    printErrorAndExit("Unexpected Move : " + move);
                }
                System.err.println("Mismatch in position : " + perftFen);

                // We figure the offending move out and make it.
                Position currentPos = parser.getGame(perftFen).getCurrentPosition();
                MoveProcessor moveProcessor = new MoveProcessor(moveBuffer);

                int moveCount = moveProcessor.addMovesToBuffer(currentPos, 0);
                boolean madeMove = false;
                for (int i = 0; i < moveCount; i++) {
                    Square squareFrom = new Square(MoveInitUtil.getFrom(moveBuffer[i]));
                    Square squareTo = new Square(MoveInitUtil.getTo(moveBuffer[i]));
                    if ((squareFrom.toString() + squareTo).equals(move)) {
                        currentPos.makeMove(moveBuffer[i]);
                        moveChain.add(moveBuffer[i]);
                        moveNameChain.add(move);
                        madeMove = true;
                        break;
                    }
                }

                if (!madeMove) {
                    printErrorAndExit("Unexpected error");
                }
                perftFen = parser.getNotation(new Game(currentPos));
                perftDepth--;
                continue outer;
            }
            System.out.println("No discrepancy found!");
            break;
        }
    }

    private static Map<String, Long> getSpotPerftData() {
        var baOut = new ByteArrayOutputStream();
        var defaultOut = System.out;
        System.setOut(new PrintStream(baOut));

        Perft.perftRunner(parser.getGame(perftFen).getCurrentPosition(), perftDepth, true);

        Map<String, Long> results = Arrays.stream(baOut.toString().split("\\n"))
                .map(x -> x.replace("\r",""))
                .filter(l -> !l.isEmpty())
                .collect(
                        Collectors.toMap(
                                x -> x.split(":")[0],
                                x -> Long.parseLong(x.split(":")[1].trim())
                        )
                );
        System.setOut(defaultOut);

        return results;
    }

    private static Map<String, Long> getStockfishPerftData() throws Exception {
        ProcessBuilder sfRunner = new ProcessBuilder(pathToStockfishExe);
        Process sf = sfRunner.start();

        List<String> result;
        try(var sfInput = sf.outputWriter(StandardCharsets.UTF_8);
            var sfWriter = new Scanner(sf.inputReader())) {
            readTill(sfWriter, "Stockfish developers", 10);

            sfInput.write("position fen " + perftFen + "\n"); sfInput.flush();
            sfInput.write("go perft " + perftDepth + "\n"); sfInput.flush();

            result = readTill(sfWriter, "Nodes searched", 120);
        }
        sf.destroy();

        return result.stream().collect(
                Collectors.toMap(
                        x -> x.split(":")[0],
                        x -> Long.parseLong(x.split(":")[1].trim())
                )
        );
    }

    private static List<String> readTill(Scanner sfWriter, String match, long timeLimitS) throws Exception {
        var result = CompletableFuture.supplyAsync(() -> {
            var sfOutput = new ArrayList<String>();
            while (sfWriter.hasNext()) {
                String line = sfWriter.nextLine();
                if (!line.isEmpty()) {
                    sfOutput.add(line);
                    if (line.contains(match)) {
                        return sfOutput;
                    }
                }
            }
            return sfOutput;
        });
       return result.get(timeLimitS, TimeUnit.SECONDS);
    }

    private static void printErrorAndExit(String error) {
        System.err.println(error + "\n" + perftFen);
        System.err.println("Move History : " + String.join(", ", moveNameChain));
        System.err.println("Move Integer : " +
                moveChain.stream().map(x -> Integer.toString(x)).collect(Collectors.joining(", ")));
        System.err.println();

        PositionPrinter.printPosition(parser.getGame(perftFen).getCurrentPosition());
        System.exit(1);
    }
}
