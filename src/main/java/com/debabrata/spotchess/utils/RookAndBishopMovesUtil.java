package com.debabrata.spotchess.utils;

import com.debabrata.spotchess.types.enums.PieceType;

import java.util.Arrays;

public class RookAndBishopMovesUtil {

    private static final long [] rookMask =       {0x000101010101017eL, 0x000202020202027cL, 0x000404040404047aL, 0x0008080808080876L, 0x001010101010106eL, 0x002020202020205eL, 0x004040404040403eL, 0x008080808080807eL, 0x0001010101017e00L, 0x0002020202027c00L, 0x0004040404047a00L, 0x0008080808087600L, 0x0010101010106e00L, 0x0020202020205e00L, 0x0040404040403e00L, 0x0080808080807e00L, 0x00010101017e0100L, 0x00020202027c0200L, 0x00040404047a0400L, 0x0008080808760800L, 0x00101010106e1000L, 0x00202020205e2000L, 0x00404040403e4000L, 0x00808080807e8000L, 0x000101017e010100L, 0x000202027c020200L, 0x000404047a040400L, 0x0008080876080800L, 0x001010106e101000L, 0x002020205e202000L, 0x004040403e404000L, 0x008080807e808000L, 0x0001017e01010100L, 0x0002027c02020200L, 0x0004047a04040400L, 0x0008087608080800L, 0x0010106e10101000L, 0x0020205e20202000L, 0x0040403e40404000L, 0x0080807e80808000L, 0x00017e0101010100L, 0x00027c0202020200L, 0x00047a0404040400L, 0x0008760808080800L, 0x00106e1010101000L, 0x00205e2020202000L, 0x00403e4040404000L, 0x00807e8080808000L, 0x007e010101010100L, 0x007c020202020200L, 0x007a040404040400L, 0x0076080808080800L, 0x006e101010101000L, 0x005e202020202000L, 0x003e404040404000L, 0x007e808080808000L, 0x7e01010101010100L, 0x7c02020202020200L, 0x7a04040404040400L, 0x7608080808080800L, 0x6e10101010101000L, 0x5e20202020202000L, 0x3e40404040404000L, 0x7e80808080808000L};
    private static final long [] rookSemiMask =   {0x0101010101010101L, 0x0202020202020202L, 0x0404040404040404L, 0x0808080808080808L, 0x1010101010101010L, 0x2020202020202020L, 0x4040404040404040L, 0x8080808080808080L, 0x0101010101010101L, 0x0202020202020202L, 0x0404040404040404L, 0x0808080808080808L, 0x1010101010101010L, 0x2020202020202020L, 0x4040404040404040L, 0x8080808080808080L, 0x0101010101010101L, 0x0202020202020202L, 0x0404040404040404L, 0x0808080808080808L, 0x1010101010101010L, 0x2020202020202020L, 0x4040404040404040L, 0x8080808080808080L, 0x0101010101010101L, 0x0202020202020202L, 0x0404040404040404L, 0x0808080808080808L, 0x1010101010101010L, 0x2020202020202020L, 0x4040404040404040L, 0x8080808080808080L, 0x0101010101010101L, 0x0202020202020202L, 0x0404040404040404L, 0x0808080808080808L, 0x1010101010101010L, 0x2020202020202020L, 0x4040404040404040L, 0x8080808080808080L, 0x0101010101010101L, 0x0202020202020202L, 0x0404040404040404L, 0x0808080808080808L, 0x1010101010101010L, 0x2020202020202020L, 0x4040404040404040L, 0x8080808080808080L, 0x0101010101010101L, 0x0202020202020202L, 0x0404040404040404L, 0x0808080808080808L, 0x1010101010101010L, 0x2020202020202020L, 0x4040404040404040L, 0x8080808080808080L, 0x0101010101010101L, 0x0202020202020202L, 0x0404040404040404L, 0x0808080808080808L, 0x1010101010101010L, 0x2020202020202020L, 0x4040404040404040L, 0x8080808080808080L};
    private static final long [] rookFullMask=    {0x01010101010101FEL, 0x02020202020202FDL, 0x04040404040404FBL, 0x08080808080808F7L, 0x10101010101010EFL, 0x20202020202020DFL, 0x40404040404040BFL, 0x808080808080807FL, 0x010101010101FE01L, 0x020202020202FD02L, 0x040404040404FB04L, 0x080808080808F708L, 0x101010101010EF10L, 0x202020202020DF20L, 0x404040404040BF40L, 0x8080808080807F80L, 0x0101010101FE0101L, 0x0202020202FD0202L, 0x0404040404FB0404L, 0x0808080808F70808L, 0x1010101010EF1010L, 0x2020202020DF2020L, 0x4040404040BF4040L, 0x80808080807F8080L, 0x01010101FE010101L, 0x02020202FD020202L, 0x04040404FB040404L, 0x08080808F7080808L, 0x10101010EF101010L, 0x20202020DF202020L, 0x40404040BF404040L, 0x808080807F808080L, 0x010101FE01010101L, 0x020202FD02020202L, 0x040404FB04040404L, 0x080808F708080808L, 0x101010EF10101010L, 0x202020DF20202020L, 0x404040BF40404040L, 0x8080807F80808080L, 0x0101FE0101010101L, 0x0202FD0202020202L, 0x0404FB0404040404L, 0x0808F70808080808L, 0x1010EF1010101010L, 0x2020DF2020202020L, 0x4040BF4040404040L, 0x80807F8080808080L, 0x01FE010101010101L, 0x02FD020202020202L, 0x04FB040404040404L, 0x08F7080808080808L, 0x10EF101010101010L, 0x20DF202020202020L, 0x40BF404040404040L, 0x807F808080808080L, 0xFE01010101010101L, 0xFD02020202020202L, 0xFB04040404040404L, 0xF708080808080808L, 0xEF10101010101010L, 0xDF20202020202020L, 0xBF40404040404040L, 0x7F80808080808080L};
    private static final long [] bishopMask =     {0x0040201008040200L, 0x0000402010080400L, 0x0000004020100a00L, 0x0000000040221400L, 0x0000000002442800L, 0x0000000204085000L, 0x0000020408102000L, 0x0002040810204000L, 0x0020100804020000L, 0x0040201008040000L, 0x00004020100a0000L, 0x0000004022140000L, 0x0000000244280000L, 0x0000020408500000L, 0x0002040810200000L, 0x0004081020400000L, 0x0010080402000200L, 0x0020100804000400L, 0x004020100a000a00L, 0x0000402214001400L, 0x0000024428002800L, 0x0002040850005000L, 0x0004081020002000L, 0x0008102040004000L, 0x0008040200020400L, 0x0010080400040800L, 0x0020100a000a1000L, 0x0040221400142200L, 0x0002442800284400L, 0x0004085000500800L, 0x0008102000201000L, 0x0010204000402000L, 0x0004020002040800L, 0x0008040004081000L, 0x00100a000a102000L, 0x0022140014224000L, 0x0044280028440200L, 0x0008500050080400L, 0x0010200020100800L, 0x0020400040201000L, 0x0002000204081000L, 0x0004000408102000L, 0x000a000a10204000L, 0x0014001422400000L, 0x0028002844020000L, 0x0050005008040200L, 0x0020002010080400L, 0x0040004020100800L, 0x0000020408102000L, 0x0000040810204000L, 0x00000a1020400000L, 0x0000142240000000L, 0x0000284402000000L, 0x0000500804020000L, 0x0000201008040200L, 0x0000402010080400L, 0x0002040810204000L, 0x0004081020400000L, 0x000a102040000000L, 0x0014224000000000L, 0x0028440200000000L, 0x0050080402000000L, 0x0020100804020000L, 0x0040201008040200L};
    private static final long [] bishopSemiMask = {0x8040201008040201L, 0x0080402010080402L, 0x0000804020100804L, 0x0000008040201008L, 0x0000000080402010L, 0x0000000000804020L, 0x0000000000008040L, 0x0000000000000080L, 0x4020100804020100L, 0x8040201008040201L, 0x0080402010080402L, 0x0000804020100804L, 0x0000008040201008L, 0x0000000080402010L, 0x0000000000804020L, 0x0000000000008040L, 0x2010080402010000L, 0x4020100804020100L, 0x8040201008040201L, 0x0080402010080402L, 0x0000804020100804L, 0x0000008040201008L, 0x0000000080402010L, 0x0000000000804020L, 0x1008040201000000L, 0x2010080402010000L, 0x4020100804020100L, 0x8040201008040201L, 0x0080402010080402L, 0x0000804020100804L, 0x0000008040201008L, 0x0000000080402010L, 0x0804020100000000L, 0x1008040201000000L, 0x2010080402010000L, 0x4020100804020100L, 0x8040201008040201L, 0x0080402010080402L, 0x0000804020100804L, 0x0000008040201008L, 0x0402010000000000L, 0x0804020100000000L, 0x1008040201000000L, 0x2010080402010000L, 0x4020100804020100L, 0x8040201008040201L, 0x0080402010080402L, 0x0000804020100804L, 0x0201000000000000L, 0x0402010000000000L, 0x0804020100000000L, 0x1008040201000000L, 0x2010080402010000L, 0x4020100804020100L, 0x8040201008040201L, 0x0080402010080402L, 0x0100000000000000L, 0x0201000000000000L, 0x0402010000000000L, 0x0804020100000000L, 0x1008040201000000L, 0x2010080402010000L, 0x4020100804020100L, 0x8040201008040201L};
    private static final long [] bishopFullMask = {0x8040201008040200L, 0x0080402010080500L, 0x0000804020110A00L, 0x0000008041221400L, 0x0000000182442800L, 0x0000010204885000L, 0x000102040810A000L, 0x0102040810204000L, 0x4020100804020002L, 0x8040201008050005L, 0x00804020110A000AL, 0x0000804122140014L, 0x0000018244280028L, 0x0001020488500050L, 0x0102040810A000A0L, 0x0204081020400040L, 0x2010080402000204L, 0x4020100805000508L, 0x804020110A000A11L, 0x0080412214001422L, 0x0001824428002844L, 0x0102048850005088L, 0x02040810A000A010L, 0x0408102040004020L, 0x1008040200020408L, 0x2010080500050810L, 0x4020110A000A1120L, 0x8041221400142241L, 0x0182442800284482L, 0x0204885000508804L, 0x040810A000A01008L, 0x0810204000402010L, 0x0804020002040810L, 0x1008050005081020L, 0x20110A000A112040L, 0x4122140014224180L, 0x8244280028448201L, 0x0488500050880402L, 0x0810A000A0100804L, 0x1020400040201008L, 0x0402000204081020L, 0x0805000508102040L, 0x110A000A11204080L, 0x2214001422418000L, 0x4428002844820100L, 0x8850005088040201L, 0x10A000A010080402L, 0x2040004020100804L, 0x0200020408102040L, 0x0500050810204080L, 0x0A000A1120408000L, 0x1400142241800000L, 0x2800284482010000L, 0x5000508804020100L, 0xA000A01008040201L, 0x4000402010080402L, 0x0002040810204080L, 0x0005081020408000L, 0x000A112040800000L, 0x0014224180000000L, 0x0028448201000000L, 0x0050880402010000L, 0x00A0100804020100L, 0x0040201008040201L};

