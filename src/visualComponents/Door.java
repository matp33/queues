package visualComponents;


import interfaces.AnimatedAndObservable;
import interfaces.AnimatedObject;
import interfaces.Observable;
import interfaces.Observer;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import animations.Sprite;
import symulation.Painter;

public class Door extends AnimatedAndObservable  {
	
	private static final long serialVersionUID = 1L;
	
	private Timer timer;
	private final int delay=10;
	private List <Observer> observers;
	
	public static int STATE_CLOSING=-1;
	public static int STATE_NEUTRAL=0; // nothing
	public static int STATE_OPENING=1;
	
	private boolean isNotified;
	
	
	
		public Door(Sprite sprite, int frameDelay, Painter painter){
			super(sprite,painter);
				
			state=STATE_NEUTRAL;
			observers = new ArrayList <Observer>();
			isNotified=false;
			
		}
	
	protected void initializePosition (){
		System.out.println("1"+position);
		System.out.println("2"+painter);
		position=painter.getDoorPosition();		
	}
	
//	protected void changeAnimation(){			
//		currentAnimation.start();
//        currentAnimation.updateFrame();
//	}
	
	
	
	public void doOpening(){	
		isNotified=false;
		state=STATE_OPENING;
		currentAnimation.normalDirection();
		currentAnimation.start();		
		scheduleTask();			
	}
	
	public void doClosing(){		
		state=STATE_CLOSING;
		currentAnimation.reverseDirection();
		currentAnimation.start();		
		scheduleTask();	
	}
	
	public void stopOpening(){		
		timer.cancel();
		currentAnimation.stop();
	}
	
	public void interrupt(){		
		if (state!=STATE_NEUTRAL){		
			stopOpening();
		}
		
	}
	
	public void resume(){
		
		if (state==STATE_OPENING){
			doOpening();
		}
		else if (state==STATE_CLOSING){
			doClosing();
		}
	}
	
	
	
	private void scheduleTask(){
		
		if (timer!=null){
			timer.cancel();
		}
		
		TimerTask tt = new TimerTask(){			
			public void run(){		
				
				currentAnimation.updateFrame();	
//				Dimension d= painter.getDoorPosition();
//				painter.door.repaint();
				
				if (canClientEnter() && isNotified==false){
					notifyClients();
					
					isNotified=true;
				}
				
//				if (currentAnimation.isInitialFrame()){
//					isNotified=false;
//				}
				
				if ((state==STATE_OPENING && currentAnimation.isFinalFrame())
						|| (state==STATE_CLOSING && currentAnimation.isInitialFrame())){
					timer.cancel();
					timer.purge();
					if (state==STATE_OPENING){
						doClosing();
					}
				}
			}			
		};
		
		timer=new Timer();
		timer.schedule(tt, 0,delay);
				
	}
	
	public void start(){
        initializePosition();   
	}
	
//	public BufferedImage getImage(){
//		return currentAnimation.getSprite();
//	}


	public boolean canClientEnter(){
		if (currentAnimation.getCurrentFrame()==3 && state == STATE_OPENING ){
			return true;
		}
		else{
			return false;
		}
	}
	
	 public void notifyClients(){ 
		 
	     	Client c1=(Client)observers.get(0);
	     	c1.moveOutside();
//	     	if (!c1.isMoving()){
	     		for (int i=1; i<observers.size();i++){
	     			Client c=(Client)observers.get(i);
	     			if (c.getClientNumber()>0){
		     			c.setClientNumber(c.getClientNumber()-1);
		     			c.calculateExitTrajectory();
	     			}
	     			if (!c.isMoving())	c.resume();
	     		}
//	     	}
	     	
	     	observers.remove(0);
	     	
     }
	     
     public void removeObserver(Observer client){
     	observers.remove(client);        	
     }
     
     public void addObserver(Observer client){
     	observers.add(client);
     }
     
     public int getObserversSize(){
    	 return observers.size();
     }
	
	
	

}
