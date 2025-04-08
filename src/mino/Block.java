package mino;

import java.awt.*;

public class Block extends Rectangle {

    public int x, y;
    public static final int SIZE = 30;
    public Color c;

    public Block(Color c) {
        this.c = c;
    }

    public Block(Color c, int x, int y) {
        this.c = c;
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics2D g2) {
        g2.setColor(c);
        g2.fillRect(x, y, SIZE, SIZE);

        //draw black stroke
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(x, y, SIZE, SIZE);
    }
}