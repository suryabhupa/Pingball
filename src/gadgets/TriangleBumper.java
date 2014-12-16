package gadgets;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;

import physics.Angle;
import physics.Circle;
import physics.Geometry;
import physics.LineSegment;

/**
 * A TriangleBumper class that extends Gadget, representing a Triangle Bumper in
 * the Pingball game.
 * 
 * Abstraction function: A Triangle Bumper that should obey physical laws (i.e.,
 * bouncing balls off it correctly)
 * 
 * Representation: Two Line Segments of length 1 perpendicular to each other,
 * with another Line Segment of length sqrt(2), connecting the two ends of the
 * two Line Segments; three Circles with radius 0 at the intersection of the
 * three Line Segments (to cover edge corner cases).
 * 
 * Rep Invariant: Location should never be outside the board, which is defined
 * as the boundary from ((0,0) --> (20, 20)), that is, the initial coordinates
 * of the top left corner of the boundary box containing the Triangle Bumper
 * should be [(0,0) --> (20,20)], inclusive. TriangleBumper's Circle radii
 * should always be 0 and two of its LineSegment lengths should be 1, with the
 * third LineSegment's length being sqrt(2).
 */
public class TriangleBumper implements Gadget {
    private final static double SIDE = 1.0;
    private final static double MAX_COORDINATE = 19.0;

    private OrderedPair location;
    private Angle orientation;
    private List<LineSegment> walls;
    private List<Circle> corners;
    private List<Gadget> gadgetsToAction;
    private String name;

