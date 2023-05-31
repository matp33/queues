package events;

import constants.PositionInQueueToExit;
import core.MainLoop;
import dto.ClientToExitDTO;
import spring2.Bean;
import symulation.ApplicationConfiguration;
import visualComponents.Client;
import visualComponents.Door;
import visualComponents.StoreCheckout;

import java.util.*;

@Bean
public class ObjectsManager {

    private Door door;

    private Set<StoreCheckout> storeCheckouts = new HashSet<>();

    private Map<Integer, Deque<Client>> clientsInQueue = new HashMap<>();

    private NavigableSet<ClientToExitDTO> clientsMovingToExit = new TreeSet<>();

    private Client clientByTheDoor;

    private ApplicationConfiguration applicationConfiguration;

    private MainLoop mainLoop;

    private Set<Client> visibleClients = new HashSet<>();

    public ObjectsManager(ApplicationConfiguration applicationConfiguration, MainLoop mainLoop) {
        this.applicationConfiguration = applicationConfiguration;
        this.mainLoop = mainLoop;
    }

    public void initializeObjects (){
        this.door = new Door();
        door.initializePosition();
        mainLoop.addObject(door);
        int numberOfQueues = applicationConfiguration.getNumberOfQueues();
        for (int i=0;i<numberOfQueues;i++){
            StoreCheckout checkout = new StoreCheckout(i);
            mainLoop.addObject(checkout);
            storeCheckouts.add(checkout);
            clientsInQueue.put(i, new ArrayDeque<>());
        }
    }

    public void addVisibleClient(Client client){
        visibleClients.add(client);
    }

    public Set<Client> getVisibleClients() {
        return new HashSet<>(visibleClients);
    }

    public void removeClientFromView(Client client){
        visibleClients.remove(client);
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
        return !clientsInQueue.get(client.getQueueNumber()).isEmpty() && clientsInQueue.get(client.getQueueNumber()).getFirst().equals(client);
    }

    public Optional<ClientToExitDTO> getClientClosestToDoor (){
        return clientsMovingToExit.stream().filter(client->client.getPositionInQueueToExit().equals(PositionInQueueToExit.AT_DOOR)).findAny();

    }

}
