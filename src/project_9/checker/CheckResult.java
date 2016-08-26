package project_9.checker;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import project_9.Utils;

/**
 * Author:  Martijn
 * Date:    22-6-2016
 */
public class CheckResult {

    /** Parse Tree mapping of types */
    private ParseTreeProperty<Type> types;
    /** Parse Tree mapping of offsets */
    private ParseTreeProperty<Integer> offsets;
    /** Parse Tree mapping of entries */
    private ParseTreeProperty<ParserRuleContext> entries;
    /** Parse Tree mapping of global */
    private ParseTreeProperty<Integer> globalOffsets;
    /** Threadcount excluding main */
    private int threadCount;

    /** Initializes the Parse Tree mappings. */
    public CheckResult() {
        types = new ParseTreeProperty<>();
        offsets = new ParseTreeProperty<>();
        entries = new ParseTreeProperty<>();
        globalOffsets = new ParseTreeProperty<>();
        threadCount = 0;
    }

    /** Adds a type to the Parse Tree mapping of types. */
    public void setType(ParseTree ctx, Type type) {
        types.put(ctx, type);
    }

    /** Returns a type of the Parse Tree mapping of types. */
    public Type getType(ParseTree ctx) {
        return types.get(ctx);
    }

    /** Adds an offset to the Parse Tree mapping of offsets. */
    public void setOffset(ParseTree ctx, int offset) {
        offsets.put(ctx, offset);
    }

    /** Returns an offset of the Parse Tree mapping of offsets. */
    public Integer getOffset(ParseTree ctx) {
        return offsets.get(ctx);
    }

    /** Adds an entry to the Parse Tree mapping of entries. */
    public void setEntry(ParseTree ctx, ParserRuleContext entry) {
        entries.put(ctx, entry);
    }

    /** Returns an entry of the Parse Tree mapping of entries. */
    public ParserRuleContext getEntry(ParseTree ctx) {
        return entries.get(ctx);
    }

    /** Adds a global offset to the Parse Tree mapping of global offsets. */
    public void setGlobalOffset(ParseTree ctx, int global) {
        globalOffsets.put(ctx, global);
    }

    /** Returns a global offset of the Parse Tree mapping of global offsets. */
    public Integer getGlobalOffset(ParseTree ctx) {
        return globalOffsets.get(ctx);
    }

    public void addToGlobalOffset(int amount) {
        // TODO: do something with this
    }

    /** Increments the threadCount */
    public void incThreadCount() {
        this.threadCount++;
    }

    /** Returns the threadCount */
    public int getThreadCount() {
        return threadCount;
    }

}
