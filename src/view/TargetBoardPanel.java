package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.sound.sampled.*;
import javax.swing.*;
import model.Battleship;
import model.Computer;

public class TargetBoardPanel extends JPanel {
    private final int cellSize = 40;
    private final int gridSize = 10;

    private final int[][] shotsP1 = new int[gridSize][gridSize];
    private final int[][] shotsP2 = new int[gridSize][gridSize];
    private final int[][] opponentShotsOnP1 = new int[gridSize][gridSize];  // where P2/Computer has fired at P1
    private final int[][] opponentShotsOnP2 = new int[gridSize][gridSize];  // where P1 has fired at P2

    private final Battleship game;
    private final JLabel statusLabel;
    private boolean gameOver = false;

    private final Clip hitClip;
    private final Clip missClip;
    private final Clip sunkClip;

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

                // Determine which opponent board to check (where this player is firing)
                int[][] opponentShots = (game.getCurrentPlayer() == game.getPlayer1()) ? opponentShotsOnP2 : opponentShotsOnP1;
                
                // already shot here
                if (opponentShots[row][col] != 0) {
                    return;
                }


                String result = game.fireAt(row, col);
                System.out.println("Result from backend: " + result);

                switch (result) {
                    case "hit":
                        opponentShots[row][col] = 2;
                        playClip(hitClip);
                        break;
                    case "sunk":
                        opponentShots[row][col] = 2;   // sunk
                        playClip(sunkClip);
                        markSunkShip(row, col, opponentShots); //fills whole ship with red when sunk
                        break;
                    case "miss":
                        opponentShots[row][col] = 1;   // miss
                        playClip(missClip);

                        String nextPlayer = (game.getCurrentPlayer() == game.getPlayer1())
                                ? "Player 1"
                                : "Computer";
                        currentTurnText = nextPlayer + "'s Turn";

                        new javax.swing.Timer(250, ev -> {
                            turnOverlay.setVisible(true);
                            turnOverlay.repaint();
                        }) {
                            {
                                setRepeats(false);
                            }
                        }.start();

                        // If it's now the computer's turn, execute its move after delay
                        if (game.getCurrentPlayer() instanceof Computer) {
                            new javax.swing.Timer(1500, ev -> {
                                if (!gameOver) {
                                    turnOverlay.setVisible(false);
                                    executeComputerMove();
                                }
                            }) {
                                {
                                    setRepeats(false);
                                }
                            }.start();
                        }
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

    private void markSunkShip(int row, int col, int[][] shots) {
        shots[row][col] = 3;

        markDirection(row, col, -1, 0, shots); //up
        markDirection(row, col,  1, 0, shots); // down
        markDirection(row, col,  0,-1, shots); // left
        markDirection(row, col,  0, 1, shots); // right
    }

    private void markDirection(int row, int col, int dr, int dc, int[][] shots) {
        int r = row + dr;
        int c = col + dc;
        int size = shots.length;

        while (r >= 0 && r < size && c >= 0 && c < size && shots[r][c] == 2) {
            shots[r][c] = 3;
            r += dr;
            c += dc;
        }
    }

    private void executeComputerMove() {
        Computer computer = (Computer) game.getCurrentPlayer();
        int[] target = computer.selectTarget();
        int row = target[0];
        int col = target[1];

        String result = game.fireAt(row, col);
        System.out.println("Computer fired at [" + row + ", " + col + "]: " + result);
        // inform AI of the result so it can update its targeting
        computer.notifyResult(row, col, result);

        // Computer is Player 2, so record on opponentShotsOnP1 (shots at Player 1's board)
        int[][] opponentShots = opponentShotsOnP1;
        
        switch (result) {
            case "hit":
                opponentShots[row][col] = 2;
                playClip(hitClip);
                break;
            case "sunk":
                opponentShots[row][col] = 2;
                playClip(sunkClip);
                markSunkShip(row, col, opponentShots);
                break;
            case "miss":
                opponentShots[row][col] = 1;
                playClip(missClip);
                break;
        }

        repaint();
        if (game.isGameOver()) {
            gameOver = true;
            var winner = game.getWinner();
            String winnerText = (winner == game.getPlayer1()) ? "Player 1" : "Computer";

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Game over! " + winnerText + " wins!\nPlay again?",
                    "Battleship",
                    JOptionPane.YES_NO_OPTION
            );

            java.awt.Window win = SwingUtilities.getWindowAncestor(this);
            if (choice == JOptionPane.YES_OPTION) {
                if (win instanceof BattleshipWindow) {
                    win.dispose();
                    new BattleshipWindow();
                }
            } else {
                win.dispose();
            }
        } else if (game.getCurrentPlayer() instanceof Computer && !result.equals("miss")) {
            // Computer hit - it gets another turn
            updateStatusLabel();
            new javax.swing.Timer(1000, ev -> {
                if (!gameOver) {
                    executeComputerMove();
                }
            }) {
                {
                    setRepeats(false);
                }
            }.start();
        } else {
            // Player's turn
            updateStatusLabel();
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

        // Display the player's shots on the opponent's board
        int[][] boardToDisplay;
        if (game.getCurrentPlayer() == game.getPlayer1()) {
            // P1's turn: show P1's shots on P2's board (opponentShotsOnP2)
            boardToDisplay = opponentShotsOnP2;
        } else {
            // P2/Computer's turn: show P2's shots on P1's board (opponentShotsOnP1)
            boardToDisplay = opponentShotsOnP1;
        }

        // shots (this player's attacks on opponent's board)
        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                int x = c * cellSize;
                int y = r * cellSize;

                if (boardToDisplay[r][c] == 1) { // miss
                    g.setColor(Color.BLUE);
                    int pad = cellSize / 4;
                    g.drawOval(x + pad, y + pad, cellSize - 2 * pad, cellSize - 2 * pad);
                } else if (boardToDisplay[r][c] == 2) { // hit
                    g.setColor(Color.RED);
                    g.drawLine(x + 5, y + 5, x + cellSize - 5, y + cellSize - 5);
                    g.drawLine(x + cellSize - 5, y + 5, x + 5, y + cellSize - 5);
                } else if (boardToDisplay[r][c] == 3) {
                    g.setColor(Color.RED);
                    g.fillRect(x + 1, y + 1, cellSize - 2, cellSize - 2);
                }
            }
        }
    }
}

