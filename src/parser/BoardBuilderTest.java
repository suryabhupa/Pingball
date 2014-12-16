package parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gadgets.Absorber;
import gadgets.Ball;
import gadgets.Board;
import gadgets.CircleBumper;
import gadgets.Flipper;
import gadgets.Portal;
import gadgets.SquareBumper;
import gadgets.TriangleBumper;

import java.io.File;
import java.util.Arrays;

import org.junit.Test;

import physics.Angle;


/**
 * These tests work on local machines, but throw X11 errors on Didit. 
 * 
 * @category no_didit
 */
public class BoardBuilderTest {
    /**
     * Testing strategy (merged from Pingball Phase 2 Project)
     * 
     * Method: createBoardFromFile
     * - Partitions:
     *      valid files
     *      - board declaration uses 0, 1, all optional arguments
     *      - extra whitespace
     *          at beginning of lines
     *          in between factors of declarations
     *          at end of lines
     *      - comments and empty lines before board declaration
     *      - different gadgets
     *      - negative velocities
     *      
     *      invalid files
     *      - other declarations before board declaration
     *          (ball, gadget (also absorber), trigger)
     *      - declaration does not meet specific grammar
     *      - repeated gadget names
     */
    
    // ==================== TESTS FOR VALID BOARD FILES
    // =========================
    @Test
    public void testBoardDeclarationUsesZeroOptionalArguments()
            throws Exception {
        Board boardActual = BoardBuilder.constructBoard(new File(
                "boards/boardUsesZeroOptionalArguments.pb"));
        Board boardPredicted = new Board();
        boardPredicted.setBoardName("testBoard");
    }

    @Test
    public void testBoardDeclarationUsesAllOptionalArguments() throws Exception {
        Board boardActual = BoardBuilder.constructBoard(new File(
                "boards/boardDeclarationUsesAllOptionalArguments.pb"));
        Board boardPredicted = new Board();
        boardPredicted.setBoardName("testBoard");
        assertEquals(boardPredicted, boardActual);
    }

    @Test
    public void testExtraWhiteSpaceAtBeginningOfLines() throws Exception {
        Board boardActual = BoardBuilder.constructBoard(new File(
                "boards/extraWhiteSpaceAtBeginningOfLines.pb"));
        Board boardPredicted = new Board();
        boardPredicted.setBoardName("testBoard");

        boardPredicted.addGadget(new CircleBumper(10, 10, Arrays.asList()));
        boardPredicted.addGadget(new CircleBumper(10, 11, Arrays.asList()));
        assertEquals(boardPredicted, boardActual);

    }

    @Test
    public void testExtraWhiteSpaceInBetweenFactorsOfDeclarations()
            throws Exception {
        Board boardActual = BoardBuilder.constructBoard(new File(
                "boards/extraWhiteSpaceInBetweenFactorsOfDeclarations.pb"));
        Board boardPredicted = new Board();
        boardPredicted.setBoardName("testBoard");
        boardPredicted.addGadget(new CircleBumper(10, 10, Arrays.asList()));
        boardPredicted.addGadget(new CircleBumper(10, 11, Arrays.asList()));
        assertTrue(boardPredicted.equals(boardActual));
    }

    @Test
    public void testExtraWhiteSpaceAtEndOfLines() throws Exception {
        Board boardActual = BoardBuilder.constructBoard(new File(
                "boards/extraWhiteSpaceAtEndOfLines.pb"));
        Board boardPredicted = new Board();
        boardPredicted.setBoardName("testBoard");

        boardPredicted.addGadget(new CircleBumper(10, 10, Arrays.asList()));
        boardPredicted.addGadget(new CircleBumper(10, 11, Arrays.asList()));
        assertEquals(boardPredicted, boardActual);
    }

    @Test
    public void testCommentsAndEmptyLinesBeforeBoardDeclaration()
            throws Exception {
        Board boardActual = BoardBuilder.constructBoard(new File(
                "boards/commentsAndEmptyLinesBeforeBoardDeclaration.pb"));
        Board boardPredicted = new Board();
        boardPredicted.setBoardName("testBoard");
        assertEquals(boardPredicted, boardActual);
    }

    @Test
    public void testConnectedPortal() throws Exception {
        Board boardActual = BoardBuilder.constructBoard(new File(
                "boards/connectedPortal.pb"));
        Board boardPredicted = new Board();
        boardPredicted.setBoardName("testBoard");

        boardPredicted.addGadget(new Portal(10, 10, boardPredicted, "portalA1",
                "otherB1", "otherPortalB1", Arrays.asList()));
        boardPredicted.addGadget(new Portal(10, 11, boardPredicted, "portalA2",
                "otherB2", "otherPortalB2", Arrays.asList()));
        assertTrue(boardPredicted.equals(boardActual));
    }

    @Test
    public void testPortalOnItself() throws Exception {
        Board boardActual = BoardBuilder.constructBoard(new File(
                "boards/unconnectedPortal.pb"));
        Board boardPredicted = new Board();
        boardPredicted.setBoardName("BoardA");
        // boardPredicted.addGadget(new Portal("portalA", 10, 10, "portalB",
        // "BoardB"));

        boardPredicted.addGadget(new Portal(10, 10, boardPredicted, "portalA",
                "BoardB", "portalB", Arrays.asList()));
        assertTrue(boardPredicted.equals(boardActual));
    }

    @Test
    public void testNegativeVelocities() throws Exception {
        Board boardActual = BoardBuilder.constructBoard(new File(
                "boards/negativeBallVelocities.pb"));
        Board boardPredicted = new Board();
        boardPredicted.setBoardName("BoardA");
        boardPredicted.addBall(new Ball(10, 10));
        assertTrue(boardPredicted.equals(boardActual));
    }

