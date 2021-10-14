package com.debabrata.spotchess.support;

import com.debabrata.spotchess.types.Position;
import com.debabrata.spotchess.types.enums.Colour;
import com.debabrata.spotchess.types.enums.PieceType;

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
                position.addPiece(Colour.WHITE, piecePosition.pieceType, square.placeValue);
            }
        }
        for (PiecePositions piecePosition : blackPieces.piecePositions) {
            for (Square square : piecePosition.squares) {
                position.addPiece(Colour.BLACK, piecePosition.pieceType, square.placeValue);
            }
        }
        assert null != toMove;
        if (toMove == Colour.BLACK) {
            position.toggleWhiteToMove();
        }
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

    public static class Square {
        final int placeValue;
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
}
