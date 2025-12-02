package model;

public class Battleship {
    private final Player player1;
    private final Player player2;
    private Player turn; // pointer to current player who's turn it is

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

    public Player getCurrentPlayer() { return turn; }
    public Player getOpponent() { return turn == player1 ? player2 : player1; }

    // Turn Functions
    public void nextTurn() {
        if(turn == player1){
            turn  = player2;
            System.out.println("PLayer 2 turn");
        }else {
            turn = player1;
            System.out.println("Player 1 turn");
        }
    }

    public String fireAt(int row, int col) {
        String result = turn.fireAt(getOpponent(), row, col);
        if(result.equals("miss"))  // only switch turns if miss
            nextTurn();
        return result;
    }

    // Game Condition Functions

    // check if game is over
    public boolean isGameOver() {
        return player1.allShipsSunk() || player2.allShipsSunk(); // true if all ships are sunk for any player, else false
    }

    // get winner (null if game not over)
    public Player getWinner() {
        if(!isGameOver()) return null; // if game isn't over, no need to check for winner
        return player1.allShipsSunk() ? player2 : player1;
        // returns player2 as winner if player1's ships are all sunk, else player1
    }

    // Testing
    public static void main(String[] args) { }

}