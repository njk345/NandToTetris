import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CompilationEngine {
    private JackTokenizer jt;
    private BufferedWriter bw;

    public CompilationEngine(File in, File out) {
        try {
            jt = new JackTokenizer(in);
            bw = new BufferedWriter(new FileWriter(out));
            if (jt.hasMoreTokens()) {
                jt.advance();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void compileToken() {
        try {
            switch (jt.tokenType()) {
                case SYMBOL:
                    bw.write("<symbol> " + jt.symbol() + " </symbol>\n");
                    break;
                case KEYWORD:
                    bw.write("<keyword> " + jt.keyWord() + " </keyword>\n");
                    break;
                case IDENTIFIER:
                    bw.write("<identifier> " + jt.identifier() + " </identifier>\n");
                    break;
                case INT_CONST:
                    bw.write("<integerConstant> " + jt.intVal() + " </integerConstant>\n");
                    break;
                case STRING_CONST:
                    bw.write("<stringConstant> " + jt.stringVal() + " </stringConstant>\n");
            }
            if (jt.hasMoreTokens()) {
                jt.advance();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void openTag(String val) {
        try {
            bw.write("<" + val + ">\n");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void closeTag(String val) {
        try {
            bw.write("</" + val + ">\n");
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

    public void compileClass() {
        openTag("class");
        compileToken();
        compileToken();
        compileToken();
        while (isClassVarDec()) {
            compileClassVarDec();
        }
        while (isSubroutine()) {
            compileSubroutine();
        }
        compileToken();
        closeTag("class");
    }

    private boolean isClassVarDec() {
        return jt.tokenType() == JackTokenizer.TokenType.KEYWORD &&
                (jt.keyWord() == JackTokenizer.KeyWord.STATIC || jt.keyWord() == JackTokenizer.KeyWord.FIELD);
    }

    private boolean isSubroutine() {
        return jt.tokenType() == JackTokenizer.TokenType.KEYWORD && (jt.keyWord() == JackTokenizer.KeyWord.FUNCTION ||
                jt.keyWord() == JackTokenizer.KeyWord.METHOD || jt.keyWord() == JackTokenizer.KeyWord.CONSTRUCTOR);
    }

    private void compileClassVarDec() {
        openTag("classVarDec");
        compileToken();
        compileToken();
        compileToken();
        while (isComma()) {
            compileToken();
            compileToken();
        }
        compileToken();
        closeTag("classVarDec");
    }

    private boolean isComma() {
        return jt.tokenType() == JackTokenizer.TokenType.SYMBOL && jt.symbol().equals(",");
    }

    private boolean isLeftBracket() {
        return jt.tokenType() == JackTokenizer.TokenType.SYMBOL && jt.symbol().equals("[");
    }

    private boolean isLeftParen() {
        return jt.tokenType() == JackTokenizer.TokenType.SYMBOL && jt.symbol().equals("(");
    }

    private boolean isDot() {
        return jt.tokenType() == JackTokenizer.TokenType.SYMBOL && jt.symbol().equals(".");
    }

    private boolean isVarDec() {
        return jt.tokenType() == JackTokenizer.TokenType.KEYWORD && jt.keyWord() == JackTokenizer.KeyWord.VAR;
    }

    private boolean isParameterList() {
        return (jt.tokenType() == JackTokenizer.TokenType.KEYWORD && (jt.keyWord() == JackTokenizer.KeyWord.INT
                || jt.keyWord() == JackTokenizer.KeyWord.BOOLEAN || jt.keyWord() == JackTokenizer.KeyWord.CHAR)) ||
                jt.tokenType() == JackTokenizer.TokenType.IDENTIFIER;
    }

    private boolean isStatement() {
        return isLet() || isIf() || isWhile() || isDo() || isReturn();
    }

    private boolean isLet() {
        return jt.tokenType() == JackTokenizer.TokenType.KEYWORD && jt.keyWord() == JackTokenizer.KeyWord.LET;
    }

    private boolean isIf() {
        return jt.tokenType() == JackTokenizer.TokenType.KEYWORD && jt.keyWord() == JackTokenizer.KeyWord.IF;
    }

    private boolean isWhile() {
        return jt.tokenType() == JackTokenizer.TokenType.KEYWORD && jt.keyWord() == JackTokenizer.KeyWord.WHILE;
    }

    private boolean isDo() {
        return jt.tokenType() == JackTokenizer.TokenType.KEYWORD && jt.keyWord() == JackTokenizer.KeyWord.DO;
    }

    private boolean isReturn() {
        return jt.tokenType() == JackTokenizer.TokenType.KEYWORD && jt.keyWord() == JackTokenizer.KeyWord.RETURN;
    }

    private boolean isElse() {
        return jt.tokenType() == JackTokenizer.TokenType.KEYWORD && jt.keyWord() == JackTokenizer.KeyWord.ELSE;
    }

    private boolean isExpression() {
        return isTerm();
    }

    private boolean isTerm() {
        JackTokenizer.TokenType tt = jt.tokenType();
        JackTokenizer.KeyWord kw = jt.keyWord();
        return tt == JackTokenizer.TokenType.INT_CONST || tt == JackTokenizer.TokenType.STRING_CONST ||
                tt == JackTokenizer.TokenType.KEYWORD && (kw == JackTokenizer.KeyWord.TRUE || kw == JackTokenizer.KeyWord.FALSE
                        || kw == JackTokenizer.KeyWord.NULL || kw == JackTokenizer.KeyWord.THIS) ||
                tt == JackTokenizer.TokenType.IDENTIFIER || tt == JackTokenizer.TokenType.SYMBOL && (jt.symbol().equals("(") || jt.symbol().equals("-") || jt.symbol().equals("~"));
    }

    private boolean isOperator() {
        return jt.tokenType() == JackTokenizer.TokenType.SYMBOL &&
                jt.symbol().equals("+") || jt.symbol().equals("-") || jt.symbol().equals("*") || jt.symbol().equals("/") ||
                jt.symbol().equals("|") || jt.symbol().equals("&amp;") || jt.symbol().equals("&lt;") || jt.symbol().equals("&gt;") ||
                jt.symbol().equals("=");
    }

    private void compileSubroutine() {
        openTag("subroutineDec");
        compileToken();
        compileToken();
        compileToken();
        compileToken();
        compileParameterList();
        compileToken();
        openTag("subroutineBody");
        compileToken();
        while (isVarDec()) {
            compileVarDec();
        }
        compileStatements();
        compileToken();
        closeTag("subroutineBody");
        closeTag("subroutineDec");
    }

    private void compileParameterList() {
        openTag("parameterList");
        if (isParameterList()) {
            compileToken();
            compileToken();
            while (isComma()) {
                compileToken();
                compileToken();
                compileToken();
            }
        }
        closeTag("parameterList");
    }

    private void compileVarDec() {
        openTag("varDec");
        compileToken();
        compileToken();
        compileToken();
        while (isComma()) {
            compileToken();
            compileToken();
        }
        compileToken();
        closeTag("varDec");
    }

    private void compileStatements() {
        openTag("statements");
        while (isStatement()) {
            if (isLet()) compileLet();
            else if (isIf()) compileIf();
            else if (isWhile()) compileWhile();
            else if (isDo()) compileDo();
            else if (isReturn()) compileReturn();
        }
        closeTag("statements");
    }

    private void compileDo() {
        openTag("doStatement");
        compileToken();
        compileToken();
        if (isLeftParen()) {
            compileToken();
            compileExpressionList();
            compileToken();
        } else {
            compileToken();
            compileToken();
            compileToken();
            compileExpressionList();
            compileToken();
        }
        compileToken();
        closeTag("doStatement");
    }

    private void compileLet() {
        openTag("letStatement");
        compileToken();
        compileToken();
        if (isLeftBracket()) {
            compileToken();
            compileExpression();
            compileToken();
        }
        compileToken();
        compileExpression();
        compileToken();
        closeTag("letStatement");
    }

    private void compileWhile() {
        openTag("whileStatement");
        compileToken();
        compileToken();
        compileExpression();
        compileToken();
        compileToken();
        compileStatements();
        compileToken();
        closeTag("whileStatement");
    }

    private void compileReturn() {
        openTag("returnStatement");
        compileToken();
        if (isExpression()) {
            compileExpression();
        }
        compileToken();
        closeTag("returnStatement");
    }

    private void compileIf() {
        openTag("ifStatement");
        compileToken();
        compileToken();
        compileExpression();
        compileToken();
        compileToken();
        compileStatements();
        compileToken();
        if (isElse()) {
            compileToken();
            compileToken();
            compileStatements();
            compileToken();
        }
        closeTag("ifStatement");
    }

    private void compileExpression() {
        openTag("expression");
        compileTerm();
        while (isOperator()) {
            compileToken();
            compileTerm();
        }
        closeTag("expression");
    }

    private void compileTerm() {
        openTag("term");
        if (jt.tokenType() == JackTokenizer.TokenType.INT_CONST || jt.tokenType() == JackTokenizer.TokenType.STRING_CONST
                || jt.tokenType() == JackTokenizer.TokenType.KEYWORD) {
            compileToken();
        } else if (isLeftParen()) {
            compileToken();
            compileExpression();
            compileToken();
        } else if (jt.tokenType() == JackTokenizer.TokenType.SYMBOL && (jt.symbol().equals("-") || jt.symbol().equals("~"))) {
            compileToken();
            compileTerm();
        } else {
            compileToken();
            if (isLeftBracket()) {
                compileToken();
                compileExpression();
                compileToken();
            } else if (isLeftParen()) {
                compileToken();
                compileExpressionList();
                compileToken();
            } else if (isDot()) {
                compileToken();
                compileToken();
                compileToken();
                compileExpressionList();
                compileToken();
            }
        }
        closeTag("term");
    }

    private void compileExpressionList() {
        openTag("expressionList");
        if (isExpression()) {
            compileExpression();
            while (isComma()) {
                compileToken();
                compileExpression();
            }
        }
        closeTag("expressionList");
    }
}
