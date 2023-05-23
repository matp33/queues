
package symulation;

import java.awt.*;
import java.util.*;
import java.util.List;

import constants.ClientPositionType;
import constants.SimulationEventType;
import constants.TypeOfTimeEvent;
import events.ClientEventsHandler;
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

	private ApplicationConfiguration applicationConfiguration;

    
	public Simulation ()  {

		this.applicationConfiguration = ApplicationConfiguration.getInstance();
		this.painter=applicationConfiguration.getPainter();

    }

    public void prepareSimulation(double initialTime, SortedSet<SimulationEvent> simulationEvents)  {

		ClientAction clientAction;
   

		Map<Integer, List<Client>> queueIndexToClientsInQueueMap = new HashMap<>();
		List<ClientAction> clientActions = new ArrayList<>();
		SimulationEvent lastArrival =  simulationEvents.stream().filter(event -> event.getSimulationEventType().equals(TypeOfTimeEvent.ARRIVAL)).max(Comparator.comparing(SimulationEvent::getEventTime)).orElseThrow(()->new IllegalArgumentException("simulation events empty"));
		for (SimulationEvent event : simulationEvents) {
			double arrivalTime = event.getEventTime();
			int queueNumber = event.getQueueNumber();
			TypeOfTimeEvent eventType = event.getSimulationEventType();
			SimulationEventType action;
			double time;
			if (eventType.equals(TypeOfTimeEvent.ARRIVAL)) {
				List<Client> clients = queueIndexToClientsInQueueMap.computeIfAbsent(queueNumber, index -> new ArrayList<>());
				Client client = new Client(
						painter.getQueue(queueNumber), clients.size(),
						arrivalTime);
				clients.add(client);

				if (arrivalTime <= initialTime) {
					action = SimulationEventType.APPEAR_IN_POSITION;
					time = initialTime;
				} else {
					Pair<Double, SimulationEventType> result = calculateAppearTime(queueNumber, arrivalTime, initialTime,
							clients.size());
					time = result.getObject1();

					action = result.getObject2();
				}

				Point clientPosition;
				ClientPositionType positionType;
				if (action == SimulationEventType.ARRIVAL) {
					positionType = ClientPositionType.ARRIVAL; // TODO client should appear in positionType = "Arrival"
					clientPosition = painter.calculateClientDestinationCoordinates(0, 0, positionType);

				} else {
					Pair<Point, ClientPositionType> pair = calculatePosition(queueNumber, arrivalTime, initialTime,
							clients.size());
					clientPosition = pair.getObject1();
					positionType = pair.getObject2();
				}
				client.saveInformation(clientPosition, positionType);
				client.startDrawingMe();
				clientAction = new ClientAction(time, action, client);
				clientActions.add(clientAction);
				if (event == lastArrival) {
					clientAction = new ClientAction(arrivalTime,
							SimulationEventType.PAUSE, null);
					clientActions.add(clientAction);
				}
			} else {
				List<Client> clients = queueIndexToClientsInQueueMap.get(queueNumber);
				double departureTime = event.getEventTime();
				clientAction = new ClientAction(departureTime, SimulationEventType.DEPARTURE,
						clients.get(0));
				clients.remove(0);
				clientActions.add(clientAction);
			}
		}
		ClientEventsHandler clientEventsHandler = applicationConfiguration.getClientEventsHandler();
		clientEventsHandler.setEventsList(clientActions);

    }



    private Pair <Double,SimulationEventType> calculateAppearTime(int queueNumber, double eventTime, double initialTime,
    													int peopleInQueue){
    				
		Point pointWaitPlace=painter.calculateClientDestinationCoordinates(0, 0, ClientPositionType.WAITING_ROOM);
		Point pointInQueue=painter.calculateClientDestinationCoordinates(peopleInQueue, queueNumber,
				ClientPositionType.GOING_TO_QUEUE);
                           	
//		System.out.println(queues[queueNumber].
//				findNumberOfLastClient()+"in queue");
      
		double timeToQueue=Client.calculateTimeToGetToQueue(pointInQueue, pointWaitPlace);
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

	public Pair <Point,ClientPositionType> calculatePosition(int queueNumber, double arrivalTime,
					double initialTime, int peopleInQueue){
	
		// TODO this is too similar method to calculateAppearTime check it
		
		
		Point pointInitial=painter.calculateClientDestinationCoordinates(0, 0, ClientPositionType.ARRIVAL);
		Point pointInQueue=painter.calculateClientDestinationCoordinates(peopleInQueue,
                             queueNumber, ClientPositionType.GOING_TO_QUEUE);
        Point calculatedPosition = Client.calculateCoordinates(pointInQueue, pointInitial,
        								arrivalTime);
        
        	if (calculatedPosition.equals(pointInQueue)){
//        		System.out.println("same"+arrivalTime+"?"+queueNumber);
        		return new Pair <>(calculatedPosition,ClientPositionType.WAITING_IN_QUEUE);
        	}
		
	    if (arrivalTime<0){
	    	arrivalTime=0;
	    }  	    	    
	    
	    ClientPositionType positionType;
	    
	    if (arrivalTime<=initialTime){
	    	positionType=ClientPositionType.WAITING_IN_QUEUE;
	    	calculatedPosition=pointInQueue;
	    }
	    else{
	    	positionType=ClientPositionType.GOING_TO_QUEUE;
	    }
//	    System.out.println(calculatedPosition+"time "+arrivalTime);
	    return new Pair<>(calculatedPosition, positionType);
	}


    


}
    


