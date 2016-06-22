import static org.junit.Assert.assertEquals;

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
public class ParseTest {

    private final AtlantisCompiler compiler = AtlantisCompiler.instance();

    @Test
    public void testBasic() throws IOException, ParseException {
        ParseTree tree = parse("basic");
        CheckResult result = check(tree);

        // debug
        Utils.pr(result.toString());

        ParseTree body = tree.getChild(3);
        assertEquals(Type.INT, result.getType(body.getChild(0).getChild(1)));
        assertEquals(Type.INT, result.getType(body.getChild(1).getChild(1)));
        assertEquals(Type.INT, result.getType(body.getChild(2).getChild(1)));
    }

    private ParseTree parse(String filename) throws IOException, ParseException {
        return this.compiler.parse(new File(Utils.BASE_DIR, filename + Utils.EXT));
    }

    private CheckResult check(ParseTree tree) throws ParseException {
        return this.compiler.check(tree);
    }

}
