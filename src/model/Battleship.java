public class Battleship {
    private static Player turn; // pointer to player who's turn it is
    String[] shiptypes = {"Carrier", "Battleship", "Cruiser", "Submarine", "Destroyer"};
    /// taken from Battleship, the ship types are as follows:
    ///  Carrier (5 squares), Battleship (4 squares), Cruiser (3 squares), Submarine (3 squares), Destroyer (2 squares)
    String[] directions = {"North", "East", "South", "West"}; // directions
    
    // game control functions
    public static void startNewGame(){

    }

    public static void resetGame(){

    }

    // ship functions
    protected static boolean canPlaceShip(Player p, String type, int row, int col, String direction){
        return true;
    }

    protected static void placeShip(Player p, String type, int row, int col, String direction){
        
    }

    protected static boolean allShipsPlaced(Player p){
        return false;
    }

    // game mechanics
    protected static boolean fireAt(Player attacker, int row, int col){
        // true if hit, false if missed
        return false;
    }

    // game condition functions
    protected static boolean isGameOver(){
        return false;
    }

    /*protected static Player getWinner(){

    }*/

    // turn functions
    protected static Player getCurrentTurn(){
        return turn;
    }

    protected static void switchTurn(){
        
    }

    // board functions
    /*protected static int[][] getBoard(Player p){
        
    }*/

}
