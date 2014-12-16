package parser;

import gadgets.Absorber;
import gadgets.CircleBumper;
import gadgets.Flipper;
import gadgets.Gadget;
import gadgets.Portal;
import gadgets.SquareBumper;
import gadgets.TriangleBumper;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gadgets.Ball;
import gadgets.Board;
import physics.Angle;
import physics.Vect;

/**
 * BoardBuilder class to construct a complete board, including gadgets and
 * setting trigger actions for each respective gadgets, from a source file.
 * 
 * Abstraction Function: Class represents a builder that takes various
 * BoardElements and returns a Board that is specified by the BoardElements.
 * 
 * Representation: No constructor methods are present, as BoardBuilder only
 * serves as an intermediary class to handle conversions between BoardElements
 * and Board objects. Therefore, in that sense, no concrete data structure or
 * other representation is used to maintain the integrity of BoardBuilder, and
 * so rep invariants do not exist for this class. The rep invariants of the
 * Board class are used in checking and maintaining the integrity of the Board,
 * but those are outlined in the Board class.
 * 
 * Rep Invariant: To reiterate, the rep invariants of the Board class are used
 * in checking and maintaining the integrity of the Board, but those are
 * outlined in the Board class.
 */
public class BoardBuilder {

    // Provides the mapping between possible inputs as keyboard inputs to
    // KeyEvent.Integers.
    private final static Map<String, Integer> stringToKeyMapping;
    static {
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("a", KeyEvent.VK_A);
        map.put("b", KeyEvent.VK_B);
        map.put("c", KeyEvent.VK_C);
        map.put("d", KeyEvent.VK_D);
        map.put("e", KeyEvent.VK_E);
        map.put("f", KeyEvent.VK_F);
        map.put("g", KeyEvent.VK_G);
        map.put("h", KeyEvent.VK_H);
        map.put("i", KeyEvent.VK_I);
        map.put("j", KeyEvent.VK_J);
        map.put("k", KeyEvent.VK_K);
        map.put("l", KeyEvent.VK_L);
        map.put("m", KeyEvent.VK_M);
        map.put("n", KeyEvent.VK_N);
        map.put("o", KeyEvent.VK_O);
        map.put("p", KeyEvent.VK_P);
        map.put("q", KeyEvent.VK_Q);
        map.put("r", KeyEvent.VK_R);
        map.put("s", KeyEvent.VK_S);
        map.put("t", KeyEvent.VK_T);
        map.put("u", KeyEvent.VK_U);
        map.put("v", KeyEvent.VK_V);
        map.put("w", KeyEvent.VK_W);
        map.put("x", KeyEvent.VK_X);
        map.put("y", KeyEvent.VK_Y);
        map.put("z", KeyEvent.VK_Z);
        map.put("0", KeyEvent.VK_0);
        map.put("1", KeyEvent.VK_1);
        map.put("2", KeyEvent.VK_2);
        map.put("3", KeyEvent.VK_3);
        map.put("4", KeyEvent.VK_4);
        map.put("5", KeyEvent.VK_5);
        map.put("6", KeyEvent.VK_6);
        map.put("7", KeyEvent.VK_7);
        map.put("8", KeyEvent.VK_8);
        map.put("9", KeyEvent.VK_9);
        map.put("shift", KeyEvent.VK_SHIFT);
        map.put("ctrl", KeyEvent.VK_CONTROL);
        map.put("alt", KeyEvent.VK_ALT);
        map.put("meta", KeyEvent.VK_META);
        map.put("space", KeyEvent.VK_SPACE);
        map.put("left", KeyEvent.VK_LEFT);
        map.put("right", KeyEvent.VK_RIGHT);
        map.put("up", KeyEvent.VK_UP);
        map.put("down", KeyEvent.VK_DOWN);
        map.put("minus", KeyEvent.VK_MINUS);
        map.put("equals", KeyEvent.VK_EQUALS);
        map.put("backspace", KeyEvent.VK_BACK_SPACE);
        map.put("openbracket", KeyEvent.VK_OPEN_BRACKET);
        map.put("closebracket", KeyEvent.VK_CLOSE_BRACKET);
        map.put("backslash", KeyEvent.VK_BACK_SLASH);
        map.put("semicolon", KeyEvent.VK_SEMICOLON);
        map.put("quote", KeyEvent.VK_QUOTE);
        map.put("enter", KeyEvent.VK_ENTER);
        map.put("comma", KeyEvent.VK_COMMA);
        map.put("period", KeyEvent.VK_PERIOD);
        map.put("slash", KeyEvent.VK_SLASH);
        stringToKeyMapping = Collections.unmodifiableMap(map);
    }

    List<BoardElement> boardElements = new ArrayList<BoardElement>();

