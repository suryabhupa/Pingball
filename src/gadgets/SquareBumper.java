package gadgets;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import physics.Circle;
import physics.Geometry;
import physics.LineSegment;

/**
 * A SquareBumper class that extends Gadget, representing a Square Bumper in the
 * Pingball game.
 * 
 * Abstraction function: A Square Bumper that should obey physical laws (i.e.,
 * bouncing balls off it correctly)
 * 
 * Representation: Four Line Segments of length 1, arranged as a square, with
 * the top left corner being the coordinates passed into the constructor; four
 * Circles with radius 0 at the intersection of the four Line Segments (to cover
 * edge corner cases).
 * 
 * Rep Invariant: Location should never be outside the board, which is defined
 * as the boundary from ((0,0) --> (20, 20)), that is, the initial coordinates
 * of the top left corner of the boundary box containing the Square Bumper
 * should be [(0,0) --> (20,20)], inclusive. SquareBumper's Circle radii should
 * always be 0 and its LineSegment lengths should be 1.
 */
public class SquareBumper implements Gadget {

    private final double SIDELENGTH = 1.0;
    private final LineSegment[] sides;
    private final Circle[] vertices; // vertices are circles of radius 0
    private List<Gadget> gadgetsTriggered = new ArrayList<Gadget>();
    private final OrderedPair location;
    private String name;

    /**
     * Initializes a new Square Bumper
     * 
     * @param x
     *            the x-coordinate of the top left corner of the bumper
     * @param y
     *            the y-coordinate of the top left corner of the bumper
     */
    public SquareBumper(int x, int y, List<Gadget> triggerGadgets) {

        sides = new LineSegment[4];
        sides[0] = new LineSegment(x, y, x, y + SIDELENGTH); // Left side of
                                                             // square
        sides[1] = new LineSegment(x, y + 1, x + 1, y + SIDELENGTH); // Bottom
                                                                     // side of
                                                                     // square
        sides[2] = new LineSegment(x + SIDELENGTH, y + SIDELENGTH, x
                + SIDELENGTH, y); // Right side of square
        sides[3] = new LineSegment(x + SIDELENGTH, y, x, y); // Top side of
                                                             // square

        vertices = new Circle[4];
        vertices[0] = new Circle(x, y, 0);
        vertices[1] = new Circle(x, y + SIDELENGTH, 0);
        vertices[2] = new Circle(x + SIDELENGTH, y, 0);
        vertices[3] = new Circle(x + SIDELENGTH, y + SIDELENGTH, 0);

        gadgetsTriggered.addAll(triggerGadgets);
        location = new OrderedPair(x, y);
        checkRep();
    }

    @Override
    public double timeUntilCollision(Ball ball) {
        double timeUntil = Double.POSITIVE_INFINITY;

        // Find minimum time for ball to hit one of the sides of the square
        for (LineSegment side : sides) {
            double timeToSide = Geometry.timeUntilWallCollision(side,
                    ball.getCirc(), ball.getVec());
            if (timeToSide < timeUntil) {
                timeUntil = timeToSide;
            }
        }

        // Find minimum time for ball to hit one of the vertices of the square
        for (Circle vertex : vertices) {
            double timeToVertex = Geometry.timeUntilCircleCollision(vertex,
                    ball.getCirc(), ball.getVec());
            if (timeToVertex < timeUntil) {
                timeUntil = timeToVertex;
            }
        }
        return timeUntil;
    }

    @Override
    public void affectBall(Ball ball) {
        double minTime = Double.MAX_VALUE;
        LineSegment minWall = null;
        Circle minCorner = null;

        for (LineSegment wall : this.sides) {
            double time = Geometry.timeUntilWallCollision(
                    new LineSegment(wall.p1(), wall.p2()), ball.getCirc(),
                    ball.getVec());
            if (time < minTime) {
                minTime = time;
                minWall = wall;
            }
        }

        for (Circle corner : this.vertices) {
            double time = Geometry.timeUntilCircleCollision(
                    new Circle(corner.getCenter(), corner.getRadius()),
                    ball.getCirc(), ball.getVec());
            if (time < minTime) {
                minTime = time;
                minCorner = corner;
            }
        }

        double newX = ball.getLoc().getX() + ball.getVec().x() * minTime;
        double newY = ball.getLoc().getY() + ball.getVec().y() * minTime;
        ball.setLoc(newX, newY);

        if (minCorner != null) {
            ball.setVec(Geometry.reflectCircle(minCorner.getCenter(), ball
                    .getCirc().getCenter(), ball.getVec()));
        } else {
            ball.setVec(Geometry.reflectWall(new LineSegment(minWall.p1(),
                    minWall.p2()), ball.getVec()));
        }

        for (Gadget gadget : this.gadgetsTriggered) {
            gadget.doAction();
        }
        checkRep();
    }

    @Override
    public char getString() {
        return '#';
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
    public void doAction() {
        return;
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

    /**
     * Method to help test that square bumper is created properly by line
     * segments
     * 
     * @return the line segments that make up the square bumper
     */
    protected List<LineSegment> getLineSegments() {
        List<LineSegment> lines = new ArrayList<LineSegment>();
        for (LineSegment line : this.sides) {
            lines.add(line);
        }
        return lines;
    }

    /**
     * Method to help test that square bumper is created properly by circles
     * 
     * @return the circles that make up the square bumper
     */
    protected List<Circle> getCircles() {
        List<Circle> circles = new ArrayList<Circle>();
        for (Circle c : this.vertices) {
            circles.add(c);
        }
        return circles;
    }
    
    /**
     * Method to return the gadgets that the square bumper triggers.
     * 
     * @return List<Gadget> the SquareBumper's list of gadgets that it triggers.
     */
    public List<Gadget> getGadgetsTriggered() {
        return this.gadgetsTriggered;
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
        g2d.fillRect((int) ((location.getX() + DRAW_OFFSET) * SCALE_FACTOR),
                (int) ((location.getY() + DRAW_OFFSET) * SCALE_FACTOR),
                (int) (SIDELENGTH * SCALE_FACTOR),
                (int) (SIDELENGTH * SCALE_FACTOR));
    }

}
