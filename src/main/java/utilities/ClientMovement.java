

package utilities;

import constants.PositionInQueueToExit;
import core.MainLoop;
import core.ObjectsManager;
import dto.PointWithTimeDTO;
import visualComponents.AnimatedObject;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import spring2.Bean;
import visualComponents.Client;
import visualComponents.Door;
import visualComponents.StoreCheckout;

@Bean
public class ClientMovement {

    public static final int DISTANCE_TO_DOOR_VERTICAL = 20;

    private ObjectsManager objectsManager;

    private MainLoop mainLoop;

    private static final int stepSize=2;

    private static final int zigzagLength=20;

    private static final double movementDelay=0.01;

    public static final double waitRoomDelay = 1;

    public double calculateTimeOfMovingToQueue(double arrivalTime) {
        return arrivalTime + waitRoomDelay;
    }

    public double calculateTimeFromWaitingRoomToQueue (Point queuePosition,
                                                       Point waitingRoomPosition){
        double timeToGetToQueue = calculateTimeToGetToQueue(queuePosition, waitingRoomPosition);
        return timeToGetToQueue + waitRoomDelay;

    }


    public ClientMovement(ObjectsManager objectsManager, MainLoop mainLoop) {
        this.objectsManager = objectsManager;
        this.mainLoop = mainLoop;
    }

    public PointWithTimeDTO calculateTimeToGetToDoor(Client client){
        Point belowDoor = calculatePositionNextToDoor();
        List<Point> trajectory = moveClient(belowDoor, client);
        return new PointWithTimeDTO(belowDoor, mainLoop.getTimePassedSeconds() + trajectory.size() * MainLoop.DELTA_TIME);

    }

    private Point calculatePositionNextToDoor() {
        Door door = objectsManager.getDoor();
        Point doorPosition = door.getPosition();
        return new Point(doorPosition.x, doorPosition.y + DISTANCE_TO_DOOR_VERTICAL);
    }

    public PointWithTimeDTO calculateTimeToGetToPosition(Client client, int indexInPosition, PositionInQueueToExit positionInQueueToExit) {
        Point destinationPosition = calculatePositionNextToDoor();
        switch (positionInQueueToExit){
            case LEFT:
                destinationPosition.x-= indexInPosition * client.getWidth();
                break;
            case RIGHT:
                destinationPosition.x+= indexInPosition * client.getWidth();
                break;
        }
        List<Point> trajectory = moveClient(destinationPosition, client);
        return new PointWithTimeDTO(destinationPosition, mainLoop.getTimePassedSeconds() + trajectory.size() * MainLoop.DELTA_TIME);
    }

