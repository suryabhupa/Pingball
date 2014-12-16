package gadgets;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import physics.Vect;

/* JUnit tests for Ball.java.

 *  
 *  collidesWithBall():
 *   - Input space: position and velocity of the balls when they collide. 
 *                  velocity = 0, non-zero
 *   - Output space: correct velocities of the ball after collision (details in each
 *                  individual test)
 *  
 */

public class BallTests {
    /*
     * Tests collidesWithBall() one on moving, one non-moving ball.
     */
    @Test
    public void testcollidesWithBallWithOnlyOneMoving() {
        Ball ball1 = new Ball(1.0, 1.0);
        ball1.setVec(new Vect(0, 0));
        Ball ball2 = new Ball(5.0, 1.0);
        ball2.setVec(new Vect(-5, 0));
        ball1.collidesWithBall(ball2);
        // ball1 should move at 45 degree angle south-west
        assertEquals(ball1.getVec().x(), -5.0, 0.1);
        // ball1 should move at 45 degree angle north-west
        assertEquals(ball2.getVec().x(), ball2.getVec().y(), 0.1);
        assertEquals(ball1.getVec().y(), ball2.getVec().y(), 0.1);
    }

    /*
     * Tests setBallsAfterCollision() on two moving colliding balls.
     */
    @Test
    public void testcollidesWithBallWithBothMoving() {
        Ball ball1 = new Ball(4.75, 0);
        ball1.setVec(new Vect(4.75, 7.5));
        Ball ball2 = new Ball(5.25, 0);
        ball2.setVec(new Vect(-4.75, 7.5));
        ball1.collidesWithBall(ball2);
        assertEquals(ball1.getVec().x(), -4.75, 0.1);
        assertEquals(ball2.getVec().x(), 4.75, 0.1);
        assertEquals(ball1.getVec().y(), 7.5, 0.1);
        assertEquals(ball2.getVec().y(), 7.5, 0.1);
    }

    @Test
    // test for ball-ball collisions
    public void testBallCollision() {
        Ball ball1 = new Ball(.5, .5);
        ball1.setVec(new Vect(1, 0));
        Ball ball2 = new Ball(1.5, .5);
        ball2.setVec(new Vect(-1, 0));
        ball1.collidesWithBall(ball2);
        assertEquals(ball1.getVec(), new Vect(-1, 0));
        assertEquals(ball2.getVec(), new Vect(1, 0));
    }

}
