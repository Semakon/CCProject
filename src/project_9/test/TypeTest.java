package project_9.test;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;
import project_9.AtlantisCompiler;
import project_9.ParseException;
import project_9.Utils;
import project_9.checker.CheckResult;
import project_9.checker.Type;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test class to test types in a few Atlantis programs.
 *
 * Author:  Martijn
 * Date:    15-6-2016
 */
public class TypeTest {

    /** Instance of AtlantisCompiler */
    private final AtlantisCompiler compiler = AtlantisCompiler.instance();

    /** Tests types of Basic.atl */
    @Test
    public void testBasic() throws IOException {
        try {
            ParseTree tree = parse("Basic");
            CheckResult result = check(tree);

            ParseTree body = tree.getChild(3);

            assertEquals(Type.INT, result.getType(body.getChild(1).getChild(1)));
            assertEquals(Type.INT, result.getType(body.getChild(3).getChild(1)));
            assertEquals(Type.INT, result.getType(body.getChild(5).getChild(1)));

            assertEquals(Type.INT, result.getType(body.getChild(7).getChild(0)));
            assertEquals(Type.INT, result.getType(body.getChild(9).getChild(0)));
            assertEquals(Type.INT, result.getType(body.getChild(11).getChild(0)));

            assertEquals(Type.BOOL, result.getType(body.getChild(13).getChild(1)));
            assertEquals(Type.BOOL, result.getType(body.getChild(15).getChild(1)));
            assertEquals(Type.BOOL, result.getType(body.getChild(17).getChild(1)));

            assertEquals(Type.BOOL, result.getType(body.getChild(19).getChild(0)));
            assertEquals(Type.BOOL, result.getType(body.getChild(21).getChild(0)));
            assertEquals(Type.BOOL, result.getType(body.getChild(23).getChild(0)));

            // expected behaviour
        } catch (ParseException e) {
            e.print();
            fail("Should have parsed, but didn't.");
        }
    }

    /** Tests types of Blocks.atl */
    @Test
    public void testBlocks() throws IOException {
        try {
            ParseTree tree = parse("Blocks");
            CheckResult result = check(tree);

            ParseTree body = tree.getChild(3);

            assertEquals(Type.BOOL, result.getType(body.getChild(3).getChild(1)));  // type of 'x >= 0'
            assertEquals(Type.BOOL, result.getType(body.getChild(5).getChild(1)));  // type of 'i == 6'

            // expected behaviour
        } catch (ParseException e) {
            e.print();
            fail("Should have parsed, but didn't.");
        }
    }

    /** Tests types of Scopes.atl */
    @Test
    public void testScopes() throws IOException {
        try {
            ParseTree tree = parse("Scopes");
            CheckResult result = check(tree);

            ParseTree body = tree.getChild(3);
            ParseTree fstIf = body.getChild(5).getChild(3);
            ParseTree sndIf = fstIf.getChild(3).getChild(3);
            ParseTree thrIf = fstIf.getChild(5).getChild(3);

            assertEquals(Type.INT, result.getType(body.getChild(1).getChild(1)));
            assertEquals(Type.BOOL, result.getType(fstIf.getChild(1).getChild(1)));
            assertEquals(Type.INT, result.getType(sndIf.getChild(1).getChild(1)));
            assertEquals(Type.BOOL, result.getType(thrIf.getChild(1).getChild(1)));
            assertEquals(Type.BOOL, result.getType(thrIf.getChild(3).getChild(0)));

        } catch (ParseException e) {
            e.print();
            fail("Should have succeeded, but didn't");
        }
    }

    /** Tests types of BlocksFail.atl */
    @Test
    public void testBlocksFail() throws IOException {
        try {
            check("BlocksFail");
            fail("Should not have succeeded, but did.");
        } catch (ParseException e) {
            // this is the expected behaviour.
            e.print();
            assertEquals(2, e.getMessages().size());
        }
    }

    /** Tests types of BasicFail.atl */
    @Test
    public void testBasicFail() throws IOException {
        try {
            check("BasicFail");
            fail("Should not have passed, but did.");
        } catch (ParseException e) {
            // this is the expected behaviour.
            e.print();
            assertEquals(5, e.getMessages().size());
        }
    }

    /** Tests types of ScopesFail.atl */
    @Test
    public void testScopesFail() throws IOException {
        try {
            check("ScopesFail");
            fail("Shouldn't have succeeded, but did.");
        } catch (ParseException e) {
            // this is the expected behaviour.
            e.print();
            assertEquals(3, e.getMessages().size());
        }
    }

    /** Checks a file with <code>filename</code> and returns a CheckResult. */
    private CheckResult check(String filename) throws ParseException, IOException {
        return check(parse(filename));
    }

    /** Parses a file with <code>filename</code> into a parse tree. */
    private ParseTree parse(String filename) throws IOException, ParseException {
        return this.compiler.parse(new File(Utils.BASE_DIR, filename + Utils.EXT));
    }

    /** Checks a parse tree and returns a CheckResult. */
    private CheckResult check(ParseTree tree) throws ParseException {
        return this.compiler.check(tree);
    }


}
