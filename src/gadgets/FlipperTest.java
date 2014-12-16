package gadgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import physics.Angle;
import physics.Vect;

/**
 * FlipperTests throw an X11 error on Didit, but work fine on local machines. 
 * 
 * @category no_didit
 */
public class FlipperTest {
    /**
     * Testing Strategy 
     * timeUntilCollision: 
     * -ball directed towards flipper's edges/circles
     * -ball misses the flippers
     * -ball tangential to the flipper's edges/circle
     * -testing both left/right flippers
     * 
     * affectBall: 
     * -ball directed towards flipper's edges/circles
     * -ball misses the flippers
     * -ball tangential to the flipper's circle
     * -testing both left/right flippers
     * -testing rotating part of flippers
     * 
     */

    @Test
    public void testTimeUntilCollisionNoHit() throws Exception {
        Board board = new Board();
        Flipper LEFT_FLIPPER = new Flipper(7.0, 7.0, true, Angle.DEG_270,
                board, new ArrayList<Gadget>());
        Flipper RIGHT_FLIPPER = new Flipper(5.0, 5.0, false, new Angle(0),
                board, new ArrayList<Gadget>());
        double EPSILON = 0.0001; // epsilon is allowed error

        Ball ball = new Ball(5, 0);
        ball.setVec(new Vect(1, 0));
        assertEquals(Double.MAX_VALUE, LEFT_FLIPPER.timeUntilCollision(ball),
                EPSILON);
        assertEquals(Double.MAX_VALUE, RIGHT_FLIPPER.timeUntilCollision(ball),
                EPSILON);
    }

    @Test
    public void testTimeUntilCollisionCornerAndEdgeDirectHit() throws Exception {
        Board board = new Board();
        Flipper LEFT_FLIPPER = new Flipper(7.0, 7.0, true, Angle.DEG_270,
                board, new ArrayList<Gadget>());
        Flipper RIGHT_FLIPPER = new Flipper(5.0, 5.0, false, new Angle(0),
                board, new ArrayList<Gadget>());
        double EPSILON = 0.0001; // epsilon is allowed error

        Ball ball = new Ball(7, 4);
        Ball ball1 = new Ball(7.5, 6);
        ball.setVec(new Vect(0, 5));
        ball1.setVec(new Vect(0, 5));
        assertEquals(0.55, LEFT_FLIPPER.timeUntilCollision(ball1), EPSILON);
        assertEquals(0.15, RIGHT_FLIPPER.timeUntilCollision(ball), EPSILON);
    }

    @Test
    public void testTimeUntilCollisionBetweenTwoFlippers() throws Exception {
        Board board = new Board();
        Flipper newLeftFlipper = new Flipper(7.0, 7.0, true, Angle.DEG_270,
                board, new ArrayList<Gadget>());
        Flipper newRightFlipper = new Flipper(9.0, 7.0, false, Angle.DEG_90,
                board, new ArrayList<Gadget>());
        double EPSILON = 0.0001; // epsilon is allowed error

        Ball ball = new Ball(9, 6);
        ball.setVec(new Vect(0, 5));
        assertEquals(0.55, newLeftFlipper.timeUntilCollision(ball), EPSILON);
        assertEquals(0.55, newRightFlipper.timeUntilCollision(ball), EPSILON);
    }

    @Test
    public void testAffectBallDirectandCornerHit() throws Exception {
        Board board = new Board();
        Flipper LEFT_FLIPPER = new Flipper(7.0, 7.0, true, Angle.DEG_270,
                board, new ArrayList<Gadget>());
        Flipper RIGHT_FLIPPER = new Flipper(5.0, 5.0, false, new Angle(0),
                board, new ArrayList<Gadget>());

        Ball ball = new Ball(7, 4);
        Ball ball1 = new Ball(7.5, 6);
        ball.setVec(new Vect(0, 2));
        ball1.setVec(new Vect(0, 5));

        LEFT_FLIPPER.affectBall(ball1);
        RIGHT_FLIPPER.affectBall(ball);

        assertEquals(new Vect(0.0, -4.75), ball1.getVec());
        assertEquals(new Vect(0.0, -1.9), ball.getVec());
    }

