package view; 

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BoardPanel extends JPanel {
	
	private final int cellSize = 40;
	private final int gridSize = 10;
    private final int[][] cellStates;

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

                //out of bounds check
                if(row >= 0 && row < gridSize && col >= 0 && col < gridSize) {

                    if (cellStates[row][col] == 0) {
                        cellStates[row][col] = 1;
                    } else {
                        cellStates[row][col] = 0;
                    }

                    System.out.println("Clicked cell: (" + row + ", " + col + ")");
                    repaint();
                }
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