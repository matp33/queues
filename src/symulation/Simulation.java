
package symulation;

import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import constants.ClientPositionType;
import constants.SimulationEventType;
import otherFunctions.ClientAction;
import otherFunctions.Pair;
import visualComponents.Client;

public class Simulation {
    
    public static final int FINISH=5;
    
    public static final String NO_MORE_ARRIVALS="No more clients will arive. Do you want to continue?";
	public static final String TITLE_NO_MORE_ARRIVALS = "No more arrivals";
	public static final String TITLE_FROM_BEGINNING = "Restarting simulation";
    public static final String SIMULATION_FINISHED = "Simulation has been finished.";
    
    private final Painter painter;
    private final Manager manager;
    private List <Pair<Double,Integer>> queueEvents; // Map time to queue number

    private int numberOfQueues;
//    public Timing timerClass;
    
    
	public Simulation (int numberOfQueues, Painter painter, Manager manager){
		
		this.painter=painter;
		this.manager=manager;
		this.numberOfQueues=numberOfQueues;
		
                
    }

    public void prepareSimulation(double initialTime,double [][] arrivals,
                          double [][] departures) throws Exception {
   
//    timerClass.isRunning=true;
    
    	queueEvents=new ArrayList <Pair<Double,Integer>>();
    
    
    int peopleInQueue []=new int [numberOfQueues];
//    System.out.println("queue numbers "+numberOfQueues);


    ClientAction clientAction;
   
    int departIndex=0;

    while(departIndex<departures.length){
    	if (departures[departIndex][0]<initialTime){
    		departIndex++;
    	}
    	else{
    		break;
    	}
    }
    int arriveIndex=departIndex; // we start at same indexes because for any i: arrivals[i] and 
    
    //departures[i] describe same client

    List<ClientAction> listOfEvents = new ArrayList<>();
    while ((arriveIndex<arrivals.length || departIndex<departures.length)){   	

		// departIndex>=departures.length ??? why is this needed
        if (departIndex>=departures.length ||(arriveIndex<arrivals.length  &&
                arrivals[arriveIndex][0]<departures[departIndex][0])){


			double arrivalTime=arrivals[arriveIndex][0];

        	
        	
        	if (!queueEvents.isEmpty() && arrivalTime>=(Double)queueEvents.get(0).getObject1()){

        		System.out.println("OOOO"+arriveIndex);
        		int queueNumber=(int)queueEvents.get(0).getObject2();
        		peopleInQueue[queueNumber]--;        		   
        		queueEvents.remove(0);     
        	}
        	

            
            int queueNumber=(int)arrivals[arriveIndex][1]; 
            SimulationEventType action;
            double time;
//            System.out.println("current time "+currentTime +" in queue "+peopleInQueue[queueNumber]);
            
            	if (arrivalTime<=initialTime){
            		action=SimulationEventType.APPEAR_IN_POSITION;
            		time=initialTime;
            	}
            	else{
            		Pair <Double,SimulationEventType> result=calculateAppearTime(queueNumber, arrivalTime,initialTime,
							 peopleInQueue[queueNumber]);
            		time=result.getObject1();
            		
            		action=result.getObject2();
            	}
            	
            	
            	
//            int clientNumber=arriveIndex-departIndex;	
//            System.out.println(arrivalTime+"at "+peopleInQueue[queueNumber]);
            Client client=new Client(
            		manager.getQueue(queueNumber),peopleInQueue[queueNumber],painter,
            		arrivalTime,manager);                        
	        Dimension dim;
			ClientPositionType type;
            if (action== SimulationEventType.ARRIVAL){
            	type=ClientPositionType.ARRIVAL; // TODO client should appear in type = "Arrival"
            	dim=painter.calculateClientCoordinates(0, 0, type);
            	
            }
            else{
            	Pair <Dimension, ClientPositionType> pair= calculatePosition(queueNumber,arrivalTime,initialTime,
						peopleInQueue[queueNumber]);
            	dim= pair.getObject1();
                type=pair.getObject2();
            }
            
            client.saveInformation(dim,type);
			client.startDrawingMe();
            clientAction=new ClientAction(time,action, client);
            listOfEvents.add(clientAction); 
                        
	            if (arriveIndex==arrivals.length-1 && arrivalTime>initialTime){
	                clientAction=new ClientAction(arrivalTime,
							SimulationEventType.PAUSE,null);
	                listOfEvents.add(clientAction);
	            }
            
            peopleInQueue[queueNumber]++;
            arriveIndex++;
//            System.out.println("after "+client.clientNumber);
            continue;
            
        }
        
        if ( arriveIndex>=arrivals.length || (departIndex<departures.length &&
                                    departures[departIndex][0]<=arrivals[arriveIndex][0]) ){ 	
        	
            double departureTime=departures[departIndex][0];
            int queueNumber=(int)departures[departIndex][1];
            Client c=findFirstClient(departIndex, listOfEvents);
            int delay=0;
            if (c!=null){
            	delay=calculateTotalDelay(c.getQueueNumber(), listOfEvents, departIndex);
            }
//            System.out.println("delay "+delay+" depart "+currentTime);
            
            // TODO check if delay is calculated right
            
            
            queueEvents.add(new Pair<>(departureTime + (double) delay / 1000, queueNumber));
            clientAction=new ClientAction(departureTime, SimulationEventType.DEPARTURE,
            			c);
            listOfEvents.add(clientAction);   
              
            departIndex++;
            
            // Solution: don't create new client action, instead just replace the values
            
            // TODO Check pattern: prototype
        }
        
        
    }           
    // Sorting events by ascending times
    sortEvents(listOfEvents);
    manager.saveEventsList(listOfEvents);

//    if (timerClass.timer==null){
//        timerClass.startSimulation();
//    }  
   
    for (int i=0; i<listOfEvents.size();i++){
    	if (listOfEvents.get(i).getClient()!=null)
    	System.out.println(" time: "+listOfEvents.get(i).getTime()+
    			" action "+listOfEvents.get(i).getAction()+" id "+listOfEvents.get(i).getClient().id);
    }

    System.out.println("done");

    }


