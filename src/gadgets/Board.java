package gadgets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import physics.Angle;
import physics.Geometry;
import physics.Vect;

/**
 * A Board class that represents the playing board and contains the outer walls
 * that define the board, contains all the gadgets and balls on the boards and
 * all the respective bounding edges and corners.
 * 
 * Abstraction function: This is a board that contains the game of Pingball and
 * can be used in conjunction with other boards or across networks.
 * 
 * Representation: Bounded by LineSegments edges and Circles corners and all
 * other objects are placed inside the board.
 * 
 * Rep Invariant: Dimensions are unchanging and all objects are contained in the
 * board, which they are by definition, or the objects would failed to be added.
 * 
 * Thread-safety argument: There are two Threads in Board: one that runs the
 * board, and another that listens to server messages.
 * 
 * Access within each of these threads to shared variables are protected by
 * locks, and PrintWriter's thread-unsafety is resolved with this protection.
 */
public class Board {

    private static final long serialVersionUID = 1L;
    private static final double DIMENSION = 20.0;
    private static final double FPS = 1.0 / 50.0;
    private static final int DIMENSION_SIZE = 22;
    private static final double DELTA_T = 0.0005; // 0.0024

    private double accelerationGravity = 25.0;
    private double mu1 = 0.025;
    private double mu2 = 0.025;

    protected List<Ball> balls;
    protected List<Gadget> gadgets;
    protected char[][] boardAsString;
    protected List<String> portalNames;
    protected Map<String, Set<Ball>> ballsToRemove;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Thread serverListener;

    protected String name;
    protected boolean serverPlay;
    protected String currentHost;
    protected int currentPort;

    protected boolean paused = false; // Used for pausing the game

    private static final int FRAME_WIDTH = 600; // 600
    private static final int FRAME_HEIGHT = FRAME_WIDTH * 24 / 20;
    private static final double SCALE_FACTOR = 60.0 * FRAME_WIDTH / 1600;

    protected static final Map<Integer, HashSet<String>> KEYUP_MAPPINGS = new HashMap<Integer, HashSet<String>>();
    protected static final Map<Integer, HashSet<String>> KEYDOWN_MAPPINGS = new HashMap<Integer, HashSet<String>>();

    /**
     * Single-machine play constructor method for Board class. Each outer wall
     * is added as a Gadget, and a two dimensional character array is used to
     * keep track of all objects and their string representations, and it is
     * this array that is updated at every timestep with new calculations about
     * the objects' locations and future locations.
     */
    public Board() {
        balls = new ArrayList<Ball>();
        gadgets = new ArrayList<Gadget>();
        portalNames = new ArrayList<String>();
        setBallsToRemove(new ConcurrentHashMap<String, Set<Ball>>());

        gadgets.add(new Wall(-1.0, -1.0, DIMENSION, -1.0, this));
        gadgets.add(new Wall(DIMENSION, -1.0, DIMENSION, DIMENSION, this));
        gadgets.add(new Wall(-1.0, DIMENSION, DIMENSION, DIMENSION, this));
        gadgets.add(new Wall(-1.0, -1.0, -1.0, DIMENSION, this));

        boardAsString = new char[DIMENSION_SIZE][DIMENSION_SIZE];
        for (int i = 0; i < DIMENSION_SIZE; i++) {
            boardAsString[0][i] = '.';
            boardAsString[DIMENSION_SIZE - 1][i] = '.';
            boardAsString[i][0] = '.';
            boardAsString[i][DIMENSION_SIZE - 1] = '.';
        }

        for (int i = 1; i < DIMENSION_SIZE - 1; i++) {
            for (int j = 1; j < DIMENSION_SIZE - 1; j++) {
                boardAsString[i][j] = ' ';
            }
        }
        this.setServerPlay(false);
    }

    /**
     * Set name of board
     */
    public void setBoardName(String name) {
        this.name = name;
    }

