package view;

import model.Battleship;
import model.Player;

import javax.swing.*; 
import java.awt.*;
import java.awt.event.*;

public class BattleshipWindow extends JFrame {
    private final Battleship game;
    private final BoardPanel playerBoard;
    private final TargetBoardPanel enemyBoard;
    private final JPanel centerPanel;
    private final JLabel statusLabel;
    private boolean player1Done = false;

	public BattleshipWindow() {
		super("Battleship");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(230, 230, 240)); // light gray
        setResizable(false);

        statusLabel = new JLabel("Player 1: place your ships.");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        add(statusLabel, BorderLayout.SOUTH);

        game = new Battleship();
        DragController drag = new  DragController();

        playerBoard = new BoardPanel(drag);
        enemyBoard = new TargetBoardPanel(game, statusLabel);

        centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(playerBoard, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        //making the sidebar for buttons that specify what ship is being used
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(180, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(0, 1, 8, 8));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Controls"));

        //create buttons
        JButton carrierButton = new JButton("Carrier(5)");
        JButton battleshipButton = new JButton("Battleship(4)");
        JButton cruiserButton = new JButton("Cruiser(3)");
        JButton submarineButton = new JButton("Submarine(2)");
        JButton destroyerButton = new JButton("Destroyer(1)");
        JButton rotateButton = new JButton("Rotate");
        JButton readyButton = new JButton("Ready");
        JButton resetButton = new JButton("Reset");

        buttonPanel.add(carrierButton);
        buttonPanel.add(battleshipButton);
        buttonPanel.add(cruiserButton);
        buttonPanel.add(submarineButton);
        buttonPanel.add(destroyerButton);
        buttonPanel.add(rotateButton);
        buttonPanel.add(readyButton);
        buttonPanel.add(resetButton);

        carrierButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                drag.startDrag(1, 5, playerBoard.isHorizontal() , playerBoard.getCarrierH(), playerBoard.getCarrierV());
            }
        });

        battleshipButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                drag.startDrag(2, 4, playerBoard.isHorizontal() , playerBoard.getBattleshipH(), playerBoard.getBattleshipV());
            }
        });

        cruiserButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                drag.startDrag(3, 3, playerBoard.isHorizontal() , playerBoard.getCruiserH(), playerBoard.getCruiserV());
            }
        });

        submarineButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                drag.startDrag(4, 2, playerBoard.isHorizontal() , playerBoard.getSubmarineH(), playerBoard.getSubmarineV());
            }
        });

        destroyerButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                drag.startDrag(5, 1, playerBoard.isHorizontal() , playerBoard.getDestroyerH(), playerBoard.getDestroyerV());
            }
        });

        rotateButton.addActionListener(e -> {
            drag.horizontal = !drag.horizontal;
            playerBoard.toggleHorizontal();
        });

        readyButton.addActionListener(e -> {
            if (!playerBoard.allShipsPlaced()) {
                JOptionPane.showMessageDialog(
                        this,
                        "You must place all 5 ships before continuing.",
                        "Ships not placed.",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (!player1Done) {
                //FIRST PRESS: save Player 1 ships
                Player p1 = game.getPlayer1();

                for (view.PlacedShip s : playerBoard.getPlacedShips()) {
                    model.PlacedShip backendShip =
                            new model.PlacedShip(s.row, s.col, s.length, s.horizontal, s.type);
                    boolean ok = p1.placeShip(backendShip);
                    if (!ok) {
                        System.err.println("Backend rejected P1 ship type " + s.type);
                    }
                }

                player1Done = true;

                // Clear the GUI board for Player 2 placement
                playerBoard.resetBoard();

                // Re-enable buttons in case they were disabled
                carrierButton.setEnabled(true);
                battleshipButton.setEnabled(true);
                cruiserButton.setEnabled(true);
                submarineButton.setEnabled(true);
                destroyerButton.setEnabled(true);
                rotateButton.setEnabled(true);
                readyButton.setEnabled(true);

                statusLabel.setText("Player 2: place your ships.");
                return;
            }

            //SECOND PRESS: save Player 2 ships and start game
            Player p2 = game.getPlayer2();

            for (view.PlacedShip s : playerBoard.getPlacedShips()) {
                model.PlacedShip backendShip =
                        new model.PlacedShip(s.row, s.col, s.length, s.horizontal, s.type);
                boolean ok = p2.placeShip(backendShip);
                if (!ok) {
                    System.err.println("Backend rejected P2 ship type " + s.type);
                }
            }

            // Now lock placement and disable ship-selection buttons
            playerBoard.lockPlacement();

            carrierButton.setEnabled(false);
            battleshipButton.setEnabled(false);
            cruiserButton.setEnabled(false);
            submarineButton.setEnabled(false);
            destroyerButton.setEnabled(false);
            rotateButton.setEnabled(false);
            readyButton.setEnabled(false);

            centerPanel.removeAll();
            centerPanel.add(enemyBoard, BorderLayout.CENTER);
            centerPanel.revalidate();
            centerPanel.repaint();
            statusLabel.setText("Player 1's turn");

            //remove button to reset ship placement after game has started
            resetButton.setVisible(false);
        });


        resetButton.addActionListener(e -> {
            playerBoard.resetBoard();

            carrierButton.setEnabled(true);
            battleshipButton.setEnabled(true);
            cruiserButton.setEnabled(true);
            submarineButton.setEnabled(true);
            destroyerButton.setEnabled(true);
            rotateButton.setEnabled(true);
            readyButton.setEnabled(true);

            String placingPlayer = player1Done ? "Player 2" : "Player 1";
            statusLabel.setText(placingPlayer + ": place your ships.");
        });



        sidebar.add(buttonPanel, BorderLayout.NORTH);
        add(sidebar, BorderLayout.EAST);

        setVisible(true);
	}


	public static void main(String[] args) {
		new BattleshipWindow(); 
	}
}