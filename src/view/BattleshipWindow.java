package view;

import javax.swing.*; 
import java.awt.*;
import java.awt.event.*;

public class BattleshipWindow extends JFrame {
	
	public BattleshipWindow() {
		super("Battleship");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLayout(new BorderLayout());

        DragController drag = new  DragController();
        BoardPanel board = new BoardPanel(drag);
        add(board, BorderLayout.CENTER);

        //making the sidebar for buttons that specify what ship is being used
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(160, 0));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(0, 1, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //create buttons
        JButton carrierButton = new JButton("Carrier(5)");
        JButton battleshipButton = new JButton("Battleship(4)");
        JButton cruiserButton = new JButton("Cruiser(3)");
        JButton submarineButton = new JButton("Submarine(2)");
        JButton destroyerButton = new JButton("Destroyer(1)");
        JButton rotateButton = new JButton("Rotate");

        buttonPanel.add(carrierButton);
        buttonPanel.add(battleshipButton);
        buttonPanel.add(cruiserButton);
        buttonPanel.add(submarineButton);
        buttonPanel.add(destroyerButton);
        buttonPanel.add(rotateButton);

        carrierButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                drag.startDrag(1, 5, board.isHorizontal() , board.getCarrierH(), board.getCarrierV());
            }
        });

        battleshipButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                drag.startDrag(2, 4, board.isHorizontal() , board.getBattleshipH(), board.getBattleshipV());
            }
        });

        cruiserButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                drag.startDrag(3, 3, board.isHorizontal() , board.getCruiserH(), board.getCruiserV());
            }
        });

        submarineButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                drag.startDrag(4, 2, board.isHorizontal() , board.getSubmarineH(), board.getSubmarineV());
            }
        });

        destroyerButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                drag.startDrag(5, 1, board.isHorizontal() , board.getDestroyerH(), board.getDestroyerV());
            }
        });

        rotateButton.addActionListener(e -> {
            drag.horizontal = !drag.horizontal;
            board.toggleHorizontal();
        });

        sidebar.add(buttonPanel, BorderLayout.NORTH);
        add(sidebar, BorderLayout.EAST);

        setVisible(true);
	}

	public static void main(String[] args) {
		new BattleshipWindow(); 
	}
}