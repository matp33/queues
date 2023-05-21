
package tests;

import symulation.ApplicationConfiguration;
import symulation.Manager;
import symulation.Painter;
import visualComponents.Client;

import java.util.Random;


public class RegularTests {

//    static int nrKolejki=;
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

                    double [][] arrivals= new double [numberOfClients][2];
                    double [][] departures = new double [numberOfClients][2];

                    for (int i=0; i<numberOfClients;i++){
                        int queueNumber = new Random().nextInt(numberOfQueues);
                        arrivals[i][0]=i*(double)arrivalDelay/1000;
                        departures[i][0]=i*(double)arrivalDelay/1000+5;
                        departures[i][1]=queueNumber;
                        arrivals[i][1]= queueNumber;
                        System.out.println("---- "+arrivals[i][0]);
//                        System.out.println("---- "+departures[i][0]);
                    }
                    applicationConfiguration.getPainter().setTimeTable(arrivals, departures);
                    Manager manager = applicationConfiguration.getManager();
                    manager.setTimeTable(arrivals, departures);
                    manager.doSimulation();



    }
        
    public static void test1ClientPerQueue(int numberOfQueues) {
    	double time = 1.5;
        ApplicationConfiguration applicationConfiguration = ApplicationConfiguration.getInstance();
        applicationConfiguration.setNumberOfQueues(numberOfQueues);
    	double [][] arrivals = new double [numberOfQueues][2];
    	double [][] departs = new double [numberOfQueues][2];
    	
    	
    	for (int i=0; i<numberOfQueues; i++){
    		arrivals[i][0]=time;
    		arrivals[i][1]=i;
    		departs[i][0]=time+1;
    		departs[i][1]=i;
    	}
//    	for (int i=4; i<6; i++){
//    		arrivals[i-4][0]=time;
//    		arrivals[i-4][1]=i;
//    		departs[i-4][0]=time+1;
//    		departs[i-4][1]=i;
//    	}

        Manager manager = applicationConfiguration.getManager();
        manager.setTimeTable(arrivals, departs);
    	applicationConfiguration.getPainter().setTimeTable(arrivals, departs);

        manager.doSimulation();

    }

    public static void testQueueUpdating (int queueNumber, int delay)  {
        ApplicationConfiguration applicationConfiguration = ApplicationConfiguration.getInstance();
        applicationConfiguration.setNumberOfQueues(queueNumber);
        int insertedClients=2;
        
        for (int i=0; i<insertedClients;i++){
//            painter.simulation.insertClient(queueNumber-1);
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
        double [][] arrivals= new double [arrivingClients][2];
        double [][] departures = new double [arrivingClients+2][2];
        departures[0][0]=1;
        departures[0][1]=queueNumber-1;
        departures[1][0]=2.5;
        departures[1][1]=queueNumber-1;
            for (int i=0; i<arrivingClients;i++){
                arrivals[i][0]=6+i*(double)arrivalDelay/1000;
                departures[i+2][0]=8+(5*i+1)*(double)arrivalDelay/1000+delay/1000+
                                    Client.waitRoomDelay/1000;
                departures[i+2][1]=queueNumber-1;
                arrivals[i][1]=queueNumber-1;
            }

        applicationConfiguration.getManager().setTimeTable(arrivals, departures);
        applicationConfiguration.getPainter().setTimeTable(arrivals, departures);
        applicationConfiguration.getManager().doSimulation();

        


    }

}


