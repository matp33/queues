package interfaces;

import java.awt.*;

public interface Observable {

	public void addObserver (Observer client);
	public void removeObserver (Observer client);
	public void notifyClients () throws Exception;
	public Point getPosition ();
	
}
