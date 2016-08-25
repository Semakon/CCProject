package project_9.test;

import org.junit.Test;
import project_9.AtlantisCompiler;
import project_9.ParseException;
import project_9.Utils;
import project_9.generator.Program;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.fail;

/**
 * A test class that compiles Atlantis programs and checks for any errors.
 *
 * Author:  Martijn
 * Date:    30-6-2016
 */
public class GeneratorTest {

    /** Instance of AtlantisCompiler */
    private final AtlantisCompiler compiler = AtlantisCompiler.instance();

    /** Tests compiling of Basic.atl */
    @Test
    public void basicTest() throws IOException, ParseException {
        try {
            test("Basic");
        } catch (ParseException e) {
            e.print();
            fail("Program should've compiled, but didn't.");
        }
    }

    /** Tests compiling of Blocks.atl */
    @Test
    public void blocksTest() throws IOException, ParseException {
        try {
            test("Blocks");
        } catch (ParseException e) {
            e.print();
            fail("Program should've compiled, but didn't.");
        }
    }

    /** Tests compiling of IfStatment.atl */
    @Test
    public void ifStatmentTest() throws IOException {
        try {
            test("IfStatement");
        } catch (ParseException e) {
            e.print();
            fail("Program should've compiled, but didn't.");
        }
    }

    /** Tests compiling of Scopes.atl */
    @Test
    public void scopesTest() throws IOException {
        try {
            test("Scopes");
        } catch (ParseException e) {
            e.print();
            fail("Program should've compiled, but didn't.");
        }
    }

    /** Tests compiling of WhileLoop.atl */
    @Test
    public void whileLoopTest() throws IOException {
        try {
            test("WhileLoop");
        } catch (ParseException e) {
            e.print();
            fail("Program should've compiled, but didn't.");
        }
    }

    /** Tests compiling of Gcd.atl */
    @Test
    public void gcdTest() throws IOException {
        try {
            test("Gcd");
        } catch (ParseException e) {
            e.print();
            fail("Program should've compiled, but didn't.");
        }
    }

    /** Tests compiling of ForkTest.atl */
    @Test
    public void forkTest() throws IOException, ParseException {
        try {
            test("ForkTest");
        } catch (ParseException e) {
            e.print();
            fail("Program should've compiled, but didn't.");
        }
    }

    /**
     * Prints a file with name <code>filename</code> and writes it to a haskell file.
     * @param filename The name of the file.
     * @throws IOException
     * @throws ParseException
     */
    private void test(String filename) throws IOException, ParseException {
        Program prog = compile(filename);

        System.out.println(prog.toString());
        for (String line : prog.generateCode(filename)) {
            System.out.println(line);
        }

//        Utils.toHaskellFile(prog, filename);
    }

    /**
     * Creates a Program object from a file with name <code>filename</code>.
     * @param filename The name of the file.
     * @return A Program object.
     */
    private Program compile(String filename) throws IOException, ParseException {
        return this.compiler.compile(new File(Utils.BASE_DIR + filename + Utils.EXT));
    }

}
