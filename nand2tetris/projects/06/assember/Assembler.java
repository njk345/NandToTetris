import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.Scanner;
/**
 * A class to represent the top-level Assembler program
 */
public class Assembler {
    /**
     * The main function:
     *     Takes in the name of an assembly file (Prog.asm), parses it, and outputs
     *     its translation into binary into a new file (Prog.hack).
     * 
     * @param args command-line arguments: args[0] is the name of the inputted ASM file
     */
    public static void main(String[] args) {
        String filename = args[0].substring(0, args[0].indexOf("."));
        Scanner in = null;
        Parser parser = null;
        SymbolTable st = new SymbolTable();
        BufferedWriter bw = null;
        int romCounter;
        int varCounter;
        
        try {
            // FIRST PASS
            in = new Scanner(new FileReader(args[0]));
            parser = new Parser(in);
            romCounter = 0;
            
            /* Go through the ASM file and add all labels and their
               ROM addresses to the SymbolTable */
            while(parser.hasMoreCommands()) {
                parser.advance();
                Parser.CommandType currCommType = parser.commandType();
                if (currCommType == Parser.CommandType.L_COMMAND) {
                    String symbol = parser.symbol();
                    st.addEntry(symbol, romCounter);
                } else {
                    romCounter++;
                }
            }
            
            // SECOND PASS
            in = new Scanner(new FileReader(args[0]));
            parser = new Parser(in);
            bw = new BufferedWriter(new FileWriter(filename + ".hack"));
            varCounter = 16;
    
            while(parser.hasMoreCommands()) {
                parser.advance();
                Parser.CommandType currCommType = parser.commandType();
                String line = null;
                
                if (currCommType == Parser.CommandType.A_COMMAND) {
                    int value;
                    String symbol = parser.symbol();
                    if (st.contains(symbol)) { // symbol either variable we've seen or a label
                        value = st.getAddress(symbol);
                    } else {
                        if(isNumber(symbol)) { // symbol is an integer constant
                            value = Integer.parseInt(symbol);
                        } else { // symbol is a new variable
                            st.addEntry(symbol, varCounter);
                            value = varCounter;
                            varCounter++;
                        }
                    }
                    
                    String binaryValue = Integer.toBinaryString(value);
                    String leftZeros = "0";
                    for (int i = 0; i < 15 - binaryValue.length(); i++) {
                        leftZeros += "0";
                    }
                    line = leftZeros + binaryValue;
                } else if(currCommType == Parser.CommandType.C_COMMAND) {
                    line = "111";
                    String[] compBits = Code.compBits(parser.comp());
                    String[] destBits = Code.destBits(parser.dest());
                    String[] jumpBits = Code.jumpBits(parser.jump());
                    line += Arrays.stream(compBits).collect(Collectors.joining());
                    line += Arrays.stream(destBits).collect(Collectors.joining());
                    line += Arrays.stream(jumpBits).collect(Collectors.joining());
                }
                if(currCommType != Parser.CommandType.L_COMMAND) {
                    bw.write(line);
                    bw.newLine();
                }
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if(in != null) {
                    in.close();
                }
                if(bw != null) {
                    bw.close();
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * A method to determine whether a string can be parsed as an integer
     *
     * @param s A string
     *
     * @return True, if s represents an integer, false if not
     */
    private static boolean isNumber(String s) {
        try {
            int n = Integer.parseInt(s);
            return true;
        } catch(NumberFormatException nfe) {
            return false;
        }
    }
}