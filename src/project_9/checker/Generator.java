package project_9.checker;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import project_9.ParseException;
import project_9.atlantis.AtlantisBaseVisitor;
import project_9.atlantis.AtlantisParser.*;


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
	/** List of instruction */
	private List<Op> instructions;
	/** List of errors that occured during generating. */
	private List<String> errors;
	
	
	/** Generates SprIl code for a given parse tree and a pre-computed checker result.*/
	public Program generate(ParseTree tree, CheckResult checkResult) throws ParseException {
		this.program = new Program();
		this.checkResult = checkResult;
		this.errors = new ArrayList<>();
		this.regs = new ParseTreeProperty<Integer>();
		this.regCount = 5;
		this.instrcount = 0;
		this.regsInUse = new boolean[regCount];
		tree.accept(this);
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
		String result = n > -1 && n < 26 ? "reg" + String.valueOf((char)(n + 65)) : null;
		return result;
	}

	private Reg addReg(int n) {
		String id = null;
		if (n >= 0 && n <= 25) {
			id = "reg" + String.valueOf((char)(n + 65));
		} else {
			addError("Invalid character ('%s') for generating a register.",
					String.valueOf((char)(n + 65)));
		}
		return new Reg(id);
	}
	
	/**
	 * Indicates the next free register
	 * @return The Integer for the next free register (starting at 0).
	 */
	private int nextReg() {
		for (int i = 0; i < regCount; i++) {
			if (!regsInUse[i]) {
				switchReg(i);
				return i;
			}
		}
		return -1;
	}
	
	
	/**
	 * Method to check whether all registers are in use.
	 * @return 	False if there is a "free" register, 
	 * 			True if otherwise.
	 */
	private boolean fullRegs() {
		for (int i = 0; i < regCount; i++) {
			if (!regsInUse[i]) {
				return false;
			}
		}
		return true;
	}
	/** Constructs an operation from the parameters
	 * @param opCode
	 * @param args
	 * @return
	 */
	private Op emit(String opCode, String... args) {
		Op result = new Op(opCode, args);
		this.instrcount++;
		return result;
	}
	
	/** Retrieves the offset of a variable node from the checker*/
	private Integer offset(ParseTree node) {
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
		visit(ctx.target());
		Integer reg = regs.get(ctx.expr());
		String target = "Diraddr " + offset(ctx.target()).toString();
		
		emit("Store", toReg(reg), target);
		switchReg(reg);
		return result;
	}
	
	@Override
	public Op visitIfStat(IfStatContext ctx) {
		Op result = visit(ctx.expr());
		Integer reg = regs.removeFrom(ctx.expr());
		regs.put(ctx, reg);
		
		if (ctx.ELSE() != null) {
			int branchloc = instructions.size();
			this.instrcount = 0;
			visit(ctx.block(0));
			int jumploc = instructions.size() + 1;
			int thencount = this.instrcount;
			this.instrcount = 0;
			visit(ctx.block(1));
			int elsecount = this.instrcount;
			Op branch = emit("Branch", toReg(reg), "Rel", Integer.toString(thencount));
			instructions.add(branchloc, branch);
			Op jump = emit("Jump", "Rel", Integer.toString(elsecount));
			instructions.add(jumploc, jump);
		} else {
			int branchloc = instructions.size();
			this.instrcount = 0;
			visit(ctx.block(0));
			int count = this.instrcount;
			Op branch = emit("Branch", toReg(reg), "Rel", Integer.toString(count));
			instructions.add(branchloc, branch);
		}
		switchReg(reg);
		return result;
	}
	
	@Override
	public Op visitWhileStat(WhileStatContext ctx) {
		int jumpTo = instructions.size();
		this.instrcount = 0;
		Op result = visit(ctx.expr());
		Op block = visit(ctx.block());
		Op nop = emit("Nop"); //<- Jump to here at end of while loop.
		int endWhile = instructions.size() - 1;
		Integer reg = regs.removeFrom(ctx.expr());
		regs.put(ctx, reg);
		Op branch = emit("Branch", toReg(reg), "Abs", Integer.toString(endWhile));
		instructions.add(jumpTo, branch);
		Op jump = emit("Jump", "Abs", Integer.toBinaryString(jumpTo));
		instructions.add(endWhile, jump);
		switchReg(reg);
		return result;
	}
	
	@Override
	public Op visitNotExpr(NotExprContext ctx) {
		Op result = visit(ctx.expr());
		
		Integer operand1 = regs.removeFrom(ctx.expr());
		regs.put(ctx, operand1);
		Integer target = nextReg();
		if (ctx.not().MINUS() == null) {
			Op compute = emit("Compute", "NEq", toReg(operand1), "reg0", toReg(target));
			instructions.add(compute);
		} else {
			Op compute = emit("Compute", "Sub", "reg0", toReg(operand1), toReg(target));
			instructions.add(compute);
		}
		return result;
	}
	
	@Override
	public Op visitMultExpr(MultExprContext ctx) {
		Op result = visit(ctx.expr(0));
		visit(ctx.expr(1));
		
		Integer operand1 = regs.removeFrom(ctx.expr(0));
		Integer operand2 = regs.removeFrom(ctx.expr(1));
		switchReg(operand1);
		switchReg(operand2);
		Integer target = nextReg();
		String op;
		
		if (ctx.multOp().MULT() == null) {
			op = "Div";
		} else {
			op = "Mul";
		}
		
		Op compute = emit("Compute", op, toReg(operand1), toReg(operand2), toReg(target));
		instructions.add(compute);
		return result;
	}
	
	@Override
	public Op visitPlusExpr(PlusExprContext ctx) {
		Op result = visit(ctx.expr(0));
		visit(ctx.expr(1));
		
		Integer op1 = regs.removeFrom(ctx.expr(0));
		Integer op2 = regs.removeFrom(ctx.expr(1));
		switchReg(op1);
		switchReg(op2);
		Integer target = nextReg();
		String op;
		
		if (ctx.plusOp().PLUS() == null) {
			op = "Sub";
		} else {
			op = "Add";
		}
		Op compute = emit("Compute", op, toReg(op1), toReg(op2), toReg(target));
		instructions.add(compute);
		return compute;
	}
	
	@Override
	public Op visitCompExpr(CompExprContext ctx) {
		visit(ctx.expr(0));
		visit(ctx.expr(1));
		Integer op1 = regs.removeFrom(ctx.expr(0));
		Integer op2 = regs.removeFrom(ctx.expr(1));
		switchReg(op1);
		switchReg(op2);
		Integer target = nextReg();
		String op = "";
		
		if (ctx.compOp().getText().equalsIgnoreCase("=")) {
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
		
		Op compute = emit("Compute", op, toReg(op1), toReg(op2), toReg(target));
		instructions.add(compute);
		return compute;
	}
	
	@Override
	public Op visitBoolOpExpr(BoolOpExprContext ctx) {
		Op result = visit(ctx.expr(0));
		visit(ctx.expr(1));
		
		Integer op1 = regs.removeFrom(ctx.expr(0));
		Integer op2 = regs.removeFrom(ctx.expr(1));
		switchReg(op1);
		switchReg(op2);
		Integer target = nextReg();
		String op;
		
		if (ctx.boolOp().AND() != null) {
			op = "And";
		} else {
			op = "Or";
		}
		Op compute = emit("Compute", op, toReg(op1), toReg(op2), toReg(target));
		instructions.add(compute);
		return compute;
	}
	
	@Override
	public Op visitParExpr(ParExprContext ctx) {
		Integer reg = regs.removeFrom(ctx);
		regs.put(ctx.expr(), reg); 
		return visit(ctx.expr());
	}
	
	@Override
	public Op visitVarExpr(VarExprContext ctx) {
		String text = ctx.VAR().getText();
		Integer reg = nextReg();
		Op emit = emit("Load", text, toReg(reg));
		instructions.add(emit);
		return emit;
	}
	
	@Override
	public Op visitNumExpr(NumExprContext ctx) {
		Integer value = Integer.parseInt(ctx.getText());
		Integer reg = nextReg();
		Op emit = emit("Ldconst", value.toString(), toReg(reg));
		instructions.add(emit);
		return emit;
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

	/**
	 * Records a general error.
	 * @param message the error message
	 * @param args arguments for the message, see {@link String#format}
	 */
	private void addError(String message, Object... args) {
		message = String.format(message, args);
		this.errors.add(message);
	}

}
