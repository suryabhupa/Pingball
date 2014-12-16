package gadgets;

import java.awt.Graphics2D;

import physics.Angle;
import physics.Circle;
import physics.Geometry;
import physics.Geometry.VectPair;
import physics.Vect;

/**
 * A standalone Ball class that represents a single ball in the Pingball game.
 * Methods here provide methods to create balls, modify their location,
 * velocity, and representation.
 * 
 * Abstraction function: A physical ball that should obey physical laws with
 * radius 0.25. 
 * 
 * Representation: A Circle of radius 0.25. 
 * 
 * Rep Invariant: Location should never be outside the board, which is defined
 * as the boundary from ((0,0) --> (20, 20)). Ball's Circle radius should always
 * be 0.25.
 */
public class Ball {
    private Vect vec;
    private Circle circ;
    private OrderedPair pos;
    private boolean doesGravityHold;
    private boolean doesFrictionHold;
    private String name;

    private final int UPPER_WALL_BOUND = 20;
    private final int LOWER_WALL_BOUND = 0;
    private final double BALL_RADIUS = 0.25;
    private final double BALL_MASS = 1.0;

    /**
     * Checks the rep invariants and asserts false if any of the conditions
     * aren't met.
     */
    private void checkRep() {
        assert ((this.pos.getX() <= UPPER_WALL_BOUND)
                && (this.pos.getX() >= LOWER_WALL_BOUND)
                && (this.pos.getY() >= LOWER_WALL_BOUND) && (this.pos.getY() <= UPPER_WALL_BOUND));
        assert (circ.getRadius() == BALL_RADIUS);
    }

    /**
     * Constructor for the Ball object, initializing it as a Circle with radius
     * 0.25, with starting vector 0 in the upwards direction, and gravity is set
     * to affect the ball by default, but can be modified. The initial location
     * values are stored in an OrderedPair object, instead of just as two
     * standalone doubles.
     * 
     * @param initialX
     *            double Provides the initial x coordinate of the center of the
     *            Circle that represents it
     * @param initialY
     *            double Provides the initial y coordinate of the center of the
     *            Circle that represents it
     */
    public Ball(double initialX, double initialY) {
        this.pos = new OrderedPair(initialX, initialY);
        this.circ = new Circle(initialX, initialY, BALL_RADIUS);
        this.vec = new Vect(Angle.DEG_90, 0.0);
        doesGravityHold = true;
        doesFrictionHold = true;
        checkRep();
    }

    /** OBSERVER METHODS **/

    /**
     * Method to set the name of the ball
     * 
     * @param name
     *            String indicating name of ball
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Method to get the name of the ball
     * 
     * @return name String indicating name of ball
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the current Vect of the ball representing the velocity vector that
     * the ball
     * 
     * @return Vect representing the current velocity vector
     */
    public Vect getVec() {
        return new Vect(this.vec.angle(), this.vec.length());
    }

    /**
     * Gets the current Circle object that represents the ball
     * 
     * @return Circle representing the Circle associated to the ball centered at
     *         the ball's location
     */
    public Circle getCirc() {
        return new Circle(this.circ.getCenter(), this.circ.getRadius());
    }

    /**
     * Gets the current location the ball as an OrderedPair object
     * 
     * @return OrderedPair representing the location of the ball, or the center
     *         of the ball's circle representation. OrderedPair has a getX() and
     *         getY() functions to return the coordinates of the location.
     */
    public OrderedPair getLoc() {
        return new OrderedPair(this.pos.getX(), this.pos.getY());
    }

    /**
     * Gets the boolean representing if gravity is being considered in the
     * calculations of the ball's next velocity vector in the next timestep
     * 
     * @return boolean representing true/false if gravity is on/off in the
     *         calculations of the next velocity vector, or during an collisions
     *         or reflections
     */
    public boolean getGravityValue() {
        return this.doesGravityHold;
    }

    /**
     * Gets the boolean representing if friction is being considered in the
     * calculations of the ball's next velocity vector in the next timestep
     * 
     * @return boolean representing true/false if friction is on/off in the
     *         calculations of the next velocity vector, or during an collisions
     *         or reflections
     */
    public boolean getFrictionValue() {
        return this.doesFrictionHold;
    }

