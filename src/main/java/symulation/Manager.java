package symulation;

import core.MainLoop;
import events.ClientEventsHandler;
import events.EventSubscriber;
import events.ObjectsManager;

import java.util.SortedSet;

import javax.swing.*;

import events.UIEventQueue;
import spring2.Bean;
import view.NavigationPanel;
import view.SimulationPanel;
import visualComponents.Client;
import visualComponents.Indicator;
import visualComponents.OutsideWorld;

@Bean
public class Manager implements EventSubscriber {
	

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
		uiEventQueue.addSubscriber(this);

		outside = new OutsideWorld();
		 this.waitingRoomIndicator = waitingRoomIndicator;

		this.numberOfQueues=applicationConfiguration.getNumberOfQueues();

	}


	@Override
	public int handleNewDialog(JPanel panel, String title) {
		return simulationPanel.displayWindowWithPanel(panel, title);
	}

	@Override
	public void handleNewMessage(String message) {
		simulationPanel.displayMessage(message);
	}

	@Override
	public void handleReinitializeEvent() {

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
    	Client.nr=0;
		mainLoop.setTimePassed (time);
    	waitingRoomIndicator.clear();
    	
        navigationPanel.setButtonRestartToActive();
        simulation.prepareSimulation(time,clientArrivalEvents);

		resumeSimulation();

//        System.out.println("resume");
    }

	private void resumeSimulation (){
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
    

	@Override
	public void handleNewTimetable(SortedSet<ClientArrivalEvent> clientArrivalEvents) {
		restart(0, clientArrivalEvents);
	}

	@Override
	public void handleRestart(double time) {
		restart(time);
	}

	@Override
	public void handleResume() {
		resumeSimulation();
	}

	@Override
	public boolean handlePause() {
		boolean wasPaused = mainLoop.isPaused();
		mainLoop.pause();
		navigationPanel.setButtonStopToResume();
		simulationPanel. stopSprites();
		return wasPaused;
	}
}
