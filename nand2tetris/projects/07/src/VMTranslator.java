import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class VMTranslator {
    public static void main(String[] args) {
        try {
            Scanner in = new Scanner(new FileReader(args[0]));
            String filePrefix = args[0].substring(0, args[0].indexOf(".vm"));
            String outFilename = filePrefix + ".asm";
            BufferedWriter out = new BufferedWriter(new FileWriter(outFilename));
            Parser parser = new Parser(in);
            CodeWriter codeWriter = new CodeWriter(out);
            codeWriter.setFileName(filePrefix);

            while(parser.hasMoreCommands()) {
                parser.advance();
                Command commandType = parser.commandType();
                switch(commandType) {
                    case C_ARITHMETIC:
                        codeWriter.writeArithmetic(parser.arg1());
                        break;
                    case C_PUSH:
                    case C_POP:
                        codeWriter.writePushPop(commandType, parser.arg1(), parser.arg2());
                        break;
                }
            }
            in.close();
            codeWriter.close();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
