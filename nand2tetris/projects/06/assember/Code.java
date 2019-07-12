/**
 * A class containing static methods to convert ASM C-Instruction mneumonics into bits
 */
public class Code {
    /**
     * A static method that converts a dest string to bits (e.g. D -> ["0", "1", "0"])
     *
     * @param dest a destination string (e.g. null, M, D, MD, AMD)
     * @return a string array containing the 3 bits associated with dest, left-to-right
     */
    public static String[] destBits(String dest) {
        
    }
    /**
     * A static method that converts a comp string to bits (e.g. D+1 -> ["0", "1", "1", "1", "1", "1"])
     *
     * @param comp a computation string (e.g. 0, -1, D, D+A)
     * @return a string array containing the 7 bits associated with comp, left-to-right
     */
    public static String[] compBits(String comp) {
        
    }
    /**
     * A static method that converts a jump string to bits (e.g. JGE -> ["0", "1", "1"])
     *
     * @param jump a jump instruction string (e.g. null, JGT, JNE, JMP)
     * @return a string array containing the 3 bits associated with jump, left-to-right
     */
    public static String[] jumpBits(String jump) {
        
    }
}