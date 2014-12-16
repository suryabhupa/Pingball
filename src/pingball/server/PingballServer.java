package pingball.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * PingballServer class that instantiates the main server for the Pingball game.
 * The server handles connections, ensures thread-safety, and sends and receives
 * messages from clients, mediating interaction between clients in the process.
 * 
 * Abstraction Function: Serves as the server to handle all communication
 * between games and with games, while keeping all actions thread-safe.
 * 
 * Representation: Uses a number of Java classes to create the server and to
 * handle all socket communication between games.
 * 
 * Rep Invariant: Maintain that each of the instance variables of the class are
 * not null.
 * 
 * Thread safety argument: We used the "confinement of data" thread-safe
 * pattern. Each client is allocated it's own queue, stored in the thread-safe
 * Map clientQueues. All access to create and destroy queues are protected by
 * locks.
 * 
 * Each client queue in clientQueues is stores the messages that are to be sent
 * to that client. Since each client thread only adds messages to their queue,
 * there is no chance of two threads adding to the same queue at the same time
 * and causing a race condition.
 * 
 * Any modification of clientPortalNames is guarded by a lock. The Lists inside
 * are also wrapped by Collections.synchronizedList, and connections is also
 * wrapped by Collections.synchronizedList. Access or modification of the above
 * are all guarded by locks. Messages are sent as Strings, which are immutable.
 * 
 * A new thread is created for each socket, and the printWriter of each socket
 * is called within its respective thread only. Thus, the system is thread-safe.
 */
public class PingballServer {

    /** Default server port. */
    private static final int DEFAULT_PORT = 10987;
    /** Maximum port number as defined by ServerSocket. */
    private static final int MAXIMUM_PORT = 65535;

    /** Socket for receiving incoming connections. */
    private final ServerSocket serverSocket;

    /** Map mapping client names to their respective queues */
    private final Map<String, BlockingQueue<String>> clientQueues;
    /** Map giving the list of names of portals on a given client's board */
    private final Map<String, List<String>> clientPortalNames;
    /** List of all horizontal connections */
    private final List<String> connections;

    /** System.out message for debugging */
    private final boolean verbose = false;

