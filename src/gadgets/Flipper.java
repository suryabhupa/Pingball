package gadgets;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import physics.Angle;
import physics.Circle;
import physics.Geometry;
import physics.LineSegment;
import physics.Vect;

/**
 * A Flipper class that implements the Gadget interface that represents a
 * flipper in the Pingball game. Methods here provide methods to create the
 * flipper, and calculate collision events with balls as well as rotate the
 * flipper upon trigger.
 * 
 * Abstraction function: A flipper that has coordinates in the playing field and
 * can rotate depending on its left or right designation and its current
 * orientation.
 * 
 * Representation: Two LineSegments and Three Circle corners.
 * 
 * Rep Invariant: Bounding edges and corners are never placed or rotate outside
 * of the playing field.
 */
public class Flipper implements Gadget {
    private final double RIGHT_ANGLE = 90.0;
    private final double VELOCITY = Math.toRadians(1080.0);

    private OrderedPair location;
    private LineSegment edge;
    private List<Circle> corners = new ArrayList<Circle>();
    private boolean isLeftFlipper;
    private Angle originalOrientation;
    private Board board;
    private String name;

    // orientation is 2 when rotation, 1 when vertical (with respect to the
    // originialOrientation), 0 when horizontal (with respect to the
    // originalOrientation)
    private int orientation = 1;
    private int prevOrientation = -1;
    private Angle currAngle = Angle.ZERO;
    private List<Gadget> gadgetsToAction;

    private final static double COEFFICIENT_OF_REFLECTION = 0.95;

    /**
     * Constructor for Flipper class; uses arguments to specify entirely the
     * behavior of the flipper
     * 
     * @param initialX
     *            double for x-coordinate of pivot point
     * @param initialY
     *            double for y-coordinate of pivot point
     * @param isLeftFlipper
     *            boolean for isLeft or isRight flipper
     * @param orientation
     *            double representing starting orientation, which is either 0 or
     *            90
     * @param board
     *            Board object that it is being placed on
     * @param gadgetsToAction
     *            List<Gadget> list of gadgets that can be triggered by a
     *            flipper
     * @throws Exception
     *             throws an Exception
     */
    public Flipper(double initialX, double initialY, boolean isLeftFlipper,
            Angle orientationToBegin, Board board, List<Gadget> gadgetsToAction)
            throws Exception {
        this.board = board;
        this.isLeftFlipper = isLeftFlipper;
        this.originalOrientation = orientationToBegin;
        if (!board.canIAddToBoard(new OrderedPair(initialX, initialY),
                new OrderedPair(2, 2)))
            throw new Exception("This flipper can't be added");

        if (originalOrientation.equals(Angle.DEG_90)) {
            this.location = new OrderedPair(initialX + 2, initialY);
            if (!isLeftFlipper)
                this.location = new OrderedPair(initialX + 2, initialY + 2);
        } else if (originalOrientation.equals(Angle.DEG_180)) {
            this.location = new OrderedPair(initialX + 2, initialY + 2);
            if (!isLeftFlipper)
                this.location = new OrderedPair(initialX, initialY + 2);
        } else if (originalOrientation.equals(Angle.DEG_270)) {
            this.location = new OrderedPair(initialX, initialY + 2);
            if (!isLeftFlipper)
                this.location = new OrderedPair(initialX, initialY);
        } else {
            this.location = new OrderedPair(initialX, initialY);
            if (!isLeftFlipper)
                this.location = new OrderedPair(initialX + 2, initialY);
        }

        if (originalOrientation.equals(Angle.ZERO)) {
            edge = new LineSegment(location.getX(), location.getY(),
                    location.getX(), location.getY() + 2);
            corners.add(new Circle(location.getX(), location.getY(), 0));
            corners.add(new Circle(location.getX(), location.getY() + 2, 0));
        } else if (originalOrientation.equals(Angle.DEG_90)) {
            edge = new LineSegment(location.getX(), location.getY(),
                    location.getX() - 2, location.getY());
            corners.add(new Circle(location.getX() - 2, location.getY(), 0));
            corners.add(new Circle(location.getX(), location.getY(), 0));
        } else if (originalOrientation.equals(Angle.DEG_180)) {
            edge = new LineSegment(location.getX(), location.getY(),
                    location.getX(), location.getY() - 2);
            corners.add(new Circle(location.getX(), location.getY(), 0));
            corners.add(new Circle(location.getX(), location.getY() - 2, 0));
        } else {
            edge = new LineSegment(location.getX(), location.getY(),
                    location.getX() + 2, location.getY());
            corners.add(new Circle(location.getX(), location.getY(), 0));
            corners.add(new Circle(location.getX() + 2, location.getY(), 0));
        }

        this.gadgetsToAction = new ArrayList<Gadget>();
        if (gadgetsToAction != null) {
            this.gadgetsToAction.addAll(gadgetsToAction);
        }
    }

