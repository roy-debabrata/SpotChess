package com.debabrata.spotchess.generators;

import java.util.Scanner;

/**
 * This is not a generic implementation of a for B(k,n) with k symbols with permutations of n length within
 * De Bruijn sequence of k^n length. This is for bitboards so, B(2, 6) so we can have 64 digit binary numbers.
 *
 * I have also of late and quiet painfully, I must mention, realized that you need to begin with 0. This is because
 * our shortcut of choice (multiply by single bit long to get unique new numbers) doesn't rotate the De Bruijn Sequence
 * but left shifts it. The problem with that is that the last numbers are necessarily trailed by additional zeros.
 * This can create a lot of conflict scenarios in the last 6 digits.
 *
 * Contemplate on "(longContainingSingleBit * deBruijnMagic) >>> 58)" you'll get what I mean in the last para.
 */
public class DeBruijnSequenceGenerator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the De Bruijn Sequence generator!");

        System.out.println("How many combinations to skip? This is so you can have your own number. (Max: 67108864) ");
        do {
            findSequenceNumber = scanner.nextInt();
        } while (findSequenceNumber < 1 || findSequenceNumber > 67108864);
        populateLinks();
        boolean found = sequenceSearch(0, 0, 64);
        if (found) {
            long magic = getAsLong(0);
            validateAndPrintAssociatedArray(magic);
        }
    }

    /**
     * How this program works:
     * ----------------------------------------------------------------------------------------------------------------
     * We use array index as stand in for the bits in the finished sequence. So 0b001001 denotes 9th index but also the
     * sequence of digits "001001" in the 64 bit DeBruijn sequence. We can look for neighbours to either side of this
     * sequence. We have arbitrarily chosen right. So the only two possible neighbours/indexes we can reach from 9 -
     * "001001" are "010010" or "010011" i.e. 18 or 19. Since DeBruijn numbers are essentially circular in nature what
     * we are trying to do is find a circuit through this sequence that ends up at the node that we started with
     * (basically an Euler Circuit).
     *
     * In the array "links"
     * row 0 and 1 give possible neighbours to an index. 0 being the neighbour we get by adding a 0 and 1 by adding a 1.
     * row 2 is for the index of the neighbour currently under consideration for finding the circuit. -1 is default.
     */
    private static int[][] links = new int[3][64];
    private static int sequencesFound = 0;
    private static int findSequenceNumber;

    private static void populateLinks() {
        for (int i = 0; i < 64; i++) {
            links[0][i] = (i << 1) & 62;
            links[1][i] = ((i << 1) & 62) + 1;
            links[2][i] = -1;
        }
    }

    private static boolean sequenceSearch(int firstLink, int currentLink, int remainingNodes) {
        if (links[2][currentLink] >= 0)
            return false; /* Link has been used before */
        if (remainingNodes == 1) {
            /* This is the last number */
            if (firstLink != links[0][currentLink] && firstLink != links[1][currentLink]) {
                /* This last number doesn't loop back to the firstLink. Circuit not found.
                 *
                 * Since we have hard coded 0 to be our starting point we can get rid of this check and reduce the depth
                 * of the search to 56 and it should work just fine. We'll need to make sure that the last number in the
                 * link is odd (otherwise there will be a 7 digit sequence of 0s). That should make the process about 32
                 * times faster and it's a neat trick. But since generating the magic is a one time activity and because
                 * the program is fast enough even without the trick I'm leaving this in. Also, I find it appealing that
                 * the concept of circularity of DeBruijn sequences is emphasised this way. */
                return false;
            }
            return findSequenceNumber == ++sequencesFound;
        } else {
            links[2][currentLink] = links[0][currentLink]; /* We try the first potential neighbour. */
            boolean found = sequenceSearch(firstLink, links[0][currentLink], remainingNodes - 1);
            if (found) {
                return true;
            }
            links[2][currentLink] = links[1][currentLink]; /* We try the second potential neighbour. */
            found = sequenceSearch(firstLink, links[1][currentLink], remainingNodes - 1);
            if (found) {
                return true;
            }
            links[2][currentLink] = -1;
        }
        return false;
    }

    private static long getAsLong(int firstLink) {
        StringBuilder magicNumber = new StringBuilder();
        magicNumber.append(Integer.toBinaryString(firstLink));
        int positionPointer = links[2][firstLink];
        for (int i = 0; i < 64 - 6; i++) {
            magicNumber.append(positionPointer % 2 == 0 ? "0" : "1");
            if (positionPointer >= 0) {
                positionPointer = links[2][positionPointer];
            }
        }
        return Long.parseLong(magicNumber.toString(), 2);
    }

    private static int[] getDeBruijnSequenceTranslator(long magic) {
        int [] resultArray = new int[64];
        for (long lsob = 1L, count = 0; lsob != 0; lsob = lsob << 1, count++) {
            int pos = (int) ((lsob * magic) >>> 58);
            if (resultArray[pos] != 0) {
                System.out.println("CLASH! This really shouldn't have happened. Investigate: " +
                        Long.toHexString(magic) + " #" + resultArray[pos] + "," + count);
                return null;
            }
            resultArray[pos] = (int) count;
        }
        return resultArray;
    }

    private static void validateAndPrintAssociatedArray(long magic) {
        int [] resultArray = getDeBruijnSequenceTranslator(magic);
        if ( null == resultArray ){
            System.out.println("Writing this mostly because I hate being warned by my IDE on possible NPE.");
            return;
        }

        System.out.printf("Our Magic De Bruijn Sequence = 0x%016XL \n", magic);
        int count = 0;
        System.out.println("int [] magicTranslate = { ");
        for (int x : resultArray) {
            count++;
            System.out.printf("%3d,", x);
            if (count % 8 == 0) {
                System.out.println();
            }
        }
        System.out.println("};");
    }
}
