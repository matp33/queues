package events;

import symulation.ApplicationConfiguration;
import visualComponents.Client;
import visualComponents.Door;
import visualComponents.StoreCheckout;

import java.util.*;

public class ObjectsManager {

    private Door door;

    private Set<StoreCheckout> storeCheckouts = new HashSet<>();

    private Map<Integer, Deque<Client>> clientsInQueue = new HashMap<>();

    public void initializeObjects (){
        this.door = new Door(0);
        door.initializePosition();
        int numberOfQueues = ApplicationConfiguration.getInstance().getNumberOfQueues();
        for (int i=0;i<numberOfQueues;i++){
            storeCheckouts.add(new StoreCheckout(i));
            clientsInQueue.put(i, new ArrayDeque<>());
        }
    }

    public Deque<Client> getClientsInQueue (int queueNumber){
        return clientsInQueue.get(queueNumber);
    }

    public Door getDoor() {
        return door;
    }


    public boolean isClientInCheckout (Client client){
        return clientsInQueue.get(client.getQueueNumber()).getFirst().equals(client);
    }

}