    @Test
    public void testAffectBallBetweenTwoFlippers() throws Exception {
        Board board = new Board();
        Flipper LEFT_FLIPPER = new Flipper(7.0, 7.0, true, Angle.DEG_270,
                board, new ArrayList<Gadget>());
        Flipper RIGHT_FLIPPER = new Flipper(9.0, 7.0, false, Angle.DEG_90,
                board, new ArrayList<Gadget>());
        double EPSILON = 0.0001; // epsilon is allowed error

        Ball ball = new Ball(9, 6);
        ball.setVec(new Vect(0, 5));
        Ball ball1 = new Ball(9, 6);
        ball1.setVec(new Vect(0, 5));

        LEFT_FLIPPER.affectBall(ball1);
        RIGHT_FLIPPER.affectBall(ball);

        assertEquals(4.75, ball.getVec().length(), EPSILON);
        assertEquals(new Vect(0, -4.75), ball.getVec());
        assertEquals(4.75, ball1.getVec().length(), EPSILON);
        assertEquals(new Vect(0, -4.75), ball1.getVec());
    }

    /**
     * This is a hybird human-unit test that is meant to demonstrate the
     * flipping nature of the flipper, and how a ball should react once it is
     * close to a flipper and once it flips and how it moves away.
     * 
     * @throws Exception
     */
    @Test
    public void testAffectBallRotatedUponTriggerLeftFlipperHumanmoveForwardFrame()
            throws Exception {
        Board board = new Board();
        Flipper LEFT_FLIPPER = new Flipper(7.0, 7.0, true, Angle.DEG_270,
                board, new ArrayList<Gadget>());
        CircleBumper circle = new CircleBumper(12, 12, new ArrayList<Gadget>(
                Arrays.asList(LEFT_FLIPPER)));

        Ball ball = new Ball(8, 6.5);
        Ball ball1 = new Ball(12, 1);
        ball1.setVec(new Vect(0, 0));
        ball.setVec(new Vect(0, 0));

        board.addBall(ball);
        board.addBall(ball1);
        board.addGadget(LEFT_FLIPPER);
        board.addGadget(circle);

        int i = 0;
        while (i < 25) {
            board.moveForwardFrame();
            i++;
        }

        assertTrue(ball.getLoc().getX() > 7);
        assertTrue(ball.getLoc().getY() > 7);
    }

    /**
     * This is a hybird human-unit test that is meant to demonstrate the
     * flipping nature of the flipper, and how a ball should react once it is
     * close to a flipper and once it flips and how it moves away.
     * 
     * @throws Exception
     */
    @Test
    public void testAffectBallRotatedUponTriggerRightFlipperHumanmoveForwardFrame()
            throws Exception {
        Board board = new Board();
        Flipper RIGHT_FLIPPER = new Flipper(7.0, 7.0, false, Angle.DEG_90,
                board, new ArrayList<Gadget>());
        CircleBumper circle = new CircleBumper(12, 12, new ArrayList<Gadget>(
                Arrays.asList(RIGHT_FLIPPER)));

        Ball ball = new Ball(8, 6.5);
        Ball ball1 = new Ball(12, 1);
        ball1.setVec(new Vect(0, 0));
        ball.setVec(new Vect(0, 0));

        board.addBall(ball);
        board.addBall(ball1);
        board.addGadget(RIGHT_FLIPPER);
        board.addGadget(circle);

        int i = 0;
        while (i < 25) {
            board.moveForwardFrame();
            i++;
        }

        assertTrue(ball.getLoc().getX() > 7);
        assertTrue(ball.getLoc().getY() > 7);
    }

    @Test
    public void testAffectBallRotatedUponTriggerRightFlipperHorizontalPushHumanmoveForwardFrame()
            throws Exception {
        Board board = new Board();
        Flipper LEFT_FLIPPER = new Flipper(7.0, 7.0, true, Angle.DEG_270,
                board, new ArrayList<Gadget>());
        CircleBumper circle = new CircleBumper(12, 15, new ArrayList<Gadget>(
                Arrays.asList(LEFT_FLIPPER)));

        Ball ball = new Ball(7, 6);
        Ball ball1 = new Ball(12, 2);
        ball1.setVec(new Vect(0, 0));
        ball.setVec(new Vect(0, 0));

        board.addBall(ball);
        board.addBall(ball1);
        board.addGadget(LEFT_FLIPPER);
        board.addGadget(circle);

        int i = 0;
        while (i < 25) {
            board.moveForwardFrame();
            i++;
        }

        assertEquals(ball.getLoc().getX(), 7, 0.01);
        assertTrue(ball.getLoc().getY() > 7);
    }

