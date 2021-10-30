package com.debabrata.spotchess.constants;

public class TypeConstants {

    /* The following are used to signify which piece to restore pieces to in case of an un-do move. */
    public static final int ROOK_TAKEN   = 0x01000000;  // 0001
    public static final int BISHOP_TAKEN = 0x02000000;  // 0010
    public static final int QUEEN_TAKEN  = 0x03000000;  // 0011
    public static final int KNIGHT_TAKEN = 0x04000000;  // 0100
    public static final int PAWN_TAKEN   = 0x08000000;  // 1000

    public static final int TAKEN_MASK   = 0x0F000000;  // 1000
}
