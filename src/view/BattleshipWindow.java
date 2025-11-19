package view;

import javax.swing.*; 
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BattleshipWindow extends JFrame {
	
	public BattleshipWindow() {
		super("Battleship");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLayout(new BorderLayout());

        BoardPanel board = new BoardPanel();
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

        carrierButton.addActionListener(new ShipLengthListener(board, 5));
        battleshipButton.addActionListener(new ShipLengthListener(board, 4));
        cruiserButton.addActionListener(new ShipLengthListener(board, 3));
        submarineButton.addActionListener(new ShipLengthListener(board, 2));
        destroyerButton.addActionListener(new ShipLengthListener(board, 1));
        rotateButton.addActionListener(e -> board.toggleHorizontal());

        sidebar.add(buttonPanel, BorderLayout.NORTH);
        add(sidebar, BorderLayout.EAST);

        setVisible(true);
	}

    private static class ShipLengthListener implements ActionListener {

        private final BoardPanel board;
        private final int length;

        public ShipLengthListener(BoardPanel b, int len) {
            board = b;
            length = len;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            board.setShipLength(length);
        }
    }

	public static void main(String[] args) {
		new BattleshipWindow(); 
	}
}