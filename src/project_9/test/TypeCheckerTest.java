package project_9.test;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;
import project_9.AtlantisCompiler;
import project_9.ParseException;
import project_9.checker.CheckResult;
import project_9.checker.Type;

import static org.junit.Assert.*;

/**
 * Author:  Martijn
 * Date:    23-6-2016
 */
public class TypeCheckerTest {

    private final AtlantisCompiler compiler = AtlantisCompiler.instance();

    @Test
    public void test() throws ParseException {
        ParseTree tree = parse("basic");
        CheckResult res = check(tree);

        assertEquals(Type.INT, res.getType(tree.getChild(3).getChild(1).getChild(1)));
    }

    private ParseTree parse(String filename) throws ParseException {
        return this.compiler.parse(filename);
    }

    private CheckResult check(ParseTree tree) throws ParseException {
        return this.compiler.check(tree);
    }

}
