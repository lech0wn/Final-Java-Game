package main;

import mino.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class PlayManager {

    Font font;

    //main play area
    final int WIDTH = Block.SIZE * 10;
    final int HEIGHT = Block.SIZE * 20;
    public static int left_x;
    public static int right_x;
    public static int top_y;
    public static int bottom_y;

    //mino
    Mino currentMino;
    final int MINO_START_X;
    final int MINO_START_Y;
    Mino nextMino;
    final int NEXTMINO_X;
    final int NEXTMINO_Y;
    public static ArrayList<Block> staticBlocks = new ArrayList<>();

    //7-bag system
    private SevenBag sevenBag = new SevenBag();

    //others
    public static int dropInterval = 60; //mino drops every 60 frames
    boolean gameOver;

    //hold system
    private Mino holdMino = null;
    private boolean canHold = true;

    //timer
    private int elapsedSeconds = 0;
    private Timer timer;

    //score
    int level = 1;
    int lines;
    int score;

    public PlayManager(){

        try {
            File font_file = new File("src/fonts/PressStart2P-Regular.ttf");
            font = Font.createFont(Font.TRUETYPE_FONT, font_file);
        } catch (Exception e) {
            font = new Font("Arial", Font.PLAIN, 12);
            e.printStackTrace();
        }

        left_x = (GamePanel.WIDTH/2) - (WIDTH/2);
        right_x = left_x + WIDTH;
        top_y = 50;
        bottom_y = top_y + HEIGHT;

        MINO_START_X = left_x + (WIDTH/2) - Block.SIZE;
        MINO_START_Y = top_y + Block.SIZE;

        NEXTMINO_X = right_x + 175;
        NEXTMINO_Y = top_y + 500;

        //set starting mino
        currentMino = pickMino();
        currentMino.setXY(MINO_START_X, MINO_START_Y);
        nextMino = sevenBag.getNextMino();
        nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);
    }

    private Mino pickMino(){
        return sevenBag.getNextMino();
    }

    private void startTimer() {
        // Create a timer that increments every second
        timer = new Timer(1000, e -> {
            elapsedSeconds++;
            System.out.println("Timer updated: " + elapsedSeconds); // Debugging
        });
        timer.start();
        System.out.println("Timer started"); // Debugging
    }

    private void resetTimer(){
        if(timer!= null){
            timer.stop();
        }
        elapsedSeconds = 0;
        startTimer();
    }

    public void update(){
        //check if currentMino is active
        if(!currentMino.active){
            canHold = true;
            //if mino is not active, put it into staticBlocks
            staticBlocks.add(currentMino.b[0]);
            staticBlocks.add(currentMino.b[1]);
            staticBlocks.add(currentMino.b[2]);
            staticBlocks.add(currentMino.b[3]);

            //check if the game is over
            if(currentMino.b[0].x == MINO_START_X && currentMino.b[0].y == MINO_START_Y){
                //this means that the currentMino immediately collided a block and could not move at all
                //so its xy are the same with the nextMino's
                gameOver = true;
                GamePanel.music.stop();
                GamePanel.se.play(2, false);
            }

            currentMino.deactivating = false;

            //replace currentMino with the nextMino
            currentMino = nextMino;
            currentMino.setXY(MINO_START_X, MINO_START_Y);
            nextMino = sevenBag.getNextMino();
            nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);

            //when a mino becomes inactive, check if line(s) can be deleted
            checkDelete();
        } else{
            currentMino.update();
        }
    }

    private void checkDelete(){
        int x = left_x;
        int y = top_y;
        int blockCount = 0;
        int lineCount = 0;

        while(x < right_x && y < bottom_y){

            for(int i = 0; i < staticBlocks.size(); i++){
                if(staticBlocks.get(i).x == x && staticBlocks.get(i).y == y){
                    //increase the count if there is a static block
                    blockCount++;
                }
            }

            x += Block.SIZE;

            if(x == right_x){

                //if the blockCount hits 10, that means the current y line is all filled with blocks
                //so we can delete them
                if(blockCount == 10){

                    for(int i = staticBlocks.size()-1; i > -1; i--){
                        //remove all the blocks in the current y line
                        if(staticBlocks.get(i).y == y){
                            staticBlocks.remove(i);
                        }
                    }

                    lineCount++;
                    lines++;

                    //drop speed
                    //if the line score hits a certain number, increase the drop speed
                    //1 is the fastest
                    if(lines % 4 == 0 && dropInterval > 1){

                        level++;
                        if(dropInterval > 20){
                            dropInterval -= 20;
                        } else{
                            dropInterval -= 1;
                        }
                    }

                    //a line has been deleted so need to slide down blocks that are above it
                    for(int i = 0; i < staticBlocks.size(); i++){
                        //if a block is above the current y, move it down by the block size
                        if(staticBlocks.get(i).y < y){
                            staticBlocks.get(i).y += Block.SIZE;
                        }
                    }
                }
                blockCount = 0;
                x = left_x;
                y += Block.SIZE;
            }
        }

        //add score
        if(lineCount > 0){
            GamePanel.se.play(1, false);
            int singleLineScore = 10 * level;
            score+= singleLineScore * lineCount;
        }
    }

    public void retryGame(){
        GamePanel.music.stop();
        GamePanel.music.play(0, true);
        GamePanel.music.loop();

        //clear all static blocks
        staticBlocks.clear();

        //reset current and next mino
        currentMino = pickMino();
        currentMino.setXY(MINO_START_X, MINO_START_Y);
        nextMino = pickMino();
        nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);

        //reset game state variables
        holdMino = null;
        gameOver = false;

        //reset score and lines
        lines = 0;
        score = 0;

        //reset level and drop interval
        level = 1;
        dropInterval = 60;

        //reset timer
        resetTimer();
    }

    public void dropMinoImmediately(){
        while(currentMino.active){
            currentMino.update();
        }
    }

    public void holdPiece() {
        if (!canHold) {
            return; //prevent holding more than once per drop
        }

        if (holdMino == null) {
            //if the hold slot is empty, move the currentMino to the hold slot
            holdMino = currentMino;
            currentMino = nextMino;
            currentMino.setXY(MINO_START_X, MINO_START_Y);
            nextMino = pickMino();
            nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);
        } else {
            //swap the currentMino with the holdMino
            Mino temp = holdMino;
            holdMino = currentMino;
            currentMino = temp;
            currentMino.setXY(MINO_START_X, MINO_START_Y);
        }

        canHold = false; //disable holding until the next tetromino drop
    }

    public void draw(Graphics2D g2){
        //play area frame
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(left_x-4, top_y-4, WIDTH+8, HEIGHT+8);

        //grid
        g2.setColor(new Color(200, 200, 200, 150));
        g2.setStroke(new BasicStroke(1f));
        for (int x = left_x; x <= right_x; x += Block.SIZE) {
            g2.drawLine(x, top_y, x, bottom_y);
        }
        for (int y = top_y; y <= bottom_y; y += Block.SIZE) {
            g2.drawLine(left_x, y, right_x, y);
        }

        //timer display
        g2.setColor(Color.white);
        g2.setFont(font.deriveFont(20f));
        int minutes = elapsedSeconds / 60;
        int seconds = elapsedSeconds % 60;
        String timeString = String.format("TIME: %02d:%02d", minutes, seconds);
        g2.drawString(timeString, left_x, top_y - 20);

        //next mino frame
        g2.setColor(Color.white);
        int x = right_x + 100;
        int y = bottom_y - 200;
        g2.drawRect(x, y, 200, 200);
        Font gameFont = font.deriveFont(20f);
        g2.setFont(gameFont);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString("NEXT", x+60, y+60);

        //hold mino frame
        int holdX = left_x - 300;
        int holdY = top_y + 50;
        g2.drawRect(holdX, holdY, 200, 200);
        g2.drawString("HOLD", holdX + 60, holdY - 10);

        //draw score frame
        g2.drawRect(x, top_y, 250, 300);
        x += 40;
        y = top_y + 90;
        g2.drawString("LEVEL: " + level, x, y); y += 70;
        g2.drawString("LINES: " + lines, x, y); y += 70;
        g2.drawString("SCORE: " + score, x, y);

        //draw hold mino
        if (holdMino != null) {
            holdMino.setXY(holdX + 50, holdY + 50); //center the hold piece
            holdMino.draw(g2);
        }

        //draw currentMIno
        if(currentMino != null){
            currentMino.draw(g2);
        }

        //draw next mino
        nextMino.draw(g2);


        //draw static blocks
        for (int i = 0; i < staticBlocks.size(); i++){
            staticBlocks.get(i).draw(g2);
        }

        //draw pause or game over
        g2.setColor(Color.yellow);
        g2.setFont(g2.getFont().deriveFont(50f));
        if(gameOver){
            x = left_x + 25;
            y = top_y + 320;
            g2.drawString("GAME OVER", x, y);
        }
        if(KeyHandler.pausePressed){
            x = left_x + 70;
            y = top_y + 320;
            g2.drawString("PAUSED", x, y);
        }

        //draw game title
        x = 35;
        y = top_y + 320;
        g2.setColor(Color.white);
        Font titleFont = font.deriveFont(60f);
        g2.setFont(titleFont);
        g2.drawString("TETRIS", x + 20, y + 50);

    }
}