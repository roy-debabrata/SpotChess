package com.debabrata.spotchess.types;

import com.debabrata.spotchess.utils.BitUtil;

import java.util.Objects;

public class Square {
    final int placeValue;
    final String name;

    public Square(int placeValue) {
        if (placeValue >= 0 && placeValue < 64) {
            this.placeValue = placeValue;
            this.name = String.valueOf((char) ('h' - (placeValue % 8))) + ((placeValue / 8) + 1);
        } else {
            this.placeValue = -1;
            this.name = "Invalid";
        }
    }

    public Square (long bitPosition) {
        this(BitUtil.getLastBitPlaceValue(bitPosition));
    }

    public Square(String name) {
        if (null != name && name.length() == 2) {
            char file = name.charAt(0);
            char rank = name.charAt(1);
            if (rank >= '1' && rank <= 'h' && file >= 'a' && file <= 'h') {
                this.placeValue = (rank - '1') * 8 + ('h' - file);
                this.name = name;
                return;
            }
        }
        this.placeValue = -1;
        this.name = "Invalid";
    }

    public int getValue() {
        return placeValue;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Square square = (Square) o;
        return placeValue == square.placeValue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(placeValue);
    }
}