package project_9.test;

import org.junit.Test;
import project_9.AtlantisCompiler;
import project_9.ParseException;
import project_9.Utils;
import project_9.checker.Program;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Author:  martijn
 * Date:    1-7-16.
 */
public class GeneratorTest {

    private final AtlantisCompiler compiler = AtlantisCompiler.instance();

    @Test
    public void basicTest() throws IOException, ParseException {
        String filename = "Basic";
        compileTest(filename);
    }

    @Test
    public void basicBlocksTest() throws IOException, ParseException {
        String filename = "BasicBlocks";
        compileTest(filename);

    }

    private void compileTest(String filename) throws IOException, ParseException {
        Program prog = compile(filename);
        System.out.println(prog.toString());

        List<String> code = prog.generateCode(filename);
        for (String aCode : code) {
            System.out.println(aCode);
        }

//        Utils.toHaskellFile(prog, filename);
    }

    private Program compile(String filename) throws IOException, ParseException {
        return this.compiler.compile(new File(Utils.BASE_DIR + filename + Utils.EXT));
    }

}
