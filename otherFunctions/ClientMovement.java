

package otherFunctions;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import visualComponents.Client;

public class ClientMovement {
    
private Client client;                                

    public ClientMovement (Client client){
        this.client=client;
    }

    public List <Dimension> moveClient (Dimension coordinates){

    int horizontalStep;
    int verticalStep;

        if (client.getPosition().width<coordinates.width){
            horizontalStep=Client.stepSize;
        }
        else {
            horizontalStep=-1*Client.stepSize;
        }

        if (client.getPosition().height<coordinates.height){
            verticalStep=Client.stepSize;
        }
        else {
            verticalStep=-1*Client.stepSize;
        }

    List <Dimension> newCoords = new ArrayList <Dimension> ();
    int newXCoord=client.getPosition().width;
    int newyYCoord=client.getPosition().height;
    int counter=1; 

//    System.out.println("wspolrzedne" +wspolrzedne+"klient "+klient.x+","+klient.y);

    //  ******************************** zigzag movement *****************************************
    
        while (Math.abs(newXCoord-coordinates.width)>=Client.stepSize ||
                                Math.abs(newyYCoord-coordinates.height)>=Client.stepSize){

            if (counter<Client.zigzagLength && Math.abs(newXCoord-coordinates.width)>=Client.stepSize){
                newXCoord+=horizontalStep;                
                newCoords.add(new Dimension(newXCoord,newyYCoord));
                
            }
            if (counter>=Client.zigzagLength && Math.abs(newyYCoord-coordinates.height)>=Client.stepSize){
                newyYCoord+=verticalStep;                
                newCoords.add(new Dimension(newXCoord,newyYCoord));
            }
            if (counter==2*Client.zigzagLength){
                counter=0;
            }
            counter++;
            
//            System.out.println("x"+nowyX+"y"+nowyY);
                        
        }

    while (Math.abs(newXCoord-coordinates.width)!=0){
        newXCoord+=horizontalStep/Client.stepSize;
        newCoords.add(new Dimension(newXCoord,newyYCoord));

    }
    while (Math.abs(newyYCoord-coordinates.height)!=0){
        newyYCoord+=verticalStep/Client.stepSize;
        newCoords.add(new Dimension(newXCoord,newyYCoord));
    }

//    for (int i=0; i<newCoords.size();i++){
//    	System.out.println(newCoords.size()+"rozmiar "+newCoords.get(i));
//    }
//    System.out.println(newCoords.size()+"rozmiar "+newCoords);
    return newCoords;

    }

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

}
