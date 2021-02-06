import java.io.File;

public class CompilationEngine {
    private JackTokenizer jt;
    private VMWriter vmw;
    private SymbolTable st;
    private int ifIndex, whileIndex;
    private String className, subRetType;

    public CompilationEngine(File in, File out) {
        /* Initialize the Tokenizer, VMWriter, and SymbolTable */
        jt = new JackTokenizer(in);
        vmw = new VMWriter(out);
        st = new SymbolTable();
        ifIndex = 0;
        whileIndex = 0;
        className = in.getName().substring(0, in.getName().indexOf(".")); //set className to Jack file name prefix
        if (jt.hasMoreTokens()) { // advance the tokenizer to get started
            jt.advance();
        }
    }

    public void close() {
        vmw.close();
    }

    private void eat() {
        if (jt.hasMoreTokens()) {
            jt.advance();
        }
    }

    /* A method to compile a class - the top level of the recursive descent compilation */
    public void compileClass() {
        eat(); // pass "class"
        String foundClassName = jt.identifier();
        if (!foundClassName.equals(className)) { // if class name and file name don't match, throw error
            throw new RuntimeException("File name and class name do not match");
        }
        eat();
        eat(); // pass "{"
        while (isClassVarDec()) {
            compileClassVarDec();
        }
        while (isSubroutine()) {
            compileSubroutine();
        }
        eat();
    }

    private void compileClassVarDec() {
        SymbolTable.Kind kind = SymbolTable.Kind.valueOf(jt.keyWord().toString()); // class var is either "static" or "field"
        eat();
        String type = null;
        if (jt.tokenType() == JackTokenizer.TokenType.KEYWORD) {
            type = jt.keyWord().toString(); // class var is an "int", "boolean", or "char"
        } else if (jt.tokenType() == JackTokenizer.TokenType.IDENTIFIER) {
            type = jt.identifier(); // class var is an object of class className
        }
        eat();
        String name = jt.identifier(); // store the name of the class variable
        eat();

        st.define(name, type, kind); // add the class variable to the symbol table

        while (isComma()) { // add any other class variables of the same kind and type to the symbol table
            eat();
            name = jt.identifier(); // get other class var name
            eat();
            st.define(name, type, kind); // add to symbol table
        }
        eat();
    }

    private void compileSubroutine() {
        ifIndex = 0;
        whileIndex = 0;
        st.startSubroutine();
        String subKind = jt.keyWord().toString(); // get keyword representing "function", "method", or "constructor"
        eat(); // pass subKind

        if (jt.tokenType() == JackTokenizer.TokenType.KEYWORD && jt.keyWord() == JackTokenizer.KeyWord.VOID) {
            subRetType = "void";
        } else if (jt.tokenType() == JackTokenizer.TokenType.KEYWORD) {
            subRetType = jt.keyWord().toString();
        } else {
            subRetType = jt.identifier();
        }

        if (subKind.equals("constructor") && !subRetType.equals(className)) {
            throw new RuntimeException("Constructor name and class name do not match");
        }
        if (subKind.equals("method")) { // if a method, add className.this to the symbol table
            st.define("this", subRetType, SymbolTable.Kind.ARG);
        }
        eat(); // pass return type
        String subName = className + "." + jt.identifier(); // get name of the subroutine
        eat(); // pass subName
        eat(); // pass "("
        compileParameterList();
        eat(); // pass ")"
        eat(); // pass "{"
        while (isVarDec()) {
            compileVarDec();
        }
        vmw.writeFunction(subName, st.varCount(SymbolTable.Kind.VAR));
        if (subKind.equals("constructor")) {
            /* Allocate a size 1 memory block for the new object, push the class fields to the constant segment,
                and set base of THIS segment to point at its base */
            vmw.writePush(VMWriter.Segment.CONST, st.varCount(SymbolTable.Kind.FIELD));
            vmw.writeCall("Memory.alloc", 1);
            vmw.writePop(VMWriter.Segment.POINTER, 0);
        } else if (subKind.equals("method")) {
            /* Push subroutine arguments onto the stack and set base of THIS segment */
            vmw.writePush(VMWriter.Segment.ARG, 0);
            vmw.writePop(VMWriter.Segment.POINTER, 0);
        }
        compileStatements();
        eat(); // "}"
    }

