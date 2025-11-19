package view; 

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BoardPanel extends JPanel {
	
	private final int cellSize = 40;
	private final int gridSize = 10;
    private final int[][] cellStates;
    private int shipLength;
    private boolean horizontal = true;

    public void setShipLength(int len) {
        this.shipLength = len;
        System.out.println("Ship length: " + shipLength);
    }

    public void toggleHorizontal() {
        horizontal = !horizontal;
        System.out.println("Orientation: " + (horizontal ? "Horizontal" : "Vertical"));
    }

	public BoardPanel() {
        setPreferredSize(new Dimension(cellSize * gridSize, cellSize * gridSize));

        cellStates = new int[gridSize][gridSize];
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                cellStates[i][j] = 0;
            }
        }

        //mouse listener
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                int row = e.getY() / cellSize;
                int col = e.getX() / cellSize;

                for (int i = 0; i < gridSize; i++) {
                    for (int j = 0; j < gridSize; j++) {
                        cellStates[i][j] = 0;
                    }
                }

                if(horizontal){ //horizontal placement
                    if(col + shipLength > gridSize){
                        System.out.println("Ship does not fit horizontally!");
                        return;
                    }

                    for (int i = 0; i < shipLength; i++) {
                        cellStates[row][col + i] = 1;
                    }
                } else {    //vertical placement
                    if(row + shipLength > gridSize){
                        System.out.println("Ship does not fit vertically!");
                        return;
                    }

                    for (int i = 0; i < shipLength; i++) {
                        cellStates[row + i][col] = 1;
                    }
                }

                System.out.println("Clicked cell: (" + row + ", " + col + ")");
                repaint();
            }
        });

    }
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

        //fill cells based on state
        for(int i = 0; i < gridSize; i++) {
            for(int j = 0; j < gridSize; j++) {
                int x = j *  cellSize;
                int y = i * cellSize;

                if(cellStates[i][j] == 1) {
                    g.setColor(Color.BLUE);
                } else {
                    g.setColor(Color.WHITE);
                }
                g.fillRect(x, y, cellSize, cellSize);
            }
        }
		
		//draws vertical and horizontal lines
		g.setColor(Color.BLACK);
		for(int i = 0; i <= gridSize; i++) {
			g.drawLine(i * cellSize,  0, i * cellSize, gridSize * cellSize);
	        g.drawLine(0, i * cellSize, gridSize * cellSize, i * cellSize);
		}
	}
	
}