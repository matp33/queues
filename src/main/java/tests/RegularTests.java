
package tests;

import dto.ClientArrivalEventDTO;
import spring2.Bean;
import simulation.*;

import java.util.*;

@Bean
public class RegularTests {

    static double arrivalDelay=0.7;

    private Manager manager;

    private final ApplicationConfiguration applicationConfiguration;

    public RegularTests(Manager manager, ApplicationConfiguration applicationConfiguration) {
        this.manager = manager;
        this.applicationConfiguration = applicationConfiguration;
    }

    public void testInserting(int numberOfStoreCheckouts, int numberOfClients) throws InterruptedException {


        manager.beginSimulation();
        for (int i=0; i<numberOfClients;i++){
            Thread.sleep(1500);
        }
                         

    
    }

    public void testMultipleClientsWithMultipleQueues(int numberOfQueues, int numberOfClients)  {

            applicationConfiguration.setNumberOfQueues(numberOfQueues);

            SortedSet<ClientArrivalEventDTO> clientArrivalEventDTOS = new TreeSet<>(Comparator.comparing(ClientArrivalEventDTO::getArrivalTime).thenComparing(ClientArrivalEventDTO::getQueueNumber));

            for (int i=0; i<numberOfClients;i++){
                int queueNumber = i%4; // new Random().nextInt(numberOfQueues);
                double timeInQueue = 1; //generateRandomTimeInQueue();
                clientArrivalEventDTOS.add(new ClientArrivalEventDTO(timeInQueue, i/4, queueNumber));
            }
            manager.doSimulation(0.0, clientArrivalEventDTOS);



    }

    private double generateRandomTimeInQueue() {
        Random random = new Random();
        int maxTimeInQueue = 3;
        int minTimeInQueue = 1;
        return random.nextDouble() * (maxTimeInQueue-minTimeInQueue) + minTimeInQueue;
    }

    public void test1ClientPerQueue(int numberOfQueues) {
    	double time = 1.5;
        applicationConfiguration.setNumberOfQueues(numberOfQueues);
        SortedSet<ClientArrivalEventDTO> clientArrivalEventDTOS = new TreeSet<>(Comparator.comparing(ClientArrivalEventDTO::getArrivalTime));

        for (int i=0; i<numberOfQueues; i++){
            double timeInQueue = generateRandomTimeInQueue();
            clientArrivalEventDTOS.add(new ClientArrivalEventDTO(timeInQueue, time, i));

        }
//    	for (int i=4; i<6; i++){
//    		arrivals[i-4][0]=time;
//    		arrivals[i-4][1]=i;
//    		departs[i-4][0]=time+1;
//    		departs[i-4][1]=i;
//    	}


        manager.doSimulation(0.0, clientArrivalEventDTOS);

    }

    public void testQueueUpdating (int numberOfQueues, int delay)  {
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
        SortedSet<ClientArrivalEventDTO> clientArrivalEventDTOS = new TreeSet<>(Comparator.comparing(ClientArrivalEventDTO::getArrivalTime));
        double timeInQueue = generateRandomTimeInQueue();

        for (int i=0; i<arrivingClients;i++){
            clientArrivalEventDTOS.add(new ClientArrivalEventDTO(timeInQueue, 6+i*arrivalDelay, numberOfQueues-1));
        }

        manager.doSimulation(0.0, clientArrivalEventDTOS);





    }

}


