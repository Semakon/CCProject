import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import pp.AtlantisLexer;
import pp.AtlantisParser;
import pp.AtlantisParser.*;
import pp.Type;
import org.junit.Test;
import pp.TypeChecker;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Author:  Martijn
 * Date:    15-6-2016
 */
public class ParseTest {

    private TypeChecker typeChecker = new TypeChecker();
    private ParseTreeWalker walker = new ParseTreeWalker();

    @Test
    public void test() {

    }

    private void fromFile() {

    }

    private ProgramContext parse(FileReader fl) throws IOException {
        CharStream chars = new ANTLRInputStream(fl);
        Lexer lexer = new AtlantisLexer(chars);
        TokenStream tokens = new CommonTokenStream(lexer);
        AtlantisParser parser = new AtlantisParser(tokens);
        return parser.program();
    }

}
