

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
import visualComponents.Client;

public class CustomLayout {

//    private final JButton mainButton; // from this button we put all others to the left
    
    private final int minimumWindowHeight=700;
    private final int freeSpaceForScreen=10; 		// for the window to not hide even partially
    private final int freeSpaceScreenVertically=50; 
    private final int spaceVertically=10; 			// 1st objects start at this height
    private final JPanel buttonsPanel;
    
    // variables //
    private boolean isScaled;         
    private double queueProportions;           		// background image original proportions
    
    private int spaceBetweenObjectsHorizontally;
    private int clientsWidth;
    private int clientsHeight;
    private int windowWidth;      
    private int windowHeight;      
    private int tillsWidth;      
    private int tillsHeight;       
    private int doorsHeight;
    
    private int tillsPositionY;
    private int doorsPositionY;
    
    private int numberOfQueues;
    
    
    private int maximumClientsInQueueVisible;

    private SpriteManager spriteManager;

    public CustomLayout (int numberOfQueues, JPanel buttonsPanel) throws IOException {
    	

        spriteManager = new SpriteManager();
     this.numberOfQueues=numberOfQueues;	
     this.buttonsPanel=buttonsPanel;
     BufferedImage imgCashRegister=spriteManager.getSprite(SpriteType.QUEUE).getSprite(0, 0);
     BufferedImage imgBackground=spriteManager.getSprite(SpriteType.BACKGROUND).getSprite(0, 0);
     BufferedImage imgDoor= spriteManager.getSprite(SpriteType.DOOR).getSprite(0, 0);
     BufferedImage imgClient = spriteManager.getSprite(SpriteType.CLIENT).getSprite(0, 0);

     doorsHeight=imgDoor.getHeight();

     clientsWidth=imgClient.getWidth();
     clientsHeight=imgClient.getHeight();

     tillsWidth=imgCashRegister.getWidth();
     tillsHeight=imgCashRegister.getHeight();

     int backgroundWidth=imgBackground.getWidth();
     int backgroundHeight=imgBackground.getHeight();

     queueProportions=(double)backgroundWidth/(double)backgroundHeight;
    
     calculateWindowSize(numberOfQueues);     
     calculateRelativePosition();
    
    }
    
    public void calculateWindowSize(int numberOfQueues){
    	 
    	int minimumWindowWidth=(int)(minimumWindowHeight*queueProportions);
    	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final double SCREEN_WIDTH = screenSize.getWidth();
        final double SCREEN_HEIGHT = screenSize.getHeight();

        int maximumWindowWidth;
        if (SCREEN_WIDTH/queueProportions<SCREEN_HEIGHT){
            maximumWindowWidth=(int)(SCREEN_WIDTH);
        }
        else{
            maximumWindowWidth=(int)((SCREEN_HEIGHT-freeSpaceScreenVertically)*queueProportions);
//            System.out.println("2");
        }

        if (numberOfQueues*tillsWidth<=minimumWindowWidth){
           windowWidth=minimumWindowWidth;
           windowHeight=(int)(windowWidth/queueProportions-freeSpaceScreenVertically);
           spaceBetweenObjectsHorizontally=(int)(minimumWindowWidth-numberOfQueues*tillsWidth)/(numberOfQueues+1);
        }
        
        else{

            if (numberOfQueues*tillsWidth<=maximumWindowWidth){
               spaceBetweenObjectsHorizontally=(int)((maximumWindowWidth-
                       numberOfQueues*tillsWidth-freeSpaceForScreen)/(numberOfQueues+1));
            }
            else{
                spaceBetweenObjectsHorizontally=10;
                tillsWidth=(int)(maximumWindowWidth-freeSpaceForScreen)/numberOfQueues-spaceBetweenObjectsHorizontally;
                isScaled=true;

            }
            windowWidth=spaceBetweenObjectsHorizontally+(tillsWidth+spaceBetweenObjectsHorizontally)*numberOfQueues;
            windowHeight=(int)(windowWidth/queueProportions); 
        }

        maximumClientsInQueueVisible=(windowHeight-tillsHeight-spaceVertically)/clientsHeight-2;
    }

