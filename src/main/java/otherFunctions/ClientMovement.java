

package otherFunctions;

import interfaces.AnimatedObject;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import visualComponents.Client;
import visualComponents.StoreCheckout;

public class ClientMovement {
    
private Client client;
private List <AnimatedObject> objects;

    public ClientMovement (Client client, List <AnimatedObject> objects){
        this.client=client;
        this.objects=objects;
    }

    public List <Point> moveClient (Point coordinates){

    
    int newXCoord=client.getPosition().x;
    int newYCoord=client.getPosition().y;
    
    int minOfRangeX = Math.min(newXCoord, coordinates.x);
    int maxOfRangeX = Math.max(newXCoord, coordinates.x);
    int minOfRangeY = Math.min(newYCoord, coordinates.y);
    int maxOfRangeY = Math.max(newYCoord, coordinates.y);
    
    int rectangleWidth=maxOfRangeX-minOfRangeX;
    int rectangleHeight=maxOfRangeY-minOfRangeY;
    
    List <AnimatedObject> objectsOnTheWay = new ArrayList <AnimatedObject>();
    Rectangle clientTrajectory = new Rectangle(minOfRangeX, minOfRangeY, rectangleWidth, rectangleHeight);
    Direction movingDirection = chooseWhichWayToGo(new Point (newXCoord, newYCoord), coordinates);
    int i=client.getId();
    
    for (AnimatedObject object: objects){
    	if (object instanceof StoreCheckout){
    		StoreCheckout q = (StoreCheckout)object;
    		Rectangle checkoutArea = new Rectangle (q.getPosition().x, q.getPosition().y, q.getSize().width,
    				q.getSize().height);
    			if (checkoutArea.intersects(clientTrajectory)){
    				objectsOnTheWay.add(object);
    			}

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