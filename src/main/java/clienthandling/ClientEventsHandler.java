package clienthandling;

import constants.ClientPositionType;
import core.ChangeableObject;
import core.ObjectsManager;
import dto.ClientActionDTO;
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

    public ClientEventsHandler(ObjectsManager objectsManager, ExitQueueManager exitQueueManager, ClientMovement clientMovement, AppLayoutManager appLayoutManager, SimulationPanel simulationPanel) {
        this.objectsManager = objectsManager;
        this.exitQueueManager = exitQueueManager;
        this.clientMovement = clientMovement;
        this.appLayoutManager = appLayoutManager;
        this.simulationPanel = simulationPanel;
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
        for (Client visibleClient : objectsManager.getVisibleClients()) {
            if (visibleClient.stopped()){
                handleClientStoppedMoving(visibleClient, currentTime);
            }
        }
        if (setOfEvents.isEmpty()){
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
            case WAITING_ROOM:
                ClientActionDTO clientActionDTO = new ClientActionDTO(clientMovement.calculateTimeOfMovingToQueue(client.getArrivalTime()), ClientPositionType.GOING_TO_QUEUE, client);
                setOfEvents.add(clientActionDTO);
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
    }

    private void moveClientToWaitingRoom(Client client, double currentTime)  {
        client.setPositionType(ClientPositionType.WAITING_ROOM);
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
