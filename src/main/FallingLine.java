package main;

import java.awt.*;

public class FallingLine {
    public int x, y; // Position of the line
    public int height; // Height of the line
    public static final int WIDTH = 2; // Fixed width for all lines

    public FallingLine(int x, int y, int height) {
        this.x = x;
        this.y = y;
        this.height = height;
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.fillRect(x, y, WIDTH, height);
    }
}