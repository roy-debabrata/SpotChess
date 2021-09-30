package com.debabrata.spotchess.utils;

import com.debabrata.spotchess.types.enums.PieceType;

public class RookAndBishopMovesUtil {

    public static final long [] rookMask =   {0x000101010101017eL, 0x000202020202027cL, 0x000404040404047aL, 0x0008080808080876L, 0x001010101010106eL, 0x002020202020205eL, 0x004040404040403eL, 0x008080808080807eL, 0x0001010101017e00L, 0x0002020202027c00L, 0x0004040404047a00L, 0x0008080808087600L, 0x0010101010106e00L, 0x0020202020205e00L, 0x0040404040403e00L, 0x0080808080807e00L, 0x00010101017e0100L, 0x00020202027c0200L, 0x00040404047a0400L, 0x0008080808760800L, 0x00101010106e1000L, 0x00202020205e2000L, 0x00404040403e4000L, 0x00808080807e8000L, 0x000101017e010100L, 0x000202027c020200L, 0x000404047a040400L, 0x0008080876080800L, 0x001010106e101000L, 0x002020205e202000L, 0x004040403e404000L, 0x008080807e808000L, 0x0001017e01010100L, 0x0002027c02020200L, 0x0004047a04040400L, 0x0008087608080800L, 0x0010106e10101000L, 0x0020205e20202000L, 0x0040403e40404000L, 0x0080807e80808000L, 0x00017e0101010100L, 0x00027c0202020200L, 0x00047a0404040400L, 0x0008760808080800L, 0x00106e1010101000L, 0x00205e2020202000L, 0x00403e4040404000L, 0x00807e8080808000L, 0x007e010101010100L, 0x007c020202020200L, 0x007a040404040400L, 0x0076080808080800L, 0x006e101010101000L, 0x005e202020202000L, 0x003e404040404000L, 0x007e808080808000L, 0x7e01010101010100L, 0x7c02020202020200L, 0x7a04040404040400L, 0x7608080808080800L, 0x6e10101010101000L, 0x5e20202020202000L, 0x3e40404040404000L, 0x7e80808080808000L};
    public static final long [] bishopMask = {0x0040201008040200L, 0x0000402010080400L, 0x0000004020100a00L, 0x0000000040221400L, 0x0000000002442800L, 0x0000000204085000L, 0x0000020408102000L, 0x0002040810204000L, 0x0020100804020000L, 0x0040201008040000L, 0x00004020100a0000L, 0x0000004022140000L, 0x0000000244280000L, 0x0000020408500000L, 0x0002040810200000L, 0x0004081020400000L, 0x0010080402000200L, 0x0020100804000400L, 0x004020100a000a00L, 0x0000402214001400L, 0x0000024428002800L, 0x0002040850005000L, 0x0004081020002000L, 0x0008102040004000L, 0x0008040200020400L, 0x0010080400040800L, 0x0020100a000a1000L, 0x0040221400142200L, 0x0002442800284400L, 0x0004085000500800L, 0x0008102000201000L, 0x0010204000402000L, 0x0004020002040800L, 0x0008040004081000L, 0x00100a000a102000L, 0x0022140014224000L, 0x0044280028440200L, 0x0008500050080400L, 0x0010200020100800L, 0x0020400040201000L, 0x0002000204081000L, 0x0004000408102000L, 0x000a000a10204000L, 0x0014001422400000L, 0x0028002844020000L, 0x0050005008040200L, 0x0020002010080400L, 0x0040004020100800L, 0x0000020408102000L, 0x0000040810204000L, 0x00000a1020400000L, 0x0000142240000000L, 0x0000284402000000L, 0x0000500804020000L, 0x0000201008040200L, 0x0000402010080400L, 0x0002040810204000L, 0x0004081020400000L, 0x000a102040000000L, 0x0014224000000000L, 0x0028440200000000L, 0x0050080402000000L, 0x0020100804020000L, 0x0040201008040200L};
    public static final long [] rookMagic =    {0x0480002081104000L, 0x0240004010086000L, 0x0480200082191000L, 0x1880100080440800L, 0x2100050002104800L, 0x0200090810040600L, 0x0100008402000100L, 0x0600020081440021L, 0x4000800090400020L, 0x0804400050002000L, 0x0412003020820042L, 0x0002002200400810L, 0x8012808084000800L, 0x0108808004000600L, 0x500300C100260004L, 0x0000801100104080L, 0x00AA928004204002L, 0x0A70024020004000L, 0x1301010040126000L, 0x2004808010020804L, 0x0221010012080004L, 0x084101000804000EL, 0x2E80808046000100L, 0x1000160004228051L, 0x2002C00080098030L, 0x0200200080400584L, 0x1103100080200080L, 0x0030001100490120L, 0x1003004500100800L, 0x4200040080800600L, 0x00013A0400281009L, 0x0010108200064409L, 0x004000A848800880L, 0x0401400081802000L, 0x0024200080801000L, 0x0034411062000A00L, 0x0202080101000411L, 0x1A00810200800400L, 0x1118080114001012L, 0x2000124402000081L, 0x0020904000208000L, 0x0A20005004A0C000L, 0x08220020B0820040L, 0x48220B00D0010020L, 0x0028000C01808028L, 0x048A000824020050L, 0x8102180221840010L, 0x210C1C8449020004L, 0x008000A440098080L, 0x0380804008210100L, 0x8400100081600080L, 0x0000210088500500L, 0x2402040008008280L, 0x108080A400820080L, 0x0000801100060080L, 0x0A8300E285140200L, 0x202084C200110322L, 0x4401006042118602L, 0x010200600A108042L, 0x0021001000052009L, 0x0201001410080023L, 0x0502001081040802L, 0x0000320490280104L, 0x0000022400411882L};
    public static final long [] bishopMagic =  {0x942001020800408CL, 0x0662080200AE0008L, 0x0421281888800100L, 0x02609201400C0600L, 0x0025104108401009L, 0x0886021004082C00L, 0x0005080202205860L, 0x00C3004800880800L, 0x5004080224480202L, 0x080B429202020200L, 0x024C084801002000L, 0x0200144101201214L, 0x0020040420016000L, 0x68000228200808A0L, 0x8800006808341006L, 0x060A0600D1045004L, 0x2010604004280084L, 0x2004012008020240L, 0x0108401004044870L, 0x1208030404200800L, 0x0002800400A02008L, 0x0200800808030800L, 0x0A82041100A22010L, 0x405C8800240C0200L, 0x8208400220420204L, 0x0001900004040800L, 0x0102080082480440L, 0x2280404104010200L, 0x08008C0080802002L, 0x0114046004100408L, 0x8118840002220260L, 0x0002202030840104L, 0x105220608050A204L, 0x0806101000040100L, 0x1C10304808040800L, 0x40004008A1020200L, 0x0250060600102008L, 0x0001100100282404L, 0x8048180888150882L, 0x8020810148160200L, 0x4802016018082000L, 0x0110A08808022011L, 0x000200104804A400L, 0x0660004050400200L, 0x0010080100400400L, 0x0140080081000020L, 0x0208020440500401L, 0x0244582081080022L, 0x2009080210043010L, 0xA000820105200000L, 0x8400A60201048208L, 0x0004808021880182L, 0x000006502A020000L, 0x0000400801410810L, 0x004202480A108024L, 0x1050020804408188L, 0x40020080C8080450L, 0x800020210C0A2100L, 0x0050088314010404L, 0x0250040028940410L, 0x0102001088902400L, 0x00107010A0019100L, 0x0814400208810301L, 0xAB40042802002020L};