    /**
     * Method used to rotate flipper "behind the scenes" for frame-to-frame
     * calculations of collisions and resulting velocities. This method doesn't
     * change the string representation of the flipper, as this method only
     * handles the turning of the flipper
     * 
     * @param amount
     *            double the number of degrees to be rotate from 0, can only
     *            range from 0 to 90, which will flip it not at all, or up to a
     *            full quarter circle
     */
    public void rotateFlippersAngle(double amount) {
        if (this.prevOrientation == 1) {
            double newAmount = Math.min(amount,
                    RIGHT_ANGLE - Math.toDegrees(this.currAngle.radians()));

            Angle angleToRotate = new Angle(Math.toRadians(newAmount));
            if (isLeftFlipper) {
                angleToRotate = new Angle(2 * Math.PI
                        - Math.toRadians(newAmount));
            }

            edge = Geometry.rotateAround(edge, new Vect(location.getX(),
                    location.getY()), angleToRotate);

            for (int i = 0; i < corners.size(); i++) {
                corners.set(i, Geometry.rotateAround(corners.get(i), new Vect(
                        location.getX(), location.getY()), angleToRotate));
            }

            this.currAngle = this.currAngle.plus(new Angle(Math
                    .toRadians(newAmount)));
        } else if (this.prevOrientation == 0) {
            double newAmount = Math.min(amount,
                    Math.toDegrees(this.currAngle.radians()));
            Angle angleToRotate = new Angle(2 * Math.PI
                    - Math.toRadians(newAmount));
            if (isLeftFlipper) {
                angleToRotate = new Angle(Math.toRadians(newAmount));
            }

            edge = Geometry.rotateAround(edge, new Vect(location.getX(),
                    location.getY()), angleToRotate);

            for (int i = 0; i < corners.size(); i++) {
                corners.set(i, Geometry.rotateAround(corners.get(i), new Vect(
                        location.getX(), location.getY()), angleToRotate));
            }

            this.currAngle = this.currAngle.minus(new Angle(Math
                    .toRadians(newAmount)));
        }
    }

    /**
     * Calculates time until collision; method will have different calculations
     * depending on if the flipper is rotating or is standing still.
     * 
     * @param ball
     *            Ball to be collided with
     * @return double representing time until collision
     */
    @Override
    public double timeUntilCollision(Ball ball) {
        double minCornerTime = Double.MAX_VALUE;
        double minEdgeTime = Double.MAX_VALUE;
        if (this.orientation == 2) {
            double velocity = VELOCITY;
            if (this.prevOrientation == 1 && isLeftFlipper) {
                velocity = -1 * VELOCITY;
            } else if (this.prevOrientation == 0 && !isLeftFlipper) {
                velocity = -1 * VELOCITY;
            }
            for (Circle corner : this.corners) {
                double time = Geometry.timeUntilRotatingCircleCollision(
                        new Circle(corner.getCenter(), corner.getRadius()),
                        new Vect(location.getX(), location.getY()), velocity,
                        ball.getCirc(), ball.getVec());
                if (time < minCornerTime) {
                    if (currAngle.plus(new Angle(velocity * time)).compareTo(
                            Angle.DEG_90) == -1
                            && currAngle.plus(new Angle(velocity * time))
                                    .compareTo(Angle.ZERO) != -1)
                        minCornerTime = time;
                }
            }
            double time = Geometry.timeUntilRotatingWallCollision(
                    new LineSegment(edge.p1(), edge.p2()),
                    new Vect(location.getX(), location.getY()), velocity,
                    ball.getCirc(), ball.getVec());
            if (time < minEdgeTime) {
                if (currAngle.plus(new Angle(velocity * time)).compareTo(
                        Angle.DEG_90) == -1
                        && currAngle.plus(new Angle(velocity * time))
                                .compareTo(Angle.ZERO) != -1)
                    minEdgeTime = time;
            }
        } else {
            for (Circle corner : this.corners) {
                double time = Geometry.timeUntilCircleCollision(new Circle(
                        corner.getCenter(), corner.getRadius()),
                        ball.getCirc(), ball.getVec());
                if (time < minCornerTime) {
                    minCornerTime = time;
                }
            }
            double time = Geometry.timeUntilWallCollision(
                    new LineSegment(edge.p1(), edge.p2()), ball.getCirc(),
                    ball.getVec());
            if (time < minEdgeTime) {
                minEdgeTime = time;
            }
        }
        return Math.min(minCornerTime, minEdgeTime);
    }

