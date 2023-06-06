
package listeners;

import constants.UIEventType;
import events.UIEventHandler;
import events.UIEvent;
import events.UIEventQueue;
import spring2.Bean;
import symulation.Manager;

@Bean
public class PauseButtonClickHandler implements UIEventHandler {

    private boolean isSimulationPaused = false;

    private Manager manager;

    private UIEventQueue uiEventQueue;

    public PauseButtonClickHandler(Manager manager, UIEventQueue uiEventQueue) {
        this.manager = manager;
        this.uiEventQueue = uiEventQueue;
        this.uiEventQueue.subscribeToEvents(this, UIEventType.PAUSE_BUTTON_CLICK);

    }

    @Override
    public void handleEvent(UIEvent<?> uiEvent) {
        if (!isSimulationPaused){
            manager.pause();
            isSimulationPaused = true;

        }
        else{
            manager.resumeSimulation();
            isSimulationPaused = false;
        }
    }
}
