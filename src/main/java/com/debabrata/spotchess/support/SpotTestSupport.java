package com.debabrata.spotchess.support;

import com.debabrata.spotchess.console.PositionPrinter;
import com.debabrata.spotchess.exception.InvalidPositionException;
import com.debabrata.spotchess.logic.MoveProcessor;
import com.debabrata.spotchess.support.notation.move.SANParser;
import com.debabrata.spotchess.types.Position;
import com.debabrata.spotchess.types.Square;
import com.debabrata.spotchess.types.enums.Colour;
import com.debabrata.spotchess.types.enums.PieceType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SpotTestSupport {
    public static final Square a1= new Square("a1");
    public static final Square a2= new Square("a2");
    public static final Square a3= new Square("a3");
    public static final Square a4= new Square("a4");
    public static final Square a5= new Square("a5");
    public static final Square a6= new Square("a6");
    public static final Square a7= new Square("a7");
    public static final Square a8= new Square("a8");
    public static final Square b1= new Square("b1");
    public static final Square b2= new Square("b2");
    public static final Square b3= new Square("b3");
    public static final Square b4= new Square("b4");
    public static final Square b5= new Square("b5");
    public static final Square b6= new Square("b6");
    public static final Square b7= new Square("b7");
    public static final Square b8= new Square("b8");
    public static final Square c1= new Square("c1");
    public static final Square c2= new Square("c2");
    public static final Square c3= new Square("c3");
    public static final Square c4= new Square("c4");
    public static final Square c5= new Square("c5");
    public static final Square c6= new Square("c6");
    public static final Square c7= new Square("c7");
    public static final Square c8= new Square("c8");
    public static final Square d1= new Square("d1");
    public static final Square d2= new Square("d2");
    public static final Square d3= new Square("d3");
    public static final Square d4= new Square("d4");
    public static final Square d5= new Square("d5");
    public static final Square d6= new Square("d6");
    public static final Square d7= new Square("d7");
    public static final Square d8= new Square("d8");
    public static final Square e1= new Square("e1");
    public static final Square e2= new Square("e2");
    public static final Square e3= new Square("e3");
    public static final Square e4= new Square("e4");
    public static final Square e5= new Square("e5");
    public static final Square e6= new Square("e6");
    public static final Square e7= new Square("e7");
    public static final Square e8= new Square("e8");
    public static final Square f1= new Square("f1");
    public static final Square f2= new Square("f2");
    public static final Square f3= new Square("f3");
    public static final Square f4= new Square("f4");
    public static final Square f5= new Square("f5");
    public static final Square f6= new Square("f6");
    public static final Square f7= new Square("f7");
    public static final Square f8= new Square("f8");
    public static final Square g1= new Square("g1");
    public static final Square g2= new Square("g2");
    public static final Square g3= new Square("g3");
    public static final Square g4= new Square("g4");
    public static final Square g5= new Square("g5");
    public static final Square g6= new Square("g6");
    public static final Square g7= new Square("g7");
    public static final Square g8= new Square("g8");
    public static final Square h1= new Square("h1");
    public static final Square h2= new Square("h2");
    public static final Square h3= new Square("h3");
    public static final Square h4= new Square("h4");
    public static final Square h5= new Square("h5");
    public static final Square h6= new Square("h6");
    public static final Square h7= new Square("h7");
    public static final Square h8= new Square("h8");

    public static Position position(WhitePiecePositions whitePieces, BlackPiecePositions blackPieces, Colour toMove) {
        assert null != whitePieces && null != blackPieces && null != toMove;
        boolean whiteKingNotAtE1 = true;
        boolean whiteRookNotAtA1 = true;
        boolean whiteRookNotAtH1 = true;
        boolean blackKingNotAtE8 = true;
        boolean blackRookNotAtA8 = true;
        boolean blackRookNotAtH8 = true;
        Position.Builder positionBuilder = new Position.Builder();
        for (PiecePositions piecePosition : whitePieces.piecePositions) {
            for (Square square : piecePosition.squares) {
                if (piecePosition.pieceType == PieceType.KING) {
                    if (square.equals(e1)) {
                        whiteKingNotAtE1 = false;
                    }
                }
                if (piecePosition.pieceType == PieceType.ROOK) {
                    if (square.equals(a1)) {
                        whiteRookNotAtA1 = false;
                    } else if (square.equals(h1)) {
                        whiteRookNotAtH1 = false;
                    }
                }
                positionBuilder.withPiece(Colour.WHITE, piecePosition.pieceType, square);
            }
        }
        for (PiecePositions piecePosition : blackPieces.piecePositions) {
            for (Square square : piecePosition.squares) {
                positionBuilder.withPiece(Colour.BLACK, piecePosition.pieceType, square);
                if (piecePosition.pieceType == PieceType.KING) {
                    if (square.equals(e8)) {
                        blackKingNotAtE8 = false;
                    }
                }
                if (piecePosition.pieceType == PieceType.ROOK) {
                    if (square.equals(a8)) {
                        blackRookNotAtA8 = false;
                    } else if (square.equals(h8)) {
                        blackRookNotAtH8 = false;
                    }
                }
            }
        }
        positionBuilder.toMove(toMove);
        /* Check if the castling flags make sense. */
        if (whiteKingNotAtE1) {
            positionBuilder.kingMoved(Colour.WHITE);
        } else {
            if (whiteRookNotAtA1) {
                positionBuilder.leftRookMoved(Colour.WHITE);
            }
            if (whiteRookNotAtH1) {
                positionBuilder.rightRookMoved(Colour.WHITE);
            }
        }
        if (blackKingNotAtE8) {
            positionBuilder.kingMoved(Colour.BLACK);
        } else {
            if (blackRookNotAtA8) {
                positionBuilder.leftRookMoved(Colour.BLACK);
            }
            if (blackRookNotAtH8) {
                positionBuilder.rightRookMoved(Colour.BLACK);
            }
        }
        try {
            return positionBuilder.build();
        } catch (InvalidPositionException e) {
            throw new AssertionError(e.getMessage(), e);
        }
    }

    public static Position position(Position position,  PieceRemover pieceRemover) {
        assert null != position && null != pieceRemover;
        Position.Builder positionBuilder = new Position.Builder(position);
        for (Square square:pieceRemover.squares) {
            positionBuilder.withoutPiece(square);
        }
        try {
            return positionBuilder.build();
        } catch (InvalidPositionException e) {
            throw new AssertionError(e.getMessage(), e);
        }
    }

    public static WhitePiecePositions white(PiecePositions ... piecePositions){
        return new WhitePiecePositions(piecePositions);
    }

    public static BlackPiecePositions black(PiecePositions ... piecePositions){
        return new BlackPiecePositions(piecePositions);
    }

    public static PiecePositions k(Square position) {
        return new PiecePositions(PieceType.KING, new Square[] {position});
    }

    public static PiecePositions q(Square ... positions) {
        return new PiecePositions(PieceType.QUEEN, positions);
    }

    public static PiecePositions r(Square ... positions) {
        return new PiecePositions(PieceType.ROOK, positions);
    }

    public static PiecePositions b(Square ... positions) {
        return new PiecePositions(PieceType.BISHOP, positions);
    }

    public static PiecePositions n(Square ... positions) {
        return new PiecePositions(PieceType.KNIGHT, positions);
    }

    public static PiecePositions p(Square ... positions) {
        return new PiecePositions(PieceType.PAWN, positions);
    }

    public static Square[] except(Square ... positions) {
        return IntStream.range(0,64)
                .filter(i -> Arrays.stream(positions).noneMatch(j -> j.getValue() == i))
                .mapToObj(Square::new)
                .toArray(Square[]::new);
    }

    public static PieceRemover withoutPieceAt(Square ... positions) {
        return new PieceRemover(positions);
    }

    public static Stream<Square> squaresStream(IntStream positions) {
        return positions.mapToObj(Square::new);
    }


    public static class PiecePositions {
        final PieceType pieceType;
        final Square [] squares;
        private PiecePositions(PieceType pieceType, Square [] squares) {
            this.pieceType = pieceType;
            this.squares = squares;
        }
    }

    public static class WhitePiecePositions {
        final PiecePositions [] piecePositions;
        private WhitePiecePositions(PiecePositions [] piecePositions) {
            this.piecePositions = piecePositions;
        }
    }

    public static class BlackPiecePositions {
        final PiecePositions [] piecePositions;
        private BlackPiecePositions(PiecePositions [] piecePositions) {
            this.piecePositions = piecePositions;
        }
    }

    public static class PieceRemover {
        final Square [] squares;
        private PieceRemover(Square [] squares) {
            this.squares = squares;
        }
    }

    /**
     * @param position position in which the move needs to be made.
     * @param moves standard algebraic notation of the move moves to be made separated by spaces.
     * @return true if it's a legal move and was successfully made. False otherwise.
     */
    public static boolean move(Position position, String moves) {
        assert null != position && null != moves;
        String[] movesList = moves.split("\\s+");

        SANParser sanParser = new SANParser();

        for (String move : movesList) {
            int moveInt = sanParser.getMove(position, move);
            if (moveInt == 0) {
                return false;
            }
            position.makeMove(moveInt);
        }
        return true;
    }

    public static int moveAndGetReverseMove(Position position, String move) {
        assert null != position && null != move;

        SANParser sanParser = new SANParser();
        int moveInt = sanParser.getMove(position, move);

        return position.makeMove(moveInt);
    }


    public static void assertEquals(Position expectedPosition, Position actualPosition) {
        if (expectedPosition == actualPosition) {
            return; /* They are the same object, or they are both null. */
        }
        if (actualPosition == null) {
            throw new AssertionError("Actual position is null, expected is not.");
        }
        if (expectedPosition == null) {
            throw new AssertionError("Expected position is null, actual is not.");
        }
        boolean piecesAreDifferent =
                expectedPosition.getWhitePieces() != actualPosition.getWhitePieces() ||
                expectedPosition.getBlackPieces() != actualPosition.getBlackPieces() ||
                expectedPosition.getPawnsAndKnights()  != actualPosition.getPawnsAndKnights() ||
                expectedPosition.getKnightsAndKings()  != actualPosition.getKnightsAndKings() ||
                expectedPosition.getRooksAndQueens()   != actualPosition.getRooksAndQueens()  ||
                expectedPosition.getQueensAndBishops() != actualPosition.getQueensAndBishops();
        if (piecesAreDifferent) {
            String expectedBoard = PositionPrinter.getConsolePrintableBoard(expectedPosition, true, true);
            String actualBoard = PositionPrinter.getConsolePrintableBoard(actualPosition, true, true);
            System.out.println("Expected: \n" + expectedBoard + "\n\nActual:\n" + actualBoard);
            throw new AssertionError("Actual and expected have different piece positions.");
        }
    }

    public static void assertStrictlyEquals(Position expectedPosition, Position actualPosition) {
        assertEquals(expectedPosition, actualPosition);
        if (expectedPosition.getFlags() == actualPosition.getFlags()) {
            return;
        }
        List<String> flagInfoExpected = PositionPrinter.getAdditionalStateInfo(expectedPosition);
        List<String> flagInfoActual = PositionPrinter.getAdditionalStateInfo(actualPosition);

        StringBuilder message = new StringBuilder();
        for (int i = 0; i < flagInfoExpected.size(); i++) {
            if (! flagInfoExpected.get(i).equals(flagInfoActual.get(i))) {
                if (message.length() == 0){
                    message.append(flagInfoExpected.get(i)).append(" vs ").append(flagInfoActual.get(i));
                } else {
                    message.append("; ").append(flagInfoExpected.get(i)).append(" vs ").append(flagInfoActual.get(i));
                }
            }
        }

        if (message.length() > 0) {
            throw new AssertionError(message.toString());
        }
    }

    public static void assertStalemate(Position position) {
        if (MoveProcessor.getMovesInPosition(position).size() > 0) {
            throw new AssertionError("Moves found. Expected stalemate.");
        }
        if(MoveProcessor.isKingUnderCheck(position)) {
            throw new AssertionError("King is under check. Expected stalemate.");
        }
    }

    public static void assertCheckmate(Position position) {
        if (MoveProcessor.getMovesInPosition(position).size() > 0) {
            throw new AssertionError("Moves found. Expected checkmate.");
        }
        if(!MoveProcessor.isKingUnderCheck(position)) {
            throw new AssertionError("King is not under check. Expected checkmate.");
        }
    }

    public static void assertHasMoves(Position position, String expectedMoves) {
        assert null != expectedMoves && null != position && position.validate();

        List<String> expectedMovesList = Arrays.asList(expectedMoves.split("\\s+"));
        List<String> actualMoves = getMoveNotationList(position, MoveProcessor.getMovesInPosition(position));

        compareMoves(actualMoves, expectedMovesList);
    }

    public static void assertOnlyMoves(Position position, String expectedMoves) {
        assert null != expectedMoves && null != position && position.validate();

        List<String> expectedMovesList = Arrays.asList(expectedMoves.split("\\s+"));
        List<String> actualMoves = getMoveNotationList(position, MoveProcessor.getMovesInPosition(position));

        if (actualMoves.size() > expectedMovesList.size()) {
            throw new AssertionError("More moves than expected.");
        }

        compareMoves(actualMoves, expectedMovesList);
    }

    private static void compareMoves(List<String> actualMoves, List<String> expectedMoves) {
        StringBuilder notFound = null;
        for(String move: expectedMoves) {
            boolean match = actualMoves.contains(move);
            if (!match) {
                if (notFound == null) {
                    notFound = new StringBuilder(move);
                } else {
                    notFound.append(" ").append(move);
                }
            }
        }
        if (notFound != null) {
            throw new AssertionError("The following moves did not match - " + notFound);
        }
    }

    public static void assertDoesNotHaveMoves(Position position, String movesNotExpected) {
        assert null !=position && null != movesNotExpected && position.validate();
        String[] unexpectedMovesArr = movesNotExpected.split("\\s+");

        List<Integer> moveIntList = MoveProcessor.getMovesInPosition(position);

        List<String> moves = getMoveNotationList(position, moveIntList);
        StringBuilder found = null;
        for(String move: unexpectedMovesArr) {
            boolean match = moves.contains(move);
            if (match) {
                if (found == null) {
                    found = new StringBuilder(move);
                } else {
                    found.append(" ").append(move);
                }
            }
        }
        if (found != null) {
            throw new AssertionError("The following matched but were not expected to match - " + found);
        }
    }

    private static List<String> getMoveNotationList(Position position, List<Integer> moveIntList) {
        SANParser sanParser = new SANParser();
        List<String> moves = new ArrayList<>();
        for (int moveInt : moveIntList) {
            String move = sanParser.getNotation(position, moveInt);
            moves.add(move);
        }
        return moves;
    }
}