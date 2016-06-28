package project_9.checker;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:  martijn
 * Date:    28-6-16.
 */
public class Program {

    /** List of operations that this program consists of. */
    private List<Op> operations;

    public Program() {
        operations = new ArrayList<>();
    }

    /** Returns the list of operations. */
    public List<Op> getOperations() {
        return operations;
    }

    /**
     * Returns the operation at the given line number.
     * @param i the line number.
     * @return the operation at the given line number.
     */
    public Op getOpAt(int i) {
        return this.operations.get(i);
    }

    /**
     * Returns a given operation's line number.
     * @param op operation whose line number is returned.
     * @return a given operation's line number, -1 if the operation is not found.
     */
    public int getLineNumber(Op op) {
        int lineNumber = -1;
        for (int i = 0; i < this.operations.size(); i++) {
            if (op.equals(this.operations.get(i))) {
                lineNumber = i;
                break;
            }
        }
        return lineNumber;
    }

    /**
     * Replaces an operation at a given line number within the program.
     * @param i The line number.
     * @param op The new operation that will replace the old one.
     */
    public void setOpAt(int i, Op op) {
        operations.set(i, op);
    }
    
    /**
     * Adds an operation at a given line number within the program (without replacing).
     * @param i The line number.
     * @param op The operation to be inserted. 
     */
    public void addOpAt(int i, Op op) {
    	operations.add(i, op);
    }
    /**
     * Adds an operation to the end of the program.
     * @param op The operation to be added.
     */
    public void addOp(Op op) {
    	operations.add(op);
    }

}
