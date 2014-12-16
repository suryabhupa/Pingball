package gadgets;

import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import physics.Circle;
import physics.Geometry;
import physics.Vect;

/**
 * CircleBumperTests work fine on local machines, but throw an X11 error on
 * Didit.
 * 
 * @category no_didit
 */
public class CircleBumperTest {
    /**
     * Testing Strategy
     * 
     * timeUntilCollision: 
     * -ball directed towards the center of the bumper's circle; 
     * -ball not directed towards the center of the bumper's center, and hits the bumper 
     * -ball tangential to the bumper circle
     * -ball does not hit the bumper at all
     * 
     * affectBall: 
     * -ball directed towards the center of the bumper's circle
     * -ball not directed towards the center of the bumper's center, and hits the bumper
     * -ball tangential to the bumper circle
     */

    private final static CircleBumper CIRCLE_BUMPER = new CircleBumper(9.5,
            9.5, new ArrayList<Gadget>()); // a circle bumper where the center
                                           // of the circle is at
    // origin
    private final static double EPSILON = 0.0001; // epsilon is allowed error

    @Test
    public void testTimeUntilCollisionDirectHit() {
        Ball ball = new Ball(5, 10);
        ball.setVec(new Vect(1, 0));
        assertEquals(4.25, CIRCLE_BUMPER.timeUntilCollision(ball), EPSILON);
    }

    @Test
    public void testTimeUntilCollisionIndirectHit() {
        Ball ball = new Ball(5, 10.25);
        ball.setVec(new Vect(1, 0));
        assertEquals(
                Geometry.timeUntilCircleCollision(new Circle(10, 10, 0.5),
                        ball.getCirc(), ball.getVec()),
                CIRCLE_BUMPER.timeUntilCollision(ball), EPSILON);
    }

    @Test
    public void testTimeUntilCollisionTangential() {
        Ball ball = new Ball(5, 10.75);
        ball.setVec(new Vect(1, 0));
        assertEquals(5.0, CIRCLE_BUMPER.timeUntilCollision(ball), EPSILON);
    }

    @Test
    public void testTimeUntilCollisionNoHit() {
        Ball ball = new Ball(10, 15);
        ball.setVec(new Vect(2, 1));
        assertEquals(Double.POSITIVE_INFINITY,
                CIRCLE_BUMPER.timeUntilCollision(ball), EPSILON);
    }

    @Test
    public void testAffectBallDirectHit() {
        Ball ball = new Ball(5, 10);
        ball.setVec(new Vect(1, 0));
        CIRCLE_BUMPER.affectBall(ball);
        OrderedPair ballNewLocation = ball.getLoc();
        Vect ballNewVelocity = ball.getVec();
        assertEquals(9.25, ballNewLocation.getX(), EPSILON);
        assertEquals(10.0, ballNewLocation.getY(), EPSILON);
        assertEquals(-1, ballNewVelocity.x(), EPSILON);
        assertEquals(0.0, ballNewVelocity.y(), EPSILON);
    }

    @Test
    public void testAffectBallIndirectHit() {
        Ball ball = new Ball(5, 10.25);
        ball.setVec(new Vect(1, 0));
        CIRCLE_BUMPER.affectBall(ball);
        OrderedPair ballNewLocation = ball.getLoc();
        Vect ballNewVelocity = ball.getVec();
        assertEquals(10 - Math.sqrt(2) / 2, ballNewLocation.getX(), EPSILON);
        assertEquals(10.25, ballNewLocation.getY(), EPSILON);
        assertEquals(
                1.0,
                Math.pow(ballNewVelocity.x(), 2)
                        + Math.pow(ballNewVelocity.y(), 2), EPSILON);
    }

    @Test
    public void testAffectBallTangential() {
        Ball ball = new Ball(5, 10.75);
        ball.setVec(new Vect(2, 0));
        CIRCLE_BUMPER.affectBall(ball);
        OrderedPair ballNewLocation = ball.getLoc();
        Vect ballNewVelocity = ball.getVec();
        assertEquals(10.0, ballNewLocation.getX(), EPSILON);
        assertEquals(10.75, ballNewLocation.getY(), EPSILON);
        assertEquals(
                4.0,
                Math.pow(ballNewVelocity.x(), 2)
                        + Math.pow(ballNewVelocity.y(), 2), EPSILON);
    }

    /*
     * Ball bouncing off straight off a CircleBumper. Ball should bounce
     * straight back.
     */
    @Test
    public void testBallCollidingStraightOffCircleBumper() {
        Board board = new Board();
        board.setGravity(0);
        board.setMu1(0);
        board.setMu2(0);// No gravity or friction
        Ball ball = new Ball(5, 10.5);
        ball.setVec(new Vect(4.75, 0));
        board.addBall(ball);
        board.addGadget(new CircleBumper(10, 10, new ArrayList<Gadget>()));
        board.affectBoardState(2);
        // Ball should now be back at (5, 10.5)
        Ball testBall = board.getListOfBalls().get(0);
        OrderedPair center = testBall.getLoc();
        assertEquals(center.getX(), 5, 1);
        assertEquals(center.getY(), 10.5, 1);
        assertEquals(ball.getVec().x(), -4.75, 0.1);
        assertEquals(ball.getVec().y(), 0, 0.1);
    }

    /*
     * Ball bouncing off a CircleBumper at an angle. Ball should bounce left and
     * upwards
     */
    @Test
    public void testBallCollidingAtAnAngleOffCircleBumper() {
        Board board = new Board();
        board.setGravity(0);
        board.setMu1(0);
        board.setMu2(0);// No gravity or friction
        Ball ball = new Ball(5, 10.5);
        ball.setVec(new Vect(4.75, 0));
        board.addBall(ball);
        board.addGadget(new CircleBumper(10, 10, new ArrayList<Gadget>()));
        board.affectBoardState(2);
        // Ball should bounce left
        assertTrue(ball.getVec().x() < 0);
        assertTrue(ball.getVec().y() == 0);
    }

    @Test
    // test for circle bumper
    public void testCircleBumper() {
        Ball ball2 = new Ball(.5, .5);
        ball2.setVec(new Vect(1, 0));
        Ball ball2a = new Ball(.5, .5);
        ball2a.setVec(new Vect(1, 0));
        CircleBumper circBump = new CircleBumper(1, 0, new ArrayList<Gadget>());
        circBump.affectBall(ball2);
        assertEquals(ball2.getVec(), Geometry.reflectCircle(new Vect(.5, .5),
                ball2a.getCirc().getCenter(), ball2a.getVec(), 1));
    }
}
