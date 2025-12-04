package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Board {
    private final int size = 10;
    // 0 = empty, 1 = ship, 2 = hit, 3 = miss
    private final int[][] grid;
    private final List<PlacedShip> ships;

    // cells (col,row) of the ship sunk by the LAST call to fireAt()
    private final List<Point> lastSunkCells = new ArrayList<>();

    public Board() {
        grid = new int[size][size];
        ships = new ArrayList<>();
    }

    // place a ship on board
    public boolean placeShip(PlacedShip ship) {
        if (!canPlace(ship)) {
            return false;
        }

        ships.add(ship);

        int r = ship.getRow();
        int c = ship.getCol();

        for (int i = 0; i < ship.getLength(); i++) {
            if (ship.isHorizontal()) {
                grid[r][c + i] = 1;
            } else {
                grid[r + i][c] = 1;
            }
        }
        return true;
    }

    // check placement
    public boolean canPlace(PlacedShip ship) {
        int r = ship.getRow();
        int c = ship.getCol();

        for (int i = 0; i < ship.getLength(); i++) {
            int rr = ship.isHorizontal() ? r      : r + i;
            int cc = ship.isHorizontal() ? c + i  : c;

            if (rr >= size || cc >= size) return false; // off board
            if (grid[rr][cc] != 0) return false;        // already occupied
        }
        return true;
    }

    // fire at a cell
    public String fireAt(int row, int col) {
        if (row < 0 || row >= size || col < 0 || col >= size) {
            return "invalid";
        }

        // clear info about any previous sunk ship
        lastSunkCells.clear();

        final int EMPTY = 0;
        final int SHIP  = 1;

        int cell = grid[row][col];

        switch (cell) {
            case SHIP: // hit
                grid[row][col] = 2;

                // find which ship was hit
                for (PlacedShip s : ships) {
                    if (s.contains(row, col)) {
                        s.hit();

                        // if it just sunk, record ALL its cells
                        if (s.isSunk()) {
                            int r = s.getRow();
                            int c = s.getCol();

                            for (int i = 0; i < s.getLength(); i++) {
                                int rr = s.isHorizontal() ? r      : r + i;
                                int cc = s.isHorizontal() ? c + i  : c;
                                // x = col, y = row
                                lastSunkCells.add(new Point(cc, rr));
                            }
                            return "sunk";
                        }
                        break;
                    }
                }
                return "hit";

            case EMPTY: // miss
                grid[row][col] = 3;
                return "miss";

            default:    // already targeted
                return "redundant";
        }
    }

    // check if all ships on the board are sunk
    public boolean allShipsSunk() {
        for (PlacedShip s : ships) {
            if (!s.isSunk()) return false;
        }
        return true;
    }

    // getters (you may or may not actually use these from the GUI)
    public int[][] getGrid() { return grid; }
    public List<PlacedShip> getShips() { return ships; }

    // expose last sunk ship cells for Battleship / UI
    public List<Point> getLastSunkCells() {
        return new ArrayList<>(lastSunkCells); // defensive copy
    }
}
