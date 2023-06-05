
package symulation;

import java.awt.*;
import java.util.*;
import java.util.List;

import constants.ClientPositionType;
import core.MainLoop;
import events.ClientEventsHandler;
import events.ObjectsManager;
import otherFunctions.ClientAction;
import otherFunctions.ClientMovement;
import spring2.Bean;
import visualComponents.Client;

@Bean
public class Simulation {
    

    public static final String NO_MORE_ARRIVALS="No more clients will arive. Do you want to continue?";
	public static final String TITLE_NO_MORE_ARRIVALS = "No more arrivals";
	public static final String TITLE_FROM_BEGINNING = "Restarting simulation";
    public static final String SIMULATION_FINISHED = "Simulation has been finished.";

	private int clientId = 0;
    

	private final ClientEventsHandler clientEventsHandler;

	private ClientMovement clientMovement;

	private  MainLoop mainLoop;

	private ObjectsManager objectsManager;

	private CustomLayout customLayout;

	public Simulation(ClientEventsHandler clientEventsHandler, ClientMovement clientMovement, MainLoop mainLoop, ObjectsManager objectsManager, CustomLayout customLayout) {
		this.clientEventsHandler = clientEventsHandler;
		this.clientMovement = clientMovement;
		this.mainLoop = mainLoop;
		this.objectsManager = objectsManager;
		this.customLayout = customLayout;
	}

	public void prepareSimulation(double simulationStartTime, SortedSet<ClientArrivalEvent> clientArrivalEvents)  {

		ClientAction clientAction;
   		clientId = 0;

		Map<Integer, List<Client>> queueIndexToClientsInQueueMap = new HashMap<>();
		SortedSet<ClientAction> clientActions = new TreeSet<>();
		ClientArrivalEvent lastArrival =  clientArrivalEvents.stream().max(Comparator.comparing(ClientArrivalEvent::getArrivalTime)).orElseThrow(()->new IllegalArgumentException("simulation events empty"));
		for (ClientArrivalEvent event : clientArrivalEvents) {
			double arrivalTime = event.getArrivalTime();
			int queueNumber = event.getQueueNumber();
			List<Client> clients = queueIndexToClientsInQueueMap.computeIfAbsent(queueNumber, index -> new ArrayList<>());
			Client client = new Client(clientId++,
					queueNumber, clients.size(),
					arrivalTime, event.getTimeInCheckout());
			clients.add(client);
			objectsManager.addVisibleClient(client);


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
		
		
		Point pointInitial=customLayout.calculateClientDestinationCoordinates(0, 0, ClientPositionType.ARRIVAL);
		Point pointWaitPlace=customLayout.calculateClientDestinationCoordinates(0, 0, ClientPositionType.WAITING_ROOM);

		Point pointInQueue=customLayout.calculateClientDestinationCoordinates(peopleInQueue,
                             queueNumber, ClientPositionType.GOING_TO_QUEUE);
        Point calculatedPosition = clientMovement.calculateCoordinates(pointInQueue, pointInitial,
        								arrivalTime);

		double totalTime= clientMovement.calculateTimeFromWaitingRoomToQueue(pointInQueue, pointWaitPlace);

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
    


