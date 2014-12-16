package gadgets;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import physics.Circle;
import physics.Geometry;
import physics.Vect;

/**
 * A CircleBumper class that extends Gadget, representing a Circle Bumper in the
 * Pingball game.
 * 
 * Abstraction function: A Circle Bumper that should obey physical laws (i.e.,
 * bouncing balls off it correctly)
 * 
 * Representation: A Circle of radius 0.5, with center at the center of the
 * boundary box with top left corner being the coordinates passed into the
 * constructor
 * 
 * Rep Invariant: Location should never be outside the board, which is defined
 * as the boundary from ((0,0) --> (20, 20)), that is, the initial coordinates
 * of the top left corner of the boundary box containing the Circle Bumper
 * should be [(0,0) --> (20,20)], inclusive. CircleBumper's Circle radius should
 * always be 0.5.
 */
public class CircleBumper implements Gadget {

    private final Circle bumper;
    private List<Gadget> gadgetsTriggered;
    private final double DIAMETER = 1.0;
    private final OrderedPair location;
    private String name;

    /**
     * Initializes a new Circle Bumper
     * 
     * @param x
     *            the x-coordinate of the smallest bounding box of the circle
     * @param y
     *            the y-coordinate of the smallest bounding box of the circle
     */
    public CircleBumper(double x, double y, List<Gadget> gadgetsToAction) {
        this.bumper = new Circle(x + DIAMETER / 2, y + DIAMETER / 2,
                DIAMETER / 2);
        this.gadgetsTriggered = new ArrayList<Gadget>();
        this.gadgetsTriggered.addAll(gadgetsToAction);
        location = new OrderedPair(x, y);
    }

    @Override
    public double timeUntilCollision(Ball ball) {
        double time = Geometry.timeUntilCircleCollision(this.bumper,
                ball.getCirc(), ball.getVec());
        return time;
    }

    @Override
    public void affectBall(Ball ball) {
        double minTime = timeUntilCollision(ball);

        double newX = ball.getLoc().getX() + ball.getVec().x() * minTime;
        double newY = ball.getLoc().getY() + ball.getVec().y() * minTime;
        ball.setLoc(newX, newY);

        Vect circleVec = this.bumper.getCenter();
        Vect ballCircleVec = ball.getCirc().getCenter();
        Vect ballVec = ball.getVec();

        Vect newVector = Geometry.reflectCircle(circleVec, ballCircleVec,
                ballVec);

        ball.setVec(newVector);

        for (Gadget gadget : gadgetsTriggered) {
            gadget.doAction();
        }

        checkRep();
    }

    @Override
    public void doAction() {
        return;
    }

    @Override
    public char getString() {
        return 'O';
    }

    @Override
    public OrderedPair getSize() {
        return new OrderedPair(1, 1);
    }

    @Override
    public OrderedPair getLoc() {
        return new OrderedPair(this.location.getX(), this.location.getY());
    }

    @Override
    public void changeState(double timeElapsed) {
        return;
    }

    /**
     * Checks the representation invariant
     */
    private void checkRep() {
        // No representation invariant to check
    }

    @Override
    public void setInvisibility(boolean isInvisible) {
        return;
    }

    @Override
    public void setClientName(String name) {
        return;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void addToTriggered(Gadget newGadget) {
        this.gadgetsTriggered.add(newGadget);

    }

    @Override
    public String getClientName() {
        return null;
    }

    @Override
    public void drawOnFrame(Graphics2D g2d, double SCALE_FACTOR) {
        g2d.fillOval((int) ((location.getX() + DRAW_OFFSET) * SCALE_FACTOR),
                (int) ((location.getY() + DRAW_OFFSET) * SCALE_FACTOR),
                (int) (DIAMETER * SCALE_FACTOR),
                (int) (DIAMETER * SCALE_FACTOR));
    }

}
