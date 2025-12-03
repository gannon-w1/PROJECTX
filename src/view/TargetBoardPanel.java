package view;

import model.Battleship;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.IOException;

public class TargetBoardPanel extends JPanel {
    private final int cellSize = 40;
    private final int gridSize = 10;

    private final int[][] shotsP1 = new int[gridSize][gridSize];
    private final int[][] shotsP2 = new int[gridSize][gridSize];

    private final Battleship game;
    private final JLabel statusLabel;
    private boolean gameOver = false;

    private Clip hitClip;
    private Clip missClip;
    private Clip sunkClip;

    private JPanel turnOverlay;
    private String currentTurnText = "";

    public TargetBoardPanel(Battleship game, JLabel statusLabel) {
        this.game = game;
        this.statusLabel = statusLabel;
        setPreferredSize(new Dimension(cellSize * gridSize, cellSize * gridSize));

        turnOverlay = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                //dims the background
                g.setColor(new Color(0,0,0,150));
                g.fillRect(0, 0, getWidth(), getHeight());

                //turn next
                g.setColor(Color.white);
                g.setFont(new Font("Serif", Font.BOLD, 36));

                String text = currentTurnText;
                int textWidth = g.getFontMetrics().stringWidth(text);

                g.drawString(text, (getWidth() - textWidth) / 2, getHeight() / 2);
            }
        };

        turnOverlay.setOpaque(false);
        turnOverlay.setVisible(true);

        turnOverlay.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                turnOverlay.setVisible(false);
                repaint();
            }
        });

        setLayout(null);
        turnOverlay.setBounds(0, 0, cellSize * gridSize, cellSize * gridSize);
        add(turnOverlay);


        //load sounds
        hitClip = loadClip("/view/sounds/Hit.wav");
        missClip = loadClip("/view/sounds/Miss.wav");
        sunkClip = loadClip("/view/sounds/Sunk.wav");

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
                        shots[row][col] = 2;
                        playClip(hitClip);
                        break;
                    case "sunk":
                        shots[row][col] = 2;   // sunk
                        playClip(sunkClip);
                        break;
                    case "miss":
                        shots[row][col] = 1;   // miss
                        playClip(missClip);

                        String nextPlayer = (game.getCurrentPlayer() == game.getPlayer1())
                                ? "Player 2"
                                : "Player 1";
                        currentTurnText = nextPlayer + "'s Turn";

                        new javax.swing.Timer(250, ev -> {
                            turnOverlay.setVisible(true);
                            turnOverlay.repaint();
                        }) {
                            {
                                setRepeats(false);
                            }}.start();
                        break;
                    case "redundant":
                    case "invalid":
                    default:
                        // don't change shots so they can try elsewhere
                        return;
                }

                repaint();
                if (game.isGameOver()) {
                    gameOver = true;
                    var winner = game.getWinner();
                    String winnerText = (winner == game.getPlayer1()) ? "Player 1" : "Player 2";

                    int choice = JOptionPane.showConfirmDialog(
                            TargetBoardPanel.this,
                            "Game over! " + winnerText + " wins!\nPlay again?",
                            "Battleship",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (choice == JOptionPane.YES_OPTION) {
                        // Find the window that contains this panel
                        java.awt.Window win = SwingUtilities.getWindowAncestor(TargetBoardPanel.this);
                        if (win instanceof BattleshipWindow) {
                            win.dispose();              // close old game window
                            new BattleshipWindow();     // start fresh game
                        }
                    } else {
                        // No = just leave the final board shown
                        statusLabel.setText("Game over! " + winnerText + " wins.");
                    }
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

    private Clip loadClip(String path) {
        try {
            AudioInputStream audioIn =
                    AudioSystem.getAudioInputStream(getClass().getResource(path));
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void playClip(Clip clip) {
        if(clip == null) return;
        if (clip.isRunning()) {
            clip.stop();
        }
        clip.setFramePosition(0);
        clip.start();
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

