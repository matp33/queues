package visualComponents;

import interfaces.AnimatedAndObservable;
import interfaces.Observer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import sprites.SpriteType;
import symulation.Painter;

public class StoreCheckout extends AnimatedAndObservable{

	private static final long serialVersionUID = 1L;
	private int clientsAboveLimit;
    private int queueNumber;
    private int x,y;
    private final int maxClients;
     
	private List<Client> clientsArriving;
	private List<Client> clientsExiting;
	private List <Client> clientsInQueue;
        
        public StoreCheckout(Painter painter, int queueNumber){
            
        	super(SpriteType.STORE_CHECKOUT, painter);
        	maxClients=painter.maxClientsVisibleInQueue;
            clientsAboveLimit=0;
            setClientsList(new ArrayList <Client>());
            this.queueNumber=queueNumber;

            Point position=painter.calculateQueueIndicatorPosition(queueNumber);
//            Dimension clientDimensions=Client.clientDimensions;
            x=position.x;
            y=position.y;
           
            clientsArriving=new ArrayList<Client>();
            clientsExiting=new ArrayList<Client>();
            this.position =painter.getCheckoutPosition(queueNumber);
            startDrawingMe();

        }

        public Client addClient (Client c){

        	
            Client client=findNumberOfLastClient();
                        
            if (client!=null){
//            	System.out.println("client "+c.abc+" -> "+client.abc);
            	c.setClientNumber(client.getClientNumber()+1);
                c.setObjectObserved(client);
            }
            else{
            	c.setObjectObserved(this);
            }
                       	
        	getClientsArriving().add(c);        
                
            return c;
        }
        
        public List<Client> getClientsArriving() {			
			return clientsArriving;
		}
        
        public void setClientsArriving(List <Client> clientsArriving) {			
			this.clientsArriving=clientsArriving;
		}
        

		public void increaseNumber(){
			clientsAboveLimit++;
        }
        
        public void decreaseNumber(){
        	clientsAboveLimit--;
        }



        
        public boolean isLast(Client client) {
        	if (getClientsList().isEmpty() ){
        		return true;
        	}
        	else
			return getClientsList().get(getClientsList().size()-1)==client;
		}

        public Client findNumberOfLastClient(){
            
           if (!getClientsArriving().isEmpty()){
            	int i=findClientMovingToHere();
            	if (i!=-1){
            		
//            		System.out.println("someone is going"+clientsArriving.size());
            		return getClientsArriving().get(i);
            	}
            	            	
            }
           
           if (getClientsList().size()>0){
//        	   System.out.println("(( "+clientsInQueue);
               return getClientsList().get(getClientsList().size()-1);
           }
           else
            	return null;
		           	
        }
        
        private int findClientMovingToHere(){
        	for (int i=getClientsArriving().size()-1; i>=0;i--){
        		if (getClientsArriving().get(i).getQueueNumber()==getQueueNumber()){
        			return i;
        		}
        	}
        	return -1; // n
        }
        
        public int lastClientInQueue(){
        	int size=getClientsArriving().size();
        	if (size>0){
        		return getClientsArriving().get(size-1).getClientNumber();
        	}
        	else{
        		return 0;
        	}
        	
        }
        
        public boolean isClientOutOfSight(Client c){
        	return c.getClientNumber()+1>maxClients;//clients number goes from 0, so we add +1
        }
        
        public boolean isClientLastVisible(Client c){
        	return c.getClientNumber()+1==maxClients;  
        }
        
        // when a client leaves queue
       
        


        @Override
        public void paintComponent(Graphics g){
        	
        	super.paintComponent(g);	
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.BLUE);

            if (!getClientsList().isEmpty()){
//            	System.out.println("@"+getClientsList().size());
//            	g2d.drawString(""+getClientsList().size(), x , y );
                    if (clientsAboveLimit>0){
                        g2d.drawString("+"+clientsAboveLimit, x , y );
//                         System.out.println("ponad stan "+x+"x"+y+"y");
                    }
            }
            
            
        }

        public boolean isThereClientsInQueue(){
        	return !getClientsList().isEmpty();
        }

		public List<Client> getClientsExiting() {
			return clientsExiting;
		}

		public void setClientsExiting(List<Client> clientsExiting) {
			this.clientsExiting = clientsExiting;
		}

		public List <Client> getClientsList() {
			return clientsInQueue;
		}

		public void setClientsList(List <Client> clientsList) {
			this.clientsInQueue = clientsList;
		}

		public int getQueueNumber() {
			return queueNumber;
		}

		@Override
		public void addObserver(Observer client) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void removeObserver(Observer client) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void notifyClients() {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void initializePosition() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void interrupt() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void resume() {
			// TODO Auto-generated method stub
			
		}
		

		


}
