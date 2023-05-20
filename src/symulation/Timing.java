package symulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import constants.SimulationEventType;
import core.MainLoop;
import otherFunctions.ClientAction;
import visualComponents.Client;

public class Timing {
	private double time;
	public Timer timer;
	private List<ClientAction> listOfEvents;

	public int threadsNumber;
	private boolean isRunning;
	private static final int TIMER_TIME_DELAY=10;
	private Manager manager;
	private Painter painter;
	private Thread clientMovementThread;

	public Timing( Manager manager,Painter painter) {
//		simulation=s;
		this.painter=painter;
		this.manager=manager;
		threadsNumber=0;
		listOfEvents= new ArrayList<>();
		timer = new Timer();
				
	}

	public void startSimulation(){		    
	    
	    manager.beginSimulation();
	    scheduleTimeUpdatingForUI();
	    startThreadForClientPositionUpdates();
	
	}

	public void resumeSimulation (){
		try {
			MainLoop.getInstance().resume();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		isRunning = true;
		painter.repaint();
		scheduleTimeUpdatingForUI();
		startThreadForClientPositionUpdates();
	}
	
	public void stopSimulation(){
		
		timer.cancel();
	    timer.purge();
	    isRunning=false;
	    synchronized(listOfEvents){
	    	listOfEvents.notify();
	    }
	    
	}

	public void stopTimeCounting(){
		timer.cancel();
		isRunning = false;
	}
	
	public void scheduleTimeUpdatingForUI() {
		


	    TimerTask task=new TimerTask(){
	        @Override
	        public void run(){
	        	
//	        	System.out.println(listOfEvents.size()+"/"+manager.isAnyClientThere());
	        	
	        	synchronized(listOfEvents){
	        		if (!listOfEvents.isEmpty() && getTime()>=listOfEvents.get(0).getTime()){
	        			listOfEvents.notify();
	        		}
	        	}
	        	
	            if (listOfEvents.isEmpty() && manager.isAnyClientThere()==false){
	                manager.finishSimulation(false);
	                this.cancel();
	            }
	            
	            setTime(getTime()+(double)TIMER_TIME_DELAY/1000);
	            painter.updateTime(getTime());
	          
	        }
	    };
		timer = new Timer();
	    timer.scheduleAtFixedRate(task, 0, TIMER_TIME_DELAY);
	}

	private void startThreadForClientPositionUpdates() {
		
		Runnable r = new Runnable (){
			@Override
			public void run (){
				while (!listOfEvents.isEmpty() && isRunning==true){
					
					ClientAction clientAction=listOfEvents.get(0);
					double time=clientAction.getTime();
					
					synchronized (listOfEvents){
						while (isRunning() && getTime()<time){
							try {
								listOfEvents.wait();
							} 
							catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
					System.out.print("###### ");
					Thread.getAllStackTraces().keySet().stream().map(Thread::getName).sorted().peek(s-> System.out.print(", ")).forEach(System.out::print);
					System.out.println();
					if (!isRunning){
						System.out.println("returned");
						return;
					}
					
		    		listOfEvents.remove(0);

					SimulationEventType action=clientAction.getAction();
		            Client client=clientAction.getClient();

		            switch (action){
		                case ARRIVAL:
//		                     	    System.out.println("arrival");
							client.moveToWaitingRoom();
							client.startDrawingMe();
		                     break;
		                case APPEAR_IN_POSITION:
							client.moveToQueue();
							client.startDrawingMe();
//		                     	   System.out.println("appear");
							 break;
		                case DEPARTURE:
//		                     	    System.out.println("exit "+client.abc);
		                     client.moveToExit();
		                     break;
		                case PAUSE:
		                     manager.pause();
		                     boolean b=manager.askQuestion(Simulation.NO_MORE_ARRIVALS,
		                     		Simulation.TITLE_NO_MORE_ARRIVALS);
		                     if (b==false){
		                         manager.finishSimulation(true);	     	                                    
		                     }
		                     else{
		                     	manager.resume(false);  
		                     	return;		     	                                
		                     }	     	                                                              
		                     break; 	
		            	}
		                                        
//		                    System.out.println("delete 1; left: "+listOfEvents.size());

				}	
			}
		};
		
		System.out.println("calle"+listOfEvents.size());
		isRunning=true;
		double[] eventsTimes = new double [listOfEvents.size()];
		
		for (int i=0; i<listOfEvents.size();i++){  			
			eventsTimes[i]=listOfEvents.get(i).getTime();
			if (listOfEvents.get(i).getClient()!=null)
			System.out.println("!"+listOfEvents.get(i).getClient().id);
		}
		
		clientMovementThread =new Thread(r);
		clientMovementThread.start();
		
    }
	    
	

	
	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}
	
	public boolean isRunning(){
		return isRunning;
	}
	
	public void setRunning (boolean running){
		isRunning=running;
	}

	
	public void setEventsList(List<ClientAction> listOfEvents){
		this.listOfEvents=listOfEvents;
		
	}


	
}