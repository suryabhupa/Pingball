package gadgets;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import physics.Geometry;
import physics.LineSegment;
import physics.Vect;

public class WallTest {
    /**
     * Testing Strategy 
     *      timeUntilCollision: 
     *          ball hits at an angle
     *          ball does not hit wall 
     *      affectBall: 
     *          ball hits at an angle
     */

    private static final Wall WALL = new Wall(-1.0,-1.0, -1.0, 20.0, null);
    private static final double EPSILON = 0.0001;

    @Test
    public void testTimeUntilCollisionSlantedCollision() {
        Ball ball = new Ball(5, 15);
        ball.setVec(new Vect(-1, -1));
        assertEquals(Geometry.timeUntilWallCollision(new LineSegment(0, 0, 0,
                20), ball.getCirc(), ball.getVec()),
                WALL.timeUntilCollision(ball), EPSILON);
    }

    @Test
    public void testTimeUntilCollisionNoCollision() {
        Ball ball = new Ball(5, 4);
        ball.setVec(new Vect(0, -1));
        assertEquals(Double.MAX_VALUE, WALL.timeUntilCollision(ball), EPSILON);
    }

    @Test
    public void testAffectBallSlantedCollision() {
        Ball ball = new Ball(5, 15);
        ball.setVec(new Vect(-1, -1));
        WALL.affectBall(ball);
        OrderedPair ballNewLocation = ball.getLoc();
        Vect ballNewVelocity = ball.getVec();
        assertEquals(0.25, ballNewLocation.getX(), EPSILON);
        assertEquals(10.25, ballNewLocation.getY(), EPSILON);
        assertEquals(1.0, ballNewVelocity.x(), EPSILON);
        assertEquals(-1.0, ballNewVelocity.y(), EPSILON);
    }
    
    /**
    @Test
    public void testTransparentWall() {
        Ball ball = new Ball(5, 15);
        ball.setVec(new Vect(-1, -1));
        WALL.setInvisibility(true);
        WALL.affectBall(ball);
        OrderedPair ballNewLocation = ball.getLoc();
        Vect ballNewVelocity = ball.getVec();
        assertEquals(20.0, ballNewLocation.getX(), EPSILON);
        assertEquals(10.25, ballNewLocation.getY(), EPSILON);
        assertEquals(1.0, ballNewVelocity.x(), EPSILON);
        assertEquals(-1.0, ballNewVelocity.y(), EPSILON);
    }**/
}
