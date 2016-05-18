package events;


import java.util.Timer;
import java.util.TimerTask;

import symulation.Painter;
import visualComponents.Client;



public class ClientsArrive implements Runnable  {
//
    private Painter painter;
    private Client client;
    private boolean isContinued;
    public static final double timeDelay=0.5;
//   
//
//    public ClientsArrive (int queueNumber,Painter painter, Client client)  {
//        
//        isContinued=false;
//        this.painter=painter;
//        this.client=client;
//        painter.simulation.timerClass.threadsArrivals.add(this);       
//
//    }
//
    @Override
    public void run()  {
//        if (isContinued==false){
//            goAndStop();
//        }
//        else{
//        	// TODO przy kontynuowaniu uwzglednic czas czekania w poczekalni
//        	moveToQueue();
//        }
//        
    }
//
//
//    public void continueMoving(){
//        isContinued=true;
//    }
//
//    private void goAndStop() {
//
//       
////        System.out.println("client "+client.clientNumber+" @#!time "+painter.getTime());
////       client.enterSimulation(client.getPositionType());
//    	client.startMoving();
//        client.moveToWaitingRoom();
//        painter.waitingRoomIndicator.clientsOverLimit++;
//       
//        synchronized(client.lockWaitingRoom){ 
//            while (client.getPositionType()!=Client.POSITION_WAITING_ROOM || client.isMoving()==true){
//                try{
//                    client.lockWaitingRoom.wait();
//                }
//                catch (InterruptedException ex){
//                    ex.printStackTrace(); // TODO consider replacing with "throws interrupted exception"
//                }
//
//            }
//        }
//
////        System.out.println("done "+client.getClientNumber()+painter.getTime());
//	   client.getQueue().addClient(client);
////	   System.out.println("client "+client.clientNumber+" @#!time "+painter.getTime());
//       
//       Timer timer=new Timer();
//       TimerTask task=new TimerTask(){
//           @Override
//           public void run(){
//               painter.waitingRoomIndicator.clientsOverLimit--;               
//               moveToQueue();
//               
//           }
//       };
//       
//       timer.schedule(task, (int)(1000*timeDelay));
//    }
//    
//
//	public void moveToQueue(){
//
//    
//        if ( painter.simulation.timerClass.isRunning==false){
//        	return;
//        }
//
//        
//    client.moveToQueue();  
//    painter.simulation.timerClass.threadsArrivals.remove(this);
//    
//
//    }
//
//
//    public void putClient (Client client){
//    	
//    		
//    	client.getQueue().addClient(client); 
////        System.out.println("waitin in "+painter.getTime()+"#"+client.getClientNumber());
//        
//        client.startMoving();
//        if (client.getPositionType()==Client.POSITION_GOING_TO_QUEUE){
//        	client.moveToQueue();
//        }
//        if (client.getPositionType()==Client.POSITION_WAITING_IN_QUEUE){
//   			client.saveMeInLog();
//   		}
////        else{
//        	painter.simulation.timerClass.threadsArrivals.remove(this);
////        }
//        
//        
//    	
//        
//    }
//
//
}
