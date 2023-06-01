package symulation;

import core.MainLoop;
import events.ClientEventsHandler;
import events.ObjectsManager;
import spring2.Bean;
import sprites.SpriteManager;
import visualComponents.Indicator;

import java.io.IOException;

@Bean
public class ApplicationInitializer {


    private final ObjectsManager objectsManager;

    private final ClientEventsHandler clientEventsHandler;

    private final MainLoop mainLoop;

    private final Indicator waitingRoomIndicator;

    public ApplicationInitializer(MainLoop mainLoop, ObjectsManager objectsManager, ClientEventsHandler clientEventsHandler, Indicator waitingRoomIndicator) {
        this.objectsManager = objectsManager;
        this.clientEventsHandler = clientEventsHandler;
        this.mainLoop = mainLoop;
        this.waitingRoomIndicator = waitingRoomIndicator;
    }

    public void initialize () {
        objectsManager.initializeObjects();
        mainLoop.addObject(clientEventsHandler);
        waitingRoomIndicator.initialize();
    }

}
