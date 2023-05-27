package visualComponents;

import java.awt.*;

import javax.swing.JComponent;

import symulation.ApplicationConfiguration;
import symulation.Painter;

// ******* This class is used to show how many clients are not shown due to limits of size *********


public class Indicator extends JComponent {

private static final long serialVersionUID = 1L;
public int clientsOverLimit,x,y;

    public Indicator(){

	    clientsOverLimit=0;
	    Point position = ApplicationConfiguration.getInstance().getPainter().calculateWaitingRoomIndicatorPosition();
	    this.x=position.x;
	    this.y=position.y;

    }
    
    public void clear(){
    	if (clientsOverLimit!=0){
    		clientsOverLimit=0;
    	}
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLUE);
        
        if (clientsOverLimit>1){

            String p=Integer.toString(clientsOverLimit-1);
            p="+"+p; //example +3
            g2d.drawString(p, x, y);
            
        }

    }



}
