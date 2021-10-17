package com.debabrata.spotchess.console;

public class ConsoleDisplaySettings {
    private static boolean SHOW_BITBOARDS = true;
    private static boolean SHOW_ADDITIONAL_INFO = true;
    private static boolean SWITCH_PIECE_ICON_COLOURS = true;
    private static boolean USE_UNICODE_CHESS_PIECES = true;

    public static boolean showBitboards() {
        return SHOW_BITBOARDS;
    }

    public static void setShowBitboards(boolean showBitboards) {
        SHOW_BITBOARDS = showBitboards;
    }

    public static boolean showAdditionalInfo() {
        return SHOW_ADDITIONAL_INFO;
    }

    public static void setShowAdditionalInfo(boolean showAdditionalInfo) {
        SHOW_ADDITIONAL_INFO = showAdditionalInfo;
    }

    public static boolean switchPieceIconColours() {
        return SWITCH_PIECE_ICON_COLOURS;
    }

    public static void setSwitchPieceIconColours(boolean switchPieceIconColours) {
        SWITCH_PIECE_ICON_COLOURS = switchPieceIconColours;
    }

    public static boolean useUnicodeChessPieces() {
        return USE_UNICODE_CHESS_PIECES;
    }

    public static void setUseUnicodeChessPieces(boolean useUnicodeChessPieces) {
        USE_UNICODE_CHESS_PIECES = useUnicodeChessPieces;
    }
}
