package symulation;

import core.MainLoop;
import events.EventSubscriber;
import interfaces.AnimatedObject;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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

	private Door door;
	
	private PrintWriter printWriter;
	private File logsFile =new File("./src/logs/log.txt");
	private static final String lOG_HEADER="Queue no.\ttime predicted\tarrival time";

	public OutsideWorld outside;
	public StoreCheckout[] storeCheckouts;
	public Indicator waitingRoomIndicator;

	private int numberOfQueues;
	

	private List<ClientAction> listOfEvents;
	
	public Manager(Painter painter){


		this.painter = painter;
		painter.addEventsSubscriber(this);


		outside = new OutsideWorld();
		 waitingRoomIndicator=new Indicator(painter);
		 Point point=painter.getDoorPosition();
		 int i=0;
		 while (painter.getCheckoutPosition(i).x<point.x){
			 i++;
		 }
		 System.out.println("IIIIIIIIIII"+i);

		this.numberOfQueues=Painter.getNumberOfQueues();
		 timeTable=new TimeTable();
		 simulation=new Simulation(painter,this);
		 
	     try{
			 if (!logsFile.getParentFile().exists()){
				 logsFile.getParentFile().mkdirs();
			 }
	         printWriter=new PrintWriter(logsFile,"UTF-8");
	         printWriter.println(lOG_HEADER);
	     }
	     catch (FileNotFoundException fg){
	         fg.printStackTrace();
	     }
	     catch (UnsupportedEncodingException fg){
	         fg.printStackTrace();
	     }
	     
	    
	}

	public void initializeStaticObjects (){
		door = new Door(painter, 0);
		door.initializePosition();
		int numberOfQueues = Painter.getNumberOfQueues();
		storeCheckouts =new StoreCheckout[numberOfQueues];
		for (int i=0;i<numberOfQueues;i++){
			storeCheckouts[i]=new StoreCheckout(painter,i);
		}
	}

	public Door getDoor() {
		return door;
	}

	public void setTimeTable(double [][] arrivals, double [][] departures){
        timeTable.arrivals=arrivals;
        timeTable.departures=departures;
    }

	public void restart(double time) throws Exception {
		
		clean();
		initializeStaticObjects();
		doSimulation(time);
	}
	
	public void clean(){
		painter.clean();
	}

    public void doSimulation () throws Exception {
        doSimulation(0.0);
    }


	public void doSimulation (double time) throws Exception {
    	Client.nr=0;
		MainLoop.getInstance().setTimePassed ((long)time * 1000);
    	waitingRoomIndicator.clear();
    	
        painter.setButtonRestartToActive();
        simulation.prepareSimulation(time,timeTable.arrivals,timeTable.departures);

        painter.resume(false);
//        System.out.println("resume");
    }
    


	
	public void openDoor(){
		door.doOpening();		
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
    
    public void stopWritingLogs(){
        printWriter.close();
    }
    
    public void saveEvent (int queueNumber, double timePredicted, double arrivalTime){
        printWriter.println(queueNumber+"\t"+timePredicted+"\t"+arrivalTime+"\t");
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
            if (door.getObserversSize()!=0){
            	Client c=(Client)door.getFirstObserver();
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
