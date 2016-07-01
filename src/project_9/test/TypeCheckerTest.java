package project_9.test;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;
import project_9.AtlantisCompiler;
import project_9.ParseException;
import project_9.Utils;
import project_9.checker.CheckResult;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.fail;

/**
 * Author:  Martijn
 * Date:    23-6-2016
 */
public class TypeCheckerTest {

    private final AtlantisCompiler compiler = AtlantisCompiler.instance();

    @Test
    public void test() throws IOException {
        try {
            check("Basic");
            // expected behaviour
        } catch (ParseException e) {
            fail("Should have succeeded, but didn't.");
            e.print();
        }

    }

    @Test
    public void failTest() throws IOException {
        try {
            check("basicFail");
            fail("Should not have succeeded, but did.");
        } catch (ParseException e) {
            // expected behaviour
            e.print();
        }
    }

    private ParseTree parse(String filename) throws ParseException, IOException {
        return this.compiler.parse(new File(Utils.BASE_DIR + filename + Utils.EXT));
    }

    private CheckResult check(ParseTree tree) throws ParseException {
        return this.compiler.check(tree);
    }

    private CheckResult check(String filename) throws ParseException, IOException {
        return this.compiler.check(new File(Utils.BASE_DIR + filename + Utils.EXT));
    }

}
