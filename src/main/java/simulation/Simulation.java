
package simulation;

import java.awt.*;
import java.util.*;
import java.util.List;

import constants.ClientPositionType;
import core.MainLoop;
import clienthandling.ClientEventsHandler;
import core.ObjectsManager;
import dto.ClientActionDTO;
import dto.ClientArrivalEventDTO;
import utilities.ClientMovement;
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

	private AppLayoutManager appLayoutManager;

	public Simulation(ClientEventsHandler clientEventsHandler, ClientMovement clientMovement, MainLoop mainLoop, ObjectsManager objectsManager, AppLayoutManager appLayoutManager) {
		this.clientEventsHandler = clientEventsHandler;
		this.clientMovement = clientMovement;
		this.mainLoop = mainLoop;
		this.objectsManager = objectsManager;
		this.appLayoutManager = appLayoutManager;
	}

	public void prepareSimulation(double simulationStartTime, SortedSet<ClientArrivalEventDTO> clientArrivalEventDTOS)  {

		ClientActionDTO clientActionDTO;
   		clientId = 0;

		Map<Integer, List<Client>> queueIndexToClientsInQueueMap = new HashMap<>();
		SortedSet<ClientActionDTO> clientActionDTOS = new TreeSet<>();
		ClientArrivalEventDTO lastArrival =  clientArrivalEventDTOS.stream().max(Comparator.comparing(ClientArrivalEventDTO::getArrivalTime)).orElseThrow(()->new IllegalArgumentException("simulation events empty"));
		for (ClientArrivalEventDTO event : clientArrivalEventDTOS) {
			double arrivalTime = event.getArrivalTime();
			int queueNumber = event.getQueueNumber();
			List<Client> clients = queueIndexToClientsInQueueMap.computeIfAbsent(queueNumber, index -> new ArrayList<>());
			Client client = new Client(clientId++,
					queueNumber, clients.size(),
					arrivalTime, event.getTimeInCheckout());
			clients.add(client);
			objectsManager.addVisibleClient(client);


			clientActionDTO = createClientAction(client, queueNumber, arrivalTime, simulationStartTime,
					clients.size());

			mainLoop.addObject(client);
			clientActionDTOS.add(clientActionDTO);
			if (event == lastArrival) {
				clientActionDTO = new ClientActionDTO(arrivalTime,
						ClientPositionType.PAUSE, null);
				clientActionDTOS.add(clientActionDTO);
			}
		}
		clientEventsHandler.setEventsList(clientActionDTOS);

    }

	private ClientActionDTO createClientAction(Client client, int queueNumber, double arrivalTime,
											   double simulationStartTime, int peopleInQueue){
	
		// TODO this is too similar method to calculateAppearTime check it
		
		
		Point pointInitial= appLayoutManager.calculateClientDestinationCoordinates(0, 0, ClientPositionType.ARRIVAL);
		Point pointWaitPlace= appLayoutManager.calculateClientDestinationCoordinates(0, 0, ClientPositionType.WAITING_ROOM);

		Point pointInQueue= appLayoutManager.calculateClientDestinationCoordinates(peopleInQueue,
                             queueNumber, ClientPositionType.GOING_TO_QUEUE);
        Point calculatedPosition = clientMovement.calculateCoordinates(pointInQueue, pointInitial,
        								arrivalTime);

		double totalTime= clientMovement.calculateTimeFromWaitingRoomToQueue(pointInQueue, pointWaitPlace);

		ClientPositionType positionType;
		if (calculatedPosition.equals(pointInQueue)){
			positionType = ClientPositionType.GOING_TO_QUEUE;
			client.saveInformation(calculatedPosition, positionType);
			return new ClientActionDTO(arrivalTime, positionType, client);
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
		return new ClientActionDTO(time, positionType, client);
	}


    


}
    


