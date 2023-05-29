package events;

import clienthandling.ExitQueueManager;
import constants.ClientPositionType;
import core.ChangeableObject;
import otherFunctions.ClientAction;
import symulation.ApplicationConfiguration;
import symulation.Painter;
import visualComponents.Client;
import visualComponents.Door;

import java.util.*;

public class ClientEventsHandler implements ChangeableObject {

    private SortedSet<ClientAction> setOfEvents = new TreeSet<>();

    private Painter painter;

    private ObjectsManager objectsManager;

    private ExitQueueManager exitQueueManager;

    public ClientEventsHandler() {
        painter = ApplicationConfiguration.getInstance().getPainter();
        objectsManager = ApplicationConfiguration.getInstance().getObjectsManager();
        exitQueueManager = ApplicationConfiguration.getInstance().getExitQueueManager();
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
                moveClientToWaitingRoom(client);
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
        clientsInQueue.forEach(Client::moveUpInQueue);
        exitQueueManager.moveClientToExit(client, objectsManager.getClientsMovingToExit());

    }

    private void moveClientToQueue (Client client){
        Deque<Client> clientsInQueue = objectsManager.getClientsInQueue(client.getQueueNumber());
        client.setClientNumber(clientsInQueue.size());
        clientsInQueue.offerLast(client);
        client.setPositionType(ClientPositionType.GOING_TO_QUEUE);
        client.calculateTrajectory();
        client.calculateExpectedTimeInQueue();
    }

    private void moveClientToWaitingRoom(Client client)  {
        client.setPositionType(ClientPositionType.WAITING_ROOM);
        client.calculateTrajectory();
        client.calculateExpectedTimeInWaitingRoom();
    }

    private void moveOutside(Client client){
        client.setPositionType(ClientPositionType.OUTSIDE_VIEW);
        client.calculateTrajectory();
    }

}