    private void compileParameterList() {
        /* Method that compiles a subroutine's argument list, adding args to the symbol table */
        String argName;
        String argType = null;
        if (isParameterList()) {
            if (jt.tokenType() == JackTokenizer.TokenType.KEYWORD) {
                argType = jt.keyWord().toString();
            } else if (jt.tokenType() == JackTokenizer.TokenType.IDENTIFIER) {
                argType = jt.identifier();
            }
            eat();
            argName = jt.identifier();
            st.define(argName, argType, SymbolTable.Kind.ARG);
            eat();
            while (isComma()) {
                eat();
                if (jt.tokenType() == JackTokenizer.TokenType.KEYWORD) {
                    argType = jt.keyWord().toString();
                } else if (jt.tokenType() == JackTokenizer.TokenType.IDENTIFIER) {
                    argType = jt.identifier();
                }
                eat();
                argName = jt.identifier();
                st.define(argName, argType, SymbolTable.Kind.ARG);
                eat();
            }
        }
    }

    /* Method to compile a variable declaration line */
    private void compileVarDec() {
        eat();
        String varType = null;
        String varName;
        if (jt.tokenType() == JackTokenizer.TokenType.KEYWORD) {
            varType = jt.keyWord().toString();
        } else if (jt.tokenType() == JackTokenizer.TokenType.IDENTIFIER) {
            varType = jt.identifier();
        }
        eat();
        varName = jt.identifier();
        st.define(varName, varType, SymbolTable.Kind.VAR);
        eat();
        while (isComma()) {
            eat();
            varName = jt.identifier();
            st.define(varName, varType, SymbolTable.Kind.VAR);
            eat();
        }
        eat();
    }

    private void compileStatements() {
        while (isStatement()) {
            if (isLet()) compileLet();
            else if (isIf()) compileIf();
            else if (isWhile()) compileWhile();
            else if (isDo()) compileDo();
            else if (isReturn()) compileReturn();
            else throw new RuntimeException("Statement must begin with let, if, while, do, or return");
        }
    }

    private void compileDo() {
        eat(); // pass "do"
        String idName = jt.identifier(); // contains either subroutineName or (className | varName)
        eat();
        if (isLeftParen()) { // then it is a subroutine (method belonging to the current class)
            vmw.writePush(VMWriter.Segment.POINTER, 0);
            eat(); // skip "("
            int nArgs = compileExpressionList();
            eat(); // skip ")"
            vmw.writeCall(className + "." + idName, nArgs + 1);
            vmw.writePop(VMWriter.Segment.TEMP, 0);
        } else { // then it is a static function or method (belonging to another class)
            if (st.kindOf(idName) == SymbolTable.Kind.NONE) { // STATIC CLASS FUNCTION
                eat(); // "."
                String subName = jt.identifier();
                eat(); //subName
                eat(); // "("
                int nArgs = compileExpressionList();
                eat(); // ")"
                vmw.writeCall(idName + "." + subName, nArgs);
                vmw.writePop(VMWriter.Segment.TEMP, 0);
            } else { // METHOD
                eat(); // "."
                String subName = jt.identifier();
                vmw.writePush(VMWriter.Segment.fromVal(st.typeOf(idName)), st.indexOf(idName));
                eat(); //subName
                eat(); // "("
                int nArgs = compileExpressionList();
                eat(); // ")"
                vmw.writeCall(st.typeOf(idName) + "." + subName, nArgs + 1);
                vmw.writePop(VMWriter.Segment.TEMP, 0);
            }
        }
        eat(); // ";"
    }

    private void compileLet() {
        eat();
        String varName = jt.identifier();
        eat(); // pass varName
        if (isLeftBracket()) {
            eat(); // "["
            compileExpression();
            vmw.writePush(VMWriter.Segment.fromVal(st.kindOf(varName).toString()), st.indexOf(varName));
            vmw.writeArithmetic(VMWriter.Command.ADD);

            eat(); // "]"
            eat(); // "="
            compileExpression();

            vmw.writePop(VMWriter.Segment.TEMP, 0);
            vmw.writePop(VMWriter.Segment.POINTER, 1);
            vmw.writePush(VMWriter.Segment.TEMP, 0);
            vmw.writePop(VMWriter.Segment.THAT, 0);
        } else {
            eat(); // pass "="
            compileExpression();

            vmw.writePop(VMWriter.Segment.fromVal(st.kindOf(varName).toString()), st.indexOf(varName));
        }
        eat(); // pass ";"
    }

