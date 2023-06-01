package visualComponents;

import constants.ClientPositionType;
import interfaces.AnimatedObject;

import java.awt.*;

import animations.Animation;

import java.util.*;
import java.util.List;


public class Client extends AnimatedObject {

private double arrivalTime;
private double timeSupposed=0;
private int clientNumber;
private ClientPositionType positionType;
private int delayStartTime;

private Point lookAtPoint;

	private List <Point> trajectory = new ArrayList<>();
private int queueNumber;

private Animation currentAnimation,moveLeft,moveRight,moveDown,moveUp;
private Point position;

public final int queueDelay; // delay before client moves when he sees that another client moved
private static final int frameTime=20; // so many steps before animation changes to next



public static final int stepSize=2;
public static final int zigzagLength=20; // for zigzag: denotes how many steps are done in each direction
public static final double movementDelay=0.01; // seconds
private static final int minMovementDelay=600; // min value for queue delay
private static final int maxMovementDelay=700; // max value for queue delay
private static final long serialVersionUID = 1L;
public static final double waitRoomDelay = 1;
public static int nr;
private final int id;

private boolean isWaiting;

private double timeInCheckout;


public Client(int queueNumber, int clientNumber, double arrivalTime, double timeInCheckout)  {

		super();
		this.queueNumber = queueNumber;
		this.timeInCheckout = timeInCheckout;
		nr++;
		id=nr;
		this.queueDelay=createDelay();
    	this.arrivalTime =arrivalTime;


	moveDown=new Animation(sprite.getSpriteFileName(), sprite.getSprite(0),frameTime);
		moveUp=new Animation(sprite.getSpriteFileName(),sprite.getSprite(3),frameTime);
		moveLeft=new Animation(sprite.getSpriteFileName(),sprite.getSprite(1),frameTime);
		moveRight=new Animation(sprite.getSpriteFileName(),sprite.getSprite(2),frameTime);

        isWaiting=false;

        this.clientNumber=clientNumber;
//        positionType=Client.POSITION_WAITING_ROOM;
        currentAnimation=moveUp;
        currentAnimation.start();    
//        red = false;
                
    }

	@Override
	public int getWidth (){
		return spriteWidth;
	}

	public int getId() {
		return id;
	}

	public double calculateTimeOfMovingToQueue() {
		return arrivalTime + waitRoomDelay;
	}

	public double getTimeInCheckout() {
		return timeInCheckout;
	}


	private int createDelay(){
    	Random random=new Random();
		return random.nextInt(maxMovementDelay-minMovementDelay+1)+minMovementDelay;
    }




    
    public void decreaseClientIndex()  {
		clientNumber--;
    }


    @Override
    public void interrupt()  {
    	    	
    	stopMoving();
    	
    }

	@Override
	public void scheduleMoving() {

	}

	public void stopMoving(){
    	    	
		currentAnimation.setLastFrame();
//    		System.out.println("o o is null: "+clientNumber);
    }


	public void calculateExpectedTimeInWaitingRoom(double currentTime) {
		timeSupposed= currentTime+trajectory.size()*movementDelay+ waitRoomDelay;
	}


	public void calculateExpectedTimeInQueue() {
		timeSupposed+=trajectory.size()*movementDelay;
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
        }
        
        return point;

    }    

	public void saveInformation(Point point, ClientPositionType type) {
		position=point;
		setPositionType(type);		
	}

	public Point getPosition(){
		return position;
	}

	public int getQueueNumber() {
		return queueNumber;
	}


	public ClientPositionType getPositionType() {
		return positionType;
	}
	
	public void setPositionType(ClientPositionType positionType) {
		this.positionType = positionType;
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
	public void update(double timePassed) {

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

	

	
	@Override
    public void paintComponent(Graphics g) {
     
    	int x = position.x;
    	int y = position.y;
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(currentAnimation.getSprite(),x,y,null);
        g2d.drawString(""+id, x+getSize().width, y+getSize().height);
              
    }

	@Override
	public String toString (){
		return "" + id + " pos: " +positionType;
	}

	public void setTrajectory(List<Point> trajectory) {
		this.trajectory = trajectory;
	}

	public void setLookAtPoint(Point lookAtPoint) {
		this.lookAtPoint = lookAtPoint;
	}

	public boolean stopped() {
		return trajectory.isEmpty();
	}
}

