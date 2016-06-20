package pp;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import pp.AtlantisParser.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by martijn on 15-6-16.
 */
public class TypeChecker extends AtlantisBaseListener {

    private ParseTreeProperty<Type> types;
    private List<String> errors;
    private Map<String, Type> varTypes;

    public TypeChecker() {
        this.types = new ParseTreeProperty<>();
        this.errors = new ArrayList<>();
        this.varTypes = new HashMap<>();
    }

    public List<String> getErrors() {
        return errors;
    }

    @Override
    public void enterProgram(ProgramContext ctx) {
        //TODO: implement
    }

    @Override
    public void exitAssStat(AssStatContext ctx) {
        Type type = type(ctx.expr());
        setType(ctx.target(), type);
        this.varTypes.put(ctx.target().getText(), type);
        //TODO: not done
    }

    @Override
    public void exitIfStat(IfStatContext ctx) {
        checkType(ctx.expr(), Type.BOOL);
    }

    @Override
    public void exitWhileStat(WhileStatContext ctx) {
        checkType(ctx.expr(), Type.BOOL);
    }

    @Override
    public void exitInStat(InStatContext ctx) {
        // nothing to do?
    }

    @Override
    public void exitOutStat(OutStatContext ctx) {
        // nothing to do?
    }

    @Override
    public void exitVarTarget(VarTargetContext ctx) {
        // nothing to do?
    }

    @Override
    public void exitArrayTarget(ArrayTargetContext ctx) {
        checkType(ctx.expr(), Type.INT);
    }

    @Override
    public void exitNotExpr(NotExprContext ctx) {
        Type actual = type(ctx.expr());
        if (actual.equals(Type.INT)) {
            setType(ctx, Type.INT);
        } else if (actual.equals(Type.BOOL)) {
            setType(ctx, Type.BOOL);
        } else {
            addError(ctx, "Expected type '%s' or '%s' but found '%s'", Type.INT, Type.BOOL, actual);
        }
    }

    @Override
    public void exitHatExpr(HatExprContext ctx) {
        checkType(ctx.expr(0), Type.INT);
        checkType(ctx.expr(1), Type.INT);
        setType(ctx, Type.INT);
    }

    @Override
    public void exitMultExpr(MultExprContext ctx) {
        checkType(ctx.expr(0), Type.INT);
        checkType(ctx.expr(1), Type.INT);
        setType(ctx, Type.INT);
    }

    @Override
    public void exitPlusExpr(PlusExprContext ctx) {
        checkType(ctx.expr(0), Type.INT);
        checkType(ctx.expr(1), Type.INT);
        setType(ctx, Type.INT);
    }

    @Override
    public void exitCompExpr(CompExprContext ctx) {
        checkType(ctx.expr(0), type(ctx.expr(1)));
        setType(ctx, Type.BOOL);
    }

    @Override
    public void exitBoolOpExpr(BoolOpExprContext ctx) {
        checkType(ctx.expr(0), Type.BOOL);
        checkType(ctx.expr(1), Type.BOOL);
        setType(ctx, Type.BOOL);
    }

    @Override
    public void exitParExpr(ParExprContext ctx) {
        setType(ctx, type(ctx.expr()));
    }

    @Override
    public void exitVarExpr(VarExprContext ctx) {
        setType(ctx, varTypes.get(ctx.VAR().getText()));
    }

    @Override
    public void exitNumExpr(NumExprContext ctx) {
        setType(ctx, Type.INT);
    }

    @Override
    public void exitStrExpr(StrExprContext ctx) {
        setType(ctx, Type.STR);
    }

    @Override
    public void exitBoolExpr(BoolExprContext ctx) {
        setType(ctx, Type.BOOL);
    }

    @Override
    public void exitIndexExpr(IndexExprContext ctx) {
        checkType(ctx.expr(), Type.INT);
    }

    @Override
    public void exitArrayExpr(ArrayExprContext ctx) {
        Type type = null;
        int size = ctx.expr().size();
        if (size >= 1) {
            Type t = type(ctx.expr(0));
            if (t.sameType(Type.INT)) {
                type = Type.INT_ARR;
            } else if (t.sameType(Type.STR)) {
                type = Type.STR_ARR;
            } else if (t.sameType(Type.BOOL)) {
                type = Type.BOOL_ARR;
            } else {
                //TODO: do we allow arrays of arrays?
            }

            for (int i = 0; i < size; i++) {
                checkType(ctx.expr(i), t);
            }
        }
        setType(ctx, type); //TODO: type can be null, find way to deal with this
    }

    @Override
    public void exitBoolType(BoolTypeContext ctx) {
        setType(ctx, Type.BOOL);
    }

    @Override
    public void exitIntType(IntTypeContext ctx) {
        setType(ctx, Type.INT);
    }

    @Override
    public void exitStringType(StringTypeContext ctx) {
        setType(ctx, Type.STR);
    }

    @Override
    public void exitArrayType(ArrayTypeContext ctx) {
        int upper = Integer.parseInt(ctx.NUM().getText());
        Type type = new Type.Array(0, upper, type(ctx.type()));
        setType(ctx, type);
    }

    /** Sets the given parse tree's type to the given type. */
    private void setType(ParseTree ctx, Type type) {
        types.put(ctx, type);
    }

    /** Returns the given parse tree's type. */
    public Type type(ParseTree ctx) {
        return types.get(ctx);
    }

    /** Returns true if the given parse tree has the same type as the given type. */
    private void checkType(ParserRuleContext ctx, Type expected) {
        Type actual = type(ctx);
        if (actual == null) {
            throw new IllegalArgumentException("Missing inferred type of "
                    + ctx.getText());
        }
        if (!actual.sameType(expected)) {
            addError(ctx, "Expected type '%s' but found '%s'", expected, actual);
        }
    }

    /** Records an error at a given parse tree node.
     * @param ctx the parse tree node at which the error occurred
     * @param message the error message
     * @param args arguments for the message, see {@link String#format}
     */
    private void addError(ParserRuleContext ctx, String message, Object... args) {
        addError(ctx.getStart(), message, args);
    }

    /** Records an error at a given token.
     * @param token the token at which the error occurred
     * @param message the error message
     * @param args arguments for the message, see {@link String#format}
     */
    private void addError(Token token, String message, Object... args) {
        int line = token.getLine();
        int column = token.getCharPositionInLine();
        message = String.format(message, args);
        message = String.format("Line %d:%d - %s", line, column, message);
        this.errors.add(message);
    }

}
