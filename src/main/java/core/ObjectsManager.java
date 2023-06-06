package core;

import constants.PositionInQueueToExit;
import dto.ClientToExitDTO;
import spring2.Bean;
import simulation.ApplicationConfiguration;
import simulation.AppLayoutManager;
import visualComponents.AnimatedObject;
import visualComponents.Client;
import visualComponents.Door;
import visualComponents.StoreCheckout;

import java.awt.*;
import java.util.*;

@Bean
public class ObjectsManager {

    private Door door;

    private Set<StoreCheckout> storeCheckouts = new HashSet<>();

    private Map<Integer, Deque<Client>> clientsInQueue = new HashMap<>();

    private NavigableSet<ClientToExitDTO> clientsMovingToExit = new TreeSet<>();

    private Client clientByTheDoor;

    private ApplicationConfiguration applicationConfiguration;

    private Set<Client> visibleClients = new LinkedHashSet<>();

    private AppLayoutManager appLayoutManager;

    private Set<AnimatedObject> animatedObjects = new HashSet<>();

    private Set<Client> clientsInQueueToEntrance = new LinkedHashSet<>();

    private Client clientInEntrance;

    public ObjectsManager(ApplicationConfiguration applicationConfiguration, AppLayoutManager appLayoutManager) {
        this.applicationConfiguration = applicationConfiguration;
        this.appLayoutManager = appLayoutManager;
    }

    public void initializeObjects (){
        this.door = new Door();
        Point position = appLayoutManager.calculateDoorPosition();
        door.setPosition(position);
        animatedObjects.add(door);
        int numberOfQueues = applicationConfiguration.getNumberOfQueues();
        for (int i=0;i<numberOfQueues;i++){
            StoreCheckout checkout = new StoreCheckout(i);
            Point queueIndicatorPosition = appLayoutManager.calculateQueueIndicatorPosition(i);
            Point checkoutPosition = appLayoutManager.calculateCheckoutPosition(i);
            checkout.initializePosition(queueIndicatorPosition, checkoutPosition);
            animatedObjects.add(checkout);
            storeCheckouts.add(checkout);
            clientsInQueue.put(i, new ArrayDeque<>());
        }
    }

    public Set<AnimatedObject> getAnimatedObjects() {
        return animatedObjects;
    }

    public void addVisibleClient(Client client){
        visibleClients.add(client);
    }

    public Set<Client> getVisibleClients() {
        return new LinkedHashSet<>(visibleClients);
    }

    public void removeClientFromView(Client client){
        visibleClients.remove(client);
    }

    public Set<StoreCheckout> getStoreCheckouts() {
        return storeCheckouts;
    }

    public StoreCheckout getStoreCheckout(int index){
        return storeCheckouts.stream().filter(checkout->checkout.getCheckoutIndex() == index).findFirst().orElseThrow(()->new IllegalArgumentException("store checkout not found: "+index));
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

    public void removeObjects() {
        storeCheckouts.clear();
        animatedObjects.clear();
        clientByTheDoor = null;
        clientsInQueue.clear();
        clientsMovingToExit.clear();
        visibleClients.clear();
        clientsInQueueToEntrance.clear();
        clientInEntrance = null;
    }

    public boolean isAnyClientInEntrance() {
        return clientInEntrance != null;
    }

    public void removeClientFromQueueToEntrance(Client client) {
        clientsInQueueToEntrance.remove(client);
    }

    public void addClientToQueueToEntrance(Client client) {
        clientsInQueueToEntrance.add(client);
    }

    public void setClientInEntrance(Client client){
        clientInEntrance = client;
    }

    public void clearClientInEntrance() {
        clientInEntrance = null;
    }

    public boolean isFirstClientInQueueToEntrance(Client client) {
        return clientsInQueueToEntrance.isEmpty() ||  clientsInQueueToEntrance.iterator().next().equals(client);
    }

    public Client getClientInEntrance() {
        return clientInEntrance;
    }
}
