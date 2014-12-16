package gadgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import physics.Angle;
import physics.Circle;
import physics.Geometry;
import physics.LineSegment;
import physics.Vect;

/**
 * TriangleBumperTests work fine on local machines, but throw an X11 error on Didit. 
 * 
 * @category no_didit
 */
public class TriangleBumperTest {
    /**
     * Testing Strategy 
     *      timeUntilCollision: 
     *          ball collides with a leg
     *          ball collides with the hypotenuse 0-orientation
     *          ball collides with the hypotenuse 90-orientation
     *          ball collides with the hypotenuse 180-orientation
     *          ball collides with the hypotenuse 270-orientation
     *          ball collides with a corner 
     *          ball collides with bumper along the line of a side 
     *      affectBall 
     *          ball collides with a leg 
     *          ball collides with the hypotenuse 0-orientation
     *          ball collides with the hypotenuse 90-orientation
     *          ball collides with the hypotenuse 180-orientation
     *          ball collides with the hypotenuse 270-orientation
     *          ball collides with a corner 
     *          ball collides with bumper along the line of a side
     */

    private static final TriangleBumper TRIANGLE_BUMPER_0 = new TriangleBumper(
            5, 5, Angle.ZERO, null);
    private static final TriangleBumper TRIANGLE_BUMPER_90 = new TriangleBumper(
            5, 5, Angle.DEG_90, null);
    private static final TriangleBumper TRIANGLE_BUMPER_180 = new TriangleBumper(
            5, 5, Angle.DEG_180, null);
    private static final TriangleBumper TRIANGLE_BUMPER_270 = new TriangleBumper(
            5, 5, Angle.DEG_270, null);
    private static final double EPSILON = 0.0001;

    @Test
    public void testTimeUntilCollisonLegCollision() {
        Ball ball = new Ball(3, 5.5);
        ball.setVec(new Vect(1, 0));
        assertEquals(1.75, TRIANGLE_BUMPER_0.timeUntilCollision(ball), EPSILON);
    }

    @Test
    public void testTimeUntilCollisionHypotenuseCollision0() {
        Ball ball = new Ball(5.4, 8);
        ball.setVec(new Vect(0, -1));
        LineSegment hypotenuse = new LineSegment(5, 6, 6, 5);
        assertEquals(Geometry.timeUntilWallCollision(hypotenuse,
                ball.getCirc(), ball.getVec()),
                TRIANGLE_BUMPER_0.timeUntilCollision(ball), EPSILON);
    }

    @Test
    public void testTimeUntilCollisionHypotenuseCollision90() {
        Ball ball = new Ball(5.4, 8);
        ball.setVec(new Vect(0, -1));
        LineSegment hypotenuse = new LineSegment(5, 5, 6, 6);
        assertEquals(Geometry.timeUntilWallCollision(hypotenuse,
                ball.getCirc(), ball.getVec()),
                TRIANGLE_BUMPER_90.timeUntilCollision(ball), EPSILON);
    }

    @Test
    public void testTimeUntilCollisionHypotenuseCollision180() {
        Ball ball = new Ball(5.4, 2);
        ball.setVec(new Vect(0, 1));
        LineSegment hypotenuse = new LineSegment(5, 6, 6, 5);
        assertEquals(Geometry.timeUntilWallCollision(hypotenuse,
                ball.getCirc(), ball.getVec()),
                TRIANGLE_BUMPER_180.timeUntilCollision(ball), EPSILON);
    }

    @Test
    public void testTimeUntilCollisonHypotenuseCollision270() {
        Ball ball = new Ball(5.4, 2);
        ball.setVec(new Vect(0, 1));
        LineSegment hypotenuse = new LineSegment(5, 5, 6, 6);
        assertEquals(Geometry.timeUntilWallCollision(hypotenuse,
                ball.getCirc(), ball.getVec()),
                TRIANGLE_BUMPER_270.timeUntilCollision(ball), EPSILON);
    }

