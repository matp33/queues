package simulation;

import constants.UIEventType;
import core.MainLoop;
import dto.ClientArrivalEventDTO;

import java.util.SortedSet;

import core.ObjectsManager;
import events.UIEvent;
import events.UIEventHandler;
import events.UIEventQueue;
import spring2.Bean;
import view.NavigationPanel;
import view.SimulationPanel;
import visualComponents.Indicator;

@Bean
public class SimulationController implements UIEventHandler {
	

	private Simulation simulation;

	private NavigationPanel navigationPanel;

	public Indicator waitingRoomIndicator;

	private ApplicationConfiguration applicationConfiguration;

	private final ObjectsManager objectsManager;

	private final MainLoop mainLoop;


	private SortedSet<ClientArrivalEventDTO> timeTable;

	private SimulationPanel simulationPanel;

	private boolean repaintRequested = false;

	private AppLayoutManager appLayoutManager;

	public SimulationController(Indicator waitingRoomIndicator, ApplicationConfiguration applicationConfiguration, Simulation simulation, NavigationPanel navigationPanel, ObjectsManager objectsManager, MainLoop mainLoop, SimulationPanel simulationPanel, AppLayoutManager appLayoutManager, UIEventQueue uiEventQueue)  {

		this.applicationConfiguration = applicationConfiguration;
		this.simulation = simulation;
		this.navigationPanel = navigationPanel;
		this.objectsManager = objectsManager;
		this.mainLoop = mainLoop;
		this.simulationPanel = simulationPanel;
		 this.waitingRoomIndicator = waitingRoomIndicator;
		this.appLayoutManager = appLayoutManager;
		uiEventQueue.subscribeToEvents(this, UIEventType.SIMULATION_FINISHED);
	}

	public void restart(double time, SortedSet<ClientArrivalEventDTO> timeTable) {

		simulationPanel.toggleSimulationFinished();
		navigationPanel.setButtonStopActiveness(true);
		removeObjects();
		if (repaintRequested){
			repaintRequested = false;
			appLayoutManager.initialize(applicationConfiguration.getNumberOfQueues(), navigationPanel.getPanel());
			appLayoutManager.calculateWindowSize(applicationConfiguration.getNumberOfQueues());
		}
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


	public void requestRepaint() {
		repaintRequested = true;
	}

	@Override
	public void handleEvent(UIEvent<?> uiEvent) {
		simulationPanel.toggleSimulationFinished();
		simulationPanel.repaint();
		navigationPanel.setButtonStopActiveness(false);
		pauseSimulation();
	}
}
