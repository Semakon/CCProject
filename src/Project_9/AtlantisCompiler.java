package project_9;

import project_9.atlantis.AtlantisLexer;
import project_9.atlantis.AtlantisParser;
import project_9.checker.CheckResult;
import project_9.checker.TypeChecker;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Author:  Martijn
 * Date:    22-6-2016
 */
public class AtlantisCompiler {

    /** The singleton instance of this class. */
    private final static AtlantisCompiler instance = new AtlantisCompiler();

    /** The fixed TypeChecker of this compiler. */
    private final TypeChecker checker;

    /** Compiles and runs the program named in the argument. */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: filename");
            return;
        }
        try {
            System.out.println("--- Running " + args[0]);
            ParseTree res = instance().parse(new File(args[0]));
            System.out.println(res);
            System.out.println("--- Done with " + args[0]);
        } catch (ParseException exc) {
            exc.print();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    /** This class' constructor. */
    public AtlantisCompiler() {
        this.checker = new TypeChecker();
    }

    /** Returns the singleton instance of this class. */
    public static AtlantisCompiler instance() {
        return instance;
    }

    /** Type checks a given Atlantis string. */
    public CheckResult check(String text) throws ParseException {
        return check(parse(text));
    }

    /** Type checks a given Atlantis file. */
    public CheckResult check(File file) throws ParseException, IOException {
        return check(parse(file));
    }

    /** Type checks a given Atlantis parse tree. */
    public CheckResult check(ParseTree tree) throws ParseException {
        return this.checker.check(tree);
    }

    /** Compiles a given Atlantis String into a parse tree. */
    public ParseTree parse(String text) throws ParseException {
        return parse (new ANTLRInputStream(text));
    }

    /** Compiles a given Atlantis file into a parse tree. */
    public ParseTree parse(File file) throws ParseException, IOException {
        return parse(new ANTLRInputStream(new FileReader(file)));
    }

    /** Parses a given CharStream into a parse tree. */
    private ParseTree parse(CharStream chars) throws ParseException {
        ErrorListener listener = new ErrorListener();
        Lexer lexer = new AtlantisLexer(chars);
        lexer.removeErrorListeners();
        lexer.addErrorListener(listener);
        TokenStream tokens = new CommonTokenStream(lexer);
        AtlantisParser parser = new AtlantisParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(listener);
        ParseTree result = parser.program();
        listener.throwException();
        return result;
    }

}
