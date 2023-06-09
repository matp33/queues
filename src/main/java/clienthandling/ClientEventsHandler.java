package clienthandling;

import constants.ClientPositionType;
import constants.UIEventType;
import core.ChangeableObject;
import core.ObjectsManager;
import dto.ClientActionDTO;
import events.UIEvent;
import events.UIEventQueue;
import navmesh.GridHandler;
import sprites.SpriteManager;
import utilities.ClientMovement;
import spring2.Bean;
import simulation.AppLayoutManager;
import view.SimulationPanel;
import visualComponents.Client;
import visualComponents.Door;
import visualComponents.StoreCheckout;

import java.awt.*;
import java.util.*;

@Bean
public class ClientEventsHandler implements ChangeableObject {

    private SortedSet<ClientActionDTO> setOfEvents = new TreeSet<>();

    private ObjectsManager objectsManager;

    private ExitQueueManager exitQueueManager;

    private ClientMovement clientMovement;

    private AppLayoutManager appLayoutManager;

    private SimulationPanel simulationPanel;

    private UIEventQueue uiEventQueue;

    private GridHandler gridHandler;



    public ClientEventsHandler(ObjectsManager objectsManager, ExitQueueManager exitQueueManager, ClientMovement clientMovement, AppLayoutManager appLayoutManager, SimulationPanel simulationPanel, UIEventQueue uiEventQueue, GridHandler gridHandler) {
        this.objectsManager = objectsManager;
        this.exitQueueManager = exitQueueManager;
        this.clientMovement = clientMovement;
        this.appLayoutManager = appLayoutManager;
        this.simulationPanel = simulationPanel;
        this.uiEventQueue = uiEventQueue;
        this.gridHandler = gridHandler;
    }

    public void setEventsList(SortedSet<ClientActionDTO> setOfEvents) {
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
        gridHandler.clearTemporarilyOccupiedCells();
        for (Client visibleClient : objectsManager.getVisibleClients()) {
            Point clientPosition = visibleClient.getPosition();
            gridHandler.markCellsOccupied(clientPosition, new Dimension(SpriteManager.CLIENT_WIDTH, SpriteManager.CLIENT_HEIGHT), true);
            if (visibleClient.stopped()){
                handleClientStoppedMoving(visibleClient, currentTime);
            }
        }
        if (setOfEvents.isEmpty() ){
            if ( objectsManager.getVisibleClients().isEmpty()){
                uiEventQueue.publishNewEvent(new UIEvent<>(UIEventType.SIMULATION_FINISHED, new Object()));
            }
            return;
        }
        Iterator<ClientActionDTO> iterator = setOfEvents.iterator();
        ClientActionDTO clientActionDTO = iterator.next();
        double actionTime= clientActionDTO.getTime();

        if (currentTime < actionTime){
            return;
        }

        iterator.remove();

        ClientPositionType action= clientActionDTO.getClientPositionType();
        Client client= clientActionDTO.getClient();

        switch (action){
            case QUEUE_FOR_ENTRANCE:
                moveClientToQueueForEntrance(client);
                break;
            case ENTRANCE:
                moveClientToEntrance(client);
                break;
            case GOING_TO_QUEUE:
                moveClientToQueue(client);
                break;
            case EXITING:
                moveClientToExit(client);
                break;
        }
    }

    private void moveClientToQueueForEntrance(Client client) {
        Point lookAtPoint= appLayoutManager.calculateClientDestinationCoordinates(client.getClientNumber(),
                client.getQueueNumber(), client.getPositionType());
        clientMovement.calculateAndSetClientTrajectory(client, lookAtPoint);
    }

    private void handleClientStoppedMoving(Client client, double timePassed) {
        switch (client.getPositionType()){
            case GOING_TO_QUEUE:
                if (objectsManager.isClientInCheckout(client)){
                    client.setPositionType(ClientPositionType.IDLE);
                    ClientActionDTO clientActionDTO = new ClientActionDTO(timePassed + client.getTimeInCheckout(), ClientPositionType.EXITING, client);
                    setOfEvents.add(clientActionDTO);
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
                simulationPanel.removeObject(client);
                exitQueueManager.handleClientWentOutsideView(client);
                break;
            case ENTRANCE:
                ClientActionDTO clientActionDTO = new ClientActionDTO(clientMovement.calculateTimeOfMovingToQueue(timePassed), ClientPositionType.GOING_TO_QUEUE, client);
                setOfEvents.add(clientActionDTO);
                client.setPositionType(ClientPositionType.IDLE);
                break;
            case QUEUE_FOR_ENTRANCE:
                boolean anyClientInEntrance = objectsManager.isAnyClientInEntrance();
                if (!anyClientInEntrance && objectsManager.isFirstClientInQueueToEntrance(client)){
                    client.setPositionType(ClientPositionType.ENTRANCE);
                    moveClientToEntrance(client);
                    objectsManager.setClientInEntrance(client);
                    objectsManager.removeClientFromQueueToEntrance(client);
                }
                else{
                    objectsManager.addClientToQueueToEntrance(client);
                }
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
            if (clientToMove.getClientNumber()+1== appLayoutManager.getMaximumVisibleClients()){
                storeCheckout.decreaseClientsAboveLimit();
            }
            clientToMove.decreaseClientIndex();
            Point lookAtPoint= appLayoutManager.calculateClientDestinationCoordinates(clientToMove.getClientNumber(),
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
        Point lookAtPoint= appLayoutManager.calculateClientDestinationCoordinates(client.getClientNumber(), client.getQueueNumber(), client.getPositionType());
        clientMovement.calculateAndSetClientTrajectory(client, lookAtPoint);
        if (client.equals(objectsManager.getClientInEntrance())){
            objectsManager.clearClientInEntrance();
        }
    }

    private void moveClientToEntrance(Client client)  {
        Point lookAtPoint= appLayoutManager.calculateClientDestinationCoordinates(client.getClientNumber(),
                client.getQueueNumber(), client.getPositionType());
        clientMovement.calculateAndSetClientTrajectory(client, lookAtPoint);
    }

    private void moveOutside(Client client){
        client.setPositionType(ClientPositionType.OUTSIDE_VIEW);
        Point lookAtPoint= appLayoutManager.calculateClientDestinationCoordinates(client.getClientNumber(),
                client.getQueueNumber(), client.getPositionType());
        clientMovement.calculateAndSetClientTrajectory(client, lookAtPoint);
    }

}