    /**
     * Make a PingballServer that listens for connections on port.
     * 
     * @param port
     *            port number, require 0 <= port <= 65535
     * @throws IOException
     *             if an error occurs opening the server socket
     */
    public PingballServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        clientQueues = new ConcurrentHashMap<String, BlockingQueue<String>>();
        clientPortalNames = new ConcurrentHashMap<String, List<String>>();
        connections = Collections.synchronizedList(new ArrayList<String>());
        checkRep();
        if (verbose)
            System.out.println("Server started on port " + port);
    }

    /**
     * Check the rep invariant that each of the instance variables are not null.
     */
    private void checkRep() {
        assert serverSocket != null;
        assert clientQueues != null;
        assert clientPortalNames != null;
        assert connections != null;
    }

    /**
     * Helper method to return the list of active boards.
     * 
     * @return connections, list of active boards.
     */
    public Map<String, BlockingQueue<String>> returnBoards() {
        return clientQueues; // modify this.
    }

    /**
     * Run the server, listening for client connections and handling them. Also
     * creates a new thread that handles commands from System.in Never returns
     * unless an exception is thrown.
     * 
     * @throws IOException
     *             if the main server socket is broken (IOExceptions from
     *             individual clients do *not* terminate serve())
     */
    public void serve() throws IOException {
        // Make a new thread to take care of System.in arguments
        /**
         * =============================================================
         * ============== THREAD 1 : SYSTEM.IN ARGUMENTS ===============
         * =============================================================
         */
        Thread commands = new Thread(new Runnable() {
            Scanner in = new Scanner(System.in);

            public void run() {
                while (true) {
                    String command = in.nextLine(); // e.g. h NAME_left
                                                    // NAME_right
                    // Parse System.in arguments
                    parseSystemInCommand(command);
                }
            }
        });
        commands.start();
        while (true) {
            // block until a client connects
            final Socket socket = serverSocket.accept();
            // create new thread for the new client
            /**
             * ==========================================================
             * ================== CLIENT THREADS ========================
             * ==========================================================
             */
            Thread handler = new Thread(new Runnable() {
                public void run() {
                    try {
                        try {
                            // handle the client
                            handleConnection(socket);
                        } finally {
                            socket.close();
                        }
                    } catch (IOException ioe) {
                        // ioe.printStackTrace(); // but don't terminate serve()
                    }
                }
            });
            // start thread
            handler.start();
        }
    }

    /**
     * Method to parse the command sent when actions are done between client and
     * server, such as disconnects and connects, and linking walls of boards
     * together.
     * 
     * @param command
     *            String representing the command sent to be executed.
     */
    protected void parseSystemInCommand(String command) {
        String commandType = command.split(" ")[0];
        try {
            // New client message format: NEWCLIENT clientName side
            // Example: NEWCLIENT Mars 0
            if (commandType.equals("h")) {
                String NAME_left = command.split(" ")[1];
                String NAME_right = command.split(" ")[2];
                String NAME_leftmessage = "NEWCLIENT " + NAME_right + " 1";
                String NAME_rightmessage = "NEWCLIENT " + NAME_left + " 3";
                synchronized (this) {
                    clientQueues.get(NAME_left).put(NAME_leftmessage);
                    clientQueues.get(NAME_right).put(NAME_rightmessage);
                    connections.add(command);
                }
            } else if (commandType.equals("v")) {
                String NAME_top = command.split(" ")[1];
                String NAME_bottom = command.split(" ")[2];
                String NAME_topmessage = "NEWCLIENT " + NAME_bottom + " 2";
                String NAME_bottommessage = "NEWCLIENT " + NAME_top + " 0";

                synchronized (this) {
                    clientQueues.get(NAME_top).put(NAME_topmessage);
                    clientQueues.get(NAME_bottom).put(NAME_bottommessage);
                    connections.add(command);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle a single client connection. Returns when client disconnects.
     * 
     * @param socket
     *            socket where the client is connected
     * @throws IOException
     *             if the connection encounters an error or terminates
     *             unexpectedly
     */
    private void handleConnection(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // blocks until client sends their name
        final String clientName = in.readLine();
        if (verbose)
            System.out.println("Connection: " + clientName);

        synchronized (this) {
            // Create new LinkedBlockingQueue for this client
            clientQueues.put(clientName, new LinkedBlockingQueue<String>());

            // Create new Set to keep track of the portal names in this client's
            // board
            final int numPortals = Integer.parseInt(in.readLine());
            List<String> portalNames = Collections
                    .synchronizedList(new ArrayList<String>());
            for (int i = 0; i < numPortals; i++)
                portalNames.add(in.readLine());
            clientPortalNames.put(clientName, portalNames);
        }
        // Create new thread to send messages from the client queue to the
        // client
        /**
         * ====================================================================
         * ================ THREAD TO SEND MESSAGES TO CLIENT =================
         * ====================================================================
         */
        Thread sender = new Thread(new Runnable() {
            public void run() {
                try {
                    while (true) {
                        String message = clientQueues.get(clientName).take();
                        if (verbose)
                            System.out.printf("Sent message to %s: %s\n",
                                    message, clientName);
                        out.println(message);
                    }
                } catch (Exception e) {
                }
            }
        });
        sender.start();

        try {
            // Parse input from clients and put messages in the correct queue
            // Possible messages:
            // - NEWBALL newClientName xLoc yLoc xVelocity yVelocity
            // e.g. NEWBALL Mars 0.0 3.3 -5.0 6.0
            // - NEWBALLTHROUGHPORTAL oldClientName oldPortalname newClientName
            // newPortalName xVelocity
            // yVelocity
            // e.g. NEWBALLTHROUGHPORTAL Earth Alpha Mars Gamma -5.0 6.0
            for (String line = in.readLine(); line != null; line = in
                    .readLine()) {
                if (verbose)
                    System.out.println("Client message: " + line);
                String messageType = line.split(" ")[0];
                if (messageType.equals("NEWBALL")) {
                    String newClientName = line.split(" ")[1];
                    String xLoc = line.split(" ")[2];
                    String yLoc = line.split(" ")[3];
                    String xVec = line.split(" ")[4];
                    String yVec = line.split(" ")[5];
                    String message = messageType + " " + xLoc + " " + yLoc
                            + " " + xVec + " " + yVec;
                    // Put message in client's queue
                    try {
                        synchronized (this) {
                            clientQueues.get(newClientName).put(message);
                        }
                    } catch (Exception e) {
                    }
                }
                if (messageType.equals("NEWBALLTHROUGHPORTAL")) {
                    String oldClientName = line.split(" ")[1];
                    String oldClientPortalName = line.split(" ")[2];
                    String newClientName = line.split(" ")[3];
                    String newClientPortalName = line.split(" ")[4];
                    String xVec = line.split(" ")[5];
                    String yVec = line.split(" ")[6];
                    String message = messageType + " " + newClientPortalName
                            + " " + xVec + " " + yVec;

                    synchronized (this) {
                        // If the new client exists, and the new portal exists
                        // too
                        if (clientPortalNames.containsKey(newClientName)
                                && clientPortalNames.get(newClientName)
                                        .contains(newClientPortalName)) {
                            // Put message in client's queue
                            try {
                                clientQueues.get(newClientName).put(message);
                                String acceptanceMessage = "PORTALBALLACCEPTED "
                                        + oldClientPortalName;
                                clientQueues.get(oldClientName).add(
                                        acceptanceMessage);
                            } catch (Exception e) {
                            }
                        } else { // Otherwise, send a message back to the
                                 // original
                            // ball sender
                            String rejectionMessage = "PORTALBALLREJECTED "
                                    + oldClientPortalName;
                            clientQueues.get(oldClientName).add(
                                    rejectionMessage);
                        }
                    }
                }

            }
        } finally {
            if (verbose)
                System.out.println("Disconnection: " + clientName);
            // Disconnect client. Remove their queue, remove their connections,
            // and send messages to other clients about the disconnection.
            // Client disconnect message: DISCONNECT clientName side
            // Example: DISCONNECT Mars 0

            synchronized (this) {
                List<String> connectionsToRemove = new ArrayList<String>();
                for (String s : connections)
                    if (s.indexOf(clientName) != -1)
                        connectionsToRemove.add(s);

                for (String s : connectionsToRemove) {
                    String connectionType = s.split(" ")[0];
                    if (connectionType.equals("h")) {
                        String leftClient = s.split(" ")[1];
                        String rightClient = s.split(" ")[2];
                        String leftClientMessage = "DISCONNECT " + rightClient
                                + " 1";
                        String rightClientMessage = "DISCONNECT " + leftClient
                                + " 3";
                        try {
                            clientQueues.get(leftClient).put(leftClientMessage);
                            clientQueues.get(rightClient).put(
                                    rightClientMessage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (connectionType.equals("v")) {
                        String topClient = s.split(" ")[1];
                        String bottomClient = s.split(" ")[2];
                        String topClientMessage = "DISCONNECT " + bottomClient
                                + " 2";
                        String bottomClientMessage = "DISCONNECT " + topClient
                                + " 0";
                        try {
                            clientQueues.get(topClient).put(topClientMessage);
                            clientQueues.get(bottomClient).put(
                                    bottomClientMessage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    connections.remove(s);
                }
                clientQueues.remove(clientName);
                clientPortalNames.remove(clientName);

                out.close();
                in.close();
            }
        }
    }

    /**
     * Start a PingballServer using the given arguments. The only specified
     * argument is the port value, which is and integer in the range 0 to 65535,
     * inclusive, which represents the port where the server should listen for
     * incoming connections.
     * 
     * @param args
     *            arguments as described
     */
    public static void main(String[] args) {
        int port = DEFAULT_PORT;

        Queue<String> arguments = new LinkedList<String>(Arrays.asList(args));
        try {
            while (!arguments.isEmpty()) {
                String flag = arguments.remove();
                try {
                    if (flag.equals("--port")) {
                        port = Integer.parseInt(arguments.remove());
                        if (port < 0 || port > MAXIMUM_PORT) {
                            throw new IllegalArgumentException("port " + port
                                    + " out of range");
                        }
                    }
                } catch (NoSuchElementException nsee) {
                    throw new IllegalArgumentException("missing argument for "
                            + flag);
                } catch (NumberFormatException nfe) {
                    throw new IllegalArgumentException(
                            "unable to parse number for " + flag);
                }
            }
        } catch (IllegalArgumentException iae) {
            System.err.println(iae.getMessage());
            System.err.println("usage: PingballServer [--port PORT]");
            return;
        }

        try {
            runPingballServer(port);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    /**
     * Start a PingballServer running on the specified port
     * 
     * @param port
     *            The network port on which the server should listen.
     * @throws IOException
     *             if a network error occurs
     */
    public static void runPingballServer(int port) throws IOException {

        PingballServer server = new PingballServer(port);
        server.serve();
    }
}
