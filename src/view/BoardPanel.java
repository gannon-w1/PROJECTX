package view; 

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BoardPanel extends JPanel {
	
	private final int cellSize = 40;
	private final int gridSize = 10;
	
	public BoardPanel() {
        setPreferredSize(new Dimension(cellSize * gridSize, cellSize * gridSize));

        //mouse listener
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                int row = e.getY() / cellSize;
                int col = e.getX() / cellSize;

                System.out.println("Clicked cell: (" + row + ", " + col + ")");
            }
        });
    }
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		//draws vertical and horizontal lines
		g.setColor(Color.BLACK);
		for(int i = 0; i <= gridSize; i++) {
			g.drawLine(i * cellSize,  0, i * cellSize, gridSize * cellSize);
	        g.drawLine(0, i * cellSize, gridSize * cellSize, i * cellSize);
		}
	}
	
}