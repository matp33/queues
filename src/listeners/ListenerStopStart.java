
package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import symulation.Manager;

public class ListenerStopStart implements ActionListener {

private Manager manager;

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
