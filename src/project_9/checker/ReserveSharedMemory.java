package project_9.checker;

import project_9.atlantis.AtlantisBaseListener;
import project_9.atlantis.AtlantisParser;

/**
 * Author:  martijn
 * Date:    26-8-16.
 */
public class ReserveSharedMemory extends AtlantisBaseListener {

    /** Amount of reserved memory. */
    private int reservedMemory;
    /** CheckResult produced by the TypeChecker */
    private CheckResult result;

    /** Constructs a new ReserveSharedMemory object. */
    public ReserveSharedMemory(int reservedMemory, CheckResult result) {
        this.reservedMemory = reservedMemory;
        this.result = result;
    }

    @Override
    public void exitVarExpr(AtlantisParser.VarExprContext ctx) {
        Integer globalOffset = result.getGlobalOffset(ctx);
        if (globalOffset != null) {
            result.setGlobalOffset(ctx, globalOffset + reservedMemory);
        }
    }

    @Override
    public void exitVarTarget(AtlantisParser.VarTargetContext ctx) {
        Integer globalOffset = result.getGlobalOffset(ctx);
        if (globalOffset != null) {
            result.setGlobalOffset(ctx, globalOffset + reservedMemory);
        }
    }

}
