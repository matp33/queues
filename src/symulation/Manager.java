package symulation;

import core.MainLoop;
import events.EventSubscriber;
import interfaces.AnimatedObject;

import java.awt.*;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JOptionPane;

import otherFunctions.ClientAction;
import visualComponents.Client;
import visualComponents.Door;
import visualComponents.Indicator;
import visualComponents.OutsideWorld;
import visualComponents.StoreCheckout;

public class Manager implements EventSubscriber {
	

	private Simulation simulation;
	private Painter painter;
	private SortedSet<ClientArrivalEvent> timeTable = new TreeSet<>();

	public OutsideWorld outside;
	public StoreCheckout[] storeCheckouts;
	public Indicator waitingRoomIndicator;

	private int numberOfQueues;
	

	private List<ClientAction> listOfEvents;

	private ApplicationConfiguration applicationConfiguration;
	
	public Manager()  {

		applicationConfiguration = ApplicationConfiguration.getInstance();
		this.painter = applicationConfiguration.getPainter();
		painter.addEventsSubscriber(this);


		outside = new OutsideWorld();
		 waitingRoomIndicator=new Indicator();
		 Point point=painter.getDoorPosition();
		 int i=0;
		 while (painter.getCheckoutPosition(i).x<point.x){
			 i++;
		 }
		 System.out.println("IIIIIIIIIII"+i);

		this.numberOfQueues=applicationConfiguration.getNumberOfQueues();
		 simulation=new Simulation();

	}

	public void initializeStaticObjects (){
		Door door = new Door(0);
		door.initializePosition();
		int numberOfQueues = applicationConfiguration.getNumberOfQueues();
		storeCheckouts =new StoreCheckout[numberOfQueues];
		for (int i=0;i<numberOfQueues;i++){
			storeCheckouts[i]=new StoreCheckout(i);
		}
		ApplicationConfiguration.getInstance().getClientEventsHandler().addToLoop();
	}

	public void setTimeTable(SortedSet<ClientArrivalEvent> clientArrivalEvents){
        this.timeTable = clientArrivalEvents;
    }

	public void restart(double time) {
		
		clean();
		initializeStaticObjects();
		doSimulation(time);
	}
	
	public void clean(){
		painter.clean();
		MainLoop.getInstance().removeObjects();
	}

    public void doSimulation ()  {
        doSimulation(0.0);
    }


	public void doSimulation (double time)  {
    	Client.nr=0;
		MainLoop.getInstance().setTimePassed (time);
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


    public boolean isAnyClientThere(){
        for (int i = 0; i< storeCheckouts.length; i++){
            if (!storeCheckouts[i].getClientsList().isEmpty()){
//            	System.out.println("1");
                return true;
            }
            if (!storeCheckouts[i].getClientsArriving().isEmpty()){
//            	System.out.println("2");
                return true;
            }
//            System.out.println("@@"+door.getObserversSize());
            if (painter.getDoor().getObserversSize()!=0){
            	Client c=(Client)painter.getDoor().getFirstObserver();
//            	System.out.println("3"+c.id);
                return true;
            }
            
            if (outside.getObserversSize()!=0){
//            	System.out.println("4");
            	return true;
            }
        }
        
        return false;
//        return !timerClass.getClientsArriving().isEmpty() || !timerClass.getClientsExiting().isEmpty();
    }


    public StoreCheckout getQueue (int queueNumber){
    	return storeCheckouts[queueNumber];
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
