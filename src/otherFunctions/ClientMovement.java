

package otherFunctions;

import interfaces.AnimatedObject;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

import symulation.Painter;
import visualComponents.Client;
import visualComponents.Queue;

public class ClientMovement {
    
private Client client;
private List <AnimatedObject> objects;

    public ClientMovement (Client client, List <AnimatedObject> objects){
        this.client=client;
        this.objects=objects;
    }

    public List <Dimension> moveClient (Dimension coordinates){

    
    int newXCoord=client.getPosition().width;
    int newYCoord=client.getPosition().height;
    
    int minOfRangeX = Math.min(newXCoord, coordinates.width);
    int maxOfRangeX = Math.max(newXCoord, coordinates.width);
    int minOfRangeY = Math.min(newYCoord, coordinates.height);
    int maxOfRangeY = Math.max(newYCoord, coordinates.height);
    
    int rectangleWidth=maxOfRangeX-minOfRangeX;
    int rectangleHeight=maxOfRangeY-minOfRangeY;
    
    List <AnimatedObject> objectsOnTheWay = new ArrayList <AnimatedObject>();
    Rectangle clientTrajectory = new Rectangle(minOfRangeX, minOfRangeY, rectangleWidth, rectangleHeight);
    Direction movingDirection = chooseWhichWayToGo(new Dimension (newXCoord, newYCoord), coordinates);
    int i=client.id;
    
    for (AnimatedObject object: objects){
    	if (object instanceof Queue){
    		Queue q = (Queue)object;
    		Rectangle queueArea = new Rectangle (q.getPosition().width, q.getPosition().height, q.getSize().width,
    				q.getSize().height);
    			if (queueArea.intersects(clientTrajectory)){
    				objectsOnTheWay.add(object);
    			}

    	}
    }    
    
    List <Dimension> newCoords = new ArrayList <Dimension> ();    
    int counter=1;           
    int horizontalStep;
    int verticalStep;
    
    //  ******************************** zigzag movement *****************************************
    
        while (Math.abs(newXCoord-coordinates.width)>=Client.stepSize ||
                                Math.abs(newYCoord-coordinates.height)>=Client.stepSize){
        	
        	horizontalStep = movingDirection.getHorizontalDirection();
        	verticalStep = movingDirection.getVerticalDirection();
        	
        	boolean b=false;
        	boolean c=false;
        	Point stepX = new Point (newXCoord+horizontalStep,newYCoord);
        	Point stepY = new Point (newXCoord, newYCoord + verticalStep);
        	Rectangle recx = new Rectangle(stepX, client.getSize());
        	Rectangle recy = new Rectangle(stepY, client.getSize());        	        	
        	
        	for (AnimatedObject obj: objectsOnTheWay){ 
        		Rectangle objectArea = new Rectangle(obj.getPosition().width, obj.getPosition().height,
        				obj.getSize().width,obj.getSize().height);
        		    	
        		while (objectArea.intersects(recx) ){  
        			newYCoord+=verticalStep;
        			newCoords.add(new Dimension (newXCoord,newYCoord));
        			stepX = new Point (stepX.x,newYCoord);
        			recx.setLocation(stepX);
        			b=true;
        		}
        		
        		while (objectArea.intersects(recy)){
        			newXCoord+=horizontalStep;
	    			newCoords.add(new Dimension (newXCoord,newYCoord));
	    			c=true;
	    			stepY = new Point (newXCoord, stepY.y);
	    			recy.setLocation(stepY);
        		}
        		        		
        	}
        	
        	if (b==true) counter=0;
        	if (c==true) counter=Client.zigzagLength;
        	if (b==true || c== true){
        		movingDirection=chooseWhichWayToGo(new Dimension(newXCoord,newYCoord), coordinates);
        		continue;
        	}

            if (counter<Client.zigzagLength && Math.abs(newXCoord-coordinates.width)>=Client.stepSize){
                newXCoord+=horizontalStep;                
                newCoords.add(new Dimension(newXCoord,newYCoord));
                
            }
            if (counter>=Client.zigzagLength && Math.abs(newYCoord-coordinates.height)>=Client.stepSize){
                newYCoord+=verticalStep;               
                newCoords.add(new Dimension(newXCoord,newYCoord));
            }
            if (counter==2*Client.zigzagLength){
                counter=0;
            }
            counter++;
                                    
        }
        
    horizontalStep=movingDirection.getHorizontalDirection();
    verticalStep=movingDirection.getVerticalDirection();
                
    while (Math.abs(newXCoord-coordinates.width)!=0){
        newXCoord+=horizontalStep/Client.stepSize;
        newCoords.add(new Dimension(newXCoord,newYCoord));
    }
    
    while (Math.abs(newYCoord-coordinates.height)!=0){
        newYCoord+=verticalStep/Client.stepSize;
        newCoords.add(new Dimension(newXCoord,newYCoord));
    }

    return newCoords;

    }

