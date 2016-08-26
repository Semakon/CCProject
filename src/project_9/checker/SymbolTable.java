package project_9.checker;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores and tests for nested scopes of variable declarations.
 * A newly constructed SymbolTable should consist of a single (outer) scope.
 *
 * Author:  Martijn
 * Date:    1-7-2016
 */
public class SymbolTable {

    /** Map of level to scope */
    private List<Scope> scopes;
    /** Pointer to the current active scope */
    private Scope pointer;


    /** Construct scope SymbolTable */
    public SymbolTable() {
        scopes = new ArrayList<>();
        Scope s = new Scope(0, 1);

        scopes.add(s);
        pointer = s;
    }

    public Scope getPointer() {
        return pointer;
    }


    /** Adds a next deeper scope level. */
    public void openScope() {
        int level = pointer.getLevel() + 1;
        int levelId = nextLevelId(level);
        Scope newScope = new Scope(pointer.getCurrentOffset(),
                pointer.getCurrentGlobalOffset(), level, levelId, pointer);

        scopes.add(newScope);
        pointer = newScope;
    }

    /**
     * Removes the deepest scope level.
     * @throws RuntimeException if the table only contains the outer scope.
     */
    public void closeScope() throws RuntimeException {
        if (pointer.getLevel() == 0) {
            throw new RuntimeException("Outer scope can't be removed");
        }
        pointer = pointer.getParent();
    }

    /**
     * Tries to declare a given identifier in the deepest scope level.
     * @return <code>true</code> if the identifier was added,
     * <code>false</code> if it was already declared in this scope.
     */
    public boolean insert(String id, Type type, boolean global) {
        if (global) {
            return !contains(id) && pointer.globalPut(id, type);
        }
        return !contains(id) && pointer.put(id, type);
    }

    /**
     * Looks up the type of a variable in the enclosing scope.
     * @param id the ID of the variable.
     * @return the variable's type if the variable is in the enclosing scope,
     *          otherwise <code>null</code>.
     */
    public Type lookupType(String id) {
        Scope tempPointer = pointer;
        while (true) {
            if (tempPointer == null) return null;
            if (tempPointer.contains(id) || tempPointer.containsGlobal(id)) {
                return tempPointer.type(id);
            } else {
                tempPointer = tempPointer.getParent();
            }
        }
    }

    /**
     * Looks up the offset of a variable in the enclosing scope.
     * @param id the ID of the variable.
     * @return the variable's offset if the variable is in the enclosing scope,
     *          otherwise <code>null</code>.
     */
    public Integer lookupOffset(String id) {
        Scope tempPointer = pointer;
        while (true) {
            if (tempPointer == null) return null;
            if (tempPointer.contains(id)) {
                return tempPointer.offset(id);
            } else {
                tempPointer = tempPointer.getParent();
            }
        }
    }

    /**
     * Looks up the global offset of a variable in the enclosing scope.
     * @param id the ID of the variable.
     * @return the variable's global offset if the variable is in the enclosing scope,
     *          otherwise <code>null</code>.
     */
    public Integer lookupGlobalOffset(String id) {
        Scope tempPointer = pointer;
        while (true) {
            if (tempPointer == null) return null;
            if (tempPointer.containsGlobal(id)) {
                return tempPointer.globalOffset(id);
            } else {
                tempPointer = tempPointer.getParent();
            }
        }
    }

    /**
     * Tests if a given identifier is in the scope of any declaration.
     * @return <code>true</code> if there is any enclosing scope in which
     * the identifier is declared; <code>false</code> otherwise.
     */
    public boolean contains(String id) {
        /* Temporary pointer to the active scope or its parents */
        Scope tempPointer = pointer;
        while (true) {
            if (tempPointer == null) return false;
            if (tempPointer.contains(id) || tempPointer.containsGlobal(id)) {
                return true;
            } else {
                tempPointer = tempPointer.getParent();
            }
        }
    }

    /** Generates the next level ID for a level */
    private int nextLevelId(int level) {
        int max = 0;
        for (Scope s : scopes) {
            if (s.getLevel() == level && s.getLevelId() > max) {
                max = s.getLevelId();
            }
        }
        return max + 1;
    }

    /** Add an amount to all global offsets in every scope (to reserve regSprID in shared memory). */
    public void addToGlobalOffsets(int amount) {
        for (Scope s : scopes) {
            s.addToGlobalOffset(amount);
        }
    }

    public String toString() {
        return scopes.toString();
    }

}
