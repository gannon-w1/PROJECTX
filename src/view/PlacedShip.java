package view;

public class PlacedShip {
    public int row, col, length;
    public boolean horizontal;
    public int type;

    public PlacedShip(int row, int col, int length, boolean horizontal, int type) {
        this.row = row;
        this.col = col;
        this.length = length;
        this.horizontal = horizontal;
        this.type = type;
    }
}

