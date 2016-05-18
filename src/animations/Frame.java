

package animations;

import java.awt.image.BufferedImage;

public class Frame {

    private BufferedImage frame;
    private int duration;

    public Frame (BufferedImage frame, int duration){
        this.frame=frame;
        this.duration=duration;
    }

    public BufferedImage getFrame(){
        return frame;
    }

    public void ustawFrame(BufferedImage frame){
        this.frame=frame;
    }

    public int getDuration(){
        return duration;
    }

    public void setDuration(int duration){
        this.duration=duration;
    }



}