    private void compileWhile() {
        whileIndex++;
        vmw.writeLabel("WHILE_EXP" + whileIndex);
        eat(); // "while"
        eat(); // "("
        compileExpression();
        vmw.writeArithmetic(VMWriter.Command.NOT);
        vmw.writeIf("WHILE_END" + whileIndex);
        eat(); // ")"
        eat(); // "{"
        int oldWhileIndex = whileIndex; // maintain old whileIndex in case nested whiles in compileStatements
        compileStatements();
        whileIndex = oldWhileIndex;
        eat(); // "}"

        vmw.writeGoto("WHILE_EXP" + whileIndex);
        vmw.writeLabel("WHILE_END" + whileIndex);
    }

    private void compileReturn() {
        eat();
        if (isExpression()) {
            compileExpression();
            if (subRetType.equals("void")) {
                vmw.writePush(VMWriter.Segment.CONST, 0);
            }
        } else {
            vmw.writePush(VMWriter.Segment.CONST, 0);
        }
        vmw.writeReturn();
        eat();
    }

    /* Need to figure out how to decrement ifIndex when coming out
    of inner nested loops and back into elses of outer loops
     */
    private void compileIf() {
        ifIndex++;
        eat(); // "if"
        eat(); // "("
        compileExpression();
        vmw.writeArithmetic(VMWriter.Command.NOT);
        eat(); // ")"

        vmw.writeIf("IF_TRUE" + ifIndex);

        eat(); // "{"
        int oldIfIndex = ifIndex;
        compileStatements();
        ifIndex = oldIfIndex; // restore ifIndex in case there were nested ifs in compileStatements() call
        eat(); // "}"
        vmw.writeGoto("IF_FALSE" + ifIndex);
        vmw.writeLabel("IF_TRUE" + ifIndex);

        if (isElse()) {
            eat(); // "else"
            eat(); // "{"
            oldIfIndex = ifIndex;
            compileStatements();
            ifIndex = oldIfIndex;
            eat(); // "}
        }
        vmw.writeLabel("IF_FALSE" + ifIndex);
    }

    private void compileExpression() {
        compileTerm();
        while (isOperator()) {
            String op = jt.symbol();
            eat();
            compileTerm();
            switch (op) {
                case "+":
                    vmw.writeArithmetic(VMWriter.Command.ADD);
                    break;
                case "-":
                    vmw.writeArithmetic(VMWriter.Command.SUB);
                    break;
                case "*":
                    vmw.writeCall("Math.multiply", 2);
                    break;
                case "/":
                    vmw.writeCall("Math.divide", 2);
                    break;
                case "&":
                    vmw.writeArithmetic(VMWriter.Command.AND);
                    break;
                case "<":
                    vmw.writeArithmetic(VMWriter.Command.LT);
                    break;
                case "=":
                    vmw.writeArithmetic(VMWriter.Command.EQ);
                    break;
                case ">":
                    vmw.writeArithmetic(VMWriter.Command.GT);
                    break;
                default:
                    throw new RuntimeException("Unknown operator " + op);
            }
        }
    }

