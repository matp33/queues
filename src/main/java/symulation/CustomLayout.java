

package symulation;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.List;
import java.util.NavigableSet;
import java.util.Optional;

import javax.swing.JPanel;

import constants.ClientPositionType;
import constants.PositionInQueueToExit;
import dto.ClientToExitDTO;
import dto.PointWithTimeAndQueueIndexDTO;
import dto.PointWithTimeDTO;
import otherFunctions.ClientMovement;
import sprites.SpriteManager;
import sprites.SpriteType;
import visualComponents.Client;
import visualComponents.Door;

public class CustomLayout {

    private final int minimumWindowHeight=700;
    private final int horizontalPadding =10;
    private final int verticalPadding =50;
    private final int verticalMarginBetweenObjects =10;
    private final JPanel buttonsPanel;
    private double checkoutProportions;
    private int spaceBetweenCheckouts;
    private int clientsWidth;
    private int clientsHeight;
    private int windowWidth;      
    private int windowHeight;      
    private int checkoutWidth;
    private int checkoutHeight;
    private int doorHeight;
    
    private int checkoutYPosition;
    private int doorPositionY;
    private int maximumNumberOfClientsInQueue;

    private SpriteManager spriteManager;

    public CustomLayout (JPanel buttonsPanel) {
    	


        spriteManager = ApplicationConfiguration.getInstance().getSpriteManager();
        this.buttonsPanel=buttonsPanel;

    }

    public void initialize(int checkoutsAmount) {
        BufferedImage imgStoreCheckout=spriteManager.getSprite(SpriteType.STORE_CHECKOUT).getSprite(0, 0);
        BufferedImage imgBackground=spriteManager.getSprite(SpriteType.BACKGROUND).getSprite(0, 0);
        BufferedImage imgDoor= spriteManager.getSprite(SpriteType.DOOR).getSprite(0, 0);
        BufferedImage imgClient = spriteManager.getSprite(SpriteType.CLIENT).getSprite(0, 0);

        doorHeight =imgDoor.getHeight();

        clientsWidth=imgClient.getWidth();
        clientsHeight=imgClient.getHeight();

        checkoutWidth =imgStoreCheckout.getWidth();
        checkoutHeight =imgStoreCheckout.getHeight();

        int backgroundWidth=imgBackground.getWidth();
        int backgroundHeight=imgBackground.getHeight();

        checkoutProportions =(double)backgroundWidth/(double)backgroundHeight;

        calculateWindowSize(checkoutsAmount);
        doorPositionY = verticalMarginBetweenObjects;
        checkoutYPosition = doorPositionY + doorHeight + verticalMarginBetweenObjects;
    }

    public void calculateWindowSize(int numberOfCheckouts){
    	 
    	int minimumWindowWidth=(int)(minimumWindowHeight* checkoutProportions);
    	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final double SCREEN_WIDTH = screenSize.getWidth();
        final double SCREEN_HEIGHT = screenSize.getHeight();

        int maximumWindowWidth;
        if (SCREEN_WIDTH/ checkoutProportions <SCREEN_HEIGHT){
            maximumWindowWidth=(int)(SCREEN_WIDTH);
        }
        else{
            maximumWindowWidth=(int)((SCREEN_HEIGHT- verticalPadding)* checkoutProportions);
//            System.out.println("2");
        }

        if (numberOfCheckouts* checkoutWidth <=minimumWindowWidth){
           windowWidth=minimumWindowWidth;
           windowHeight=(int)(windowWidth/ checkoutProportions - verticalPadding);
           spaceBetweenCheckouts = (minimumWindowWidth-numberOfCheckouts* checkoutWidth) /(numberOfCheckouts+1);
        }
        
        else{

            if (numberOfCheckouts* checkoutWidth <=maximumWindowWidth){
               spaceBetweenCheckouts = (maximumWindowWidth-
                       numberOfCheckouts* checkoutWidth - horizontalPadding)/(numberOfCheckouts+1);
            }
            else{
                spaceBetweenCheckouts =10;
                checkoutWidth = (maximumWindowWidth- horizontalPadding) /numberOfCheckouts- spaceBetweenCheckouts;

            }
            windowWidth= spaceBetweenCheckouts +(checkoutWidth + spaceBetweenCheckouts)*numberOfCheckouts;
            windowHeight=(int)(windowWidth/ checkoutProportions);
        }

        maximumNumberOfClientsInQueue =(windowHeight- checkoutHeight - verticalMarginBetweenObjects)/clientsHeight-2;
    }



   public Point calculateClientDestinationCoordinates(int clientNumber, int queueNumber, ClientPositionType position){
       int x=0;
       int y=0;

       switch (position){
           case GOING_TO_QUEUE:
               if (clientNumber< maximumNumberOfClientsInQueue){
                   y= checkoutYPosition + checkoutHeight +clientsHeight*(clientNumber);
               }
               else{
                   y= checkoutYPosition + checkoutHeight +clientsHeight*(maximumNumberOfClientsInQueue);
               }

               x=(int)((queueNumber+1)*(spaceBetweenCheckouts + checkoutWidth)-0.5*(checkoutWidth
                       +clientsWidth));
               break;
           case ARRIVAL:
               x=(buttonsPanel.getWidth()+clientsWidth)/2;
               y=buttonsPanel.getLocation().y;
               break;
           case WAITING_ROOM:
               x=(buttonsPanel.getWidth()-clientsWidth)/2;
               y=buttonsPanel.getLocation().y-clientsHeight;
               break;
           case OUTSIDE_VIEW:
               Point doorPosition= calculateDoorPosition();
               y=doorPosition.y;
               x=doorPosition.x;
               break;


       }


       return new Point(x,y);

   }

   public Point calculateCheckoutPosition(int checkoutIndex){
        return new Point(spaceBetweenCheckouts +checkoutIndex*(checkoutWidth +
                spaceBetweenCheckouts), checkoutYPosition);
   }
   
   public Point calculateQueueIndicatorPosition(int queueNumber){
       Point a = calculateClientDestinationCoordinates(maximumNumberOfClientsInQueue,
		   	   queueNumber, ClientPositionType.GOING_TO_QUEUE);
	   
	   return new Point (a.x+clientsWidth,a.y+clientsHeight);
   }

   public Point calculateDoorPosition(){
       return new Point(windowWidth/2, doorPositionY);
   }

	public Dimension getWindowDimensions(){
	    return new Dimension(windowWidth, windowHeight);
	}
    
    public int getMaximumVisibleClients(){
    	return maximumNumberOfClientsInQueue;
    }

    public Rectangle getMovementArea(){
//        return new Rectangle(spaceBetweenObjectsHorizontally,doorsPositionY,windowWidth
//        		-spaceBetweenObjectsHorizontally, buttonsPositionY-doorsPositionY);
    	return new Rectangle(0,0,windowWidth, windowHeight);
    }

	public Point calculateWaitingRoomIndicatorPosition() {
		Point d = calculateClientDestinationCoordinates(0, 0,
                ClientPositionType.WAITING_ROOM);
		return new Point (d.x+clientsWidth, d.y+clientsHeight);
	}


}
