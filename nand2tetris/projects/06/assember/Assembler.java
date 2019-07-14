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
        BufferedWriter bw = null;
        try {
            in = new Scanner(new FileReader(args[0]));
            parser = new Parser(in);
            bw = new BufferedWriter(new FileWriter(filename + ".hack"));
            while(parser.hasMoreCommands()) {
                parser.advance();
                Parser.CommandType currCommType = parser.commandType();
                String line = null;
                if (currCommType == Parser.CommandType.A_COMMAND) {
                    int value = Integer.parseInt(parser.symbol());
                    String binVal = Integer.toBinaryString(value);
                    String leftZeros = "0";
                    for (int i = 0; i < 15 - binVal.length(); i++) {
                        leftZeros += "0";
                    }
                    line = leftZeros + binVal;
                } else { // C-Command
                    line = "111";
                    String[] compBits = Code.compBits(parser.comp());
                    String[] destBits = Code.destBits(parser.dest());
                    String[] jumpBits = Code.jumpBits(parser.jump());
                    line += Arrays.stream(compBits).collect(Collectors.joining());
                    line += Arrays.stream(destBits).collect(Collectors.joining());
                    line += Arrays.stream(jumpBits).collect(Collectors.joining());
                }
                System.out.println(line);
                bw.write(line);
                bw.newLine();
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
}