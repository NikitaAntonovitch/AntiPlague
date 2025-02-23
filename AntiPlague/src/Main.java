import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

// *_vw - View
// *_mod - Model
// *_ctrl - Controller

public class Main {
    private static void showLeadersDialog(JDialog parent, HashMap<String, Long> scoreMap) { // function for compare list of objects
        // Create a DefaultListModel to hold the data for the JList
        DefaultListModel<String> listModel = new DefaultListModel<>();

        // Process the scoreMap to populate the listModel
        AtomicInteger rank = new AtomicInteger(1); // Counter for player rank
        scoreMap.entrySet().stream() // Get all entries from the map
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed()) // Sort by score in descending order
                .limit(20) // Limit to the top 20 entries
                .forEach(entry -> {
                    // Format the data into a single string, including the rank
                    String rowData = String.format("%d.  %s -> %d", rank.getAndIncrement(), entry.getKey(), entry.getValue());
                    // Add the formatted string to the listModel
                    listModel.addElement(rowData);
                });

        // Create a JList with the populated listModel
        JList<String> list = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(300, 200)); // Set preferred size for the scroll pane

        // Create a JDialog to display the JList
        JDialog dialog = new JDialog(parent, "Leaders", true);
        dialog.getContentPane().add(scrollPane); // Add the scroll pane to the dialog
        dialog.pack(); // Pack the dialog to fit its contents
        dialog.setLocationRelativeTo(parent); // Center the dialog relative to the parent frame
        dialog.setVisible(true); // Make the dialog visible
    }

    static JDialog getDialog() {
        JDialog jDialog = new JDialog();
        jDialog.setTitle("Please select options"); // set dialog for the start dialog in the game
        jDialog.setSize(500, 500); // set size for dialog
        jDialog.setLocationRelativeTo(null); // set dialog position on the center of the screen
        jDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); // set close function
        return jDialog;
    }

    public static void main(String[] args) { // class main
        JDialog jDialog = getDialog();

        JPanel jPanel = new JPanel();
        jDialog.add(jPanel);

        jPanel.setLayout(new GridLayout(3,1));
        JButton bGame = new JButton("New Game");
        Font buttonFont = new Font("Serif", Font.PLAIN, 19); // Font name, style, size
        bGame.setFont(buttonFont);
        bGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // difficulty levels
                JDialog jDialogLevels = new JDialog();
                jDialogLevels.setResizable(false);
                jDialogLevels.setVisible(true);
                jDialogLevels.setSize(150, 300);
                jDialogLevels.setLocationRelativeTo(null);
                JPanel panelDialog = new JPanel();
                panelDialog.setLayout(new GridLayout(3, 1));
                jDialogLevels.add(panelDialog);
                JButton level1 = new JButton("level 1");
                Font fLevel1 = new Font("Serif", Font.PLAIN, 19); // Font name, style, size
                level1.setFont(fLevel1);
                level1.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        SwingUtilities.invokeLater(() -> { //  is a method in Java used to ensure that a piece of code runs on the Event Dispatch Thread (EDT), which is the thread responsible for handling GUI updates and events in Swing applications.
                            GameWindow_ctrl gameWindow = new GameWindow_ctrl(1);
                            jDialogLevels.dispose();
                            gameWindow.setVisible(true);
                        });
                    }
                });

                JButton level2 = new JButton("level 2");
                Font fLevel2 = new Font("Serif", Font.PLAIN, 19); // Font name, style, size
                level2.setFont(fLevel2);
                level2.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        GameWindow_ctrl gameWindow = new GameWindow_ctrl(2);
                        jDialogLevels.dispose();
                        gameWindow.setVisible(true);
                    }
                });

                JButton level3 = new JButton("level 3");
                Font fLevel3 = new Font("Serif", Font.PLAIN, 19); // Font name, style, size
                level3.setFont(fLevel3);
                level3.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        GameWindow_ctrl gameWindow = new GameWindow_ctrl(3);
                        jDialogLevels.dispose();
                        gameWindow.setVisible(true);
                    }
                });
                panelDialog.add(level1);
                panelDialog.add(level2);
                panelDialog.add(level3);
            }
        });

        JButton bScore = new JButton("High Scores");
        Font fScore = new Font("Serif", Font.PLAIN, 19); // Font name, style, size
        bScore.setFont(fScore);
        bScore.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HashMap <String, Long> mapScore = new HashMap<>();
                Serialize_mod serialize_mod = new Serialize_mod();
                mapScore = serialize_mod.deserializeMapModel("Top20/score.ser");
                // System.out.println("deserialized map"); // debug
                showLeadersDialog(jDialog, mapScore);
            }
        });

        JButton bExit = new JButton("Exit");
        Font fExit = new Font("Serif", Font.PLAIN, 19); // Font name, style, size
        bExit.setFont(fExit);
        bExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        jPanel.add(bGame);
        jPanel.add(bScore);
        jPanel.add(bExit);
        jDialog.setVisible(true);
    }
}