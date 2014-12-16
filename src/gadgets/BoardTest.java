package gadgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import physics.Angle;
import physics.Vect;

/**
 * BoardTests don't work on Didit, but work fine on local machines.
 * 
 * @category no_didit
 */
public class BoardTest {
    /**
     * Testing Strategy: Test different triggers, collisions, and preconditions
     * of board class. 
     * 
     * Check board dimensions are correct:
     * - Ball-to-ball collision 
     * - Ball with 0-velocity 
     * - CircleBumper triggers Absorber listener
     * - SquareBumper triggers Absorber listener 
     * - TriangleBumper triggers Absorber listener 
     * - Self-triggering Absorber triggers itself
     */

    private final static double EPSILON = 0.0001;

    // Check that the board's dimensions are correct by checking the time taken
    // until balls collide with the board.
    @Test
    public void testBoardDimensions() {
        Vect[] initialVelocities = { new Vect(-1, 0), new Vect(0, -1),
                new Vect(1, 0), new Vect(0, 1) };
        Vect[] finalVelocities = { new Vect(1, 0), new Vect(0, 1),
                new Vect(-1, 0), new Vect(0, -1) };
        for (int i = 0; i < 4; i++) {
            Vect initialVelocity = initialVelocities[i];
            Vect finalVelocity = finalVelocities[i];
            Board board = new Board();
            Ball ball = new Ball(10, 10);
            board.addBall(ball);
            ball.setGravity(false);
            ball.setVec(initialVelocity);
            board.affectBoardState(19.5);
            OrderedPair ballNewLocation = ball.getLoc();
            Vect ballNewVelocity = ball.getVec();
            assertEquals(10.0, ballNewLocation.getX(), EPSILON);
            assertEquals(10.0, ballNewLocation.getY(), EPSILON);
            assertEquals(finalVelocity.x(), ballNewVelocity.x(), EPSILON);
            assertEquals(finalVelocity.y(), ballNewVelocity.y(), EPSILON);
        }
    }

    @Test
    public void testBallBallCollision() {
        Board board = new Board();
        Ball firstBall = new Ball(2, 4);
        firstBall.setVec(new Vect(0, 1));
        Ball secondBall = new Ball(2, 10);
        secondBall.setVec(new Vect(0, -1));

        board.addBall(firstBall);
        board.addBall(secondBall);

        board.affectBoardState(6);

        OrderedPair firstBallNewLocation = firstBall.getLoc();
        Vect firstBallNewVelocity = firstBall.getVec();
        OrderedPair secondBallNewLocation = secondBall.getLoc();
        Vect secondBallNewVelocity = secondBall.getVec();

        assertEquals(2, firstBallNewLocation.getX(), EPSILON);
        assertEquals(3.5, firstBallNewLocation.getY(), EPSILON);
        assertEquals(0, firstBallNewVelocity.x(), EPSILON);
        assertEquals(-1, firstBallNewVelocity.y(), EPSILON);

        assertEquals(2, secondBallNewLocation.getX(), EPSILON);
        assertEquals(10.5, secondBallNewLocation.getY(), EPSILON);
        assertEquals(0, secondBallNewVelocity.x(), EPSILON);
        assertEquals(1, secondBallNewVelocity.y(), EPSILON);
    }

    // Check that it is possible for a ball to maintain zero velocity
    @Test
    public void testBallWithZeroVelocity() throws InterruptedException {
        Board board = new Board();
        Ball ball = new Ball(5.5, 4.75);
        Gadget squareBumper = new SquareBumper(5, 5, new ArrayList<Gadget>());

        board.addBall(ball);
        board.addGadget(squareBumper);

        Vect ballNewVelocity = ball.getVec();
        assertEquals(0.0, ballNewVelocity.length(), EPSILON);
    }

    @Test
    public void testCircleBumperTriggersAbsorber() throws InterruptedException {
        Board board = new Board();
        Absorber absorber = new Absorber(0, 17, 20, 2, false, null);
        CircleBumper circleBumperWithAbsorberListener = new CircleBumper(2, 6,
                Arrays.asList(absorber));

        board.addGadget(absorber);
        board.addGadget(circleBumperWithAbsorberListener);

        Ball ballThatHitsAbsorber = new Ball(10, 16.5);
        ballThatHitsAbsorber.setVec(new Vect(0, 4));
        ballThatHitsAbsorber.setGravity(false);
        Ball ballThatHitsCircleBumper = new Ball(2.5, 4.75);
        ballThatHitsCircleBumper.setVec(new Vect(0, 1));
        ballThatHitsCircleBumper.setGravity(false);

        board.addBall(ballThatHitsAbsorber);
        board.addBall(ballThatHitsCircleBumper);

        for (int i = 0; i < 25; i++) {
            board.affectBoardState(0.05);
        }

        OrderedPair newBallLocation = ballThatHitsAbsorber.getLoc();
        assertEquals(19.75, newBallLocation.getX(), EPSILON);
        assertEquals(6.25, newBallLocation.getY(), EPSILON);
    }

