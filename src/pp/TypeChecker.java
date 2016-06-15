package pp;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;
import pp.AtlantisParser.*;

import java.util.Map;

/**
 * Created by martijn on 15-6-16.
 */
public class TypeChecker extends AtlantisBaseListener {

    private ParseTreeProperty<Type> types;
    private Map<String, Type> varTypes;

    public TypeChecker() {
        this.types = new ParseTreeProperty<>();
    }

    @Override
    public void enterProgram(ProgramContext ctx) {
        //TODO: implement
    }

    @Override
    public void exitBoolOpExpr(BoolOpExprContext ctx) {
        checkType(ctx.expr(0), Type.BOOL);
        //TODO: implement further
    }

    @Override
    public void exitParExpr(ParExprContext ctx) {
        setType(ctx, type(ctx.expr()));
    }

    @Override
    public void exitCallExpr(CallExprContext ctx) {
        setType(ctx, type(ctx.VAR())); // TODO: test with function
    }

    @Override
    public void exitBoolExpr(BoolExprContext ctx) {
        setType(ctx, Type.BOOL);
    }

    @Override
    public void exitNumExpr(NumExprContext ctx) {
        setType(ctx, Type.INT);
    }

    @Override
    public void exitVarExpr(VarExprContext ctx) {
        setType(ctx, varTypes.get(ctx.VAR().getText()));
    }

    @Override public void visitTerminal(TerminalNode node) {
        //TODO: implement VAR
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
    private void checkType(ParseTree ctx, Type expected) {
        Type actual = type(ctx);
        if (actual == null) {
            throw new IllegalArgumentException("Missing inferred type of "
                    + ctx.getText());
        }
        if (!actual.equals(expected)) {
            //TODO: addError(ctx, "Expected type '%s' but found '%s'", expected, actual);
        }
    }

}
