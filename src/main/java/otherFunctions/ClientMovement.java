

package otherFunctions;

import constants.PositionInQueueToExit;
import core.MainLoop;
import dto.PointWithTimeDTO;
import events.ObjectsManager;
import interfaces.AnimatedObject;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import symulation.ApplicationConfiguration;
import visualComponents.Client;
import visualComponents.Door;
import visualComponents.StoreCheckout;

public class ClientMovement {

    public static final int DISTANCE_TO_DOOR_VERTICAL = 20;

    public PointWithTimeDTO calculateTimeToGetToDoor(Client client){
        Point belowDoor = calculatePositionNextToDoor();
        List<Point> trajectory = moveClient(belowDoor, client);
        return new PointWithTimeDTO(belowDoor, MainLoop.getInstance().getTimePassedSeconds() + trajectory.size() * MainLoop.DELTA_TIME);

    }

    private Point calculatePositionNextToDoor() {
        ObjectsManager objectsStateHandler = ApplicationConfiguration.getInstance().getObjectsManager();
        Door door = objectsStateHandler.getDoor();
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
        return new PointWithTimeDTO(destinationPosition, MainLoop.getInstance().getTimePassedSeconds() + trajectory.size() * MainLoop.DELTA_TIME);
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
    Direction movingDirection = chooseWhichWayToGo(new Point (newXCoord, newYCoord), coordinates);
    Set<StoreCheckout> storeCheckouts = ApplicationConfiguration.getInstance().getObjectsManager().getStoreCheckouts();

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
    
        while (Math.abs(newXCoord-coordinates.x)>=Client.stepSize ||
                                Math.abs(newYCoord-coordinates.y)>=Client.stepSize){
        	
        	horizontalStep = movingDirection.getHorizontalDirection();
        	verticalStep = movingDirection.getVerticalDirection();
        	
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
        	if (c==true) counter=Client.zigzagLength;
        	if (b==true || c== true){
        		movingDirection=chooseWhichWayToGo(new Point(newXCoord,newYCoord), coordinates);
        		continue;
        	}

            if (counter<Client.zigzagLength && Math.abs(newXCoord-coordinates.x)>=Client.stepSize){
                newXCoord+=horizontalStep;                
                newCoords.add(new Point(newXCoord,newYCoord));
                
            }
            if (counter>=Client.zigzagLength && Math.abs(newYCoord-coordinates.y)>=Client.stepSize){
                newYCoord+=verticalStep;               
                newCoords.add(new Point(newXCoord,newYCoord));
            }
            if (counter==2*Client.zigzagLength){
                counter=0;
            }
            counter++;
                                    
        }
        
    horizontalStep=movingDirection.getHorizontalDirection();
    verticalStep=movingDirection.getVerticalDirection();
                
    while (Math.abs(newXCoord-coordinates.x)!=0){
        newXCoord+=horizontalStep/Client.stepSize;
        newCoords.add(new Point(newXCoord,newYCoord));
    }
    
    while (Math.abs(newYCoord-coordinates.y)!=0){
        newYCoord+=verticalStep/Client.stepSize;
        newCoords.add(new Point(newXCoord,newYCoord));
    }

    return newCoords;

    }

    public Direction chooseWhichWayToGo (Point start, Point end){
    	
    	int horizontalDirection;
    	int verticalDirection;
    	if (start.x<end.x) horizontalDirection=Client.stepSize;
        else horizontalDirection=-1*Client.stepSize;    

        if (start.y<end.y) verticalDirection=Client.stepSize;
        else verticalDirection=-1*Client.stepSize;
        
        return new Direction (verticalDirection, horizontalDirection);
        
    }
    
}
