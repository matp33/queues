package symulation;

import core.MainLoop;
import clienthandling.ClientEventsHandler;
import otherFunctions.ObjectsManager;
import spring2.Bean;
import view.NavigationPanel;
import visualComponents.Indicator;

@Bean
public class ApplicationInitializer {


    private final ObjectsManager objectsManager;

    private final ClientEventsHandler clientEventsHandler;

    private final MainLoop mainLoop;

    private final Indicator waitingRoomIndicator;

    private final CustomLayout customLayout;

    private final ApplicationConfiguration applicationConfiguration;

    private final NavigationPanel navigationPanel;

    public ApplicationInitializer(MainLoop mainLoop, ObjectsManager objectsManager, ClientEventsHandler clientEventsHandler, Indicator waitingRoomIndicator, CustomLayout customLayout, ApplicationConfiguration applicationConfiguration, NavigationPanel navigationPanel) {
        this.objectsManager = objectsManager;
        this.clientEventsHandler = clientEventsHandler;
        this.mainLoop = mainLoop;
        this.waitingRoomIndicator = waitingRoomIndicator;
        this.customLayout = customLayout;
        this.applicationConfiguration = applicationConfiguration;
        this.navigationPanel = navigationPanel;
    }

    public void initialize () {
        customLayout.initialize(applicationConfiguration.getNumberOfQueues(), navigationPanel.getPanel());
        customLayout.calculateWindowSize(applicationConfiguration.getNumberOfQueues());
        objectsManager.initializeObjects();
        mainLoop.addObject(clientEventsHandler);
        waitingRoomIndicator.initialize();
    }

}
