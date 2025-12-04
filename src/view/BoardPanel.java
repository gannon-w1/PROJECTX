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
    private final boolean[] shipPlaced = new boolean[6];

    private final Image carrierH, carrierV;
    private final Image battleshipH, battleshipV;
    private final Image cruiserH, cruiserV;
    private final Image submarineH, submarineV;
    private final Image destroyerH, destroyerV;

    private final DragController drag;
    private boolean placementLocked = false;

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
                if(placementLocked) {
                    drag.stopDrag();
                    return;
                }

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

    public boolean allShipsPlaced() {
        for(int i = 1; i <= 5; i++) {
            if(!shipPlaced[i]) {
                return false;
            }
        }
        return true;
    }

    public java.util.List<PlacedShip> getPlacedShips() {
        return placedShips;
    }

    public void resetBoard() {
        placedShips.clear();
        for(int i = 1; i < shipPlaced.length; i++) {
            shipPlaced[i] = false;
        }
        placementLocked = false;
        if(drag != null) {
            drag.stopDrag();
        }
        repaint();
    }

    public void lockPlacement() {
        placementLocked = true;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                int x = c * cellSize;
                int y = r * cellSize;

                if ((r + c) % 2 == 0)
                    g2.setColor(new Color(215, 230, 255)); // light ocean blue
                else
                    g2.setColor(new Color(195, 215, 245)); // slightly darker blue

                g2.fillRect(x, y, cellSize, cellSize);
            }
        }

        //draws vertical and horizontal lines
        g2.setColor(new Color(40, 40, 40));
        g2.setStroke(new BasicStroke(2f));
        for (int i = 0; i <= gridSize; i++) {
            g2.drawLine(i * cellSize, 0, i * cellSize, gridSize * cellSize);
            g2.drawLine(0, i * cellSize, gridSize * cellSize, i * cellSize);
        }

        for (PlacedShip s : placedShips) {
            Image img = getShipImageForType(s.type, s.horizontal);

            int x = s.col * cellSize;
            int y = s.row * cellSize;

            int w = s.horizontal ? s.length * cellSize : cellSize;
            int h = s.horizontal ? cellSize : s.length * cellSize;

            g2.drawImage(img, x, y, w, h, this);
        }
        drawGhostShip(g2);
    }
}