    /**
     * Primary method in BoardBuilder; constructs the Board object to be
     * returned using various helper methods that produce the primary Board,
     * gadgets, and trigger relationships from a source file.
     * 
     * A particular thing to note is that repeated lines or lines that contain
     * the effectively same information (our definition of effective varies a
     * little for each line type) throw exception, since this behavior is
     * otherwise unspecified.
     * 
     * @param file
     *            File object that contains the specifications for the board and
     *            each of the objects to be placed on the board in the
     *            prescribed format.
     * @return Board object containing each of the objects
     * @throws Exception
     */
    public static Board constructBoard(File file) throws Exception {
        // Step 1: parse the file line-by-line to construct the BoardElement
        // objects that correspond to each Gadget, action, ball, or board.
        List<BoardElement> listOfElements = new ArrayList<BoardElement>();
        BufferedReader bufferedreader = new BufferedReader(new FileReader(file));

        while (true) {
            String line = bufferedreader.readLine();
            if (line == null)
                break;
            BoardElement element = BoardElement.parseLine(line);
            if (element != null)
                listOfElements.add(element);
        }
        bufferedreader.close();

        // Step 2: Using adjusted BoardElement objects created, we proceed to
        // construct the Board
        Board board = null;

        for (BoardElement element : listOfElements) {
            String type = element.getElementType();
            if (type.equals("board")) { // Board case
                if (board != null)
                    throw new IllegalArgumentException(
                            "Unexpected board definition");
                board = constructBoard(element);

            } else if (type.equals("fire")) { // Fire case
                // Finding triggerObject within gadget objects
                String triggerName = element.getString("trigger", "", false);
                boolean found = false;
                Gadget triggerGadget = null;
                for (Gadget gadget : board.getListOfGadgets())
                    if (gadget.getName().equals(triggerName)) {
                        found = true;
                        triggerGadget = gadget;
                        break;
                    }
                if (!found || triggerGadget == null)
                    throw new IllegalArgumentException("Trigger not found");

                // Finding actionObject within gadget objects
                String actionName = element.getString("action", "", false);
                found = false;
                Gadget actionGadget = null;
                for (Gadget gadget : board.getListOfGadgets())
                    if (gadget.getName().equals(actionName)) {
                        found = true;
                        actionGadget = gadget;
                        break;
                    }
                if (!found || actionGadget == null)
                    throw new IllegalArgumentException("Action not found");

                triggerGadget.addToTriggered(actionGadget);

            } else if (type.equals("ball")) { // Ball case

                if (board == null)
                    throw new IllegalArgumentException(
                            "Board not defined first");
                Ball ballToAdd = constructBall(element, board);

                boolean found = false;
                for (Ball ball : board.getListOfBalls())
                    if (ball.getName().equals(ballToAdd.getName())) {
                        found = true;
                        break;
                    }
                if (found) {
                    throw new IllegalArgumentException("Duplicate ball found");
                }

                board.addBall(ballToAdd);

            } else if (type.equals("keyup") || type.equals("keydown")) { // Key
                                                                         // events
                if (board == null)
                    throw new IllegalArgumentException(
                            "Board not defined first");

                String keyString = element.getString("key", "", false);
                Integer key = stringToKeyMapping.get(keyString);
                String nameOfGadget = element.getString("action", "", false);
                String keyDir = type;

                if (board.getKeyDirMapping(keyDir).size() == 0) {
                    board.addKeyMapping(key, nameOfGadget, keyDir);
                } else {
                    board.addKeyMapping(key, nameOfGadget, keyDir);
                }

                System.out.println(keyDir + " " + board.getKeyDirMapping(keyDir));

            } else { // Bumpers + other Gadgets case

                if (board == null)
                    throw new IllegalArgumentException(
                            "Board not defined first");
                Gadget gadgetToAdd = constructGadget(element, board);

                if (board.getListOfGadgets().size() > 0) {
                    boolean found = false;
                    for (Gadget gadget : board.getListOfGadgets()) {
                        if (gadget.getName().equals(gadgetToAdd.getName())) {
                            found = true;
                            break;
                        }
                    }
                    if (found) {
                        throw new IllegalArgumentException(
                                "Duplicate element found");
                    }
                }
                board.addGadget(gadgetToAdd);
            }
        }

        return board;
    }

    /**
     * Helper function to main constructBoard method; creates a Ball with
     * specified arguments passed in a line through the source file.
     * 
     * @param element
     *            BoardElement that contains all the information about the type
     *            of object, which is in this case, a Ball.
     * @return Ball object that is constructed from the BoardElement
     *         specifications
     */
    public static Ball constructBall(BoardElement element, Board board) {
        String type = element.getElementType();
        String name = element.getString("name", "", false);

        Ball ball = null;

        if (type.equals("ball")) {
            double x = element.getFloat("x", 0, false);
            double y = element.getFloat("y", 0, false);
            double vx = element.getFloat("xVelocity", 0, false);
            double vy = element.getFloat("yVelocity", 0, false);

            ball = new Ball(x, y);
            ball.setName(name);
            ball.setVec(new Vect(vx, vy));
        }

        if (ball == null) {
            throw new IllegalArgumentException("Unsupported statement "
                    + element.getElementType());
        }

        return ball;
    }

