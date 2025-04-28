import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Payoff Matrices Explorer!");
        System.out.println("First, let's create your payoff matrix for Player 1" +
                "(P1) and Player 2 (P2).");
        System.out.println("Type the number of moves you want each player to have," +
                " either 2 or 3 (please do not add extra characters or spaces).");
        System.out.println("If an integer other than 2 or 3 is entered, the program is not" +
                "guaranteed to function correctly and may stop functioning.");
        System.out.println("Re-run if you accidentally type in the wrong integer.");
        while (!scanner.hasNextInt()) {
            System.out.println("Please enter a valid number");
            scanner.next();
        }
        int numMoves = scanner.nextInt();
        int[][] p1Payoffs = new int[numMoves][numMoves];
        int[][] p2Payoffs = new int[numMoves][numMoves];

        System.out.println("Now, you're going to input the integer payoff " +
                "values for P1 for the payoff matrix, from left to right, top to bottom.");
        for (int i = 0; i < numMoves; i++) {
            for (int j = 0; j < numMoves; j++) {
                System.out.println("Row " + (i + 1) + ", Column " + (j + 1) + ": ");
                while (!scanner.hasNextInt()) {
                    System.out.println("Please enter a valid number");
                    scanner.next();
                }
                p1Payoffs[i][j] = scanner.nextInt();
            }
        }

        System.out.println("Now, you're going to input the integer payoff " +
                "values for P2 for the payoff matrix, from left to right, top to bottom.");
        for (int k = 0; k < numMoves; k++) {
            for (int l = 0; l < numMoves; l++) {
                System.out.println("Row " + (k + 1) + ", Column " + (l + 1) + ": ");
                while (!scanner.hasNextInt()) {
                    System.out.println("Please enter a valid number");
                    scanner.next();
                }
                p2Payoffs[k][l] = scanner.nextInt();
            }
        }
        PayoffMatrix matrix = new PayoffMatrix(numMoves, p1Payoffs, p2Payoffs);

        System.out.println("Great! Now that you have your matrix, type in the number" +
                " corresponding to what you want. You can:");
        System.out.println("1. Find the pure strategy Nash Equilibria, if they exist.”");
        System.out.println("2. If you have a 2x2 matrix, find the mixed strategy" +
                " Nash Equilibrium.");
        System.out.println("3. Find a player's best response to the other player's specific move.");
        System.out.println("4. Edit your matrix and study to study the effects of your changes" +
                " after.");
        System.out.println("Type 'exit' to quit.\n");

        while (true) {

            System.out.print("Enter a number (or 'exit'): ");
            String input = scanner.nextLine();
            if (input.equals("exit")) {
                System.out.println("Thank you for visiting!");
                scanner.close();
                return;
            }


            if (input.equals("1")) {
                ArrayList<ArrayList<Integer>> pureEquilibria = matrix.findPureNashEquilibrium();
                if (pureEquilibria == null) {
                    System.out.println("No pure strategy Nash Equilibrium exists!");
                } else {
                    System.out.println("The following are pure strategy Nash Equilibrium: ");
                    for (ArrayList<Integer> square : pureEquilibria) {
                        int move1 = square.get(0);
                        int move2 = square.get(1);
                        System.out.println("(Move " + move1 + " for P1, Move "
                                + move2 + " for P2)");
                    }
                }
            } else if (input.equals("2")) {
                ArrayList<Double> mixedPQ = matrix.findMixedNashEquilibrium();
                if (mixedPQ == null) {
                    System.out.println("Matrix is either not 2x2, or no valid values" +
                            " for p and q exist!");
                } else {
                    System.out.println("For a mixed Nash Equilibrium: ");
                    double p = mixedPQ.get(0);
                    double q = mixedPQ.get(1);
                    System.out.println("p = " + p + "; q = " + q);
                }
            } else if (input.equals("3")) {
                System.out.println("Enter the player (1 or 2) you want to know their best response for.");
                while (!scanner.hasNextInt()) {
                    System.out.println("Please enter a valid number");
                    scanner.next();
                }
                int player = scanner.nextInt();
                System.out.println("Enter the move you want to know Player " + player
                        + "'s best response to (ex. 1).");
                while (!scanner.hasNextInt()) {
                    System.out.println("Please enter a valid number");
                    scanner.next();
                }
                int move = scanner.nextInt();

                Integer bestResponse = matrix.findBestResponse(move, player);
                if (bestResponse == null) {
                    System.out.println("Either player or move provided was invalid.");
                } else {
                    System.out.println("Player " + player + "'s best response to the other player's Move "
                            + move + " is: Move " + bestResponse);
                }
            } else if (input.equals("4")) {
                System.out.println("Type the number of moves you want each player to have," +
                        " either 2 or 3 (please do not add extra characters or spaces).");
                System.out.println("If an integer other than 2 or 3 is entered, the " +
                        "program is not guaranteed to function correctly and " +
                        " may stop functioning.");
                System.out.println("Re-run if you accidentally type in the wrong integer.");
                while (!scanner.hasNextInt()) {
                    System.out.println("Please enter a valid number");
                    scanner.next();
                }
                numMoves = scanner.nextInt();
                p1Payoffs = new int[numMoves][numMoves];
                p2Payoffs = new int[numMoves][numMoves];

                System.out.println("Now, you're going to input the integer payoff " +
                        "values for P1 for the payoff matrix, from left to right, top to bottom.");
                for (int i = 0; i < numMoves; i++) {
                    for (int j = 0; j < numMoves; j++) {
                        System.out.println("Row " + (i + 1) + ", Column " + (j + 1) + ": ");
                        while (!scanner.hasNextInt()) {
                            System.out.println("Please enter a valid number");
                            scanner.next();
                        }
                        p1Payoffs[i][j] = scanner.nextInt();
                    }
                }

                System.out.println("Now, you're going to input the integer payoff " +
                        "values for P2 for the payoff matrix, from left to right, top to bottom.");
                for (int k = 0; k < numMoves; k++) {
                    for (int l = 0; l < numMoves; l++) {
                        System.out.println("Row " + (k + 1) + ", Column " + (l + 1) + ": ");
                        while (!scanner.hasNextInt()) {
                            System.out.println("Please enter a valid number");
                            scanner.next();
                        }
                        p2Payoffs[k][l] = scanner.nextInt();
                    }
                }
                matrix = new PayoffMatrix(numMoves, p1Payoffs, p2Payoffs);
            }
        }
    }
}
