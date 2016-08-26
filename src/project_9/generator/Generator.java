package project_9.generator;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import project_9.ParseException;
import project_9.Utils;
import project_9.atlantis.AtlantisBaseVisitor;
import project_9.atlantis.AtlantisParser.*;
import project_9.checker.CheckResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Class to generate SprIl code for Atlantis.
 * @author Kevin Booijink
 */
public class Generator extends AtlantisBaseVisitor<Op> {

    /** The program that is generated. */
	private Program program;
	/** Counts the current number of threads active (not including the main thread). */
    private int threadCount = 0;
    /** Current generated thread */
    private int currentThread = 0;
	/** Ensures only one Thread-wait-loop is produced. */
	private boolean threadWait = true;
	/** Absolute position of the Thread-wait-loop. */
	private int waitLoopPos;
	/** The outcome of the checker phase. */
	private CheckResult checkResult;
	/** Register count, used to generate fresh registers. */
	private int regCount;
	/** Map of thread-to-ParseTreeProperty to keep track of which expression belongs to which register. */
	private Map<Integer, ParseTreeProperty<Integer>> regs;
	/** Map of thread-to-Array to indicate which registers are in use (True = in use, False = not in use). */
	private Map<Integer, boolean[]> regsInUse;
	/** Integer to keep track of the amount of instructions. */
	private int instrcount;
    /** List of all memory locations of forks that have not been joined yet. */
    private List<Integer> unjoinedForks;
	/** List of errors that occurred. */
	private List<String> errors;
	
	
	/** Generates SprIl code for a given parse tree and a pre-computed checker result.*/
	public Program generate(ParseTree tree, CheckResult checkResult) throws ParseException {
		this.program = new Program();
		this.checkResult = checkResult;
        this.regCount = 5;

		this.regs = new HashMap<>();
		this.regsInUse = new HashMap<>();
        this.regs.put(currentThread, new ParseTreeProperty<Integer>());
        this.regsInUse.put(currentThread, new boolean[regCount]);

		this.errors = new ArrayList<>();
		this.instrcount = 0;
        this.unjoinedForks = new ArrayList<>();

		tree.accept(this);
		program.addOp(opGen("EndProg"));
		if (hasErrors()) {
			throw new ParseException(getErrors());
		}
        program.setMaxThreads(checkResult.getThreadCount());
		return program;
	}

	public List<String> getErrors() {
		return errors;
	}

	public boolean hasErrors() {
		return !this.errors.isEmpty();
	}

	/**
	 * Switches the state of a register
	 */
	private void switchReg(int n) {
        regsInUse.get(currentThread)[n] = !regsInUse.get(currentThread)[n];
	}
	
	/**
	 * Converts an integer indication of a register
	 * to a SprIl indication of a register.
	 */
	private String toReg(int n) {
		return n > -1 && n < 26 ? "reg" + String.valueOf((char)(n + 65)) : null;
	}
	
	/**
	 * Indicates the next free register
	 * @return The Integer for the next free register (starting at 0).
	 */
	private int nextReg(ParserRuleContext ctx) {
		for (int i = 0; i < regCount; i++) {
			if (!regsInUse.get(currentThread)[i]) {
				switchReg(i);
				return i;
			}
		}
		addError(ctx, "Too many registers are used");
		return -1;
	}

	/** Constructs an operation from the parameters
	 * @param opCode The main instruction of the operation.
	 * @param args The arguments of the operation.
	 * @return An Op constructed from the given parameters.
	 */
	private Op opGen(String opCode, String... args) {
		Op result = new Op(opCode, args);
		this.instrcount++;
		return result;
	}
	
	/** Retrieves the offset of a variable node from the checker. */
	private int offset(ParseTree node) {
		return this.checkResult.getOffset(node);
	}

    /** Retrieves the global offset of a variable node from the checker. */
    private int globalOffset(ParseTree node) {
        return this.checkResult.getGlobalOffset(node);
    }
	
	@Override
	public Op visitProgram(ProgramContext ctx) {
		return visit(ctx.block());
		
	}
	
	@Override
	public Op visitBlock(BlockContext ctx) {
		Op result = visit(ctx.stat(0));
		for (StatContext stat : ctx.stat()) {
			if (stat == ctx.stat(0)) continue;
			visit(stat);
		}
		return result;
	}
	
