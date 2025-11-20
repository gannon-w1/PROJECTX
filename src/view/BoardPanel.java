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
    private int placedRow = -1;
    private int placedCol = -1;
    private final Image carrierH;
    private final Image carrierV;
    private final Image battleshipH;
    private final Image battleshipV;
    private final Image cruiserH;
    private final Image cruiserV;
    private final Image submarineH;
    private final Image submarineV;
    private final Image destroyerH;
    private final Image destroyerV;

    //load the images
    private Image loadImage(String path) {
        return new ImageIcon(getClass().getResource(path)).getImage();
    }

    //getter for ship images
    private Image getShipImage() {
        return switch (shipLength) {
            case 5 -> horizontal ? carrierH : carrierV;
            case 4 -> horizontal ? battleshipH : battleshipV;
            case 3 -> horizontal ? cruiserH : cruiserV;
            case 2 -> horizontal ? submarineH : submarineV;
            case 1 -> horizontal ? destroyerH : destroyerV;
            default -> null;
        };
    }

    //ensures correct sizing
    private void drawShipImage(Graphics g) {
        if(placedRow == -1 || placedCol == -1)
            return;

        Image img = getShipImage();
        if (img == null)
            return;

        int drawX = placedCol * cellSize;
        int drawY = placedRow * cellSize;

        int w = horizontal ? shipLength * cellSize : cellSize;
        int h = horizontal ? cellSize : shipLength * cellSize;

        g.drawImage(img, drawX, drawY, w, h, this);
    }

    //sets the ship length
    public void setShipLength(int len) {
        this.shipLength = len;
        System.out.println("Ship length: " + shipLength);
    }

    //toggle for horizontal or verticle
    public void toggleHorizontal() {
        horizontal = !horizontal;
        System.out.println("Orientation: " + (horizontal ? "Horizontal" : "Vertical"));
    }

	public BoardPanel() {
        setPreferredSize(new Dimension(cellSize * gridSize, cellSize * gridSize));

        //Load the images
        carrierH = loadImage("/view/resources/ships/carrierH.png");
        carrierV = loadImage("/view/resources/ships/carrierV.png");

        cruiserH = loadImage("/view/resources/ships/cruiserH.png");
        cruiserV = loadImage("/view/resources/ships/cruiserV.png");

        destroyerH = loadImage("/view/resources/ships/destroyerH.png");
        destroyerV = loadImage("/view/resources/ships/destroyerV.png");

        submarineH = loadImage("/view/resources/ships/submarineH.png");
        submarineV = loadImage("/view/resources/ships/submarineV.png");

        battleshipH = loadImage("/view/resources/ships/battleshipH.png");
        battleshipV = loadImage("/view/resources/ships/battleshipV.png");

        //board data
        cellStates = new int[gridSize][gridSize];

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

                    //place ship
                    for (int i = 0; i < shipLength; i++) {
                        cellStates[row][col + i] = 1;
                    }

                    //where image goes
                    placedRow = row;
                    placedCol = col;

                } else {    //vertical placement

                    if(row + shipLength > gridSize){
                        System.out.println("Ship does not fit vertically!");
                        return;
                    }

                    for (int i = 0; i < shipLength; i++) {
                        cellStates[row + i][col] = 1;
                    }

                    //where image goes
                    placedRow = row;
                    placedCol = col;
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

        drawShipImage(g);
	}
	
}