    @Deprecated
    /*
     * TODO to be removed
     */
    public List <Dimension> moveToExit (Dimension coordinates, Dimension tillCoordinates,
                                              Dimension tillDimension, int clientsWidth){

        List <Dimension> coords = new ArrayList <Dimension> ();
        int tillXCordinate = tillCoordinates.width;
        int newX=client.getPosition().width;
        int newY=client.getPosition().height;

        int signum;
        if (newX>coordinates.width){
            signum=1;
//            System.out.println("szerokosc kasy "+wymiaryKasy.height);
            while ( (newX- (tillXCordinate-clientsWidth))*signum >0){
//                System.out.println(nowyX+"nowyX");
                newX+=-1*signum*Client.stepSize;
                coords.add(new Dimension(newX, newY));
            }
            if (coords.size()==0){
//                System.out.println("nowyx "+nowyX+" szer "+(wspolrzednaXKasy)+";"+klient.nrKlienta);
            }
            if (coords.size()>0){
            	Dimension lastPoint=coords.get(coords.size()-1);
                newX=tillXCordinate-clientsWidth+5;
                lastPoint=new Dimension(tillXCordinate-clientsWidth,lastPoint.height);
                coords.set(coords.size()-1, lastPoint);
            }
            

        }
        else{
            signum=-1;
            while ( (newX- (tillXCordinate+tillDimension.width))*signum >0){
                newX+=-1*signum*Client.stepSize;
                coords.add(new Dimension(newX, newY));
            }
            
            if (coords.size()>0){
	            Dimension lastPoint=coords.get(coords.size()-1);
	            newX=tillXCordinate+tillDimension.width-5;
	            lastPoint=new Dimension(tillXCordinate+tillDimension.width,lastPoint.height);
	            coords.set(coords.size()-1, lastPoint);
            }
        }

            
        signum=1;
        if (newY<coordinates.height){
            signum=-1;
        }
            while ((newY-coordinates.height)*signum>0){
                newY+=(-1)*signum*Client.stepSize;
                coords.add(new Dimension(newX, newY));
            }
            
        if (coords.size()>0){
        	Dimension lastPoint=coords.get(coords.size()-1);
            
            lastPoint=new Dimension(lastPoint.width,coordinates.height);
            coords.set(coords.size()-1, lastPoint);
        }
        

        if (newX<coordinates.width){
            signum=-1;
        }
        else{
            signum=1;
        }
            while ((newX-coordinates.width)*signum>0){
                newX+=(-1)*signum*Client.stepSize;
                coords.add(new Dimension(newX, newY));
            }
        
        if (coords.size()>0){
        	Dimension lastPoint=coords.get(coords.size()-1);
            lastPoint=new Dimension(lastPoint.width,coordinates.height);
            coords.set(coords.size()-1, lastPoint);
        }
        
        return coords;
    }

    public List <Dimension> moveAsQuadraticFunction (Dimension coordinates){

        int horizontalStep;
        

            if (client.getPosition().width<coordinates.width){
                horizontalStep=1;
            }
            else {
                horizontalStep=-1;
            }
            
        
        List <Dimension> coords = new ArrayList <Dimension> ();

        int initialXCoord=client.getPosition().width;
        int initialYCoord=client.getPosition().height;

        int finalXCoord=coordinates.width;
        int finalYCoord=coordinates.height;

        double a=(double)(initialYCoord-finalYCoord)/(double)(Math.pow(initialXCoord,2)-Math.pow(finalXCoord,2)-8*initialXCoord+8*finalXCoord);        
        double b=-8*a;
        double c=initialYCoord-a*Math.pow(initialXCoord,2)+8*a*initialXCoord;

        int newX=initialXCoord;
        int newY=initialYCoord;       
        int signum;

        if (newY-coordinates.height<0){
            signum=-1;
        }
        else{
            signum=1;
        }

        if (a==0 && b==0){
            coords.add(new Dimension(initialXCoord,initialYCoord));
            return coords;

        }

            while (signum*(newY-coordinates.height)>0){

                newX+=horizontalStep;
                newY=(int)(a*Math.pow(newX,2)+b*newX+c);
                coords.add(new Dimension(newX,newY));

            }

        return coords;

    }
    
    public Direction chooseWhichWayToGo (Dimension start, Dimension end){
    	
    	int horizontalDirection;
    	int verticalDirection;
    	if (start.width<end.width) horizontalDirection=Client.stepSize;  
        else horizontalDirection=-1*Client.stepSize;    

        if (start.height<end.height) verticalDirection=Client.stepSize;    
        else verticalDirection=-1*Client.stepSize;
        
        return new Direction (verticalDirection, horizontalDirection);
        
    }
    
}
