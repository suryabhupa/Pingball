package gadgets;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import physics.Circle;
import physics.Geometry;
import physics.Vect;

/**
 * PortalTests don't work on Didit and throw an X11 error, but work fine on
 * local machines.
 * 
 * @category no_didit
 */
public class PortalTest {
    /**
     * Testing Strategy
     * 
     * timeUntilCollision: 
     * -ball directed towards the center of the portal's circle
     * -ball not directed towards the center of the portal's center, and hits the portal
     * -ball tangential to the portal circle 
     * -ball does not hit the portal at all
     * 
     * affectBall: 
     * -ball directed towards the center of the portal's circle
     * -ball not directed towards the center of the portal's center, and hits the portal
     * -ball tangential to the portal circle
     */

    private static Board board = new Board();
    private final static Portal PORTAL = new Portal(9.5, 9.5, board, "1",
            "board", "2", new ArrayList<Gadget>()); // a portal where the center
    // of the circle is at origin
    private final static Portal PORTAL2 = new Portal(15.5, 1.5, board, "2",
            "board", "1", new ArrayList<Gadget>());
    private final static double EPSILON = 0.0001; // epsilon is allowed error

    @Test
    public void testTimeUntilCollisionDirectHit() {
        board.setBoardName("board");
        board.setServerPlay(true);
        board.addGadget(PORTAL);
        board.addGadget(PORTAL2);
        Ball ball = new Ball(5, 10);
        ball.setVec(new Vect(1, 0));
        assertEquals(4.25, PORTAL.timeUntilCollision(ball), EPSILON);
    }

    @Test
    public void testTimeUntilCollisionIndirectHit() {
        Ball ball = new Ball(5, 10.25);
        ball.setVec(new Vect(1, 0));
        assertEquals(
                Geometry.timeUntilCircleCollision(new Circle(10, 10, 0.5),
                        ball.getCirc(), ball.getVec()),
                PORTAL.timeUntilCollision(ball), EPSILON);
    }

    @Test
    public void testTimeUntilCollisionTangential() {
        board.setBoardName("board");
        board.setServerPlay(true);
        board.addGadget(PORTAL);
        board.addGadget(PORTAL2);
        Ball ball = new Ball(5, 10.75);
        ball.setVec(new Vect(1, 0));
        assertEquals(5.0, PORTAL.timeUntilCollision(ball), EPSILON);
    }

    @Test
    public void testTimeUntilCollisionNoHit() {
        Ball ball = new Ball(10, 15);
        ball.setVec(new Vect(2, 1));
        assertEquals(Double.POSITIVE_INFINITY, PORTAL.timeUntilCollision(ball),
                EPSILON);
    }
}
