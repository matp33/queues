package symulation;

import interfaces.AnimatedObject;

import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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

public class Manager {
	

	private Simulation simulation;
	private Painter painter;
	private TimeTable timeTable;
	private Timing timerClass;
	
	private PrintWriter printWriter;
	private File logsFile =new File("./src/logs/log.txt");
	private static final String lOG_HEADER="Queue no.\ttime predicted\tarrival time";

	public OutsideWorld outside;
	public StoreCheckout[] storeCheckouts;
	public Indicator waitingRoomIndicator;
	public Door door;
	
	private int numberOfQueues;
	

	private List<ClientAction> listOfEvents;
	
	public Manager(int checkoutsAmount){
		
		 
		try {

	         painter = new Painter(checkoutsAmount, this);
	         timerClass=new Timing(this,painter);
	         
	         storeCheckouts =new StoreCheckout[checkoutsAmount];
				 for (int i=0;i<checkoutsAmount;i++){
		            storeCheckouts[i]=new StoreCheckout(painter,i);
		         }
	         
         } 
		
		catch (IOException e1) {
 			e1.printStackTrace();
 		}
		
		 outside = new OutsideWorld();
		 waitingRoomIndicator=new Indicator(painter);
		 Dimension d=painter.getDoorPosition();
		 int i=0;
		 while (painter.getCashRegisterPosition(i).width<d.width){
			 i++;
		 }
		 System.out.println("IIIIIIIIIII"+i);
		 door=new Door(painter,i);
		 door.start();
         
         
		 this.numberOfQueues=checkoutsAmount;
		 timeTable=new TimeTable();	  
		 simulation=new Simulation(checkoutsAmount,painter,this); //simulation starts here
		 
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
	
	public void setTimeTable(double [][] arrivals, double [][] departures){
        timeTable.arrivals=arrivals;
        timeTable.departures=departures;
    }
	
	public void restart(double time) throws Exception {
		
		clean();
		doSimulation(time);
	}
	
	public void clean(){
		//TODO its copypasted
		painter.clean();
		storeCheckouts =new StoreCheckout[numberOfQueues];
		 for (int i=0;i<numberOfQueues;i++){
           storeCheckouts[i]=new StoreCheckout( painter,i);
           
           
        }
		 Dimension d=painter.getDoorPosition();
		 int i=0;
		 while (painter.getCashRegisterPosition(i).width<d.width){
			 i++;
		 }
		 door=new Door(painter,i);
		 door.start();
		 System.out.println("!!!!!!!!!!!!!!!!!!!!!! "+i);
	}

    public void doSimulation () throws Exception {
		clean();
        doSimulation(0.0);
    }


	public void doSimulation (double time) throws Exception {
    	Client.nr=0;
    	waitingRoomIndicator.clear();
    	
        painter.setButtonRestartToActive();
        timerClass.setTime(time);
        simulation.prepareSimulation(time,timeTable.arrivals,timeTable.departures);
        System.out.println("hI");
        timerClass.setEventsList(listOfEvents);
        timerClass.setRunning(true);
        
        resume(false);
//        System.out.println("resume");
    }
    
    public void resume(boolean fromZero){
		
	    if (fromZero==true){	
	        timerClass.setTime(0);
	    }
	    
	    if (timerClass.timer==null){
	    	timerClass.startSimulation();
	    }
	    painter.resumeSprites();
		painter.setButtonStopToPaused();
	    	
	}

	public void pause(){
		
		if (!timerClass.isRunning()){
			return;
		}
	    
		timerClass.stopSimulation();
	    painter.setButtonStopToResume();
	    painter.stopSprites();
	    
	    
	}
	
	public void openDoor(){
		door.doOpening();		
	}
	
	

	public int getNumberOfClientsAtDoor(){
		return door.getObserversSize();
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
    
    public boolean isRunning(){
    	return timerClass.isRunning();
    }
    
    public double getTime(){
    	return timerClass.getTime();
    }
    
    public void doChange(int numberOfQueues) throws IOException {
    	this.numberOfQueues=numberOfQueues;
    	simulation.setNumberOfQueues(numberOfQueues);
    	painter.initiate(numberOfQueues);
       
    }
    
    public void displayMessage (String text){
        JOptionPane.showMessageDialog(painter, text);
    }

    
    public void finishSimulation(boolean skipMsg){
    	painter.setButtonStopActiveness(false);
    	if (!skipMsg)
	    displayMessage(Simulation.SIMULATION_FINISHED);
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
    
    public boolean askQuestion (String question, String title){

        int chosenOption=JOptionPane.showOptionDialog(null, question, title,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, new Object[]{"Yes","No"}, "Yes");

        if (chosenOption==JOptionPane.YES_OPTION){
            return true;
        }
        else{
            return false;
        }

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
    
    public void saveEventsList(List<ClientAction> e){
    	listOfEvents = e;
    }
    
    public List <AnimatedObject> getAllObjects(){
    	return painter.getAllObjects();
    }
	
}
