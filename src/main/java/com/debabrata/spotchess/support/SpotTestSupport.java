package com.debabrata.spotchess.support;

import com.debabrata.spotchess.console.PositionPrinter;
import com.debabrata.spotchess.support.notation.move.StandardAlgebraicNotation;
import com.debabrata.spotchess.types.Position;
import com.debabrata.spotchess.types.enums.Colour;
import com.debabrata.spotchess.types.enums.PieceType;
import com.debabrata.spotchess.utils.MoveUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SpotTestSupport {
    public static final Square a1= new Square(7);
    public static final Square b1= new Square(6);
    public static final Square c1= new Square(5);
    public static final Square d1= new Square(4);
    public static final Square e1= new Square(3);
    public static final Square f1= new Square(2);
    public static final Square g1= new Square(1);
    public static final Square h1= new Square(0);
    public static final Square a2= new Square(15);
    public static final Square b2= new Square(14);
    public static final Square c2= new Square(13);
    public static final Square d2= new Square(12);
    public static final Square e2= new Square(11);
    public static final Square f2= new Square(10);
    public static final Square g2= new Square(9);
    public static final Square h2= new Square(8);
    public static final Square a3= new Square(23);
    public static final Square b3= new Square(22);
    public static final Square c3= new Square(21);
    public static final Square d3= new Square(20);
    public static final Square e3= new Square(19);
    public static final Square f3= new Square(18);
    public static final Square g3= new Square(17);
    public static final Square h3= new Square(16);
    public static final Square a4= new Square(31);
    public static final Square b4= new Square(30);
    public static final Square c4= new Square(29);
    public static final Square d4= new Square(28);
    public static final Square e4= new Square(27);
    public static final Square f4= new Square(26);
    public static final Square g4= new Square(25);
    public static final Square h4= new Square(24);
    public static final Square a5= new Square(39);
    public static final Square b5= new Square(38);
    public static final Square c5= new Square(37);
    public static final Square d5= new Square(36);
    public static final Square e5= new Square(35);
    public static final Square f5= new Square(34);
    public static final Square g5= new Square(33);
    public static final Square h5= new Square(32);
    public static final Square a6= new Square(47);
    public static final Square b6= new Square(46);
    public static final Square c6= new Square(45);
    public static final Square d6= new Square(44);
    public static final Square e6= new Square(43);
    public static final Square f6= new Square(42);
    public static final Square g6= new Square(41);
    public static final Square h6= new Square(40);
    public static final Square a7= new Square(55);
    public static final Square b7= new Square(54);
    public static final Square c7= new Square(53);
    public static final Square d7= new Square(52);
    public static final Square e7= new Square(51);
    public static final Square f7= new Square(50);
    public static final Square g7= new Square(49);
    public static final Square h7= new Square(48);
    public static final Square a8= new Square(63);
    public static final Square b8= new Square(62);
    public static final Square c8= new Square(61);
    public static final Square d8= new Square(60);
    public static final Square e8= new Square(59);
    public static final Square f8= new Square(58);
    public static final Square g8= new Square(57);
    public static final Square h8= new Square(56);

    public static Position position(WhitePiecePositions whitePieces, BlackPiecePositions blackPieces, Colour toMove) {
        Position position = new Position();
        for (PiecePositions piecePosition : whitePieces.piecePositions) {
            for (Square square : piecePosition.squares) {
                boolean success = position.addPiece(Colour.WHITE, piecePosition.pieceType, square.placeValue);
                assert success;
            }
        }
        for (PiecePositions piecePosition : blackPieces.piecePositions) {
            for (Square square : piecePosition.squares) {
                boolean success = position.addPiece(Colour.BLACK, piecePosition.pieceType, square.placeValue);
                assert success;
            }
        }
        assert null != toMove;
        if (toMove == Colour.BLACK) {
            position.toggleWhiteToMove();
        }
        /* Check if the castling flags make sense. */
        if (position.getPieceType(e1.placeValue) != PieceType.KING || position.getPieceColour(e1.placeValue) != Colour.WHITE) {
            position.kingMoved(true);
        } else {
            if (position.getPieceType(a1.placeValue) != PieceType.ROOK || position.getPieceColour(a1.placeValue) != Colour.WHITE) {
                position.leftRookMoved(true);
            }
            if (position.getPieceType(h1.placeValue) != PieceType.ROOK || position.getPieceColour(h1.placeValue) != Colour.WHITE) {
                position.rightRookMoved(true);
            }
        }
        if (position.getPieceType(e8.placeValue) != PieceType.KING || position.getPieceColour(e8.placeValue) != Colour.BLACK) {
            position.kingMoved(false);
        } else {
            if (position.getPieceType(a8.placeValue) != PieceType.ROOK || position.getPieceColour(a8.placeValue) != Colour.BLACK) {
                position.leftRookMoved(false);
            }
            if (position.getPieceType(h8.placeValue) != PieceType.ROOK || position.getPieceColour(h8.placeValue) != Colour.BLACK) {
                position.rightRookMoved(false);
            }
        }
        assert position.checkSanity();
        return position;
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
                .filter(i -> Arrays.stream(positions).noneMatch(j -> j.placeValue == i))
                .mapToObj(Square::new)
                .toArray(Square[]::new);
    }

    public static Stream<Square> squaresStream(IntStream positions) {
        return positions.mapToObj(Square::new);
    }

    public static class Square {
        public final int placeValue;
        Square(int placeValue) {
            this.placeValue = placeValue;
        }
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

    /**
     * @param position position in which the move needs to be made.
     * @param moves standard algebraic notation of the move moves to be made separated by spaces.
     * @return true if it's a legal move and was successfully made. False otherwise.
     */
    public static boolean move(Position position, String moves) {
        assert null != position && null != moves;
        String[] movesList = moves.split("\\s+");

        StandardAlgebraicNotation sanProcessor = new StandardAlgebraicNotation();

        for (String move : movesList) {
            int moveInt = sanProcessor.getMove(position, move);
            if (moveInt == 0) {
                return false;
            }
            position.makeMove(moveInt);
        }
        return true;
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
        if (MoveUtil.getMovesInPosition(position).size() > 0) {
            throw new AssertionError("Has legal moves. Expected stalemate.");
        }
        if(MoveUtil.isKingUnderCheck(position)) {
            throw new AssertionError("King is under check. Expected stalemate.");
        }
    }

    public static void assertCheckmate(Position position) {
        if (MoveUtil.getMovesInPosition(position).size() > 0) {
            throw new AssertionError("Has legal moves. Expected checkmate.");
        }
        if(!MoveUtil.isKingUnderCheck(position)) {
            throw new AssertionError("King is not under check. Expected checkmate.");
        }
    }

    public static void assertHasMoves(Position position, String expectedMoves) {
        assert null != expectedMoves && null != position && position.checkSanity();

        List<String> expectedMovesList = Arrays.asList(expectedMoves.split("\\s+"));
        List<String> actualMoves = getMoveNotationList(position, MoveUtil.getMovesInPosition(position));

        compareMoves(actualMoves, expectedMovesList);
    }

    public static void assertOnlyMoves(Position position, String expectedMoves) {
        assert null != expectedMoves && null != position && position.checkSanity();

        List<String> expectedMovesList = Arrays.asList(expectedMoves.split("\\s+"));
        List<String> actualMoves = getMoveNotationList(position, MoveUtil.getMovesInPosition(position));

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
        assert null !=position && null != movesNotExpected && position.checkSanity();
        String[] unexpectedMovesArr = movesNotExpected.split("\\s+");

        List<Integer> moveIntList = MoveUtil.getMovesInPosition(position);

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
        StandardAlgebraicNotation sanProcessor = new StandardAlgebraicNotation();
        List<String> moves = new ArrayList<>();
        for (int moveInt : moveIntList) {
            String move = sanProcessor.getNotation(position, moveInt);
            moves.add(move);
        }
        return moves;
    }
}