    /**
     * Helper function to main constructBoard method; creates a Gadget with
     * specified arguments passed in a line through the source file.
     * 
     * @param element
     *            BoardElement that contains all the information about the type
     *            of object, which is in this case, a Gadget.
     * @param board
     *            Board argument that will contain the Gadget
     * @return Gadget object that is constructed from the BoardElement
     *         specifications
     * @throws Exception
     */
    public static Gadget constructGadget(BoardElement element, Board board)
            throws Exception {
        String type = element.getElementType();
        String name = element.getString("name", "a", true);

        Gadget gadget = null;
        if (type.equals("absorber")) {
            int x = element.getInt("x", 0, false);
            int y = element.getInt("y", 0, false);
            int width = element.getInt("width", 0, false);
            int height = element.getInt("height", 0, false);

            gadget = new Absorber(x, y, width, height, false,
                    new ArrayList<Gadget>());
            gadget.setName(name);

        } else if (type.equals("portal")) {
            int x = element.getInt("x", 0, false);
            int y = element.getInt("y", 0, false);
            String otherPortal = element.getString("otherPortal", "", false);
            String otherBoard = element.getString("otherBoard",
                    board.getName(), true);

            gadget = new Portal(x, y, board, name, otherBoard, otherPortal,
                    new ArrayList<Gadget>());
            gadget.setName(name);
            board.addPortalName(name);

        } else if (type.indexOf("Bumper") >= 0) {
            int x = element.getInt("x", 0, false);
            int y = element.getInt("y", 0, false);
            if (type.equals("squareBumper")) {
                gadget = new SquareBumper(x, y, new ArrayList<Gadget>());
                gadget.setName(name);
            } else if (type.equals("circleBumper")) {
                gadget = new CircleBumper(x, y, new ArrayList<Gadget>());
                gadget.setName(name);
            } else if (type.equals("triangleBumper")) {
                int orientation = element.getInt("orientation", 0, true);
                Angle angleOrientation = Angle.ZERO;
                if (orientation == 0) {
                    angleOrientation = Angle.ZERO;
                } else if (orientation == 90) {
                    angleOrientation = Angle.DEG_90;
                } else if (orientation == 180) {
                    angleOrientation = Angle.DEG_180;
                } else if (orientation == 270) {
                    angleOrientation = Angle.DEG_270;
                }
                gadget = new TriangleBumper(x, y, angleOrientation,
                        new ArrayList<Gadget>());
                gadget.setName(name);
            }

        } else if (type.indexOf("Flipper") >= 0) {
            int x = element.getInt("x", 0, false);
            int y = element.getInt("y", 0, false);
            int orientation = element.getInt("orientation", 0, true);
            Angle angleOrientation = Angle.ZERO;
            if (orientation == 0) {
                angleOrientation = Angle.ZERO;
            } else if (orientation == 90) {
                angleOrientation = Angle.DEG_90;
            } else if (orientation == 180) {
                angleOrientation = Angle.DEG_180;
            } else if (orientation == 270) {
                angleOrientation = Angle.DEG_270;
            }
            if (type.equals("leftFlipper")) {
                gadget = new Flipper(x, y, true, angleOrientation, board,
                        new ArrayList<Gadget>());
            } else if (type.equals("rightFlipper")) {
                gadget = new Flipper(x, y, false, angleOrientation, board,
                        new ArrayList<Gadget>());
            }
            gadget.setName(element.getString("name", "", false));
        }

        if (gadget == null) {
            throw new IllegalArgumentException("Unsupported arguments "
                    + element.getElementType());
        }

        return gadget;
    }

    /**
     * Helper function to main constructBoard method; creates a Board with
     * specified arguments passed in a line through the source file.
     * 
     * @param element
     *            BoardElement that contains all the information about the type
     *            of object, which is in this case, a Board.
     * @return Board object that is constructed from the BoardElement
     *         specifications
     */
    public static Board constructBoard(BoardElement element) {
        if (!element.getElementType().equals("board"))
            throw new IllegalArgumentException("The element is not a board");

        Board board = new Board();

        String name = element.getString("name", "default", true);
        double gravity = element.getFloat("gravity", 25.0, true);
        double friction1 = element.getFloat("friction1", 0.025, true);
        double friction2 = element.getFloat("friction2", 0.025, true);

        board.setGravity(gravity);
        board.setMu1(friction1);
        board.setMu2(friction2);
        board.setBoardName(name);

        return board;
    }

}
