package pingball.server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 * Additional feature for Pingball Phase 3 - ServerGUI allowing for connection
 * of boards in more user friendly way.
 * 
 * Abstraction Function: Represents a GUI that makes connecting boards much
 * easier and avoids the tedious entering of commands into the console.
 * 
 * Representation: Using a collection of Swing elements and a number of
 * associated Java classes, this is represented as GUI with buttons to denote
 * each action.
 * 
 * Rep Invariant: We want to ensure that the server and other JFrame elements
 * are not null. 
 * 
 * Thread Safety Argument: There are three Threads in ServerGUI: one that runs
 * the GUI (the main thread), another that updates the table with list of active
 * boards and another that runs an instance of PingballServer. A thread-safe
 * concurrent hash map is used for boardNames, which is updated by information
 * from the server thread. Other relevant data is confined to its respective
 * thread. PingballServer creates a number of threads described in
 * PingballServer.java, and none of those threads share any data with the GUI
 * thread or table update thread beyond which is described above. The GUI events
 * are held in the event dispatch main thread, as verified below.
 * 
 */
public class ServerGUI extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L; // required by Serializable

    // components to use in the GUI
    private final JButton newConnectionButton;
    private final JLabel guiHeading;
    private final JTable board1ConnectionTable;
    private final JTable board2ConnectionTable;
    private final JScrollPane scrollPane;
    private final JScrollPane scrollPane2;

    // new Jradiobuttons to select horizontal 'h' or vertical 'v' connections.
    private final JRadioButton horizontal;
    private final JRadioButton vertical;

    private final PingballServer server;

    private int dataIndex = 0;

    private Map<String, Integer> boardNames = new ConcurrentHashMap<>();
    private Thread runServerThread = null;
    private Thread updateTableThread = null;

    private boolean horRadioButtonState = false;
    private boolean verRadioButtonState = false;

    private String selectedBoard1 = "";
    private String selectedBoard2 = "";

    /**
     * Constructs a new GUI for a connecting boards together.
     * 
     * @param port
     *            int representing the port that the server is listening to
     * 
     * @throws IOException
     */
    public ServerGUI(int port) throws IOException {
        // Verify this is the event-dispatch thread
        System.out.println("Is event dispatch thread? - "
                + javax.swing.SwingUtilities.isEventDispatchThread());

        server = new PingballServer(port);
        runServer(server);

        horizontal = new JRadioButton("Horizontal");
        horizontal.setName("horizontal");
        horizontal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                horRadioButtonState = true;
                verRadioButtonState = false;
            }
        });

        vertical = new JRadioButton("Vertical");
        vertical.setName("vertical");
        vertical.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verRadioButtonState = true;
                horRadioButtonState = false;
            }
        });

        newConnectionButton = new JButton();
        newConnectionButton.setName("newPuzzleButton");
        newConnectionButton.setText("Connect!");
        newConnectionButton.addActionListener(this);

        guiHeading = new JLabel();
        guiHeading.setName("GUIheading");
        guiHeading.setText("Server GUI: Connect Boards");

        // tables to list active boards
        board1ConnectionTable = new JTable(new DefaultTableModel(
                new Object[] { "Board 1 Name" }, 0));
        board1ConnectionTable.setName("board1ConnectionTable");

        board1ConnectionTable.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent event) {
                        selectedBoard1 = (String) board1ConnectionTable
                                .getValueAt(
                                        board1ConnectionTable.getSelectedRow(),
                                        0);
                        System.out
                                .println("board1selected = " + selectedBoard1);
                    }
                });

        updateTable(board1ConnectionTable);

        board2ConnectionTable = new JTable(new DefaultTableModel(
                new Object[] { "Board 2 Name" }, 0));
        board2ConnectionTable.setName("board2ConnectionTable");

        board2ConnectionTable.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent event) {
                        selectedBoard2 = (String) board2ConnectionTable
                                .getValueAt(
                                        board2ConnectionTable.getSelectedRow(),
                                        0);
                        System.out
                                .println("board2selected = " + selectedBoard2);
                    }
                });

        scrollPane = new JScrollPane(board1ConnectionTable);
        scrollPane2 = new JScrollPane(board2ConnectionTable);

        ButtonGroup group = new ButtonGroup();
        group.add(horizontal);
        group.add(vertical);

        // Defining the GroupLayouts
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createParallelGroup().addGroup(
                layout.createSequentialGroup()
                        .addGroup(
                                layout.createParallelGroup(
                                        GroupLayout.Alignment.LEADING)
                                        .addComponent(guiHeading, 100, 100,
                                                Short.MAX_VALUE)
                                        .addComponent(horizontal)
                                        .addComponent(scrollPane, 100, 100,
                                                Short.MAX_VALUE))
                        .addGroup(
                                layout.createParallelGroup(
                                        GroupLayout.Alignment.LEADING)
                                        .addComponent(newConnectionButton)
                                        .addComponent(vertical)
                                        .addComponent(scrollPane2, 100, 100,
                                                Short.MAX_VALUE))));

        layout.setVerticalGroup(layout
                .createSequentialGroup()
                .addGroup(
                        layout.createParallelGroup(
                                GroupLayout.Alignment.BASELINE)
                                .addComponent(guiHeading)
                                .addComponent(newConnectionButton))
                .addGroup(
                        layout.createParallelGroup(
                                GroupLayout.Alignment.BASELINE)
                                .addComponent(horizontal)
                                .addComponent(vertical))
                .addGroup(
                        layout.createParallelGroup(
                                GroupLayout.Alignment.BASELINE)
                                .addComponent(scrollPane, 100, 100,
                                        Short.MAX_VALUE)
                                .addComponent(scrollPane2, 100, 100,
                                        Short.MAX_VALUE)));
        checkRep();
    }
    
    /**
     * Checks the rep invariant and non-nullness of all elements. 
     */
    private void checkRep() {
        assert newConnectionButton != null;
        assert guiHeading != null;
        assert board1ConnectionTable != null;
        assert board2ConnectionTable != null;
        assert scrollPane != null;
        assert scrollPane2 != null;
        assert server != null;
    }
    
    /**
     * Updates a table with the list of available boards.
     * 
     * @param JTable
     *            table to update.
     */
    private void updateTable(JTable table) {
        updateTableThread = new Thread(new Runnable() {
            public void run() {
                try {
                    while (true) {
                        Map<String, BlockingQueue<String>> boardMap = server
                                .returnBoards();
                        // System.out.println("boardList: " +
                        // boardMap.toString());

                        // get the boards and add them to the list of available
                        // boards in the serverGUI

                        for (String boardName : boardMap.keySet()) {
                            // add boards.
                            if ((boardNames.isEmpty())
                                    || (!(boardNames.containsKey(boardName)))) {
                                boardNames.put(boardName, dataIndex);

                                DefaultTableModel board1TableModel = (DefaultTableModel) board1ConnectionTable
                                        .getModel();
                                board1TableModel
                                        .addRow(new Object[] { boardName });

                                DefaultTableModel board2TableModel = (DefaultTableModel) board2ConnectionTable
                                        .getModel();
                                board2TableModel
                                        .addRow(new Object[] { boardName });

                                dataIndex += 1; // remove
                            }
                        }

                        // check if boards have been removed.
                        if (boardMap.keySet().size() < boardNames.size()) {
                            List<String> boardsToRemove = new ArrayList<>();

                            for (String boardName : boardNames.keySet()) {
                                if (!(boardMap.keySet().contains(boardName))) {
                                    DefaultTableModel board1TableModel = (DefaultTableModel) board1ConnectionTable
                                            .getModel();
                                    // board1TableModel.removeRow(boardNames.get(boardName));
                                    board1TableModel.setValueAt("",
                                            boardNames.get(boardName), 0);

                                    DefaultTableModel board2TableModel = (DefaultTableModel) board2ConnectionTable
                                            .getModel();
                                    // board2TableModel.removeRow(boardNames.get(boardName));
                                    board2TableModel.setValueAt("",
                                            boardNames.get(boardName), 0);

                                    boardsToRemove.add(boardName);
                                }
                            }

                            for (String board : boardsToRemove) {
                                boardNames.remove(board);
                            }

                        }

                    }
                } finally {
                }
            }
        });
        // start Thread
        updateTableThread.start();
    }

    /**
     * Runs an instance of PingballServer.
     * 
     * @param server
     *            instance of PingballServer
     */
    private void runServer(PingballServer server) {
        runServerThread = new Thread(new Runnable() {
            public void run() {
                try {
                    server.serve();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                }
            }
        });
        // start Thread
        runServerThread.start();
    }

    /**
     * Start the GUI client.
     * 
     * @param args
     *            arguments used in the command-line interface
     */
    public static void main(final String[] args) {
        String[] arguments = args;
        int portNumber;

        if (arguments.length == 0) {
            portNumber = 10987; // default port number.
        } else {
            portNumber = Integer.parseInt(arguments[0]);
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ServerGUI main;
                try {
                    main = new ServerGUI(portNumber);
                    main.pack(); // show all
                    main.setVisible(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // System.out.println("hor button: " + horRadioButtonState +
        // "; ver button: " + verRadioButtonState);
        String connectionType;
        if (horRadioButtonState) {
            connectionType = "h";
        } else {
            connectionType = "v";
        }

        String commandToServer = connectionType + " " + selectedBoard1 + " "
                + selectedBoard2;
        server.parseSystemInCommand(commandToServer);
        System.out.println(commandToServer);

    }

}
