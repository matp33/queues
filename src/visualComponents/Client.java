package visualComponents;

import constants.ClientPositionType;
import core.MainLoop;
import interfaces.AnimatedAndObservable;
import interfaces.Observable;
import interfaces.Observer;

import java.awt.*;

import otherFunctions.ClientMovement;
import sprites.SpriteManager;
import sprites.SpriteType;
import symulation.ApplicationConfiguration;
import symulation.Manager;
import symulation.Painter;
import animations.Animation;
import sprites.Sprite;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class Client extends AnimatedAndObservable implements Observer {

private double destinationTime;	
private double timeSupposed=0;
private int clientNumber;
private ClientPositionType positionType;
private int delayWaited;
private int delayStartTime;

private boolean isMoving;
private boolean isSavedInLog; 

private Manager manager;
private ClientMovement movement;
private List <Point> trajectory = new ArrayList<>();
private List <Observer> observers;
private StoreCheckout storeCheckout;

private Timer timer;
private Timer timerDelay; // timer for moving inside queue connected to clients having some delays

private TimerTask movingTask;
private Animation currentAnimation,moveLeft,moveRight,moveDown,moveUp;
private Observable objectObservedByMe;
private Point position;

public final int queueDelay; // delay before client moves when he sees that another client moved
private static final int frameTime=20; // so many steps before animation changes to next



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
private SpriteManager spriteManager;

public Client(StoreCheckout storeCheckout, int clientNumber, Painter painter,
			  double destinationTime, Manager manager)  {

		super(SpriteType.CLIENT,painter);
		spriteManager = ApplicationConfiguration.getInstance().getSpriteManager();
		nr++;
		id=nr;
		this.manager=manager;
		this.queueDelay=createDelay();
    	this.destinationTime=destinationTime;
		Sprite spriteClient = spriteManager.getSprite(SpriteType.CLIENT);

		moveDown=new Animation(spriteClient.getSprite(0),frameTime);
		moveUp=new Animation(spriteClient.getSprite(3),frameTime);
		moveLeft=new Animation(spriteClient.getSprite(1),frameTime);
		moveRight=new Animation(spriteClient.getSprite(2),frameTime);

        isWaiting=false;
        isMoving=false;
        isSavedInLog=false;
        
        this.painter=painter;
        this.clientNumber=clientNumber;
        movement=new ClientMovement(this, new ArrayList<>());
//        positionType=Client.POSITION_WAITING_ROOM;
        currentAnimation=moveUp;
        currentAnimation.start();    
        this.storeCheckout = storeCheckout;
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
    public void moveUpInQueue()  {
    	
//    	System.out.println("decreasing client: "+clientNumber+"time: "+manager.getTime());
    	
    	if (getPositionType().ordinal()>= ClientPositionType.EXITING.ordinal()){
    		return;
    	}
    	

        TimerTask tt=new TimerTask(){
            @Override
            public void run(){
            	isWaiting=false;
            	delayWaited=0; 
            	decreaseNumberOfClientsInQueue();
	            	if (getPositionType().ordinal()>ClientPositionType.WAITING_ROOM.ordinal()){
	            		calculateTrajectory();    
	            	}
				try {
					notifyClients();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
        };
        
        if (storeCheckout.getClientsList().contains(this)){ // if client is in queue
//        	System.out.println("queue "+queueDelay+" delay "+delayWaited);
        	timerDelay.schedule(tt, (int)(queueDelay-delayWaited));
        			
        }
        else{
        	timerDelay.schedule(tt, 0);
        }
        delayStartTime=(int)(MainLoop.getInstance().getTimePassedMilliseconds()); //TODO this is too complicated I guess
        isWaiting=true;
        
    	
    }


	protected void decreaseNumberOfClientsInQueue() {
		if ((
				getPositionType()==ClientPositionType.WAITING_IN_QUEUE && storeCheckout.isClientLastVisible(this))){
			storeCheckout.decreaseClientsAboveLimit();
			
		}
    	clientNumber--;    	
		
	}


    public void scheduleMoving(){


    }
    
    @Override
    public void interrupt()  {
    	    	
    	if (isWaiting){
    		
    		delayWaited+=(int)(MainLoop.getInstance().getTimePassedMilliseconds())-delayStartTime; // no negative values allowed
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

    public void moveToWaitingRoom()  {
    	setPositionType(ClientPositionType.WAITING_ROOM);
        calculateTrajectory();
        timeSupposed=MainLoop.getInstance().getTimePassedMilliseconds()+trajectory.size()*movementDelay+ waitRoomDelay;
    }

    public void moveToQueue(){     
    	storeCheckout.addClient(this);
    	setPositionType(ClientPositionType.GOING_TO_QUEUE);
        calculateTrajectory();
        timeSupposed+=trajectory.size()*movementDelay;        
    }


    public void moveToExit()  {
    	
//    	if (id==12){
//    		manager.pause();
//    	}
    	storeCheckout.getClientsList().remove(this);
    	
//    	System.out.println("Exit "+id);
    	setPositionType(ClientPositionType.EXITING);
    	calculateTrajectory();
    	setObjectObserved(manager.getDoor());
                
        notifyClients();    
        
        
    }
    
    public void moveOutside(){
//    	red=true;
//    	System.out.println("outside: "+id);
    	setPositionType(ClientPositionType.OUTSIDE_VIEW);
    	setObjectObserved(manager.outside);
    	calculateTrajectory();
    }

    public void calculateTrajectory(){
    	
       Point destination=painter.calculateClientDestinationCoordinates(getClientNumber(), getQueueNumber(), positionType);
       
       trajectory=movement.moveClient(destination);  
    }


    private void move() {

        if (MainLoop.getInstance().isPaused()){
            stopMoving();
            return;
        }
        
        if (trajectory.isEmpty()){ // stopped

        	if (MainLoop.getInstance().getTimePassedMilliseconds()<destinationTime){ // client came before his arrival time, so he waits
        		double d=0;
        		stopMoving();  
        		createDelay(d);        		 
//        		System.out.println("waiting"+id);
        		return;
        	} // TODO if get time == destinationTime jump to queue 
        	
            if (getPositionType()==ClientPositionType.GOING_TO_QUEUE &&  isSavedInLog==false){
  
                storeCheckout.getClientsArriving().remove(this);
                storeCheckout.getClientsList().add(this);
//                System.out.println("!!!!"+queue.getClientsList().size());
                saveMeInLog();
                
            }
            
            if (getPositionType()==ClientPositionType.OUTSIDE_VIEW ){
                painter.removeObject(this);
                objectObservedByMe.removeObserver(this);
            }
            
            if (getPositionType()==ClientPositionType.EXITING  ){
            	if (manager.getDoor().isFirst(this))
                manager.openDoor();
            }
            // Opening client 3, but moving outside: 1, 1 should open not 3
            
            stopMoving();
        }
        
        if (!trajectory.isEmpty()){

        	
            if (getPositionType()==ClientPositionType.WAITING_ROOM && isMoving()==false){
            	setPositionType(ClientPositionType.GOING_TO_QUEUE);
            }
            
            if (getPositionType()==ClientPositionType.ARRIVAL && isMoving()==false){
            	setPositionType(ClientPositionType.WAITING_ROOM);
            }
            

            Point point=trajectory.get(0);
            trajectory.remove(0);
            chooseDirection(point);
            currentAnimation.start();
            currentAnimation.updateFrame();
            position=new Point(point.x, point.y);
            
        }

    }

    public static double calculateTimeToGetToQueue(Point queuePosition,
                                             Point waitingRoomPosition){

        int horizontalMoves=Math.abs(queuePosition.x - waitingRoomPosition.x)/stepSize+1;
        int verticalMoves=Math.abs(queuePosition.y - waitingRoomPosition.y)/stepSize+1;

        return (horizontalMoves+verticalMoves)*movementDelay;

    }


    public static Point calculateCoordinates (Point checkoutPosition, Point startingPosition,
                                               double destinationTime){
        
        int amount=(int) (destinationTime/(2*movementDelay));
        int excess=amount%zigzagLength;
        amount-=excess;
        int additional=2*excess;

         
        int signumForX=(int)Math.signum(startingPosition.x-checkoutPosition.x); // should add or
        										// subtract from x position? add -> 1, subtract -1
        int signumForY=(int)Math.signum(startingPosition.y-checkoutPosition.y);
                    
        Point point=new Point(checkoutPosition.x+signumForX*((amount)*stepSize+additional),
                checkoutPosition.y+signumForY*((amount)*stepSize));
                
        if (point.y>startingPosition.y){
        	int diff=point.y-startingPosition.y;
//        	point.setSize(point.x+signumForX*diff, point.y-signumForY*diff);
        }
        
        return point;

    }    

	public int getQueueDelay() {
		return queueDelay;
	}

	public void saveInformation(Point point, ClientPositionType type) {
		position=point;
		setPositionType(type);		
	}
	
	public void saveMeInLog()  {
		if (storeCheckout.isClientOutOfSight(this)) storeCheckout.increaseNumber();
        storeCheckout.getClientsArriving().remove(this);
        isSavedInLog=true;
        manager.saveEvent(getQueueNumber(),timeSupposed,MainLoop.getInstance().getTimePassedMilliseconds());
        setPositionType(ClientPositionType.WAITING_IN_QUEUE);
	}
	
	public Point getPosition(){
		return position;
	}

	public int getQueueNumber() {
		return storeCheckout.getCheckoutIndex();
	}


	public ClientPositionType getPositionType() {
		return positionType;
	}
	
	private void setPositionType(ClientPositionType positionType) {
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
		
	public List<Point> getTrajectory(){
		return trajectory;
	}

	public void addObserver(Observer o){
		observers.add(o);
	}
	
	public void removeObserver(Observer o){
		observers.remove(o);
	}
	
	public void notifyClients ()  {
		
		for (int i=0; i<observers.size();i++){
			observers.get(i).moveUpInQueue();
		}
	}
	
	public void setObjectObserved (Observable o){
		if (objectObservedByMe!=null){
			objectObservedByMe.removeObserver(this);
		}
		
		objectObservedByMe=o;		
		o.addObserver(this);	
	}
	
	private void chooseDirection (Point destination){
		
		if (destination==null){
		}
		
		int diff1=Math.abs(position.x-destination.x);
		int diff2=Math.abs(position.y-destination.y);
		
		if (diff1<diff2){
			if (position.y<destination.y){
	            currentAnimation=moveDown;
	        }
	        if (position.y>destination.y){
	            currentAnimation=moveUp;
	        }
		}
		
		else{
			
	        if (position.x<destination.x){
	            currentAnimation=moveRight;
	        }
	        if (position.x>destination.x){
	            currentAnimation=moveLeft;
	        }
		}
		
        
		
	}

	@Override
	public void update() {

		if (!trajectory.isEmpty()){
			Point point=trajectory.get(0);
			trajectory.remove(0);
			chooseDirection(point);
			currentAnimation.start();
			currentAnimation.updateFrame();
			position=new Point(point.x, point.y);
		}
	}

	@Override
	protected void initializePosition() {
//		position=painter.calculateClientDestinationCoordinates(clientNumber, 0, ClientPositionType.ARRIVAL);
	}

	
	public Observable getObjectObserved(){
		return objectObservedByMe;
	}
	
	
	@Override
    public void paintComponent(Graphics g) {
     
    	int x = position.x;
    	int y = position.y;
        Graphics2D g2d = (Graphics2D) g;
//        if (red){
//        	g2d.setColor(Color.red);
//        }
        g2d.drawImage(currentAnimation.getSprite(),x,y,null); 
//        if (!trajectory.isEmpty())
        g2d.drawString(""+id, x+getSize().width, y+getSize().height);
              
    }
	
	
	private void scheduleGoingToQueue(int delay){
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
				scheduleMoving();
			}
		};
		timerDelay.schedule(tt, (int)(1000*delay));
	}



}

