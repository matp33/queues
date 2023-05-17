package symulation;

import interfaces.AnimatedObject;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.swing.JOptionPane;

import animations.Sprite;
import otherFunctions.ClientAction;
import otherFunctions.TimeTable;
import visualComponents.Client;
import visualComponents.Door;
import visualComponents.Indicator;
import visualComponents.OutsideWorld;
import visualComponents.Queue;

public class Manager {
	
	public static final int SPRITE_CLIENT=0;
	public static final int SPRITE_QUEUE = 1;
	
	private Simulation simulation;
	private Painter painter;
	private TimeTable timeTable;
	private Timing timerClass;
	
	private PrintWriter printWriter;
	private File logsFile =new File("./src/logs/log.txt");
	private static final String lOG_HEADER="Queue no.\ttime predicted\tarrival time";

	public OutsideWorld outside;
	public Queue[] queues; 
	public Indicator waitingRoomIndicator;
	public Door door;
	
	private int numberOfQueues;
	
	private Sprite clientSprite;
	private Sprite doorSprite;
	private Sprite queueSprite;
	private List<ClientAction<Double,Integer,Client>> listOfEvents;
	
	public Manager(int numberOfQueues){
		
		 
		try {
			doorSprite = new Sprite (43, 69, "/door2.png");
			Sprite backgrnd=new Sprite(620,395,"/supermarket-kolejka.jpg");
			clientSprite = new Sprite (30, 45, "/sprite.png");
			queueSprite = new Sprite (106, 58, "/kasa.png");
		
			
		 
			 BufferedImage [] images=new BufferedImage [4];
	         
			 images[0]=queueSprite.getSprite(0, 0);		
			 images[1]=backgrnd.getSprite(0, 0);
	         images[2]=doorSprite.getSprite(0, 0);
	         images[3]=clientSprite.getSprite(0, 0);
	         
	         
	         
	         painter = new Painter(numberOfQueues, 0, images,this);
	         timerClass=new Timing(numberOfQueues, this,painter);
	         
	         queues=new Queue [numberOfQueues];
				 for (int i=0;i<numberOfQueues;i++){
		            queues[i]=new Queue (queueSprite,painter,i);
		         }
	         
         } 
		
		catch (IOException e1) {
 			e1.printStackTrace();
 		}
		
		 outside = new OutsideWorld();
		 waitingRoomIndicator=new Indicator(painter);
		 Dimension d=painter.getDoorPosition();
		 int i=0;
		 while (painter.getTillPosition(i).width<d.width){
			 i++;
		 }
		 System.out.println("IIIIIIIIIII"+i);
		 door=new Door(doorSprite,20,painter,i);
		 door.start();
         
         
		 this.numberOfQueues=numberOfQueues;
		 timeTable=new TimeTable();	  
		 simulation=new Simulation(numberOfQueues,painter,this); //simulation starts here
		 
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
	
	public void saveTimeTable(double [][] arrivals, double [][] departures){
        timeTable.arrivals=arrivals;
        timeTable.departures=departures;
    }
	
	public void restart(double time) throws InterruptedException{
		
		clean();
		doSimulation(time);
	}
	
	public void clean(){
		//TODO its copypasted
		painter.clean();
		queues=new Queue [numberOfQueues];
		 for (int i=0;i<numberOfQueues;i++){
           queues[i]=new Queue (queueSprite,painter,i);
           
           
        }
		 Dimension d=painter.getDoorPosition();
		 int i=0;
		 while (painter.getTillPosition(i).width<d.width){
			 i++;
		 }
		 door=new Door(doorSprite,20,painter,i);		 
		 door.start();
		 System.out.println("!!!!!!!!!!!!!!!!!!!!!! "+i);
	}

    public void doSimulation () throws InterruptedException{
        doSimulation(0.0);
    }

    public void doSimulation (double time) throws InterruptedException{
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
    
    public boolean isQueueNumberSame(int numbOfQueues) {
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
    
    public void restartSimulation(){
    	restartSimulation(numberOfQueues);
    }
    
    public void restartSimulation(int numberOfQueues){
    	painter.cleanScreen();
    	simulation=new Simulation(numberOfQueues,painter,this);
    }
    
    public double getTime(){
    	return timerClass.getTime();
    }
    
    public void doChange(int numberOfQueues){
    	this.numberOfQueues=numberOfQueues;
    	simulation.setNumberOfQueues(numberOfQueues);
    	painter.initiate(numberOfQueues,true);
       
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
        for (int i=0; i< queues.length;i++){
            if (!queues[i].getClientsList().isEmpty()){
//            	System.out.println("1");
                return true;
            }
            if (!queues[i].getClientsArriving().isEmpty()){
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
    
    
    public Sprite getSprite (int type){
    	if (type==SPRITE_CLIENT){
    		return clientSprite;
    	}
    	else if (type==SPRITE_QUEUE){
    		return queueSprite;    		
    	}
    	else return null;
    }
    
    public Queue getQueue (int queueNumber){
    	return queues[queueNumber];
    }
    
    public void saveEventsList(List<ClientAction<Double,Integer,Client>> e){
    	listOfEvents = e;
    }
    
    public List <AnimatedObject> getAllObjects(){
    	return painter.getAllObjects();
    }
	
}