    public static long [][] rookAttacksCache = new long[64][];
    public static long [][] bishopAttacksCache = new long[64][];

    public static int [] rookShift = {
            52,53,53,53,53,53,53,52,53,54,54,54,54,54,54,53,
            53,54,54,54,54,54,54,53,53,54,54,54,54,54,54,53,
            53,54,54,54,54,54,54,53,53,54,54,54,54,54,54,53,
            53,54,54,54,54,54,54,53,52,53,53,53,53,53,53,52
    };

    public static int [] bishopShift = {
            58,59,59,59,59,59,59,58,59,59,59,59,59,59,59,59,
            59,59,57,57,57,57,59,59,59,59,57,55,55,57,59,59,
            59,59,57,55,55,57,59,59,59,59,57,57,57,57,59,59,
            59,59,59,59,59,59,59,59,58,59,59,59,59,59,59,58
    };

    public static long getRookMoves(int placeValue, long boardPosition) {
        int index = (int)(((boardPosition & rookMask[placeValue]) * rookMagic[placeValue]) >>> rookShift[placeValue]);
        return rookAttacksCache[placeValue][index];
    }

    public static long getBishopMoves(int placeValue, long boardPosition) {
        int index = (int)(((boardPosition & bishopMask[placeValue]) * bishopMagic[placeValue]) >>> bishopShift[placeValue]);
        return bishopAttacksCache[placeValue][index];
    }

