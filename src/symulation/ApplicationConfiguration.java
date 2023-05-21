package symulation;

import sprites.SpriteManager;

import java.io.IOException;


public class ApplicationConfiguration {

    private SpriteManager spriteManager = null;

    private Manager manager;

    private Painter painter;

    private static ApplicationConfiguration applicationConfiguration;

    private int numberOfQueues;

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
        manager.initializeStaticObjects();
    }

    public SpriteManager getSpriteManager() {
        return spriteManager;
    }

    public Manager getManager() {
        return manager;
    }

    public Painter getPainter() {
        return painter;
    }
}