    /** MUTATOR METHODS **/

    /**
     * Assigns a new Vect object as the current vector of the ball, and checks
     * to ensure that the rep invariant is preserved
     * 
     * @param newVector
     *            the new Vect representing the new calculated vector that is to
     *            be assigned to ball
     */
    public void setVec(Vect newVector) {
        this.vec = newVector;
        checkRep();
    }

    /**
     * Assigns the new location of the ball as a pair of doubles; stores the
     * location internally as an OrderedPair and updates the Circle as a new
     * circle centered at the new location with a radius of 0.5
     * 
     * @param newX
     *            double the new x coordinate of the ball
     * @param newY
     *            double the new y coordinate of the ball
     */
    public void setLoc(double newX, double newY) {
        this.pos = new OrderedPair(newX, newY);
        this.circ = new Circle(newX, newY, BALL_RADIUS);
        checkRep();
    }

    /**
     * Assigns the boolean field of the ball to update if gravity is being
     * considered or not in the calculations of the next velocity vector in the
     * next timestep
     * 
     * @param value
     *            boolean to be assigned to doesGravityHold and will indicate if
     *            gravity is being considered
     */
    public void setGravity(boolean value) {
        this.doesGravityHold = value;
        checkRep();
    }

    /**
     * Assigns the boolean field of the ball to update if friction is being
     * considered or not in the calculations of the next velocity vector in the
     * next timestep
     * 
     * @param value
     *            boolean to be assigned to doesFrictionHold and will indicate
     *            if friction is being considered
     */
    public void setFriction(boolean value) {
        this.doesFrictionHold = value;
        checkRep();
    }

    /**
     * Method to calculate a ball-ball collision, updating both balls' locations
     * and velocity vectors, using each balls' mutator methods. Each mass of the
     * ball is assumed to be 1.0.
     * 
     * @param ball
     *            Ball the ball that this ball will collide with
     */
    public void collidesWithBall(Ball ball) {
        double timeUntilCollision = Geometry.timeUntilBallBallCollision(
                this.getCirc(), this.getVec(), ball.getCirc(), ball.getVec());

        double newX = this.pos.getX() + this.vec.x() * timeUntilCollision;
        double newY = this.pos.getY() + this.vec.y() * timeUntilCollision;
        this.setLoc(newX, newY);

        newX = ball.getLoc().getX() + ball.getVec().x() * timeUntilCollision;
        newY = ball.getLoc().getY() + ball.getVec().y() * timeUntilCollision;
        ball.setLoc(newX, newY);

        VectPair pairOfVecs = Geometry.reflectBalls(this.getCirc().getCenter(),
                BALL_MASS, this.getVec(), ball.getCirc().getCenter(),
                BALL_MASS, ball.getVec());

        this.setVec(pairOfVecs.v1);
        ball.setVec(pairOfVecs.v2);

    }

    /**
     * Method to return String representation of the ball
     * 
     * @return String representation for debugging purposes; format is
     *         "Ball(x, y)"
     */
    @Override
    public String toString() {
        return "Ball(" + getLoc().getX() + ", " + getLoc().getY() + ")";
    }

    /**
     * Method to draw ball object on frames. 
     * 
     * @param g2d
     *            Graphics2D object to assist in rendering differnet kinds of
     *            geometries
     * @param SCALE_FACTOR
     *            double that represents the scaling of the GUI for different
     *            screens
     */
    public void drawOnFrame(Graphics2D g2d, double SCALE_FACTOR) {
        g2d.fillOval(
                (int) ((pos.getX() - BALL_RADIUS + Gadget.DRAW_OFFSET) * SCALE_FACTOR),
                (int) ((pos.getY() - BALL_RADIUS + Gadget.DRAW_OFFSET) * SCALE_FACTOR),
                (int) (2 * BALL_RADIUS * SCALE_FACTOR),
                (int) (2 * BALL_RADIUS * SCALE_FACTOR));
    }
}
