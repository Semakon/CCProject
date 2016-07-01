package project_9.test;

import org.junit.Test;
import project_9.AtlantisCompiler;
import project_9.ParseException;
import project_9.Utils;
import project_9.checker.Program;

import java.io.File;
import java.io.IOException;

/**
 * Author:  Martijn
 * Date:    30-6-2016
 */
public class GeneratorTest {

    private final AtlantisCompiler compiler = AtlantisCompiler.instance();

    @Test
    public void basicTest() throws IOException, ParseException {
        String filename = "Basic";
        Program prog = compile(filename);

        System.out.println(prog.toString());
        for (String line : prog.generateCode(filename)) {
            System.out.println(line);
        }

        Utils.toHaskellFile(prog, filename);
    }

    @Test
    public void basicBlocksTest() throws IOException, ParseException {
        String filename = "BasicBlocks";
        Program prog = compile(filename);

//        System.out.println(prog.toString());
        for (String line : prog.generateCode(filename)) {
            System.out.println(line);
        }
    }

    private Program compile(String filename) throws IOException, ParseException {
        return this.compiler.compile(new File(Utils.BASE_DIR + filename + Utils.EXT));
    }

}
