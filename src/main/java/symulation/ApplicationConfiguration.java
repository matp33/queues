package symulation;

import clienthandling.ExitQueueManager;
import core.MainLoop;
import events.ClientEventsHandler;
import events.ObjectsManager;
import otherFunctions.AppLogger;
import otherFunctions.ClientMovement;
import spring2.Bean;
import sprites.SpriteManager;

import java.io.IOException;

@Bean
public class ApplicationConfiguration {


    private int numberOfQueues;

    public void setNumberOfQueues(int numberOfQueues){
        this.numberOfQueues = numberOfQueues;
    }

    public int getNumberOfQueues() {
        return numberOfQueues;
    }

}
