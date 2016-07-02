package project_9.test;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;
import project_9.AtlantisCompiler;
import project_9.ParseException;
import project_9.Utils;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.fail;

/**
 * Author:  Martijn
 * Date:    23-6-2016
 */
public class SyntaxTest {

    /** Instance of AtlantisCompiler */
    private final AtlantisCompiler compiler = AtlantisCompiler.instance();

    @Test
    public void test() throws IOException {
        try {
            parse("SyntaxFail");
            fail("Shouldn't have succeeded, but did.");
        } catch (ParseException e) {
            // this is the expected behaviour
            e.print();
        }
    }

    @Test
    public void testSyntax() throws IOException {
        try {
            parse("Syntax");
            // this is the expected behaviour
        } catch (ParseException e) {
            e.print();
            fail("Should have succeeded, but didn't.");
        }
    }

    /** Parses a file with <code>filename</code> into a parse tree. */
    private ParseTree parse(String filename) throws ParseException, IOException {
        return this.compiler.parse(new File(Utils.BASE_DIR + filename + Utils.EXT));
    }

}