    // ==================== TESTS FOR INVALID BOARD FILES
    // =========================

    @Test(expected = Exception.class)
    public void testOtherDeclarationsBeforeBoard() throws Exception {
        BoardBuilder.constructBoard(new File(
                "boards/otherDeclarationsBeforeBoard.pb"));
    }

    @Test(expected = Exception.class)
    public void testDeclarationDoesNotMeetGrammar() throws Exception {
        BoardBuilder.constructBoard(new File(
                "boards/declarationDoesNotMeetGrammar.pb"));
    }

    @Test(expected = Exception.class)
    public void testRepeatedGadgetNames() throws Exception {
        BoardBuilder.constructBoard(new File("boards/repeatedGadgetNames.pb"));
    }

    // =========== TESTS ENTIRE STAFF-PROVIDED ORIGINAL ABSORBER BOARD
    // ==================

    @Test
    public void testEntireAbsorberBoard() throws Exception {
        Board boardActual = BoardBuilder.constructBoard(new File(
                "boards/oldAbsorber.pb"));

        Ball ballA = new Ball(10.25, 15.25);
        Ball ballB = new Ball(19.25, 3.25);
        Ball ballC = new Ball(1.25, 5.25);

        TriangleBumper Tri = new TriangleBumper(19, 0, Angle.DEG_180,
                Arrays.asList());

        CircleBumper CircleA = new CircleBumper(1, 10, Arrays.asList());
        CircleBumper CircleB = new CircleBumper(2, 10, Arrays.asList());
        CircleBumper CircleC = new CircleBumper(3, 10, Arrays.asList());
        CircleBumper CircleD = new CircleBumper(4, 10, Arrays.asList());
        CircleBumper CircleE = new CircleBumper(5, 10, Arrays.asList());

        // Absorber Abs = new Absorber("Abs", 0, 18, 20, 2);
        Absorber Abs = new Absorber(0, 18, 20, 2, false, Arrays.asList());

        CircleA.addToTriggered(Abs);
        CircleB.addToTriggered(Abs);
        CircleC.addToTriggered(Abs);
        CircleD.addToTriggered(Abs);
        CircleE.addToTriggered(Abs);

        // add all gadgets to Board
        // Board boardPredicted = new Board("oldAbsorber", 25.0);

        Board boardPredicted = new Board();
        boardPredicted.setBoardName("oldAbsorber");
        boardPredicted.setGravity(25.0);

        boardPredicted.addBall(ballA);
        boardPredicted.addBall(ballB);
        boardPredicted.addBall(ballC);
        boardPredicted.addGadget(Tri);
        boardPredicted.addGadget(CircleA);
        boardPredicted.addGadget(CircleB);
        boardPredicted.addGadget(CircleC);
        boardPredicted.addGadget(CircleD);
        boardPredicted.addGadget(CircleE);
        boardPredicted.addGadget(Abs);

        assertEquals(boardActual, boardPredicted);
    }

    // =========== TESTS SOME STAFF BOARDS ==================

    @Test
    public void testMultiplayerLeft() throws Exception {
        Board boardActual = BoardBuilder.constructBoard(new File(
                "src/parser/multiplayer_left.pb"));
        assertTrue(boardActual != null);
    }

    @Test
    public void testMultiplayerRight() throws Exception {
        Board boardActual = BoardBuilder.constructBoard(new File(
                "src/parser/multiplayer_right.pb"));
        assertTrue(boardActual != null);
    }

    @Test
    public void SimpleBoard() throws Exception {
        Board boardActual = BoardBuilder.constructBoard(new File(
                "src/parser/simple_board.pb"));
        assertTrue(boardActual != null);
    }

    // ==================== TESTS FOR triggerDeclarations
    // =========================
    @Test
    public void testAddingTriggersToAllGadgets() throws Exception {
        Board boardActual = BoardBuilder.constructBoard(new File(
                "boards/triggers.pb"));

        Board boardPredicted = new Board();
        boardPredicted.setBoardName("testTriggers");

        // new gadget constructors - add names as appropriate.
        TriangleBumper Tri = new TriangleBumper(10, 10, Angle.DEG_180,
                Arrays.asList());
        CircleBumper Circle = new CircleBumper(11, 10, Arrays.asList());
        SquareBumper Square = new SquareBumper(12, 10, Arrays.asList());
        Flipper FlipperL = new Flipper(10, 15, true, Angle.ZERO,
                boardPredicted, Arrays.asList());
        Flipper FlipperR = new Flipper(15, 15, false, Angle.ZERO,
                boardPredicted, Arrays.asList());
        Absorber Abs = new Absorber(0, 18, 20, 2, false, Arrays.asList());
        Portal aPortal = new Portal(3, 3, boardPredicted, "aPortal",
                "testTriggers", "Beta", Arrays.asList());
        CircleBumper last = new CircleBumper(5, 5, Arrays.asList());

        // add triggers
        Tri.addToTriggered(Circle);
        Circle.addToTriggered(Square);
        Square.addToTriggered(FlipperL);
        FlipperL.addToTriggered(FlipperR);
        FlipperR.addToTriggered(Abs);
        Abs.addToTriggered(aPortal);
        aPortal.addToTriggered(last);

        // add gadgets to boardPredicted
        boardPredicted.addGadget(Tri);
        boardPredicted.addGadget(Circle);
        boardPredicted.addGadget(Square);
        boardPredicted.addGadget(FlipperL);
        boardPredicted.addGadget(FlipperR);
        boardPredicted.addGadget(Abs);
        boardPredicted.addGadget(aPortal);
        boardPredicted.addGadget(last);

        assertEquals(boardPredicted, boardActual);

    }

}