    public static void setupCaches() {
        setupCache(PieceType.ROOK, rookMask, rookMagic, rookShift, rookAttacksCache);
        setupCache(PieceType.BISHOP, bishopMask, bishopMagic, bishopShift, bishopAttacksCache);
    }

    private static void setupCache( PieceType piece, long [] masks, long [] magics, int[] shift, long [][] targetCache ){
        for ( int i = 0; i < 64; i ++ ) {
            long[] pieceCombinations = getAllPossiblePieceCombinations(masks[i]);
            long[] moveCombinations = getAllPossibleMovesCombinations(piece, i, pieceCombinations);

            /* This could be potentially smaller if we figured out what the last used position is and store it somewhere
            *  but right now we are content with what we have. */
            targetCache[i] = new long[1 << (64 - shift[i])];

            for ( int j = 0; j < pieceCombinations.length; j ++ ) {
                int index = (int)((pieceCombinations[j] * magics[i]) >>> shift[i]);
                if ( targetCache[i][index] != 0 && targetCache[i][index] != moveCombinations[j]) {
                    /*  We have the code so that we could recover from this by simply finding a new key, but I'd rather
                     *  the code blew up on my face than do a resource consuming recovery and on a user's computer.
                     *  By the way what we just found out are two moves that are not the same that map to the same
                     *  location in the cache so the magic number is faulty. */
                    throw new RuntimeException("Broken CacheKey for rook at position " + i);
                }
                targetCache[i][index] = moveCombinations[j];
            }
        }
    }

    public static long[] getAllPossibleMovesCombinations(PieceType pieceType, int placeValue, long[] pieceCombinations){
        long [] moves  = new long[pieceCombinations.length];
        for ( int i = 0; i < pieceCombinations.length; i++ ){
            moves[i] = getMoves(pieceType, placeValue, pieceCombinations[i]);
        }
        return moves;
    }

    /**
     * Generate all possible combination of piece placements on the bit positions marked by the preMask.
     * Counting up from 0 and using least significant n bits as unique combinations, we map each of those bits to a bit
     * on the mask. Using an int to count up we can map up to 32 bits which is more than enough for our purposes.
     * No single piece attack as many positions.
     *
     * @param preMask gives the region for which to generate all possible combinations of values.
     *                Must not contain more than 32 active bits.
     * @return an array containing bitboards of all combinations of bits of the preMask. */
    public static long[] getAllPossiblePieceCombinations(long preMask){
        int bitCount = Long.bitCount(preMask);
        int [] bitToPositionMapping = new int[bitCount];
        int i = 0;
        while ( preMask != 0 ){
            bitToPositionMapping[i++] = BitPositionUtil.getLastBitPlaceValue(preMask);
            preMask = preMask & (preMask - 1);
        }
        int comboCount = 1 << bitCount;
        long [] possibleBoardPositions = new long[comboCount];
        for ( int permutation = 0; permutation < comboCount; permutation ++){
            long newBoardPosition = 0;
            int selector = 1;
            for ( int position : bitToPositionMapping ){
                if ((selector & permutation) != 0){
                    newBoardPosition = newBoardPosition | (1L << position);
                }
                selector = selector << 1;
            }
            possibleBoardPositions[permutation] = newBoardPosition;
        }
        return possibleBoardPositions;
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
        throw new RuntimeException("Check: " + placeValue + ", " + pieceCombinations + ", " + shift + ", " + rayEdgeMask);
    }

    public static long getPieceMask(PieceType pieceType, int placeValue){
        if ( pieceType == PieceType.ROOK ) {
            return getRookMask(placeValue);
        }
        return getBishopMask(placeValue);
    }

    public static long getRookMask(int placeValue){
        long fileH = 0x0101010101010101L;
        long rank0 = 0x00000000000000FFL;
        long fileHWithoutEdges = 0x0001010101010100L;
        long rank0WithoutEdges = 0x000000000000007EL;
        long border = 0xFF818181818181FFL;
        int file = placeValue % 8;
        int rank = placeValue / 8;
        long mask = ((fileH << file ) ^ (rank0 << (rank * 8)));
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

    public static long getBishopMask(int placeValue){
        long position = 1L << placeValue;
        long mask = 0;
        long leftDiagonalSeed  = 0x80;
        long rightDiagonalSeed = 0x01;
        long border = 0xFF818181818181FFL;
        long leftDiagonalSelector = leftDiagonalSeed;
        long rightDiagonalSelector = rightDiagonalSeed;
        for ( int i = 0; i < 15; i++ ){
            if ((leftDiagonalSelector & position) != 0 ){
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
        return mask & ~border;
    }
}