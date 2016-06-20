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
 *
 */
public class Generator extends AtlantisBaseVisitor</*TODO: Make some kind of operation*/> {

	/** The resulting program. */
	private Program prog;
	/** The outcome of the checker phase. */
	private Result checkResult;
	/** Register count, used to generate fresh registers. */
	private int regCount;
	/** Association of expression/target nodes to registers. */
	private ParseTreeProperty<Reg> regs;
	
	
	/** Generates SprIl code for a given parse tree and a pre-computed checker result.*/
	public Program generate(ParseTree tree, Result checkResult) {
		this.prog = new Program();
		this.checkResult = checkResult;
		this.regs = new ParseTreeProperty<>();
		this.regCount = 0;
		tree.accept(this);
		return this.prog;
	}
	
	/** Constructs an operation from the parameters
	 * and adds it to the program under construction
	 * @param opCode
	 * @param args
	 * @return
	 */
	private Op emit(OpCode opCode, Operand... args) {
		Op result = new Op( opCode, args);
		this.prog.addInstr(result);
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
		
		emit(OpCode.store, operand1, something, offset);
		return result;
	}
	
	@Override
	public Op visitIfStat(IfStatContext ctx) {
		Op result = visit(ctx.expr());
		Reg reg = reg(ctx.expr());
		setReg(ctx, reg);
		
		if (ctx.ELSE() != null) {
			emit(OpCode.branch, reg, val);
			visit(ctx.block(0)).setLabel();//label
			emit(OpCode.jump, endreg)
			visit(ctx.block(1)).setLabel();
		} else {
			emit(OpCode.cbr, reg, toEnd);
			visit(ctx.block(0)).setLabel();
		}
		
		return result;
	}
	
	@Override
	public Op visitWhileStat(WhileStatContext ctx) {
		Op result = visit(ctx.expr());
		Reg reg = reg(ctx.expr());
		setReg(ctx, reg);
		emit(OpCode.branch, reg, /*args*/);
		visit(ctx.block()).setLabel();
		emit(OpCode.jump, whileLabel);
		emit(endLabel, OpCode.nop);
		
		return result;
	}
	
	@Override
	public Op visitInStat(InStatContext ctx) {
		Reg target = reg(ctx.target());
		String text - ctx.STR().getText().replaceAll("\"", "");
		
		Op result = emit(OpCode.write, new Str(text), target);
		emit(OpCode.store, /* args*/);
		return result;
	}
	
	@Override
	public Op visitOutStat(OutStatContext ctx) {
		Op result = visit(ctx.expr());
		String text = ctx.STR().getText().replaceAll("\"", "");
		emit(OpCode.read, new Str(text), reg(ctx.expr()));
		return result;
	}
	
	@Override
	public Op visitNotExpr(NotExprContext ctx) {
		Op result = visit(ctx.expr());
		
		Operand operand1 = reg(ctx.expr());
		Operand operand2;
		Operand target = reg(ctx);
		OpCode opCode;
		
		if (ctx.not().MINUS() == null) {
			operand2 = TRUE_VALUE;
			opCode = OpCode.xor;
			emit(/*revert value*/);
		} else {
			operand2 = new Num(0);
			opCode = OpCode.compute;
			emit(opCode, minus, operand1, operand2, operand1);
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
		Operator op;
		
		if (ctx.multOp().MULT() == null) {
			op = OpCode.div;
		} else {
			op = OpCode.mult;
		}
		
		emit(OpCode.compute, op, operand1, operand2, target);
		return result;
	}
	
	@Override
	public Op visitPlusExpr(PlusExprContext ctx) {
		Op result = visit(ctx.expr(0));
		visit(ctx.expr(1));
		
		Operand op1 = reg(ctx.expr(0));
		Operand op2 = reg(ctx.expr(2));
		Operand target = reg(ctx);
		Oper op;
		
		if (ctx.plusOp().PLUS() == null) {
			op = Oper.sub;
		} else {
			op = Oper.add;
		}
		emit(OpCode.compute, op, op1, op2, target);
	}
}
