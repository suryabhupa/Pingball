package parser;

import java.util.HashMap;

/**
 * Represents an element parsed from the board file. Each element is parsed from
 * non-empty, non-comment line, parsed from each of the different kids of
 * formats:
 * 
 * [elementType] [property1]=[value1] [property2]=[value2] ...
 * 
 * Abstraction Function: Represents a parsed line from the .pb files and its
 * associated object; stores each property as a key-value pairing.
 * 
 * Representation: The mapping between properties and values is done with a
 * HashMap<String, String> that maps a particular property to tis value, while
 * the elementType, or the first value parsed, is kept in a separate field.
 * 
 * Rep Invariant: elementType cannot be null.
 */

public class BoardElement {

    /** key-value pairings: (property1, type1), (property2, type2), ... **/
    private final HashMap<String, String> properties;
    /** type of element **/
    private final String elementType;

    /** CONSTRUCTOR AND PRODUCER METHODS **/

    /**
     * Instantiates an empty type of BoardElement with no associated properties,
     * but instantiated with an initial type.
     * 
     * @param elementType
     *            String to indicate the type of BoardElement
     */
    public BoardElement(String elementType) {
        this.elementType = elementType;
        this.properties = new HashMap<String, String>();
        checkRep();
    }

    /**
     * Maintains rep invariant of ensuring that the elementType is not null.
     */
    private void checkRep() {
        assert elementType != null;
    }

    /**
     * Parses a line and creates a BoardElement instance from the parsed
     * arguments and lines. Throws an IllegalArgumentException if the input
     * formatting is malformed.
     * 
     * @param line
     *            String to be parsed
     * @return new BoardElement created from the parsed line with the correct
     *         attributes
     */
    public static BoardElement parseLine(String line) {
        line = line.replaceAll("\\s*=\\s*", "=");
        String[] tokens = line.trim().split("\\s+");

        if (tokens.length == 0 || tokens[0].length() == 0
                || tokens[0].charAt(0) == '#') {
            return null;
        }

        BoardElement newBoardElement = new BoardElement(tokens[0]);
        for (int i = 1; i < tokens.length; i++) {
            String[] keyValuePair = tokens[i].split("=", 2);
            if (keyValuePair.length != 2) {
                throw new IllegalArgumentException("Incorrect input formats");
            }

            newBoardElement.setProperty(keyValuePair[0], keyValuePair[1]);
        }
        return newBoardElement;
    }

    /** GETTER METHODS **/

    /**
     * Gets the type of element associated with a particular BoardElement
     * 
     * @return String to indicate the type of BoardElement
     */
    public String getElementType() {
        return this.elementType;
    }

    /**
     * Gets the key-value pairings of properties associated to any BoardElement
     * 
     * @return HashMap<String, String> represents the key-value pairings
     */
    public HashMap<String, String> getProperties() {
        return this.properties;
    }

    /**
     * Returns the String value in the key-value pairing associated with
     * (property, value). If useDefault is true, and if key is not found in
     * properties, then the defaultValue is returned. Otherwise, an
     * IllegalArgumentException is thrown if key is not found in properties
     * 
     * @param property
     *            String the key in the key-value pairing
     * @param defaultValue
     *            String default value
     * @param useDefault
     *            boolean indicates whether we use default or throw an exception
     * @return String from the key-value pairing associated with property, or
     *         defaultValue otherwise
     */
    public String getString(String property, String defaultValue,
            boolean useDefault) {
        assert property != null;
        assert defaultValue != null;

        String value = this.properties.get(property);
        if (value == null)
            if (useDefault) {
                return defaultValue;
            } else {
                throw new IllegalArgumentException(
                        "Key not found in object's properties");
            }
        return value;
    }

    /**
     * Returns the int value in the key-value pairing associated with (property,
     * value). If useDefault is true, and if key is not found in properties,
     * then the defaultValue is returned. Otherwise, an IllegalArgumentException
     * is thrown if key is not found in properties
     * 
     * @param property
     *            String the key in the key-value pairing
     * @param defaultValue
     *            int default value
     * @param useDefault
     *            boolean indicates whether we use default or throw an exception
     * @return int from the key-value pairing associated with property, or
     *         defaultValue otherwise
     */
    public int getInt(String property, int defaultValue, boolean useDefault) {
        assert property != null;

        String value = this.properties.get(property);
        if (value == null)
            if (useDefault) {
                return defaultValue;
            } else {
                throw new IllegalArgumentException(
                        "Key not found in object's properties");
            }
        return Integer.parseInt(value);
    }

    /**
     * Returns the float value in the key-value pairing associated with
     * (property, value). If useDefault is true, and if key is not found in
     * properties, then the defaultValue is returned. Otherwise, an
     * IllegalArgumentException is thrown if key is not found in properties
     * 
     * @param property
     *            String the key in the key-value pairing
     * @param defaultValue
     *            float default value
     * @param useDefault
     *            boolean indicates whether we use default or throw an exception
     * @return float from the key-value pairing associated with property, or
     *         defaultValue otherwise
     */
    public double getFloat(String property, double defaultValue,
            boolean useDefault) {
        assert property != null;

        String value = this.properties.get(property);
        if (value == null)
            if (useDefault) {
                return defaultValue;
            } else {
                throw new IllegalArgumentException(
                        "Key not found in object's properties");
            }
        return Float.parseFloat(value);
    }

    /** SETTER METHODS **/

    /**
     * Sets a property in the key-value pairing for a BoardElement
     * 
     * @param property
     *            String representing a key in the key-value pairing and is one
     *            of possible properties
     * @param value
     *            String representing a value in the key-value pairing and is
     *            one of the possible values
     */
    public void setProperty(String property, String value) {
        assert property != null;
        assert value != null;
        this.properties.put(property, value);
        checkRep();
    }
}