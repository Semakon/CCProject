package pp;

import org.antlr.v4.runtime.atn.SemanticContext.Operator;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import com.sun.org.apache.xpath.internal.compiler.OpCodes;

import jdk.nashorn.internal.runtime.regexp.joni.constants.OPCode;
import pp.AtlantisParser.*;


/**
 * Class to generate SprIl code for Atlantis.
 * @author Kevin Booijink
 */
public class Generator extends AtlantisBaseVisitor<Op> {


	/** The outcome of the checker phase. */
	private Result checkResult;
	/** Register count, used to generate fresh registers. */
	private int regCount;
	/** Association of expression/target nodes to registers. */
	private ParseTreeProperty<Reg> regs;
	
	
	/** Generates SprIl code for a given parse tree and a pre-computed checker result.*/
	public String generate(ParseTree tree, Result checkResult) {
		this.prog = new Program();
		this.checkResult = checkResult;
		this.regs = new ParseTreeProperty<>();
		this.regCount = 0;
		tree.accept(this);
		return this.prog;
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
	private Num offset(ParseTree node) {
		return new Num(this.checkResult.getOffset(node));
	}
	
	/** Returns a register for a given parse tree node,
	 * creating a fresh register if there is none for that node. */
	private Reg reg(ParseTree node) {
		Reg result = this.regs.get(node);
		if (result == null) {
			result = new Reg("r_" + this.regCount);
			this.regs.put(node, result);
			this.regCount++;
		}
		return result;
	}
	
	/** Assigns a register to a given parse tree node. */
	private void setReg(ParseTree node) {
		this.regs.put(node, reg);
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
	}
	
	@Override
	public Op visitAssStat(AssStatContext ctx) {
		Op result = visit(ctx.expr());
		visit(ctx.VAR());
		Operand operand1 = reg(ctx.expr());
		Operand offset = offset(ctx.VAR());
		
		emit("store", operand1, something, offset);
		return result;
	}
	
	@Override
	public Op visitIfStat(IfStatContext ctx) {
		Op result = visit(ctx.expr());
		Reg reg = reg(ctx.expr());
		setReg(ctx, reg);
		
		if (ctx.ELSE() != null) {
			emit("branch", reg, val);
			visit(ctx.block(0)).setLabel();//label
			emit("jump", endreg)
			visit(ctx.block(1)).setLabel();
		} else {
			emit("branch", reg, toEnd);
			visit(ctx.block(0)).setLabel();
		}
		
		return result;
	}
	
	@Override
	public Op visitWhileStat(WhileStatContext ctx) {
		Op result = visit(ctx.expr());
		Reg reg = reg(ctx.expr());
		setReg(ctx, reg);
		emit("branch", reg, /*args*/);
		visit(ctx.block()).setLabel();
		emit("jump", whileLabel);
		emit(endLabel, OpCode.nop);
		
		return result;
	}
	
	@Override
	public Op visitInStat(InStatContext ctx) {
		Reg target = reg(ctx.target());
		String text - ctx.STR().getText().replaceAll("\"", "");
		
		Op result = emit(OpCode.write, new Str(text), target);
		emit("store", /* args*/);
		return result;
	}
	
	@Override
	public Op visitOutStat(OutStatContext ctx) {
		Op result = visit(ctx.expr());
		String text = ctx.STR().getText().replaceAll("\"", "");
		emit("read", new Str(text), reg(ctx.expr()));
		return result;
	}
	
	@Override
	public Op visitNotExpr(NotExprContext ctx) {
		Op result = visit(ctx.expr());
		
		Operand operand1 = reg(ctx.expr());
		Operand operand2;
		Operand target = reg(ctx);
		String opCode;
		
		if (ctx.not().MINUS() == null) {
			operand2 = TRUE_VALUE;
			opCode = "doRevert";
			emit(/*revert value*/);
		} else {
			operand2 = new Num(0);
			opCode = "compute";
			emit(opCode, "Sub", operand1, operand2, operand1);
		}
		return result;
	}
	
	@Override
	public Op visitHatExpr(HatExprContext ctx) {
		return null;
	}
	
	@Override
	public Op visitMultExpr(MultExprContext ctx) {
		Op result = visit(ctx.expr(0));
		visit(ctx.expr(1));
		
		Operand operand1 = reg(ctx.expr(0));
		Operand operand2 = reg(ctx.expr(1));
		Operand target = reg(ctx);
		String op;
		
		if (ctx.multOp().MULT() == null) {
			op = "Div";
		} else {
			op = "Mul";
		}
		
		emit("compute, op, operand1, operand2, target);
		return result;
	}
	
	@Override
	public Op visitPlusExpr(PlusExprContext ctx) {
		Op result = visit(ctx.expr(0));
		visit(ctx.expr(1));
		
		Operand op1 = reg(ctx.expr(0));
		Operand op2 = reg(ctx.expr(2));
		Operand target = reg(ctx);
		String op;
		
		if (ctx.plusOp().PLUS() == null) {
			op = "Sub";
		} else {
			op = "Add";
		}
		emit("compute", op, op1, op2, target);
	}
	
	@Override
	public Op visitCompExpr(CompExprContext ctx) {
		Op result = visit(ctx.expr(0));
		visit(ctx.expr(1));
		Operand op1 = reg(ctx.expr(0));
		Operand op2 = reg(ctx.expr(1));
		Reg target = reg(ctx);
		Oper op;
		
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
		
		emit("compute", op, op1, op2, target);
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
