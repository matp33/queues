
package uieventshandlers;

import constants.UIEventType;
import events.UIEventHandler;
import events.UIEvent;
import events.UIEventQueue;
import spring2.Bean;
import simulation.SimulationController;

@Bean
public class PauseButtonClickHandler implements UIEventHandler {

    private boolean isSimulationPaused = false;

    private SimulationController simulationController;

    private UIEventQueue uiEventQueue;

    public PauseButtonClickHandler(SimulationController simulationController, UIEventQueue uiEventQueue) {
        this.simulationController = simulationController;
        this.uiEventQueue = uiEventQueue;
        this.uiEventQueue.subscribeToEvents(this, UIEventType.PAUSE_BUTTON_CLICK);

    }

    @Override
    public void handleEvent(UIEvent<?> uiEvent) {
        if (!isSimulationPaused){
            simulationController.pauseSimulation();
            isSimulationPaused = true;

        }
        else{
            simulationController.startSimulation();
            isSimulationPaused = false;
        }
    }
}
