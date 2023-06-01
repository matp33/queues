package visualComponents;

import interfaces.AnimatedAndObservable;
import interfaces.Observer;

import java.awt.*;

public class StoreCheckout extends AnimatedAndObservable{

	private static final long serialVersionUID = 1L;
	private int clientsAboveLimit;
    private int checkoutIndex;

	private Point queueIndicatorPosition;

	public StoreCheckout(int checkoutIndex){

		super();
		clientsAboveLimit=0;
		this.checkoutIndex =checkoutIndex;

	}

	public void initializePosition (Point queueIndicatorPosition, Point checkoutPosition){
		this.queueIndicatorPosition = queueIndicatorPosition;
		this.position = checkoutPosition;
	}

	public void decreaseClientsAboveLimit(){
		clientsAboveLimit--;
	}


	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.BLUE);

		if (clientsAboveLimit>0){
			g2d.drawString("+"+clientsAboveLimit, queueIndicatorPosition.x , queueIndicatorPosition.y );
		}
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
	public void interrupt() {
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
