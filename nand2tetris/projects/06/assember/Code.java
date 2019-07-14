import java.util.Arrays;
/**
 * A class containing static methods to convert ASM C-Instruction mneumonics into bits
 */
public class Code {
    /**
     * A 2d int array where each entry COMP_BITS[i] holds the bits for ZERO_COMPS[i] and ONE_COMPS[i]
     */
    private static final int[][] COMP_BITS = {
        {1, 0, 1, 0, 1, 0}, {1, 1, 1, 1, 1, 1}, {1, 1, 1, 0, 1, 0}, {0, 0, 1, 1, 0, 0},
        {1, 1, 0, 0, 0, 0}, {0, 0, 1, 1, 0, 1}, {1, 1, 0, 0, 0, 1}, {0, 0, 1, 1, 1, 1},
        {1, 1, 0, 0, 1, 1}, {0, 1, 1, 1, 1, 1}, {1, 1, 0, 1, 1, 1}, {0, 0, 1, 1, 1, 0},
        {1, 1, 0, 0, 1, 0}, {0, 0, 0, 0, 1, 0}, {0, 1, 0, 0, 1, 1}, {0, 0, 0, 1, 1, 1},
        {0, 0, 0, 0, 0, 0}, {0, 1, 0, 1, 0, 1}
    };
    /**
     * A 2d int array where each entry JUMP_BITS[i] holds the bits for JUMP_STRS[i]
     */
    private static final int[][] JUMP_BITS = {
        {0, 0, 0}, {0, 0, 1}, {0, 1, 0}, {0, 1, 1}, 
        {1, 0, 0}, {1, 0, 1}, {1, 1, 0}, {1, 1, 1}
    };
    /**
     * A string array holding all 8 possible jump code strings
     */
    private static final String[] JUMP_STRS = {
        "null", "JGT", "JEQ", "JGE", "JLT", "JNE", "JLE", "JMP"
    };
    /**
     * A String array holding all 18 possible comp strings for which a = 0
     */
    private static final String[] ZERO_COMPS = {
        "0", "1", "-1", "D", "A", "!D", "!A", "-D", "-A", "D+1", "A+1", "D-1", "A-1",
        "D+A", "D-A", "A-D", "D&A", "D|A"
    };
    /**
     * A String array holding all 10 possible comp strings for which a = 1,
     * with empty strings added so that ONE_COMPS[i] still corresponds to COMP_BITS[i]
     */
    private static final String[] ONE_COMPS = {
        "", "", "", "", "M", "", "!M", "", "-M", "", "M+1", "", "M-1", "D+M", "D-M", 
        "M-D", "D&M", "D|M"
    };
    
    /**
     * A static method that converts a dest string to bits (e.g. D -> ["0", "1", "0"])
     *
     * @param dest a destination string (e.g. null, M, D, MD, AMD)
     * @return a string array containing the 3 bits associated with dest, left-to-right
     */
    public static String[] destBits(String dest) {
        String[] bits = new String[3];
        bits[0] = dest.contains("A")? "1" : "0";
        bits[1] = dest.contains("D")? "1" : "0";
        bits[2] = dest.contains("M")? "1" : "0";
        return bits;
    }
    /**
     * A static method that converts a comp string to bits (e.g. D+1 -> ["0", "0", "1", "1", "1", "1", "1"])
     *
     * @param comp a computation string (e.g. 0, -1, D, D+A)
     * @return a string array containing the 7 bits associated with comp, left-to-right
     */
    public static String[] compBits(String comp) {
        String[] bits = new String[7];
        if(Arrays.asList(ZERO_COMPS).contains(comp)) {
            /* the A bit is 0 */
            bits[0] = "0";
            int[] cmpBits = COMP_BITS[arrIndexOf(ZERO_COMPS, comp)];
            for (int i = 1; i < bits.length; i++) {
                bits[i] = "" + cmpBits[i-1];
            }
        } else {
            /* the A bit is 1 */
            bits[0] = "1";
            int[] cmpBits = COMP_BITS[arrIndexOf(ONE_COMPS, comp)];
            for (int i = 1; i < bits.length; i++) {
                bits[i] = "" + cmpBits[i-1];
            }
        }
        return bits;
    }
    /**
     * A static method that converts a jump string to bits (e.g. JGE -> ["0", "1", "1"])
     *
     * @param jump a jump instruction string (e.g. null, JGT, JNE, JMP)
     * @return a string array containing the 3 bits associated with jump, left-to-right
     */
    public static String[] jumpBits(String jump) {
        int jIndex = arrIndexOf(JUMP_STRS, jump);
        int[] jBits = JUMP_BITS[jIndex];
        String[] jmpBits = new String[3];
        for (int i = 0; i < jBits.length; i++) {
            jmpBits[i] = "" + jBits[i];
        }
        return jmpBits;
    }
    /**
     * A private static method that returns the index of string s in String[] arr
     *
     * @param arr A String array
     * @param s A string
     *
     * @return the index of s in arr, or -1 if arr does not contain s
     */
    private static int arrIndexOf(String[] arr, String s) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(s)) {
                return i;
            }
        }
        return -1; // shouldn't get here
    }
}