	@Override
	public Op visitAssStat(AssStatContext ctx) {
		Op result = visit(ctx.expr());

		int reg = regs.get(currentThread).get(ctx.expr());

		String target = "DirAddr ";
        String opCode = "Store";

        if (checkResult.getGlobalOffset(ctx.target()) != null) {
            target += globalOffset(ctx.target());
            opCode = "WriteInstr";
        } else if (checkResult.getOffset(ctx.target()) != null) {
            target += offset(ctx.target());
        } else {
            addError(ctx, "The variable '%s' is undefined", ctx.target().getText());
        }

		Op store = opGen(opCode, toReg(reg), target);

		program.addOp(store);
		switchReg(reg);
		return result;
	}

	@Override
	public Op visitDeclStat(DeclStatContext ctx) {
		Op result = null;
        if (ctx.expr() != null) {
            // only used when declaration is also an assignment
            result = visit(ctx.expr());
            Integer reg = regs.get(currentThread).get(ctx.expr());

            String target = "DirAddr "; //TODO: fix target (not necessarily here)
            String opCode;

            if (ctx.GLOBAL() != null) {
                target += globalOffset(ctx.target());
                opCode = "WriteInstr";
            } else {
                target += offset(ctx.target());
                opCode = "Store";
            }

            Op store = opGen(opCode, toReg(reg), target);
            program.addOp(store);
            switchReg(reg);
        }
        return result;
	}
	
	@Override
	public Op visitIfStat(IfStatContext ctx) {
        Op result;
        int reg;

        // compute size of block
        int startIf = instrcount;
        visit(ctx.block(0));
        int size = instrcount - startIf;

        // insert jump operation at beginning of if statement
        Op jmp = opGen("Jump", "Rel " + (size + 2));
        program.insertOp(startIf, jmp);

        // determine second jump location
        int sndJmpLoc = instrcount;

        // compute size of expression
        size = instrcount;
        result = visit(ctx.expr());
        reg = regs.get(currentThread).removeFrom(ctx.expr());
        regs.get(currentThread).put(ctx, reg);
        size = instrcount - size;

        // add branch operation at the end of expr
        int brnch = -(instrcount - startIf);
        Op branch = opGen("Branch", toReg(reg), "Rel (" + brnch + ")");
        program.addOp(branch);

        // add size of second block to size (if there is a second block)
        if (ctx.ELSE() != null) {
            int startElse = instrcount;
            visit(ctx.block(1));
            size += instrcount - startElse;
        }

        // insert jump at the end of the first block
        Op sndJmp = opGen("Jump", "Rel " + (size + 2));
        program.insertOp(sndJmpLoc, sndJmp);

        switchReg(reg);
        return result;
	}
	
	@Override
	public Op visitWhileStat(WhileStatContext ctx) {
        // compute size of block
        int startWhile = instrcount;
        Op result = visit(ctx.block());
        int blckSize = instrcount - startWhile;

        // insert jump operation at beginning while loop
        Op jmp = opGen("Jump", "Rel " + (blckSize + 1));
        program.insertOp(startWhile, jmp);

        // visit expression
        visit(ctx.expr());
        int reg = regs.get(currentThread).removeFrom(ctx.expr());
        regs.get(currentThread).put(ctx, reg);

        // add branch operation at the end of while loop
        int brnch = -(instrcount - startWhile) + 1;
        Op branch = opGen("Branch", toReg(reg), "Rel (" + brnch + ")");
        program.addOp(branch);

        switchReg(reg);
        return result;
	}

    @Override
    public Op visitForkStat(ForkStatContext ctx) {
		threadCount++;

		int branchPos = instrcount;
		// reserved: Branch regSprID (Abs XX)

        int loadPos = instrcount + 1;
		// reserved: Load (ImmValue XX) regX

        int reg = nextReg(ctx);
        int sharedMemoryAddress = threadCount; // TODO: fix
        Op writeInstr = opGen("WriteInstr", toReg(reg), "DirAddr " + sharedMemoryAddress);
        program.addOp(writeInstr);
		switchReg(reg);

        int jumpPos = instrcount + 2;
        // reserved: Jump (Abs XX)

		// ensures only one thread-wait loop is created
		if (threadWait) {
			waitLoopPos = instrcount + 3;

			// begin waiting loop:
			Op readInstr = opGen("ReadInstr", "IndAddr regSprID");
			program.addOp(readInstr);

			int reg2 = nextReg(ctx);
			Op receive = opGen("Receive", toReg(reg2));
			program.addOp(receive);

			int reg3 = nextReg(ctx);
			Op compEq = opGen("Compute", "Equal", toReg(reg2), "reg0", toReg(reg3));
			program.addOp(compEq);

			Op branch2 = opGen("Branch", toReg(reg3), "Rel (-3)");
			program.addOp(branch2);

			Op jump2 = opGen("Jump", "Ind " + toReg(reg2));
			program.addOp(jump2);

			switchReg(reg2);
			switchReg(reg3);

			threadWait = false;
		}

		// branch at the start of fork
		Op branch = opGen("Branch", "regSprID", "Abs " + waitLoopPos);
		program.insertOp(branchPos, branch);

        // Load at the second instruction of fork (after branch)
        int loadIndex = instrcount + 2;
        Op load = opGen("Load", "ImmValue " + loadIndex, toReg(reg));
		program.insertOp(loadPos, load);

		// set currentThread to this thread
		currentThread = threadCount;
		this.regs.put(currentThread, new ParseTreeProperty<Integer>());
		this.regsInUse.put(currentThread, new boolean[regCount]);

        // start fork's block
        visit(ctx.block());

        // Set this thread's regSprID to 0 (signal done)
        program.addOp(opGen("WriteInstr", "reg0", "IndAddr regSprID"));
        // EndProg at the end of sprockell 1 fork
        program.addOp(opGen("EndProg"));

        // Jump to sprockell 0 part (before the waiting loop)
        int jumpIndex = instrcount + 1;
        Op jump = opGen("Jump", "Abs " + jumpIndex);
        program.insertOp(jumpPos, jump);

		unjoinedForks.add(currentThread);

		// set current thread to main
        currentThread = 0;
        return branch;
    }

