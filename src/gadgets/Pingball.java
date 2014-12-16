package gadgets;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import parser.BoardBuilder;

/**
 * Pingball class that instantiates the main Pingball game. Provided as
 * hardcoded examples are different boards.
 * 
 * Abstraction Function: Represents the full GUI and all parts of the game.
 * 
 * Representation: Using a combination of Swing elements, Graphics2D, Gadgets,
 * and other related Java classes, Pingball accomplishes the construction of a
 * full interactive GUI.
 * 
 * Rep Invariant: All instance variable are not null.
 * 
 * Thread-Safety Argument: There are two threads in Pingball, the main thread
 * listening to key presses and a GUI event dispatch thread. The data from key
 * presses is not shared, and each click is processed on the fly and then
 * discarded. There is no central pool of key click data kept. The action
 * listeners are only called in the event dispatch thread, as verified below. No
 * other data is shared between threads. Thus, the system is thread safe.
 * 
 */
public class Pingball extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_PORT = 10987;
    private static final int MAXIMUM_PORT = 65535;

    private static final int FRAME_WIDTH = 600; // 600
    private static final int FRAME_HEIGHT = FRAME_WIDTH * 24 / 20;
    private static final double SCALE_FACTOR = 60.0 * FRAME_WIDTH / 1600;

    private File boardFile;
    private Board board;
    private JLabel currentServerLabel = new JLabel();

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        for (int i = 0; i < board.gadgets.size(); i++)
            board.gadgets.get(i).drawOnFrame(g2d, SCALE_FACTOR);

        for (Ball b : board.balls)
            b.drawOnFrame(g2d, SCALE_FACTOR);
    }

    /**
     * Main Pingball constructor. Creates the JFrame and JPanel to hold all
     * board elements and GUI elements, while, on start-up, parses any relevant
     * board information, handles the connection to any server/port upon the
     * appropriate button clicks, and handles any changes to the GUI, such as
     * exiting, restarting, dis/connecting, and pause/resumes.
     * 
     * @param clientServer
     *            boolean indicating if client-server play is enabled; this is
     *            set in the main method when parsing optional command line
     *            arguments.
     * @param host
     *            String indicating name of server host
     * @param filename
     *            String indicating name of file to be parsed and to be used in
     *            creation of the interactable board
     * @param port
     *            int indicating number of port to connect to
     * @throws Exception
     *             Exception thrown if file can't be parsed
     */
    public Pingball(boolean clientServer, String host, String filename, int port)
            throws Exception {
        boardFile = new File(filename);
        if (!clientServer || host == null) {
            if (filename != null) {
                board = BoardBuilder.constructBoard(boardFile);
            } else {
                throw new IllegalArgumentException("unable to parse file");
            }
        } else {
            board = BoardBuilder.constructBoard(boardFile);
            board.setHostAndPort(host, port);
        }

        // Main JFrame to store/hold all information about the game
        JFrame frame = new JFrame("Pingball");
        // frame.add(this);

        JPanel main = new JPanel();
        GroupLayout layout = new GroupLayout(main);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        main.setLayout(layout);

        // Keyboard Inputs

        KeyListener listener = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                handleKeyDown(e.getKeyCode());
            }

            public void keyReleased(KeyEvent e) {
                handleKeyUp(e.getKeyCode());
            }

            @SuppressWarnings("unused")
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
            }

            private void handleKeyUp(int keyCode) {
                HashSet<String> names = Board.KEYUP_MAPPINGS.get(keyCode);
                System.out.println("handleKeyUp names: " + names);
                if (names != null) {
                    for (String s : names) {
                        for (Gadget g : board.getListOfGadgets()) {
                            if (s.equals(g.getName())) {
                                g.doAction();
                            }
                        }
                    }
                }
            }

            private void handleKeyDown(int keyCode) {
                HashSet<String> names = Board.KEYDOWN_MAPPINGS.get(keyCode);
                System.out.println("handleKeyDown names: " + names);
                if (names != null) {
                    for (String s : names) {
                        for (Gadget g : board.getListOfGadgets()) {
                            if (s.equals(g.getName())) {
                                g.doAction();
                            }
                        }
                    }
                }
            }
        };

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                requestFocusInWindow();
            }

            public void mouseClicked(MouseEvent me) {
                requestFocusInWindow();
            }

        });

        // GUI components
        // add event dispatch

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                JFileChooser fc = new JFileChooser();
                fc.setCurrentDirectory(new File("boards"));

                JButton loadButton = new JButton();
                loadButton.setName("loadButton");
                loadButton.setText("Load board from file");
                loadButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        @SuppressWarnings("unused")
                        int returnVal = fc.showOpenDialog(loadButton);
                        boardFile = fc.getSelectedFile();

                        try {
                            boolean serverPlay = board.serverPlay;
                            String host = board.currentHost;
                            int port = board.currentPort;
                            if (serverPlay)
                                board.disconnectFromServer();
                            board = BoardBuilder.constructBoard(boardFile);
                            if (serverPlay) {
                                board.setHostAndPort(host, port);
                                currentServerLabel.setText("Connected to "
                                        + host + "::" + port);
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                });

                JButton pauseButton = new JButton();
                pauseButton.setName("pauseButton");
                pauseButton.setText("Pause");
                pauseButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (!board.paused) {
                            pauseButton.setText("Resume");
                            board.paused = true;
                        } else {
                            pauseButton.setText("Pause");
                            board.paused = false;
                        }
                    }
                });

                JButton restartButton = new JButton();
                restartButton.setName("restartButton");
                restartButton.setText("Restart");
                restartButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            boolean serverPlay = board.serverPlay;
                            String host = board.currentHost;
                            int port = board.currentPort;
                            if (serverPlay)
                                board.disconnectFromServer();
                            board = BoardBuilder.constructBoard(boardFile);
                            if (serverPlay) {
                                board.setHostAndPort(host, port);
                                currentServerLabel.setText("Connected to "
                                        + host + "::" + port);
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                });

                JButton exitButton = new JButton();
                exitButton.setName("exitButton");
                exitButton.setText("Exit");
                exitButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        frame.dispatchEvent(new WindowEvent(frame,
                                WindowEvent.WINDOW_CLOSING));
                    }
                });

                // While we attach an action listener, neither action listener
                // actually does anything.
                JLabel hostnameLabel = new JLabel();
                hostnameLabel.setName("hostnameLabel");
                hostnameLabel.setText("Enter new hostname:");

                JTextField newHostnameField = new JTextField();
                newHostnameField.setName("newHostnameField");
                newHostnameField.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                    }
                });

                JLabel portLabel = new JLabel();
                portLabel.setName("portLabel");
                portLabel.setText("Enter new port:");

                JTextField newPortField = new JTextField();
                newPortField.setName("newPortField");
                newPortField.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                    }
                });

                JButton connectToServerButton = new JButton();
                connectToServerButton.setName("connectToServerButton");
                connectToServerButton.setText("Connect to server!");
                connectToServerButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (!newHostnameField.getText().equals("")
                                && !newPortField.getText().equals("")) {
                            String newHostname = newHostnameField.getText();
                            int newPort = Integer.parseInt(newPortField
                                    .getText());
                            if (board.isServerPlay()) {
                                board.disconnectFromServer();
                            }
                            boolean connected = board.setHostAndPort(
                                    newHostname, newPort);
                            if (connected)
                                currentServerLabel.setText("Connected to "
                                        + newHostname + "::" + newPort);
                            else
                                currentServerLabel.setText("");
                        }
                    }
                });

                JButton disconnectFromServerButton = new JButton();
                disconnectFromServerButton
                        .setName("disconnectFromServerButton");
                disconnectFromServerButton.setText("Disconnect from server!");
                disconnectFromServerButton
                        .addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                board.disconnectFromServer();
                                currentServerLabel.setText("");
                            }
                        });

                currentServerLabel.setName("currentServerLabel");

                // setting GroupLayout
                layout.setHorizontalGroup(layout
                        .createParallelGroup()
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addGroup(
                                                layout.createParallelGroup(
                                                        GroupLayout.Alignment.LEADING)
                                                        .addComponent(
                                                                loadButton)
                                                        .addComponent(
                                                                hostnameLabel)
                                                        .addComponent(
                                                                disconnectFromServerButton))
                                        .addGroup(
                                                layout.createParallelGroup(
                                                        GroupLayout.Alignment.LEADING)
                                                        .addComponent(
                                                                pauseButton)
                                                        .addComponent(
                                                                newHostnameField))
                                        .addGroup(
                                                layout.createParallelGroup(
                                                        GroupLayout.Alignment.LEADING)
                                                        // .addComponent(resumeButton)
                                                        .addComponent(portLabel)
                                                        .addComponent(
                                                                currentServerLabel))
                                        .addGroup(
                                                layout.createParallelGroup(
                                                        GroupLayout.Alignment.LEADING)
                                                        .addComponent(
                                                                restartButton)
                                                        .addComponent(
                                                                newPortField))
                                        .addGroup(
                                                layout.createParallelGroup(
                                                        GroupLayout.Alignment.LEADING)
                                                        .addComponent(
                                                                exitButton)
                                                        .addComponent(
                                                                connectToServerButton)))
                        .addComponent(Pingball.this));

                layout.setVerticalGroup(layout
                        .createSequentialGroup()
                        .addGroup(
                                layout.createParallelGroup(
                                        GroupLayout.Alignment.BASELINE)
                                        .addComponent(loadButton)
                                        .addComponent(pauseButton)
                                        // .addComponent(resumeButton)
                                        .addComponent(restartButton)
                                        .addComponent(exitButton))
                        .addGroup(
                                layout.createParallelGroup(
                                        GroupLayout.Alignment.BASELINE)
                                        .addComponent(hostnameLabel)
                                        .addComponent(newHostnameField)
                                        .addComponent(portLabel)
                                        .addComponent(newPortField)
                                        .addComponent(connectToServerButton))
                        .addGroup(
                                layout.createParallelGroup(
                                        GroupLayout.Alignment.BASELINE)
                                        .addComponent(
                                                disconnectFromServerButton)
                                        .addComponent(currentServerLabel))

                        .addComponent(Pingball.this));
                System.out.println("Is event dispatch thread? - "
                        + javax.swing.SwingUtilities.isEventDispatchThread());

                // frame.addKeyListener(listener);
                frame.add(main);
                frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
                frame.setVisible(true);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
        });
        this.addKeyListener(listener);
        this.setFocusable(true);
        this.requestFocus();
        checkRep();
        run();
    }

    /**
     * Checks that all instance variables are not null.
     */
    private void checkRep() {
        assert boardFile != null;
        assert board != null;
        assert currentServerLabel != null;
    }

    /**
     * Start the board animations.
     */
    private void run() {
        // Animation
        long oldTimeMillis = System.currentTimeMillis();
        double FRAMERATE = 50;
        while (true) {
            board.moveForwardFrame();
            // Paint to GUI
            repaint();

            // Make sure the framerate is 20fps
            while (System.currentTimeMillis() - oldTimeMillis < 1000 / FRAMERATE
                    || board.paused) {
            }
            oldTimeMillis = System.currentTimeMillis();
        }
    }

    /**
     * Main method to run Pingball. Parses the optional command line arguments,
     * and after takign into account the existence of them or lack thereof,
     * creates the Pingball instance and runs the game.
     * 
     * @param args
     *            arguments as specified in Pingball phase 2 specifications,
     *            including file, host, and port.
     * 
     * @throws Exception
     *             if file cannot be found, or if server connection cannot be
     *             made.
     */
    public static void main(String[] args) throws Exception {
        boolean clientServer;
        String host = null;
        int port = DEFAULT_PORT;
        String filename = null;

        Queue<String> arguments = new LinkedList<String>(Arrays.asList(args));
        if (arguments.size() == 0) {
            clientServer = false;
            filename = "boards/blank.pb";
        } else if (arguments.size() == 1) { // Single-machine play
            clientServer = false;
            while (!arguments.isEmpty())
                filename = arguments.remove();
        } else { // Client-server play
            clientServer = true;
            try {
                while (!arguments.isEmpty()) {
                    String flag = arguments.remove();
                    try {
                        if (flag.equals("--host"))
                            host = arguments.remove();
                        else if (flag.equals("--port")) {
                            port = Integer.parseInt(arguments.remove());
                            if (port < 0 || port > MAXIMUM_PORT) {
                                throw new IllegalArgumentException("port "
                                        + port + " out of range");
                            }
                        } else {
                            filename = flag;
                        }
                    } catch (NoSuchElementException nsee) {
                        throw new IllegalArgumentException(
                                "missing argument for " + flag);
                    } catch (NumberFormatException nfe) {
                        throw new IllegalArgumentException(
                                "unable to parse number for " + flag);
                    }
                }
            } catch (IllegalArgumentException iae) {
                System.err.println(iae.getMessage());
                System.err
                        .println("usage: (single machine play) Pingball [FILE]\n"
                                + "(client-server mode) Pingball [--host HOST] [--port PORT] FILE");
                return;
            }
        }
        Pingball pingballGame = new Pingball(clientServer, host, filename, port);
        pingballGame.run();
    }

}