    @Test
    public void testTimeUntilCollisionCornerCollision() {
        Ball ball = new Ball(2, 9);
        ball.setVec(new Vect(1, -1));
        assertEquals(
                Geometry.timeUntilCircleCollision(new Circle(5, 6, 0),
                        ball.getCirc(), ball.getVec()),
                TRIANGLE_BUMPER_0.timeUntilCollision(ball), EPSILON);
    }

    @Test
    public void testTimeUntilCollisionParallelCollision() {
        Ball ball = new Ball(5, 10);
        ball.setVec(new Vect(0, -1));
        assertEquals(3.75, TRIANGLE_BUMPER_0.timeUntilCollision(ball), EPSILON);
    }

    @Test
    public void testAffectBallLegCollision() {
        Ball ball = new Ball(3, 5.5);
        ball.setVec(new Vect(1, 0));
        TRIANGLE_BUMPER_0.affectBall(ball);
        OrderedPair ballNewLocation = ball.getLoc();
        Vect ballNewVelocity = ball.getVec();
        assertEquals(4.75, ballNewLocation.getX(), EPSILON);
        assertEquals(5.5, ballNewLocation.getY(), EPSILON);
        assertEquals(-1, ballNewVelocity.x(), EPSILON);
        assertEquals(0.0, ballNewVelocity.y(), EPSILON);
    }

    @Test
    public void testAffectBallHypotenuseCollision0() {
        Ball ball = new Ball(5.4, 8);
        ball.setVec(new Vect(0, -1));
        LineSegment hypotenuse = new LineSegment(5, 6, 6, 5);
        Vect expectedVelocityAfterCollision = Geometry.reflectWall(hypotenuse,
                ball.getVec());
        TRIANGLE_BUMPER_0.affectBall(ball);
        Vect actualVelocityAfterCollision = ball.getVec();
        assertEquals(expectedVelocityAfterCollision,
                actualVelocityAfterCollision);
        assertEquals(expectedVelocityAfterCollision,
                actualVelocityAfterCollision);
    }

    @Test
    public void testAffectBallHypotenuseCollision90() {
        Ball ball = new Ball(5.4, 8);
        ball.setVec(new Vect(0, -1));
        LineSegment hypotenuse = new LineSegment(5, 5, 6, 6);
        Vect expectedVelocityAfterCollision = Geometry.reflectWall(hypotenuse,
                ball.getVec());
        TRIANGLE_BUMPER_90.affectBall(ball);
        Vect actualVelocityAfterCollision = ball.getVec();
        assertEquals(expectedVelocityAfterCollision.x(),
                actualVelocityAfterCollision.x(), EPSILON);
        assertEquals(expectedVelocityAfterCollision.y(),
                actualVelocityAfterCollision.y(), EPSILON);
    }

    @Test
    public void testAffectBallHypotenuseCollision180() {
        Ball ball = new Ball(5.4, 2);
        ball.setVec(new Vect(0, 1));
        LineSegment hypotenuse = new LineSegment(5, 6, 6, 5);
        Vect expectedVelocityAfterCollision = Geometry.reflectWall(hypotenuse,
                ball.getVec());
        TRIANGLE_BUMPER_180.affectBall(ball);
        Vect actualVelocityAfterCollision = ball.getVec();
        assertEquals(expectedVelocityAfterCollision.x(),
                actualVelocityAfterCollision.x(), EPSILON);
        assertEquals(expectedVelocityAfterCollision.y(),
                actualVelocityAfterCollision.y(), EPSILON);
    }

    @Test
    public void testAffectBallHypotenuseCollision270() {
        Ball ball = new Ball(5.4, 2);
        ball.setVec(new Vect(0, 1));
        LineSegment hypotenuse = new LineSegment(5, 5, 6, 6);
        Vect expectedVelocityAfterCollision = Geometry.reflectWall(hypotenuse,
                ball.getVec());
        TRIANGLE_BUMPER_270.affectBall(ball);
        Vect actualVelocityAfterCollision = ball.getVec();
        assertEquals(expectedVelocityAfterCollision.x(),
                actualVelocityAfterCollision.x(), EPSILON);
        assertEquals(expectedVelocityAfterCollision.y(),
                actualVelocityAfterCollision.y(), EPSILON);
    }

