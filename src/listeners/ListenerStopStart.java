
package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import symulation.Manager;
import symulation.Painter;

public class ListenerStopStart implements ActionListener {

private Painter painter;

    private boolean isSimulationPaused = false;

    public ListenerStopStart (Painter painter){
        this.painter=painter;
    }

    @Override
    public void actionPerformed (ActionEvent e){

        if (!isSimulationPaused){
            painter.pause();
            isSimulationPaused = true;

        }
        else{
            painter.resume(false);
            isSimulationPaused = false;
        }


    }

}
