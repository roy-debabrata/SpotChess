package com.debabrata.spotchess.magics;

public class BitPositionUtil {
    private static final long deBruijnMagic = 0x0218A3A4DAB967EFL;
    private static final int [] magicTranslate = {
            0,  1,  2,  7,  3, 13,  8, 19,
            4, 27, 14, 45,  9, 30, 20, 51,
            5, 17, 25, 28, 15, 37, 46, 39,
            10, 48, 34, 31, 41, 21, 59, 52,
            63,  6, 12, 18, 26, 44, 29, 50,
            16, 24, 36, 38, 47, 33, 40, 58,
            62, 11, 43, 49, 23, 35, 32, 57,
            61, 42, 22, 56, 60, 55, 54, 53,
    };

    public static int getBitPlaceValue(long singleBit){
        return magicTranslate[(int)((singleBit * deBruijnMagic) >>> 58)];
    }

    public static int getLastBitPlaceValue(long board){
        return magicTranslate[(int)(((board & -board) * deBruijnMagic) >>> 58)];
    }
}
