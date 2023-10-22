package com.debabrata.spotchess.settings;

import com.debabrata.spotchess.support.notation.move.MoveNotation;

public class EngineSettings {

    private static int threadCount = 1;
    private static int ttTableSizeMb = 0;
    private static MoveNotation moveFormat = MoveNotation.UCI;

    public static int getThreadCount() {
        return threadCount;
    }

    public static void setThreadCount(int threadCount) {
        EngineSettings.threadCount = threadCount;
    }

    public static int getTtTableSizeMb() {
        return ttTableSizeMb;
    }

    public static void setTtTableSizeMb(int ttTableSizeMb) {
        EngineSettings.ttTableSizeMb = ttTableSizeMb;
    }

    public static MoveNotation getMoveFormat() {
        return moveFormat;
    }

    public static void setMoveFormat(MoveNotation moveFormat) {
        EngineSettings.moveFormat = moveFormat;
    }
}
