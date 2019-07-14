import java.util.Scanner;

/**
 * A class to handle the parsing of lines from an assembly file into commands
 */
public class Parser {
    /**
     * A static enum to represent the three types of commands
     */
    public static enum CommandType {
        A_COMMAND, C_COMMAND, L_COMMAND
    }
    /**
     * A Scanner through which the assembly file can be read
     */
    private Scanner in;
    /**
     * A String to represent the latest command the scanner has read
     */
    private String currentCommand;
    /**
     * A CommandType enum variable holding the type of the latest command
     */
    private CommandType currentCommandType;
    
    /**
     * A constructor for Parser. Initializes all fields.
     *
     * @param in the Scanner through which to read the assembly file being parsed
     */
    public Parser(Scanner in) {
        this.in = in;
        this.currentCommand = null;
        this.currentCommandType = null;
    }
    /** 
     * A method that checks whether the inputted file has any more lines to read
     *
     * @return true: if the scanner, in, has a next line to read
     *         false: otherwise
     */
    public boolean hasMoreCommands() {
        return in.hasNextLine();
    }
    /**
     * A method that has the scanner read the current line and move to the next line, 
     * removing whitespace from the current line, storing it in currentCommand, and
     * determining its command type and storing it in commandType
     */
    public void advance() {
        /* Reads the next line, removing whitespace, determines
           the command type, and skips past all commented lines */
        do {
            /* Get the next line, remove whitespace, and remove in-line comments */
            currentCommand = removeInlineComment(in.nextLine().replaceAll("\\s", ""));
            System.out.println(currentCommand);
            if (!currentCommand.isEmpty()) { // if line not blank
                if(currentCommand.charAt(0) == '@') {
                    currentCommandType = CommandType.A_COMMAND;
                } else if(currentCommand.charAt(0) == '(') {
                    currentCommandType = CommandType.L_COMMAND;
                } else {
                    currentCommandType = CommandType.C_COMMAND;
                }
            } /* If line is empty and there are more lines, skip to next for real command */
        } while(currentCommand.isEmpty() && hasMoreCommands());
    }
    /**
     * A method that returns the type of the current command
     *
     * @return a CommandType enum variable that is the current command's type
     */
    public CommandType commandType() {
        return currentCommandType;
    }
    /**
     * A method to return the symbol associated with the current command,
     * exclusively if it is an A- or L-Command
     *
     * @return a String holding the command's symbol, which is either a custom string
     *         or an integer constant
     */
    public String symbol() {
        if(currentCommandType == CommandType.A_COMMAND) {
            return currentCommand.substring(1);
        } else if (currentCommandType == CommandType.L_COMMAND) {
            return currentCommand.substring(1, currentCommand.length() - 1);
        } else {
            return null;
        }
    }
    /**
     * A method to return the current command's destination info, exclusively if it is
     * a C-Command
     *
     * @return a String holding the current command's destination mneumonic - one of 8
     *         strings such as "null", "M", "MD", "AMD", etc.
     */
    public String dest() {
        if(currentCommandType == CommandType.C_COMMAND) {
            int eqInd = currentCommand.indexOf('=');
            if (eqInd == -1) {
                return "null";
            } else {
                return currentCommand.substring(0, eqInd);
            }
        } else {
            return null;
        }
    }
    /**
     * A method to return the current command's computation info, only if it is a
     * C-Command
     *
     * @return a String holding the current command's comp pattern - one of 28
     *         expressions such as 0, 1, D, !A, A+1, D&A, etc.
     */
    public String comp() {
        if(currentCommandType == CommandType.C_COMMAND) {
            int eqInd = currentCommand.indexOf('=');
            int semcolInd = currentCommand.indexOf(';');
            if(eqInd == -1) {
                return currentCommand.substring(0, semcolInd);
            } else {
                if(semcolInd != -1) {
                    return currentCommand.substring(eqInd + 1, semcolInd);
                } else {
                    return currentCommand.substring(eqInd + 1);
                }
            }
        } else {
            return null;
        }
    }
    /**
     * A method to return the current command's jump info, only if it is a 
     * C-command
     *
     * @return a String holding the current command's jump instruction - one of
     *         8 possibilities including null, JEQ, JGE, JMP, etc.
     */
    public String jump() {
        if(currentCommandType == CommandType.C_COMMAND) {
            int semcolInd = currentCommand.indexOf(';');
            if (semcolInd == -1) {
                return "null";
            } else {
                return currentCommand.substring(semcolInd + 1);
            }
        } else {
            return null;
        }
    }
    /**
     * A method to remove inline comments (e.g. "@R0 //address R0" --> "R0 ") from strings
     *
     * @param line a String
     *
     * @return line, with an inline comment removed if it exists
     */
    private static String removeInlineComment(String line) {
        int slashIndex = line.indexOf("/");
        if (slashIndex != -1) {
            return line.substring(0, slashIndex);
        } else {
            return line;
        }
    }
}