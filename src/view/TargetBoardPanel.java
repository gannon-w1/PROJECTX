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

    private final Clip hitClip;
    private final Clip missClip;
    private final Clip sunkClip;

    private final JPanel turnOverlay;
    private String currentTurnText = "";

    //flash animation for shots
    private int flashRow = -1, flashCol = -1;
    private float flashAlpha = 0f;
    private javax.swing.Timer flashTimer;

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
                        startFlash(row,col);
                        break;
                    case "sunk":
                        playClip(sunkClip);
                        for (java.awt.Point p : game.getLastSunkCells()) {
                            int r = p.y; // row is y
                            int c = p.x; // col is x
                            shots[r][c] = 3;   // 3 = sunk (filled red)
                        }
                        startFlash(row,col);
                        break;
                    case "miss":
                        shots[row][col] = 1;   // miss
                        playClip(missClip);
                        startFlash(row,col);

                        String nextPlayer = (game.getCurrentPlayer() == game.getPlayer1())
                                ? "Player 1"
                                : "Player 2";
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

                    java.awt.Window win = SwingUtilities.getWindowAncestor(TargetBoardPanel.this);
                    if (choice == JOptionPane.YES_OPTION) {
                        // Find the window that contains this panel
                        if (win instanceof BattleshipWindow) {
                            win.dispose();              // close old game window
                            new BattleshipWindow();     // start fresh game
                        }
                    } else {
                        // No = just leave the final board shown
                        win.dispose();
                    }
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

    private void startFlash(int row, int col) {
        flashRow = row;
        flashCol = col;
        flashAlpha = 0.8f;  // starting opacity (0–1)

        // stop any current flash animation
        if (flashTimer != null && flashTimer.isRunning()) {
            flashTimer.stop();
        }

        flashTimer = new javax.swing.Timer(40, e -> {
            flashAlpha -= 0.1f;       // fade out
            if (flashAlpha <= 0f) {
                flashAlpha = 0f;
                flashRow = -1;
                flashCol = -1;
                flashTimer.stop();
            }
            repaint();
        });
        flashTimer.setRepeats(true);
        flashTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 =  (Graphics2D) g;

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

        int[][] shots = getCurrentShotsMatrix();

        // shots
        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                int x = c * cellSize;
                int y = r * cellSize;

                if (shots[r][c] == 1) { // miss
                    g2.setColor(Color.BLUE);
                    int pad = cellSize / 4;
                    g2.drawOval(x + pad, y + pad, cellSize - 2 * pad, cellSize - 2 * pad);
                } else if (shots[r][c] == 2) { // hit
                    g2.setColor(Color.RED);
                    g2.drawLine(x + 5, y + 5, x + cellSize - 5, y + cellSize - 5);
                    g2.drawLine(x + cellSize - 5, y + 5, x + 5, y + cellSize - 5);
                } else if (shots[r][c] == 3) {
                    g2.setColor(Color.RED);
                    g2.fillRect(x + 1, y + 1, cellSize - 2, cellSize - 2);
                }
            }
        }
        // draw flash overlay for the last shot
        if (flashRow >= 0 && flashCol >= 0 && flashAlpha > 0f) {
            int x = flashCol * cellSize;
            int y = flashRow * cellSize;

            Graphics2D g2d = (Graphics2D) g2.create();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, flashAlpha));
            g2d.setColor(Color.WHITE);
            g2d.fillRect(x + 1, y + 1, cellSize - 2, cellSize - 2);
            g2d.dispose();
        }
    }
}

