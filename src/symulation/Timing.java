package symulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import otherFunctions.ClientAction;
import visualComponents.Client;
import events.ClientsArrive;
import events.QueueUpdate;

public class Timing {
	private double time;
	public Timer timer;
	public List<Thread> threadsWaiting;
	private List<ClientAction<Double,Integer,Client>> listOfEvents;

	private List <Timer> eventTimers;
	public Object lock;
	public int threadsNumber;
	private boolean isRunning;
	private static final int TIMER_TIME_DELAY=10;
	private Manager manager;
	private Painter painter;

	public Timing( int numberOfQueues,Manager manager,Painter painter) {
//		simulation=s;
		this.painter=painter;
		this.manager=manager;
		threadsNumber=0;
		listOfEvents=new ArrayList <ClientAction<Double,Integer,Client>> ();

		eventTimers = new ArrayList <Timer>();
//		queues=new Queue [numberOfQueues];

		
		lock=new Object();		
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
	    while (!eventTimers.isEmpty()){
	    	eventTimers.get(0).cancel();
	    	eventTimers.remove(0);
	    }
	    
	}
	
	private void startTimer() {
		
		timer=new Timer();        
	    TimerTask task=new TimerTask(){
	        @Override
	        public void run(){
	        	
//	        	System.out.println(listOfEvents.size()+"/"+manager.isAnyClientThere());
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
		
		isRunning=true;
		double[] eventsTimes = new double [listOfEvents.size()];
		
		for (int i=0; i<listOfEvents.size();i++){  			
			eventsTimes[i]=listOfEvents.get(i).getTime();	
//			if (listOfEvents.get(i).getClient()!=null)
//			System.out.println("!"+listOfEvents.get(i).getAction()+"A"+listOfEvents.get(i).getTime()+"T"+
//					listOfEvents.get(i).getClient().abc);
		}
		
	            
	            for (int i=0; i<eventsTimes.length;i++){                 
	               	                
	                
	                TimerTask ti = new TimerTask (){
	                	@Override
	                	public void run(){
	                		
	     	                       int action=(Integer)listOfEvents.get(0).getAction();	     	                       
	     	                       Client client=(Client)listOfEvents.get(0).getClient();	     	                       
	     	                       listOfEvents.remove(0);
	     	                       System.out.println(listOfEvents.get(0).getTime()+"action "+
	     	                    		  listOfEvents.get(0).getAction()+"abc"+client.abc);
	     	                       // TODO error is here for sure
	     	                       	                       
	     	                       switch (action){
	     	                           case Simulation.ARRIVAL:	   
//	     	                        	    System.out.println("arrival");
	     	                        	    client.moveToWaitingRoom();
	     	                                break;
	     	                           case Simulation.APPEAR_IN_POSITION:		
//	     	                        	   System.out.println("appear");
	     	                                client.moveToQueue();
	     								    break;
	     	                           case Simulation.DEPARTURE:
	     	                        	    System.out.println("exit "+client.abc);
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
	     	                       
	     	                       eventTimers.remove(this);
	     	                       
//	     	                       System.out.println("delete 1; left: "+listOfEvents.size());
	     	
	     	                }
//	     	                }
	     	                
	                	
	                };
	                Timer t = new Timer();
	                
	                double delay=Math.max(eventsTimes[i]-getTime(),0+i*0.01);	                
	                t.schedule(ti, (int)(delay*1000));
	                eventTimers.add(t);
	                
	
	            }
	
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

	
	public void setEventsList(List<ClientAction<Double,Integer,Client>> listOfEvents){
		this.listOfEvents=listOfEvents;
		
	}


	
}