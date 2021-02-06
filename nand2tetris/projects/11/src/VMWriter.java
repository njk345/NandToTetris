import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class VMWriter {
    private BufferedWriter bw;

    public VMWriter(File outFile) {
        try {
            bw = new BufferedWriter(new FileWriter(outFile));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void writePush(Segment segment, int index) {
        try {
            bw.write("push " + segment + " " + index + "\n");
            System.out.println("push " + segment + " " + index);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void writePop(Segment segment, int index) {
        try {
            bw.write("pop " + segment + " " + index + "\n");
            System.out.println("pop " + segment + " " + index);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void writeArithmetic(Command command) {
        try {
            bw.write(command + "\n");
            System.out.println(command);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void writeLabel(String label) {
        try {
            bw.write("label " + label + "\n");
            System.out.println("label " + label);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void writeGoto(String label) {
        try {
            bw.write("goto " + label + "\n");
            System.out.println("goto " + label);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void writeIf(String label) {
        try {
            bw.write("if-goto " + label + "\n");
            System.out.println("if-goto " + label);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void writeCall(String name, int nArgs) {
        try {
            bw.write("call " + name + " " + nArgs + "\n");
            System.out.println("call " + name + " " + nArgs);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void writeFunction(String name, int nLocals) {
        try {
            bw.write("function " + name + " " + nLocals + "\n");
            System.out.println("function " + name + " " + nLocals);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void writeReturn() {x2
        try {
            bw.write("return\n");
            System.out.println("return");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void close() {
        try {
            bw.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    enum Segment {
        CONST("constant"), ARG("argument"), LOCAL("local"), STATIC("static"), THIS("this"), THAT("that"), POINTER("pointer"),
        TEMP("temp");

        private String str;

        Segment(String str) {
            this.str = str;
        }

        public static Segment fromVal(String val) {
            if (val.equals("var")) {
                return LOCAL;
            } else if (val.equals("argument")) {
                return ARG;
            } else if (val.equals("static")) {
                return STATIC;
            } else if (val.equals("field")) {
                return THIS;
            } else { // val.equals("none")
                return null;
            }
        }

        @Override
        public String toString() {
            return str;
        }
    }

    enum Command {
        ADD("add"), SUB("sub"), NEG("neg"), EQ("eq"), GT("gt"), LT("lt"), AND("and"), OR("or"), NOT("not");

        private String str;

        Command(String str) {
            this.str = str;
        }

        @Override
        public String toString() {
            return str;
        }
    }
}