    @Override
    public Op visitJoinStat(JoinStatContext ctx) {
        // joins all threads currently active

        String reg = toReg(nextReg(ctx));
        Op result = null;
        if (unjoinedForks.isEmpty()) {
            addError(ctx, "There are no open threads other than Sprockell 0 (main)");
        } else {
            result = joinLoop(reg, unjoinedForks.get(0));
        }

        for (int i = 1; i < unjoinedForks.size(); i++) {
            joinLoop(reg, unjoinedForks.get(i));
        }

        return result;
    }

    /** Creates a join loop for a certain sprockell with a register. */
    private Op joinLoop(String reg, int sprockell) {
        Op readInstr = opGen("ReadInstr", "DirAddr " + sprockell);
		program.addOp(readInstr);

        Op receive = opGen("Receive", reg);
        program.addOp(receive);

        Op compute = opGen("Compute", "NEq", reg, "reg0", reg);
        program.addOp(compute);

        Op branch = opGen("Branch", reg, "Rel (-3)");
		program.addOp(branch);

        return readInstr;
    }

	@Override
	public Op visitLockStat(LockStatContext ctx) {
		// lock
		int shMemLoc = 0; // TODO: make variable specific
		Op testAndSet = opGen("TestAndSet", "DirAddr " + shMemLoc);
		program.addOp(testAndSet);

		int n = nextReg(ctx);
		String reg = toReg(n);

		Op receive = opGen("Receive", reg);
		program.addOp(receive);

		Op compute = opGen("Compute", "Equal", reg, "reg0", reg);
		program.addOp(compute);

		Op branch = opGen("Branch", reg, "Rel (-3)");
		program.addOp(branch);

		switchReg(n);

		// block
		visit(ctx.block());

		// unlock
		Op write = opGen("WriteInstr", "reg0", "DirAddr " + shMemLoc);
		program.addOp(write);

		return testAndSet;
	}
	
	@Override
	public Op visitNotExpr(NotExprContext ctx) {
		Op result = visit(ctx.expr());

        int operand1 = regs.get(currentThread).removeFrom(ctx.expr());
        int target = nextReg(ctx);
        regs.get(currentThread).put(ctx, target);

		if (ctx.not().MINUS() == null) {
			Op compute = opGen("Compute", "Equal", toReg(operand1), "reg0", toReg(target));
			program.addOp(compute);
		} else {
			Op compute = opGen("Compute", "Sub", "reg0", toReg(operand1), toReg(target));
			program.addOp(compute);
		}
		return result;
	}
	
	@Override
	public Op visitMultExpr(MultExprContext ctx) {
		Op result = visit(ctx.expr(0));
		visit(ctx.expr(1));

        int operand1 = regs.get(currentThread).removeFrom(ctx.expr(0));
        int operand2 = regs.get(currentThread).removeFrom(ctx.expr(1));
		switchReg(operand1);
		switchReg(operand2);

        int target = nextReg(ctx);
        regs.get(currentThread).put(ctx, target);

		String op;
		if (ctx.multOp().MULT() == null) {
			op = "Div";
		} else {
			op = "Mul";
		}

		Op compute = opGen("Compute", op, toReg(operand1), toReg(operand2), toReg(target));
		program.addOp(compute);
		return result;
	}
	