    @Test
    public void testSquareBumperTriggersAbsorber() {
        Board board = new Board();
        Absorber absorber = new Absorber(0, 17, 20, 2, false, null);
        SquareBumper squareBumperWithAbsorberListener = new SquareBumper(2, 6,
                Arrays.asList(absorber));

        board.addGadget(absorber);
        board.addGadget(squareBumperWithAbsorberListener);

        Ball ballThatHitsAbsorber = new Ball(10, 16.5);
        ballThatHitsAbsorber.setVec(new Vect(0, 4));
        ballThatHitsAbsorber.setGravity(false);
        Ball ballThatHitsSquareBumper = new Ball(2.5, 4.75);
        ballThatHitsSquareBumper.setVec(new Vect(0, 1));
        ballThatHitsSquareBumper.setGravity(false);

        board.addBall(ballThatHitsAbsorber);
        board.addBall(ballThatHitsSquareBumper);

        for (int i = 0; i < 25; i++) {
            board.affectBoardState(0.05);
        }

        OrderedPair newBallLocation = ballThatHitsAbsorber.getLoc();
        assertEquals(19.75, newBallLocation.getX(), EPSILON);
        assertEquals(6.25, newBallLocation.getY(), EPSILON);
    }

    @Test
    public void testTriangleBumperTriggersAbsorber() {
        Board board = new Board();
        Absorber absorber = new Absorber(0, 17, 20, 2, false, null);
        TriangleBumper triangleBumperWithAbsorberListener = new TriangleBumper(
                2, 6, Angle.ZERO, Arrays.asList(absorber));

        board.addGadget(absorber);
        board.addGadget(triangleBumperWithAbsorberListener);

        Ball ballThatHitsAbsorber = new Ball(10, 16.5);
        ballThatHitsAbsorber.setVec(new Vect(0, 4));
        ballThatHitsAbsorber.setGravity(false);
        Ball ballThatHitsTriangleBumper = new Ball(2.5, 4.75);
        ballThatHitsTriangleBumper.setVec(new Vect(0, 1));
        ballThatHitsTriangleBumper.setGravity(false);

        board.addBall(ballThatHitsAbsorber);
        board.addBall(ballThatHitsTriangleBumper);

        for (int i = 0; i < 25; i++) {
            board.affectBoardState(0.05);
        }

        OrderedPair newBallLocation = ballThatHitsAbsorber.getLoc();
        assertEquals(19.75, newBallLocation.getX(), EPSILON);
        assertEquals(6.25, newBallLocation.getY(), EPSILON);
    }

    @Test
    public void testSelfTriggeringAbsorberTriggersItself()
            throws InterruptedException {
        Board board = new Board();

        Absorber absorber = new Absorber(0, 17, 20, 2, true, null);
        board.addGadget(absorber);

        Ball ball = new Ball(10, 9.75);
        ball.setVec(new Vect(0, 1));
        board.addBall(ball);

        double timeUntilBallHitsAbsorber = 7.0;
        double durationOfShootingUp = 0.2;
        double EJECTION_VELOCITY = -50;

        board.affectBoardState(timeUntilBallHitsAbsorber + durationOfShootingUp);
        OrderedPair ballNewLocation = ball.getLoc();

        assertEquals(19.75, ballNewLocation.getX(), EPSILON);
        assertEquals(18.75 + durationOfShootingUp * EJECTION_VELOCITY,
                ballNewLocation.getY(), EPSILON);
    }

    /*
     * Ball should move straight at constant velocity under no gravity or
     * friction.
     */
    @Test
    public void testUpdatePositionBallMovingStraightNoGravity() {
        Board board = new Board();
        board.setGravity(0);
        board.setMu1(0);
        board.setMu2(0);
        Ball ball = new Ball(1, 1);
        ball.setVec(new Vect(1, 1));
        board.addBall(ball);
        board.affectBoardState(10);
        // Ball should now be at (11, 11)
        OrderedPair center = board.getListOfBalls().get(0).getLoc();
        assertEquals(center.getX(), 11, 0.1);
        assertEquals(center.getY(), 11, 0.1);
    }

    /*
     * Tests ball falling under the influence of gravity. Ball should fall
     * straight down.
     */
    @Test
    public void testUpdatePositionBallUnderGravity() {
        Board board = new Board();
        Ball ball = new Ball(10, 0);
        ball.setVec(new Vect(0, 0));
        board.addBall(ball);
        board.moveForwardFrame();
        // Ball should fall down
        assertTrue(ball.getVec().x() == 0);
        assertTrue(ball.getVec().y() > 0);
    }

    /*
     * Tests ball slowing down due to friction. Ball should slow down to 0 after
     * an extended period of time.
     */
    @Test
    public void testUpdatePositionBallFrictionGravity() {
        Board board = new Board();
        board.setGravity(0);
        board.setMu1(0.025);
        board.setMu2(0.025); // Board with friction, no gravity
        Ball ball = new Ball(10, 0);
        ball.setVec(new Vect(10, 0));
        board.addBall(ball);
        board.moveForwardFrame();
        // Ball should slow down
        assertTrue(ball.getVec().x() < 10);
        assertEquals(ball.getVec().y(), 0, 0);
    }

    /*
     * Tests if making a wall invisible is successful, as well as making it
     * solid again.
     */
    @Test
    public void testUpdateWall() {
        Board board = new Board();
        board.makeWallInvisible("top", 0);
        board.makeWallInvisible("right", 1);
        board.makeWallInvisible("bottom", 2);
        board.makeWallInvisible("left", 3);
        board.displayBoard();
        board.makeWallSolid(0);
        board.makeWallSolid(1);
        board.makeWallSolid(2);
        board.makeWallSolid(3);
        board.displayBoard();
    }

}
