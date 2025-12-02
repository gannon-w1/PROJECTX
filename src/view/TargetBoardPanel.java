package view;

import model.Battleship;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TargetBoardPanel extends JPanel {
    private final int cellSize = 40;
    private final int gridSize = 10;

    private final int[][] shotsP1 = new int[gridSize][gridSize];
    private final int[][] shotsP2 = new int[gridSize][gridSize];

    private final Battleship game;
    private final JLabel statusLabel;
    private boolean gameOver = false;

    public TargetBoardPanel(Battleship game, JLabel statusLabel) {
        this.game = game;
        this.statusLabel = statusLabel;
        setPreferredSize(new Dimension(cellSize * gridSize, cellSize * gridSize));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(gameOver) {
                    return;
                }
                int row = e.getY() / cellSize;
                int col = e.getX() / cellSize;

                if (row < 0 || row >= gridSize || col < 0 || col >= gridSize) {
                    return;
                }

                int[][] shots = getCurrentShotsMatrix();
                // already shot here
                if (shots[row][col] != 0) {
                    return;
                }


                String result = game.fireAt(row, col);
                System.out.println("Result from backend: " + result);

                switch (result) {
                    case "hit":
                    case "sunk":
                        shots[row][col] = 2;   // hit
                        break;
                    case "miss":
                        shots[row][col] = 1;   // miss
                        break;
                    case "redundant":
                    case "invalid":
                    default:
                        // don't change shots so they can try elsewhere
                        return;
                }

                repaint();
                if(game.isGameOver()) {
                    gameOver = true;
                    var winner = game.getWinner();
                    String winnerText;
                    if(winner == game.getPlayer1()) {
                        winnerText = "Player 1";
                    } else {
                        winnerText = "Player 2";
                    }

                    JOptionPane.showMessageDialog(
                            TargetBoardPanel.this,
                            "Game over! " + winnerText + " wins!",
                            "Battleship",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } else {
                    updateStatusLabel();
                }
            }
        });
    }

    private int[][] getCurrentShotsMatrix() {
        return (game.getCurrentPlayer() == game.getPlayer1() ? shotsP1 : shotsP2);
    }

    private void updateStatusLabel() {
        if (game.getCurrentPlayer() == game.getPlayer1()) {
            statusLabel.setText("Player 1's turn.");
        } else {
            statusLabel.setText("Player 2's turn.");
        }
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // grid
        g.setColor(Color.BLACK);
        for (int i = 0; i <= gridSize; i++) {
            g.drawLine(i * cellSize, 0, i * cellSize, gridSize * cellSize);
            g.drawLine(0, i * cellSize, gridSize * cellSize, i * cellSize);
        }

        int[][] shots = getCurrentShotsMatrix();

        // shots
        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                int x = c * cellSize;
                int y = r * cellSize;

                if (shots[r][c] == 1) { // miss
                    g.setColor(Color.BLUE);
                    int pad = cellSize / 4;
                    g.drawOval(x + pad, y + pad, cellSize - 2 * pad, cellSize - 2 * pad);
                } else if (shots[r][c] == 2) { // hit
                    g.setColor(Color.RED);
                    g.drawLine(x + 5, y + 5, x + cellSize - 5, y + cellSize - 5);
                    g.drawLine(x + cellSize - 5, y + 5, x + 5, y + cellSize - 5);
                }
            }
        }
    }
}