    public List <Point> moveClient (Point coordinates, Client client){

    
    int newXCoord=client.getPosition().x;
    int newYCoord=client.getPosition().y;
    
    int minOfRangeX = Math.min(newXCoord, coordinates.x);
    int maxOfRangeX = Math.max(newXCoord, coordinates.x);
    int minOfRangeY = Math.min(newYCoord, coordinates.y);
    int maxOfRangeY = Math.max(newYCoord, coordinates.y);
    
    int rectangleWidth=maxOfRangeX-minOfRangeX;
    int rectangleHeight=maxOfRangeY-minOfRangeY;
    
    List <StoreCheckout> objectsOnTheWay = new ArrayList <>();
    Rectangle clientTrajectory = new Rectangle(minOfRangeX, minOfRangeY, rectangleWidth, rectangleHeight);
    MoveDirection movingMoveDirection = chooseWhichWayToGo(new Point (newXCoord, newYCoord), coordinates);
    Set<StoreCheckout> storeCheckouts = objectsManager.getStoreCheckouts();

    for (StoreCheckout q: storeCheckouts){
        Rectangle checkoutArea = new Rectangle (q.getPosition().x, q.getPosition().y, q.getSize().width,
                q.getSize().height);
            if (checkoutArea.intersects(clientTrajectory)){
                objectsOnTheWay.add(q);
            }

    }

    List <Point> newCoords = new ArrayList <> ();
    int counter=1;           
    int horizontalStep;
    int verticalStep;
    
    //  ******************************** zigzag movement *****************************************
    
        while (Math.abs(newXCoord-coordinates.x)>=stepSize ||
                                Math.abs(newYCoord-coordinates.y)>=stepSize){
        	
        	horizontalStep = movingMoveDirection.getHorizontalDirection();
        	verticalStep = movingMoveDirection.getVerticalDirection();
        	
        	boolean b=false;
        	boolean c=false;
        	Point stepX = new Point (newXCoord+horizontalStep,newYCoord);
        	Point stepY = new Point (newXCoord, newYCoord + verticalStep);
        	Rectangle recx = new Rectangle(stepX, client.getSize());
        	Rectangle recy = new Rectangle(stepY, client.getSize());        	        	
        	
        	for (AnimatedObject obj: objectsOnTheWay){ 
        		Rectangle objectArea = new Rectangle(obj.getPosition().x, obj.getPosition().y,
        				obj.getSize().width,obj.getSize().height);
        		    	
        		while (objectArea.intersects(recx) ){  
        			newYCoord+=verticalStep;
        			newCoords.add(new Point (newXCoord,newYCoord));
        			stepX = new Point (stepX.x,newYCoord);
        			recx.setLocation(stepX);
        			b=true;
        		}
        		
        		while (objectArea.intersects(recy)){
        			newXCoord+=horizontalStep;
	    			newCoords.add(new Point (newXCoord,newYCoord));
	    			c=true;
	    			stepY = new Point (newXCoord, stepY.y);
	    			recy.setLocation(stepY);
        		}
        		        		
        	}
        	
        	if (b==true) counter=0;
        	if (c==true) counter=zigzagLength;
        	if (b==true || c== true){
        		movingMoveDirection =chooseWhichWayToGo(new Point(newXCoord,newYCoord), coordinates);
        		continue;
        	}

            if (counter<zigzagLength && Math.abs(newXCoord-coordinates.x)>=stepSize){
                newXCoord+=horizontalStep;                
                newCoords.add(new Point(newXCoord,newYCoord));
                
            }
            if (counter>=zigzagLength && Math.abs(newYCoord-coordinates.y)>=stepSize){
                newYCoord+=verticalStep;               
                newCoords.add(new Point(newXCoord,newYCoord));
            }
            if (counter==2*zigzagLength){
                counter=0;
            }
            counter++;
                                    
        }
        
    horizontalStep= movingMoveDirection.getHorizontalDirection();
    verticalStep= movingMoveDirection.getVerticalDirection();
                
    while (Math.abs(newXCoord-coordinates.x)!=0){
        newXCoord+=horizontalStep/stepSize;
        newCoords.add(new Point(newXCoord,newYCoord));
    }
    
    while (Math.abs(newYCoord-coordinates.y)!=0){
        newYCoord+=verticalStep/stepSize;
        newCoords.add(new Point(newXCoord,newYCoord));
    }

    return newCoords;

    }

    public MoveDirection chooseWhichWayToGo (Point start, Point end){
    	
    	int horizontalDirection;
    	int verticalDirection;
    	if (start.x<end.x) horizontalDirection=stepSize;
        else horizontalDirection=-1*stepSize;

        if (start.y<end.y) verticalDirection=stepSize;
        else verticalDirection=-1*stepSize;
        
        return new MoveDirection(verticalDirection, horizontalDirection);
        
    }

    public void calculateAndSetClientTrajectory(Client client, Point lookAtPoint) {
        List<Point> trajectory = moveClient(lookAtPoint, client);
        client.setTrajectory(trajectory);
        client.setLookAtPoint(lookAtPoint);
    }

    public double calculateTimeToGetToQueue(Point queuePosition,
                                                   Point waitingRoomPosition){

        int horizontalMoves=Math.abs(queuePosition.x - waitingRoomPosition.x)/stepSize+1;
        int verticalMoves=Math.abs(queuePosition.y - waitingRoomPosition.y)/stepSize+1;

        return (horizontalMoves+verticalMoves)*movementDelay;

    }


    public Point calculateCoordinates (Point checkoutPosition, Point startingPosition,
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

}