    @Test
    public void testAffectBallCornerCollision() {
        Ball ball = new Ball(2, 9);
        ball.setVec(new Vect(1, -1));
        TRIANGLE_BUMPER_0.affectBall(ball);
        Vect expectedVelocityAfterCollision = new Vect(-1, 1);
        Vect actualVelocityAfterCollision = ball.getVec();
        assertEquals(expectedVelocityAfterCollision.x(),
                actualVelocityAfterCollision.x(), EPSILON);
        assertEquals(expectedVelocityAfterCollision.y(),
                actualVelocityAfterCollision.y(), EPSILON);
    }

    @Test
    public void testAffectBallParallelCollision() {
        Ball ball = new Ball(5, 10);
        ball.setVec(new Vect(0, -1));
        TRIANGLE_BUMPER_0.affectBall(ball);
        Vect expectedVelocityAfterCollision = new Vect(0, 1);
        Vect actualVelocityAfterCollision = ball.getVec();
        assertEquals(expectedVelocityAfterCollision.x(),
                actualVelocityAfterCollision.x(), EPSILON);
        assertEquals(expectedVelocityAfterCollision.y(),
                actualVelocityAfterCollision.y(), EPSILON);
    }

    
    
    /*
     * Check that a ball bounces off the top of the triangleBumper correctly
     * ball bounces back up, aka with negatively velocity than it had coming in
     */
    @Test
    public void traingleBumperStraightSideTest(){
        Board board = new Board();
        board.setGravity(0);
        board.setMu1(0);
        board.setMu2(0);// No gravity or friction
        TriangleBumper triangle = new TriangleBumper(10,10,Angle.ZERO, null);
        Ball ball1 = new Ball(10.5, 5);
        ball1.setVec(new Vect(0, 4.5));
        board.addBall(ball1);
        board.addGadget(triangle);
        board.affectBoardState(2);
        assertEquals(new Vect(0,-4.5),ball1.getVec());
        assertTrue(ball1.getLoc().getY() < 10);

    }
    @Test
    //test for triangle bumper, 0
    public void testTriangleBumper0() {
        Ball ball1 = new Ball(.5, .5);
        ball1.setVec(new Vect(1,0));
        TriangleBumper triangBump = new TriangleBumper(1,0,Angle.ZERO, null);
        triangBump.affectBall(ball1);
        LineSegment lineSeg1011 = new LineSegment(1,0,1,1);
        assertEquals(ball1.getVec(), Geometry.reflectWall(lineSeg1011, new Vect(1,0)));   
    }
    
    @Test
    //test for triangle bumper, 90
    public void testTriangleBumper90() {
        Ball ball1 = new Ball(1,1/Math.sqrt(2));
        ball1.setVec(new Vect(1,0));
        TriangleBumper triangBump = new TriangleBumper(1,0,Angle.DEG_90, null);
        triangBump.affectBall(ball1);
        assertEquals(ball1.getVec(), Geometry.reflectWall(new LineSegment(1,0,2,1), new Vect(1,0)));   
    }
    
    @Test
    //test for triangle bumper, 180
    public void testTriangleBumper180() {
        Ball ball1 = new Ball(1,1-1/Math.sqrt(2));
        ball1.setVec(new Vect(1,0));
        TriangleBumper triangBump = new TriangleBumper(1,0,Angle.DEG_180, null);
        triangBump.affectBall(ball1);
        assertEquals(ball1.getVec(), Geometry.reflectWall(new LineSegment(2,0,1,1), new Vect(1,0)));   
    }
    
    @Test
    //test for triangle bumper, 270
    public void testTriangleBumper270() {
        Ball ball1 = new Ball(.5,.5);
        ball1.setVec(new Vect(1,0));
        TriangleBumper triangBump = new TriangleBumper(1,0,Angle.DEG_270, null);
        triangBump.affectBall(ball1);
        assertEquals(ball1.getVec(), Geometry.reflectWall(new LineSegment(1,1,1,2), new Vect(1,0)));   
      }
    

}