	@Override
	public Op visitPlusExpr(PlusExprContext ctx) {
		Op result = visit(ctx.expr(0));
		visit(ctx.expr(1));

        int op1 = regs.get(currentThread).removeFrom(ctx.expr(0));
        int op2 = regs.get(currentThread).removeFrom(ctx.expr(1));
		switchReg(op1);
		switchReg(op2);

        int target = nextReg(ctx);
        regs.get(currentThread).put(ctx, target);

		String op;
		if (ctx.plusOp().PLUS() == null) {
			op = "Sub";
		} else {
			op = "Add";
		}

		Op compute = opGen("Compute", op, toReg(op1), toReg(op2), toReg(target));
		program.addOp(compute);
		return result;
	}
	
	@Override
	public Op visitCompExpr(CompExprContext ctx) {
		visit(ctx.expr(0));
		visit(ctx.expr(1));
        int op1 = regs.get(currentThread).removeFrom(ctx.expr(0));
        int op2 = regs.get(currentThread).removeFrom(ctx.expr(1));
		switchReg(op1);
		switchReg(op2);
        int target = nextReg(ctx);
        regs.get(currentThread).put(ctx, target);

		String op = "";
		if (ctx.compOp().getText().equalsIgnoreCase("==")) {
			op = "Equal";
		} else if (ctx.compOp().getText().equalsIgnoreCase(">=")) {
			op = "GtE";
		} else if (ctx.compOp().getText().equalsIgnoreCase(">")) {
			op = "Gt";
		} else if (ctx.compOp().getText().equalsIgnoreCase("<=")) {
			op = "LtE";
		} else if (ctx.compOp().getText().equalsIgnoreCase("<")) {
			op = "Lt";
		} else if (ctx.compOp().getText().equalsIgnoreCase("<>")) {
			op = "NEq";
		}

		Op compute = opGen("Compute", op, toReg(op1), toReg(op2), toReg(target));
		program.addOp(compute);
		return compute;
	}
	
	@Override
	public Op visitBoolOpExpr(BoolOpExprContext ctx) {
		Op result = visit(ctx.expr(0));
		visit(ctx.expr(1));

        int op1 = regs.get(currentThread).removeFrom(ctx.expr(0));
        int op2 = regs.get(currentThread).removeFrom(ctx.expr(1));
		switchReg(op1);
		switchReg(op2);
        int target = nextReg(ctx);
        regs.get(currentThread).put(ctx, target);

		String op;
		if (ctx.boolOp().AND() != null) {
			op = "And";
		} else {
			op = "Or";
		}

		Op compute = opGen("Compute", op, toReg(op1), toReg(op2), toReg(target));
		program.addOp(compute);
		return result;
	}
	
	@Override
	public Op visitParExpr(ParExprContext ctx) {
        Op result = visit(ctx.expr());
        int reg = regs.get(currentThread).removeFrom(ctx.expr());
		regs.get(currentThread).put(ctx, reg);
		return result;
	}
	
	@Override
	public Op visitVarExpr(VarExprContext ctx) {
		int reg = nextReg(ctx);
		boolean addReceive = false;

        int offset = 1;
        String opCode = null;
        if (checkResult.getGlobalOffset(ctx) != null) {
            offset = this.checkResult.getGlobalOffset(ctx);
            opCode = "ReadInstr";
			addReceive = true;
        } else if (checkResult.getOffset(ctx) != null) {
            offset = this.checkResult.getOffset(ctx);
            opCode = "Load";
        } else {
            addError(ctx, "The variable '%s' is undefined", ctx.getText());
        }

		Op result = opGen(opCode, "DirAddr " + offset, toReg(reg));

        regs.get(currentThread).put(ctx, reg);
		program.addOp(result);
		if (addReceive) {
			program.addOp(opGen("Receive", toReg(reg)));
		}
		return result;
	}
	
	@Override
	public Op visitNumExpr(NumExprContext ctx) {
		int reg = nextReg(ctx);
		String value = "ImmValue " + ctx.getText();
		Op result = opGen("Load", value, toReg(reg));

        regs.get(currentThread).put(ctx, reg);
		program.addOp(result);
		return result;
	}

    @Override
    public Op visitFalseExpr(FalseExprContext ctx) {
        int reg = nextReg(ctx);
		String value = "ImmValue " + Utils.FALSE_VALUE;
		Op result = opGen("Load", value, toReg(reg));

        regs.get(currentThread).put(ctx, reg);
		program.addOp(result);
        return result;
    }

    @Override
    public Op visitTrueExpr(TrueExprContext ctx) {
        int reg = nextReg(ctx);
		String value = "ImmValue " + Utils.TRUE_VALUE;
        Op result = opGen("Load", value, toReg(reg));

        regs.get(currentThread).put(ctx, reg);
        program.addOp(result);
        return result;
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
