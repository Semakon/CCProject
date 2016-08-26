package project_9.checker;

import java.util.LinkedHashMap;
import java.util.Map;

/** Class combining the information of a single scope level. */
public class Scope {

    /** Starting offset of this scope.
     * Used to calculate offsets of newly declared variables. */
    private int currentOffset;
    /** Starting global offset of this scope.
     * Used to calculate offsets of newly declared variables. */
    private int currentGlobalOffset;
    /** Map from declared variables to their types. */
    private final Map<String, Type> types;
    /** Map from declared variables to their offset within the allocation
     * record of this scope. */
    private final Map<String, Integer> offsets;
    /** Map from declared variables to their global offsets within the allocation
     * record of this scope. */
    private final Map<String, Integer> globalOffsets;
    /** This scope's level in the symbol table */
    private int level;
    /** This scope's id within its scope level */
    private int levelId;
    /** Pointer to this scope's parent scope */
    private Scope parent;

    /** Constructs the outer scope of a symbol table. */
    public Scope(int level, int levelId) {
        this(1, 1, level, levelId, null);
    }

    /** Constructs a fresh, initially empty scope. */
    public Scope(int currentOffset, int currentGlobalOffset, int level, int levelId, Scope parent) {
        this.currentOffset = currentOffset;
        this.currentGlobalOffset = currentGlobalOffset;
        this.level = level;
        this.levelId = levelId;
        this.parent = parent;
        this.types = new LinkedHashMap<>();
        this.offsets = new LinkedHashMap<>();
        this.globalOffsets = new LinkedHashMap<>();
    }

    public Scope getParent() {
        return parent;
    }

    public int getLevelId() {
        return levelId;
    }

    public int getLevel() {
        return level;
    }

    public int getCurrentOffset() {
        return currentOffset;
    }

    public int getCurrentGlobalOffset() {
        return currentGlobalOffset;
    }

    /** Tests if a given identifier is declared in this scope. */
    public boolean contains(String id) {
        return this.offsets.containsKey(id);
    }

    /** Tests if a given global identifier is declared in this scope. */
    public boolean containsGlobal(String id) {
        return this.globalOffsets.containsKey(id);
    }

    /**
     * Declares an identifier with a given type, if the identifier
     * is not yet in this scope.
     * @return <code>true</code> if the identifier was added;
     * <code>false</code> if it was already declared.
     */
    public boolean put(String id, Type type) {
        boolean result = !contains(id);
        if (result) {
            this.types.put(id, type);
            this.offsets.put(id, this.currentOffset);
            this.currentOffset += type.getSize();
        }
        return result;
    }

    /**
     * Declares a global identifier with a given type, if the identifier
     * is not yet in this scope.
     * @return <code>true</code> if the identifier was added;
     * <code>false</code> if it was already declared.
     */
    public boolean globalPut(String id, Type type) {
        boolean result = !contains(id);
        if (result) {
            this.types.put(id, type);
            this.globalOffsets.put(id, this.currentGlobalOffset);
            this.currentGlobalOffset += type.getSize();
        }
        return result;
    }

    /** Returns the type of a given (presumably declared) identifier. */
    public Type type(String id) {
        return this.types.get(id);
    }

    /**
     * Returns the offset of a given (presumably declared) identifier.
     * with respect to the beginning of this scope's activation record.
     * Offsets are assigned in order of declaration.
     */
    public Integer offset(String id) {
        return this.offsets.get(id);
    }

    /** Returns the global offset of a given (presumably declared) identifier. */
    public Integer globalOffset(String id) {
        return this.globalOffsets.get(id);
    }

    /** Add a certain amount to all global offsets (to reserve regSprID in shared memory). */
    public void addToGlobalOffset(int amount) {
        for (String key : globalOffsets.keySet()) {
            int x = globalOffsets.get(key);
            globalOffsets.put(key, x + amount);
        }
    }

    public String toString() {
        return level + "-" + levelId + "=" + this.types.keySet().toString();
    }

}
