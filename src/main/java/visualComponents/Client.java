package visualComponents;

import constants.ClientPositionType;
import interfaces.AnimatedObject;

import java.awt.*;

import animations.Animation;

import java.util.*;
import java.util.List;


public class Client extends AnimatedObject {

private double arrivalTime;
private int clientNumber;
private ClientPositionType positionType;
private Point lookAtPoint;

private List <Point> trajectory = new ArrayList<>();
private int queueNumber;

private Animation currentAnimation,moveLeft,moveRight,moveDown,moveUp;
private Point position;
private static final long serialVersionUID = 1L;

private final int id;
private double timeInCheckout;

public Client(int clientId, int queueNumber, int clientNumber, double arrivalTime, double timeInCheckout)  {

		super();
		this.id = clientId;
		this.queueNumber = queueNumber;
		this.timeInCheckout = timeInCheckout;
    	this.arrivalTime =arrivalTime;

		moveDown=animations[0];
		moveUp=animations[3];
		moveLeft= animations[1];
		moveRight= animations[2];

        this.clientNumber=clientNumber;
        currentAnimation=moveUp;
        currentAnimation.start();    

    }

	public double getArrivalTime() {
		return arrivalTime;
	}

	@Override
	public int getWidth (){
		return spriteWidth;
	}

	public int getId() {
		return id;
	}

	public double getTimeInCheckout() {
		return timeInCheckout;
	}

    public void decreaseClientIndex()  {
		clientNumber--;
    }

    @Override
    public void interrupt()  {
		currentAnimation.setLastFrame();
    }

	public void saveInformation(Point point, ClientPositionType type) {
		position=point;
		setPositionType(type);		
	}

	public Point getPosition(){
		return position;
	}
	public int getQueueNumber() {
		return queueNumber;
	}
	public ClientPositionType getPositionType() {
		return positionType;
	}
	
	public void setPositionType(ClientPositionType positionType) {
		this.positionType = positionType;
	}


	public int getClientNumber() {
		return clientNumber;
	}

	public void setClientNumber(int clientNumber) {
		this.clientNumber = clientNumber;
	}
		
	private void chooseDirection (Point destination){
		
		int horizontalDistanceAbs=Math.abs(position.x-destination.x);
		int verticalDistanceAbs=Math.abs(position.y-destination.y);
		
		if (horizontalDistanceAbs<verticalDistanceAbs){
			if (position.y<destination.y){
	            currentAnimation=moveDown;
	        }
	        if (position.y>destination.y){
	            currentAnimation=moveUp;
	        }
		}
		else{
	        if (position.x<destination.x){
	            currentAnimation=moveRight;
	        }
	        if (position.x>destination.x){
	            currentAnimation=moveLeft;
	        }
		}

	}

	@Override
	public void update(double timePassed) {

		if (!trajectory.isEmpty()){
			Point point=trajectory.get(0);
			trajectory.remove(0);
			chooseDirection(point);
			currentAnimation.start();
			currentAnimation.updateFrame();
			position=new Point(point.x, point.y);
		}

	}


	@Override
    public void paintComponent(Graphics g) {
     
    	int x = position.x;
    	int y = position.y;
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(currentAnimation.getSprite(),x,y,null);
        g2d.drawString("" + id, x+getSize().width, y+getSize().height);
              
    }

	@Override
	public String toString (){
		return "" + id + " pos: " +positionType;
	}

	public void setTrajectory(List<Point> trajectory) {
		this.trajectory = trajectory;
	}

	public void setLookAtPoint(Point lookAtPoint) {
		this.lookAtPoint = lookAtPoint;
	}

	public boolean stopped() {
		return trajectory.isEmpty();
	}
}

