package project_9.generator;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:  martijn
 * Date:    28-6-16.
 */
public class Program {

    /** List of operations that this program consists of. */
    private List<Op> operations;
    /** Highest number of threads active at any time (not including the main thread) */
    private int maxThreads;

    public Program() {
        operations = new ArrayList<>();
    }

    /** Sets maxThreads */
    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    /**
     * Adds an operation at a given line number within the program (without replacing).
     * @param i The line number.
     * @param op The operation to be inserted. 
     */
    public void insertOp(int i, Op op) {
    	operations.add(i, op);
    }

    /**
     * Adds an operation to the end of the program.
     * @param op The operation to be added.
     */
    public void addOp(Op op) {
    	operations.add(op);
//        System.out.println(this);
    }

    /**
     * Generates a list of String operations to be put in a haskell file.
     * @return A List of Strings that can be put in a haskell file.
     */
    public List<String> generateCode(String filename) {
        List<String> code = new ArrayList<>();

        code.add("module " + filename + " where");
        code.add("");
        code.add("import BasicFunctions");
        code.add("import HardwareTypes");
        code.add("import Sprockell");
        code.add("import System");
        code.add("import Simulation");
        code.add("");

        code.add("prog :: [Instruction]");
        code.add("prog = [");
        code.add("          " + operations.get(0).getInstr());
        for (int i = 1; i < operations.size(); i++) {
            code.add("        , " + operations.get(i).getInstr());
        }
        code.add("       ]");
        code.add("");

        String progs = "";
        for (int i = 0; i < maxThreads; i++) {
            progs += ",prog";
        }
        code.add("demoTest = sysTest [prog" + progs + "]");
        return code;
    }

    /**
     * Creates a String representation of this program.
     * @return A string representation of this program.
     */
    public String toString() {
    	String result = "\nprogram = [\n";
        for (Op op : operations) {
            result += op.getInstr() + ",\n";
        }
    	result = result.substring(0, result.length() - 2); // cut off ", "
    	result += "\n]";
    	return result;
    }

}
