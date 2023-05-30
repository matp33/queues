
package symulation;

import java.awt.*;
import java.util.*;
import java.util.List;

import constants.ClientPositionType;
import core.MainLoop;
import events.ClientEventsHandler;
import otherFunctions.ClientAction;
import spring2.Bean;
import visualComponents.Client;

@Bean
public class Simulation {
    
    public static final int FINISH=5;
    
    public static final String NO_MORE_ARRIVALS="No more clients will arive. Do you want to continue?";
	public static final String TITLE_NO_MORE_ARRIVALS = "No more arrivals";
	public static final String TITLE_FROM_BEGINNING = "Restarting simulation";
    public static final String SIMULATION_FINISHED = "Simulation has been finished.";
    
    private final Painter painter;

	private final ClientEventsHandler clientEventsHandler;

	private  MainLoop mainLoop;

	public Simulation(Painter painter, ClientEventsHandler clientEventsHandler, MainLoop mainLoop) {
		this.painter = painter;
		this.clientEventsHandler = clientEventsHandler;
		this.mainLoop = mainLoop;
	}

	public void prepareSimulation(double simulationStartTime, SortedSet<ClientArrivalEvent> clientArrivalEvents)  {

		ClientAction clientAction;
   

		Map<Integer, List<Client>> queueIndexToClientsInQueueMap = new HashMap<>();
		SortedSet<ClientAction> clientActions = new TreeSet<>();
		ClientArrivalEvent lastArrival =  clientArrivalEvents.stream().max(Comparator.comparing(ClientArrivalEvent::getArrivalTime)).orElseThrow(()->new IllegalArgumentException("simulation events empty"));
		for (ClientArrivalEvent event : clientArrivalEvents) {
			double arrivalTime = event.getArrivalTime();
			int queueNumber = event.getQueueNumber();
			List<Client> clients = queueIndexToClientsInQueueMap.computeIfAbsent(queueNumber, index -> new ArrayList<>());
			Client client = new Client(
					painter.getQueue(queueNumber), clients.size(),
					arrivalTime, event.getTimeInCheckout());
			clients.add(client);


			clientAction = createClientAction(client, queueNumber, arrivalTime, simulationStartTime,
					clients.size());

			mainLoop.addObject(client);
			clientActions.add(clientAction);
			if (event == lastArrival) {
				clientAction = new ClientAction(arrivalTime,
						ClientPositionType.PAUSE, null);
				clientActions.add(clientAction);
			}
		}
		clientEventsHandler.setEventsList(clientActions);

    }

	private ClientAction createClientAction(Client client, int queueNumber, double arrivalTime,
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
				Client.waitRoomDelay;

		ClientPositionType positionType;
		if (calculatedPosition.equals(pointInQueue)){
			positionType = ClientPositionType.GOING_TO_QUEUE;
			client.saveInformation(calculatedPosition, positionType);
			return new ClientAction(arrivalTime, positionType, client);
		}
		
	    if (arrivalTime<0){
	    	arrivalTime=0;
	    }  	    	    
	    
		double time;

	    if ( totalTime + simulationStartTime >= arrivalTime){
	    	positionType=ClientPositionType.GOING_TO_QUEUE;
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
    


