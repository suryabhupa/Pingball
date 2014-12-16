package gadgets;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import physics.Angle;
import physics.Circle;
import physics.Geometry;
import physics.LineSegment;
import physics.Vect;

/**
 * An Absorber class that implements the Gadget interface that represents an
 * absorber in the Pingball game. Methods here provide methods to create the
 * absorber, and calculate collision events with balls.
 * 
 * Abstraction function: An absorber that has coordinates in the playing field;
 * absorbers balls, and can shoot them upwards from the absorber's bottom right
 * corner.
 * 
 * Representation: Four LineSegments as edges with four Circles as corners.
 * 
 * Rep Invariant: Bounding edges and corners are never modified or placed
 * outside of the playing field.
 */
public class Absorber implements Gadget {
    private final static double EJECTION_VELOCITY = -50.0;

    private OrderedPair origin;
    private int width;
    private int length;
    private boolean selfTriggering;
    private List<Ball> heldBalls;
    private Ball ejectingBall;
    private List<Gadget> gadgetsToAction;
    // Side segments
    private LineSegment[] sides;
    // Corners
    private Circle bottomRight;
    private Circle[] corners;
    private String name;

    /**
     * Constructor method to create an Absorber with a location at the upper
     * right with initialX and initialY stored as an OrderedPair, and with width
     * and length specified to specify how the LineSegments and Circle corners
     * are created. The Absorber can be selfTriggerable, which means that once a
     * ball touches the absorber, it will shoot any other balls "stored" in the
     * absorber, and it has a list of gadgets gadgetsToAction that any contact
     * with an absorber will trigger.
     * 
     * @param initialX
     *            double that specifies upper right x-coordinate of the absorber
     * @param initialY
     *            double that specifies upper right y-coordinate of the absorber
     * @param width
     *            int that specifies number of units of width of absorber
     * @param length
     *            int that specifies number of units of length of absorber
     * @param selfTriggering
     *            boolean to indicate if the absorber triggers itself
     * @param gadgetsToAction
     *            List<Gadget> that specifies what gadgets (if any) are
     *            triggered by absorber
     */
    public Absorber(double initialX, double initialY, int width, int length,
            boolean selfTriggering, List<Gadget> gadgetsToAction) {

        this.origin = new OrderedPair(initialX, initialY);
        this.width = width;
        this.length = length;
        this.selfTriggering = selfTriggering;
        this.heldBalls = new ArrayList<Ball>();
        this.ejectingBall = null;

        double leftX = origin.getX();
        double rightX = leftX + width;
        double topY = origin.getY();
        double bottomY = topY + length;

        // Initialize the sides as LineSegments
        LineSegment rightSide = new LineSegment(rightX, topY, rightX, bottomY);
        LineSegment topSide = new LineSegment(leftX, topY, rightX, topY);
        LineSegment leftSide = new LineSegment(leftX, topY, leftX, bottomY);
        LineSegment bottomSide = new LineSegment(leftX, bottomY, rightX,
                bottomY);
        this.sides = new LineSegment[] { rightSide, topSide, leftSide,
                bottomSide };

        // Initialize the corners as Circles
        Circle topRight = new Circle(rightX, topY, 0);
        Circle topLeft = new Circle(leftX, topY, 0);
        this.bottomRight = new Circle(rightX, bottomY, 0);
        Circle bottomLeft = new Circle(leftX, bottomY, 0);
        this.corners = new Circle[] { topRight, topLeft, this.bottomRight,
                bottomLeft };

        // Adds gadgets to the gadgetsToAction list
        this.gadgetsToAction = new ArrayList<Gadget>();
        if (gadgetsToAction != null) {
            this.gadgetsToAction.addAll(gadgetsToAction);
        }
    }

    /**
     * Updates the ball currently being ejected by the absorber. A ball is still
     * being ejected if some part of it lies within the absorber.
     */
    private void updateEjectingBall() {

        if (ejectingBall != null) {
            double bottomOfBall = ejectingBall.getLoc().getY()
                    + ejectingBall.getCirc().getRadius();
            if (bottomOfBall <= this.origin.getY()) {
                ejectingBall.setGravity(true);
                ejectingBall.setFriction(true);
                ejectingBall = null;
            }
        }
    }

