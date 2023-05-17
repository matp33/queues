
package tests;

import symulation.Manager;
import visualComponents.Client;


public class RegularTests {

//    static int nrKolejki=;
    static int arrivalDelay=700;

    public static void testInserting(int numberOfQueues,int numberOfClients){

//                Painter painter= new Painter(numberOfQueues,10.0,new WindowFrame());
                Manager manager = new Manager(numberOfQueues);
                
                try{
                    manager.beginSimulation();
//                    painter.simulation.timerClass.isRunning=true;
                    for (int i=0; i<numberOfClients;i++){
//                        painter.simulation.insertClient(numberOfQueues-1);
//                        
                    }
                    for (int i=0; i<numberOfClients;i++){
//                        painter.simulation.timerClass.clientLeaves(painter.simulation, numberOfQueues-1);
                        Thread.sleep(1500);
                    }
                         
                }
                catch (Exception ex){
                		ex.printStackTrace();
                }

    
    }

    public static void test(int numberOfQueues, int numberOfClients){
             Manager manager= new Manager(numberOfQueues);
//                new Okno_frame(okno);
//                okno.zapytaj("start?");

                try{//
                    Thread.sleep(500);
//                    okno.symulacja.startujCzas();
                    double [][] arrivals= new double [numberOfClients][2];
                    double [][] departures = new double [numberOfClients][2];
                    
                    for (int i=0; i<numberOfClients;i++){
                        arrivals[i][0]=i*(double)arrivalDelay/1000;
                        departures[i][0]=i*(double)arrivalDelay/1000+1+Client.waitRoomDelay/1000*5*i;
                        departures[i][1]=1;
                        arrivals[i][1]=1;
                        System.out.println("---- "+arrivals[i][0]);
//                        System.out.println("---- "+departures[i][0]);
                    }
                    
                    manager.saveTimeTable(arrivals, departures);
                    manager.doSimulation();


                }
                catch (Exception ex){
                    ex.printStackTrace();
                }

    }
        
    public static void test1ClientPerQueue(int numberOfQueues){
    	double time = 1.5;
    	Manager manager = new Manager (numberOfQueues);
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
    	
    	manager.saveTimeTable(arrivals, departs);
    	
    	try{
            manager.doSimulation();
        }

        catch (Exception ex){
            ex.printStackTrace();
        }
    	
    }

    public static void testQueueUpdating (int queueNumber, int delay){
        Manager manager= new Manager(10);
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

        manager.saveTimeTable(arrivals, departures);
        try{
            manager.doSimulation();
        }

        catch (Exception ex){
            ex.printStackTrace();
        }

        


    }

}


