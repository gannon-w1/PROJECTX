package view; 

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class BoardPanel extends JPanel {

    //constants / fields
	private final int cellSize = 40;
	private final int gridSize = 10;

    private final ArrayList<PlacedShip> placedShips = new ArrayList<>();
    private int shipLength;
    private boolean horizontal = true;
    private int selectedShipType = -1;

    private final Image carrierH, carrierV;
    private final Image battleshipH, battleshipV;
    private final Image cruiserH, cruiserV;
    private final Image submarineH, submarineV;
    private final Image destroyerH, destroyerV;

    //constructor
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

        //mouse listener
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = e.getY() / cellSize;
                int col = e.getX() / cellSize;

                if(selectedShipType == -1) {
                    System.out.println("Select a ship first!");
                    return;
                }

                if(alreadyPlaced(selectedShipType)) {
                    System.out.println("Ship has already been placed!");
                    return;
                }
                //check boundaries
                if(horizontal && col + shipLength > gridSize) {
                    System.out.println("Doesn't fit horizontally");
                    return;
                }

                if(!horizontal && row + shipLength > gridSize) {
                    System.out.println("Doesn't fit vertically");
                    return;
                }

                //check for overlap
                if(overlapDetection(row, col, shipLength, horizontal)) {
                    System.out.println("Overlaps another ship!");
                    return;
                }

                //If none of the chekcs fail, place ship and save
                placedShips.add(new PlacedShip(row, col, shipLength, horizontal, selectedShipType));

                repaint();
            }
        });

    }

    //toggle for horizontal or vertical
    public void toggleHorizontal() {
        horizontal = !horizontal;
        System.out.println("Orientation: " + (horizontal ? "Horizontal" : "Vertical"));
    }

    //sets the ship length/type
    public void setShipLength(int len) {
        this.shipLength = len;

        switch (len) {
            case 5: selectedShipType = 1; break;
            case 4: selectedShipType = 2; break;
            case 3: selectedShipType = 3; break;
            case 2: selectedShipType = 4; break;
            case 1: selectedShipType = 5; break;
        }

        System.out.println("Ship length: " + shipLength);
    }

    private boolean overlapDetection(int row, int col, int length, boolean horizontal) {
        for(PlacedShip ship : placedShips) {
            for(int i = 0; i < length; i++) {
                int newR = horizontal ? row : row + i;
                int newC = horizontal ? col + i : col;

                for(int j = 0; j < ship.length; j++) {
                    int oldR = ship.horizontal ? ship.row : ship.row + j;
                    int oldC = ship.horizontal ? ship.col + j : ship.col;

                    if(newR == oldR && newC == oldC)
                        return true; //overlap exists
                }
            }
        }
        return false;
    }

    //load the images
    private Image loadImage(String path) {
        return new ImageIcon(getClass().getResource(path)).getImage();
    }

    private Image getShipImageForType(int type, boolean horiz) {
        return switch (type) {
            case 1 -> horiz ? carrierH : carrierV;
            case 2 -> horiz ? battleshipH : battleshipV;
            case 3 -> horiz ? cruiserH : cruiserV;
            case 4 -> horiz ? submarineH : submarineV;
            case 5 -> horiz ? destroyerH : destroyerV;
            default -> null;
        };

    }

    //checks if a ship type is already placed
    private boolean alreadyPlaced(int type) {
        for(PlacedShip ship : placedShips) {
            if(ship.type == type) {
                return true;
            }
        }
        return false;
    }

    @Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		//draws vertical and horizontal lines
		for(int i = 0; i <= gridSize; i++) {
            g.setColor(Color.BLACK);
			g.drawLine(i * cellSize,  0, i * cellSize, gridSize * cellSize);
	        g.drawLine(0, i * cellSize, gridSize * cellSize, i * cellSize);
		}

        for (PlacedShip s : placedShips) {
            Image img = getShipImageForType(s.type, s.horizontal);

            int x = s.col * cellSize;
            int y = s.row * cellSize;

            int w = s.horizontal ? s.length * cellSize : cellSize;
            int h = s.horizontal ? cellSize : s.length * cellSize;

            g.drawImage(img, x, y, w, h, this);
        }
	}
	
}