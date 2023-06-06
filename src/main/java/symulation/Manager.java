package symulation;

import clienthandling.ClientEventsHandler;
import core.MainLoop;
import events.*;

import java.util.SortedSet;

import otherFunctions.ObjectsManager;
import spring2.Bean;
import view.NavigationPanel;
import view.SimulationPanel;
import visualComponents.Indicator;
import visualComponents.OutsideWorld;

@Bean
public class Manager {
	

	private Simulation simulation;

	private NavigationPanel navigationPanel;

	public OutsideWorld outside;
	public Indicator waitingRoomIndicator;

	private int numberOfQueues;


	private ApplicationConfiguration applicationConfiguration;

	private final ObjectsManager objectsManager;

	private final MainLoop mainLoop;

	private final ClientEventsHandler clientEventsHandler;

	private SortedSet<ClientArrivalEvent> timeTable;

	private SimulationPanel simulationPanel;

	public Manager(Indicator waitingRoomIndicator, ApplicationConfiguration applicationConfiguration, Simulation simulation, NavigationPanel navigationPanel, ObjectsManager objectsManager, MainLoop mainLoop, ClientEventsHandler clientEventsHandler, SimulationPanel simulationPanel, UIEventQueue uiEventQueue)  {

		this.applicationConfiguration = applicationConfiguration;
		this.simulation = simulation;
		this.navigationPanel = navigationPanel;
		this.objectsManager = objectsManager;
		this.mainLoop = mainLoop;
		this.clientEventsHandler = clientEventsHandler;
		this.simulationPanel = simulationPanel;

		outside = new OutsideWorld();
		 this.waitingRoomIndicator = waitingRoomIndicator;

		this.numberOfQueues=applicationConfiguration.getNumberOfQueues();

	}

	public void restart(double time, SortedSet<ClientArrivalEvent> timeTable) {
		
		clean();
		objectsManager.initializeObjects();
		doSimulation(time, timeTable);
		mainLoop.addObject(clientEventsHandler);

	}

	public void restart (double time){
		restart(time, timeTable);
	}
	
	public void clean(){
		simulationPanel.clean();
		mainLoop.removeObjects();
	}


	public void doSimulation (double time, SortedSet<ClientArrivalEvent> clientArrivalEvents)  {
		this.timeTable = clientArrivalEvents;
		applicationConfiguration.setSimulationTime(timeTable.last().getArrivalTime());

		mainLoop.setTimePassed (time);
    	waitingRoomIndicator.clear();
    	
        navigationPanel.setButtonRestartToActive();
        simulation.prepareSimulation(time,clientArrivalEvents);

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