    /**
     * Calculates time until absorber-ball collision.
     * 
     * @param ball
     *            a ball
     * @return double the time it takes for the input ball to collide with the
     *         absorber
     */
    @Override
    public double timeUntilCollision(Ball ball) {
        double minTime = Double.MAX_VALUE;

        updateEjectingBall();
        if (ball == ejectingBall || this.heldBalls.contains(ball)) {
            return minTime;
        }

        for (LineSegment side : sides) {
            double timeForBallToCollideWithSide = Geometry
                    .timeUntilWallCollision(
                            new LineSegment(side.p1(), side.p2()),
                            ball.getCirc(), ball.getVec());
            if (timeForBallToCollideWithSide < minTime) {
                minTime = timeForBallToCollideWithSide;
            }
        }
        for (Circle corner : corners) {
            double timeForBallToCollideWithCorner = Geometry
                    .timeUntilCircleCollision(new Circle(corner.getCenter(),
                            corner.getRadius()), ball.getCirc(), ball.getVec());
            if (timeForBallToCollideWithCorner < minTime) {
                minTime = timeForBallToCollideWithCorner;
            }
        }
        return minTime;
    }

    /**
     * This method will eject a single ball currently held by the absorber. The
     * ball is ejected at 50L / second to the top of the board. This method does
     * nothing if there are no ball currently held or if a ball is still being
     * ejected (i.e. an ejected ball is still in the absorber).
     */
    @Override
    public void doAction() {
        updateEjectingBall();
        if (ejectingBall == null) {
            if (!heldBalls.isEmpty()) {
                Vect velocity = new Vect(0, EJECTION_VELOCITY);
                Ball firstHeldBall = heldBalls.remove(0);
                firstHeldBall.setVec(velocity);
                ejectingBall = firstHeldBall;
            }
        }
    }

    /**
     * Changes the input ball's state by moving it to the corner of the
     * absorber. The ball's center will be 0.25 L from the bottom and right
     * sides of the absorber. The ball is then held at that location under the
     * absorber is triggered. If the absorber is self-triggering then the ball
     * is immediately shot out, unless another ball is currently being ejected.
     * 
     * @param ball
     *            Ball object that is going to be affected.
     */
    @Override
    public void affectBall(Ball ball) {
        Vect bottomRightCenter = bottomRight.getCenter();
        double bottomRightX = bottomRightCenter.x();
        double bottomRightY = bottomRightCenter.y();
        double offset = 0.25;
        ball.setLoc(bottomRightX - offset, bottomRightY - offset);
        Vect stationaryVelocity = new Vect(Angle.DEG_270, 0.0);
        ball.setVec(stationaryVelocity);
        ball.setGravity(false);
        ball.setFriction(false);
        heldBalls.add(ball);

        if (selfTriggering) {
            doAction();
        }

        for (Gadget gadget : gadgetsToAction) {
            gadget.doAction();
        }
    }

    /**
     * This method feeds in numbers from the Board class and these times are
     * used by updateEjectingBalls.
     * 
     * @param timeElapsed
     *            time that is elapsed in the time frame between updates
     */
    @Override
    public void changeState(double timeElapsed) {
        updateEjectingBall();
    }

    /** OBSERVER METHODS **/

    @Override
    public char getString() {
        return '=';
    }

    @Override
    public OrderedPair getSize() {
        return new OrderedPair(width, length);
    }

    @Override
    public OrderedPair getLoc() {
        return new OrderedPair(origin.getX(), origin.getY());
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
        this.gadgetsToAction.add(newGadget);
    }

    /**
     * Method to ask if absorber is self triggering; i.e. when a ball hits the
     * absorber, a ball held by the absorber is released.
     * 
     * @return boolean true or false depending on if the absorber is self
     *         triggering or not
     */
    public boolean isSelfTriggering() {
        if (this.selfTriggering || gadgetsToAction.contains(this)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getClientName() {
        return null;
    }

    @Override
    public void drawOnFrame(Graphics2D g2d, double SCALE_FACTOR) {
        g2d.setColor(Color.gray);
        g2d.fillRect((int) ((origin.getX() + DRAW_OFFSET) * SCALE_FACTOR),
                (int) ((origin.getY() + DRAW_OFFSET) * SCALE_FACTOR),
                (int) (width * SCALE_FACTOR), (int) (length * SCALE_FACTOR));
        g2d.setColor(Color.black);
    }

}