    /**
     * Connect to a server by specifying host and port
     * 
     * @param host
     *            Name of host, e.g. "localhost"
     * @param port
     *            Port number to connect to, must be between 0 and 65535,
     *            inclusive.
     * @return true if successfully connected, false otherwise.
     */
    public boolean setHostAndPort(String host, int port) {
        if (!host.equals("") && host != null && (0 <= port && port <= 65535)) {
            currentHost = host;
            currentPort = port;
            setServerPlay(true);
            try {
                socket = new Socket(host, port);
                in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                out = new PrintWriter(new OutputStreamWriter(
                        socket.getOutputStream()));
            } catch (Exception e) {
                setServerPlay(false);
                return false;
                // Do nothing with exceptions
            }
            out.println(this.name); // Give the server the client's name
            out.println(portalNames.size()); // Tell the server how many portals
                                             // are
            // there on the board
            for (String portalName : portalNames)
                out.println(portalName);
            out.flush();

            if (isServerPlay()) {
                // Create thread that listens to server
                serverListener = new Thread(new Runnable() {
                    public void run() {
                        try {
                            listenToServer();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                serverListener.start();
            }
            return true;
        }
        setServerPlay(false);
        return false;
    }

    /**
     * Disconnect from server if already connected to server.
     */
    public void disconnectFromServer() {
        System.out.println(isServerPlay());
        if (isServerPlay()) {
            setServerPlay(false);
            try {
                socket.close();
                in.close();
                out.close();
            } catch (IOException e) {
                setServerPlay(false);
            }
        }
    }

    /**
     * Method used to listen for messages from the server. Messages can be: -
     * new ball messages - other client joining messages - other client
     * disconnect messages
     * 
     * @throws IOException
     */
    protected void listenToServer() throws IOException {
        String line = "";
        while (true) {
            try {
                line = in.readLine();
            } catch (Exception e) {
            }
            synchronized (this) {
                String messageType = line.split(" ")[0];
                // New ball message format: NEWBALL xLoc yLoc xVelocity
                // yVelocity
                // Example: NEWBALL 0.0 3.3 -5.0 6.0
                // New ball through portal format: NEWBALLTHROUGHPORTAL
                // portalName
                // xVelocity yVelocity
                // Example: NEWBALLTHROUGHPORTAL Gamma -5.0 6.0
                // New client message format: NEWCLIENT clientName side
                // Example: NEWCLIENT Mars 0
                // Client disconnect message: DISCONNECT clientName side
                // Example: DISCONNECT Mars 0
                // Ball has been accepted into portal: PORTALBALLACCEPTED
                // portalName
                // Example: PORTALBALLACCEPTED Gamma
                // Ball has been rejected from portal: PORTALBALLREJECTED
                // portalName
                // Example: PORTALBALLREJECTED Gamma
                if (messageType.equals("NEWBALL")) {
                    double xLoc = Double.parseDouble(line.split(" ")[1]);
                    double yLoc = Double.parseDouble(line.split(" ")[2]);
                    double xVec = Double.parseDouble(line.split(" ")[3]);
                    double yVec = Double.parseDouble(line.split(" ")[4]);
                    Ball newBall = new Ball(xLoc, yLoc);
                    newBall.setVec(new Vect(xVec, yVec));
                    balls.add(newBall);
                } else if (messageType.equals("NEWBALLTHROUGHPORTAL")) {
                    System.out.println(line);
                    String portalName = line.split(" ")[1];
                    double xVec = Double.parseDouble(line.split(" ")[2]);
                    double yVec = Double.parseDouble(line.split(" ")[3]);

                    for (Gadget g : gadgets) {
                        String name = g.getName();
                        if (name != null) {
                            if (name.equals(portalName)) {
                                double xLoc = (double) g.getLoc().getX()
                                        + (double) g.getSize().getX() * 0.5;
                                double yLoc = (double) g.getLoc().getY()
                                        + (double) g.getSize().getY() * 0.5;
                                Ball newBall = new Ball(xLoc, yLoc);
                                newBall.setVec(new Vect(xVec, yVec));
                                balls.add(newBall);
                                break;
                            }
                        }
                    }
                } else if (messageType.equals("NEWCLIENT")) {
                    String clientName = line.split(" ")[1];
                    int side = Integer.parseInt(line.split(" ")[2]);
                    makeWallInvisible(clientName, side);
                } else if (messageType.equals("DISCONNECT")) {
                    int side = Integer.parseInt(line.split(" ")[2]);
                    makeWallSolid(side);
                } else if (messageType.equals("PORTALBALLACCEPTED")) {
                    String portalName = line.split(" ")[1];
                    if (getBallsToRemove().containsKey(portalName)) {
                        for (Ball b : getBallsToRemove().get(portalName))
                            balls.remove(b);
                        getBallsToRemove().put(portalName, new HashSet<Ball>());
                    }
                } else if (messageType.equals("PORTALBALLREJECTED")) {
                    String portalName = line.split(" ")[1];
                    if (getBallsToRemove().containsKey(portalName)) {
                        getBallsToRemove().put(portalName, new HashSet<Ball>());
                    }
                }
            }
        }
    }

    /**
     * Method used to send a Ball instance from one Board to another
     * 
     * @param ball
     *            Ball instance that is being sent from one Board to another
     * @param xLoc
     *            double representing the x location of the board the ball
     *            should appear at
     * @param yLoc
     *            double representing the y location of the board the ball
     *            should appear at
     * @param newClientName
     *            Client that the ball should be sent to
     */
    protected void sendBall(Ball ball, double xLoc, double yLoc,
            String newClientName) {
        // First, remove ball from current list of balls
        removeBall(ball);

        // Next, send new ball message to server
        // Format: NEWBALL newClientName xLoc yLoc xVelocity yVelocity
        // Example: NEWBALL Mars 0.0 3.3 -5.0 6.0
        String message = "NEWBALL " + newClientName + " ";
        message += Double.toString(xLoc) + " ";
        message += Double.toString(yLoc) + " ";
        message += Double.toString(ball.getVec().x()) + " ";
        message += Double.toString(ball.getVec().y()) + " ";
        out.println(message);
        out.flush();
    }

    /**
     * Method used to send a Ball instance from one Board to another through a
     * portal
     * 
     * @param ball
     *            Ball instance that is being sent from one Board to another
     * @param newClientName
     *            Client that the ball should be sent to
     * @param newPortalName
     *            Portal in the new client that the ball should be sent to
     */
    protected void sendBallThroughPortal(Ball ball, String portalName,
            String newClientName, String newPortalName) {
        Set<Ball> ballsPendingRemoval = new HashSet<Ball>();
        for (String portal : getBallsToRemove().keySet())
            ballsPendingRemoval.addAll(getBallsToRemove().get(portal));
        if (!ballsPendingRemoval.contains(ball) && isServerPlay()) {
            // First, send new ball message to server
            // Format: NEWBALLTHROUGHPORTAL oldClientName oldPortalName
            // newClientName
            // newPortalName xVelocity yVelocity
            // Example: NEWBALLTHROUGHPORTAL Mars Gamma -5.0 6.0
            String message = "NEWBALLTHROUGHPORTAL " + this.name + " "
                    + portalName + " " + newClientName + " " + newPortalName
                    + " ";
            message += Double.toString(ball.getVec().x()) + " ";
            message += Double.toString(ball.getVec().y()) + " ";
            out.println(message);
            out.flush();

            // Next, remove ball from current list of balls

            if (!getBallsToRemove().containsKey(portalName))
                getBallsToRemove().put(portalName, new HashSet<Ball>());
            getBallsToRemove().get(portalName).add(ball);
        }
    }

    /**
     * Adds a portal name to the list of portal names.
     * 
     * @param portalName
     *            . name of new portal that was added
     */
    public void addPortalName(String portalName) {
        portalNames.add(portalName);
    }

    /**
     * Adds a single Gadget to the board after guaranteeing within the box.
     * 
     * @param gadget
     *            Gadget to be added
     * @return boolean True/false if the adding of the gadget was successful.
     */
    public boolean addGadget(Gadget gadget) {
        OrderedPair gadgetLoc = gadget.getLoc();
        if (gadgetLoc.getX() > DIMENSION - 1.0 || gadgetLoc.getX() < 0.0) {
            return false;
        }
        if (gadgetLoc.getY() > DIMENSION - 1.0 || gadgetLoc.getY() < 0.0) {
            return false;
        }

        OrderedPair gadgetSize = gadget.getSize();

        for (int i = 0; i < gadgetSize.getX(); i++) {
            for (int j = 0; j < gadgetSize.getY(); j++) {
                if (boardAsString[(int) (gadgetLoc.getX() + 1 + i)][(int) (gadgetLoc
                        .getY() + 1 + j)] != ' ') {
                    return false;
                }
            }
        }

        char gadgetChar = gadget.getString();

        for (int i = 0; i < gadgetSize.getX(); i++) {
            for (int j = 0; j < gadgetSize.getY(); j++) {
                boardAsString[(int) (gadgetLoc.getX() + 1 + i)][(int) (gadgetLoc
                        .getY() + 1 + j)] = gadgetChar;
            }
        }

        this.gadgets.add(gadget);
        return true;
    }

    /**
     * Method that adds a key mapping between a KeyEvent.Integer int and a
     * Gadget, referenced by its name. Additionally, due to optional keyup or
     * keydown indications, we can further specify which of the two hashmaps
     * that we place the key-value mapping.
     * 
     * @param key
     *            Integer that corresponds to a VK event from the KeyEvent
     *            class; each Integer corresponds to a respective character
     * @param nameOfGadget
     *            String name of gadget that is being bound to the key; note
     *            that multiple keys can bind to the same gadget, and a gadget
     *            can be triggered by multiple keys
     * @param keyDir
     *            String indicating if the action is on key press or key
     *            release, for keydown or keyup respectively; any other input
     *            will be ignored.
     */
    public void addKeyMapping(Integer key, String nameOfGadget, String keyDir) {
        if (keyDir.equals("keyup")) {
            HashSet<String> currentValues = Board.KEYUP_MAPPINGS.get(key);
            if (currentValues == null) {
                currentValues = new HashSet<String>();
                Board.KEYUP_MAPPINGS.put(key, currentValues);
            }
            currentValues.add(nameOfGadget);
        } else if (keyDir.equals("keydown")) {
            HashSet<String> currentValues = Board.KEYDOWN_MAPPINGS.get(key);
            if (currentValues == null) {
                currentValues = new HashSet<String>();
                Board.KEYDOWN_MAPPINGS.put(key, currentValues);
            }
            currentValues.add(nameOfGadget);
        }
    }

    /**
     * Lets a gadget know if it can be placed on this board.
     * 
     * @param loc
     *            Ordered Pair that represents the top left corner of the top
     *            left boundary box of the gadget.
     * @param size
     *            Ordered Pair that represents the size of the gadget.
     * @return boolean representing if the gadget can be added to the board or
     *         not.
     */
    protected boolean canIAddToBoard(OrderedPair loc, OrderedPair size) {
        for (int i = 0; i < size.getX(); i++) {
            for (int j = 0; j < size.getY(); j++) {
                if (boardAsString[(int) (loc.getX() + 1 + i)][(int) (loc.getY() + 1 + j)] != ' ') {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * The counterpart of updateGadget(): Removes a specified gadget from the
     * board by removing the character(s) from the char array while calculation
     * is being done, or while the object is being updated in its string
     * representation.
     * 
     * @param gadget
     *            Gadget to be removed
     */
    protected void removeGadget(Gadget gadget) {
        OrderedPair gadgetLoc = gadget.getLoc();
        OrderedPair gadgetSize = gadget.getSize();

        for (int i = 0; i < gadgetSize.getX(); i++) {
            for (int j = 0; j < gadgetSize.getY(); j++) {
                boardAsString[(int) (gadgetLoc.getX() + 1 + i)][(int) (gadgetLoc
                        .getY() + 1 + j)] = ' ';
            }
        }
    }

    /**
     * The counterpart of removeGadget(): Adds the gadget back onto the board by
     * adding the character(s) back into the char array after being removed
     * while the gadget was being updated in its string representation.
     * 
     * @param gadget
     *            Gadget to be added back in
     * @return boolean True/false if the adding of the gadget was successful
     */
    protected boolean updateGadget(Gadget gadget) {
        char gadgetChar = gadget.getString();
        OrderedPair gadgetLoc = gadget.getLoc();
        OrderedPair gadgetSize = gadget.getSize();

        for (int i = 0; i < gadgetSize.getX(); i++) {
            for (int j = 0; j < gadgetSize.getY(); j++) {
                boardAsString[(int) (gadgetLoc.getX() + 1 + i)][(int) (gadgetLoc
                        .getY() + 1 + j)] = gadgetChar;
            }
        }

        return true;
    }

    /**
     * Adds a ball object to the board by first checking the rep invariant.
     * 
     * @param ball
     *            Ball to be added
     * @return boolean True/false if the adding of the ball was successful
     */
    public boolean addBall(Ball ball) {
        OrderedPair ballLoc = ball.getLoc();
        if (ballLoc.getX() > DIMENSION || ballLoc.getX() < 0.0) {
            return false;
        }
        if (ballLoc.getY() > DIMENSION || ballLoc.getY() < 0.0) {
            return false;
        }

        if (boardAsString[(int) (ballLoc.getX()) + 1][(int) (ballLoc.getY()) + 1] != ' '
                && boardAsString[(int) (ballLoc.getX()) + 1][(int) (ballLoc
                        .getY()) + 1] != '.') {
            return false;
        }

        this.balls.add(ball);
        return true;
    }

    /**
     * Method called to make a wall invisible
     * 
     * @param name
     *            String representing the name of the Board that is being
     *            attached to the current Board
     * @param side
     *            int representing the location of the wall. 0 is the top wall,
     *            1 is the right wall, 2 is the bottom wall, and 3 is the left
     *            wall
     */
    protected void makeWallInvisible(String name, int side) {
        gadgets.get(side).setInvisibility(true);
        gadgets.get(side).setClientName(name);

        String stringToPut = "";
        if (name.length() > DIMENSION_SIZE) {
            stringToPut = name.substring(0, DIMENSION_SIZE);
        } else {
            int length = DIMENSION_SIZE / 2 - name.length() / 2;
            for (int i = 0; i < length; i++) {
                stringToPut += '.';
            }
            stringToPut = stringToPut.concat(name);
            for (int i = stringToPut.length(); i < DIMENSION_SIZE; i++) {
                stringToPut += '.';
            }
        }

        if (side == 0) {
            for (int i = 0; i < DIMENSION_SIZE; i++) {
                boardAsString[i][0] = stringToPut.charAt(i);
            }

        } else if (side == 1) {
            for (int i = 0; i < DIMENSION_SIZE; i++) {
                boardAsString[DIMENSION_SIZE - 1][i] = stringToPut.charAt(i);
            }

        } else if (side == 2) {
            for (int i = 0; i < DIMENSION_SIZE; i++) {
                boardAsString[i][DIMENSION_SIZE - 1] = stringToPut.charAt(i);
            }

        } else {
            for (int i = 0; i < DIMENSION_SIZE; i++) {
                boardAsString[0][i] = stringToPut.charAt(i);
            }
        }
    }

    /**
     * Method called to make a wall solid
     * 
     * @param side
     *            int representing the location of the wall. 0 is the top wall,
     *            1 is the right wall, 2 is the bottom wall, and 3 is the left
     *            wall
     */
    protected void makeWallSolid(int side) {
        gadgets.get(side).setInvisibility(false);
        if (side == 0) {
            for (int i = 0; i < DIMENSION_SIZE; i++) {
                boardAsString[i][0] = '.';
            }

        } else if (side == 1) {
            for (int i = 0; i < DIMENSION_SIZE; i++) {
                boardAsString[DIMENSION_SIZE - 1][i] = '.';
            }

        } else if (side == 2) {
            for (int i = 0; i < DIMENSION_SIZE; i++) {
                boardAsString[i][DIMENSION_SIZE - 1] = '.';
            }

        } else {
            for (int i = 0; i < DIMENSION_SIZE; i++) {
                boardAsString[0][i] = '.';
            }
        }
    }

    /**
     * Removes a ball object from the board
     * 
     * @param ball
     *            Ball to be removed
     */
    protected void removeBall(Ball ball) {
        this.balls.remove(ball);
    }

    public String getName() {
        return name;
    }

    /**
     * Observer method to get the list of balls currently being contained in the
     * board.
     * 
     * @return List<Ball> list of balls in the board
     */
    public List<Ball> getListOfBalls() {
        List<Ball> listOfBalls = new ArrayList<Ball>();
        listOfBalls.addAll(this.balls);
        return listOfBalls;
    }

    /**
     * Gets list of gadgets currently on the board.
     * 
     * @return List<Gadget> list of all gadgets
     */
    public List<Gadget> getListOfGadgets() {
        List<Gadget> listOfGadgets = new ArrayList<Gadget>();
        listOfGadgets.addAll(this.gadgets);
        return listOfGadgets;
    }

    /**
     * Gets the appropriate key-to-string mappings that will indicate what keys
     * trigger what gadgets, who are referenced by their name.
     * 
     * @param keyDir
     *            String representing keyup mappings or keydown mappings
     * @return Map<Integer, List<String>> respective map of the keyups or
     *         keydowns generated from parsing the board file
     */
    public Map<Integer, HashSet<String>> getKeyDirMapping(String keyDir) {
        Map<Integer, HashSet<String>> keyMappings = new HashMap<Integer, HashSet<String>>();
        if (keyDir.equals("keyup"))
            keyMappings.putAll(Board.KEYUP_MAPPINGS);
        else if (keyDir.equals("keydown"))
            keyMappings.putAll(Board.KEYDOWN_MAPPINGS);
        return keyMappings;
    }

    /**
     * Method to set gravity
     * 
     * @param gravity
     *            double representing the acceleration of gravity
     */
    public void setGravity(double gravity) {
        this.accelerationGravity = gravity;
    }

    /**
     * Method to get gravity
     * 
     * @return double representing the current value for the acceleration of
     *         gravity
     */
    public double getGravity() {
        return this.accelerationGravity;
    }

    /**
     * Method to set mu1
     * 
     * @param mu1
     *            double representing the value of mu1
     */
    public void setMu1(double mu1) {
        this.mu1 = mu1;
    }

    /**
     * Method to get mu1
     * 
     * @return double representing the current value for mu1
     */
    public double getMu1() {
        return this.mu1;
    }

    /**
     * Method to set mu2
     * 
     * @param mu2
     *            double representing the value of mu2
     */
    public void setMu2(double mu2) {
        this.mu2 = mu2;
    }

    /**
     * Method to get mu2
     * 
     * @return double representing the current value for mu2
     */
    public double getMu2() {
        return this.mu2;
    }

    /**
     * Given a time frame, computes the angles, velocities, and positions after
     * all collisions
     * 
     * @param timeFrame
     *            a double indicating the timespan
     */
    protected synchronized void affectBoardState(double timeFrame) {
        double minTime = timeFrame;
        List<Gadget> currGadget = new ArrayList<Gadget>();
        List<Ball> currBall = new ArrayList<Ball>();
        List<Ball> firstBallInCollision = new ArrayList<Ball>();
        List<Ball> secondBallInCollision = new ArrayList<Ball>();

        // get the list of balls and and list of gadgets involved in the
        // earliest collision(s)
        // get the time also
        for (Ball ball : this.balls) {
            for (Gadget gadget : this.gadgets) {
                double timeUntilCollision = gadget.timeUntilCollision(ball);
                if (timeUntilCollision < minTime) {
                    currGadget = new ArrayList<Gadget>(Arrays.asList(gadget));
                    currBall = new ArrayList<Ball>(Arrays.asList(ball));
                    minTime = timeUntilCollision;
                } else if (timeUntilCollision == minTime) {
                    currGadget.add(gadget);
                    currBall.add(ball);
                }
            }
        }

        // same thing as above but for ball-ball. This can affect list of
        // gadgets and list of balls if there is an earlier collision
        for (int i = 0; i < this.balls.size(); i++) {
            for (int j = i + 1; j < this.balls.size(); j++) {
                Ball ball1 = this.balls.get(i);
                Ball ball2 = this.balls.get(j);
                double timeUntilCollision = Geometry
                        .timeUntilBallBallCollision(ball1.getCirc(),
                                ball1.getVec(), ball2.getCirc(), ball2.getVec());
                if (timeUntilCollision < minTime) {
                    firstBallInCollision = new ArrayList<Ball>(
                            Arrays.asList(ball1));
                    secondBallInCollision = new ArrayList<Ball>(
                            Arrays.asList(ball2));
                    currGadget = new ArrayList<Gadget>();
                    currBall = new ArrayList<Ball>();
                    minTime = timeUntilCollision;
                } else if (timeUntilCollision == minTime) {
                    firstBallInCollision.add(ball1);
                    secondBallInCollision.add(ball2);
                }
            }
        }

        // create a new set of balls
        Set<Ball> ballSet = new HashSet<Ball>();

        // if a ball is involved in the earliest collision that occurs then
        // we add to set of balls
        for (int i = 0; i < currGadget.size(); i++) {
            Ball ball = currBall.get(i);
            ballSet.add(ball);
            currGadget.get(i).affectBall(ball);
        }

        for (int i = 0; i < firstBallInCollision.size(); i++) {
            Ball ball1 = firstBallInCollision.get(i);
            Ball ball2 = secondBallInCollision.get(i);
            ballSet.add(ball1);
            ballSet.add(ball2);
            ball1.collidesWithBall(ball2);
        }

        // for all the balls that aren't in the set of balls, we just update
        // location
        for (Ball ball : this.balls) {

            if (!ballSet.contains(ball)) {
                double newX = ball.getLoc().getX() + ball.getVec().x()
                        * minTime;
                double newY = ball.getLoc().getY() + ball.getVec().y()
                        * minTime;
                ball.setLoc(newX, newY);
            }
        }

        // change gadget states
        for (Gadget gadget : this.gadgets) {
            gadget.changeState(minTime);
        }

        if (ballSet.size() > 0) {
            affectBoardState(timeFrame - minTime);
        }
    }

    /**
     * Method currently implements gravity if it is enabled for each ball,
     * updates the balls' velocities and is the main method used to test and
     * display the board in the main program.
     */
    protected synchronized void moveForwardFrame() {
        for (int i = 0; i < FPS / DELTA_T; i++) {
            for (Ball ball : this.balls) {
                if (ball.getGravityValue()) {
                    ball.setVec(ball.getVec().plus(
                            new Vect(Angle.DEG_90, accelerationGravity
                                    * DELTA_T)));
                }
                if (ball.getFrictionValue()) {
                    ball.setVec(ball.getVec().times(
                            1.0 - mu1 * DELTA_T - mu2 * ball.getVec().length()
                                    * DELTA_T));
                }
            }
            affectBoardState(DELTA_T);
        }
    }

    /**
     * Displays board with each of the gadgets, walls, bounding edges and
     * corners, and the balls
     */
    public void displayBoard() {

        List<OrderedPair> listOfLocs = new ArrayList<OrderedPair>();

        for (Ball ball : this.balls) {

            OrderedPair ballLoc = ball.getLoc();
            if ((int) ballLoc.getX() == DIMENSION_SIZE - 2
                    && (int) ballLoc.getY() == DIMENSION_SIZE - 2) {
                listOfLocs.add(new OrderedPair((int) ballLoc.getX(),
                        (int) ballLoc.getY()));
            } else if ((int) ballLoc.getX() == DIMENSION_SIZE - 2) {
                listOfLocs.add(new OrderedPair((int) ballLoc.getX(),
                        (int) ballLoc.getY() + 1));
            } else if ((int) ballLoc.getY() == DIMENSION_SIZE - 2) {
                listOfLocs.add(new OrderedPair((int) ballLoc.getX() + 1,
                        (int) ballLoc.getY()));
            } else {
                listOfLocs.add(new OrderedPair((int) ballLoc.getX() + 1,
                        (int) ballLoc.getY() + 1));
            }
        }

        for (int i = 0; i < DIMENSION_SIZE; i++) {
            for (int j = 0; j < DIMENSION_SIZE; j++) {
                boolean check = true;
                for (OrderedPair loc : listOfLocs) {
                    if (j == (int) loc.getX() && (int) loc.getY() == i) {
                        System.out.print('*');
                        check = false;
                        break;
                    }
                }
                if (check) {
                    System.out.print(boardAsString[j][i]);
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Basic test of equality, checking that there are the same number of
     * gadgets and balls in each boards, and that their names are equal.
     * 
     * @param other
     *            Other object to check equality with
     * @return boolean based on if the the two boards are, at the basic level,
     *         equal
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof Board) {
            Board otherBoard = (Board) other;

            if (this.getListOfGadgets().size() == otherBoard.getListOfGadgets()
                    .size()
                    && (this.name == otherBoard.name || (this.name != null
                            && otherBoard.name != null && this.name
                                .equals(otherBoard.name)))
                    && this.getListOfBalls().size() == otherBoard
                            .getListOfBalls().size()) {

                return true;
            }
        }
        return false;
    }

    /**
     * Getter method to return if server play is enabled or not.
     * 
     * @return boolean serverPlay, indicating whether server play is enabled or
     *         not.
     */
    public boolean isServerPlay() {
        return serverPlay;
    }

    /**
     * Setter method to set whether server play is enabled or not.
     * 
     * @param serverPlay
     *            boolean indicating whether server play is enabled or not.
     */
    public void setServerPlay(boolean serverPlay) {
        this.serverPlay = serverPlay;
    }

    /**
     * Getter method to return the balls to remove during portals' actions.
     * 
     * @return Map<String, Set<Ball>> the mapping of portals to the balls they
     *         need to remove.
     */
    public Map<String, Set<Ball>> getBallsToRemove() {
        return ballsToRemove;
    }

    /**
     * Setter method to set the balls to remove during portals' actions.
     * 
     * @param ballsToRemove
     *            Map<String, Set<Ball>> of names of portals to the balls they
     *            need to be removed.
     */
    public void setBallsToRemove(Map<String, Set<Ball>> ballsToRemove) {
        this.ballsToRemove = ballsToRemove;
    }

    /**
     * Getter method to get the current frame height of the GUI.
     * 
     * @return int representing frame height
     */
    public static int getFrameHeight() {
        return FRAME_HEIGHT;
    }

    /**
     * Getter method to get the current scale factor of the objects in the GUI.
     * 
     * @return double representing scale factor
     */
    public static double getScaleFactor() {
        return SCALE_FACTOR;
    }

    /**
     * Getter method to get the serial version UID
     * 
     * @return long representing serial version UID
     */
    public static long getSerialversionuid() {
        return serialVersionUID;
    }
}
