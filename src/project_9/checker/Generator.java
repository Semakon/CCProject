package project_9.checker;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import project_9.ParseException;
import project_9.Utils;
import project_9.atlantis.AtlantisBaseVisitor;
import project_9.atlantis.AtlantisParser.*;

import java.util.ArrayList;
import java.util.List;


/**
 * Class to generate SprIl code for Atlantis.
 * @author Kevin Booijink
 */
public class Generator extends AtlantisBaseVisitor<Op> {

	private Program program;
	/** The outcome of the checker phase. */
	private CheckResult checkResult;
	/** Register count, used to generate fresh registers. */
	private int regCount;
	/** ParseTreeProperty to keep track of which expression belongs to which register. */
	private ParseTreeProperty<Integer> regs;
	/** Array to indicate which registers are in use (True = in use, False = not in use). */
	private boolean[] regsInUse;
	/** Integer to keep track of the amount of instructions*/
	private int instrcount;

	private List<String> errors;
	
	
	/** Generates SprIl code for a given parse tree and a pre-computed checker result.*/
	public Program generate(ParseTree tree, CheckResult checkResult) throws ParseException {
		this.program = new Program();
		this.checkResult = checkResult;
		this.regs = new ParseTreeProperty<>();
		this.errors = new ArrayList<>();
		this.regCount = 5;
		this.instrcount = 0;
		this.regsInUse = new boolean[regCount];
		tree.accept(this);
		program.addOp(opGen("EndProg"));
		if (hasErrors()) {
			throw new ParseException(getErrors());
		}
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
		regsInUse[n] = !regsInUse[n];
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
			if (!regsInUse[i]) {
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
	
	/** Retrieves the offset of a variable node from the checker*/
	private int offset(ParseTree node) {
		return this.checkResult.getOffset(node);
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

		int reg = regs.get(ctx.expr());
		String target = "DirAddr " + offset(ctx.target());
		Op store = opGen("Store", toReg(reg), target);

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

            Integer reg = regs.get(ctx.expr());
            String target = "DirAddr " + offset(ctx.target());

            Op store = opGen("Store", toReg(reg), target);
            program.addOp(store);
            switchReg(reg);
        }
        return result;
	}
	
	@Override
	public Op visitIfStat(IfStatContext ctx) {
		Op result = visit(ctx.expr());
		int reg = regs.removeFrom(ctx.expr());
        regs.put(ctx, reg);

		// invert comparison to satisfy branch
		Op compute = opGen("Compute", "Equal", toReg(reg), "reg0", toReg(reg));
		program.addOp(compute);
		
		if (ctx.ELSE() != null) {
			int branchloc = program.getOperations().size();
			this.instrcount = 0;
			visit(ctx.block(0));

			int jumploc = program.getOperations().size() + 1;

			// adjust for two extra instructions
			int thencount = this.instrcount + 2;
			this.instrcount = 0;
			visit(ctx.block(1));

			// adjust for extra instruction
			int elsecount = this.instrcount + 1;

			// emit branch to then/else
			Op branch = opGen("Branch", toReg(reg), "Rel " + Integer.toString(thencount));
			program.addOpAt(branchloc,  branch);

			// emit jump to end of if-statement
			Op jump = opGen("Jump", "Rel " + Integer.toString(elsecount));
			program.addOpAt(jumploc, jump);
		} else {
			int branchloc = program.getOperations().size();
			this.instrcount = 0;
			visit(ctx.block(0));

			// adjust for extra instruction
			int count = this.instrcount + 1;

			// emit jump to end of if-statement
			Op branch = opGen("Branch", toReg(reg), "Rel " + Integer.toString(count));
			program.addOpAt(branchloc, branch);
		}
		switchReg(reg);
		return result;
	}
	
	@Override
	public Op visitWhileStat(WhileStatContext ctx) {
		int jumpLoc = program.getOperations().size();
		Op result = visit(ctx.expr());
		int reg = regs.removeFrom(ctx.expr());
		regs.put(ctx, reg);

		// invert comparison to satisfy branch
		Op compute = opGen("Compute", "Equal", toReg(reg), "reg0", toReg(reg));
		program.addOp(compute);

		int jumpTo = program.getOperations().size();

		this.instrcount = 0;
		visit(ctx.block());
		int endWhile = instrcount + 2;

		Op nop = opGen("Nop"); //<- Jump to here at end of while loop.
		program.addOp(nop);

		Utils.pr("Endwhile: " + endWhile, "Jumpto: " + jumpTo, "JumpLoc: " + jumpLoc);

		Op branch = opGen("Branch", toReg(reg), "Rel " + endWhile);
		program.addOpAt(jumpTo, branch);

		Op jump = opGen("Jump", "Abs " + jumpTo);
		program.addOpAt(jumpLoc, jump);
		switchReg(reg);
		return result;
	}
	
	@Override
	public Op visitNotExpr(NotExprContext ctx) {
		Op result = visit(ctx.expr());

        int operand1 = regs.removeFrom(ctx.expr());
        int target = nextReg(ctx);
        regs.put(ctx, target);

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

        int operand1 = regs.removeFrom(ctx.expr(0));
        int operand2 = regs.removeFrom(ctx.expr(1));
		switchReg(operand1);
		switchReg(operand2);

        int target = nextReg(ctx);
        regs.put(ctx, target);

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

        int op1 = regs.removeFrom(ctx.expr(0));
        int op2 = regs.removeFrom(ctx.expr(1));
		switchReg(op1);
		switchReg(op2);

        int target = nextReg(ctx);
        regs.put(ctx, target);

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
        int op1 = regs.removeFrom(ctx.expr(0));
        int op2 = regs.removeFrom(ctx.expr(1));
		switchReg(op1);
		switchReg(op2);
        int target = nextReg(ctx);
        regs.put(ctx, target);

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

        int op1 = regs.removeFrom(ctx.expr(0));
        int op2 = regs.removeFrom(ctx.expr(1));
		switchReg(op1);
		switchReg(op2);
        int target = nextReg(ctx);
        regs.put(ctx, target);

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
        int reg = regs.removeFrom(ctx);
		regs.put(ctx.expr(), reg); 
		return visit(ctx.expr());
	}
	
	@Override
	public Op visitVarExpr(VarExprContext ctx) {
		int reg = nextReg(ctx);
        int offset = this.checkResult.getOffset(ctx);
		Op result = opGen("Load", "DirAddr " + offset, toReg(reg));

        regs.put(ctx, reg);
		program.addOp(result);
		return result;
	}
	
	@Override
	public Op visitNumExpr(NumExprContext ctx) {
		int reg = nextReg(ctx);
		String value = "ImmValue " + ctx.getText();
		Op result = opGen("Load", value, toReg(reg));

        regs.put(ctx, reg);
		program.addOp(result);
		return result;
	}

    @Override
    public Op visitFalseExpr(FalseExprContext ctx) {
        int reg = nextReg(ctx);
		String value = "ImmValue " + Utils.FALSE_VALUE;
        Op result = opGen("Load", value, toReg(reg));

        regs.put(ctx, reg);
        program.addOp(result);
        return result;
    }

    @Override
    public Op visitTrueExpr(TrueExprContext ctx) {
        int reg = nextReg(ctx);
		String value = "ImmValue " + Utils.TRUE_VALUE;
        Op result = opGen("Load", value, toReg(reg));

        regs.put(ctx, reg);
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
