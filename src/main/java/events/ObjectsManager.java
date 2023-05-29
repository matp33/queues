package events;

import constants.PositionInQueueToExit;
import core.MainLoop;
import dto.ClientToExitDTO;
import dto.PointWithTimeDTO;
import otherFunctions.ClientMovement;
import symulation.ApplicationConfiguration;
import visualComponents.Client;
import visualComponents.Door;
import visualComponents.StoreCheckout;

import java.util.*;
import java.util.stream.Collectors;

public class ObjectsManager {

    private Door door;

    private Set<StoreCheckout> storeCheckouts = new HashSet<>();

    private Map<Integer, Deque<Client>> clientsInQueue = new HashMap<>();

    private NavigableSet<ClientToExitDTO> clientsMovingToExit = new TreeSet<>();

    private Client clientByTheDoor;

    public void initializeObjects (){
        this.door = new Door();
        door.initializePosition();
        MainLoop.getInstance().addObject(door);
        int numberOfQueues = ApplicationConfiguration.getInstance().getNumberOfQueues();
        for (int i=0;i<numberOfQueues;i++){
            storeCheckouts.add(new StoreCheckout(i));
            clientsInQueue.put(i, new ArrayDeque<>());
        }
    }

    public Set<StoreCheckout> getStoreCheckouts() {
        return storeCheckouts;
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

    public NavigableSet<ClientToExitDTO> getClientsMovingToExit() {
        return clientsMovingToExit;
    }

    public void removeClientFromQueueToExit (Client client){
        clientsMovingToExit.removeIf(clientToExit->clientToExit.getClient().equals(client));
    }

    public Door getDoor() {
        return door;
    }


    public boolean isClientInCheckout (Client client){
        return clientsInQueue.get(client.getQueueNumber()).getFirst().equals(client);
    }

    public Optional<ClientToExitDTO> getClientClosestToDoor (){
        return clientsMovingToExit.stream().filter(client->client.getPositionInQueueToExit().equals(PositionInQueueToExit.AT_DOOR)).findAny();

    }

}