    private static final long [] rookMagic =      {0x0080002A10804004L, 0x0C4008C020003000L, 0x220011A201188040L, 0x008010008018000CL, 0x9480040080480002L, 0x0900284B00240002L, 0x110024020010C100L, 0x06801641A5000080L, 0xE000800080400020L, 0x0004804002200682L, 0x2881001045012000L, 0x0000800800500080L, 0x0202808004000800L, 0x0482000201044810L, 0x000C000450016208L, 0x8002000084004102L, 0x1009208000814002L, 0x0002C04009201000L, 0x0010008010802008L, 0x0021010020100008L, 0x0002020008211004L, 0x0000808002000400L, 0x0489040088031002L, 0x0800420000408403L, 0x4000400080042280L, 0x4002010200408020L, 0x0511004D00102000L, 0xA080080080801000L, 0x0048018080080400L, 0x800C000480020080L, 0x0012001200040308L, 0x112080008000E100L, 0x100020C008800081L, 0x0840003000600800L, 0x1008804202002010L, 0x0081051001000820L, 0x2008010165001028L, 0x0006008C9A001810L, 0x0002000802001415L, 0x6001006982000401L, 0x0F40004080A48002L, 0x003000402004C000L, 0x0421804600520020L, 0x2012022010420008L, 0x0140080100110004L, 0x0004010002004040L, 0x012186080D8C0010L, 0x4080004484060001L, 0x0000800040002080L, 0x400200508100A200L, 0x0020844510220200L, 0x0001002050004900L, 0x8111100800050100L, 0x00044A0080640080L, 0x8000210248100400L, 0x2400104401852200L, 0x0280410010800021L, 0xE002C08010630A02L, 0x4000402000190031L, 0xC00101100028600DL, 0x0902000410A0080AL, 0x0801004C00380601L, 0x8020220090080114L, 0x8040207644010082L};
    private static final long [] bishopMagic =    {0x01102208004400C0L, 0x0808053404064880L, 0x4108181110280020L, 0x0204105204008488L, 0x000110C000100300L, 0xC420C410400BA000L, 0x20408688A0100318L, 0x10036182080340C0L, 0x0032202016008300L, 0x0020A01C01020421L, 0x2821100088810200L, 0x2503842420800020L, 0x0200020210400EA0L, 0x0101009050080902L, 0x00000C0403094800L, 0x4000010110B22001L, 0x40110804A0880308L, 0x088680044408020AL, 0x0001000804C40084L, 0x4402000440330008L, 0x0004000094200902L, 0x0412012408840C30L, 0x0040401888041020L, 0x5302004082440642L, 0x0210084184481000L, 0x100450000A106110L, 0x01A8080011104300L, 0x1304040000401280L, 0x0009010008104000L, 0x4002120004104200L, 0x8088020000908400L, 0x8122084022010080L, 0x800802C800C10808L, 0x70050808014A3000L, 0x000044100A120020L, 0x0004200800810810L, 0x882800EC00404100L, 0x0201004202050100L, 0x1005040105840104L, 0x0C04090A01082184L, 0x0048320824002004L, 0x0002080114004808L, 0x1008908410000100L, 0x8001082018000102L, 0x2100400822802810L, 0x0008010800240200L, 0x2060210401000481L, 0x2048882180618180L, 0x8204680824100000L, 0x081045040B200890L, 0x0000020100880420L, 0x01800000C2022010L, 0x0880001002020002L, 0x2000240810130348L, 0x402CA00801010120L, 0x024C04040400A280L, 0x04008200E3200804L, 0x0200248082882108L, 0x0024284206822108L, 0x0000400001420220L, 0x0000004020164400L, 0x0033000410064210L, 0x1A100CA910040C80L, 0x00A078020800A120L};

