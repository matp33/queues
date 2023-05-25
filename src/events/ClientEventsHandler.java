package events;

import constants.ClientPositionType;
import core.ChangeableObject;
import otherFunctions.ClientAction;
import symulation.ApplicationConfiguration;
import symulation.Painter;
import visualComponents.Client;
import visualComponents.Door;

import java.util.*;

public class ClientEventsHandler implements ChangeableObject {

    private List<ClientAction> listOfEvents = new ArrayList<>();

    private Painter painter;

    private ObjectsManager objectsManager;

    public ClientEventsHandler() {
        painter = ApplicationConfiguration.getInstance().getPainter();
        objectsManager = ApplicationConfiguration.getInstance().getObjectsStateHandler();
    }

    public void setEventsList(List<ClientAction> listOfEvents) {
        this.listOfEvents = listOfEvents;
    }
    @Override
    public void update(double currentTime) {
        if (listOfEvents.isEmpty()){
            return;
        }
        ClientAction clientAction=listOfEvents.get(0);
        double actionTime=clientAction.getTime();

        if (currentTime < actionTime){
            return;
        }

        listOfEvents.remove(0);

        ClientPositionType action=clientAction.getClientPositionType();
        Client client=clientAction.getClient();

        switch (action){
            case ARRIVAL:
                moveClientToWaitingRoom(client);
                break;
            case WAITING_IN_QUEUE:
            case GOING_TO_QUEUE:
                moveClientToQueue(client);
                break;
            case PAUSE:
                painter.pauseSimulationAndAskQuestion();
                break;
            case EXITING:
                moveClientToExit(client);
                break;
        }
    }

    public void handleClientStoppedMoving(Client client, double timePassed) {
        switch (client.getPositionType()){
            case GOING_TO_QUEUE:
            case WAITING_IN_QUEUE: //TODO WAITING IN QUEUE SHOULD BE DELETED
                if (objectsManager.isClientInCheckout(client)){
                    ClientAction clientAction = new ClientAction(timePassed + client.getTimeInCheckout(), ClientPositionType.EXITING, client);
                    listOfEvents.add(clientAction); //TODO should be a sorted collection
                    listOfEvents.sort(Comparator.comparing(ClientAction::getTime));
                }
                break;
            case EXITING:
                Door door = objectsManager.getDoor();
                if (door.isFirst(client))
                    door.doOpening();
                break;
            case OUTSIDE_VIEW:
                painter.removeObject(client);
                client.getObjectObservedByMe().removeObserver(client); //TODO remove this field object observed by me
                break;
            case WAITING_ROOM:
                ClientAction clientAction = new ClientAction(client.calculateTimeOfMovingToQueue(), ClientPositionType.GOING_TO_QUEUE, client);
                listOfEvents.add(clientAction);
                listOfEvents.sort(Comparator.comparing(ClientAction::getTime));
                break;


        }
    }

    private void moveClientToExit(Client client)  {
        int queueNumber = client.getQueueNumber();

        client.setPositionType(ClientPositionType.EXITING);
        client.calculateTrajectory();
        client.setObjectObserved(objectsManager.getDoor());
        Deque<Client> clientsInQueue = objectsManager.getClientsInQueue(queueNumber);
        clientsInQueue.removeFirst();
        clientsInQueue.forEach(Client::moveUpInQueue);

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

    //TODO should be private
    public void moveOutside(Client client){
        client.setPositionType(ClientPositionType.OUTSIDE_VIEW);
        client.calculateTrajectory();
    }

}
