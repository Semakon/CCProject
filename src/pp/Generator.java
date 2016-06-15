package pp;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import com.sun.org.apache.xpath.internal.compiler.OpCodes;

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
		
		emit(OpCode.Store, operand1, something, offset);
		return result;
	}
	
	@Override
	public Op visitIfStat(IfStatContext ctx) {
		Op result = visit(ctx.expr());
		Reg reg = reg(ctx.expr());
		setReg(ctx, reg);
		
		if (ctx.ELSE() != null) {
			emit(OpCode.)
		}
	}
}
