package visualComponents;

import interfaces.AnimatedAndObservable;
import interfaces.Observer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StoreCheckout extends AnimatedAndObservable{

	private static final long serialVersionUID = 1L;
	private int clientsAboveLimit;
    private int checkoutIndex;
    private int x,y;
    private final int maxClients;
     
	private List<Client> clientsArriving;
	private List<Client> clientsExiting;
	private List <Client> clientsInQueue;
        
        public StoreCheckout(int checkoutIndex){
            
        	super();
        	maxClients=painter.maxClientsVisibleInQueue;
            clientsAboveLimit=0;
            setClientsList(new ArrayList <Client>());
            this.checkoutIndex =checkoutIndex;

            Point position=painter.calculateQueueIndicatorPosition(checkoutIndex);
//            Dimension clientDimensions=Client.clientDimensions;
            x=position.x;
            y=position.y;
           
            clientsArriving=new ArrayList<Client>();
            clientsExiting=new ArrayList<Client>();
            this.position =painter.getCheckoutPosition(checkoutIndex);
            startDrawingMe();

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
        
        public void decreaseClientsAboveLimit(){
        	clientsAboveLimit--;
        }



        
        public boolean isLast(Client client) {
        	if (getClientsList().isEmpty() ){
        		return true;
        	}
        	else
				return getClientsList().get(getClientsList().size()-1)==client;
		}

	public boolean isFirst(Client client) {
		return getClientsList().indexOf(client) == 0;
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
			if (getClientsArriving().get(i).getQueueNumber()== getCheckoutIndex()){
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

	public int getCheckoutIndex() {
		return checkoutIndex;
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
		public void interrupt(double timePassedSeconds) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void scheduleMoving() {
			// TODO Auto-generated method stub
			
		}


	@Override
	public void update(double currentTimeMilliseconds) {

	}
}
