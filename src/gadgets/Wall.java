package gadgets;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import physics.Circle;
import physics.Geometry;
import physics.LineSegment;
import physics.Vect;

/**
 * A Wall class that extends Gadget, representing one of the four outer walls in
 * the Pingball game.
 * 
 * Abstraction function: A physical wall that should obey physical laws (i.e.,
 * bouncing balls off it correctly)
 * 
 * Representation: A Line Segment of length 20, with two Circles of radius 0 at
 * the two ends of the Line Segment (to cover edge corner cases).
 * 
 * Rep Invariant: Location should always be the outer edge of the board. This
 * means that the wall must be exactly vertical or exactly horizontal, with
 * length 20.
 */
public class Wall implements Gadget {

    private Vect origin;
    private Vect endpoint;

    private List<LineSegment> allLineSegs;
    private List<Circle> allCircles;
    private OrderedPair location;

    private final double x1;
    private final double x2;
    private final double y1;
    private final double y2;

    private boolean isInvisible;
    private String clientName;
    
    /** New client that the ball should be sent to */
    private Board board;

    private final double WALL_WIDTH = 1.0;

    // This is first initialized to "-" because we want to distinguish the Wall
    // gadgets from other gadgets that are parsed in from the board file.
    private String name = "-";

    /**
     * int representing the location of the wall. 0 is the top wall, 1 is the
     * right wall, 2 is the bottom wall, and 3 is the left wall
     */
    private int side;

    /**
     * Constructor for the Wall object, initializing it as a LineSegment with
     * length 20, representing one of the four outer walls, and two Circles with
     * radius 0 at either end of the Line Segment, for edge cases where the ball
     * hits the corner. The initial location values are stored in an OrderedPair
     * object, instead of just as two stand-alone doubles.
     * 
     * @param x1
     *            double Provides the initial x coordinate of the beginning of
     *            the top Line Segment
     * @param x2
     *            double Provides the final x coordinate of the end of the top
     *            Line Segment
     * @param y1
     *            double Provides the initial y coordinate of the beginning of
     *            the left Line Segment
     * @param y2
     *            double Provides the final y coordinate of the end of the left
     *            Line Segment
     * @param board
     *            Board representing the Board the Wall is in
     */
    public Wall(double x1, double y1, double x2, double y2, Board board) {

        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;

        location = new OrderedPair(x1, x2);

        if (x1 < x2 || y1 < y2) {
            origin = new Vect(x1, y1);
            endpoint = new Vect(x2, y2);
        } else {
            origin = new Vect(x2, y2);
            endpoint = new Vect(x1, y1);
        }

        this.board = board;

        if (x1 == x2) {
            if (x1 == -1.0)
                side = 3;
            else
                side = 1;
        } else {
            if (y1 == 20.0)
                side = 2;
            else
                side = 0;
        }

        allLineSegs = new ArrayList<LineSegment>();
        allCircles = new ArrayList<Circle>();

        allLineSegs
                .add(new LineSegment(new Vect(x1, y1), new Vect(x2 + 1, y1))); // Top
                                                                               // edge
        allLineSegs.add(new LineSegment(new Vect(x1, y2 + 1), new Vect(x2 + 1,
                y2 + 1))); // Bottom edge
        allLineSegs
                .add(new LineSegment(new Vect(x1, y1), new Vect(x1, y2 + 1))); // Left
                                                                               // edge
        allLineSegs.add(new LineSegment(new Vect(x2 + 1, y1), new Vect(x2 + 1,
                y2 + 1))); // Right edge

        allCircles.add(new Circle(new Vect(x1, y1), 0)); // Top-left vertex
        allCircles.add(new Circle(new Vect(x1, y2 + 1), 0)); // Bottom-left
                                                             // vertex
        allCircles.add(new Circle(new Vect(x2 + 1, y1), 0)); // Top-right vertex
        allCircles.add(new Circle(new Vect(x2 + 1, y2 + 1), 0)); // Bottom-right
                                                                 // vertex
    }

    @Override
    public double timeUntilCollision(Ball ball) {
        double minTime = Double.MAX_VALUE;
        for (LineSegment line : allLineSegs) {
            double collisionTime = Geometry.timeUntilWallCollision(line,
                    ball.getCirc(), ball.getVec());
            if (collisionTime < minTime)
                minTime = collisionTime;
        }
        for (Circle circ : allCircles) {
            double collisionTime = Geometry.timeUntilCircleCollision(circ,
                    ball.getCirc(), ball.getVec());
            if (collisionTime < minTime)
                minTime = collisionTime;
        }
        return minTime;
    }