    private static final int [] rookShift = {
            52,53,53,53,53,53,53,52,53,54,54,54,54,54,54,53,
            53,54,54,54,54,54,54,53,53,54,54,54,54,54,54,53,
            53,54,54,54,54,54,54,53,53,54,54,54,54,54,54,53,
            53,54,54,54,54,54,54,53,52,53,53,53,53,53,53,52
    };

    private static final int [] bishopShift = {
            58,59,59,59,59,59,59,58,59,59,59,59,59,59,59,59,
            59,59,57,57,57,57,59,59,59,59,57,55,55,57,59,59,
            59,59,57,55,55,57,59,59,59,59,57,57,57,57,59,59,
            59,59,59,59,59,59,59,59,58,59,59,59,59,59,59,58
    };

    private static final int maxRookShift   = 64 - Arrays.stream(rookShift).min().getAsInt();
    private static final int maxBishopShift = 64 - Arrays.stream(bishopShift).min().getAsInt();

    private static final long [] rookAttacksCache = new long[getCacheSize(maxRookShift)];
    private static final long [] rookPinsCache = new long[getCacheSize(maxRookShift)];
    private static final long [] bishopAttacksCache = new long[getCacheSize(maxBishopShift)];
    private static final long [] bishopPinsCache = new long[getCacheSize(maxBishopShift)];

