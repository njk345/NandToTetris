import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class JackTokenizer {
    private int currIndex;
    private String currToken, fileString;
    private char currChar;
    private TokenType currTokenType;

    public JackTokenizer(File inFile) {
        this.currIndex = 0;
        this.currToken = null;
        this.fileString = getFileNoComments(inFile);
        this.currTokenType = null;
        this.currChar = fileString.charAt(0);
    }

    private String getFileNoComments(File inFile) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(inFile));
            String out = "";
            char c;
            while ((c = (char) br.read()) != ((char) -1)) {
                if (c == '/') {
                    char nextC = (char) br.read();
                    if (nextC == '/') {
                        /* Inline comment --> ignore until after newline */
                        do {
                            c = (char) br.read();
                        } while (c != '\n');
                        out += '\n';
                    } else if (nextC == '*') {
                        /* Multiline comment --> ignore until after closing star-slash */
                        char prevC;
                        c = (char) br.read();
                        if (c == '*') { /* Documentation comment, so skip one further before loop */
                            c = (char) br.read();
                        }
                        do {
                            prevC = c;
                            c = (char) br.read();
                        } while (!(prevC == '*' && c == '/'));
                    } else {
                        out += c;
                    }
                } else {
                    out += c;
                }
            }
            return out;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }

    /* Upon being run, currIndex will already point to the index of the next char in fileString, but
     * currChar will still hold the previous char (fileString.charAt(currIndex - 1)) */
    public boolean hasMoreTokens() {
        if (currIndex >= fileString.length()) { // check if currIndex is at the end of fileString
            return false;
        }
        currChar = fileString.charAt(currIndex); // update currChar
        while (Character.isWhitespace(currChar)) { // skip currChar over all whitespace until at valid character
            currIndex++;
            if (currIndex >= fileString.length()) { // check if currIndex reached end of fileString
                return false;
            }
            currChar = fileString.charAt(currIndex);
        }
        return true;
    }

    /* Method analyzes the value of currChar, which has been set to fileString.charAt(currIndex) beforehand,
       sets currToken and currTokenType, and then increments currIndex (but NOT currChar)
     */
    public void advance() {
        if (isSymbol(currChar)) {
            currToken = "" + currChar;
            currTokenType = TokenType.SYMBOL;
            currIndex++;
        } else if (Character.isDigit(currChar)) {
            currToken = "" + currChar;
            currIndex++;
            while (Character.isDigit((currChar = fileString.charAt(currIndex)))) {
                currToken += currChar;
                currIndex++;
            }
            currTokenType = TokenType.INT_CONST;
        } else if (currChar == '\"') {
            currToken = "";
            currIndex++;
            while ((currChar = fileString.charAt(currIndex)) != '\"') {
                currToken += currChar;
                currIndex++;
            }
            currIndex++;
            currChar = fileString.charAt(currIndex);
            currTokenType = TokenType.STRING_CONST;
        } else if (Character.isAlphabetic(currChar) || currChar == '_') {
            currToken = "" + currChar;
            while (true) {
                currIndex++;
                currChar = fileString.charAt(currIndex);
                if (!Character.isAlphabetic(currChar) && !Character.isDigit(currChar) && currChar != '_') {
                    break;
                }
                currToken += currChar;
            }
            if (isKeyword(currToken)) {
                currTokenType = TokenType.KEYWORD;
            } else {
                currTokenType = TokenType.IDENTIFIER;
            }
        }
    }

    private boolean isKeyword(String s) {
        return KeyWord.fromVal(s) != null;
    }

    private boolean isSymbol(char c) {
        return "{}()[].,;+-*/&|<>=~".contains("" + c);
    }

    public TokenType tokenType() {
        return currTokenType;
    }

    public KeyWord keyWord() {
        return KeyWord.fromVal(currToken);
    }

    public String symbol() {
        String val = currToken.substring(0, 1);
        if (val.equals("<")) {
            val = "&lt;";
        } else if (val.equals(">")) {
            val = "&gt;";
        } else if (val.equals("&")) {
            val = "&amp;";
        }
        return val;
    }

    public String identifier() {
        return currToken;
    }

    public int intVal() {
        return Integer.parseInt(currToken);
    }

    public String stringVal() {
        return currToken;
    }

    enum TokenType {
        KEYWORD("keyword"), SYMBOL("symbol"), IDENTIFIER("identifier"), INT_CONST("integerConstant"),
        STRING_CONST("stringConstant");

        private final String val;

        TokenType(String val) {
            this.val = val;
        }

        public String getVal() {
            return val;
        }

        @Override
        public String toString() {
            return val;
        }
    }

    enum KeyWord {
        CLASS("class"), METHOD("method"), FUNCTION("function"), CONSTRUCTOR("constructor"),
        INT("int"), BOOLEAN("boolean"), CHAR("char"), VOID("void"), VAR("var"),
        STATIC("static"), FIELD("field"), LET("let"), DO("do"), IF("if"), ELSE("else"),
        WHILE("while"), RETURN("return"), TRUE("true"), FALSE("false"), NULL("null"),
        THIS("this");

        private final String val;

        KeyWord(String val) {
            this.val = val;
        }

        public static KeyWord fromVal(String val) {
            for (KeyWord kw : KeyWord.values()) {
                if (kw.getVal().equals(val)) {
                    return kw;
                }
            }
            return null;
        }

        public String getVal() {
            return val;
        }

        @Override
        public String toString() {
            return val;
        }
    }
}
