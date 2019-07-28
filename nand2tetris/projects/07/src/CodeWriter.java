import java.io.BufferedWriter;
import java.io.IOException;

public class CodeWriter {
    private BufferedWriter bw;
    private int ifLabel;
    private String fileName;
    public CodeWriter(BufferedWriter bw) {
        this.bw = bw;
        this.ifLabel = 0;
    }
    public void setFileName(String name) {
        this.fileName = name.substring(name.lastIndexOf("/") + 1);
    }
    public void writeArithmetic(String command) {
        try {
            if (command.equals("add")) {
                bw.write("@SP\n");
                bw.write("AM=M-1\n");
                bw.write("D=M\n");

                bw.write("@SP\n");
                bw.write("AM=M-1\n");
                bw.write("D=D+M\n");

                bw.write("@SP\n");
                bw.write("A=M\n");
                bw.write("M=D\n");
                bw.write("@SP\n");
                bw.write("M=M+1\n");
            } else if (command.equals("sub")) {
                bw.write("@SP\n");
                bw.write("AM=M-1\n");
                bw.write("D=M\n");

                bw.write("@SP\n");
                bw.write("AM=M-1\n");
                bw.write("D=M-D\n");

                bw.write("@SP\n");
                bw.write("A=M\n");
                bw.write("M=D\n");
                bw.write("@SP\n");
                bw.write("M=M+1\n");
            } else if (command.equals("and")) {
                bw.write("@SP\n");
                bw.write("AM=M-1\n");
                bw.write("D=M\n");

                bw.write("@SP\n");
                bw.write("AM=M-1\n");
                bw.write("D=D&M\n");

                bw.write("@SP\n");
                bw.write("A=M\n");
                bw.write("M=D\n");
                bw.write("@SP\n");
                bw.write("M=M+1\n");
            } else if (command.equals("or")) {
                bw.write("@SP\n");
                bw.write("AM=M-1\n");
                bw.write("D=M\n");

                bw.write("@SP\n");
                bw.write("AM=M-1\n");
                bw.write("D=D|M\n");

                bw.write("@SP\n");
                bw.write("A=M\n");
                bw.write("M=D\n");
                bw.write("@SP\n");
                bw.write("M=M+1\n");
            } else if (command.equals("neg")) {
                bw.write("@SP\n");
                bw.write("AM=M-1\n");
                bw.write("D=M\n");

                bw.write("D=-D\n");

                bw.write("@SP\n");
                bw.write("A=M\n");
                bw.write("M=D\n");
                bw.write("@SP\n");
                bw.write("M=M+1\n");
            } else if (command.equals("not")) {
                bw.write("@SP\n");
                bw.write("AM=M-1\n");
                bw.write("D=M\n");

                bw.write("D=!D\n");

                bw.write("@SP\n");
                bw.write("A=M\n");
                bw.write("M=D\n");
                bw.write("@SP\n");
                bw.write("M=M+1\n");
            } else if (command.equals("eq")) {
                bw.write("@SP\n");
                bw.write("AM=M-1\n");
                bw.write("D=M\n");

                bw.write("@SP\n");
                bw.write("AM=M-1\n");
                bw.write("D=M-D\n");

                bw.write("@IF_TRUE_" + ifLabel + "\n");
                bw.write("D;JEQ\n");
                bw.write("@SP\n");
                bw.write("A=M\n");
                bw.write("M=0\n");
                bw.write("@IF_END_" + ifLabel + "\n");
                bw.write("0;JMP\n");

                bw.write("(IF_TRUE_" + ifLabel + ")\n");
                bw.write("@SP\n");
                bw.write("A=M\n");
                bw.write("M=-1\n");
                bw.write("@IF_END_" + ifLabel + "\n");
                bw.write("0;JMP\n");

                bw.write("(IF_END_" + ifLabel + ")\n");
                bw.write("@SP\n");
                bw.write("M=M+1\n");
                ifLabel++;
            } else if (command.equals("lt")) {
                bw.write("@SP\n");
                bw.write("AM=M-1\n");
                bw.write("D=M\n");

                bw.write("@SP\n");
                bw.write("AM=M-1\n");
                bw.write("D=M-D\n");

                bw.write("@IF_TRUE_" + ifLabel + "\n");
                bw.write("D;JLT\n");
                bw.write("@SP\n");
                bw.write("A=M\n");
                bw.write("M=0\n");
                bw.write("@IF_END_" + ifLabel + "\n");
                bw.write("0;JMP\n");

                bw.write("(IF_TRUE_" + ifLabel + ")\n");
                bw.write("@SP\n");
                bw.write("A=M\n");
                bw.write("M=-1\n");
                bw.write("@IF_END_" + ifLabel + "\n");
                bw.write("0;JMP\n");

                bw.write("(IF_END_" + ifLabel + ")\n");
                bw.write("@SP\n");
                bw.write("M=M+1\n");
                ifLabel++;
            } else if (command.equals("gt")) {
                bw.write("@SP\n");
                bw.write("AM=M-1\n");
                bw.write("D=M\n");

                bw.write("@SP\n");
                bw.write("AM=M-1\n");
                bw.write("D=M-D\n");

                bw.write("@IF_TRUE_" + ifLabel + "\n");
                bw.write("D;JGT\n");
                bw.write("@SP\n");
                bw.write("A=M\n");
                bw.write("M=0\n");
                bw.write("@IF_END_" + ifLabel + "\n");
                bw.write("0;JMP\n");

                bw.write("(IF_TRUE_" + ifLabel + ")\n");
                bw.write("@SP\n");
                bw.write("A=M\n");
                bw.write("M=-1\n");
                bw.write("@IF_END_" + ifLabel + "\n");
                bw.write("0;JMP\n");

                bw.write("(IF_END_" + ifLabel + ")\n");
                bw.write("@SP\n");
                bw.write("M=M+1\n");
                ifLabel++;
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    public void writePushPop(Command command, String segment, int index) {
        try {
            if (command == Command.C_PUSH) {
                if (segment.equals("constant")) {
                    bw.write("@" + index + "\n");
                    bw.write("D=A\n");
                    bw.write("@SP\n");
                    bw.write("A=M\n");
                    bw.write("M=D\n");
                    bw.write("@SP\n");
                    bw.write("M=M+1\n");
                } else if (segment.equals("local")) {
                    bw.write("@LCL\n");
                    bw.write("D=M\n");
                    bw.write("@" + index + "\n");
                    bw.write("A=D+A\n");
                    bw.write("D=M\n");
                    bw.write("@SP\n");
                    bw.write("A=M\n");
                    bw.write("M=D\n");
                    bw.write("@SP\n");
                    bw.write("M=M+1\n");
                } else if (segment.equals("argument")) {
                    bw.write("@ARG\n");
                    bw.write("D=M\n");
                    bw.write("@" + index + "\n");
                    bw.write("A=D+A\n");
                    bw.write("D=M\n");
                    bw.write("@SP\n");
                    bw.write("A=M\n");
                    bw.write("M=D\n");
                    bw.write("@SP\n");
                    bw.write("M=M+1\n");
                } else if (segment.equals("this")) {
                    bw.write("@THIS\n");
                    bw.write("D=M\n");
                    bw.write("@" + index + "\n");
                    bw.write("A=D+A\n");
                    bw.write("D=M\n");
                    bw.write("@SP\n");
                    bw.write("A=M\n");
                    bw.write("M=D\n");
                    bw.write("@SP\n");
                    bw.write("M=M+1\n");
                } else if (segment.equals("that")) {
                    bw.write("@THAT\n");
                    bw.write("D=M\n");
                    bw.write("@" + index + "\n");
                    bw.write("A=D+A\n");
                    bw.write("D=M\n");
                    bw.write("@SP\n");
                    bw.write("A=M\n");
                    bw.write("M=D\n");
                    bw.write("@SP\n");
                    bw.write("M=M+1\n");
                } else if (segment.equals("pointer")) {
                    bw.write("@3\n");
                    bw.write("D=A\n");
                    bw.write("@" + index + "\n");
                    bw.write("A=D+A\n");
                    bw.write("D=M\n");
                    bw.write("@SP\n");
                    bw.write("A=M\n");
                    bw.write("M=D\n");
                    bw.write("@SP\n");
                    bw.write("M=M+1\n");
                } else if (segment.equals("temp")) {
                    bw.write("@5\n");
                    bw.write("D=A\n");
                    bw.write("@" + index + "\n");
                    bw.write("A=D+A\n");
                    bw.write("D=M\n");
                    bw.write("@SP\n");
                    bw.write("A=M\n");
                    bw.write("M=D\n");
                    bw.write("@SP\n");
                    bw.write("M=M+1\n");
                } else if (segment.equals("static")) {
                    String label = fileName + "." + index;
                    bw.write("@" + label + "\n");
                    bw.write("D=M\n");
                    bw.write("@SP\n");
                    bw.write("A=M\n");
                    bw.write("M=D\n");
                    bw.write("@SP\n");
                    bw.write("M=M+1\n");
                }
            } else if (command == Command.C_POP) {
                if (segment.equals("local")) {
                    bw.write("@LCL\n");
                    bw.write("D=M\n");
                    bw.write("@" + index + "\n");
                    bw.write("D=D+A\n");
                    bw.write("@R13\n");
                    bw.write("M=D\n");
                    bw.write("@SP\n");
                    bw.write("AM=M-1\n");
                    bw.write("D=M\n");
                    bw.write("@R13\n");
                    bw.write("A=M\n");
                    bw.write("M=D\n");
                } else if (segment.equals("argument")) {
                    bw.write("@ARG\n");
                    bw.write("D=M\n");
                    bw.write("@" + index + "\n");
                    bw.write("D=D+A\n");
                    bw.write("@R13\n");
                    bw.write("M=D\n");
                    bw.write("@SP\n");
                    bw.write("AM=M-1\n");
                    bw.write("D=M\n");
                    bw.write("@R13\n");
                    bw.write("A=M\n");
                    bw.write("M=D\n");
                } else if (segment.equals("this")) {
                    bw.write("@THIS\n");
                    bw.write("D=M\n");
                    bw.write("@" + index + "\n");
                    bw.write("D=D+A\n");
                    bw.write("@R13\n");
                    bw.write("M=D\n");
                    bw.write("@SP\n");
                    bw.write("AM=M-1\n");
                    bw.write("D=M\n");
                    bw.write("@R13\n");
                    bw.write("A=M\n");
                    bw.write("M=D\n");
                } else if (segment.equals("that")) {
                    bw.write("@THAT\n");
                    bw.write("D=M\n");
                    bw.write("@" + index + "\n");
                    bw.write("D=D+A\n");
                    bw.write("@R13\n");
                    bw.write("M=D\n");
                    bw.write("@SP\n");
                    bw.write("AM=M-1\n");
                    bw.write("D=M\n");
                    bw.write("@R13\n");
                    bw.write("A=M\n");
                    bw.write("M=D\n");
                } else if (segment.equals("pointer")) {
                    bw.write("@3\n");
                    bw.write("D=A\n");
                    bw.write("@" + index + "\n");
                    bw.write("D=D+A\n");
                    bw.write("@R13\n");
                    bw.write("M=D\n");
                    bw.write("@SP\n");
                    bw.write("AM=M-1\n");
                    bw.write("D=M\n");
                    bw.write("@R13\n");
                    bw.write("A=M\n");
                    bw.write("M=D\n");
                } else if (segment.equals("temp")) {
                    bw.write("@5\n");
                    bw.write("D=A\n");
                    bw.write("@" + index + "\n");
                    bw.write("D=D+A\n");
                    bw.write("@R13\n");
                    bw.write("M=D\n");
                    bw.write("@SP\n");
                    bw.write("AM=M-1\n");
                    bw.write("D=M\n");
                    bw.write("@R13\n");
                    bw.write("A=M\n");
                    bw.write("M=D\n");
                } else if (segment.equals("static")) {
                    String label = fileName + "." + index;
                    bw.write("@SP\n");
                    bw.write("AM=M-1\n");
                    bw.write("D=M\n");
                    bw.write("@" + label + "\n");
                    bw.write("M=D\n");
                }
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
    public void close() {
        try {
            bw.close();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
