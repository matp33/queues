
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
import utilities.EventTimesCalculator;
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

	private EventTimesCalculator eventTimesCalculator;

	private ApplicationConfiguration applicationConfiguration;

	public Simulation(ClientEventsHandler clientEventsHandler, ClientMovement clientMovement, MainLoop mainLoop, ObjectsManager objectsManager, AppLayoutManager appLayoutManager, EventTimesCalculator eventTimesCalculator, ApplicationConfiguration applicationConfiguration) {
		this.clientEventsHandler = clientEventsHandler;
		this.clientMovement = clientMovement;
		this.mainLoop = mainLoop;
		this.objectsManager = objectsManager;
		this.appLayoutManager = appLayoutManager;
		this.eventTimesCalculator = eventTimesCalculator;
		this.applicationConfiguration = applicationConfiguration;
	}

	public void prepareSimulation(double simulationStartTime, SortedSet<ClientArrivalEventDTO> clientArrivalEventDTOS)  {

		ClientActionDTO clientActionDTO;
   		clientId = 0;
	   eventTimesCalculator.initialize();

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
			eventTimesCalculator.calculateClientEventTimes(client);

			mainLoop.addObject(client);
			clientActionDTOS.add(clientActionDTO);
			if (event == lastArrival) {
				clientActionDTO = new ClientActionDTO(arrivalTime,
						ClientPositionType.PAUSE, null);
				clientActionDTOS.add(clientActionDTO);
			}
		}
		applicationConfiguration.setSimulationTime(eventTimesCalculator.getSimulationEndTime());
		clientEventsHandler.setEventsList(clientActionDTOS);

    }

	private ClientActionDTO createClientAction(Client client, int queueNumber, double arrivalTime,
											   double simulationStartTime, int peopleInQueue){

		Point pointWaitPlace= appLayoutManager.calculateClientDestinationCoordinates(0, 0, ClientPositionType.QUEUE_FOR_ENTRANCE);
		Point pointInQueue= appLayoutManager.calculateClientDestinationCoordinates(peopleInQueue,
                             queueNumber, ClientPositionType.GOING_TO_QUEUE);
		double totalTime= clientMovement.calculateTimeFromWaitingRoomToQueue(pointInQueue, pointWaitPlace);

		ClientPositionType positionType;
		double time;
		Point calculatedPosition;

	    if ( arrivalTime >= simulationStartTime){
	    	positionType=ClientPositionType.QUEUE_FOR_ENTRANCE;
	    	calculatedPosition=pointWaitPlace;
			time = simulationStartTime;
	    }
	    else {
			positionType= ClientPositionType.GOING_TO_QUEUE;
			time=arrivalTime-totalTime;
			calculatedPosition = pointInQueue;
	    }
		client.saveInformation(calculatedPosition, positionType);
		return new ClientActionDTO(time, positionType, client);
	}


    


}
    


