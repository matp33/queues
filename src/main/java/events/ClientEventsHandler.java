package events;

import clienthandling.ExitQueueManager;
import constants.ClientPositionType;
import core.ChangeableObject;
import otherFunctions.ClientAction;
import otherFunctions.ClientMovement;
import spring2.Bean;
import symulation.CustomLayout;
import symulation.Painter;
import visualComponents.Client;
import visualComponents.Door;
import visualComponents.StoreCheckout;

import java.awt.*;
import java.util.*;

@Bean
public class ClientEventsHandler implements ChangeableObject {

    private SortedSet<ClientAction> setOfEvents = new TreeSet<>();

    private Painter painter;

    private ObjectsManager objectsManager;

    private ExitQueueManager exitQueueManager;

    private ClientMovement clientMovement;

    private CustomLayout customLayout;

    public ClientEventsHandler(Painter painter, ObjectsManager objectsManager, ExitQueueManager exitQueueManager, ClientMovement clientMovement, CustomLayout customLayout) {
        this.painter = painter;
        this.objectsManager = objectsManager;
        this.exitQueueManager = exitQueueManager;
        this.clientMovement = clientMovement;
        this.customLayout = customLayout;
    }

    public void setEventsList(SortedSet<ClientAction> setOfEvents) {
        this.setOfEvents = setOfEvents;
    }
    @Override
    public void update(double currentTime) {
        if (objectsManager.getDoor().isOpened()){ //TODO not sure if its right place, maybe another class should handle it
            Client clientByTheDoor = objectsManager.getClientByTheDoor();
            if (clientByTheDoor.getPositionType().equals(ClientPositionType.EXITING)){
                moveOutside(clientByTheDoor);
                objectsManager.removeClientFromQueueToExit(clientByTheDoor);

            }
        }
        for (Client visibleClient : objectsManager.getVisibleClients()) {
            if (visibleClient.stopped()){
                handleClientStoppedMoving(visibleClient, currentTime);
            }
        }
        if (setOfEvents.isEmpty()){
            return;
        }
        Iterator<ClientAction> iterator = setOfEvents.iterator();
        ClientAction clientAction = iterator.next();
        double actionTime=clientAction.getTime();

        if (currentTime < actionTime){
            return;
        }

        iterator.remove();

        ClientPositionType action=clientAction.getClientPositionType();
        Client client=clientAction.getClient();

        switch (action){
            case ARRIVAL:
                moveClientToWaitingRoom(client, currentTime);
                break;
            case GOING_TO_QUEUE:
                moveClientToQueue(client);
                break;
            case EXITING:
                moveClientToExit(client);
                break;
        }
    }

    public void handleClientStoppedMoving(Client client, double timePassed) {
        switch (client.getPositionType()){
            case GOING_TO_QUEUE:
                if (objectsManager.isClientInCheckout(client)){
                    client.setPositionType(ClientPositionType.EXITING); //TODO this is confusing to have client position type and client event type
                    ClientAction clientAction = new ClientAction(timePassed + client.getTimeInCheckout(), ClientPositionType.EXITING, client);
                    setOfEvents.add(clientAction);
                }
                break;
            case EXITING:
                Door door = objectsManager.getDoor();
                if (objectsManager.getClientClosestToDoor()
                        .map(clientDTO->clientDTO.getClient().equals(client)).orElse(false)){
                    door.doOpening();
                    objectsManager.setClientByTheDoor(client);
                }
                break;
            case OUTSIDE_VIEW:
                objectsManager.removeClientFromView(client);
                painter.removeObject(client);
                exitQueueManager.handleClientWentOutsideView(client);
                break;
            case WAITING_ROOM:
                ClientAction clientAction = new ClientAction(client.calculateTimeOfMovingToQueue(), ClientPositionType.GOING_TO_QUEUE, client);
                setOfEvents.add(clientAction);
                break;


        }
    }

    private void moveClientToExit(Client client)  {
        int queueNumber = client.getQueueNumber();

        client.setPositionType(ClientPositionType.EXITING);
        Deque<Client> clientsInQueue = objectsManager.getClientsInQueue(queueNumber);
        clientsInQueue.removeFirst();
        clientsInQueue.forEach(clientToMove -> {
            StoreCheckout storeCheckout = objectsManager.getStoreCheckout(clientToMove.getQueueNumber());
            if (clientToMove.getClientNumber()+1== customLayout.getMaximumVisibleClients()){
                storeCheckout.decreaseClientsAboveLimit();
            }
            clientToMove.decreaseClientIndex();
            Point lookAtPoint=painter.calculateClientDestinationCoordinates(clientToMove.getClientNumber(),
                    clientToMove.getQueueNumber(), clientToMove.getPositionType());
            clientMovement.calculateAndSetClientTrajectory(clientToMove, lookAtPoint);
        });
        exitQueueManager.moveClientToExit(client, objectsManager.getClientsMovingToExit());

    }

    private void moveClientToQueue (Client client){
        Deque<Client> clientsInQueue = objectsManager.getClientsInQueue(client.getQueueNumber());
        client.setClientNumber(clientsInQueue.size());
        clientsInQueue.offerLast(client);
        client.setPositionType(ClientPositionType.GOING_TO_QUEUE);
        Point lookAtPoint=painter.calculateClientDestinationCoordinates(client.getClientNumber(), client.getQueueNumber(), client.getPositionType());
        clientMovement.calculateAndSetClientTrajectory(client, lookAtPoint);
        client.calculateExpectedTimeInQueue();
    }

    private void moveClientToWaitingRoom(Client client, double currentTime)  {
        client.setPositionType(ClientPositionType.WAITING_ROOM);
        Point lookAtPoint=painter.calculateClientDestinationCoordinates(client.getClientNumber(),
                client.getQueueNumber(), client.getPositionType());
        clientMovement.calculateAndSetClientTrajectory(client, lookAtPoint);
        client.calculateExpectedTimeInWaitingRoom(currentTime);
    }

    private void moveOutside(Client client){
        client.setPositionType(ClientPositionType.OUTSIDE_VIEW);
        Point lookAtPoint=painter.calculateClientDestinationCoordinates(client.getClientNumber(),
                client.getQueueNumber(), client.getPositionType());
        clientMovement.calculateAndSetClientTrajectory(client, lookAtPoint);
    }

}
