

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

   //TODO multiple responsibilities detected, static method
   public static void calculateTimeOfArrivingToDoorOrQueueForDoorAndMoveThere(Client client, NavigableSet<ClientToExitDTO> clientsMovingToExit){
       PointWithTimeDTO positionDoorWithTimeToGetThere = ClientMovement.calculateTimeToGetToDoor(client);
       ClientToExitDTO newClientData;
       if (clientsMovingToExit.isEmpty() ){
           newClientData = new ClientToExitDTO(client, PositionInQueueToExit.AT_DOOR,positionDoorWithTimeToGetThere.getTime(), 0 );
           clientsMovingToExit.add(newClientData);
           shiftClients(clientsMovingToExit, newClientData);
           client.moveToPoint(positionDoorWithTimeToGetThere.getPoint());
           System.out.println("client: "+client.getId());
           System.out.println("client moving to exit, coords: "+positionDoorWithTimeToGetThere.getPoint() +" time " + positionDoorWithTimeToGetThere.getTime());
       } else if (positionDoorWithTimeToGetThere.getTime() < clientsMovingToExit.first().getEstimatedTimeAtDestination()) {
           ClientToExitDTO clientThatWasFirst = clientsMovingToExit.first();
           clientsMovingToExit.remove(clientThatWasFirst);

           clientThatWasFirst = pickQueueCloserToClient(clientThatWasFirst.getClient(), clientsMovingToExit);
           newClientData = new ClientToExitDTO(client, PositionInQueueToExit.AT_DOOR,positionDoorWithTimeToGetThere.getTime(), 0 );
           clientsMovingToExit.add(newClientData);
           client.moveToPoint(positionDoorWithTimeToGetThere.getPoint());
           shiftClients(clientsMovingToExit, clientThatWasFirst);

       } else{
           PointWithTimeDTO destinationPositionAndTimeOfArrival;
           boolean addedToCollection = false;
           for (ClientToExitDTO clientMovingToExit : clientsMovingToExit) {
               destinationPositionAndTimeOfArrival = ClientMovement.calculateTimeToGetToPosition(client, clientMovingToExit.getIndexInPosition(), clientMovingToExit.getPositionInQueueToExit());
               if (destinationPositionAndTimeOfArrival.getTime() < clientMovingToExit.getEstimatedTimeAtDestination()){
                   newClientData = new ClientToExitDTO(client, clientMovingToExit.getPositionInQueueToExit(), destinationPositionAndTimeOfArrival.getTime(), clientMovingToExit.getIndexInPosition() );
                   clientsMovingToExit.add(newClientData);
                   client.moveToPoint(destinationPositionAndTimeOfArrival.getPoint());
                   addedToCollection = true;

                   shiftClients( clientsMovingToExit, newClientData);
                   break;
               }
           }
           if (!addedToCollection){
               pickQueueCloserToClient(client, clientsMovingToExit);
           }
       }
   }

    private static ClientToExitDTO pickQueueCloserToClient(Client client, NavigableSet<ClientToExitDTO> clientsMovingToExit) {
        PointWithTimeAndQueueIndexDTO positionAndTimeToGetToLeftSide = getPositionAndTimeToGetToQueue(client, clientsMovingToExit, PositionInQueueToExit.LEFT);
        PointWithTimeAndQueueIndexDTO positionAndTimeToGetToRightSide = getPositionAndTimeToGetToQueue(client, clientsMovingToExit, PositionInQueueToExit.RIGHT);
        ClientToExitDTO clientToExitDTO;
        Point destinationPoint;
        if (positionAndTimeToGetToLeftSide.getTime()< positionAndTimeToGetToRightSide.getTime()){
            System.out.println("client "+ client.getId() +" picked left side");
            clientToExitDTO = createClientToExitDTO(client, positionAndTimeToGetToLeftSide, PositionInQueueToExit.LEFT);
            destinationPoint = positionAndTimeToGetToLeftSide.getPoint();
        }
        else{
            System.out.println("client "+ client.getId() +" picked right side");
            clientToExitDTO = createClientToExitDTO(client, positionAndTimeToGetToRightSide, PositionInQueueToExit.RIGHT);
            destinationPoint = positionAndTimeToGetToRightSide.getPoint();
        }
        clientsMovingToExit.add(clientToExitDTO);
        client.moveToPoint(destinationPoint);
        return clientToExitDTO;
    }

    private static void shiftClients(NavigableSet<ClientToExitDTO> clientsMovingToExit, ClientToExitDTO clientAfterWhichWeShouldShift) {
        for (ClientToExitDTO clientToShift : clientsMovingToExit.tailSet(clientAfterWhichWeShouldShift, false)) {
            if (clientToShift.getPositionInQueueToExit().equals(clientAfterWhichWeShouldShift.getPositionInQueueToExit())){
                clientToShift.setIndexInPosition(clientToShift.getIndexInPosition()+1);
                PointWithTimeDTO destinationPositionAndTime = ClientMovement.calculateTimeToGetToPosition(clientToShift.getClient(), clientToShift.getIndexInPosition(), clientToShift.getPositionInQueueToExit());
                clientToShift.setEstimatedTimeAtDestination(destinationPositionAndTime.getTime());
                clientToShift.getClient().moveToPoint(destinationPositionAndTime.getPoint());
            }
        }
    }

    private static ClientToExitDTO createClientToExitDTO(Client client, PointWithTimeAndQueueIndexDTO positionAndTimeToGetToLeftSide, PositionInQueueToExit positionInQueueToExit) {
        return new ClientToExitDTO(client, positionInQueueToExit, positionAndTimeToGetToLeftSide.getTime(), positionAndTimeToGetToLeftSide.getIndexInQueue());
    }

    private static PointWithTimeAndQueueIndexDTO getPositionAndTimeToGetToQueue(Client client, NavigableSet<ClientToExitDTO> clientsMovingToExit, PositionInQueueToExit positionInQueueToExit) {
        Optional<ClientToExitDTO> lastInQueueToLeftSide = clientsMovingToExit.stream().filter(clientDTO -> clientDTO.getPositionInQueueToExit().equals(PositionInQueueToExit.LEFT)).max(Comparator.naturalOrder());
        PointWithTimeDTO positionWithTime;
        int indexInPosition;
        if (lastInQueueToLeftSide.isPresent()){
            indexInPosition = lastInQueueToLeftSide.get().getIndexInPosition();
            indexInPosition++;
            positionWithTime = ClientMovement.calculateTimeToGetToPosition(client,indexInPosition, positionInQueueToExit);
        }
        else{
            indexInPosition = 1;
            positionWithTime = ClientMovement.calculateTimeToGetToPosition(client,indexInPosition, positionInQueueToExit);
        }
        return new PointWithTimeAndQueueIndexDTO(positionWithTime.getPoint(), positionWithTime.getTime(), indexInPosition);
    }

    public static Client getClientClosestToDoor (List<Client> clientsMovingToExit, Door door){
       Client clientClosestToDoor = null;
       Point doorPosition = door.getPosition();
       double minDistance = Double.MAX_VALUE;
       for (Client client : clientsMovingToExit) {
           Point thisClientPosition = client.getPosition();
           double distance = doorPosition.distance(thisClientPosition);
           if (distance < minDistance){
               minDistance = distance;
               clientClosestToDoor = client;
           }
       }
       return clientClosestToDoor;
   }
   
   private boolean isQueueLeftSideOfDoors(int checkoutIndex){
	   return calculateCheckoutPosition(checkoutIndex).x<calculateDoorPosition().x;
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
