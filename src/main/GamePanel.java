package main;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class GamePanel extends JPanel implements Runnable {
    Font font;

    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    final int FPS = 60;
    Thread gameThread;
    public static Sound music = new Sound();
    public static Sound se = new Sound();

    PlayManager pm;
    private final JFrame window; // Reference to the main JFrame

    public GamePanel(JFrame window) {
        this.window = window; // Assign the JFrame to the window variable

        try {
            File font_file = new File("src/fonts/PressStart2P-Regular.ttf");
            font = Font.createFont(Font.TRUETYPE_FONT, font_file);
        } catch (Exception e) {
            font = new Font("Arial", Font.PLAIN, 12);
            e.printStackTrace();
        }

        // Panel settings
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.BLACK);
        this.setLayout(null);

        // Ensure the panel is focusable
        this.setFocusable(true);
        this.requestFocusInWindow();

        // Implement key listener
        this.addKeyListener(new KeyHandler());

        // Initialize PlayManager
        pm = new PlayManager();

        // Add Back button
        JButton backButton = new JButton("BACK");
        backButton.setBounds(20, 20, 110, 30);
        backButton.setFocusPainted(false);
        backButton.setBackground(Color.BLACK);
        backButton.setForeground(Color.WHITE);
        Font backFont = font.deriveFont(12f);
        backButton.setFont(backFont);
        backButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        backButton.addActionListener(e -> backToMainMenu());
        this.add(backButton);
    }

    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();

        music.play(0, true);
        music.loop();
    }

    @Override
    public void run() {
        // Game loop
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    private void update() {
        if (KeyHandler.retryPressed) {
            pm.retryGame();
            KeyHandler.retryPressed = false;
        } else if (!KeyHandler.pausePressed && !pm.gameOver) {
            if (KeyHandler.spacePressed) {
                pm.dropMinoImmediately();
                KeyHandler.spacePressed = false;
            } else if (KeyHandler.holdPressed) {
                pm.holdPiece();
                KeyHandler.holdPressed = false;
            } else {
                pm.update();
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        pm.draw(g2);
    }

    private void backToMainMenu() {
        // Stop the game thread
        gameThread = null;

        // Stop the music
        music.stop();

        // Remove the GamePanel and show the MainMenu
        window.getContentPane().removeAll();
        try {
            MainMenu mainMenu = new MainMenu(window);
            window.add(mainMenu);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }

        window.revalidate();
        window.repaint();
    }
}