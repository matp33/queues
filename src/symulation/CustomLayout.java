

package symulation;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JPanel;

import constants.ClientPositionType;
import sprites.SpriteManager;
import sprites.SpriteType;

public class CustomLayout {

    private final int minimumWindowHeight=700;
    private final int horizontalPadding =10;
    private final int verticalPadding =50;
    private final int verticalMarginBetweenObjects =10;
    private final JPanel buttonsPanel;
    private double checkoutProportions;
    private int spaceBetweenCashRegisters;
    private int clientsWidth;
    private int clientsHeight;
    private int windowWidth;      
    private int windowHeight;      
    private int cashRegisterWidth;
    private int cashRegisterHeight;
    private int doorHeight;
    
    private int cashRegisterYPosition;
    private int doorPositionY;
    private int maximumNumberOfClientsInQueue;

    private SpriteManager spriteManager;

    public CustomLayout (int checkoutsAmount, JPanel buttonsPanel) throws IOException {
    	

        spriteManager = new SpriteManager();
        this.buttonsPanel=buttonsPanel;
        BufferedImage imgCashRegister=spriteManager.getSprite(SpriteType.STORE_CHECKOUT).getSprite(0, 0);
        BufferedImage imgBackground=spriteManager.getSprite(SpriteType.BACKGROUND).getSprite(0, 0);
        BufferedImage imgDoor= spriteManager.getSprite(SpriteType.DOOR).getSprite(0, 0);
        BufferedImage imgClient = spriteManager.getSprite(SpriteType.CLIENT).getSprite(0, 0);

        doorHeight =imgDoor.getHeight();

        clientsWidth=imgClient.getWidth();
        clientsHeight=imgClient.getHeight();

        cashRegisterWidth =imgCashRegister.getWidth();
        cashRegisterHeight =imgCashRegister.getHeight();

        int backgroundWidth=imgBackground.getWidth();
        int backgroundHeight=imgBackground.getHeight();

        checkoutProportions =(double)backgroundWidth/(double)backgroundHeight;

        calculateWindowSize(checkoutsAmount);
        doorPositionY = verticalMarginBetweenObjects;
        cashRegisterYPosition = doorPositionY + doorHeight + verticalMarginBetweenObjects;
    
    }
    
    public void calculateWindowSize(int numberOfCashRegisters){
    	 
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

        if (numberOfCashRegisters* cashRegisterWidth <=minimumWindowWidth){
           windowWidth=minimumWindowWidth;
           windowHeight=(int)(windowWidth/ checkoutProportions - verticalPadding);
           spaceBetweenCashRegisters = (minimumWindowWidth-numberOfCashRegisters* cashRegisterWidth) /(numberOfCashRegisters+1);
        }
        
        else{

            if (numberOfCashRegisters* cashRegisterWidth <=maximumWindowWidth){
               spaceBetweenCashRegisters = (maximumWindowWidth-
                       numberOfCashRegisters* cashRegisterWidth - horizontalPadding)/(numberOfCashRegisters+1);
            }
            else{
                spaceBetweenCashRegisters =10;
                cashRegisterWidth = (maximumWindowWidth- horizontalPadding) /numberOfCashRegisters- spaceBetweenCashRegisters;

            }
            windowWidth= spaceBetweenCashRegisters +(cashRegisterWidth + spaceBetweenCashRegisters)*numberOfCashRegisters;
            windowHeight=(int)(windowWidth/ checkoutProportions);
        }

        maximumNumberOfClientsInQueue =(windowHeight- cashRegisterHeight - verticalMarginBetweenObjects)/clientsHeight-2;
    }



   public Dimension calculateClientDestinationCoordinates(int clientNumber, int queueNumber, ClientPositionType position){
       int x=0;
       int y=0;

       switch (position){
           case GOING_TO_QUEUE:
           case WAITING_IN_QUEUE:
               if (clientNumber< maximumNumberOfClientsInQueue){
                   y= cashRegisterYPosition + cashRegisterHeight +clientsHeight*(clientNumber);
               }
               else{
                   y= cashRegisterYPosition + cashRegisterHeight +clientsHeight*(maximumNumberOfClientsInQueue);
               }

               x=(int)((queueNumber+1)*(spaceBetweenCashRegisters + cashRegisterWidth)-0.5*(cashRegisterWidth
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
           case EXITING:
               int direction=1;
               if (isQueueLeftSideOfDoors(queueNumber)){
                   direction=-1;
               }

               Dimension d= calculateDoorPosition();
               y=d.height+20;
               x=d.width+clientNumber*clientsWidth*direction;
               break;
           case OUTSIDE_VIEW:
               Dimension doorPosition= calculateDoorPosition();
               y=doorPosition.height;
               x=doorPosition.width;
               break;


       }


       return new Dimension (x,y);

   }
   
   private boolean isQueueLeftSideOfDoors(int queueNumber){
	   return calculateCashRegisterPosition(queueNumber).width<calculateDoorPosition().width;
   }

   public Dimension calculateCashRegisterPosition(int tillNumber){
        return new Dimension(spaceBetweenCashRegisters +tillNumber*(cashRegisterWidth +
                spaceBetweenCashRegisters), cashRegisterYPosition);
   }
   
   public Dimension calculateQueueIndicatorPosition(int queueNumber){
	   Dimension a = calculateClientDestinationCoordinates(maximumNumberOfClientsInQueue,
		   	   queueNumber, ClientPositionType.GOING_TO_QUEUE);
	   
	   return new Dimension (a.width+clientsWidth,a.height+clientsHeight);
   }

   public Dimension calculateDoorPosition(){
       return new Dimension(windowWidth/2, doorPositionY);
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

	public Dimension calculateWaitingRoomIndicatorPosition() {
		Dimension d = calculateClientDestinationCoordinates(0, 0,
                ClientPositionType.WAITING_ROOM);
		return new Dimension (d.width+clientsWidth, d.height+clientsHeight);
	}


}