    private static int cachedBishopIndex;
    private static int cachedRookIndex;

    static {
        /* Makes sense to do it here, all methods are static and use cases break if this call is missed before use.*/
        setupMoveTables();
    }

    private static int getCacheSize(int maxShift) {
        return 64 * (1 << maxShift);
    }

    private static void setupMoveTables() {
        setupMoveTable(PieceType.ROOK, rookMask, rookMagic, rookShift, maxRookShift, rookAttacksCache, rookPinsCache);
        setupMoveTable(PieceType.BISHOP, bishopMask, bishopMagic, bishopShift, maxBishopShift, bishopAttacksCache, bishopPinsCache);
    }

    private static void setupMoveTable(PieceType piece, long [] masks, long [] magics, int[] shift, int maxShift, long [] moveCache, long [] pinCache){
        for ( int i = 0; i < 64; i ++ ) {
            long[] pieceCombinations = BitUtil.getAllPossibleBitCombinations(masks[i]);
            long[] moveCombinations, pinCombinations;

            moveCombinations = getAllPossibleMovesCombinations(piece, i, pieceCombinations);
            pinCombinations = getAllPossiblePinCombinations(piece, i, pieceCombinations);

            for ( int j = 0; j < pieceCombinations.length; j ++ ) {
                int index = (int)((pieceCombinations[j] * magics[i]) >>> shift[i]) | i << maxShift ;
                if ((moveCache[index] != 0 && moveCache[index] != moveCombinations[j]) || (pinCache[index] != 0 && pinCache[index] != pinCombinations[j])) {
                    /*  We have the code so that we could recover from this by simply finding a new key, but I'd rather
                     *  the code blew up on my face than do a resource consuming recovery and on a user's computer.
                     *  By the way what we just found out are two moves that are not the same that map to the same
                     *  location in the cache so the magic number is faulty. */
                    throw new RuntimeException("Broken CacheKey for " + piece.name() + " at position " + i);
                }
                moveCache[index] = moveCombinations[j];
                pinCache[index] = pinCombinations[j];
            }
        }
    }

