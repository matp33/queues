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

    private final SpriteManager spriteManager;


    private final Painter painter;

    private final ObjectsManager objectsManager;

    private final ClientEventsHandler clientEventsHandler;

    private final MainLoop mainLoop;

    private final Indicator waitingRoomIndicator;

    public ApplicationInitializer(MainLoop mainLoop, SpriteManager spriteManager, Painter painter, ObjectsManager objectsManager, ClientEventsHandler clientEventsHandler, Indicator waitingRoomIndicator) {
        this.spriteManager = spriteManager;
        this.painter = painter;
        this.objectsManager = objectsManager;
        this.clientEventsHandler = clientEventsHandler;
        this.mainLoop = mainLoop;
        this.waitingRoomIndicator = waitingRoomIndicator;
    }

    public void initialize () throws IOException {
        spriteManager.loadSprites();
        painter.initiateWindow();
        objectsManager.initializeObjects();
        mainLoop.addObject(clientEventsHandler);
        waitingRoomIndicator.initialize();
    }

}
