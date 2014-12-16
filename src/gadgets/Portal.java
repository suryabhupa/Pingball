package gadgets;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import physics.Circle;
import physics.Geometry;
import physics.Vect;

/**
 * A Portal class that extends Gadget, representing a portal in the Pingball
 * game.
 * 
 * Abstraction function: A Portal that should obey game laws (i.e., removing
 * balls from the current board and sending them to the correct board)
 * 
 * Representation: A Circle of radius 0.5, with center at the center of the
 * boundary box with top left corner being the coordinates passed into the
 * constructor
 * 
 * Rep Invariant: Location should never be outside the board, which is defined
 * as the boundary from ((0,0) --> (20, 20)), that is, the initial coordinates
 * of the top left corner of the boundary box containing the Portal should be
 * [(0,0) --> (20,20)], inclusive. Portal's Circle radius should always be 0.5.
 */
public class Portal implements Gadget {
    private final double MAX_COORDINATE = 19.0;
    private final double RADIUS = 0.5;

    private OrderedPair origin;
    private Circle circle;
    private List<Gadget> gadgetsToAction;
    private Board board;
    private String name;

    private String portalName;
    private String clientName;
    private String clientPortalName;

    /**
     * Constructor for the Portal object, initializing it as a Circle with
     * radius 0.5, with center at (initialX + 0.5, initialY + 0.5). The initial
     * location values are stored in an OrderedPair object, instead of just as
     * two stand-alone doubles.
     * 
     * @param initialX
     *            double Provides the x coordinate of the top left corner of the
     *            boundary box that will contain the Portal
     * @param initialY
     *            double Provides the y coordinate of the top left corner of the
     *            boundary box that will contain the Portal
     * @param board
     *            Board where the Portal is being placed
     * @param xLoc
     *            double representing the x location of where the ball will be
     *            transferred
     * @param yLoc
     *            double representing the y location of where the ball will be
     *            transferred
     * @param gadgetsToAction
     *            List of Gadget objects that are triggered when a ball hits
     *            this Portal
     */
    public Portal(double initialX, double initialY, Board board,
            String portalName, String clientName, String clientPortalName,
            List<Gadget> gadgetsToAction) {
        double actualX = initialX + RADIUS;
        double actualY = initialY + RADIUS;

        this.origin = new OrderedPair(initialX, initialY);
        this.circle = new Circle(actualX, actualY, RADIUS);
        this.gadgetsToAction = new ArrayList<Gadget>();
        this.board = board;

        this.portalName = portalName;
        this.clientName = clientName;
        this.clientPortalName = clientPortalName;

        if (gadgetsToAction != null) {
            this.gadgetsToAction.addAll(gadgetsToAction);
        }

        checkRep();
    }

    /**
     * Checks the rep invariants and asserts false if any of the conditions
     * aren't met.
     */
    private void checkRep() {
        assert (this.origin.getX() >= 0.0 && this.origin.getX() <= MAX_COORDINATE);
        assert (this.origin.getY() >= 0.0 && this.origin.getY() <= MAX_COORDINATE);
        assert (this.circle.getRadius() == RADIUS);
    }

    @Override
    public double timeUntilCollision(Ball ball) {
        if (!board.isServerPlay()
                || (board.getBallsToRemove().keySet().contains(portalName) && board.getBallsToRemove()
                        .get(portalName).contains(ball)))
            return Double.MAX_VALUE;
        Circle ballCircle = ball.getCirc();
        Vect ballVec = ball.getVec();
        return Geometry.timeUntilCircleCollision(
                new Circle(this.circle.getCenter(), this.circle.getRadius()),
                ballCircle, ballVec);
    }

    @Override
    public void affectBall(Ball ball) {
        double minTime = timeUntilCollision(ball);
        double newX = ball.getLoc().getX() + ball.getVec().x() * minTime;
        double newY = ball.getLoc().getY() + ball.getVec().y() * minTime;
        ball.setLoc(newX, newY);

        this.board.sendBallThroughPortal(ball, this.portalName,
                this.clientName, this.clientPortalName);

        for (Gadget gadget : gadgetsToAction) {
            gadget.doAction();
        }
    }

    @Override
    public void doAction() {
        return;
    }

    @Override
    public char getString() {
        return 'o';
    }

    @Override
    public OrderedPair getSize() {
        return new OrderedPair(1, 1);
    }

    @Override
    public OrderedPair getLoc() {
        return new OrderedPair(this.origin.getX(), this.origin.getY());
    }

    @Override
    public void changeState(double timeElapsed) {
        return;
    }

    @Override
    public void setInvisibility(boolean isInvisible) {
        return;
    }

    @Override
    public void setClientName(String name) {
        this.clientName = name;
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
    public void addToTriggered(Gadget gadget) {
        this.gadgetsToAction.add(gadget);
    }

    @Override
    public String getClientName() {
        return this.clientName;
    }

    @Override
    public void drawOnFrame(Graphics2D g2d, double SCALE_FACTOR) {
        g2d.setStroke(new BasicStroke(7));

        g2d.drawOval((int) ((origin.getX() + DRAW_OFFSET) * SCALE_FACTOR),
                (int) ((origin.getY() + DRAW_OFFSET) * SCALE_FACTOR),
                (int) (2 * RADIUS * SCALE_FACTOR),
                (int) (2 * RADIUS * SCALE_FACTOR));
    }

}