    /** Gives us one row/column for this place value. If we have a cross this helps us isolate them one at a time. */
    public static long getRookSemiMask(int placeValue) {
        return rookSemiMask[placeValue];
    }

    public static long getRookMask(int placeValue) {
        return rookFullMask[placeValue];
    }

    public static long getRookMoves(int placeValue, long boardPosition) {
        int index = (int)(((boardPosition & rookMask[placeValue]) * rookMagic[placeValue]) >>> rookShift[placeValue]);
        return rookAttacksCache[index | placeValue << maxRookShift];
    }

    /** This is basically nearest pair of pieces on all 4 sides of the king. Any of these four pairs can potentially be a pin. */
    public static long getRookPins(int placeValue, long boardPosition) {
        int index = (int)(((boardPosition & rookMask[placeValue]) * rookMagic[placeValue]) >>> rookShift[placeValue]);
        return rookPinsCache[index | placeValue << maxRookShift];
    }

    /** This is the same as getRookMoves except it caches the index. This is useful for king pin pair requests. */
    public static long getKingRookMoves(int placeValue, long boardPosition) {
        int index = (int)(((boardPosition & rookMask[placeValue]) * rookMagic[placeValue]) >>> rookShift[placeValue]);
        cachedRookIndex = index | placeValue << maxRookShift;
        return rookAttacksCache[cachedRookIndex];
    }

    /** Same as getRookPins but returns using cached index value. This only works if the previous call was to getRookMoves. */
    public static long getCachedRookPins() {
        return rookPinsCache[cachedRookIndex];
    }

    /** Gives us one diagonal for that place value. If we have a cross this helps us isolate them one at a time. */
    public static long getBishopSemiMask(int placeValue) {
        return bishopSemiMask[placeValue];
    }

    public static long getBishopMask(int placeValue) {
        return bishopFullMask[placeValue];
    }

    public static long getBishopMoves(int placeValue, long boardPosition) {
        int index = (int)(((boardPosition & bishopMask[placeValue]) * bishopMagic[placeValue]) >>> bishopShift[placeValue]);
        return bishopAttacksCache[index | placeValue << maxBishopShift];
    }

