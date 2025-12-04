package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Battleship {

    private final Player player1;
    private final Player player2;
    private Player turn;  // current active player

    // stores the cells of the most recently sunk ship (used by GUI)
    private final List<Point> lastSunkCells = new ArrayList<>();

    public Battleship() {
        player1 = new Player();
        player2 = new Player();
        turn = player1;
    }

    public Player getPlayer1() { return player1; }
    public Player getPlayer2() { return player2; }

    public Player getCurrentPlayer() { return turn; }

    public Player getOpponent() {
        return (turn == player1) ? player2 : player1;
    }

    // swap turns (only called on MISS)
    public void nextTurn() {
        turn = (turn == player1) ? player2 : player1;
    }

    public String fireAt(int row, int col) {
        Player defender = getOpponent();

        String result = turn.fireAt(defender, row, col);

        // manage sunk ship details for GUI
        if ("sunk".equals(result)) {
            lastSunkCells.clear();
            lastSunkCells.addAll(defender.getBoard().getLastSunkCells());
        } else {
            lastSunkCells.clear();
        }

        // turn switches only on MISS
        if ("miss".equals(result)) {
            nextTurn();
        }

        return result;
    }

    public List<Point> getLastSunkCells() {
        return new ArrayList<>(lastSunkCells); // defensive copy
    }

    // Game state queries

    public boolean isGameOver() {
        return player1.allShipsSunk() || player2.allShipsSunk();
    }

    public Player getWinner() {
        if (!isGameOver()) return null;
        return player1.allShipsSunk() ? player2 : player1;
    }
}
