package symulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
	private Thread t;

	public Timing( int numberOfQueues,Manager manager,Painter painter) {
//		simulation=s;
		this.painter=painter;
		this.manager=manager;
		threadsNumber=0;
		listOfEvents=new ArrayList <ClientAction> ();
				
	}

	public void startSimulation(){		    
	    
	    manager.beginSimulation();
	    startTimer();
	    startThreadForEvents();
	
	}
	
	public void stopSimulation(){
		
		timer.cancel();
	    timer.purge();
	    timer=null;
	    isRunning=false;
	    synchronized(listOfEvents){
	    	listOfEvents.notify();
	    }
	    
	}
	
	private void startTimer() {
		
		timer=new Timer();        
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
	    timer.scheduleAtFixedRate(task, 0, TIMER_TIME_DELAY);
	}

	private void startThreadForEvents() {
		
		Runnable r = new Runnable (){
			@Override
			public void run (){
				while (!listOfEvents.isEmpty() && isRunning==true){
					
					ClientAction c=listOfEvents.get(0);
					double time=c.getTime();
					
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
					if (!isRunning){
						System.out.println("returned");
						return;
					}
					
		    		listOfEvents.remove(0);
		    		
		            int action=c.getAction();	     	                       
		            Client client=c.getClient();	     	                       
		            
		            if (client!=null) {
		            	
		            	System.out.println(c.getTime()+"action "+
				         		  action+"abc"+client.id);
		            }
		            
		            	                 
		            switch (action){
		                case Simulation.ARRIVAL:	   
//		                     	    System.out.println("arrival");
		                	 client.startDrawingMe();
		             	     client.moveToWaitingRoom();
		                     break;
		                case Simulation.APPEAR_IN_POSITION:		
		                	client.startDrawingMe();
//		                     	   System.out.println("appear");
		                     client.moveToQueue();
							 break;
		                case Simulation.DEPARTURE:
//		                     	    System.out.println("exit "+client.abc);
		                     client.moveToExit();
		                     break;
		                case Simulation.PAUSE:
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
		
		t=new Thread(r);
		t.start();
		
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