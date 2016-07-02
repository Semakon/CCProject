package project_9.test;

import org.junit.Assert;
import org.junit.Test;
import project_9.checker.SymbolTable;
import project_9.checker.Type;

/**
 * Author:  Martijn
 * Date:    1-7-2016
 */
public class SymbolTableTest {

    @Test
    public void test() {
        SymbolTable st = new SymbolTable();
        st.insert("a", Type.INT);
        st.insert("b", Type.INT);

        st.openScope();
        st.insert("c", Type.INT);

        st.openScope();
        st.insert("d", Type.INT);
        st.insert("e", Type.BOOL);
        st.closeScope();

        Assert.assertEquals(true, st.contains("c"));
        Assert.assertEquals(false, st.contains("d"));
        Assert.assertEquals(false, st.contains("f"));

        st.openScope();
        st.insert("f", Type.BOOL);
        st.closeScope();
        st.closeScope();

        st.openScope();
        st.insert("g", Type.INT);

        st.openScope();
        st.insert("h", Type.BOOL);
        st.closeScope();
        st.closeScope();

        Assert.assertEquals(false, st.contains("c"));
        Assert.assertEquals(true, st.contains("a"));
        Assert.assertEquals(false, st.contains("g"));

        System.out.println(st.toString());
        System.out.println(st.getPointer());
    }

    @Test
    public void testScope() {
        SymbolTable st = new SymbolTable();

        Assert.assertEquals(1, st.getPointer().getCurrentOffset());

        st.insert("a", Type.INT);
        st.insert("b", Type.INT);
        st.openScope();             // open level 1-1

        Assert.assertEquals(3, st.getPointer().getCurrentOffset());
        Assert.assertEquals(false, st.insert("a", Type.BOOL));

        st.insert("c", Type.INT);
        st.openScope();             // open level 2-1
        st.insert("d", Type.INT);
        st.insert("e", Type.BOOL);

        Assert.assertEquals(6, st.getPointer().getCurrentOffset());
        Assert.assertEquals(Type.INT, st.lookupType("d"));

        st.closeScope();
        st.openScope();             // open level 2-2
        st.insert("d", Type.BOOL);

        Assert.assertEquals(5, st.getPointer().getCurrentOffset());
        Assert.assertEquals(Type.INT, st.lookupType("a"));
        Assert.assertEquals(Type.BOOL, st.lookupType("d"));

        st.closeScope();
        st.closeScope();
        st.openScope();             // open level 1-2
        st.insert("f", Type.INT);
        st.openScope();             // open level 2-3
        st.insert("g", Type.BOOL);
        st.closeScope();
        st.closeScope();

        System.out.println(st.toString());
        System.out.println(st.getPointer());
    }

}
