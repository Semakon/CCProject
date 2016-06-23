package project_9.checker;

import org.antlr.v4.runtime.atn.SemanticContext.Operator;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import com.sun.org.apache.xpath.internal.compiler.OpCodes;

import jdk.nashorn.internal.runtime.regexp.joni.constants.OPCode;
import pp.AtlantisBaseVisitor;
import pp.AtlantisLexer;
import pp.AtlantisParser;
import pp.Op;
import pp.AtlantisParser.*;


/**
 * Class to generate SprIl code for Atlantis.
 * @author Kevin Booijink
 */
public class Generator extends AtlantisBaseVisitor<Op> {

	private String program;
	/** The outcome of the checker phase. */
	private CheckResult checkResult;
	/** Register count, used to generate fresh registers. */
	private int regCount;
	/** ParseTreeProperty to keep track of which expression belongs to which register. */
	private ParseTreeProperty<Integer> regs;
	/** Array to indicate which registers are in use (True = in use, False = not in use). */
	private boolean[] regsInUse;
	
	
	/** Generates SprIl code for a given parse tree and a pre-computed checker result.*/
	public String generate(ParseTree tree, CheckResult checkResult) {
		this.program = "";
		this.checkResult = checkResult;
		this.regs = new ParseTreeProperty<Integer>();
		this.regCount = 5;
		this.regsInUse = new boolean[regCount];
		tree.accept(this);
		return program;
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
	
	/**
	 * Indicates the next free register
	 * @return The Integer for the next free register (starting at 0).
	 */
	private int nextReg() {
		for (int i = 0; i < regCount; i++) {
			if (!regsInUse[i]) {
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
		visit(ctx.VAR());
		Integer reg = regs.get(ctx.expr());
		String target = "Diraddr " + offset(ctx.VAR()).toString();
		
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
			emit("Branch", toReg(reg) /*Jump to else-expression*/);
			visit(ctx.block(0));
			emit("Jump" /*Jump to end of expression*/);
			visit(ctx.block(1));
		} else {
			emit("Branch", toReg(reg) /*Jump to end of expression*/);
			visit(ctx.block(0));
		}
		switchReg(reg);
		return result;
	}
	
	@Override
	public Op visitWhileStat(WhileStatContext ctx) {
		Op result = visit(ctx.expr());
		Integer reg = regs.removeFrom(ctx.expr());
		regs.put(ctx, reg);
		emit("Branch", toReg(reg) /*End of expression*/);
		visit(ctx.block());
		emit("Jump" /*Start of this while*/);
		emit("Nop"); //<- Jump to here at end of while loop.
		
		
		
		switchReg(reg);
		return result;
	}
	/* Can not find instructions for IO.
	@Override
	public Op visitInStat(InStatContext ctx) {
		Integer target = nextReg();
		String text = ctx.STR().getText().replaceAll("\"", "");		
		Op result = emit(OpCode.write, new Str(text), target);
		emit("store", /* args);
		return result;
	}
	
	@Override
	public Op visitOutStat(OutStatContext ctx) {
		Op result = visit(ctx.expr());
		String text = ctx.STR().getText().replaceAll("\"", "");
		emit("read", new Str(text), reg(ctx.expr()));
		return result;
	}
	*/
	@Override
	public Op visitNotExpr(NotExprContext ctx) {
		Op result = visit(ctx.expr());
		
		Integer operand1 = regs.removeFrom(ctx.expr());
		regs.put(ctx, operand1);
		Integer target = nextReg();
		if (ctx.not().MINUS() == null) {
			emit("Compute", "NEq", toReg(operand1), "reg0", toReg(target));
		} else {
			emit("Compute", "Sub", "reg0", toReg(operand1), toReg(target));
		}
		return result;
	}
	/* No easy way for powers in SpRIL
	@Override
	public Op visitHatExpr(HatExprContext ctx) {
		return null;
	}
	*/
	
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
		
		emit("Compute", op, toReg(operand1), toReg(operand2), toReg(target));
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
		emit("Compute", op, toReg(op1), toReg(op2), toReg(target));
		return result;
	}
	
	@Override
	public Op visitCompExpr(CompExprContext ctx) {
		Op result = visit(ctx.expr(0));
		visit(ctx.expr(1));
		Integer op1 = regs.removeFrom(ctx.expr(0));
		Integer op2 = regs.removeFrom(ctx.expr(1));
		switchReg(op1);
		switchReg(op2);
		Integer target = nextReg();
		String op;
		
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
		
		emit("Compute", op, toReg(op1), toReg(op2), toReg(target));
		return result;
	}
	
	@Override
	public Op visitBoolExpr(BoolExprContext ctx) {
		Op result = visit(ctx.expr(0));
		visit(ctx.expr(1));
		
		Operand op1 = reg(ctx.expr(0));
		Operand op2 = reg(ctx.expr(1));
		Reg target = reg(ctx);
		String op;
		
		if (ctx.boolOp().AND() != null) {
			op = "And";
		} else {
			op = "Or";
		}
		emit("compute", op, op1, op2, target);
		return result;
	}
	
	@Override
	public Op visitParExpr(ParExprContext ctx) {
		setReg(ctx, reg(ctx.expr()));
		return visit(ctx.expr());
	}
	
	@Override
	public Op visitCallExpr(CallExprContext ctx) {
		//Call a function
		return null;
	}
	
	@Override
	public Op visitVarExpr(VarExprContext ctx) {
		return emit("load", reg(ctx));
	}
	
	@Override
	public Op visitNumExpr(NumExprContext ctx) {
		int value = Integer.parseInt(ctx.getText());
		return emit("load", value/*registers*/);
	}
	
	@Override
	public Op visitBoolExr(BoolExrContext ctx) {
		if (ctx.BOOL().getSymbol().equals(AtlantisLexer.TRUE)) {
			return emit("load", TRUE_VALUE, reg(ctx));
		} else {
			return emit("load", FALSE_VALUE, reg(ctx));
		}
	}
}
