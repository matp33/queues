package simulation;

import clienthandling.ClientEventsHandler;
import core.MainLoop;
import dto.ClientArrivalEventDTO;
import events.*;

import java.util.SortedSet;

import core.ObjectsManager;
import spring2.Bean;
import view.NavigationPanel;
import view.SimulationPanel;
import visualComponents.Indicator;

@Bean
public class Manager {
	

	private Simulation simulation;

	private NavigationPanel navigationPanel;

	public Indicator waitingRoomIndicator;

	private int numberOfQueues;


	private ApplicationConfiguration applicationConfiguration;

	private final ObjectsManager objectsManager;

	private final MainLoop mainLoop;


	private SortedSet<ClientArrivalEventDTO> timeTable;

	private SimulationPanel simulationPanel;

	public Manager(Indicator waitingRoomIndicator, ApplicationConfiguration applicationConfiguration, Simulation simulation, NavigationPanel navigationPanel, ObjectsManager objectsManager, MainLoop mainLoop, SimulationPanel simulationPanel)  {

		this.applicationConfiguration = applicationConfiguration;
		this.simulation = simulation;
		this.navigationPanel = navigationPanel;
		this.objectsManager = objectsManager;
		this.mainLoop = mainLoop;
		this.simulationPanel = simulationPanel;

		 this.waitingRoomIndicator = waitingRoomIndicator;

		this.numberOfQueues=applicationConfiguration.getNumberOfQueues();

	}

	public void restart(double time, SortedSet<ClientArrivalEventDTO> timeTable) {
		
		removeObjects();
		objectsManager.initializeObjects();
		objectsManager.getAnimatedObjects().forEach(mainLoop::addObject);
		doSimulation(time, timeTable);
	}

	public void restart (double time){
		restart(time, timeTable);
	}
	
	public void removeObjects(){
		objectsManager.removeObjects();
		simulationPanel.removeObjects();
		mainLoop.removeObjects();
	}


	public void doSimulation (double time, SortedSet<ClientArrivalEventDTO> clientArrivalEventDTOS)  {
		this.timeTable = clientArrivalEventDTOS;
		applicationConfiguration.setSimulationTime(timeTable.last().getArrivalTime());

		MainLoop.setTimePassed (time);
    	waitingRoomIndicator.clear();
    	
        navigationPanel.setButtonRestartToActive();
        simulation.prepareSimulation(time, clientArrivalEventDTOS);

		resumeSimulation();

//        System.out.println("resume");
    }

	public void resumeSimulation (){
		mainLoop.resume();
		navigationPanel.setButtonStopToPause();
		simulationPanel.repaint();
	}


    public boolean isStoreCheckoutNumberSame(int numbOfQueues) {
        return numbOfQueues==numberOfQueues;
    }

    public void beginSimulation(){
    	simulationPanel.repaint();
	    navigationPanel.setButtonRestartToActive();
	    navigationPanel.setButtonStopActiveness(true);
    }



	public boolean pause (){
		boolean wasPaused = mainLoop.isPaused();
		mainLoop.pause();
		navigationPanel.setButtonStopToResume();
		simulationPanel. stopSprites();
		return wasPaused;
	}


}