    /**
     * Constructor for the TriangleBumper object, initializing it as two Line
     * Segments of length 1 perpendicular to each other and another Line Segment
     * of length sqrt(2) connecting the two ends of the two Line Segments and
     * three Circles with radius 0, with centers at the three intersections of
     * the Line Segments. The orientation of the TriangleBumper object is
     * dependent on the argument given in the constructor (only values that are
     * multiples of 90 degrees are accepted). The initial location values are
     * stored in an OrderedPair object, instead of just as two stand-alone
     * doubles.
     * 
     * @param initialX
     *            double Provides the x coordinate of the top left corner of the
     *            boundary box that will contain the Triangle Bumper
     * @param initialY
     *            double Provides the y coordinate of the top left corner of the
     *            boundary box that will contain the Triangle Bumper
     * @param gadgetsToAction
     *            List of Gadget objects that are triggered when a ball hits
     *            this Triangle Bumper
     */
    public TriangleBumper(double initialX, double initialY,
            Angle orientationToSet, List<Gadget> triggerGadgets) {
        double actualX = initialX + SIDE / 2;
        double actualY = initialY + SIDE / 2;
        this.location = new OrderedPair(initialX, initialY);
        this.walls = new ArrayList<LineSegment>();
        this.corners = new ArrayList<Circle>();
        this.gadgetsToAction = new ArrayList<Gadget>();

        if (orientationToSet == Angle.DEG_270) {
            this.orientation = Angle.DEG_270;
            this.walls.add(new LineSegment(actualX - SIDE / 2, actualY + SIDE
                    / 2, actualX + SIDE / 2, actualY + SIDE / 2));
            this.walls.add(new LineSegment(actualX - SIDE / 2, actualY - SIDE
                    / 2, actualX - SIDE / 2, actualY + SIDE / 2));
            this.walls.add(new LineSegment(actualX - SIDE / 2, actualY - SIDE
                    / 2, actualX + SIDE / 2, actualY + SIDE / 2));

            this.corners.add(new Circle(actualX + SIDE / 2, actualY + SIDE / 2,
                    0.0));
            this.corners.add(new Circle(actualX - SIDE / 2, actualY + SIDE / 2,
                    0.0));
            this.corners.add(new Circle(actualX - SIDE / 2, actualY - SIDE / 2,
                    0.0));

        } else if (orientationToSet == Angle.DEG_180) {
            this.orientation = Angle.DEG_180;
            this.walls.add(new LineSegment(actualX - SIDE / 2, actualY + SIDE
                    / 2, actualX + SIDE / 2, actualY + SIDE / 2));
            this.walls.add(new LineSegment(actualX + SIDE / 2, actualY - SIDE
                    / 2, actualX + SIDE / 2, actualY + SIDE / 2));
            this.walls.add(new LineSegment(actualX - SIDE / 2, actualY + SIDE
                    / 2, actualX + SIDE / 2, actualY - SIDE / 2));

            this.corners.add(new Circle(actualX + SIDE / 2, actualY + SIDE / 2,
                    0.0));
            this.corners.add(new Circle(actualX - SIDE / 2, actualY + SIDE / 2,
                    0.0));
            this.corners.add(new Circle(actualX + SIDE / 2, actualY - SIDE / 2,
                    0.0));

        } else if (orientationToSet == Angle.DEG_90) {
            this.orientation = Angle.DEG_90;
            this.walls.add(new LineSegment(actualX + SIDE / 2, actualY - SIDE
                    / 2, actualX + SIDE / 2, actualY + SIDE / 2));
            this.walls.add(new LineSegment(actualX - SIDE / 2, actualY - SIDE
                    / 2, actualX + SIDE / 2, actualY - SIDE / 2));
            this.walls.add(new LineSegment(actualX - SIDE / 2, actualY - SIDE
                    / 2, actualX + SIDE / 2, actualY + SIDE / 2));

            this.corners.add(new Circle(actualX + SIDE / 2, actualY + SIDE / 2,
                    0.0));
            this.corners.add(new Circle(actualX + SIDE / 2, actualY - SIDE / 2,
                    0.0));
            this.corners.add(new Circle(actualX - SIDE / 2, actualY - SIDE / 2,
                    0.0));

        } else {
            this.orientation = Angle.ZERO;
            this.walls.add(new LineSegment(actualX - SIDE / 2, actualY - SIDE
                    / 2, actualX + SIDE / 2, actualY - SIDE / 2));
            this.walls.add(new LineSegment(actualX - SIDE / 2, actualY - SIDE
                    / 2, actualX - SIDE / 2, actualY + SIDE / 2));
            this.walls.add(new LineSegment(actualX - SIDE / 2, actualY + SIDE
                    / 2, actualX + SIDE / 2, actualY - SIDE / 2));

            this.corners.add(new Circle(actualX - SIDE / 2, actualY + SIDE / 2,
                    0.0));
            this.corners.add(new Circle(actualX - SIDE / 2, actualY - SIDE / 2,
                    0.0));
            this.corners.add(new Circle(actualX + SIDE / 2, actualY - SIDE / 2,
                    0.0));
        }

        if (triggerGadgets != null) {
            this.gadgetsToAction.addAll(triggerGadgets);
        }
        checkRep();
    }

    /**
     * Checks the rep invariants and asserts false if any of the conditions
     * aren't met.
     */
    private void checkRep() {
        assert (this.location.getX() >= 0.0 && this.location.getX() <= MAX_COORDINATE);
        assert (this.location.getY() >= 0.0 && this.location.getY() <= MAX_COORDINATE);
        for (Circle corner : this.corners) {
            assert (corner.getRadius() == 0.0);
        }

        int numOfStraightLines = 0;
        int numOfDiagonalLines = 0;
        for (LineSegment wall : this.walls) {
            if (wall.length() == 1.0) {
                numOfStraightLines++;
            } else if (wall.length() == Math.sqrt(2.0)) {
                numOfDiagonalLines++;
            }
        }
        assert (numOfStraightLines == 2);
        assert (numOfDiagonalLines == 1);
    }

    @Override
    public double timeUntilCollision(Ball ball) {
        double minTime = Double.MAX_VALUE;

        for (LineSegment wall : this.walls) {
            double time = Geometry.timeUntilWallCollision(
                    new LineSegment(wall.p1(), wall.p2()), ball.getCirc(),
                    ball.getVec());
            if (time < minTime) {
                minTime = time;
            }
        }

        for (Circle corner : this.corners) {
            double time = Geometry.timeUntilCircleCollision(
                    new Circle(corner.getCenter(), corner.getRadius()),
                    ball.getCirc(), ball.getVec());
            if (time < minTime) {
                minTime = time;
            }
        }

        return minTime;
    }

