import java.util.*;

public class PayoffMatrix {
    private int numMoves;
    private int[][] p1Payoffs;
    private int[][] p2Payoffs;

    /**
     * Creates a 2-player payoff matrix with either 2 or 3 moves
     * for Player 1 (P1) and Player 2 (P2)
     */
    public PayoffMatrix(int numMoves, int[][] p1Payoffs, int[][] p2Payoffs) {
        this.numMoves = numMoves;
        this.p1Payoffs = p1Payoffs;
        this.p2Payoffs = p2Payoffs;
    }

    /**
     * Returns the square(s) (represented with a ArrayList, where
     * the first int is the row number of the square, and the
     * second int is the column number) with a pure strategy
     * Nash Equilibrium. Returns null if no pure
     * strategy Nash Equilibrium exists.
     */
    public ArrayList<ArrayList<Integer>> findPureNashEquilibrium() {
        ArrayList<ArrayList<Integer>> equilibria = new ArrayList<>();
        boolean exists = false;
        //Find P1's best response to P2's first move option
        int p1BRToMove1 = findBestResponse(1, 1);
        //Find P1's best response to P2's second move option
        int p1BRToMove2 = findBestResponse(2, 1);
        //Find P2's best response to P1's first move option
        int p2BRToMove1 = findBestResponse(1, 2);
        //Find P2's best response to P1's second move option
        int p2BRToMove2 = findBestResponse(2, 2);

        //check if Nash Equilibrium is in top left square
        if (p1BRToMove1 == 1 && p2BRToMove1 == 1) {
            exists = true;
            equilibria.add(new ArrayList<>(Arrays.asList(1, 1)));

        }
        //check first row second column
        if (p1BRToMove2 == 1 && p2BRToMove1 == 2) {
            exists = true;
            equilibria.add(new ArrayList<>(Arrays.asList(1, 2)));
        }
        //check second row first column
        if (p1BRToMove1 == 2 && p2BRToMove2 == 1) {
            exists = true;
            equilibria.add(new ArrayList<>(Arrays.asList(2, 1)));
        }
        //check second row second column
        if (p1BRToMove2 == 2 && p2BRToMove2 == 2) {
            exists = true;
            equilibria.add(new ArrayList<>(Arrays.asList(2, 2)));
        }

        if (numMoves == 3) {
            //Find P1's best response to P2's third move option
            int p1BRToMove3 = findBestResponse(3, 1);
            //Find P2's best response to P1's third move option
            int p2BRToMove3 = findBestResponse(3, 2);
            //check first row third column
            if (p1BRToMove3 == 1 && p2BRToMove1 == 3) {
                exists = true;
                equilibria.add(new ArrayList<>(Arrays.asList(1, 3)));
            }
            //check second row third column
            if (p1BRToMove3 == 2 && p2BRToMove2 == 3) {
                exists = true;
                equilibria.add(new ArrayList<>(Arrays.asList(2, 3)));
            }
            //check third row first column
            if (p1BRToMove1 == 3 && p2BRToMove3 == 1) {
                exists = true;
                equilibria.add(new ArrayList<>(Arrays.asList(3, 1)));
            }
            //check third row second column
            if (p1BRToMove2 == 3 && p2BRToMove3 == 2) {
                exists = true;
                equilibria.add(new ArrayList<>(Arrays.asList(3, 2)));
            }
            //check third row third column
            if (p1BRToMove3 == 3 && p2BRToMove3 == 3) {
                exists = true;
                equilibria.add(new ArrayList<>(Arrays.asList(3, 2)));
            }
        }

        if (!exists) {
            return null;
        } else {
            return equilibria;
        }
    }

