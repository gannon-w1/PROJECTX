package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Battleship {
    private final Player player1;
    private final Player player2;
    private Player turn; // pointer to current player whose turn it is

    // cells of the ship sunk by the last successful "sunk" shot
    private final List<Point> lastSunkCells = new ArrayList<>();

    public Battleship() {
        player1 = new Player();
        player2 = new Player();
        turn = player1;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Player getCurrentPlayer() {
        return turn;
    }

    public Player getOpponent() {
        return (turn == player1) ? player2 : player1;
    }

    // Turn Functions
    public void nextTurn() {
        if (turn == player1) {
            turn = player2;
            System.out.println("Player 2 turn");
        } else {
            turn = player1;
            System.out.println("Player 1 turn");
        }
    }

    /**
     * Fire at (row, col) on the opponent's board.
     * Only switches turn on a miss.
     * Also records the exact cells of a sunk ship so the UI can highlight them.
     */
    public String fireAt(int row, int col) {
        Player defender = getOpponent();

        String result = turn.fireAt(defender, row, col);

        if ("sunk".equals(result)) {
            // pull the sunk-ship cells from the defender's board
            lastSunkCells.clear();
            lastSunkCells.addAll(defender.getBoard().getLastSunkCells());
        } else {
            // no sunk ship this shot
            lastSunkCells.clear();
        }

        // only switch turns on miss
        if ("miss".equals(result)) {
            nextTurn();
        }

        return result;
    }

    // Expose last sunk ship cells for TargetBoardPanel
    public List<Point> getLastSunkCells() {
        return new ArrayList<>(lastSunkCells); // defensive copy
    }

    // Game Condition Functions

    // check if game is over
    public boolean isGameOver() {
        // true if all ships are sunk for any player, else false
        return player1.allShipsSunk() || player2.allShipsSunk();
    }

    // get winner (null if game not over)
    public Player getWinner() {
        if (!isGameOver()) return null;
        // returns player2 as winner if player1's ships are all sunk, else player1
        return player1.allShipsSunk() ? player2 : player1;
    }

    // Testing
    public static void main(String[] args) { }
}