    /**
     * Updates a balls' velocity and location as it is approaching a flipper
     * within contact range.
     * 
     * Flipper-specific postcondition: the ball is placed tangent to the flipper
     * at the end of this method at the point and time of contact, but is
     * designated a new velocity vector representing the new direction.
     * 
     * @param ball
     *            Ball to be affected
     */
    @Override
    public void affectBall(Ball ball) {
        double minTime = Double.MAX_VALUE;
        double minCornerTime = Double.MAX_VALUE;
        double minEdgeTime = Double.MAX_VALUE;
        Circle minCorner = null;
        LineSegment minEdge = null;
        if (this.orientation == 2) {
            double velocity = VELOCITY;
            if (this.prevOrientation == 1 && isLeftFlipper) {
                velocity = -1 * VELOCITY;
            } else if (this.prevOrientation == 0 && !isLeftFlipper) {
                velocity = -1 * VELOCITY;
            }
            for (Circle corner : this.corners) {
                double time = Geometry.timeUntilRotatingCircleCollision(
                        new Circle(corner.getCenter(), corner.getRadius()),
                        new Vect(location.getX(), location.getY()), velocity,
                        ball.getCirc(), ball.getVec());
                if (time < minCornerTime) {
                    minCornerTime = time;
                    minCorner = corner;
                }
            }

            double time = Geometry.timeUntilRotatingWallCollision(
                    new LineSegment(edge.p1(), edge.p2()),
                    new Vect(location.getX(), location.getY()), velocity,
                    ball.getCirc(), ball.getVec());
            if (time < minEdgeTime) {
                minEdgeTime = time;
                minEdge = edge;
            }
            minTime = Math.min(minCornerTime, minEdgeTime);

            if (minTime == Double.MAX_VALUE)
                return;

            double newX = ball.getLoc().getX() + ball.getVec().x() * minTime;
            double newY = ball.getLoc().getY() + ball.getVec().y() * minTime;
            ball.setLoc(newX, newY);

            if (minTime == minCornerTime) {
                ball.setVec(Geometry.reflectRotatingCircle(
                        new Circle(minCorner.getCenter(), minCorner.getRadius()),
                        new Vect(location.getX(), location.getY()), velocity,
                        ball.getCirc(), ball.getVec(),
                        COEFFICIENT_OF_REFLECTION));
            } else if (minTime == minEdgeTime) {
                ball.setVec(Geometry.reflectRotatingWall(new LineSegment(
                        minEdge.p1(), minEdge.p2()), new Vect(location.getX(),
                        location.getY()), velocity, ball.getCirc(), ball
                        .getVec(), COEFFICIENT_OF_REFLECTION));
            }
        } else if (this.orientation == 1 || this.orientation == 0) {

            for (Circle corner : this.corners) {
                double time = Geometry.timeUntilCircleCollision(new Circle(
                        corner.getCenter(), corner.getRadius()),
                        ball.getCirc(), ball.getVec());
                if (time < minCornerTime) {
                    minCornerTime = time;
                    minCorner = corner;
                }
            }

            double time = Geometry.timeUntilWallCollision(
                    new LineSegment(edge.p1(), edge.p2()), ball.getCirc(),
                    ball.getVec());
            if (time < minEdgeTime) {
                minEdgeTime = time;
                minEdge = edge;
            }
            minTime = Math.min(minCornerTime, minEdgeTime);

            if (minTime == Double.MAX_VALUE)
                return;

            double newX = ball.getLoc().getX() + ball.getVec().x() * minTime;
            double newY = ball.getLoc().getY() + ball.getVec().y() * minTime;
            ball.setLoc(newX, newY);

            if (minTime == minCornerTime) {
                ball.setVec(Geometry.reflectCircle(minCorner.getCenter(),
                        new Vect(ball.getLoc().getX(), ball.getLoc().getY()),
                        ball.getVec(), COEFFICIENT_OF_REFLECTION));
            } else if (minTime == minEdgeTime) {
                ball.setVec(Geometry.reflectWall(new LineSegment(minEdge.p1(),
                        minEdge.p2()), ball.getVec(), COEFFICIENT_OF_REFLECTION));
            }
        }

        for (Gadget gadget : gadgetsToAction) {
            gadget.doAction();
        }
    }

    /**
     * Changes the orientation of the flipper from standing still to be
     * rotating.
     */
    @Override
    public void doAction() {
        if (this.orientation != 2) {
            this.prevOrientation = this.orientation;
            this.orientation = 2;
        }
    }

