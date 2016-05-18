package symulation;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.event.*;

public class WindowFrame extends JFrame {

    
	private static final long serialVersionUID = 1L;
	private Painter painter;       

    public void zamknij(){

//        System.out.println("*************"+painter.simulation.timerClass.threadsDepartures.length*painter.simulation.timerClass.threadsDepartures[0].size());

//        painter.simulation.timerClass.isRunning=false;
//        painter.stopWritingLogs();

    }
    
    public void setPainter(Painter painter){
        this.painter=painter;
    }

    public WindowFrame(){
    setTitle("Queue simulation");
    

    addWindowListener(new WindowAdapter(){
        @Override
        public void windowClosed (WindowEvent e){
            zamknij();
            super.windowClosed(e);
        }
    });

    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    
    
    }

}
