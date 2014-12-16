package gadgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import physics.Circle;
import physics.Geometry;
import physics.LineSegment;
import physics.Vect;


/**
 * SquareBumperTests work well on local machines but throw an X11 error on Didit.
 * 
 * @category no_didit
 */
public class SquareBumperTest {
    /**
     * Testing Strategy 
     *      timeUntilCollision: 
     *          ball collides with square bumper at a side 
     *          ball collides with bumper in a trajectory along the side of the square 
     *          ball collides with the corner at a slanted angle 
     *      affectBall: 
     *          ball collides with square bumper at a side 
     *          ball collides with bumper in a trajectory along the side of the square 
     *          ball collides with the corner at a slanted angle
     */

    private final static SquareBumper SQUARE_BUMPER = new SquareBumper(5, 5, new ArrayList<Gadget>()); 
    // Square bumper with (0,0), (0,1), (1,0), and (1,1) as vertices
    private final static double EPSILON = 0.0001;

    @Test
    public void testTimeUntilCollisionSideCollision() {
        Ball ball = new Ball(3.5, 8.5);
        ball.setVec(new Vect(1, -1));
        assertEquals(2.25, SQUARE_BUMPER.timeUntilCollision(ball), EPSILON);
    }

    @Test
    public void testTimeUntilCollisionParallelCollision() {
        Ball ball = new Ball(2, 6);
        ball.setVec(new Vect(1, 0));
        assertEquals(2.75, SQUARE_BUMPER.timeUntilCollision(ball), EPSILON);
    }

    @Test
    public void testTimeUntilCollisionCornerCollision() {
        Ball ball = new Ball(3, 8);
        ball.setVec(new Vect(1, -1));
        assertEquals(
                Geometry.timeUntilCircleCollision(new Circle(5, 6, 0),
                        ball.getCirc(), ball.getVec()),
                SQUARE_BUMPER.timeUntilCollision(ball), EPSILON);
    }

    @Test
    public void testAffectBallSideCollision() {
        Ball ball = new Ball(3.5, 8.5);
        ball.setVec(new Vect(1, -1));
        SQUARE_BUMPER.affectBall(ball);
        OrderedPair ballNewLocation = ball.getLoc();
        Vect ballNewVelocity = ball.getVec();
        assertEquals(5.75, ballNewLocation.getX(), EPSILON);
        assertEquals(6.25, ballNewLocation.getY(), EPSILON);
        assertEquals(1.0, ballNewVelocity.x(), EPSILON);
        assertEquals(1.0, ballNewVelocity.y(), EPSILON);
    }

    @Test
    public void testAffectBallParallelCollision() {
        Ball ball = new Ball(2, 6);
        ball.setVec(new Vect(1, 0));
        SQUARE_BUMPER.affectBall(ball);
        OrderedPair ballNewLocation = ball.getLoc();
        Vect ballNewVelocity = ball.getVec();
        assertEquals(4.75, ballNewLocation.getX(), EPSILON);
        assertEquals(6.0, ballNewLocation.getY(), EPSILON);
        assertEquals(-1.0, ballNewVelocity.x(), EPSILON);
        assertEquals(0, ballNewVelocity.y(), EPSILON);
    }

    @Test
    public void testAffectBallCornerCollision() {
        Ball ball = new Ball(3, 8);
        ball.setVec(new Vect(1, -1));
        SQUARE_BUMPER.affectBall(ball);
        OrderedPair ballNewLocation = ball.getLoc();
        Vect ballNewVelocity = ball.getVec();
        assertEquals(5 - 1 / (4 * Math.sqrt(2)), ballNewLocation.getX(),
                EPSILON);
        assertEquals(6 + 1 / (4 * Math.sqrt(2)), ballNewLocation.getY(),
                EPSILON);
        assertEquals(-1, ballNewVelocity.x(), EPSILON);
        assertEquals(1, ballNewVelocity.y(), EPSILON);
    }

    
    /*
     * Check that a ball bounces off the top of the squareBumper correctly
     * ball bounces back up, aka with negatively velocity than it had coming in
     */
    @Test
    public void squareBumperCollideOffTop(){
        Board board = new Board();
        board.setGravity(0);
        board.setMu1(0);
        board.setMu2(0);// No gravity or friction
        SquareBumper square = new SquareBumper(10,10, new ArrayList<Gadget>());
        Ball ball = new Ball(10.5, 5);
        ball.setVec(new Vect(0, 4.5));
        board.addBall(ball);
        board.addGadget(square);
        board.affectBoardState(2);
        assertEquals(new Vect(0,-4.5),ball.getVec());
        assertTrue(ball.getLoc().getY() < 10);
    }
    
    /*
     * Check that a ball bounces off the side of a SquareBumper correctly
     * ball coming from the left bounces right, aka with negatively velocity than it had coming in
     */
    @Test 
    public void squareBumperCollideLeftSide(){
        Board board = new Board();
        board.setGravity(0);
        board.setMu1(0);
        board.setMu2(0);// No gravity or friction
        SquareBumper square = new SquareBumper(8,2, new ArrayList<Gadget>());
        Ball ball = new Ball(2.5, 2.5);
        ball.setVec(new Vect(6, 0));
        board.addBall(ball);
        board.addGadget(square);
        board.affectBoardState(2);
        assertEquals(new Vect(-6,0),ball.getVec());
        assertTrue(ball.getLoc().getX() < 8);
    }
    
    @Test
    //test for square bumper
    public void testSquareBumper() {
        Ball ball1 = new Ball(.5, .5);
        ball1.setVec(new Vect(1,0));
        SquareBumper sqBump = new SquareBumper(1,0, new ArrayList<Gadget> ());
        sqBump.affectBall(ball1);
        LineSegment lineSeg1011 = new LineSegment(1,0,1,1);
        assertEquals(ball1.getVec(), Geometry.reflectWall(lineSeg1011, new Vect(1,0)));
    }
    

}