    private void calculateRelativePosition(){

        doorsPositionY=spaceVertically;
        tillsPositionY=doorsPositionY+doorsHeight+spaceVertically;        
        
    }
    
   // ************************* Calculating methods ************************** //

   public Dimension calculateClientsCoordinates(int clientNumber, int queueNumber, ClientPositionType position){
       int x=0;
       int y=0;

       if ( position==ClientPositionType.GOING_TO_QUEUE || position==ClientPositionType.WAITING_IN_QUEUE ){

            if (clientNumber<maximumClientsInQueueVisible){
                y=tillsPositionY+tillsHeight+clientsHeight*(clientNumber);
            }
            else{
                y=tillsPositionY+tillsHeight+clientsHeight*(maximumClientsInQueueVisible);
            }
            
            x=(int)((queueNumber+1)*(spaceBetweenObjectsHorizontally+tillsWidth)-0.5*(tillsWidth
                    +clientsWidth));

        }

       if (position==ClientPositionType.ARRIVAL){
            x=(buttonsPanel.getWidth()+clientsWidth)/2; 
            y=buttonsPanel.getLocation().y;
       }

       if ( position==ClientPositionType.WAITING_ROOM){
    	    x=(buttonsPanel.getWidth()-clientsWidth)/2;
        	y=buttonsPanel.getLocation().y-clientsHeight;           
       }

       if (position==ClientPositionType.EXITING){
    	   
    	   int direction=1;
    	   if (isQueueLeftToDoors(queueNumber)){
    		   System.out.println("left: "+queueNumber);
    		   direction=-1;
    	   }	
    	   
    	   Dimension d= calculateDoorPosition();
           y=d.height+20;
           x=d.width+clientNumber*clientsWidth*direction;
       }
       
       if (position == ClientPositionType.OUTSIDE_VIEW){
    	   Dimension d= calculateDoorPosition();
    	   y=d.height;
    	   x=d.width;
       }
//       System.out.println(" dim "+x+" y "+y);	
       return new Dimension (x,y);

   }
   
   private boolean isQueueLeftToDoors (int queueNumber){
	   return calculateTillsPosition(queueNumber).width<calculateDoorPosition().width;
   }

   public Dimension calculateTillsPosition(int tillNumber){
        return new Dimension(spaceBetweenObjectsHorizontally+tillNumber*(tillsWidth+
        					 spaceBetweenObjectsHorizontally), tillsPositionY);
   }
   
   public Dimension calculateQueueIndicator(int queueNumber){
	   Dimension a = calculateClientsCoordinates(maximumClientsInQueueVisible, 
		   	   queueNumber, ClientPositionType.GOING_TO_QUEUE);
	   
	   return new Dimension (a.width+clientsWidth,a.height+clientsHeight);
   }

   public Dimension calculateDoorPosition(){
       return new Dimension(windowWidth/2, doorsPositionY);
   }
   
   

    // ************************************* Getters ******************************** //

	public Dimension getWindowDimensions(){
	    return new Dimension(windowWidth, windowHeight);
	}
    
    public Dimension getTillDimensions(){
       return new Dimension (tillsWidth,tillsHeight);
    }

    public boolean isTillScaled(){
       return isScaled;
    }
    
    public int getMaximumVisibleClients(){
    	return maximumClientsInQueueVisible;
    }

    public Rectangle getMovementArea(){
//        return new Rectangle(spaceBetweenObjectsHorizontally,doorsPositionY,windowWidth
//        		-spaceBetweenObjectsHorizontally, buttonsPositionY-doorsPositionY);
    	return new Rectangle(0,0,windowWidth, windowHeight);
    }

	public Dimension calculateWaitingRoomIndicatorPosition() {
		Dimension d = calculateClientsCoordinates(0, 0,
                ClientPositionType.WAITING_ROOM);
		return new Dimension (d.width+clientsWidth, d.height+clientsHeight);
	}

	public void setNumberOfQueues(int number){
		numberOfQueues=number;
	}

}
