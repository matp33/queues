package events;


import symulation.Painter;
import visualComponents.Client;
import visualComponents.Queue;

public class QueueUpdate implements Runnable {

//    private Queue queue;
//    private Painter painter;    
//    private int queueNumber;  
//    public boolean isOutsideSymulation;
//    private boolean isContinued;
//    public int lastClientToMove;
//    double st;
//    double stop;
//
//    public QueueUpdate (int queueNumber,Queue queue, Painter painter) {
//        lastClientToMove=Integer.MAX_VALUE;
//        isContinued=false;
//        isOutsideSymulation=false;
//        this.queueNumber=queueNumber;
//        this.queue=queue;
//        this.painter=painter;
//        st=painter.getTime();
////        dodatkowiKlienci=new ArrayList <Klient>();
//    }
//
    @Override
    public void run() {
//
//    
//    if ( isContinued==false || isOutsideSymulation==true){
//
//        painter.simulation.timerClass.threadsDepartures[queueNumber].add(this);
//        int clientIndex=0;
//        for (int u=0; u<queue.getClientsList().size();u++){
//            if (queue.getClientsList().get(u).getPositionType()!=Client.POSITION_EXITING){
//                clientIndex=u;
//                break;
//            }
//        }
//
//        Client client=queue.getClientsList().get(clientIndex);        
//        client.setClientNumber(painter.getNumberOfClientsAtDoor());
//        client.moveClientToExit();
//        queue.getClientsList().remove(client);
////    	System.out.println("@"+);
////    	System.out.println("@after "+queue.getClientsList().contains(this));
//		queue.getClientsExiting().add(client);
////		queue.notifyClients(client);
////		queue.removeObserver(client);
//		painter.door.addObserver(client);
//		
//		if (!queue.isThereClientsInQueue()){
////			System.out.println("no one in queue");
//			queue.decreaseNumber();
//		}
//
    }
//
//    painter.simulation.removeThread(queueNumber, this);
//    stop=painter.getTime();
////System.out.println("koniec");
//    
//   
//        
//    }
// 
//
//    public void continueUpdate(){
//        isContinued=true;       
//    }

}