    /** This is basically nearest pair of pieces on all 4 sides of the king. Any of these four pairs can potentially be a pin. */
    public static long getBishopPins(int placeValue, long boardPosition) {
        int index = (int)(((boardPosition & bishopMask[placeValue]) * bishopMagic[placeValue]) >>> bishopShift[placeValue]);
        return bishopPinsCache[index | placeValue << maxBishopShift];
    }

    /** This is the same as getBishopMoves except it caches the index. This is useful for king pin pair requests. */
    public static long getKingBishopMoves(int placeValue, long boardPosition) {
        int index = (int)(((boardPosition & bishopMask[placeValue]) * bishopMagic[placeValue]) >>> bishopShift[placeValue]);
        cachedBishopIndex = index | placeValue << maxBishopShift;
        return bishopAttacksCache[cachedBishopIndex];
    }

    /** Same as getBishopPins but returns using cached index value. This only works if the previous call was to getKingBishopMoves. */
    public static long getCachedBishopPins() {
        return bishopPinsCache[cachedBishopIndex];
    }

    public static long[] getAllPossibleMovesCombinations(PieceType pieceType, int placeValue, long[] pieceCombinations){
        long [] moves  = new long[pieceCombinations.length];
        for ( int i = 0; i < pieceCombinations.length; i++ ){
            moves[i] = getMoves(pieceType, placeValue, pieceCombinations[i]);
        }
        return moves;
    }

    public static long[] getAllPossiblePinCombinations(PieceType pieceType, int placeValue, long[] pieceCombinations){
        long [] moves  = new long[pieceCombinations.length];
        for ( int i = 0; i < pieceCombinations.length; i++ ){
            moves[i] = getPins(pieceType, placeValue, pieceCombinations[i]);
        }
        return moves;
    }

    public static long getMoves(PieceType type, int placeValue, long pieceCombinations){
        long northMask = 0xFF00000000000000L;
        long eastMask  = 0x0101010101010101L;
        long southMask = 0x00000000000000FFL;
        long westMask  = 0x8080808080808080L;
        if ( type == PieceType.ROOK ){
            return rayFill( placeValue, pieceCombinations, 1, westMask)
                    ^ rayFill( placeValue, pieceCombinations, 8, northMask)
                    ^ rayFill( placeValue, pieceCombinations, -1, eastMask)
                    ^ rayFill( placeValue, pieceCombinations, -8, southMask);
        } else if ( type == PieceType.BISHOP ){
            return rayFill( placeValue, pieceCombinations, 9, northMask | westMask)
                    ^ rayFill( placeValue, pieceCombinations, 7, northMask | eastMask)
                    ^ rayFill( placeValue, pieceCombinations, -9, southMask | eastMask)
                    ^ rayFill( placeValue, pieceCombinations, -7, southMask | westMask);
        }
        return 0;
    }

    /* Works by shifting 1 bit at "placeValue" by "shift" bits (this essentially give the ray its direction) and
     * it keeps going till it hits either an edge defined by "rayEdgeMask" or a piece in the "pieceCombinations". If
     * correct edge mask is provided it will return from within the while loop. */
    public static long rayFill(int placeValue, long pieceCombinations, int shift, long rayEdgeMask){
        long moves = 0;
        long currentPosition = 1L << placeValue;
        while ( currentPosition != 0 ){
            if ((currentPosition & rayEdgeMask) != 0 || (currentPosition & pieceCombinations) != 0){
                moves = moves | currentPosition;
                return moves;
            }
            moves = moves | currentPosition;
            if ( shift < 0 ){
                currentPosition = currentPosition >>> (-shift);
            } else {
                currentPosition = currentPosition << shift;
            }
        }
        throw new RuntimeException("Check Ray: " + placeValue + ", " + pieceCombinations + ", " + shift + ", " + rayEdgeMask);
    }