    /**
     * Returns the string representation based on the orientation of the
     * flipper.
     * 
     * @return char which is either '-' or '|' for text display string
     */
    @Override
    public char getString() {
        if (this.orientation == 1) {
            if (this.originalOrientation == Angle.ZERO
                    || this.originalOrientation == Angle.DEG_180) {
                return '|';
            }
            return '-';
        } else {
            if (this.originalOrientation == Angle.ZERO
                    || this.originalOrientation == Angle.DEG_180) {
                return '-';
            }
            return '|';
        }
    }

    /**
     * Returns the size of the flipper depending on current orientation, i.e.
     * either a 2x1 or a 1x2 size.
     * 
     * @return OrderedPair representing the size of the flipper
     */
    @Override
    public OrderedPair getSize() {
        if (this.orientation == 1) {
            if (this.originalOrientation == Angle.ZERO
                    || this.originalOrientation == Angle.DEG_180) {
                return new OrderedPair(1, 2);
            }
            return new OrderedPair(2, 1);
        } else {
            if (this.originalOrientation == Angle.ZERO
                    || this.originalOrientation == Angle.DEG_180) {
                return new OrderedPair(2, 1);
            }
            return new OrderedPair(1, 2);
        }
    }

    /**
     * Returns pivot point as the flipper's location.
     * 
     * @return OrderedPair representing the location of the flipper
     */
    @Override
    public OrderedPair getLoc() {
        if (isLeftFlipper) {
            if (this.originalOrientation == Angle.ZERO) {
                return new OrderedPair(location.getX(), location.getY());
            } else if (this.originalOrientation == Angle.DEG_90) {
                if (this.orientation == 1) {
                    return new OrderedPair(location.getX() - 2, location.getY());
                }
                return new OrderedPair(location.getX(), location.getY());
            } else if (this.originalOrientation == Angle.DEG_180) {
                if (this.orientation == 1) {
                    return new OrderedPair(location.getX(), location.getY() - 2);
                }
                return new OrderedPair(location.getX() - 2, location.getY());
            }
            if (this.orientation == 1) {
                return new OrderedPair(location.getX(), location.getY());
            }
            return new OrderedPair(location.getX(), location.getY() - 2);
        } else {
            if (this.originalOrientation == Angle.ZERO) {
                if (this.orientation == 1) {
                    return new OrderedPair(location.getX() - 1, location.getY());
                }
                return new OrderedPair(location.getX() - 3, location.getY());
            } else if (this.originalOrientation == Angle.DEG_90) {
                if (this.orientation == 1) {
                    return new OrderedPair(location.getX() - 3, location.getY());
                }
                return new OrderedPair(location.getX() - 1, location.getY() - 2);
            } else if (this.originalOrientation == Angle.DEG_180) {
                if (this.orientation == 1) {
                    return new OrderedPair(location.getX() - 1,
                            location.getY() - 2);
                }
                return new OrderedPair(location.getX() - 1, location.getY());
            }
            return new OrderedPair(location.getX() - 1, location.getY());
        }
    }

    /**
     * Board class feeds in times, and when the flipper is rotating, this method
     * calculates the degree to which the flipper is rotate and moves the
     * flipper accordingly.
     * 
     * @param timeElapsed
     *            double represents the time between frame to factor into
     *            calculations
     */
    @Override
    public void changeState(double timeElapsed) {
        if (this.orientation == 2) {
            double angleToRotate = Math.toDegrees(VELOCITY) * timeElapsed;
            this.rotateFlippersAngle(angleToRotate);

            if (this.prevOrientation == 1) {
                if (Math.toDegrees(this.currAngle.radians()) >= RIGHT_ANGLE) {
                    this.orientation = 1;
                    board.removeGadget(this);
                    this.currAngle = new Angle(Math.PI / 2);
                    this.orientation = 0;
                    this.prevOrientation = -1;
                    board.updateGadget(this);
                }
            } else if (this.prevOrientation == 0) {
                if (Math.toDegrees(this.currAngle.radians()) <= 0.0) {
                    this.orientation = 0;
                    board.removeGadget(this);
                    this.currAngle = new Angle(0);
                    this.orientation = 1;
                    this.prevOrientation = -1;
                    board.updateGadget(this);
                }
            }
        }
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
        double BALL_END_RADIUS = 1.0 / 9.0;
        double EXTRA_OFFSET = 2.0;

        Vect v1 = corners.get(0).getCenter();
        Vect v2 = corners.get(1).getCenter();

        g2d.setStroke(new BasicStroke(10));

        g2d.drawLine((int) ((v1.x() + DRAW_OFFSET) * SCALE_FACTOR),
                (int) ((v1.y() + DRAW_OFFSET) * SCALE_FACTOR),
                (int) ((v2.x() + DRAW_OFFSET) * SCALE_FACTOR),
                (int) ((v2.y() + DRAW_OFFSET) * SCALE_FACTOR));
    }

}
