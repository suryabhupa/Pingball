package parser;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
/**
 * Testing strategy: (all are tested with the help of parseLine)
 * 
 * Blank Cases: 
 * - commented lines 
 * - blank lines 
 * - line with space proceeding/preceedings
 * 
 * Testing individual methods: 
 * - getElementType(), getProperties()
 * - getString(), getInt(), getFloat() defaults yes/no
 * - setProperty()
 */
public class BoardElementTest {
    @Test
    public void testCommentLines() {
        BoardElement element1 = BoardElement.parseLine("  # a comment  ");
        BoardElement element2 = BoardElement.parseLine("####");
        BoardElement element3 = BoardElement.parseLine("# TriangleBumper  ");

        assertEquals(null, element1);
        assertEquals(null, element2);
        assertEquals(null, element3);
    }

    @Test
    public void testBlankLines() {
        BoardElement element1 = BoardElement.parseLine(" ");
        BoardElement element3 = BoardElement.parseLine("        ");

        assertEquals(null, element1);
        assertEquals(null, element3);
    }

    @Test
    public void testLinesWithWhitespace() {
        BoardElement element2 = BoardElement.parseLine("");

        assertEquals(null, element2);
    }

    @Test
    public void testElementType() {
        BoardElement element1 = BoardElement
                .parseLine("    squareBumper name=boom x=1 y=2");
        BoardElement element2 = BoardElement
                .parseLine("hello234Hello name=boom x=1 y=2");
        BoardElement element3 = BoardElement
                .parseLine("___asdf name=boom x=1 y=2");
        BoardElement element4 = BoardElement
                .parseLine("_1234_ name=boom x=1 y=2");
        BoardElement element5 = BoardElement
                .parseLine("2A_63qpzlmI name=i x=1 y=2");

        assertEquals(element1.getElementType(), "squareBumper");
        assertEquals(element2.getElementType(), "hello234Hello");
        assertEquals(element3.getElementType(), "___asdf");
        assertEquals(element4.getElementType(), "_1234_");
        assertEquals(element5.getElementType(), "2A_63qpzlmI");
    }

    @Test
    public void testGetProperties() {
        BoardElement element1 = BoardElement
                .parseLine("    absorber name=want x=3 y=4 width=5 height=7   ");
        BoardElement element3 = BoardElement
                .parseLine("circleBumper name=in x=1 y=2 ");
        BoardElement element4 = BoardElement
                .parseLine(" triangleBumper name=my x=1 y=2 orientation=270 ");
        BoardElement element5 = BoardElement
                .parseLine("rightFlipper name=room x=1 y=2 orientation=90");
        BoardElement element2 = BoardElement
                .parseLine("  fire trigger=want action=in ");

        assertEquals(element1.getProperties().toString(),
                "{name=want, x=3, width=5, y=4, height=7}");
        assertEquals(element2.getProperties().toString(),
                "{action=in, trigger=want}");
        assertEquals(element3.getProperties().toString(), "{name=in, x=1, y=2}");
        assertEquals(element4.getProperties().toString(),
                "{orientation=270, name=my, x=1, y=2}");
        assertEquals(element5.getProperties().toString(),
                "{orientation=90, name=room, x=1, y=2}");
    }

    @Test
    public void testGetString() {
        BoardElement element1 = BoardElement
                .parseLine("    absorber name=want x=3 y=4 width=5 height=7   ");
        BoardElement element2 = BoardElement
                .parseLine("  fire trigger=want action=in ");

        assertEquals(element1.getString("length", "z", true), "z");
        assertEquals(element1.getString("name", "", false), "want");
        assertEquals(element2.getString("trigger", "", false), "want");
        assertEquals(element2.getString("x", "default", true), "default");
    }

    @Test
    public void testGetInt() {
        BoardElement element1 = BoardElement
                .parseLine("    absorber name=want x=3 y=4 width=5 height=7   ");
        BoardElement element2 = BoardElement
                .parseLine("circleBumper name=in x=1 y=2 ");
        BoardElement element3 = BoardElement
                .parseLine(" triangleBumper name=my x=1 y=2 orientation=270 ");
        BoardElement element4 = BoardElement
                .parseLine("rightFlipper name=room x=1 y=2 orientation=90");

        assertEquals(element1.getInt("length", -1, true), -1);
        assertEquals(element1.getInt("width", 0, false), 5);
        assertEquals(element2.getInt("x", 1, false), 1);
        assertEquals(element2.getInt("z", 0, true), 0);
        assertEquals(element3.getInt("orientation", 0, false), 270);
        assertEquals(element4.getInt("orientation", 0, true), 90);
    }

    @Test
    public void testGetFloat() {
        BoardElement element1 = BoardElement
                .parseLine("    board name=bb1 gravity=0.43 friction1=1.12 friction2=4.33   ");
        BoardElement element2 = BoardElement
                .parseLine("ball name=ball2 x=1.0 y=2 xVelocity=14.2 yVelocity=-12.2 ");
        BoardElement element3 = BoardElement
                .parseLine(" ball name=ball3 x=1 y=2.0 xVelocity=1.1 yVelocity=0.0 ");

        double EPSILON = 0.00001;

        assertEquals(element1.getFloat("gravity", 0.0, true), 0.43, EPSILON);
        assertEquals(element1.getFloat("friction1", 0.0, true), 1.12, EPSILON);
        assertEquals(element1.getFloat("friction2", 0.0, true), 4.33, EPSILON);
        assertEquals(element1.getFloat("friction3", 0.0, true), 0.0, EPSILON);

        assertEquals(element2.getFloat("xVelocity", 0.0, false), 14.2, EPSILON);
        assertEquals(element2.getFloat("yVelocity", 0.0, false), -12.2, EPSILON);
        assertEquals(element3.getFloat("x", 0.0, false), 1.0, EPSILON);
        assertEquals(element3.getFloat("y", 0.0, false), 2.0, EPSILON);
    }

    @Test
    public void testSetProperty() {
        BoardElement element1 = BoardElement
                .parseLine("    board name=bb1 gravity=0.43 friction1=1.12  ");
        assertEquals(element1.getProperties().toString(),
                "{gravity=0.43, name=bb1, friction1=1.12}");
        element1.setProperty("friction2", "4.33");
        assertEquals(element1.getProperties().toString(),
                "{friction2=4.33, gravity=0.43, name=bb1, friction1=1.12}");
        element1.setProperty("gravity", "-1.22");
        assertEquals(element1.getProperties().toString(),
                "{friction2=4.33, gravity=-1.22, name=bb1, friction1=1.12}");
    }
}
