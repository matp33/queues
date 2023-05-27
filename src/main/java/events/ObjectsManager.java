package events;

import symulation.ApplicationConfiguration;
import symulation.CustomLayout;
import visualComponents.Client;
import visualComponents.Door;
import visualComponents.StoreCheckout;

import java.util.*;

public class ObjectsManager {

    private Door door;

    private Set<StoreCheckout> storeCheckouts = new HashSet<>();

    private Map<Integer, Deque<Client>> clientsInQueue = new HashMap<>();

    private Deque<Client> clientsMovingToExit = new ArrayDeque<>();

    private Client clientByTheDoor;

    public void initializeObjects (){
        this.door = new Door(0);
        door.initializePosition();
        int numberOfQueues = ApplicationConfiguration.getInstance().getNumberOfQueues();
        for (int i=0;i<numberOfQueues;i++){
            storeCheckouts.add(new StoreCheckout(i));
            clientsInQueue.put(i, new ArrayDeque<>());
        }
    }

    public void setClientByTheDoor(Client clientByTheDoor) {
        this.clientByTheDoor = clientByTheDoor;
    }

    public Client getClientByTheDoor() {
        return clientByTheDoor;
    }

    public Deque<Client> getClientsInQueue (int queueNumber){
        return clientsInQueue.get(queueNumber);
    }

    public Deque<Client> getClientsMovingToExit() {
        return clientsMovingToExit;
    }

    public Door getDoor() {
        return door;
    }


    public boolean isClientInCheckout (Client client){
        return clientsInQueue.get(client.getQueueNumber()).getFirst().equals(client);
    }

    public Client getClientClosestToDoor (){
        return CustomLayout.getClientClosestToDoor( clientsMovingToExit, door);

    }

}