    public class LeftFlipperTests {

        /*
         * Ball bouncing off non-moving, non-triggered flipper. Ball should
         * bounce straight back.
         */
        @Test
        public void testBallCollidingOffNonMovingFlipper() throws Exception {
            Board board = new Board();
            board.setGravity(0);
            board.setMu1(0);
            board.setMu2(0); // No gravity or friction
            Ball ball = new Ball(5, 10);
            ball.setVec(new Vect(4.75, 0));
            board.addBall(ball);
            board.addGadget(new Flipper(10.0, 9.0, true, Angle.ZERO, board,
                    new ArrayList<Gadget>()));
            board.affectBoardState(2);
            // Ball should now be back AROUND at (5, 10). A bit before due to
            // coefficient of reflection
            OrderedPair center = board.getListOfBalls().get(0).getLoc();
            assertEquals(center.getX(), 5, 1);
            assertEquals(center.getY(), 10, 1);
        }

        /*
         * Ball bouncing off the top of a non-moving, non-triggered flipper.
         * Ball should bounce straight back.
         */
        @Test
        public void testBallCollidingOffTopOfNonMovingFlipper()
                throws Exception {
            Board board = new Board();
            board.setGravity(0);
            board.setMu1(0);
            board.setMu2(0); // No gravity or friction
            Ball ball = new Ball(10, 5);
            ball.setVec(new Vect(0, 4.5));
            board.addBall(ball);
            board.addGadget(new Flipper(10, 10, true, Angle.ZERO, board,
                    new ArrayList<Gadget>()));
            board.affectBoardState(2);
            // Ball should now be back at (10, 5)
            OrderedPair center = board.getListOfBalls().get(0).getLoc();
            assertEquals(center.getX(), 10, 1);
            assertEquals(center.getY(), 5, 1);
        }

        /*
         * Ball is right in front of the LeftFlipper. The flipper is then
         * triggered. Ball should bounce to the right and up
         */
        @Test
        public void testBallInFrontOfFlipperAndThenTriggerFlipper()
                throws Exception {
            Board board = new Board();
            board.setGravity(0);
            board.setMu1(0);
            board.setMu2(0);
            Ball ball = new Ball(10, 10);
            ball.setVec(new Vect(0, 0));
            board.addBall(ball);
            Flipper flipper = new Flipper(9, 9, true, Angle.ZERO, board,
                    new ArrayList<Gadget>());

            board.addGadget(flipper);
            flipper.doAction();
            board.affectBoardState(0.2);
            // Ball should get shot to the right and up
            assertTrue(ball.getVec().x() > 0);
            assertTrue(ball.getVec().y() < 0);
        }

        /*
         * Ball is right behind the LeftFlipper. The flipper is then triggered.
         * Ball should do nothing
         */
        @Test
        public void testBallBehindFlipperAndThenTriggerFlipper()
                throws Exception {
            Board board = new Board();
            board.setGravity(0);
            board.setMu1(0);
            board.setMu2(0);
            Ball ball = new Ball(8.75, 10);
            ball.setVec(new Vect(0, 0));
            board.addBall(ball);
            Flipper flipper = new Flipper(9, 9, true, Angle.ZERO, board,
                    new ArrayList<Gadget>());
            flipper.doAction();
            board.affectBoardState(0.3);
            // Ball should do nothing
            assertTrue(ball.getVec().x() == 0);
            assertTrue(ball.getVec().y() == 0);
        }

        /*
         * Ball is rolling towards self-triggering flipper. Flipper is
         * triggered. Ball should be shot straight back
         */
        @Test
        public void testBallHittingSelfTriggeringFlipper() throws Exception {
            Board board = new Board();
            board.setGravity(0);
            board.setMu1(0);
            board.setMu2(0);
            Ball ball = new Ball(19, 19.5);
            ball.setVec(new Vect(-4.5, 0));
            board.addBall(ball);
            Flipper flipper = new Flipper(10, 18, true, Angle.ZERO, board,
                    new ArrayList<Gadget>());
            board.addGadget(flipper);
            board.affectBoardState(2.1);
            // Ball should be shot straight back
            assertTrue(ball.getVec().x() > 0);
            assertTrue(ball.getVec().y() < 0);
        }
    }
}