    public static long getPins(PieceType type, int placeValue, long pieceCombinations) {
        long northMask = 0xFF00000000000000L;
        long eastMask  = 0x0101010101010101L;
        long southMask = 0x00000000000000FFL;
        long westMask  = 0x8080808080808080L;
        if ( type == PieceType.ROOK ){
            return pinSelect( placeValue, pieceCombinations, 1, westMask)
                    ^ pinSelect( placeValue, pieceCombinations, 8, northMask)
                    ^ pinSelect( placeValue, pieceCombinations, -1, eastMask)
                    ^ pinSelect( placeValue, pieceCombinations, -8, southMask);
        } else if ( type == PieceType.BISHOP ){
            return pinSelect( placeValue, pieceCombinations, 9, northMask | westMask)
                    ^ pinSelect( placeValue, pieceCombinations, 7, northMask | eastMask)
                    ^ pinSelect( placeValue, pieceCombinations, -9, southMask | eastMask)
                    ^ pinSelect( placeValue, pieceCombinations, -7, southMask | westMask);
        }
        return 0;
    }

    /* Works similar to how rayFill works. Just in this case we are interested in exactly the first two pieces in any
    *  'shift' direction. If there are less than two we just ignore it. */
    public static long pinSelect(int placeValue, long pieceCombinations, int shift, long rayEdgeMask){
        long pin = 0;
        long currentPosition = 1L << placeValue;
        while ( currentPosition != 0 ){
            if ((currentPosition & pieceCombinations) != 0){
                if (pin != 0) {
                    return pin | currentPosition;
                }
                pin = pin | currentPosition;
            } else if ((currentPosition & rayEdgeMask) != 0) {
                if (pin != 0) {
                    return pin | currentPosition;
                }
                return 0;
            }
            if ( shift < 0 ){
                currentPosition = currentPosition >>> (-shift);
            } else {
                currentPosition = currentPosition << shift;
            }
        }
        throw new RuntimeException("Check Pin: " + placeValue + ", " + pieceCombinations + ", " + shift + ", " + rayEdgeMask);
    }

    public static long getPieceMask(PieceType pieceType, MaskType maskType, int placeValue){
        if ( pieceType == PieceType.ROOK ) {
            return getRookMask(placeValue, maskType);
        }
        return getBishopMask(placeValue, maskType);
    }

    public static long getRookMask(int placeValue, MaskType maskType){
        long fileH = 0x0101010101010101L;
        long rank0 = 0x00000000000000FFL;
        long fileHWithoutEdges = 0x0001010101010100L;
        long rank0WithoutEdges = 0x000000000000007EL;
        long border = 0xFF818181818181FFL;
        int file = placeValue % 8;
        int rank = placeValue / 8;
        if (maskType == MaskType.SEMI_MASK) {
            return fileH << file;
        }
        long mask = ((fileH << file ) ^ (rank0 << (rank * 8)));
        if (maskType == MaskType.FULL_MASK) {
            return mask;
        }
        mask = mask & (~ border);
        if ( file == 0 || file == 7 ) {
            mask = mask | (fileHWithoutEdges << file);
        }
        if ( rank == 0 || rank == 7 ) {
            mask = mask | (rank0WithoutEdges << (rank * 8));
        }
        mask = mask & ~ (1L << placeValue);
        return mask;
    }

    public static long getBishopMask(int placeValue, MaskType maskType){
        long position = 1L << placeValue;
        long mask = 0;
        long leftDiagonalSeed  = 0x80;
        long rightDiagonalSeed = 0x01;
        long border = 0xFF818181818181FFL;
        long leftDiagonalSelector = leftDiagonalSeed;
        long rightDiagonalSelector = rightDiagonalSeed;
        for ( int i = 0; i < 15; i++ ){
            if ((leftDiagonalSelector & position) != 0 ){
                if (maskType == MaskType.SEMI_MASK) {
                    return leftDiagonalSelector;
                }
                mask ^= leftDiagonalSelector;
            }
            if ((rightDiagonalSelector & position) != 0 ){
                mask ^= rightDiagonalSelector;
            }
            leftDiagonalSeed = leftDiagonalSeed >>> 1;
            rightDiagonalSeed = (rightDiagonalSeed << 1) & 0xFF; /* To nullify after 7 shifts. */
            leftDiagonalSelector = (leftDiagonalSelector << 8) | leftDiagonalSeed;
            rightDiagonalSelector = (rightDiagonalSelector << 8) | rightDiagonalSeed;
        }
        if (maskType == MaskType.FULL_MASK) {
            return mask;
        }
        return mask & ~border;
    }

    public enum MaskType {
        MASK, SEMI_MASK, FULL_MASK
    }
}