    @Override
    public void affectBall(Ball ball) {
        double minTime = Double.MAX_VALUE;
        Ball newBall = null;

        double newX = ball.getLoc().getX() + ball.getVec().x()
                * timeUntilCollision(ball);
        double newY = ball.getLoc().getY() + ball.getVec().y()
                * timeUntilCollision(ball);
        ball.setLoc(newX, newY);

        if (this.isInvisible) {
            if (this.side == 0)
                board.sendBall(ball, ball.getLoc().getX(), 19.75, clientName);
            else if (this.side == 1)
                board.sendBall(ball, 0.25, ball.getLoc().getY(), clientName);
            else if (this.side == 2)
                board.sendBall(ball, ball.getLoc().getX(), 0.25, clientName);
            else
                board.sendBall(ball, 19.75, ball.getLoc().getY(), clientName);
        } else {
            for (LineSegment line : allLineSegs) {
                double collisionTime = Geometry.timeUntilWallCollision(line,
                        ball.getCirc(), ball.getVec());
                if (collisionTime < minTime) {
                    minTime = collisionTime;
                    newBall = new Ball(ball.getLoc().getX(), ball.getLoc()
                            .getY());
                    newBall.setVec(Geometry.reflectWall(line, ball.getVec()));
                }
            }
            for (Circle circ : allCircles) {
                double collisionTime = Geometry.timeUntilCircleCollision(circ,
                        ball.getCirc(), ball.getVec());
                if (collisionTime < minTime) {
                    minTime = collisionTime;
                    newBall = new Ball(ball.getLoc().getX(), ball.getLoc()
                            .getY());
                    newBall.setVec(Geometry.reflectCircle(circ.getCenter(),
                            ball.getCirc().getCenter(), ball.getVec()));
                }
            }
            ball.setLoc(newBall.getLoc().getX(), newBall.getLoc().getY());
            ball.setVec(newBall.getVec());
        }
    }

    @Override
    public char getString() {
        return '.';
    }

    @Override
    public OrderedPair getSize() {
        return new OrderedPair(Math.abs(origin.y() - endpoint.y()),
                Math.abs(origin.x() - endpoint.x()));
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
        this.isInvisible = isInvisible;
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
        return;
    }

    @Override
    public String getClientName() {
        return this.clientName;
    }

    @Override
    public void drawOnFrame(Graphics2D g2d, double SCALE_FACTOR) {
        g2d.fillRect((int) ((x1 + DRAW_OFFSET) * SCALE_FACTOR),
                (int) ((y1 + DRAW_OFFSET) * SCALE_FACTOR),
                (int) ((x2 - x1 + WALL_WIDTH) * SCALE_FACTOR),
                (int) ((y2 - y1 + WALL_WIDTH) * SCALE_FACTOR + DRAW_OFFSET));

        if (isInvisible) {
            Color oldColor = g2d.getColor();
            g2d.setColor(Color.white);
            g2d.setFont(new Font("Calibri", Font.BOLD, 40));
            char[] clientNameCharArray = clientName.toUpperCase().toCharArray();
            if (x1 == x2) { // Vertical wall
                for (int i = 0; i < clientNameCharArray.length; i++)
                    g2d.drawString(Character.toString(clientNameCharArray[i]),
                            (int) ((x1 + 0.3 + DRAW_OFFSET) * SCALE_FACTOR),
                            (int) (((y1 + y2) / 2 - clientNameCharArray.length
                                    / 2 + 1 + i + DRAW_OFFSET) * SCALE_FACTOR));
            } else { // Horizontal wall
                for (int i = 0; i < clientNameCharArray.length; i++)
                    g2d.drawString(Character.toString(clientNameCharArray[i]),
                            (int) (((x1 + x2) / 2 - clientNameCharArray.length
                                    / 2 + i + DRAW_OFFSET) * SCALE_FACTOR),
                            (int) ((y1 + 0.75 + DRAW_OFFSET) * SCALE_FACTOR));
            }
            g2d.setColor(oldColor);
        }
    }

}