    @Override
    public void affectBall(Ball ball) {
        double minTime = Double.MAX_VALUE;
        LineSegment minWall = null;
        Circle minCorner = null;

        for (LineSegment wall : this.walls) {
            double time = Geometry.timeUntilWallCollision(
                    new LineSegment(wall.p1(), wall.p2()), ball.getCirc(),
                    ball.getVec());
            if (time < minTime) {
                minTime = time;
                minWall = wall;
            }
        }

        for (Circle corner : this.corners) {
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

        for (Gadget gadget : this.gadgetsToAction) {
            gadget.doAction();
        }
    }

    @Override
    public char getString() {
        if (this.orientation == Angle.ZERO || this.orientation == Angle.DEG_180) {
            return '/';
        }

        return '\\';
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

    @Override
    public String getClientName() {
        return null;
    }

    @Override
    public void drawOnFrame(Graphics2D g2d, double SCALE_FACTOR) {
        int[] xPoints;
        int[] yPoints;
        if (orientation == Angle.ZERO) {
            xPoints = new int[] {
                    (int) ((location.getX() + DRAW_OFFSET) * SCALE_FACTOR),
                    (int) ((location.getX() + SIDE + DRAW_OFFSET) * SCALE_FACTOR),
                    (int) ((location.getX() + DRAW_OFFSET) * SCALE_FACTOR) };
            yPoints = new int[] {
                    (int) ((location.getY() + SIDE + DRAW_OFFSET) * SCALE_FACTOR),
                    (int) ((location.getY() + DRAW_OFFSET) * SCALE_FACTOR),
                    (int) ((location.getY() + DRAW_OFFSET) * SCALE_FACTOR) };
        } else if (orientation == Angle.DEG_90) {
            xPoints = new int[] {
                    (int) ((location.getX() + DRAW_OFFSET) * SCALE_FACTOR),
                    (int) ((location.getX() + SIDE + DRAW_OFFSET) * SCALE_FACTOR),
                    (int) ((location.getX() + SIDE + DRAW_OFFSET) * SCALE_FACTOR) };
            yPoints = new int[] {
                    (int) ((location.getY() + DRAW_OFFSET) * SCALE_FACTOR),
                    (int) ((location.getY() + DRAW_OFFSET) * SCALE_FACTOR),
                    (int) ((location.getY() + SIDE + DRAW_OFFSET) * SCALE_FACTOR) };
        } else if (orientation == Angle.DEG_180) {
            xPoints = new int[] {
                    (int) ((location.getX() + SIDE + DRAW_OFFSET) * SCALE_FACTOR),
                    (int) ((location.getX() + SIDE + DRAW_OFFSET) * SCALE_FACTOR),
                    (int) ((location.getX() + DRAW_OFFSET) * SCALE_FACTOR) };
            yPoints = new int[] {
                    (int) ((location.getY() + DRAW_OFFSET) * SCALE_FACTOR),
                    (int) ((location.getY() + SIDE + DRAW_OFFSET) * SCALE_FACTOR),
                    (int) ((location.getY() + SIDE + DRAW_OFFSET) * SCALE_FACTOR) };
        } else {
            xPoints = new int[] {
                    (int) ((location.getX() + SIDE + DRAW_OFFSET) * SCALE_FACTOR),
                    (int) ((location.getX() + DRAW_OFFSET) * SCALE_FACTOR),
                    (int) ((location.getX() + DRAW_OFFSET) * SCALE_FACTOR) };
            yPoints = new int[] {
                    (int) ((location.getY() + SIDE + DRAW_OFFSET) * SCALE_FACTOR),
                    (int) ((location.getY() + SIDE + DRAW_OFFSET) * SCALE_FACTOR),
                    (int) ((location.getY() + DRAW_OFFSET) * SCALE_FACTOR) };
        }
        g2d.fillPolygon(xPoints, yPoints, 3);
    }

}
