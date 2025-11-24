package model;

import java.util.ArrayList;
import java.util.List;

public class Board {
private final int size = 10;
private final int[][] grid; // numbers on the grid mean: 0=empty, 1=ship, 2=hit, 3=miss
private final List<PlacedShip> ships;

public Board() {
    grid = new int[size][size];
    ships = new ArrayList(); // initialize properly
}

// place a ship on board
public boolean placeShip(PlacedShip ship) { 
    if(!canPlace(ship)){
        return false; 
    }

    ships.add(ship); // add ship to list
    int r = ship.getRow();
    int c = ship.getCol();
    // build ship
    for(int i = 0; i < ship.getLength(); i++){
        if(ship.isHorizontal()){
            grid[r][c+i] = 1;
        }
        else{
            grid[r+i][c] = 1;
        }
    }
    return true;
}

// check placement
public boolean canPlace(PlacedShip ship) { 
    int r = ship.getRow();
    int c = ship.getCol();
    // build and loop through theoretical ship to check if any index of the ship is invalid
    for(int i = 0; i < ship.getLength(); i++){
        // rr and cc build the ship theoretically
        int rr = ship.isHorizontal() ? r : r+i; 
        int cc = ship.isHorizontal() ? c+i : c;
        if(rr >= size || cc >= size) return false; // if ship does not fit on board
        if(grid[rr][cc] != 0) return false; // if slot is not empty
    }
    return true; 
}

// fire at a cell
public String fireAt(int row, int col) { 
    if(row < 0 || row >= size || col < 0 || col >= size) return "invalid"; // invalid cell
    
    // named the number values for readability in the switch statement
    final int SHIP = 1;
    final int EMPTY = 0;

    int cell = grid[row][col];
    switch(cell){
        case SHIP: // hit
            grid[row][col] = 2;
            // check which ship was hit
            for(PlacedShip s : ships){
                if(s.contains(row, col)){
                    s.hit();
                    // check if ship is sunk
                    if(s.isSunk()) return "sunk";
                }
            }
            return "hit";
        case EMPTY: // miss
            grid[row][col] = 3;
            return "miss";
        default: // already fired at cell, marked as "redundant" for easier use
            return "redundant";
    }
}

// check if all ships on the board are sunk
public boolean allShipsSunk() { 
    // loop through ships to check if all ships sunk
    for(PlacedShip s : ships){
        if(!s.isSunk()) return false;
    }
    return true;
}

// getters
public int[][] getGrid() { return grid; }
public List<PlacedShip> getShips() { return ships; }

}