    private Client findFirstClient(int index, List<ClientAction>
    								events) {
    	
    	// TODO test it now
    	int j=0;
		for (int i=0; i<events.size();i++){
			Client c=events.get(i).getClient();
					
			if (c!=null && (events.get(i).getAction()==SimulationEventType.ARRIVAL || events.get(i).getAction()==SimulationEventType.APPEAR_IN_POSITION)
//					&& c.queueNumber==queueNumber
					){
				j++;
			}
			if (j>index){
				return c;
			}	
			
		}
		return null;
	}

	private void sortEvents(List <ClientAction> listOfEvents){
    	Collections.sort(listOfEvents, (m1, m2) -> {

			if (m1.getTime() < m2.getTime()) {
				return -1;
			}
			if (m1.getTime() > m2.getTime()) {
				return 1;
			} else {
				return 0;
			}

		});
    }
    
    private Pair <Double,SimulationEventType> calculateAppearTime(int queueNumber, double eventTime, double initialTime,
    													int peopleInQueue){
    				
		Dimension dimWaitPlace=painter.calculateClientCoordinates(0, 0, ClientPositionType.WAITING_ROOM);
		Dimension dimInQueue=painter.calculateClientCoordinates(peopleInQueue, queueNumber,
				ClientPositionType.GOING_TO_QUEUE);
                           	
//		System.out.println(queues[queueNumber].
//				findNumberOfLastClient()+"in queue");
      
		double timeToQueue=Client.calculateTimeToGetToQueue(dimInQueue, dimWaitPlace);
		double totalTime=timeToQueue+//timeToWaitPlace+ // TODO add it
				Client.waitRoomDelay/1000;
		
      System.out.println("event "+eventTime+" ppl "+peopleInQueue); //TODO problem with ppl in queue

		SimulationEventType action;
		double time;
		if (totalTime<=eventTime-initialTime){
			action= SimulationEventType.ARRIVAL;
			time=eventTime-totalTime;
		}
		else{
			time=initialTime;
			action=SimulationEventType.APPEAR_IN_POSITION;
		}
		return new Pair<>(time, action);
    }

    private int calculateTotalDelay(int queueNumber, List<ClientAction> events,
    				int desiredIndex) {
    	
    	int totalDelay=0;
    	int index=0;
		for (int i=1; i<events.size(); i++){
			Client client = events.get(i).getClient();
			
			if (client!=null && client.getQueueNumber()==queueNumber){
//				System.out.println("delay "+client.getQueueDelay());
				
				if (index<desiredIndex){
					index++;
				}
				else{
					totalDelay+=client.getQueueDelay();
				}
				
			}
		}

		return totalDelay;
		
	}

	public Pair <Dimension,ClientPositionType> calculatePosition(int queueNumber, double arrivalTime,
					double initialTime, int peopleInQueue){
	
		// TODO this is too similar method to calculateAppearTime check it
		
		
		Dimension dimInitial=painter.calculateClientCoordinates(0, 0, ClientPositionType.ARRIVAL);
		Dimension dimInQueue=painter.calculateClientCoordinates(peopleInQueue,
                             queueNumber, ClientPositionType.GOING_TO_QUEUE);
        Dimension calculatedPosition = Client.calculateCoordinates(dimInQueue, dimInitial,
        								arrivalTime);
        
        	if (calculatedPosition.equals(dimInQueue)){
//        		System.out.println("same"+arrivalTime+"?"+queueNumber);
        		return new Pair <>(calculatedPosition,ClientPositionType.WAITING_IN_QUEUE);
        	}
		
	    if (arrivalTime<0){
	    	arrivalTime=0;
	    }  	    	    
	    
	    ClientPositionType positionType;
	    
	    if (arrivalTime<=initialTime){
	    	positionType=ClientPositionType.WAITING_IN_QUEUE;
	    	calculatedPosition=dimInQueue;	
	    }
	    else{
	    	positionType=ClientPositionType.GOING_TO_QUEUE;
	    }
//	    System.out.println(calculatedPosition+"time "+arrivalTime);
	    return new Pair<>(calculatedPosition, positionType);
	}

	public void setNumberOfQueues(int numberOfQueues){
		this.numberOfQueues=numberOfQueues;
	}

    


}
    


