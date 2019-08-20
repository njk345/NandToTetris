import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TestTokenizer {
    public static void main(String[] args) {
        JackTokenizer tknzr;
        String inFileOrDirName = args[0];
        File inFileOrDir = new File(inFileOrDirName);
        if (inFileOrDir.isDirectory()) {
            /* If input is a directory, loop through the Xxx.jack files in it and output their translations Xxx.xml */
            File[] jackFiles = inFileOrDir.listFiles(file -> file.getName().endsWith(".jack"));
            for (File file : jackFiles) {
                File outFile = new File(file.getPath().substring(0, file.getPath().indexOf(".jack")) + "T2.xml");
                try {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
                    tknzr = new JackTokenizer(file);
                    bw.write("<tokens>\n");
                    while (tknzr.hasMoreTokens()) {
                        tknzr.advance();
                        JackTokenizer.TokenType tt = tknzr.tokenType();
                        bw.write("<" + tt + "> ");
                        bw.write(printToken(tknzr.stringVal()));
                        bw.write(" </" + tt + ">");
                        bw.newLine();
                    }
                    bw.write("</tokens>\n");
                    bw.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        } else {
            /* If input is a file Xxx.jack, just output its translation Xxx.xml */
            if (inFileOrDirName.endsWith(".jack")) {
                File outFile = new File(inFileOrDir.getPath().substring(0, inFileOrDir.getPath().indexOf(".jack")) + "T2.xml");
                try {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
                    tknzr = new JackTokenizer(inFileOrDir);
                    bw.write("<tokens>\n");
                    while (tknzr.hasMoreTokens()) {
                        tknzr.advance();
                        JackTokenizer.TokenType tt = tknzr.tokenType();
                        bw.write("<" + tt + "> ");
                        bw.write(printToken(tknzr.stringVal()));
                        bw.write(" </" + tt + ">");
                        bw.newLine();
                    }
                    bw.write("</tokens>\n");
                    bw.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            } else {
                System.out.println("Input file was not a valid .jack file");
            }
        }
    }

    private static String printToken(String token) {
        if (token.equals("<")) {
            return "&lt;";
        } else if (token.equals(">")) {
            return "&gt;";
        } else if (token.equals("&")) {
            return "&amp;";
        }
        return token;
    }
}
