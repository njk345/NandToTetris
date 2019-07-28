import java.util.Scanner;

public class Parser {
    private Scanner in;
    private String command;
    private String[] commSplit;
    private Command commType;

    public Parser(Scanner in) {
        this.in = in;
        this.command = null;
        this.commType = null;
    }
    public boolean hasMoreCommands() {
        return in.hasNextLine();
    }
    public void advance() {
        do {
            command = removeComments(in.nextLine()).toLowerCase();
            commSplit = command.split("\\s");
        } while(command.isEmpty() && hasMoreCommands());
    }
    public Command commandType() {
        if (command.equals("add") || command.equals("sub") || command.equals("neg") || command.equals("eq")
            || command.equals("gt") || command.equals("lt") || command.equals("and") || command.equals("or")
            || command.equals("not")) {
            commType = Command.C_ARITHMETIC;
        } else if (command.contains("push")) {
            commType = Command.C_PUSH;
        } else if (command.contains("pop")) {
            commType = Command.C_POP;
        } else if (command.contains("label")) {
            commType = Command.C_LABEL;
        } else if (command.contains("goto")) {
            commType = Command.C_GOTO;
        } else if (command.contains("if")) {
            commType = Command.C_IF;
        } else if (command.contains("function")) {
            commType = Command.C_FUNCTION;
        } else if (command.contains("return")) {
            commType = Command.C_RETURN;
        } else if (command.contains("call")) {
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
