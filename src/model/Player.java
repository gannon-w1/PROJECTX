package model;

public class Player{
    private final Board board;

    public Player(){
        board = new Board(); // build player board
    }

    public Board getBoard(){ return board;}

    public boolean placeShip(PlacedShip ship){
        return board.placeShip(ship);
    }

    public String fireAt(Player opp, int row, int col){
        return opp.getBoard().fireAt(row, col);
    }

    public boolean allShipsSunk(){
        return board.allShipsSunk();
    }
}