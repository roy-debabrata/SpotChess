package com.debabrata.spotchess.types;

public enum Colour {
    WHITE(0), BLACK(1);

    private int asNumber;

    Colour(int asNumber) {
        this.asNumber = asNumber;
    }
}
