package gadgets;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import physics.Circle;
import physics.Geometry;
import physics.Vect;

public class AbsorberTest {
    /**
     * Testing Strategy 
     *      timeUntilCollision: 
     *          ball collides at the gadget along the line of a side  
     *          ball collides at the bottom of the absorber
     *          ball collides at at the top of the absorber
     *          ball collides at the bottom right corner of the gadget
     *      affectBall:
     *          ball collides at the gadget along the line of a side 
     *          ball collides at the bottom of the absorber
     *          ball collides at the top of the absorber
     *          ball collides at the bottom right corner of the gadget
     *          ball collides at the top of a self-triggerable absorber
     */

    private final static Absorber ABSORBER = new Absorber(5, 5, 5, 2, false,
            null);
    private final static Absorber SELF_TRIGGERABLE_ABSORBER = new Absorber(5,
            5, 5, 2, true, null);
    private final static double EPSILON = 0.0001;

    @Test
    public void testTimeUntilCollisionParallelCollision() {
        Ball ball = new Ball(2, 5);
        ball.setVec(new Vect(1, 0));
        assertEquals(2.75, ABSORBER.timeUntilCollision(ball), EPSILON);
    }

    @Test
    public void testTimeUntilCollisionFromBottomCollision() {
        Ball ball = new Ball(9, 10);
        ball.setVec(new Vect(0, -1));
        assertEquals(2.75, ABSORBER.timeUntilCollision(ball), EPSILON);
    }

    @Test
    public void testTimeUntilCollisionFromTopCollision() {
        Ball ball = new Ball(9, 2);
        ball.setVec(new Vect(0, 1));
        assertEquals(2.75, ABSORBER.timeUntilCollision(ball), EPSILON);
    }

    @Test
    public void testTimeUntilCollisionBottomRightCornerCollision() {
        Ball ball = new Ball(17, 14);
        ball.setVec(new Vect(-1, -1));
        Circle bottomRightCorner = new Circle(10, 7, 0);
        assertEquals(
                Geometry.timeUntilCircleCollision(bottomRightCorner,
                        ball.getCirc(), ball.getVec()),
                ABSORBER.timeUntilCollision(ball), EPSILON);
    }

    @Test
    public void testAffectBallParallelCollision() {
        Ball ball = new Ball(5, 5);
        ball.setVec(new Vect(1, 0));
        ABSORBER.affectBall(ball);
        OrderedPair ballNewLocation = ball.getLoc();
        Vect ballNewVelocity = ball.getVec();
        assertEquals(9.75, ballNewLocation.getX(), EPSILON);
        assertEquals(6.75, ballNewLocation.getY(), EPSILON);
        assertEquals(0.0, ballNewVelocity.x(), EPSILON);
        assertEquals(0.0, ballNewVelocity.y(), EPSILON);
    }

    @Test
    public void testAffectBallFromBottomCollision() {
        Ball ball = new Ball(9, 10);
        ball.setVec(new Vect(0, -1));
        ABSORBER.affectBall(ball);
        OrderedPair ballNewLocation = ball.getLoc();
        Vect ballNewVelocity = ball.getVec();
        assertEquals(9.75, ballNewLocation.getX(), EPSILON);
        assertEquals(6.75, ballNewLocation.getY(), EPSILON);
        assertEquals(0.0, ballNewVelocity.x(), EPSILON);
        assertEquals(0.0, ballNewVelocity.y(), EPSILON);
    }

    @Test
    public void testAffectBallFromTopCollision() {
        Ball ball = new Ball(9, 2);
        ball.setVec(new Vect(0, 1));
        ABSORBER.affectBall(ball);
        OrderedPair ballNewLocation = ball.getLoc();
        Vect ballNewVelocity = ball.getVec();
        assertEquals(9.75, ballNewLocation.getX(), EPSILON);
        assertEquals(6.75, ballNewLocation.getY(), EPSILON);
        assertEquals(0.0, ballNewVelocity.x(), EPSILON);
        assertEquals(0.0, ballNewVelocity.y(), EPSILON);
    }

    @Test
    public void testAffectBallBottomRightCornerCollision() {
        Ball ball = new Ball(17, 14);
        ball.setVec(new Vect(-1, -1));
        ABSORBER.affectBall(ball);
        OrderedPair ballNewLocation = ball.getLoc();
        Vect ballNewVelocity = ball.getVec();
        assertEquals(9.75, ballNewLocation.getX(), EPSILON);
        assertEquals(6.75, ballNewLocation.getY(), EPSILON);
        assertEquals(0.0, ballNewVelocity.x(), EPSILON);
        assertEquals(0.0, ballNewVelocity.y(), EPSILON);
    }

    @Test
    public void testAffectBallTopCollisionOnSelfTriggeringAbsorber() {
        Ball ball = new Ball(9, 2);
        ball.setVec(new Vect(0, 1));
        SELF_TRIGGERABLE_ABSORBER.affectBall(ball);
        OrderedPair ballNewLocation = ball.getLoc();
        Vect ballNewVelocity = ball.getVec();
        assertEquals(9.75, ballNewLocation.getX(), EPSILON);
        assertEquals(6.75, ballNewLocation.getY(), EPSILON);
        assertEquals(0.0, ballNewVelocity.x(), EPSILON);
        assertEquals(-50.0, ballNewVelocity.y(), EPSILON);
    }
}
