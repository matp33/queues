

package animations;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;


public class Animation {

    private int frameCounter;
    private int frameDelay;
    private int currentFrame;
    private int animationDirection; // -1 lub 1
    private int numberOfFrames;

    private boolean isStopped;
    private List<Frame> framesList= new ArrayList<Frame>();

    public Animation(BufferedImage [] frames, int delay) throws Exception{

        this.frameDelay=delay;
        isStopped=true;

        for (int i=0; i<frames.length;i++){
            if (delay<0){
                throw new Exception ("Delay must be positive integer. Current delay is not positive.");
            }
            this.framesList.add(new Frame (frames[i],delay));
//            obecnyFrame=0;
        }

        this.numberOfFrames=0;
        this.currentFrame=0;
        this.animationDirection=1;
        this.numberOfFrames=this.framesList.size();

    }

    public void start(){
        if (!isStopped){
            return;
        }

        if (framesList.size()==0){
            return;
        }

        isStopped=false;
    }

    public void stop(){
        if (framesList.size()==0){
            return;
        }

        isStopped=true;
    }

    public void restart(){
        if (framesList.size()==0){
            return;
        }
        isStopped=false;
        currentFrame=0;
    }

    public void reset(){
        isStopped=true;
        frameCounter=0;
        currentFrame=0;
    }
    
    public void reverseDirection(){
    	if (animationDirection==1)
    	animationDirection=-1;
    }
    
    public void normalDirection(){
    	if (animationDirection==-1)
    	animationDirection=1;
    }

    public BufferedImage getSprite(){
    	if (currentFrame < 0){
    		return framesList.get(numberOfFrames-1).getFrame();
    	}
    	if (currentFrame > numberOfFrames-1){
    		return framesList.get(0).getFrame();
    	}
        return framesList.get(currentFrame).getFrame();
    }

    public void updateFrame(){

        if (!isStopped){
            frameCounter++;

            if (frameCounter>frameDelay){
                frameCounter=0;
                currentFrame+=animationDirection;
                if(currentFrame>numberOfFrames-1){
                    currentFrame=0;
                }
                else if (currentFrame<0){
                    currentFrame=numberOfFrames-1;
                }
            }
        }
    }

  public int getAnimationDelay(){
      return frameDelay;
  }
  
  public boolean isInitialFrame(){
	  return currentFrame==0;
  }
  
  public boolean isFinalFrame(){
//	  System.out.println("###"+currentFrame);
	  return currentFrame==numberOfFrames-1;
  }
  
  public int getCurrentFrame(){
	  return currentFrame;
  }
  
  public void setLastFrame(){
	  int size=framesList.size();
	  currentFrame=size-1;
  }

}
