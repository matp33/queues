package otherFunctions;

import java.util.TimerTask;

import symulation.Manager;
import symulation.Simulation;
import visualComponents.Client;

public class ExtendedTimerTask extends TimerTask{

private ClientAction <Double, Integer, Client> action;
private Manager manager;
private boolean isFinished;
public static int id;
public int i;
	
	public ExtendedTimerTask (ClientAction<Double,Integer,Client> action, Manager manager){
		this.action=action;		
		this.manager=manager;
		isFinished=false;
		id++;
		i=id;
	}
	
	@Override
	public void run() {
		System.out.println("id "+i);
		int action=this.action.getAction();	     	                       
        Client client=this.action.getClient();
        
        if (client!=null) 
             System.out.println(this.action.getTime()+"action "+
          		  action+"abc"+client.id);
             	                 
             // TODO heres a problem; sometimes it takes 2 times same client which makes
             // 1 client stay by queue
             // TODO 2 why it doesnt take clients in order of id, instead its some random
             switch (action){
                 case Simulation.ARRIVAL:	   
//              	    System.out.println("arrival");
              	    client.moveToWaitingRoom();
                      break;
                 case Simulation.APPEAR_IN_POSITION:		
//              	   System.out.println("appear");
                      client.moveToQueue();
					    break;
                 case Simulation.DEPARTURE:
//              	    System.out.println("exit "+client.abc);
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
             isFinished=true;
		
	}

}
