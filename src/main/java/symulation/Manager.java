package symulation;

import core.MainLoop;
import events.ClientEventsHandler;
import events.EventSubscriber;
import events.ObjectsManager;
import interfaces.AnimatedObject;

import java.awt.*;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JOptionPane;

import otherFunctions.ClientAction;
import spring2.Bean;
import visualComponents.Client;
import visualComponents.Indicator;
import visualComponents.OutsideWorld;

@Bean
public class Manager implements EventSubscriber {
	

	private Simulation simulation;
	private Painter painter;
	private SortedSet<ClientArrivalEvent> timeTable = new TreeSet<>();

	public OutsideWorld outside;
	public Indicator waitingRoomIndicator;

	private int numberOfQueues;
	

	private List<ClientAction> listOfEvents;

	private ApplicationConfiguration applicationConfiguration;

	private final ObjectsManager objectsManager;

	private final MainLoop mainLoop;

	private final ClientEventsHandler clientEventsHandler;

	public Manager(Indicator waitingRoomIndicator, ApplicationConfiguration applicationConfiguration, Simulation simulation, Painter painter, ObjectsManager objectsManager, MainLoop mainLoop, ClientEventsHandler clientEventsHandler)  {

		this.applicationConfiguration = applicationConfiguration;
		this.simulation = simulation;
		this.painter = painter;
		this.objectsManager = objectsManager;
		this.mainLoop = mainLoop;
		this.clientEventsHandler = clientEventsHandler;
		painter.addEventsSubscriber(this);


		outside = new OutsideWorld();
		 this.waitingRoomIndicator = waitingRoomIndicator;
		 Point point=painter.getDoorPosition();
		 int i=0;
		 while (painter.getCheckoutPosition(i).x<point.x){
			 i++;
		 }
		 System.out.println("IIIIIIIIIII"+i);

		this.numberOfQueues=applicationConfiguration.getNumberOfQueues();

	}


	public void setTimeTable(SortedSet<ClientArrivalEvent> clientArrivalEvents){
        this.timeTable = clientArrivalEvents;
    }

	public void restart(double time) {
		
		clean();
		objectsManager.initializeObjects();
		doSimulation(time);
		mainLoop.addObject(clientEventsHandler);

	}
	
	public void clean(){
		painter.clean();
		mainLoop.removeObjects();
	}

    public void doSimulation ()  {
        doSimulation(0.0);
    }


	public void doSimulation (double time)  {
    	Client.nr=0;
		mainLoop.setTimePassed (time);
    	waitingRoomIndicator.clear();
    	
        painter.setButtonRestartToActive();
        simulation.prepareSimulation(time,timeTable);

        painter.resume(false);
//        System.out.println("resume");
    }

	public boolean isTimeTableNotEmpty(){
        return !timeTable.isEmpty();
    }


    public boolean isStoreCheckoutNumberSame(int numbOfQueues) {
        return numbOfQueues==numberOfQueues;
    }

    public void beginSimulation(){
    	painter.repaint(painter.getMovementArea());
//	    System.out.println("!!!!!! "+simulation.painter.getMovementArea());
	    painter.setButtonRestartToActive();
	    painter.setButtonStopActiveness(true);
    }
    
    public int displayWindowWithPanel(Component panel, String title){
 
       return JOptionPane.showOptionDialog(painter, panel, title,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, new Object[]{"Ok","Cancel"}, "Ok");

    }

    public void setEventsList(List<ClientAction> e){
    	listOfEvents = e;
    }
    
    public List <AnimatedObject> getAllObjects(){
    	return painter.getAllObjects();
    }

	@Override
	public void handleNewTimetable(SortedSet<ClientArrivalEvent> clientArrivalEvents) {
		setTimeTable(clientArrivalEvents);
		painter.setTimeTable(clientArrivalEvents);
		restart(0);
	}

	@Override
	public void handleRestart(double time) {
		restart(time);
	}
}
