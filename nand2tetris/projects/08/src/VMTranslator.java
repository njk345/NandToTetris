import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class VMTranslator {
    public static void main(String[] args) {
        try {
            File inFile = new File(args[0]);
            List<File> vmFiles = new ArrayList<>();
            boolean isDir = inFile.isDirectory();
            if (isDir) {
                for (File f : inFile.listFiles()) {
                    if (f.getName().contains(".vm")) {
                        vmFiles.add(f);
                    }
                }
            } else {
                vmFiles.add(inFile);
            }
            String outFilename = null;
            if (isDir) {
                outFilename = inFile.getPath() + "/" + inFile.getName() + ".asm";
            } else {
                outFilename = inFile.getParentFile().getPath() + "/"
                        + inFile.getName().substring(0, inFile.getName().indexOf(".vm")) + ".asm";
            }
            BufferedWriter out = new BufferedWriter(new FileWriter(outFilename));
            CodeWriter codeWriter = new CodeWriter(out);
            if (isDir) {
                codeWriter.writeInit();
            }

            for (File f : vmFiles) {
                Scanner in = new Scanner(new FileReader(f));
                Parser parser = new Parser(in);
                codeWriter.setFileName(f.getName().substring(0, f.getName().indexOf(".vm")));

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
                        case C_LABEL:
                            codeWriter.writeLabel(parser.arg1());
                            break;
                        case C_GOTO:
                            codeWriter.writeGoto(parser.arg1());
                            break;
                        case C_IF:
                            codeWriter.writeIf(parser.arg1());
                            break;
                        case C_CALL:
                            codeWriter.writeCall(parser.arg1(), parser.arg2());
                            break;
                        case C_FUNCTION:
                            codeWriter.writeFunction(parser.arg1(), parser.arg2());
                            break;
                        case C_RETURN:
                            codeWriter.writeReturn();
                    }
                }
                in.close();
            }
            codeWriter.close();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
