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
 * Author:  Martijn
 * Date:    15-6-2016
 */
public class TypeChecker extends AtlantisBaseListener {

    private ParseTreeProperty<Type> types;
    private List<String> errors;
    private Map<String, Type> varTypes;
    private List<String> declared;

    public TypeChecker() {
        this.types = new ParseTreeProperty<>();
        this.errors = new ArrayList<>();
        this.varTypes = new HashMap<>();
        this.declared = new ArrayList<>();
    }

    public List<String> getErrors() {
        return errors;
    }

    @Override
    public void enterProgram(ProgramContext ctx) {
        // nothing to do
    }

    @Override
    public void exitBlock(BlockContext ctx) {
        // nothing to do
    }

    @Override
    public void exitAssStat(AssStatContext ctx) {
        Type type;
        if (ctx.type() == null) {
            // Check whether target variable has already been given a type
            boolean declared = false;
            for (String s : this.declared) {
                if (s.equals(ctx.target().getText())) {
                    declared = true;
                }
            }
            // if target has no type yet, give error
            if (!declared) {
                addError(ctx, "Type of variable '%s' not yet declared", ctx.target().getText());
            }
            type = this.varTypes.get(ctx.target().getText());
        } else {
            // set type of target variable
            type = type(ctx.type());
            this.varTypes.put(ctx.target().getText(), type);
        }
        setType(ctx.target(), type);
        checkType(ctx.expr(), type);
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
        setType(ctx, this.varTypes.get(ctx.getText()));
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
    public void exitBoolType(BoolTypeContext ctx) {
        setType(ctx, Type.BOOL);
    }

    @Override
    public void exitIntType(IntTypeContext ctx) {
        setType(ctx, Type.INT);
    }

    @Override
    public void exitStrType(StrTypeContext ctx) {
        setType(ctx, Type.STR);
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
