
package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import symulation.Manager;
import symulation.Painter;

public class ListenerStopStart implements ActionListener {

private Manager manager;
private JButton btnStop;

    public ListenerStopStart (Manager manager){
        this.manager=manager;                
    }

    @Override
    public void actionPerformed (ActionEvent e){

         if (manager.isRunning()  ){             
             manager.pause();
             return;
         }

         if (!manager.isRunning()  ){           
             manager.resume(false);
             return;
         }
        
    }

}
