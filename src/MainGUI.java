import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class MainGUI {
    private JFrame frame;
    private JPanel mainPanel;
    private JTextArea outputArea;
    private JTextField inputField;
    private PayoffMatrix matrix;
    private int numMoves = 2; // default 2 moves
    private int[][] p1Payoffs;
    private int[][] p2Payoffs;
    private JPanel matrixPanel;
    private JPanel graphPanel;
    private JLabel graphImageLabel;
    private enum InputState { MAIN_MENU, WAITING_FOR_PLAYER, WAITING_FOR_MOVE }
    private InputState currentState = InputState.MAIN_MENU;
    private int currentPlayer;

    public MainGUI() {
        createGUI();
        showWelcomeMessage();
    }

    /** Sets up JComponents for the GUI. */
    private void createGUI() {
        frame = new JFrame("Payoff Matrices Explorer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        mainPanel = new JPanel(new BorderLayout());

        // wording text box
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // panel at bottom for user to input their numbers
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        inputField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processInput(inputField.getText());
                inputField.setText("");
            }
        });
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processInput(inputField.getText());
                inputField.setText("");
            }
        });

        inputPanel.add(new JLabel("Enter command:"), BorderLayout.WEST);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(submitButton, BorderLayout.EAST);

        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        // panel on the right to show the matrix
        matrixPanel = new JPanel();
        matrixPanel.setBorder(BorderFactory.createTitledBorder("Payoff Matrix"));
        matrixPanel.setPreferredSize(new Dimension(350, 350));
        mainPanel.add(matrixPanel, BorderLayout.EAST);

        // graph vis panel
        graphPanel = new JPanel(new BorderLayout());
        graphPanel.setBorder(BorderFactory.createTitledBorder("Social Network Graph"));
        graphImageLabel = new JLabel();
        graphImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        graphPanel.add(graphImageLabel, BorderLayout.CENTER);

        // get mat and graph panels together
        JPanel rightPanel = new JPanel(new GridLayout(2, 1));
        rightPanel.add(matrixPanel);
        rightPanel.add(graphPanel);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        frame.add(mainPanel);
        frame.setVisible(true);
    }


    /** Welcome message for player. */
    private void showWelcomeMessage() {
        outputArea.append("Welcome to the Payoff Matrices Explorer!\n");
        outputArea.append("First, let's create your payoff matrix for Player 1 (P1) and Player 2 (P2).\n");
        outputArea.append("Type the number of moves you want each player to have (2 or 3):\n");
    }

    /** Gets user input for setting up matrix then uses enum current state to determine how
     * to handle other input from the input box. */
    private void processInput(String input) {
        if (input.equalsIgnoreCase("exit")) {
            outputArea.append("Thank you for visiting!\n");
            frame.dispose();
            return;
        }

        if (matrix == null) {
            // when first setting up
            try {
                numMoves = Integer.parseInt(input);
                if (numMoves != 2 && numMoves != 3) {
                    outputArea.append("Please enter either 2 or 3 for the number of moves.\n");
                    return;
                }
                setupMatrix();
            } catch (NumberFormatException e) {
                outputArea.append("Please enter a valid number (2 or 3).\n");
            }
        } else {
            switch (currentState) {
                case MAIN_MENU:
                    handleMainMenuInput(input);
                    break;
                case WAITING_FOR_PLAYER:
                    handlePlayerInput(input);
                    break;
                case WAITING_FOR_MOVE:
                    handleMoveInput(input);
                    break;
            }
        }
    }

    /** When not expecting input for a specific move, handles input. */
    private void handleMainMenuInput(String input) {
        switch (input) {
            case "1":
                findPureNashEquilibrium();
                break;
            case "2":
                findMixedNashEquilibrium();
                break;
            case "3":
                outputArea.append("Enter the player (1 or 2) you want to know their best response for:\n");
                currentState = InputState.WAITING_FOR_PLAYER;
                break;
            case "4":
                if (matrix.getNumMoves() != 2) {
                    outputArea.append("Social network analysis is only available for 2x2 matrices.\n");
                } else {
                    analyzeSocialRelations();
                }
                break;
            case "5":
                resizeMatrix();
                break;
            default:
                outputArea.append("Invalid command. Please enter a number between 1-5 or 'exit'.\n");
        }
    }

    /** When expecting input for player for best move. */
    private void handlePlayerInput(String input) {
        try {
            int player = Integer.parseInt(input);
            if (player != 1 && player != 2) {
                outputArea.append("Please enter either 1 or 2 for the player.\n");
                return;
            }
            currentPlayer = player;
            outputArea.append("Enter the move you want to know Player " + player +
                    "'s best response to (1-" + numMoves + "):\n");
            currentState = InputState.WAITING_FOR_MOVE;
        } catch (NumberFormatException e) {
            outputArea.append("Please enter a valid number (1 or 2).\n");
        }
    }

    /** When expecting input for move for best move. */
    private void handleMoveInput(String input) {
        try {
            int move = Integer.parseInt(input);
            if (move < 1 || move > numMoves) {
                outputArea.append("Please enter a move between 1 and " + numMoves + ".\n");
                return;
            }

            Integer bestResponse = matrix.findBestResponse(move, currentPlayer);
            if (bestResponse == null) {
                outputArea.append("Invalid move provided.\n");
            } else {
                outputArea.append("Player " + currentPlayer + "'s best response to the other player's Move " +
                        move + " is: Move " + bestResponse + "\n");
            }
            currentState = InputState.MAIN_MENU;
            showOptions();
        } catch (NumberFormatException e) {
            outputArea.append("Please enter a valid move number.\n");
        }
    }

    /** Gets input and sets up matrix. */
    private void setupMatrix() {
        p1Payoffs = new int[numMoves][numMoves];
        p2Payoffs = new int[numMoves][numMoves];

        // input dialogs i love input dialogs
        for (int i = 0; i < numMoves; i++) {
            for (int j = 0; j < numMoves; j++) {
                String p1Value = JOptionPane.showInputDialog(frame,
                        "Enter P1's payoff for Row " + (i+1) + ", Column " + (j+1) + ":");
                try {
                    p1Payoffs[i][j] = Integer.parseInt(p1Value);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(frame, "Invalid input. Please enter an integer.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    j--;
                    continue;
                }

                String p2Value = JOptionPane.showInputDialog(frame,
                        "Enter P2's payoff for Row " + (i+1) + ", Column " + (j+1) + ":");
                try {
                    p2Payoffs[i][j] = Integer.parseInt(p2Value);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(frame, "Invalid input. Please enter an integer.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    j--;
                    continue;
                }
            }
        }

        matrix = new PayoffMatrix(numMoves, p1Payoffs, p2Payoffs);
        currentState = InputState.MAIN_MENU;
        updateMatrixDisplay();
        showOptions();
    }

    /** Updates payoff matrix display. */
    private void updateMatrixDisplay() {
        matrixPanel.removeAll();
        matrixPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(2, 2, 2, 2);

        // b-b-b border !
        Border cellBorder = BorderFactory.createLineBorder(Color.BLACK, 1);

        // player label
        gbc.gridx = 0;
        gbc.gridy = 0;
        JPanel cornerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        cornerPanel.setBorder(cellBorder);
        cornerPanel.setOpaque(true);
        cornerPanel.setBackground(new Color(240, 240, 240));

        JLabel p1Label = new JLabel("P1");
        p1Label.setForeground(Color.BLUE);
        p1Label.setFont(p1Label.getFont().deriveFont(Font.BOLD));

        JLabel slashLabel = new JLabel("\\");
        slashLabel.setForeground(Color.BLACK);

        JLabel p2Label = new JLabel("P2");
        p2Label.setForeground(Color.RED);
        p2Label.setFont(p2Label.getFont().deriveFont(Font.BOLD));

        cornerPanel.add(p1Label);
        cornerPanel.add(slashLabel);
        cornerPanel.add(p2Label);
        matrixPanel.add(cornerPanel, gbc);

        // headers
        for (int j = 0; j < numMoves; j++) {
            gbc.gridx = j + 1;
            gbc.gridy = 0;
            JLabel colHeader = new JLabel("Move " + (j + 1), SwingConstants.CENTER);
            colHeader.setBorder(cellBorder);
            colHeader.setOpaque(true);
            colHeader.setBackground(new Color(240, 240, 240));
            matrixPanel.add(colHeader, gbc);
        }

        // p1 headers and payoffs
        for (int i = 0; i < numMoves; i++) {
            // Row header
            gbc.gridx = 0;
            gbc.gridy = i + 1;
            JLabel rowHeader = new JLabel("Move " + (i + 1), SwingConstants.CENTER);
            rowHeader.setBorder(cellBorder);
            rowHeader.setOpaque(true);
            rowHeader.setBackground(new Color(240, 240, 240));
            matrixPanel.add(rowHeader, gbc);

            // payoffs
            for (int j = 0; j < numMoves; j++) {
                gbc.gridx = j + 1;
                gbc.gridy = i + 1;

                JPanel cellPanel = new JPanel(new GridLayout(2, 1));
                cellPanel.setBorder(cellBorder);
                cellPanel.setBackground(Color.WHITE);

                JLabel p1temp = new JLabel(" " + p1Payoffs[i][j], SwingConstants.CENTER);
                p1temp.setFont(p1Label.getFont().deriveFont(Font.BOLD));
                p1temp.setForeground(Color.BLUE);

                JLabel p2temp = new JLabel(" " + p2Payoffs[i][j], SwingConstants.CENTER);
                p2temp.setFont(p2temp.getFont().deriveFont(Font.BOLD));
                p2temp.setForeground(Color.RED);

                cellPanel.add(p1temp);
                cellPanel.add(p2temp);
                matrixPanel.add(cellPanel, gbc);
            }
        }

        matrixPanel.revalidate();
        matrixPanel.repaint();
    }

    /** Outputs options and toggles state to be main menu. */
    private void showOptions() {
        outputArea.append("\n\nWhat would you like to do?\n");
        outputArea.append("1. Find the pure strategy Nash Equilibria, if they exist.\n");
        outputArea.append("2. If you have a 2x2 matrix, find the mixed strategy Nash Equilibrium.\n");
        outputArea.append("3. Find a player's best response to the other player's specific move.\n");
        outputArea.append("4. For a 2x2 matrix, see how the pure-strategy Nash Equilibria translate to social networks.\n");
        outputArea.append("5. Toggle between 2x2 and 3x3 matrix size.\n");
        outputArea.append("Type 'exit' to quit.\n");
        currentState = InputState.MAIN_MENU;
    }

    /** Finds and displays nash equilibrium using PayoffMatrix class. */
    private void findPureNashEquilibrium() {
        ArrayList<ArrayList<Integer>> pureEquilib = matrix.findPureNashEquilibrium();
        if (pureEquilib == null) {
            outputArea.append("\nNo pure strategy Nash Equilibrium exists! Try changing the payoff values.\n");
        } else {
            outputArea.append("\nThe following are pure strategy Nash Equilibrium:\n");
            for (ArrayList<Integer> square : pureEquilib) {
                int move1 = square.get(0);
                int move2 = square.get(1);
                outputArea.append("(Move " + move1 + " for P1, Move " + move2 + " for P2)\n");
            }
        }
    }

    /** Finds mixed strat equilibrium if it exists. */
    private void findMixedNashEquilibrium() {
        ArrayList<Double> mixedPQ = matrix.findMixedNashEquilibrium();
        if (mixedPQ == null) {
            outputArea.append("Matrix is either not 2x2, or no valid values for p and q exist!\n");
        } else {
            outputArea.append("For a mixed Nash Equilibrium:\n");
            double p = mixedPQ.get(0);
            double q = mixedPQ.get(1);
            outputArea.append("p = " + p + "; q = " + q + "\n");
        }
    }


    /** Resizes matrix to allow toggle between 2x2 and 3x3. */
    private void resizeMatrix() {
        if (numMoves == 2) {
            // go from 2 moves to 3
            int[][] newP1Payoffs = new int[3][3];
            int[][] newP2Payoffs = new int[3][3];

            // copy 2x2 values
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    newP1Payoffs[i][j] = p1Payoffs[i][j];
                    newP2Payoffs[i][j] = p2Payoffs[i][j];
                }
            }

            // new payoff values for new move
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (i == 2 || j == 2) {
                        String p1Value = JOptionPane.showInputDialog(frame,
                                "Enter P1's payoff for Row " + (i+1) + ", Column " + (j+1) + ":");
                        try {
                            newP1Payoffs[i][j] = Integer.parseInt(p1Value);
                        } catch (NumberFormatException e) {
                            JOptionPane.showMessageDialog(frame, "Invalid input. Using 0 as default.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            newP1Payoffs[i][j] = 0;
                        }

                        String p2Value = JOptionPane.showInputDialog(frame,
                                "Enter P2's payoff for Row " + (i+1) + ", Column " + (j+1) + ":");
                        try {
                            newP2Payoffs[i][j] = Integer.parseInt(p2Value);
                        } catch (NumberFormatException e) {
                            JOptionPane.showMessageDialog(frame, "Invalid input. Using 0 as default.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            newP2Payoffs[i][j] = 0;
                        }
                    }
                }
                currentState = InputState.MAIN_MENU;
            }

            p1Payoffs = newP1Payoffs;
            p2Payoffs = newP2Payoffs;
            numMoves = 3;
        } else {
            // going from 3 to 2 moves
            int[][] newP1Payoffs = new int[2][2];
            int[][] newP2Payoffs = new int[2][2];

            // keep only the mini 2x2
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    newP1Payoffs[i][j] = p1Payoffs[i][j];
                    newP2Payoffs[i][j] = p2Payoffs[i][j];
                }
            }

            p1Payoffs = newP1Payoffs;
            p2Payoffs = newP2Payoffs;
            numMoves = 2;
        }

        // new mat object so everything stays updated
        matrix = new PayoffMatrix(numMoves, p1Payoffs, p2Payoffs);
        updateMatrixDisplay();
        outputArea.append("Matrix has been resized to " + numMoves + "x" + numMoves + "\n");
        showOptions();
    }

    /** Looks through payoffs and determines relationships for graph */
    private void analyzeSocialRelations() {
        // to hold whether or not a move "helps"
        boolean[][] helps = new boolean[2][2]; // helps[from][to]

        // check payoffs to determine harming
        for (int p1Move = 0; p1Move < numMoves; p1Move++) {
            for (int p2Move = 0; p2Move < numMoves; p2Move++) {
                // p1 on p2
                if (p2Payoffs[p1Move][p2Move] > getAverageP2Payoff(p2Move)) {
                    helps[0][1] = true;
                }

                // p2 on p1
                if (p1Payoffs[p1Move][p2Move] > getAverageP1Payoff(p1Move)) {
                    helps[1][0] = true;
                }
            }
        }

        displaySocialGraph(helps);
    }

    /** Displays social graph based on payoffs calculated above.*/
    private void displaySocialGraph(boolean[][] helps) {
        // simple text graph
        String graphText = "<html><pre>";
        graphText += "Player 1";
        if (helps[0][1]) graphText += " → ";
        else graphText += " × ";
        graphText += "Player 2<br>";

        graphText += "Player 2";
        if (helps[1][0]) graphText += " → ";
        else graphText += " × ";
        graphText += "Player 1</pre></html>";

        graphImageLabel.setText(graphText);
    }

    private double getAverageP1Payoff(int move) {
        double sum = 0;
        for (int j = 0; j < numMoves; j++) {
            sum += p1Payoffs[move][j];
        }
        return sum / numMoves;
    }

    private double getAverageP2Payoff(int move) {
        double sum = 0;
        for (int i = 0; i < numMoves; i++) {
            sum += p2Payoffs[i][move];
        }
        return sum / numMoves;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainGUI();
            }
        });
    }
}
