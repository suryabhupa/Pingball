package gadgets;

import java.awt.Graphics2D;

/**
 * The Gadget interface is used to emulate any kind of Gadget; behaviors that
 * each gadget should universally have are outlined here, and are overridden as
 * necessary to create correct behavior.
 * 
 * The interface's purpose is to have a universal front so that gadgets' methods
 * can be called without knowledge of what each specific gadget is, but will
 * perform correctly regardless.
 * 
 * Abstraction Function: Serves as a 'superclass' for all gadgets, allowing a
 * user of multiple gadget classes to easily call methods of the gadgets without
 * knowing the identity of the gadget itself.
 * 
 * Representation: Implemented as an interface to be implemented by any other
 * gadget.
 * 
 * Rep Invariant: No rep invariants exist; all gadgets must implement at least
 * these methods and have the DRAW_OFFSET = 2.
 */
public interface Gadget {

    // DRAW_OFFSET is a constant that dictates the offset of our drawing
    // graphics methods, and is used as a constant for all gadgets.
    final double DRAW_OFFSET = 2;

    /**
     * A gadget changes its state within a given timeElapsed
     * 
     * @param timeElapsed
     *            the time between a collision/frame display and another
     *            collision/frame display
     */
    public void changeState(double timeElapsed);

    /**
     * Returns the time it takes for a ball to hit the gadget.
     * 
     * @param ball
     *            Ball to be collided with (potentially)
     * @return double representing time it takes for the given ball to hit the
     *         gadget
     */
    public double timeUntilCollision(Ball ball);

    /**
     * Changes the ball's properties. This method requires that the ball will
     * collide with the gadget, assuming there are no other gadgets or balls in
     * the path from ball to gadget.
     * 
     * @param ball
     *            Ball to be affected
     */
    public void affectBall(Ball ball);

    /**
     * The gadget does its action.
     */
    public void doAction();

    /**
     * Gets the character representation of the gadget.
     * 
     * @return a char representing the gadget
     */
    public char getString();

    /**
     * Gets the size of the gadget.
     * 
     * @return an OrderedPair representing the (x, y) size of the gadget
     */
    public OrderedPair getSize();

    /**
     * Gets the OrderedPair denoting the location of the gadget
     * 
     * @return an OrderedPair representing the (x, y) location of the gadget
     */
    public OrderedPair getLoc();

    /**
     * Method to set the a Gadget as invisible. Only applies to Walls.
     * 
     * @param isInvisible
     *            boolean that sets the Gadget's invisibility
     */
    public void setInvisibility(boolean isInvisible);

    /**
     * Method to set the client that a Gadget should transport to.
     * 
     * @param name
     *            name of new client
     */
    public void setClientName(String name);

    /**
     * Method to get the name of the gadget.
     * 
     * @return String representing name of gadget
     */
    public String getName();

    /**
     * Method to set the name of the gadget.
     * 
     * @param name
     *            String representing the name of the gadget that we assign
     */
    public void setName(String name);

    /**
     * Method to add gadget to the list of gadgets triggered by this gadget
     * 
     * @param newGadget
     *            Gadget object to add to list of triggered gadgets
     */
    public void addToTriggered(Gadget newGadget);

    /**
     * Method to get the clientName for a gadget
     * 
     * @return String representing client name
     */
    public String getClientName();

    /**
     * Method to draw the Gadget on a given frame.
     * 
     * @param g2d
     *            Graphics2D object to assist in rendering differnet kinds of
     *            geometries
     * @param SCALE_FACTOR
     *            double that represents the scaling of the GUI for different
     *            screens
     */
    public void drawOnFrame(Graphics2D g2d, double SCALE_FACTOR);

}
