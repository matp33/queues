package visualComponents;


import interfaces.AnimatedAndObservable;
import interfaces.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import sprites.SpriteType;
import symulation.ApplicationConfiguration;
import symulation.Painter;

public class Door extends AnimatedAndObservable  {
	
	private static final long serialVersionUID = 1L;
	
	private Timer timer;
	private final int delay=10;
	private List <Observer> observers;
	private int indexOfQueueClosestToTheLeftOfDoor;
	
	public static int STATE_CLOSING=-1;
	public static int STATE_NEUTRAL=0; // nothing
	public static int STATE_OPENING=1;
	
	private boolean isOpened;

	private TimerTask currentAnimationTask;
	
	public Door(int indexOfQueueClosestToTheLeftOfDoor){
		super(SpriteType.DOOR);
			
		state=STATE_NEUTRAL;
		observers = new ArrayList <Observer>();
		this.indexOfQueueClosestToTheLeftOfDoor =indexOfQueueClosestToTheLeftOfDoor;
		startDrawingMe();
		timer = new Timer();
		
	}
	
	public void initializePosition (){
		position=painter.getDoorPosition();		
	}
		
	public void doOpening(){	
		
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
		if (currentAnimationTask != null){
			currentAnimationTask.cancel();
		}
		currentAnimation.stop();
	}
	
	public void interrupt(){		
		if (state!=STATE_NEUTRAL){		
			stopOpening();
		}		
	}
	
	public void scheduleMoving(){
		
		if (state==STATE_OPENING){
			doOpening();
		}
		else if (state==STATE_CLOSING){
			doClosing();
		}
	}

	private void scheduleTask(){
		
		if (currentAnimationTask!=null){
			currentAnimationTask.cancel();
//			timer=null;
		}

		currentAnimationTask = new TimerTask(){
			public void run(){		
				
				currentAnimation.updateFrame();
				if (canClientEnter() ){
					notifyClients();					
					isOpened=true;
				}
				else{
					isOpened = false;
				}
				
				if ((state==STATE_OPENING && currentAnimation.isFinalFrame())
						|| (state==STATE_CLOSING && currentAnimation.isInitialFrame())){
					currentAnimationTask.cancel();
//					timer=null;
					if (state==STATE_OPENING){
						doClosing();
					}
				}
			}			
		};
		
		timer.schedule(currentAnimationTask, 0,delay);
				
	}
	

	public boolean canClientEnter(){
		if (currentAnimation.getCurrentFrame()>=2 && state == STATE_OPENING ){
			return true;
		}
		else{
			return false;
		}
	}
	
	 public void notifyClients(){ 
		 

	     	
     }
	     
     public void removeObserver(Observer client){
    	 Client c=(Client )client;
//    	 System.out.println("removing "+c.id);
     	observers.remove(client);        	
     }
     
     public void addObserver(Observer client){
    	findPlaceForClient(client);
    	Client c=(Client )client;
//    	System.out.println("added"+c.id);
//     	observers.add(client);
     	sortObservers();
     }
     
     private void findPlaceForClient(Observer client) {
    	 
    	 if (observers.isEmpty()){
    		 observers.add(client);
    		 return;
    	 }
//    	 Client cu = (Client) client;
//    	 if (cu.id==12 || cu.id==10){    		 
//    		 observers.add(0,client);
//    		 return;
//    	 }
    	 
    	 Client c = (Client )client;
//    	 System.out.println("adding client "+c.id+ "+ as last ");
    	 observers.add(client);
    	 
		
	}

	public int getObserversSize(){
    	 return observers.size();
     }

	public void sortObservers() {

		int iLeft=0;
		int iRight=0;
		for (int i=1; i<observers.size();i++){
			
			if (observers.get(i) instanceof Client){
				Client c = (Client)observers.get(i);
				int j=chooseLeftOrRight(c);
				if (j==1){
					iRight++; 
					c.setClientNumber(iRight);
				}
				if (j==-1){
					iLeft++;
					c.setClientNumber(iLeft);
				}
				c.calculateTrajectory();

				
				
			}
		}
		
		
	}
	

	public int chooseLeftOrRight(Client c){
		if (c.getQueueNumber()>= indexOfQueueClosestToTheLeftOfDoor){
			return 1; //right
		}
		else{
			return -1; //left
		}
	}
	
	public boolean isFirst (Observer o){
		for (int i=0; i<observers.size();i++){
			Client c = (Client) observers.get(i);
		}
		return observers.get(0)==o;
	}


	@Override
	public void update(double currentTimeSeconds) {

	}

	public boolean isOpened() {
		return isOpened;
	}
}