package symulation;

import clienthandling.ExitQueueManager;
import core.MainLoop;
import events.ClientEventsHandler;
import events.ObjectsManager;
import otherFunctions.AppLogger;
import otherFunctions.ClientMovement;
import sprites.SpriteManager;

import java.io.IOException;


public class ApplicationConfiguration {

    private SpriteManager spriteManager = null;

    private Manager manager;

    private Painter painter;

    private AppLogger appLogger;

    private ObjectsManager objectsManager;

    private static ApplicationConfiguration applicationConfiguration;

    private int numberOfQueues;

    private ClientEventsHandler clientEventsHandler;

    private ExitQueueManager exitQueueManager;

    private ClientMovement clientMovement;

    private ApplicationConfiguration() {
    }

    public static ApplicationConfiguration getInstance () {
        if (applicationConfiguration == null){
            createInstance();
        }
        return applicationConfiguration;
    }

    private static void createInstance() {
        applicationConfiguration = new ApplicationConfiguration();
        applicationConfiguration.spriteManager = new SpriteManager();
        applicationConfiguration.painter = Painter.getInstance();
        applicationConfiguration.manager = new Manager();
        applicationConfiguration.appLogger = new AppLogger();
        applicationConfiguration.objectsManager = new ObjectsManager();
        applicationConfiguration.clientMovement = new ClientMovement();
        applicationConfiguration.exitQueueManager = new ExitQueueManager();
        applicationConfiguration.clientEventsHandler = new ClientEventsHandler();
    }

    public ClientMovement getClientMovement() {
        return clientMovement;
    }

    public ExitQueueManager getExitQueueManager() {
        return exitQueueManager;
    }

    public ObjectsManager getObjectsManager() {
        return objectsManager;
    }

    public ClientEventsHandler getClientEventsHandler (){
        return clientEventsHandler;
    }

    public void setNumberOfQueues(int numberOfQueues){
        this.numberOfQueues = numberOfQueues;
    }

    public int getNumberOfQueues() {
        return numberOfQueues;
    }


    public void initialize () throws IOException {
        spriteManager.loadSprites();
        painter.initiateWindow();
        objectsManager.initializeObjects();
        MainLoop.getInstance().addObject(clientEventsHandler);
    }

    public SpriteManager getSpriteManager() {
        return spriteManager;
    }

    public AppLogger getAppLogger() {
        return appLogger;
    }

    public Manager getManager() {
        return manager;
    }

    public Painter getPainter() {
        return painter;
    }
}
