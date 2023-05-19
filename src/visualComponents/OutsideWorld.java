package visualComponents;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import interfaces.Observable;
import interfaces.Observer;

public class OutsideWorld implements Observable {

	private Point position;
	private List <Observer> observers;
	
	public OutsideWorld (){
		position=new Point(0,0);
		observers = new ArrayList <Observer>();
	}
	
	@Override
	public void addObserver(Observer client) {
		observers.add(client);
		
	}

	@Override
	public void removeObserver(Observer client) {
		observers.remove(client);
		
	}

	@Override
	public void notifyClients() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Point getPosition() {
		return position;
	}
	
	public int getObserversSize(){
		return observers.size();
	}

}
