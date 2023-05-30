
package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import events.UIEventQueue;
import spring2.Bean;
import spring2.BeanRegistry;
import symulation.Manager;
import symulation.Painter;

@Bean
public class ListenerStopStart implements ActionListener {

    private boolean isSimulationPaused = false;

    private UIEventQueue uiEventQueue;

    public ListenerStopStart(UIEventQueue uiEventQueue) {
        this.uiEventQueue = uiEventQueue;
    }

    @Override
    public void actionPerformed (ActionEvent e){
        if (!isSimulationPaused){
            uiEventQueue.publishPauseEvent();
            isSimulationPaused = true;

        }
        else{
            uiEventQueue.publishResumeEvent();
            isSimulationPaused = false;
        }


    }

}