    /**
     * Finds best response for a given player,
     * given the other player's move. Returns null
     * if given invalid input.
     */
    public Integer findBestResponse(int move, int player) {
        if (move <= 0 || move > numMoves || player <= 0 || player > 2) {
            return null;
        }
        //Find player's best response to the other player's move
        int bestResponse = 1;
        if (player == 1) {
            int max = p1Payoffs[0][move - 1];
            if (max < p1Payoffs[1][move - 1]) {
                bestResponse = 2;
                max = p1Payoffs[1][move - 1];
            }
            if (numMoves == 3 && max < p1Payoffs[2][move - 1]) {
                bestResponse = 3;
            }
        } else if (player == 2) {
            int max = p2Payoffs[move - 1][0];
            if (max < p2Payoffs[move - 1][1]) {
                bestResponse = 2;
                max = p2Payoffs[move - 1][1];
            }
            if (numMoves == 3 && max < p2Payoffs[move - 1][2]) {
                bestResponse = 3;
            }
        }
        return bestResponse;
    }

    /**
     * For a 2x2 matrix, returns the probabilities p and q of the mixed
     * strategy Nash Equilibrium (represented with a ArrayList, where
     * the first int is the value for p, and the second int is the
     * value for 1). p is the probability that P1 plays their first move,
     * and 1-p is the probability they play their second. q is the
     * probability P2 plays their first move, and 1-1 is the probability
     * they play their second. Return null if payoff values do not provide
     * valid equations for p and q using the method taught in class. Returns
     * null if the matrix is not 2x2.
     */
    public ArrayList<Double> findMixedNashEquilibrium() {
        if (numMoves == 3) {
            return null;
        }
        ArrayList<Double> pq = new ArrayList<>();

        //solve for p
        int pCoefficient = p2Payoffs[0][0] - p2Payoffs[1][0] - p2Payoffs[0][1] + p2Payoffs[1][1];
        double pConstant = p2Payoffs[1][1] - p2Payoffs[1][0];
        if (pCoefficient == 0) {
            return null;
        }
        double p = pConstant / pCoefficient;
        pq.add(p);

        //solve for q
        int qCoefficient = p1Payoffs[0][0] - p1Payoffs[0][1] - p1Payoffs[1][0] + p1Payoffs[1][1];
        double qConstant = p1Payoffs[1][1] - p1Payoffs[0][1];
        if (qCoefficient == 0) {
            return null;
        }
        double q = qConstant / qCoefficient;
        pq.add(q);

        return pq;
    }

    /**
     * Getter for numMoves
     */
    public int getNumMoves() {
        return numMoves;
    }

    public List<String> simulateDeviationPath(int startRow, int startCol, int deviatingPlayer, int deviationMove) {
        List<String> path = new ArrayList<>();
        path.add("Move" + startRow + ",Move" + startCol);

        int currentP1Move = startRow;
        int currentP2Move = startCol;
        Set<String> visitedStates = new HashSet<>();
        visitedStates.add("Move" + currentP1Move + ",Move" + currentP2Move);

        // first deviation specified by input
        if (deviatingPlayer == 1) {
            currentP1Move = deviationMove;
        } else {
            currentP2Move = deviationMove;
        }
        String newState = "Move" + currentP1Move + ",Move" + currentP2Move;
        path.add(newState);
        visitedStates.add(newState);

        // back and forth between deviatign players
        int activePlayer = (deviatingPlayer == 1) ? 2 : 1;
        int maxIter = numMoves * 4;
        int iter = 0;

        while (iter < maxIter) {
            iter++;

            // get best responses as next move
            int newMove;
            if (activePlayer == 1) {
                newMove = findBestResponse(currentP2Move, 1);
                if (newMove == currentP1Move) break;
                currentP1Move = newMove;
            } else {
                newMove = findBestResponse(currentP1Move, 2);
                if (newMove == currentP2Move) break;
                currentP2Move = newMove;
            }

            newState = "Move" + currentP1Move + ",Move" + currentP2Move;

            // if cycles we stop
            if (visitedStates.contains(newState)) {
                path.add(newState + " (cycle detected)");
                break;
            }

            path.add(newState);
            visitedStates.add(newState);
            activePlayer = 3 - activePlayer;
        }

        return path;
    }
}
