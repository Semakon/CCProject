package project_9.checker;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import project_9.ParseException;
import project_9.Utils;
import project_9.atlantis.AtlantisBaseListener;
import project_9.atlantis.AtlantisParser.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:  Martijn
 * Date:    15-6-2016
 */
public class TypeChecker extends AtlantisBaseListener {

    private CheckResult result;
    private List<String> errors;
    private Scope scope;

    /** Runs this checker on a given parse tree,
     * and returns the checker result.
     * @throws ParseException if an error was found during checking.
     */
    public CheckResult check(ParseTree tree) throws ParseException {
        this.result = new CheckResult();
        this.errors = new ArrayList<>();
        this.scope = new Scope();
        new ParseTreeWalker().walk(this, tree);
        if (hasErrors()) {
            Utils.pr(getErrors());
            throw new ParseException(getErrors());
        }
        return this.result;
    }

    public CheckResult getResult() {
        return this.result;
    }

    public List<String> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return !this.errors.isEmpty();
    }

    @Override
    public void enterProgram(ProgramContext ctx) {
        setEntry(ctx, entry(ctx.block()));
    }

    @Override
    public void exitBlock(BlockContext ctx) {
        setEntry(ctx, entry(ctx.stat(0)));
    }

    @Override
    public void exitAssStat(AssStatContext ctx) {
        checkType(ctx.expr(), type(ctx.target()));
        setEntry(ctx.target(), entry(ctx.expr()));
        setEntry(ctx, entry(ctx.target()));
    }

    @Override
    public void exitDeclStat(DeclStatContext ctx) {
        checkType(ctx.type(), type(ctx.target()));
        if (ctx.expr() != null) {
            checkType(ctx.expr(), type(ctx.type()));
            setEntry(ctx.target(), ctx.expr());
        }
        setEntry(ctx, ctx.target());
    }

    @Override
    public void exitIfStat(IfStatContext ctx) {
        checkType(ctx.expr(), Type.BOOL);
        setEntry(ctx, entry(ctx.expr()));
    }

    @Override
    public void exitWhileStat(WhileStatContext ctx) {
        checkType(ctx.expr(), Type.BOOL);
        setEntry(ctx, entry(ctx.expr()));
    }

    @Override
    public void exitForkStat(ForkStatContext ctx) {
        checkType(ctx.expr(), Type.INT);
        setEntry(ctx, entry(ctx.expr()));
    }

    @Override
    public void exitVarTarget(VarTargetContext ctx) {
        String id = ctx.getText();
        Type type = this.scope.type(id);

        if (ctx.getParent() instanceof DeclStatContext) {
            // target is part of a declaration
            type = type(((DeclStatContext)ctx.getParent()).type());
            this.scope.put(id, type);
        }

        if (type == null) {
            // type undefined
            addError(ctx, String.format("Type of '%s' is undefined", id));
        } else {
            setOffset(ctx, this.scope.offset(id));
            setType(ctx, type);
            setEntry(ctx, ctx);
        }
    }

    @Override
    public void exitNotExpr(NotExprContext ctx) {
        Type actual = type(ctx.expr());
        if (actual.equals(Type.INT)) {
            setType(ctx, Type.INT);
        } else if (actual.equals(Type.BOOL)) {
            setType(ctx, Type.BOOL);
        } else {
            addError(ctx, "Expected type '%s' or '%s' but found '%s'",
                    Type.INT, Type.BOOL, actual);
        }
        setEntry(ctx, entry(ctx.expr()));
    }

    @Override
    public void exitMultExpr(MultExprContext ctx) {
        checkType(ctx.expr(0), Type.INT);
        checkType(ctx.expr(1), Type.INT);
        setType(ctx, Type.INT);
        setEntry(ctx, entry(ctx.expr(0)));
    }

    @Override
    public void exitPlusExpr(PlusExprContext ctx) {
        checkType(ctx.expr(0), Type.INT);
        checkType(ctx.expr(1), Type.INT);
        setType(ctx, Type.INT);
        setEntry(ctx, entry(ctx.expr(0)));
    }

    @Override
    public void exitCompExpr(CompExprContext ctx) {
        checkType(ctx.expr(0), type(ctx.expr(1)));
        setType(ctx, Type.BOOL);
        setEntry(ctx, entry(ctx.expr(0)));
    }

    @Override
    public void exitBoolOpExpr(BoolOpExprContext ctx) {
        checkType(ctx.expr(0), Type.BOOL);
        checkType(ctx.expr(1), Type.BOOL);
        setType(ctx, Type.BOOL);
        setEntry(ctx, entry(ctx.expr(0)));
    }

    @Override
    public void exitParExpr(ParExprContext ctx) {
        setType(ctx, type(ctx.expr()));
        setEntry(ctx, entry(ctx.expr()));
    }

    @Override
    public void exitVarExpr(VarExprContext ctx) {
        String id = ctx.VAR().getText();
        setOffset(ctx, this.scope.offset(id));
        setType(ctx, this.scope.type(id));
        setEntry(ctx, ctx);
    }

    @Override
    public void exitNumExpr(NumExprContext ctx) {
        setType(ctx, Type.INT);
        setEntry(ctx, ctx);
    }

    @Override
    public void exitFalseExpr(FalseExprContext ctx) {
        setType(ctx, Type.BOOL);
        setEntry(ctx, ctx);
    }

    @Override
    public void exitTrueExpr(TrueExprContext ctx) {
        setType(ctx, Type.BOOL);
        setEntry(ctx, ctx);
    }

    @Override
    public void exitBoolType(BoolTypeContext ctx) {
        setType(ctx, Type.BOOL);
    }

    @Override
    public void exitIntType(IntTypeContext ctx) {
        setType(ctx, Type.INT);
    }

    /** Sets the given parse tree's entry to the given entry. */
    private void setEntry(ParseTree ctx, ParserRuleContext entry) {
        result.setEntry(ctx, entry);
    }

    /** Returns the given parse tree's entry. */
    public ParserRuleContext entry(ParseTree ctx) {
        return result.getEntry(ctx);
    }

    /** Sets the given parse tree's offset to the given offset. */
    private void setOffset(ParseTree ctx, int offset) {
        result.setOffset(ctx, offset);
    }

    /** Sets the given parse tree's type to the given type. */
    private void setType(ParseTree ctx, Type type) {
        result.setType(ctx, type);
    }

    /** Returns the given parse tree's type. */
    public Type type(ParseTree ctx) {
        return result.getType(ctx);
    }

    /** Returns true if the given parse tree has the same type as the given type. */
    private void checkType(ParserRuleContext ctx, Type expected) {
        Type actual = type(ctx);
        if (actual == null) {
            throw new IllegalArgumentException("Missing inferred type of "
                    + ctx.getText());
        }
        if (expected == null) {
            return;
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
