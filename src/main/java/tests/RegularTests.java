
package tests;

import constants.TypeOfTimeEvent;
import symulation.*;
import visualComponents.Client;

import java.util.*;


public class RegularTests {

    static double arrivalDelay=0.7;

    public static void testInserting(int numberOfStoreCheckouts,int numberOfClients) throws InterruptedException {

        Manager manager = new Manager( );

        manager.beginSimulation();
        for (int i=0; i<numberOfClients;i++){
            Thread.sleep(1500);
        }
                         

    
    }

    public static void testMultipleClientsWithMultipleQueues(int numberOfQueues, int numberOfClients)  {

            ApplicationConfiguration applicationConfiguration = ApplicationConfiguration.getInstance();
            applicationConfiguration.setNumberOfQueues(numberOfQueues);

            SortedSet<ClientArrivalEvent> clientArrivalEvents = new TreeSet<>(Comparator.comparing(ClientArrivalEvent::getArrivalTime).thenComparing(ClientArrivalEvent::getQueueNumber));

            for (int i=0; i<numberOfClients;i++){
                int queueNumber = i%4; // new Random().nextInt(numberOfQueues);
                double timeInQueue = 1; //generateRandomTimeInQueue();
                clientArrivalEvents.add(new ClientArrivalEvent(timeInQueue, i/4, queueNumber));
            }
            applicationConfiguration.getPainter().setTimeTable(clientArrivalEvents);
            Manager manager = applicationConfiguration.getManager();
            manager.setTimeTable(clientArrivalEvents);
            manager.doSimulation();



    }

    private static double generateRandomTimeInQueue() {
        Random random = new Random();
        int maxTimeInQueue = 3;
        int minTimeInQueue = 1;
        return random.nextDouble() * (maxTimeInQueue-minTimeInQueue) + minTimeInQueue;
    }

    public static void test1ClientPerQueue(int numberOfQueues) {
    	double time = 1.5;
        ApplicationConfiguration applicationConfiguration = ApplicationConfiguration.getInstance();
        applicationConfiguration.setNumberOfQueues(numberOfQueues);
        SortedSet<ClientArrivalEvent> clientArrivalEvents = new TreeSet<>(Comparator.comparing(ClientArrivalEvent::getArrivalTime));

        for (int i=0; i<numberOfQueues; i++){
            double timeInQueue = generateRandomTimeInQueue();
            clientArrivalEvents.add(new ClientArrivalEvent(timeInQueue, time, i));

        }
//    	for (int i=4; i<6; i++){
//    		arrivals[i-4][0]=time;
//    		arrivals[i-4][1]=i;
//    		departs[i-4][0]=time+1;
//    		departs[i-4][1]=i;
//    	}

        Manager manager = applicationConfiguration.getManager();
        manager.setTimeTable(clientArrivalEvents);
    	applicationConfiguration.getPainter().setTimeTable(clientArrivalEvents);

        manager.doSimulation();

    }

    public static void testQueueUpdating (int numberOfQueues, int delay)  {
        ApplicationConfiguration applicationConfiguration = ApplicationConfiguration.getInstance();
        applicationConfiguration.setNumberOfQueues(numberOfQueues);
        int insertedClients=2;
        
        for (int i=0; i<insertedClients;i++){
//            painter.simulation.insertClient(numberOfQueues-1);
//
        }
//        try{
//            for (int i=0; i<ilosc;i++){
//            	Thread.sleep(1500);
//                okno.symulacja.klientOdchodzi(nrKolejki-1);
//                
//            }
//        }
//        catch (InterruptedException ex){
//            ex.printStackTrace();
//        }
        int arrivingClients=2;
        SortedSet<ClientArrivalEvent> clientArrivalEvents = new TreeSet<>(Comparator.comparing(ClientArrivalEvent::getArrivalTime));
        double timeInQueue = generateRandomTimeInQueue();

        for (int i=0; i<arrivingClients;i++){
            clientArrivalEvents.add(new ClientArrivalEvent(timeInQueue, 6+i*arrivalDelay, numberOfQueues-1));
        }

        applicationConfiguration.getManager().setTimeTable(clientArrivalEvents);
        applicationConfiguration.getManager().doSimulation();
        applicationConfiguration.getPainter().setTimeTable(clientArrivalEvents);





    }

}


