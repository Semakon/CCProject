package pp;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

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

    /** Initializes the Parse Tree mappings. */
    public CheckResult() {
        types = new ParseTreeProperty<>();
        offsets = new ParseTreeProperty<>();
        entries = new ParseTreeProperty<>();
    }

    /** Adds a type to the Parse Tree mapping of types */
    public void setType(ParseTree ctx, Type type) {
        types.put(ctx, type);
    }

    /** Returns a type of the Parse Tree mapping of types */
    public Type getType(ParseTree ctx) {
        return types.get(ctx);
    }

    /** Adds an offset to the Parse Tree mapping of offsets */
    public void setOffset(ParseTree ctx, int offset) {
        offsets.put(ctx, offset);
    }

    /** Returns an offset of the Parse Tree mapping of offsets */
    public int getOffset(ParseTree ctx) {
        return offsets.get(ctx);
    }

    /** Adds an entry to the Parse Tree mapping of entries */
    public void setEntry(ParseTree ctx, ParserRuleContext entry) {
        entries.put(ctx, entry);
    }

    /** Returns an entry of the Parse Tree mapping of entries */
    public ParserRuleContext getEntry(ParseTree ctx) {
        return entries.get(ctx);
    }

    /** Gives a String representation of CheckResult */
    public String toString() {
        String res = "Types:\t" + types.toString();
        res += "\nOffsets:\t" + offsets.toString();
        res += "\nEntries:\t" + entries.toString();
        return res;
    }

}
