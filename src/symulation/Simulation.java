
package symulation;

import java.awt.*;
import java.util.*;
import java.util.List;

import constants.ClientPositionType;
import constants.TypeOfTimeEvent;
import events.ClientEventsHandler;
import otherFunctions.ClientAction;
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

    public void prepareSimulation(double simulationStartTime, SortedSet<SimulationEvent> simulationEvents)  {

		ClientAction clientAction;
   

		Map<Integer, List<Client>> queueIndexToClientsInQueueMap = new HashMap<>();
		List<ClientAction> clientActions = new ArrayList<>();
		SimulationEvent lastArrival =  simulationEvents.stream().filter(event -> event.getSimulationEventType().equals(TypeOfTimeEvent.ARRIVAL)).max(Comparator.comparing(SimulationEvent::getEventTime)).orElseThrow(()->new IllegalArgumentException("simulation events empty"));
		for (SimulationEvent event : simulationEvents) {
			double arrivalTime = event.getEventTime();
			int queueNumber = event.getQueueNumber();
			TypeOfTimeEvent eventType = event.getSimulationEventType();
			if (eventType.equals(TypeOfTimeEvent.ARRIVAL)) {
				List<Client> clients = queueIndexToClientsInQueueMap.computeIfAbsent(queueNumber, index -> new ArrayList<>());
				Client client = new Client(
						painter.getQueue(queueNumber), clients.size(),
						arrivalTime);
				clients.add(client);


				clientAction = createClientAction(client, queueNumber, arrivalTime, simulationStartTime,
						clients.size());

				client.startDrawingMe();
				clientActions.add(clientAction);
				if (event == lastArrival) {
					clientAction = new ClientAction(arrivalTime,
							ClientPositionType.PAUSE, null);
					clientActions.add(clientAction);
				}
			} else {
				List<Client> clients = queueIndexToClientsInQueueMap.get(queueNumber);
				double departureTime = event.getEventTime();
				clientAction = new ClientAction(departureTime, ClientPositionType.EXITING,
						clients.get(0));
				clients.remove(0);
				clientActions.add(clientAction);
			}
		}
		ClientEventsHandler clientEventsHandler = applicationConfiguration.getClientEventsHandler();
		clientEventsHandler.setEventsList(clientActions);

    }

	public ClientAction createClientAction(Client client, int queueNumber, double arrivalTime,
										   double simulationStartTime, int peopleInQueue){
	
		// TODO this is too similar method to calculateAppearTime check it
		
		
		Point pointInitial=painter.calculateClientDestinationCoordinates(0, 0, ClientPositionType.ARRIVAL);
		Point pointWaitPlace=painter.calculateClientDestinationCoordinates(0, 0, ClientPositionType.WAITING_ROOM);

		Point pointInQueue=painter.calculateClientDestinationCoordinates(peopleInQueue,
                             queueNumber, ClientPositionType.GOING_TO_QUEUE);
        Point calculatedPosition = Client.calculateCoordinates(pointInQueue, pointInitial,
        								arrivalTime);

		double timeNeededToMoveToQueue=Client.calculateTimeToGetToQueue(pointInQueue, pointWaitPlace);
		double totalTime=timeNeededToMoveToQueue+//timeToWaitPlace+  TODO add it
				Client.waitRoomDelay/1000;

		ClientPositionType positionType;
		if (calculatedPosition.equals(pointInQueue)){
			positionType = ClientPositionType.WAITING_IN_QUEUE;
			client.saveInformation(calculatedPosition, positionType);
			return new ClientAction(arrivalTime, positionType, client);
		}
		
	    if (arrivalTime<0){
	    	arrivalTime=0;
	    }  	    	    
	    
		double time;

	    if ( totalTime + simulationStartTime >= arrivalTime){
	    	positionType=ClientPositionType.WAITING_IN_QUEUE;
	    	calculatedPosition=pointInQueue;
			time = simulationStartTime;
	    }
	    else if (totalTime <= arrivalTime - simulationStartTime){
			positionType= ClientPositionType.ARRIVAL;
			time=arrivalTime-totalTime;
			calculatedPosition = pointInitial;
	    }
		else{
			throw new IllegalArgumentException("Unexpected situation happened");
		}
		client.saveInformation(calculatedPosition, positionType);
		return new ClientAction(time, positionType, client);
	}


    


}
    


