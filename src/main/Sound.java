package main;

import java.net.URL;
import javax.sound.sampled.*;

public class Sound {
    Clip musicClip;
    URL[] url = new URL[10];

    public Sound(){
        url[0] = getClass().getResource("/res/background music.wav");
        url[1] = getClass().getResource("/res/delete line.wav");
        url[2] = getClass().getResource("/res/gameover.wav");
        url[3] = getClass().getResource("/res/rotation.wav");
        url[4] = getClass().getResource("/res/touch floor.wav");
    }

    public void play(int i, boolean music){
        try{
            AudioInputStream ais = AudioSystem.getAudioInputStream(url[i]);
            Clip clip = AudioSystem.getClip();

            if(music){
                musicClip = clip;
            }

            clip.open(ais);

            if(i == 0){
                FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                volumeControl.setValue(-20.0f);
            }

            clip.addLineListener(event -> {
                if(event.getType() == LineEvent.Type.STOP){
                    clip.close();
                }
            });
            ais.close();
            clip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loop() {
        if (musicClip != null) {
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
        } else {
            System.err.println("Error: musicClip is null. Ensure play() is called first.");
        }
    }

    public void stop() {
        if (musicClip != null) {
            musicClip.stop();
            musicClip.close();
        }
    }
}
