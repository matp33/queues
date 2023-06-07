package utilities;

import constants.ClientPositionType;
import simulation.AppLayoutManager;
import simulation.ApplicationConfiguration;
import spring2.Bean;
import visualComponents.Client;

import java.awt.*;
import java.util.*;

@Bean
public class EventTimesCalculator {

    @SuppressWarnings("ComparatorMethodParameterNotUsed")
    static class InsertionOrderComparator implements Comparator<Integer>{

        @Override
        public int compare(Integer o1, Integer o2) {
            if (o1.equals(o2)){
                return 0;
            }
            else{
                return 1;
            }
        }
    }

    static  class ExitTimeAndTimeInCheckout {
        private double exitTime;
        private Client client;

        public ExitTimeAndTimeInCheckout(double exitTime, Client client) {
            this.exitTime = exitTime;
            this.client = client;
        }
    }

    private NavigableMap<Integer, Double> clientIdToTimeOfMovingToQueue = new TreeMap<>(new InsertionOrderComparator());
    private Map<Integer, Deque<ExitTimeAndTimeInCheckout>> queueToExitTimes = new HashMap<>();

    private final AppLayoutManager appLayoutManager;
    private final ClientMovement clientMovement;

    private final ApplicationConfiguration applicationConfiguration;

    public EventTimesCalculator(AppLayoutManager appLayoutManager, ClientMovement clientMovement, ApplicationConfiguration applicationConfiguration) {
        this.appLayoutManager = appLayoutManager;
        this.clientMovement = clientMovement;
        this.applicationConfiguration = applicationConfiguration;
    }

    public void initialize (){
        int numberOfQueues = applicationConfiguration.getNumberOfQueues();
        queueToExitTimes.clear();
        clientIdToTimeOfMovingToQueue.clear();
        for (int i=0; i< numberOfQueues; i++){
            queueToExitTimes.put(i, new LinkedList<>());
        }
    }

    public void calculateClientEventTimes (Client client){
        calculateStartTimeOfMovingFromEntranceToQueue(client);
        calculateStartTimeOfGoingFromCheckoutToExit(client);
    }

    private void calculateStartTimeOfMovingFromEntranceToQueue (Client client){
        double exitTime;
        double timeNeededToMoveToEntrance = clientMovement.calculateTimeNeededToMoveToEntrance(client);
        if (clientIdToTimeOfMovingToQueue.isEmpty()){
            exitTime = client.getArrivalTime() + ClientMovement.waitRoomDelay;
        }
        else{
            Double lastClientExit = clientIdToTimeOfMovingToQueue.lastEntry().getValue();
            double arrivalTime = client.getArrivalTime();
            if (arrivalTime > lastClientExit){
                exitTime = arrivalTime + ClientMovement.waitRoomDelay;
            }
            else{
                exitTime = lastClientExit + ClientMovement.waitRoomDelay;
            }
        }
        double movingStartTime = timeNeededToMoveToEntrance + exitTime;
        clientIdToTimeOfMovingToQueue.put(client.getId(), movingStartTime);
    }

    private void calculateStartTimeOfGoingFromCheckoutToExit (Client client){
        int queueNumber = client.getQueueNumber();
        Deque<ExitTimeAndTimeInCheckout> clientExitTimesInThisQueue = queueToExitTimes.get(queueNumber);
        double timeOfStartGoingToExitForThisClient;
        Double timeOfStartMovingToQueueThisClient = clientIdToTimeOfMovingToQueue.get(client.getId());
        double timeInCheckoutThisClient = client.getTimeInCheckout();
        double timeNeededToMoveFromEntranceToCheckoutForThisClient;
        if (clientExitTimesInThisQueue.isEmpty()){
            Point pointInQueue= appLayoutManager.calculateClientDestinationCoordinates(0,
                    queueNumber, ClientPositionType.GOING_TO_QUEUE);
            timeNeededToMoveFromEntranceToCheckoutForThisClient = clientMovement.calculateTimeToGetToQueue(pointInQueue);
            timeOfStartGoingToExitForThisClient = timeOfStartMovingToQueueThisClient + timeNeededToMoveFromEntranceToCheckoutForThisClient + timeInCheckoutThisClient;
        }
        else{
            Point pointInQueue= appLayoutManager.calculateClientDestinationCoordinates(0,
                    queueNumber, ClientPositionType.GOING_TO_QUEUE);
            timeNeededToMoveFromEntranceToCheckoutForThisClient = clientMovement.calculateTimeToGetToQueue(pointInQueue);
            double timeOfArrivingToQueueThisClient = clientIdToTimeOfMovingToQueue.get(client.getId()) + timeNeededToMoveFromEntranceToCheckoutForThisClient;
            ExitTimeAndTimeInCheckout lastClientInThisQueueExitFromQueueTime = clientExitTimesInThisQueue.getLast();
            if (timeOfArrivingToQueueThisClient < lastClientInThisQueueExitFromQueueTime.exitTime){
                timeOfStartGoingToExitForThisClient = lastClientInThisQueueExitFromQueueTime.exitTime + lastClientInThisQueueExitFromQueueTime.client.getTimeInCheckout() + timeInCheckoutThisClient;
            }
            else{
                timeOfStartGoingToExitForThisClient = timeOfArrivingToQueueThisClient + timeInCheckoutThisClient;
            }

        }

        clientExitTimesInThisQueue.add(new ExitTimeAndTimeInCheckout(timeOfStartGoingToExitForThisClient, client));
    }

    public Double getSimulationEndTime(){
        Set<Double> timesOfStoreExiting = new HashSet<>();
        for (Deque<ExitTimeAndTimeInCheckout> clientsInQueue : queueToExitTimes.values()) {
            ExitTimeAndTimeInCheckout latestClientExitInSingleQueue = clientsInQueue.getLast();
            double timeFromCheckoutToDoor = clientMovement.calculateTimeSinceCheckoutExitingToArrivingAtDoor(latestClientExitInSingleQueue.client);
            double queueExitTime = latestClientExitInSingleQueue.exitTime;
            timesOfStoreExiting.add(queueExitTime + timeFromCheckoutToDoor);
        }
        return timesOfStoreExiting.stream().max(Double::compare).orElseThrow(() -> new IllegalArgumentException("no clients in queues"));

    }

}
