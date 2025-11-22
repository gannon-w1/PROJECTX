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
    private boolean horizontal = true;
    private boolean[] shipPlaced = new boolean[6];

    private final Image carrierH, carrierV;
    private final Image battleshipH, battleshipV;
    private final Image cruiserH, cruiserV;
    private final Image submarineH, submarineV;
    private final Image destroyerH, destroyerV;

    private DragController drag;

    //constructor
	public BoardPanel(DragController drag) {
        this.drag = drag;
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

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(drag.dragging) {
                    drag.ghostRow = e.getY() / cellSize;
                    drag.ghostCol = e.getX() / cellSize;
                    repaint();
                }
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
        @Override
            public void mouseMoved(MouseEvent e) {
                if(drag.dragging) {
                    drag.ghostRow = e.getY() / cellSize;
                    drag.ghostCol = e.getX() / cellSize;
                    repaint();
                }
            }
        });
        //mouse listener
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                if(!drag.dragging) {
                    return;
                }

                int row = drag.ghostRow;
                int col = drag.ghostCol;

                int length = drag.shipLength;
                boolean horizontal = drag.horizontal;
                int type = drag.shipType;

                //bounds checking
                if(horizontal && col + length > gridSize) {
                    System.out.println("Doesn't fit horizontallly");
                    drag.stopDrag();
                    repaint();
                    return;
                }

                if(!horizontal && row + length > gridSize) {
                    System.out.println("Doesn't fit vertically");
                    drag.stopDrag();
                    repaint();
                    return;
                }

                //overlap check
                if(overlapDetection(row, col, length, horizontal)) {
                    System.out.println("Overlaps another ship");
                    drag.stopDrag();
                    repaint();
                    return;
                }

                //doesn't place ship if its already on the board
                if(shipPlaced[type]) {
                    System.out.println("Ship already placed");
                    drag.stopDrag();
                    repaint();
                    return;
                }

                //place ship
                placedShips.add(new PlacedShip(row, col, length, horizontal, type));
                shipPlaced[type] = true;

                drag.stopDrag();
                repaint();
            }
        });
    }

    //Getter functions
    public Image getCarrierH() {
        return carrierH;
    }

    public Image getCarrierV() {
        return carrierV;
    }

    public Image getDestroyerH() {
        return destroyerH;
    }

    public Image getDestroyerV() {
        return destroyerV;
    }

    public Image getBattleshipH() {
        return battleshipH;
    }

    public Image getBattleshipV() {
        return battleshipV;
    }

    public Image getCruiserH() {
        return cruiserH;
    }

    public Image getCruiserV() {
        return cruiserV;
    }

    public Image getSubmarineH() {
        return submarineH;
    }

    public Image getSubmarineV() {
        return submarineV;
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    public int getCellSize() {
        return cellSize;
    }
    //toggle for horizontal or vertical
    public void toggleHorizontal() {
        horizontal = !horizontal;
        System.out.println("Orientation: " + (horizontal ? "Horizontal" : "Vertical"));
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

    private void drawGhostShip(Graphics g) {
        if(!drag.dragging)
            return;

        Image img = drag.horizontal ? drag.imageH : drag.imageV;
        if(img == null)
            return;

        int x = drag.ghostCol * cellSize;
        int y = drag.ghostRow * cellSize;

        int w = drag.horizontal ? drag.shipLength * cellSize : cellSize;
        int h = drag.horizontal ? cellSize : drag.shipLength * cellSize;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g2.drawImage(img, x, y, w, h, this);
        g2.dispose();
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
        drawGhostShip(g);
	}
	
}