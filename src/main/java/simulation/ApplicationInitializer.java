package simulation;

import core.MainLoop;
import clienthandling.ClientEventsHandler;
import core.ObjectsManager;
import spring2.Bean;
import view.NavigationPanel;
import visualComponents.Indicator;

@Bean
public class ApplicationInitializer {


    private final ObjectsManager objectsManager;

    private final MainLoop mainLoop;

    private final Indicator waitingRoomIndicator;

    private final AppLayoutManager appLayoutManager;

    private final ApplicationConfiguration applicationConfiguration;

    private final NavigationPanel navigationPanel;

    public ApplicationInitializer(MainLoop mainLoop, ObjectsManager objectsManager, ClientEventsHandler clientEventsHandler, Indicator waitingRoomIndicator, AppLayoutManager appLayoutManager, ApplicationConfiguration applicationConfiguration, NavigationPanel navigationPanel) {
        this.objectsManager = objectsManager;
        this.mainLoop = mainLoop;
        this.waitingRoomIndicator = waitingRoomIndicator;
        this.appLayoutManager = appLayoutManager;
        this.applicationConfiguration = applicationConfiguration;
        this.navigationPanel = navigationPanel;
    }

    public void initialize () {
        appLayoutManager.initialize(applicationConfiguration.getNumberOfQueues(), navigationPanel.getPanel());
        appLayoutManager.calculateWindowSize(applicationConfiguration.getNumberOfQueues());
        objectsManager.initializeObjects();
        objectsManager.getAnimatedObjects().forEach(mainLoop::addObject);
        waitingRoomIndicator.initialize();
    }

}
