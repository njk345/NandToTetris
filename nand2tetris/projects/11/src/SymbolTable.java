import java.util.HashMap;

public class SymbolTable {
    private HashMap<String, Symbol> classTable;
    private HashMap<String, Symbol> subroutineTable;
    private int classIndex, subroutineIndex;

    public SymbolTable() {
        classTable = new HashMap<>();
        classIndex = 0;
    }

    public void startSubroutine() {
        subroutineTable = new HashMap<>();
        subroutineIndex = 0;
    }

    public void define(String name, String type, Kind kind) {
        if (kind == Kind.STATIC || kind == Kind.FIELD) {
            classTable.put(name, new Symbol(name, type, kind, classIndex));
            classIndex++;
        } else if (kind == Kind.ARG || kind == Kind.VAR) {
            subroutineTable.put(name, new Symbol(name, type, kind, subroutineIndex));
            subroutineIndex++;
        }
    }

    public int varCount(Kind kind) {
        int numKind = 0;
        for (Symbol s : subroutineTable.values()) {
            if (s.getKind() == kind) {
                numKind++;
            }
        }
        for (Symbol s : classTable.values()) {
            if (s.getKind() == kind) {
                numKind++;
            }
        }
        return numKind;
    }

    public Kind kindOf(String name) {
        Symbol s = subroutineTable.get(name);
        if (s == null) {
            Symbol s2 = classTable.get(name);
            if (s2 == null) {
                return Kind.NONE;
            }
            return s2.getKind();
        }
        return s.getKind();
    }

    public String typeOf(String name) {
        Symbol s = subroutineTable.get(name);
        if (s == null) {
            return classTable.get(name).getType();
        } else {
            return s.getType();
        }
    }

    public int indexOf(String name) {
        Symbol s = subroutineTable.get(name);
        if (s == null) {
            /* s not found in the subroutine symbol table, so check class table */
            Symbol s2 = classTable.get(name);
            if (s2 == null) {
                /* symbol not found in class or subroutine table --> return -1 for error */
                return -1;
            }
            return s2.getIndex();
        } else {
            return s.getIndex();
        }
    }

    enum Kind {
        STATIC("static"), FIELD("field"), ARG("argument"), VAR("var"), NONE("none");

        private String val;

        Kind(String val) {
            this.val = val;
        }

        @Override
        public String toString() {
            return val;
        }
    }

    static class Symbol {
        private String name, type;
        private Kind kind;
        private int index;

        public Symbol(String name, String type, Kind kind, int index) {
            this.name = name;
            this.type = type;
            this.kind = kind;
            this.index = index;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public Kind getKind() {
            return kind;
        }

        public int getIndex() {
            return index;
        }
    }
}
