package simulation;

import core.MainLoop;
import dto.ClientArrivalEventDTO;

import java.util.SortedSet;

import core.ObjectsManager;
import spring2.Bean;
import view.NavigationPanel;
import view.SimulationPanel;
import visualComponents.Indicator;

@Bean
public class SimulationController {
	

	private Simulation simulation;

	private NavigationPanel navigationPanel;

	public Indicator waitingRoomIndicator;

	private ApplicationConfiguration applicationConfiguration;

	private final ObjectsManager objectsManager;

	private final MainLoop mainLoop;


	private SortedSet<ClientArrivalEventDTO> timeTable;

	private SimulationPanel simulationPanel;

	public SimulationController(Indicator waitingRoomIndicator, ApplicationConfiguration applicationConfiguration, Simulation simulation, NavigationPanel navigationPanel, ObjectsManager objectsManager, MainLoop mainLoop, SimulationPanel simulationPanel)  {

		this.applicationConfiguration = applicationConfiguration;
		this.simulation = simulation;
		this.navigationPanel = navigationPanel;
		this.objectsManager = objectsManager;
		this.mainLoop = mainLoop;
		this.simulationPanel = simulationPanel;
		 this.waitingRoomIndicator = waitingRoomIndicator;
	}

	public void restart(double time, SortedSet<ClientArrivalEventDTO> timeTable) {
		
		removeObjects();
		objectsManager.initializeObjects();
		objectsManager.getAnimatedObjects().forEach(mainLoop::addObject);
		doVisualization(time, timeTable);
	}

	public void restart (double time){
		restart(time, timeTable);
	}
	
	public void removeObjects(){
		objectsManager.removeObjects();
		simulationPanel.removeObjects();
		mainLoop.removeObjects();
	}

	public void initialize (double time, SortedSet<ClientArrivalEventDTO> clientArrivalEventDTOS){
		this.timeTable = clientArrivalEventDTOS;
		applicationConfiguration.setSimulationTime(timeTable.last().getArrivalTime());

		MainLoop.setTimePassed (time);
		waitingRoomIndicator.clear();

		navigationPanel.setButtonRestartToActive();
	}


	public void doVisualization(double time, SortedSet<ClientArrivalEventDTO> clientArrivalEventDTOS)  {

		initialize(time, clientArrivalEventDTOS);
        simulation.prepareSimulation(time, clientArrivalEventDTOS);
		startSimulation();

    }

	public void startSimulation(){
		mainLoop.resume();
		navigationPanel.setButtonStopToPause();
		simulationPanel.repaint();
	}


	public boolean pauseSimulation(){
		boolean wasPaused = mainLoop.isPaused();
		mainLoop.pause();
		navigationPanel.setButtonStopToResume();
		simulationPanel. stopSprites();
		return wasPaused;
	}


}
