package model;

public class PlacedShip {
    private final int row, col, length, type;
    private final boolean horizontal;
    private int hits;

    public PlacedShip(int row, int col, int length, boolean horizontal, int type) {
        this.row = row;
        this.col = col;
        this.length = length;
        this.horizontal = horizontal;
        this.type = type;
        this.hits = 0;
    }

    // check if part of the ship is in the index (r,c)
    public boolean contains(int r, int c) {
        for(int i=0; i<length; i++) {
            int rr = horizontal ? row : row+i;
            int cc = horizontal ? col+i : col;
            if(rr == r && cc == c) return true;
        }
        return false;
    }

    // hit functions
    public void hit() { hits++; }
    public boolean isSunk() { return hits >= length; }

    // getters
    public int getRow() { return row; }
    public int getCol() { return col; }
    public int getLength() { return length; }
    public boolean isHorizontal() { return horizontal; }
    public int getType() { return type; }
}
