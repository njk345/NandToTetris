import java.util.HashMap;
/**
 * A class to keep track of mappings between symbols in an ASM file and numerical addresses
 */
public class SymbolTable {
    /**
     * A private HashMap field to store pairings between string symbols and integer addresses
     */
    private HashMap<String, Integer> table;
    
    /**
     * A constructor for SymbolTable - initializes the HashMap and loads in predefined symbols
     */
    public SymbolTable() {
        table = new HashMap<>();
        table.put("SP", 0);
        table.put("LCL", 1);
        table.put("ARG", 2);
        table.put("THIS", 3);
        table.put("THAT", 4);
        for(int i = 0; i <= 15; i++) {
            table.put("R" + i, i);
        }
        table.put("SCREEN", 16384);
        table.put("KBD", 24576);
    }
    /**
     * A method to add a symbol-address pairing to the symbol table
     *
     * @param symbol the String symbol to add
     * @param address the integer address associated with symbol
     */
    public void addEntry(String symbol, int address) {
        table.put(symbol, address);
    }
    /**
     * A method to determine whether a given symbol is present in the table
     *
     * @param symbol The string symbol to check the presence of in the table
     *
     * @return True, if symbol is present in the table, false if not
     */
    public boolean contains(String symbol) {
        return table.containsKey(symbol);
    }
    /**
     * A method to retrieve the address associated with a given symbol, 
       if it is present in the table. If the symbol is not present, -1 is returned
     *
     * @param symbol the symbol to attempt to look up in the table
     *
     * @return the integer address associated with symbol, or -1 if symbol not present
     */
    public int getAddress(String symbol) {
        Integer a = table.get(symbol);
        if (a == null) { //should not get here
            return -1;
        } else {
            return a.intValue();
        }
    }
}