package gadgets;

/**
 * Simple immutable class representing an ordered pair.
 */
public class OrderedPair {
    private final double x;
    private final double y;

    /**
     * Constructor for the OrderedPair
     * 
     * @param x
     *            double represents the x-coordinate
     * @param y
     *            double represents the y-coordinate
     */
    public OrderedPair(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /** OBSERVER METHOD **/

    /**
     * Gets the x-coordinate of the OrderedPair
     * 
     * @return double x-coordinate
     */
    public double getX() {
        return this.x;
    }

    /**
     * Gets the y-coordinate of the OrderedPair
     * 
     * @return double y-coordinate
     */
    public double getY() {
        return this.y;
    }
}
