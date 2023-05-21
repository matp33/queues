package symulation;

import core.MainLoop;
import events.EventSubscriber;
import interfaces.AnimatedObject;

import java.awt.*;
import java.io.*;
import java.util.List;

import javax.swing.JOptionPane;

import otherFunctions.ClientAction;
import otherFunctions.TimeTable;
import visualComponents.Client;
import visualComponents.Door;
import visualComponents.Indicator;
import visualComponents.OutsideWorld;
import visualComponents.StoreCheckout;

public class Manager implements EventSubscriber {
	

	private Simulation simulation;
	private Painter painter;
	private TimeTable timeTable;

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
		 waitingRoomIndicator=new Indicator(painter);
		 Point point=painter.getDoorPosition();
		 int i=0;
		 while (painter.getCheckoutPosition(i).x<point.x){
			 i++;
		 }
		 System.out.println("IIIIIIIIIII"+i);

		this.numberOfQueues=applicationConfiguration.getNumberOfQueues();
		 timeTable=new TimeTable();
		 simulation=new Simulation();

	}

	public void initializeStaticObjects (){
		Door door = new Door(painter, 0);
		door.initializePosition();
		int numberOfQueues = applicationConfiguration.getNumberOfQueues();
		storeCheckouts =new StoreCheckout[numberOfQueues];
		for (int i=0;i<numberOfQueues;i++){
			storeCheckouts[i]=new StoreCheckout(painter,i);
		}
	}

	public void setTimeTable(double [][] arrivals, double [][] departures){
        timeTable.arrivals=arrivals;
        timeTable.departures=departures;
    }

	public void restart(double time) {
		
		clean();
		initializeStaticObjects();
		doSimulation(time);
	}
	
	public void clean(){
		painter.clean();
	}

    public void doSimulation ()  {
        doSimulation(0.0);
    }


	public void doSimulation (double time)  {
    	Client.nr=0;
		MainLoop.getInstance().setTimePassed ((long)time * 1000);
    	waitingRoomIndicator.clear();
    	
        painter.setButtonRestartToActive();
        simulation.prepareSimulation(time,timeTable.arrivals,timeTable.departures);

        painter.resume(false);
//        System.out.println("resume");
    }

	public boolean isTimeTableNotEmpty(){
        return timeTable.arrivals!=null;
    }

    public boolean isTimeWithinSimulationRange(double time){
        return isTimeTableNotEmpty() && time<=timeTable.departures[timeTable.departures.length-1][0];
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
	public void handleNewTimetable(TimeTable event) {
		setTimeTable(event.arrivals, event.departures);
		painter.setTimeTable(event. arrivals, event.departures);
		try {
			restart(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void handleRestart(double time) {
		try {
			restart(time);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
