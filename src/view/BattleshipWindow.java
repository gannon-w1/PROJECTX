package view;

import javax.swing.*; 
import java.awt.*; 

public class BattleshipWindow extends JFrame {
	
	public BattleshipWindow() {
		super("Battleship");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLayout(new BorderLayout());
        add(new BoardPanel(), BorderLayout.CENTER); 
        
        setVisible(true);
	}
	
	public static void main(String[] args) {
		new BattleshipWindow(); 
	}
}