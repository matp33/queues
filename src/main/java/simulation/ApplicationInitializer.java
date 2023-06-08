package simulation;

import core.MainLoop;
import core.ObjectsManager;
import spring2.Bean;
import view.NavigationPanel;
import view.SimulationPanel;
import visualComponents.Indicator;

@Bean
public class ApplicationInitializer {


    private final ObjectsManager objectsManager;

    private final MainLoop mainLoop;

    private final Indicator waitingRoomIndicator;

    private final AppLayoutManager appLayoutManager;

    private final ApplicationConfiguration applicationConfiguration;

    private final NavigationPanel navigationPanel;

    private  final SimulationPanel simulationPanel;

    public ApplicationInitializer(MainLoop mainLoop, ObjectsManager objectsManager, Indicator waitingRoomIndicator, AppLayoutManager appLayoutManager, ApplicationConfiguration applicationConfiguration, NavigationPanel navigationPanel, SimulationPanel simulationPanel) {
        this.objectsManager = objectsManager;
        this.mainLoop = mainLoop;
        this.waitingRoomIndicator = waitingRoomIndicator;
        this.appLayoutManager = appLayoutManager;
        this.applicationConfiguration = applicationConfiguration;
        this.navigationPanel = navigationPanel;
        this.simulationPanel = simulationPanel;
    }

    public void initialize () {
        appLayoutManager.initialize(applicationConfiguration.getNumberOfQueues(), navigationPanel.getPanel());
        simulationPanel.initialize();
        objectsManager.initializeObjects();
        objectsManager.getAnimatedObjects().forEach(mainLoop::addObject);
        waitingRoomIndicator.initialize();
    }

}
