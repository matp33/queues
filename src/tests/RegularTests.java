
package tests;

import constants.TypeOfTimeEvent;
import symulation.*;
import visualComponents.Client;

import java.util.*;


public class RegularTests {

    static int arrivalDelay=700;

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

                    SortedSet<SimulationEvent> simulationEvents = new TreeSet<>(Comparator.comparing(SimulationEvent::getEventTime));

                    for (int i=0; i<numberOfClients;i++){
                        int queueNumber = new Random().nextInt(numberOfQueues);
                        simulationEvents.add(new SimulationEvent(TypeOfTimeEvent.ARRIVAL, i*(double)arrivalDelay/1000, queueNumber));
                        simulationEvents.add(new SimulationEvent(TypeOfTimeEvent.DEPARTURE, i*(double)arrivalDelay/1000+5, queueNumber));
                    }
                    applicationConfiguration.getPainter().setTimeTable(simulationEvents);
                    Manager manager = applicationConfiguration.getManager();
                    manager.setTimeTable(simulationEvents);
                    manager.doSimulation();



    }
        
    public static void test1ClientPerQueue(int numberOfQueues) {
    	double time = 1.5;
        ApplicationConfiguration applicationConfiguration = ApplicationConfiguration.getInstance();
        applicationConfiguration.setNumberOfQueues(numberOfQueues);
        SortedSet<SimulationEvent> simulationEvents = new TreeSet<>(Comparator.comparing(SimulationEvent::getEventTime));

        for (int i=0; i<numberOfQueues; i++){
            simulationEvents.add(new SimulationEvent(TypeOfTimeEvent.ARRIVAL, time, i));
            simulationEvents.add(new SimulationEvent(TypeOfTimeEvent.DEPARTURE, time + 1, i));

        }
//    	for (int i=4; i<6; i++){
//    		arrivals[i-4][0]=time;
//    		arrivals[i-4][1]=i;
//    		departs[i-4][0]=time+1;
//    		departs[i-4][1]=i;
//    	}

        Manager manager = applicationConfiguration.getManager();
        manager.setTimeTable(simulationEvents);
    	applicationConfiguration.getPainter().setTimeTable(simulationEvents);

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
        SortedSet<SimulationEvent> simulationEvents = new TreeSet<>(Comparator.comparing(SimulationEvent::getEventTime));
        simulationEvents.add(new SimulationEvent(TypeOfTimeEvent.DEPARTURE, 1, numberOfQueues-1));
        simulationEvents.add(new SimulationEvent(TypeOfTimeEvent.DEPARTURE, 2.5, numberOfQueues-1));


            for (int i=0; i<arrivingClients;i++){
                simulationEvents.add(new SimulationEvent(TypeOfTimeEvent.ARRIVAL, 6+i*(double)arrivalDelay/1000, numberOfQueues-1));
                simulationEvents.add(new SimulationEvent(TypeOfTimeEvent.DEPARTURE, 8+(5*i+1)*(double)arrivalDelay/1000+delay/1000+
                        Client.waitRoomDelay/1000, numberOfQueues-1));

            }

        applicationConfiguration.getManager().setTimeTable(simulationEvents);
        applicationConfiguration.getManager().doSimulation();
        applicationConfiguration.getPainter().setTimeTable(simulationEvents);





    }

}


