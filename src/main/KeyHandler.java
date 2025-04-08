package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    //movement keys
    public static boolean upPressed, downPressed, leftPressed, rightPressed;

    //game keys
    public static boolean pausePressed, retryPressed, spacePressed, holdPressed;

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if(code == KeyEvent.VK_UP){
            upPressed = true;
        }

        if(code == KeyEvent.VK_LEFT){
            leftPressed = true;
        }

        if(code == KeyEvent.VK_DOWN){
            downPressed = true;
        }

        if(code == KeyEvent.VK_RIGHT){
            rightPressed = true;
        }

        if(code == KeyEvent.VK_ESCAPE){
            if(pausePressed){
                pausePressed = false;
                GamePanel.music.play(0, true);
                GamePanel.music.loop();
            } else{
                pausePressed = true;
                GamePanel.music.stop();
            }
        }

        if(code == KeyEvent.VK_SPACE){
            spacePressed = true;
        }

        if(code == KeyEvent.VK_R){
            retryPressed = true;
        }

        if(code == KeyEvent.VK_SHIFT){
            holdPressed = true;
        }


    }

    @Override
    public void keyReleased(KeyEvent e) {}
}
