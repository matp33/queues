package interfaces;

import java.awt.Dimension;

public interface Observable {

	public void addObserver (Observer client);
	public void removeObserver (Observer client);
	public void notifyClients ();
	public Dimension getPosition ();
	
}