    private void compileTerm() {
        if (jt.tokenType() == JackTokenizer.TokenType.INT_CONST) {
            vmw.writePush(VMWriter.Segment.CONST, jt.intVal());
            eat();
        } else if (jt.tokenType() == JackTokenizer.TokenType.STRING_CONST) {    // FIX
            vmw.writePush(VMWriter.Segment.CONST, jt.stringVal().length());
            vmw.writeCall("String.new", 1);
            for (int i = 0; i < jt.stringVal().length(); i++) {
                vmw.writePush(VMWriter.Segment.CONST, jt.stringVal().charAt(i));
                vmw.writeCall("String.appendChar", 2);
            }
        } else if (jt.tokenType() == JackTokenizer.TokenType.KEYWORD) {
            if (jt.keyWord().toString().equals("true")) {
                vmw.writePush(VMWriter.Segment.CONST, 0);
                vmw.writeArithmetic(VMWriter.Command.NOT);
            } else if (jt.keyWord() == JackTokenizer.KeyWord.FALSE || jt.keyWord() == JackTokenizer.KeyWord.NULL) {
                vmw.writePush(VMWriter.Segment.CONST, 0);
            } else {
                throw new RuntimeException("Unknown keyword " + jt.keyWord());
            }
            eat();
        } else if (isLeftParen()) {
            eat();
            compileExpression();
            eat();
        } else if (jt.tokenType() == JackTokenizer.TokenType.SYMBOL && (jt.symbol().equals("-") || jt.symbol().equals("~"))) {
            String op = jt.symbol();
            eat(); // get past op
            compileTerm();
            if (op.equals("-")) {
                vmw.writeArithmetic(VMWriter.Command.NEG);
            } else {
                vmw.writeArithmetic(VMWriter.Command.NOT);
            }
        } else if (jt.tokenType() == JackTokenizer.TokenType.IDENTIFIER) {
            String name = jt.identifier();
            eat(); // pass name

            if (isLeftBracket()) {
                String kind = st.kindOf(name).toString();
                String type = st.typeOf(name);
                if (!type.equals("Array")) {
                    throw new RuntimeException("Trying to reference a non-error");
                }
                int index = st.indexOf(name);
                eat();
                compileExpression();
                eat();
                vmw.writePush(VMWriter.Segment.fromVal(kind), index);
                vmw.writeArithmetic(VMWriter.Command.ADD);
                vmw.writePop(VMWriter.Segment.POINTER, 1);
                vmw.writePush(VMWriter.Segment.THAT, 0);
            } else if (isLeftParen()) {
                String subName = className + "." + name;
                vmw.writePush(VMWriter.Segment.POINTER, 0);
                eat();
                int nArgs = compileExpressionList();
                eat();
                vmw.writeCall(subName, nArgs + 1);
            } else if (isDot()) {
                eat(); // pass dot
                String subName = jt.identifier();
                eat(); // pass subName
                String type;
                SymbolTable.Kind kind = st.kindOf(name);
                if (kind != SymbolTable.Kind.NONE) {
                    type = st.typeOf(name);
                    int index = st.indexOf(name);
                    vmw.writePush(VMWriter.Segment.fromVal(kind.toString()), index);
                } else {
                    type = name;
                }
                eat(); // pass "("
                int nArgs = compileExpressionList();
                eat(); // pass ")"
                if (kind == SymbolTable.Kind.NONE) {
                    vmw.writeCall(type + "." + subName, nArgs);
                } else {
                    vmw.writeCall(type + "." + subName, nArgs + 1);
                }
            } else {
                vmw.writePush(VMWriter.Segment.fromVal(st.kindOf(name).toString()), st.indexOf(name));
                //eat();
            }
        }
    }

    private int compileExpressionList() {
        int nArg = 0;
        if (isExpression()) {
            compileExpression();
            nArg++;
            while (isComma()) {
                eat();
                compileExpression();
                nArg++;
            }
        }
        return nArg;
    }

    private boolean isClassVarDec() {
        return jt.tokenType() == JackTokenizer.TokenType.KEYWORD &&
                (jt.keyWord() == JackTokenizer.KeyWord.STATIC || jt.keyWord() == JackTokenizer.KeyWord.FIELD);
    }

    private boolean isSubroutine() {
        return jt.tokenType() == JackTokenizer.TokenType.KEYWORD && (jt.keyWord() == JackTokenizer.KeyWord.FUNCTION ||
                jt.keyWord() == JackTokenizer.KeyWord.METHOD || jt.keyWord() == JackTokenizer.KeyWord.CONSTRUCTOR);
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
                (jt.symbol().equals("+") || jt.symbol().equals("-") || jt.symbol().equals("*") || jt.symbol().equals("/") ||
                jt.symbol().equals("|") || jt.symbol().equals("&") || jt.symbol().equals("<") || jt.symbol().equals(">") ||
                jt.symbol().equals("="));
    }
}
