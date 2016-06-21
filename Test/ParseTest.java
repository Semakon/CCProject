import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import pp.AtlantisLexer;
import pp.AtlantisParser;
import pp.AtlantisParser.*;
import pp.Type;
import org.junit.Test;
import pp.TypeChecker;

import static org.junit.Assert.assertEquals;

/**
 * Created by martijn on 15-6-16.
 */
public class ParseTest {

    private TypeChecker typeChecker = new TypeChecker();
    private ParseTreeWalker walker = new ParseTreeWalker();

    @Test
    public void test() {
        test(Type.INT, "3");
    }

    private void test(Type type, String expr) {
        ParseTree tree = parse(expr);
        this.walker.walk(this.typeChecker, tree);
        assertEquals(type, this.typeChecker.type(tree));
    }

    private ExprContext parse(String text) {
        CharStream chars = new ANTLRInputStream(text);
        Lexer lexer = new AtlantisLexer(chars);
        TokenStream tokens = new CommonTokenStream(lexer);
        AtlantisParser parser = new AtlantisParser(tokens);
        return parser.expr();
    }

}
