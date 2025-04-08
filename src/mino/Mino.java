package mino;

import main.GamePanel;
import main.KeyHandler;
import main.PlayManager;

import java.awt.*;

public class Mino {
    public Block[] b = new Block[4];
    public Block[] tempB = new Block[4];
    int autoDropCounter = 0;
    public int direction = 1; //there are 4 directions (1/2/3/4)
    boolean leftCollision, rightCollision, bottomCollision;
    public boolean active = true;
    public boolean deactivating;
    int deactivateCounter = 0;

    public void create(Color c) {
        b[0] = new Block(c);
        b[1] = new Block(c);
        b[2] = new Block(c);
        b[3] = new Block(c);
        tempB[0] = new Block(c);
        tempB[1] = new Block(c);
        tempB[2] = new Block(c);
        tempB[3] = new Block(c);
    }

    public void setXY(int x, int y) {
        b[0].x = x;
        b[0].y = y;
        b[1].x = x + Block.SIZE;
        b[1].y = y;
        b[2].x = x;
        b[2].y = y + Block.SIZE;
        b[3].x = x + Block.SIZE;
        b[3].y = y + Block.SIZE;
    }

    public void updateXY(int direction){

        checkRotationCollision();

        if(!leftCollision && !rightCollision && !bottomCollision){
            this.direction = direction;
            b[0].x = tempB[0].x;
            b[0].y = tempB[0].y;
            b[1].x = tempB[1].x;
            b[1].y = tempB[1].y;
            b[2].x = tempB[2].x;
            b[2].y = tempB[2].y;
            b[3].x = tempB[3].x;
            b[3].y = tempB[3].y;
        }
    }

    public void getDirection1(){}
    public void getDirection2(){}
    public void getDirection3(){}
    public void getDirection4(){}

    public void checkMovementCollision(){

        leftCollision = false;
        rightCollision = false;
        bottomCollision = false;

        //check static block collision
        checkStaticBlockCollision();

        //check frame collision
        //left wall
        for (Block item : b) {
            if (item.x == PlayManager.left_x) {
                leftCollision = true;
            }
        }

        //right wall
        for (Block value : b) {
            if (value.x + Block.SIZE == PlayManager.right_x) {
                rightCollision = true;
            }
        }

        //bottom floor
        for (Block block : b) {
            if (block.y + Block.SIZE == PlayManager.bottom_y) {
                bottomCollision = true;
            }
        }
    }
    public void checkRotationCollision(){
        leftCollision = false;
        rightCollision = false;
        bottomCollision = false;

        //check static block collision
        checkStaticBlockCollision();

        //check frame collision
        //left wall
        for (int i = 0; i < b.length; i++) {
            if (tempB[i].x < PlayManager.left_x) {
                leftCollision = true;
            }
        }

        //right wall
        for (int i = 0; i < b.length; i++) {
            if (tempB[i].x + Block.SIZE > PlayManager.right_x) {
                rightCollision = true;
            }
        }

        //bottom floor
        for (int i = 0; i < b.length; i++) {
            if (tempB[i].y + Block.SIZE > PlayManager.bottom_y) {
                bottomCollision = true;
            }
        }
    }

    private void checkStaticBlockCollision(){
        for(int i = 0; i < PlayManager.staticBlocks.size(); i++){
            int targetX = PlayManager.staticBlocks.get(i).x;
            int targetY = PlayManager.staticBlocks.get(i).y;

            //check down
            for(int j = 0; j < b.length; j++){
                if(b[j].y + Block.SIZE == targetY && b[j].x == targetX){
                    bottomCollision = true;
                }
            }

            //check left
            for(int j = 0; j < b.length; j++){
                if(b[j].y + Block.SIZE == targetY && b[j].x == targetX){
                    leftCollision = true;
                }
            }

            //check right
            for(int j = 0; j < b.length; j++){
                if(b[j].y + Block.SIZE == targetY && b[j].x == targetX){
                    rightCollision = true;
                }
            }
        }
    }

    public void update(){
        if(deactivating){
            deactivating();
        }

        //move mino
        if(KeyHandler.upPressed){
            switch (direction){
                case 1: getDirection2();break;
                case 2: getDirection3();break;
                case 3: getDirection4();break;
                case 4: getDirection1();break;
            }
            KeyHandler.upPressed = false;
            GamePanel.se.play(3, false);
        }

        checkMovementCollision();

        if(KeyHandler.downPressed){
            //if mino's bottom is not hitting, it can go down
            if(!bottomCollision){
                b[0].y += Block.SIZE;
                b[1].y += Block.SIZE;
                b[2].y += Block.SIZE;
                b[3].y += Block.SIZE;

                //when moved down, reset autoDropCounter
                autoDropCounter = 0;
            }
            KeyHandler.downPressed = false;
        }

        if(KeyHandler.leftPressed){
            if(!leftCollision){
                b[0].x -= Block.SIZE;
                b[1].x -= Block.SIZE;
                b[2].x -= Block.SIZE;
                b[3].x -= Block.SIZE;
            }

            KeyHandler.leftPressed = false;
        }

        if(KeyHandler.rightPressed){
            if(!rightCollision){
                b[0].x += Block.SIZE;
                b[1].x += Block.SIZE;
                b[2].x += Block.SIZE;
                b[3].x += Block.SIZE;
            }
            KeyHandler.rightPressed = false;
        }

        if(bottomCollision){
            if(deactivating == false){
                GamePanel.se.play(4, false);
            }
            deactivating = true;
        } else{
            autoDropCounter++; //counter increases every frame
            if(autoDropCounter == PlayManager.dropInterval){
                //mino goes down
                b[0].y += Block.SIZE;
                b[1].y += Block.SIZE;
                b[2].y += Block.SIZE;
                b[3].y += Block.SIZE;
                autoDropCounter = 0;
            }
        }
    }

    private void deactivating(){
        deactivateCounter++;

        //wait 45 frames until deactivate
        if(deactivateCounter == 45){

            deactivateCounter = 0;
            checkMovementCollision(); //check if bottom is still hitting

            //if the bottom is still hitting after 45 frames, deactivate the mino
            if(bottomCollision){
                active = false;
            }
        }
    }

    public void draw(Graphics2D g2) {
        b[0].draw(g2);
        b[1].draw(g2);
        b[2].draw(g2);
        b[3].draw(g2);
    }
}
