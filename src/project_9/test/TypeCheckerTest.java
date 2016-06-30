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

import static org.junit.Assert.*;

/**
 * Author:  Martijn
 * Date:    23-6-2016
 */
public class TypeCheckerTest {

    private final AtlantisCompiler compiler = AtlantisCompiler.instance();

    @Test
    public void test() throws ParseException, IOException {
        ParseTree tree = parse("basic");
        CheckResult res = check(tree);

        assertEquals(Type.INT, res.getType(tree.getChild(3).getChild(1).getChild(1)));
    }

    @Test
    public void failTest() throws IOException {
        try {
            check("basicFail");
            fail("Should not have passed the checker.");
        } catch (ParseException e) {
            // expected behaviour
            e.print();
        }
    }

    private ParseTree parse(String filename) throws ParseException, IOException {
        File file = new File(Utils.BASE_DIR + filename + Utils.EXT);
        return this.compiler.parse(file);
    }

    private CheckResult check(ParseTree tree) throws ParseException {
        return this.compiler.check(tree);
    }

    private CheckResult check(String filename) throws ParseException, IOException {
        File file = new File(Utils.BASE_DIR + filename + Utils.EXT);
        return this.compiler.check(file);
    }

}
