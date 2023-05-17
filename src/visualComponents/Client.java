package visualComponents;

import interfaces.AnimatedAndObservable;
import interfaces.Observable;
import interfaces.Observer;

import java.awt.*;
import java.io.IOException;

import otherFunctions.ClientMovement;
import symulation.Manager;
import symulation.Painter;
import animations.Animation;
import animations.Sprite;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class Client extends AnimatedAndObservable implements Observer {

private double destinationTime;	
private double timeSupposed=0;	
private int clientNumber;
private int positionType;
private int delayWaited;
private int delayStartTime;

private boolean isMoving;
private boolean isSavedInLog; 

private Manager manager;
private ClientMovement movement;
private List <Dimension> trajectory;
private List <Observer> observers;
private Queue queue;

private Timer timer;
private Timer timerDelay; // timer for moving inside queue connected to clients having some delays

private TimerTask movingTask;
private Animation currentAnimation,moveLeft,moveRight,moveDown,moveUp;
private Observable objectObservedByMe;
private Dimension position;

public final int queueDelay; // delay before client moves when he sees that another client moved
private static final int frameTime=20; // so many steps before animation changes to next


public static final int POSITION_ARRIVAL=-1;
public static final int POSITION_WAITING_ROOM=0;
public static final int POSITION_GOING_TO_QUEUE=1;
public static final int POSITION_WAITING_IN_QUEUE=2;
public static final int POSITION_EXITING=3;
public static final int POSITION_OUTSIDE=4;

public static final int stepSize=2;
public static final int zigzagLength=20; // for zigzag: denotes how many steps are done in each direction
public static final double movementDelay=0.01; // seconds
private static final int minMovementDelay=600; // min value for queue delay
private static final int maxMovementDelay=700; // max value for queue delay
private static final long serialVersionUID = 1L;
public static final int waitRoomDelay = 1000;
public static int nr;
public final int id; 

private boolean isWaiting;
//private boolean red; // TODO remove it

public Client(Sprite spriteClient, Queue queue,int clientNumber, Painter painter,
    				double destinationTime,Manager manager) {

		super(spriteClient,painter);

		nr++;
		id=nr;
		this.manager=manager;
		this.queueDelay=createDelay();
    	this.destinationTime=destinationTime;
        try {
            

            moveDown=new Animation(spriteClient.getSprite(0),frameTime);
            moveUp=new Animation(spriteClient.getSprite(3),frameTime);
            moveLeft=new Animation(spriteClient.getSprite(1),frameTime);
            moveRight=new Animation(spriteClient.getSprite(2),frameTime);
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        
        isWaiting=false;
        isMoving=false;
        isSavedInLog=false;
        
        this.painter=painter;
        this.clientNumber=clientNumber;
        movement=new ClientMovement(this,manager.getAllObjects());
//        positionType=Client.POSITION_WAITING_ROOM;
        trajectory=new ArrayList <Dimension>();
        currentAnimation=moveUp;
        currentAnimation.start();    
        this.queue=queue;
        delayWaited=0;
        observers=new ArrayList <Observer>();
		timer=new Timer();
		timerDelay=new Timer();
//        red = false;
                
    }
    
    private int createDelay(){
    	Random random=new Random();
        int queueDelay=random.nextInt(maxMovementDelay-minMovementDelay+1)+minMovementDelay;
        return queueDelay;
    }
    
    @Override
    public void updateMyNumber(){
    	
//    	System.out.println("decreasing client: "+clientNumber+"time: "+manager.getTime());
    	
    	if (getPositionType()>=POSITION_EXITING){
    		return;
    	}
    	

        TimerTask tt=new TimerTask(){
            @Override
            public void run(){
            	isWaiting=false;
            	delayWaited=0; 
            	decrease();
	            	if (getPositionType()>POSITION_WAITING_ROOM){            		
	            		calculateTrajectory();    
	            	}            	
            	notifyClients();    
            }
        };
        
        if (queue.getClientsList().contains(this)){ // if client is in queue
//        	System.out.println("queue "+queueDelay+" delay "+delayWaited);
        	timerDelay.schedule(tt, (int)(queueDelay-delayWaited));
        			
        }
        else{
        	timerDelay.schedule(tt, 0);
        }
        delayStartTime=(int)(manager.getTime()*1000); //TODO this is too complicated I guess
        isWaiting=true;
        
    	
    }

    

	protected void decrease() {
		if ((
				getPositionType()==POSITION_WAITING_IN_QUEUE && queue.isClientLastVisible(this))){
			queue.decreaseNumber();
			
		}
    	clientNumber--;    	
		
	}


    public void resume(){

    	
    	if (isMoving()){
    		return;
    	}
    	isMoving=true;

        movingTask = new TimerTask(){
            @Override
            public void run(){
                move();
            }
        };

        
        timer.scheduleAtFixedRate(movingTask, 0, (int)(1000*movementDelay));
        if (isWaiting){
//        	System.out.println("client "+clientNumber+" delay >0 "+delayWaited);
        	updateMyNumber();
        }
    }
    
    @Override
    public void interrupt(){
    	    	
    	if (isWaiting){
    		
    		delayWaited+=(int)(manager.getTime()*1000)-delayStartTime; // no negative values allowed
//    		System.out.println("delay wait: "+delayWaited+"abc"+abc);
    		timerDelay.cancel();
			timer.cancel();
    	}
    	    	
    	stopMoving();    	
    	
    }
    
    public void stopMoving(){ // this is when sprite stops by itself i.e. trajectory size = 0
    	    	
    	if (movingTask!=null){
			movingTask.cancel();
		}
    	isMoving=false;
    	currentAnimation.setLastFrame();
    	if (objectObservedByMe!=null){
    		chooseDirection(objectObservedByMe.getPosition());
//    		System.out.println("o o is null: "+clientNumber);
    	}
    }

    public void moveToWaitingRoom(){  
    	createDelayTimer(waitRoomDelay);
    	setPositionType(POSITION_WAITING_ROOM);
        calculateTrajectory();
        timeSupposed=manager.getTime()+trajectory.size()*movementDelay+ waitRoomDelay;
    }

    public void moveToQueue(){     
    	queue.addClient(this);
    	setPositionType(POSITION_GOING_TO_QUEUE);
        calculateTrajectory();
        timeSupposed+=trajectory.size()*movementDelay;        
    }

    public void moveToExit(){
    	
//    	if (id==12){
//    		manager.pause();
//    	}
    	queue.getClientsList().remove(this);
    	
//    	System.out.println("Exit "+id);
    	setPositionType(Client.POSITION_EXITING);
    	calculateTrajectory();
    	setObjectObserved(manager.door);
                
        notifyClients();    
        
        
    }
    
    public void moveOutside(){
//    	red=true;
//    	System.out.println("outside: "+id);
    	setPositionType(Client.POSITION_OUTSIDE);
    	setObjectObserved(manager.outside);
    	calculateTrajectory();
    }

    public void calculateTrajectory(){
    	
       Dimension destination=painter.calculateClientCoordinates(getClientNumber(), getQueueNumber(), positionType);
       
       trajectory=movement.moveClient(destination);  
//       System.out.println("outside desti"+trajectory);
       resume();
    }
    
//    public void calculateExitTrajectory(){ //TODO temporary fix; remove moveToExit method from movement class
////    	System.out.println("exit trajectory :"+getQueueNumber());
//    	Dimension destination=painter.calculateClientCoordinates(clientNumber, getQueueNumber(), 
//   			    positionType);  
//    	
//
//		trajectory=movement.moveToExit(destination,painter.getTillPosition(getQueueNumber()),
//		                        painter.getTillDimensions(), getSize().width);		
////		System.out.println("exit trajectory :"+destination+" destination "+trajectory);
//		resume();
//    }


    private void move(){

        if (manager.isRunning()==false){
            stopMoving();
            return;
        }
        
        if (trajectory.isEmpty()){ // stopped

        	if (manager.getTime()<destinationTime){ // client came before his arrival time, so he waits
        		double d=destinationTime-manager.getTime();
        		stopMoving();  
        		createDelay(d);        		 
//        		System.out.println("waiting"+id);
        		return;
        	} // TODO if get time == destinationTime jump to queue 
        	
            if (getPositionType()==Client.POSITION_GOING_TO_QUEUE &&  isSavedInLog==false){
  
                queue.getClientsArriving().remove(this);
                queue.getClientsList().add(this); 
//                System.out.println("!!!!"+queue.getClientsList().size());
                saveMeInLog();
                
            }
            
            if (getPositionType()==Client.POSITION_OUTSIDE ){    
                painter.removeObject(this);
                objectObservedByMe.removeObserver(this);
            }
            
            if (getPositionType()==Client.POSITION_EXITING  ){ 
            	if (manager.door.isFirst(this))
                manager.openDoor();
            }
            // Opening client 3, but moving outside: 1, 1 should open not 3
            
            stopMoving();
        }
        
        if (!trajectory.isEmpty()){

        	
            if (getPositionType()==Client.POSITION_WAITING_ROOM && isMoving()==false){ 
            	setPositionType(Client.POSITION_GOING_TO_QUEUE);
            }
            
            if (getPositionType()==Client.POSITION_ARRIVAL && isMoving()==false){ 
            	setPositionType(Client.POSITION_WAITING_ROOM);
            }
            

            Dimension d=trajectory.get(0);
            trajectory.remove(0);
            chooseDirection(d);  
            currentAnimation.start();
            currentAnimation.updateFrame();
            position=new Dimension(d.width,d.height);
            
        }

    }

    public static double calculateTimeToGetToQueue(Dimension queuePosition,
                                             Dimension waitingRoomPosition){

        int horizontalMoves=Math.abs(queuePosition.width-waitingRoomPosition.width)/stepSize+1;
        int verticalMoves=Math.abs(queuePosition.height-waitingRoomPosition.height)/stepSize+1;

        return (horizontalMoves+verticalMoves)*movementDelay;

    }

    public static double calculateTimeToGetToWaitingRoom (Dimension waitingRoomPosition,
                                                   Dimension startingPosition){

        int horizontalMoves=Math.abs(waitingRoomPosition.width-startingPosition.width)/stepSize+1;
        int verticalMoves=Math.abs(waitingRoomPosition.height-startingPosition.height)/stepSize+1;

        return (horizontalMoves+verticalMoves)*movementDelay;

    }

    public static Dimension calculateCoordinates (Dimension tillPosition, Dimension startingPosition,
                                               double destinationTime){
        
        int amount=(int) (destinationTime/(2*movementDelay));
        int excess=amount%zigzagLength;
        amount-=excess;
        int additional=2*excess;

         
        int signumForX=(int)Math.signum(startingPosition.width-tillPosition.width); // should add or
        										// subtract from x position? add -> 1, subtract -1
        int signumForY=(int)Math.signum(startingPosition.height-tillPosition.height);
                    
        Dimension d=new Dimension(tillPosition.width+signumForX*((amount)*stepSize+additional),
                tillPosition.height+signumForY*((amount)*stepSize));
                
        if (d.height>startingPosition.height){
        	int diff=d.height-startingPosition.height;
        	d.setSize(d.width+signumForX*diff, d.height-signumForY*diff);
        }
        
        return d;

    }    

	public int getQueueDelay() {
		return queueDelay;
	}

	public void saveInformation(Dimension dim, Integer type) {
		position=dim;
		setPositionType(type);		
	}
	
	public void saveMeInLog(){
		if (queue.isClientOutOfSight(this)) queue.increaseNumber();
        queue.getClientsArriving().remove(this);
        isSavedInLog=true;
        manager.saveEvent(getQueueNumber(),timeSupposed,manager.getTime());
        setPositionType(Client.POSITION_WAITING_IN_QUEUE);
	}
	
	public Dimension getPosition(){
		return position;
	}

	public int getQueueNumber() {
		return queue.getQueueNumber();
	}


	public int getPositionType() {
		return positionType;
	}
	
	private void setPositionType(int positionType) {
		this.positionType = positionType;
	}

	public boolean isMoving() {
		return isMoving;
	}

	public int getClientNumber() {
		return clientNumber;
	}

	public void setClientNumber(int clientNumber) {
		this.clientNumber = clientNumber;
	}
		
	public List<Dimension> getTrajectory(){
		return trajectory;
	}

	public void addObserver(Observer o){
		observers.add(o);
	}
	
	public void removeObserver(Observer o){
		observers.remove(o);
	}
	
	public void notifyClients (){
		
		for (int i=0; i<observers.size();i++){
			observers.get(i).updateMyNumber();
		}
	}
	
	public void setObjectObserved (Observable o){
		if (objectObservedByMe!=null){
			objectObservedByMe.removeObserver(this);
		}
		
		objectObservedByMe=o;		
		o.addObserver(this);	
	}
	
	private void chooseDirection (Dimension destination){
		
		if (destination==null){
		}
		
		int diff1=Math.abs(position.width-destination.width);
		int diff2=Math.abs(position.height-destination.height);
		
		if (diff1<diff2){
			if (position.height<destination.height){
	            currentAnimation=moveDown;
	        }
	        if (position.height>destination.height){
	            currentAnimation=moveUp;
	        }
		}
		
		else{
			
	        if (position.width<destination.width){
	            currentAnimation=moveRight;
	        }
	        if (position.width>destination.width){
	            currentAnimation=moveLeft;
	        }
		}
		
        
		
	}

	@Override
	protected void initializePosition() {
		position=painter.calculateClientCoordinates(clientNumber, 0, POSITION_ARRIVAL);		
	}

	
	public Observable getObjectObserved(){
		return objectObservedByMe;
	}
	
	
	@Override
    public void paintComponent(Graphics g) {
     
    	int x = position.width;
    	int y = position.height;
        painter.paintClient(this);
        Graphics2D g2d = (Graphics2D) g;
//        if (red){
//        	g2d.setColor(Color.red);
//        }
        g2d.drawImage(currentAnimation.getSprite(),x,y,null); 
//        if (!trajectory.isEmpty())
        g2d.drawString(""+id, x+getSize().width, y+getSize().height);
              
    }
	
	
	private void createDelayTimer(int delay){
		TimerTask tt = new TimerTask (){
			@Override
			public void run(){
				moveToQueue();
				isWaiting=false;
			}
		};
		timerDelay.schedule(tt, delay);
		isWaiting=true;
	}
	
	public void createDelay(double delay){
		TimerTask tt = new TimerTask (){
			@Override
			public void run(){
				resume();
			}
		};
		timerDelay.schedule(tt, (int)(1000*delay));
	}



}

