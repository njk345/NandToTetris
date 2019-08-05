import java.util.Scanner;

public class Parser {
    private Scanner in;
    private String[] commSplit;
    private Command commType;

    public Parser(Scanner in) {
        this.in = in;
        this.commType = null;
        this.commSplit = null;
    }
    public boolean hasMoreCommands() {
        return in.hasNextLine();
    }
    public void advance() {
        do {
            commSplit = removeComments(in.nextLine()).split("\\s");
        } while(commSplit[0].isEmpty() && hasMoreCommands());
    }
    public Command commandType() {
        if (commSplit[0].equals("add") || commSplit[0].equals("sub") || commSplit[0].equals("neg") || commSplit[0].equals("eq")
            || commSplit[0].equals("gt") || commSplit[0].equals("lt") || commSplit[0].equals("and") || commSplit[0].equals("or")
            || commSplit[0].equals("not")) {
            commType = Command.C_ARITHMETIC;
        } else if (commSplit[0].contains("push")) {
            commType = Command.C_PUSH;
        } else if (commSplit[0].contains("pop")) {
            commType = Command.C_POP;
        } else if (commSplit[0].contains("label")) {
            commType = Command.C_LABEL;
        } else if (commSplit[0].contains("if-goto")) {
            commType = Command.C_IF;
        } else if (commSplit[0].contains("goto")) {
            commType = Command.C_GOTO;
        } else if (commSplit[0].contains("function")) {
            commType = Command.C_FUNCTION;
        } else if (commSplit[0].contains("return")) {
            commType = Command.C_RETURN;
        } else if (commSplit[0].contains("call")) {
            commType = Command.C_CALL;
        }
        return commType;
    }
    public String arg1() {
        if(commType == Command.C_ARITHMETIC) {
            return commSplit[0];
        } else { // everything other than C_ARITHMETIC, excluding C_RETURN
            return commSplit[1];
        }
    }
    public int arg2() {
        return Integer.parseInt(commSplit[2]); // only called if command is push, pop, function, or call
    }
    private static String removeComments(String line) {
        int slashIndex = line.indexOf("/");
        if (slashIndex != -1) {
            return line.substring(0, slashIndex);
        } else {
            return line;
        }
    }
}
