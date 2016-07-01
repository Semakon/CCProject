import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import project_9.AtlantisCompiler;
import project_9.checker.CheckResult;
import project_9.checker.Type;
import project_9.ParseException;
import project_9.Utils;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Author:  Martijn
 * Date:    15-6-2016
 */
public class CompileTest {

    private final AtlantisCompiler compiler = AtlantisCompiler.instance();

    @Test
    public void testBasic() throws IOException, ParseException {
        ParseTree tree = parse("basic");
        CheckResult result = check(tree);

        ParseTree body = tree.getChild(3);

        assertEquals(Type.INT, result.getType(body.getChild(1).getChild(1)));
        assertEquals(Type.STR, result.getType(body.getChild(3).getChild(1)));
        assertEquals(Type.BOOL, result.getType(body.getChild(5).getChild(1)));

        assertEquals(Type.INT, result.getType(body.getChild(7).getChild(0)));
        assertEquals(Type.STR, result.getType(body.getChild(9).getChild(0)));
        assertEquals(Type.BOOL, result.getType(body.getChild(11).getChild(0)));
    }

    @Test
    public void testBasicExpr() throws IOException, ParseException {
        ParseTree tree = parse("basicExpr");
        CheckResult result = check(tree);

        ParseTree body = tree.getChild(3);
        ParseTree stat1 = body.getChild(5);
        ParseTree stat2 = body.getChild(13);

        assertEquals(Type.INT, result.getType(stat1.getChild(1)));               // type of 'z'
        assertEquals(Type.INT, result.getType(stat1.getChild(3).getChild(0)));   // type of 'x'
        assertEquals(Type.INT, result.getType(stat1.getChild(3).getChild(2)));   // type of 'y'
        assertEquals(Type.INT, result.getType(body.getChild(7).getChild(0)));    // type of 'z'

        assertEquals(Type.BOOL, result.getType(stat2.getChild(1)));              // type of 'both'
        assertEquals(Type.BOOL, result.getType(stat2.getChild(3).getChild(0)));  // type of 'isTrue'
        assertEquals(Type.BOOL, result.getType(stat2.getChild(3).getChild(2)));  // type of 'isFalse'
        assertEquals(Type.BOOL, result.getType(body.getChild(15).getChild(0)));  // type of 'both'
    }

    @Test
    public void testBasicBlocks() throws IOException, ParseException {
        ParseTree tree = parse("basicBlocks");
        CheckResult result = check(tree);

        ParseTree body = tree.getChild(3);
        ParseTree whileLoop = body.getChild(5);
        ParseTree ifBlock = body.getChild(9);

        assertEquals(Type.BOOL, result.getType(whileLoop.getChild(1)));     // type of 'x >= 0'
        assertEquals(Type.BOOL, result.getType(ifBlock.getChild(1)));       // type of 'i == 6'
    }

    @Test
    public void testBasicFail() throws IOException {
        try {
            ParseTree tree = parse("basicFail");
            check(tree);
            fail("Should not have passed, but did.");
        } catch (ParseException e) {
            // this is the expected behaviour.
        }
    }

    private ParseTree parse(String filename) throws IOException, ParseException {
        return this.compiler.parse(new File(Utils.BASE_DIR, filename + Utils.EXT));
    }

    private CheckResult check(ParseTree tree) throws ParseException {
        return this.compiler.check(tree);
    }

}
