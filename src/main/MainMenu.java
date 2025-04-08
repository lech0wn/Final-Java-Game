package main;

import mino.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class MainMenu extends JPanel {
    Font font;

    Color buttonColor = Color.decode("#1E1E1E");
    Color textColor = Color.decode("#FFFFFF");

    private final JFrame window;

    // Falling Mino pieces
    private final ArrayList<Mino> fallingMinos = new ArrayList<>();
    private final Random random = new Random();
    private final Timer timer;

    private final ArrayList<FallingLine> fallingLines = new ArrayList<>();

    public MainMenu(JFrame window) throws IOException, FontFormatException {
        this.window = window;

        try {
            File font_file = new File("src/fonts/PressStart2P-Regular.ttf");
            font = Font.createFont(Font.TRUETYPE_FONT, font_file);
        } catch (Exception e) {
            font = new Font("Arial", Font.PLAIN, 12); // Fallback font
            e.printStackTrace();
        }

        this.setPreferredSize(new Dimension(GamePanel.WIDTH, GamePanel.HEIGHT));
        this.setBackground(Color.BLACK);
        this.setLayout(null);

        // Initialize falling Mino pieces
        initializeFallingMinos();
        initializeFallingLines();

        // Start the animation timer
        timer = new Timer(50, e -> {
            updateFallingMinos();
            updateFallingLines();
        });
        timer.start();

        // Title label
        JLabel titleLabel = new JLabel("TETRIS");
        Font mainFont = font.deriveFont(64f);
        titleLabel.setFont(mainFont);
        titleLabel.setForeground(textColor);
        titleLabel.setBounds(454, 111, 384, 64);
        add(titleLabel);

        // Play button
        JButton playButton = new JButton("PLAY");
        Font playFont = font.deriveFont(32f);
        playButton.setFont(playFont);
        playButton.setFocusPainted(false);
        playButton.setBackground(buttonColor);
        playButton.setForeground(textColor);
        playButton.setBounds(525, 248, 242, 92);
        playButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        playButton.addActionListener(e -> {
            try {
                startGame();
            } catch (IOException | FontFormatException ex) {
                throw new RuntimeException(ex);
            }
        });
        this.add(playButton);

        // Instructions button
        JButton instructionsButton = new JButton("INSTRUCTIONS");
        Font menuFonts = font.deriveFont(14f);
        instructionsButton.setFont(menuFonts);
        instructionsButton.setBackground(buttonColor);
        instructionsButton.setFocusPainted(false);
        instructionsButton.setForeground(textColor);
        instructionsButton.setBounds(552, 359, 188, 71);
        instructionsButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        instructionsButton.addActionListener(e -> showInstructions());
        this.add(instructionsButton);

        // Quit button
        JButton quitButton = new JButton("QUIT");
        quitButton.setFont(menuFonts);
        quitButton.setFocusPainted(false);
        quitButton.setBackground(buttonColor);
        quitButton.setForeground(textColor);
        quitButton.setBounds(552, 454, 188, 71);
        quitButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        quitButton.addActionListener(e -> System.exit(0));
        this.add(quitButton);
    }

    private void initializeFallingLines() {
        for (int i = 0; i < 20; i++) { // Add 20 lines
            int x = random.nextInt(GamePanel.WIDTH); // Random x position
            int y = -random.nextInt(GamePanel.HEIGHT); // Start above the screen
            int height = randomHeight(); // Random height (5, 8, or 12)
            fallingLines.add(new FallingLine(x, y, height));
        }
    }

    private int randomHeight() {
        int[] heights = {5, 10, 15, 20};
        return heights[random.nextInt(heights.length)];
    }

    private void updateFallingLines() {
        for (FallingLine line : fallingLines) {
            line.y += 5; // Move down by 5 pixels

            // If the line falls off the screen, reset it
            if (line.y > GamePanel.HEIGHT) {
                line.y = -line.height; // Reset to above the screen
                line.x = random.nextInt(GamePanel.WIDTH); // Random x position
                line.height = randomHeight(); // Random height
            }
        }
    }

    private void initializeFallingMinos() {
        Mino_L1 minoL1 = new Mino_L1();
        minoL1.setXY(random.nextInt(GamePanel.WIDTH / Block.SIZE) * Block.SIZE, -random.nextInt(GamePanel.HEIGHT));
        fallingMinos.add(minoL1);

        Mino_L2 minoL2 = new Mino_L2();
        minoL2.setXY(random.nextInt(GamePanel.WIDTH / Block.SIZE) * Block.SIZE, -random.nextInt(GamePanel.HEIGHT));
        fallingMinos.add(minoL2);

        Mino_Bar minoBar = new Mino_Bar();
        minoBar.setXY(random.nextInt(GamePanel.WIDTH / Block.SIZE) * Block.SIZE, -random.nextInt(GamePanel.HEIGHT));
        fallingMinos.add(minoBar);

        Mino_Square minoSquare = new Mino_Square();
        minoSquare.setXY(random.nextInt(GamePanel.WIDTH / Block.SIZE) * Block.SIZE, -random.nextInt(GamePanel.HEIGHT));
        fallingMinos.add(minoSquare);

        Mino_T minoT = new Mino_T();
        minoT.setXY(random.nextInt(GamePanel.WIDTH / Block.SIZE) * Block.SIZE, -random.nextInt(GamePanel.HEIGHT));
        fallingMinos.add(minoT);

        Mino_Z1 minoZ1 = new Mino_Z1();
        minoZ1.setXY(random.nextInt(GamePanel.WIDTH / Block.SIZE) * Block.SIZE, -random.nextInt(GamePanel.HEIGHT));
        fallingMinos.add(minoZ1);

        Mino_Z2 minoZ2 = new Mino_Z2();
        minoZ2.setXY(random.nextInt(GamePanel.WIDTH / Block.SIZE) * Block.SIZE, -random.nextInt(GamePanel.HEIGHT));
        fallingMinos.add(minoZ2);
    }

    private void updateFallingMinos() {
        // Update the position of each falling Mino
        for (Mino mino : fallingMinos) {
            for (Block block : mino.b) {
                block.y += 5; // Move down by 5 pixels
            }

            // If the Mino falls off the screen, reset it
            if (mino.b[0].y > GamePanel.HEIGHT) {
                if (mino instanceof Mino_L1) {
                    Mino_L1 newMino = new Mino_L1();
                    newMino.setXY(random.nextInt(GamePanel.WIDTH / Block.SIZE) * Block.SIZE, -Block.SIZE);
                    fallingMinos.set(fallingMinos.indexOf(mino), newMino);
                } else if (mino instanceof Mino_L2) {
                    Mino_L2 newMino = new Mino_L2();
                    newMino.setXY(random.nextInt(GamePanel.WIDTH / Block.SIZE) * Block.SIZE, -Block.SIZE);
                    fallingMinos.set(fallingMinos.indexOf(mino), newMino);
                } else if (mino instanceof Mino_Bar) {
                    Mino_Bar newMino = new Mino_Bar();
                    newMino.setXY(random.nextInt(GamePanel.WIDTH / Block.SIZE) * Block.SIZE, -Block.SIZE);
                    fallingMinos.set(fallingMinos.indexOf(mino), newMino);
                } else if (mino instanceof Mino_Square) {
                    Mino_Square newMino = new Mino_Square();
                    newMino.setXY(random.nextInt(GamePanel.WIDTH / Block.SIZE) * Block.SIZE, -Block.SIZE);
                    fallingMinos.set(fallingMinos.indexOf(mino), newMino);
                } else if (mino instanceof Mino_T) {
                    Mino_T newMino = new Mino_T();
                    newMino.setXY(random.nextInt(GamePanel.WIDTH / Block.SIZE) * Block.SIZE, -Block.SIZE);
                    fallingMinos.set(fallingMinos.indexOf(mino), newMino);
                } else if (mino instanceof Mino_Z1) {
                    Mino_Z1 newMino = new Mino_Z1();
                    newMino.setXY(random.nextInt(GamePanel.WIDTH / Block.SIZE) * Block.SIZE, -Block.SIZE);
                    fallingMinos.set(fallingMinos.indexOf(mino), newMino);
                } else if (mino instanceof Mino_Z2) {
                    Mino_Z2 newMino = new Mino_Z2();
                    newMino.setXY(random.nextInt(GamePanel.WIDTH / Block.SIZE) * Block.SIZE, -Block.SIZE);
                    fallingMinos.set(fallingMinos.indexOf(mino), newMino);
                }
            }
        }
        repaint(); // Repaint the panel to show the updated positions
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // Draw the falling Minos
        for (Mino mino : fallingMinos) {
            mino.draw(g2);
        }

        // Draw the falling lines
        for (FallingLine line : fallingLines) {
            line.draw(g2);
        }
    }

    private void startGame() throws IOException, FontFormatException {
        // Stop the timer
        timer.stop();

        // Remove the main menu and start the game
        window.getContentPane().removeAll();
        GamePanel gamePanel = new GamePanel(window); // Pass the JFrame to the GamePanel
        window.add(gamePanel);

        window.setSize(GamePanel.WIDTH, GamePanel.HEIGHT);
        window.setLocationRelativeTo(null);

        window.revalidate();
        gamePanel.requestFocusInWindow(); // Ensure the GamePanel gets focus
        gamePanel.launchGame();
    }

    private void showInstructions() {
        // Show a dialog with instructions
        JOptionPane.showMessageDialog(
                window,
                "Instructions:\n" +
                        "- Use arrow keys to move the pieces.\n" +
                        "- Press 'Space' to drop the piece immediately.\n" +
                        "- Press 'Shift' to hold a piece.\n" +
                        "- Press 'R' to restart the game.\n" +
                        "- Clear lines to score points!",
                "Instructions",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}