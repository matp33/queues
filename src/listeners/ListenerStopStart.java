
package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import symulation.Manager;

public class ListenerStopStart implements ActionListener {

private Manager manager;

    private boolean isSimulationPaused = false;

    public ListenerStopStart (Manager manager){
        this.manager=manager;                
    }

    @Override
    public void actionPerformed (ActionEvent e){

        if (!isSimulationPaused){
            manager.pause();
            isSimulationPaused = true;

        }
        else{
            manager.resume(false);
            isSimulationPaused = false;
        }


    }

}
