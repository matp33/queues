package core;

import constants.UIEventType;
import events.UIEvent;
import events.UIEventQueue;
import interfaces.AnimatedObject;
import spring2.Bean;
import view.SimulationPanel;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Bean
public class MainLoop {

    public static final double DELTA_TIME = 0.02;
    private Timer timer;

    private List<ChangeableObject> changeableObjects = new ArrayList<>();

    private boolean isPaused = true;

    private double timePassedSeconds = 0;

    private SimulationPanel simulationPanel;


    private UIEventQueue uiEventQueue;


    public MainLoop(SimulationPanel simulationPanel,  UIEventQueue uiEventQueue) {
        this.simulationPanel = simulationPanel;
        this.uiEventQueue = uiEventQueue;
        timer = new Timer();
        timer.scheduleAtFixedRate(mainLoopTask(),0, (int)(DELTA_TIME*1000));
    }

    public void addObject (ChangeableObject object){
        changeableObjects.add(object);
        if (object instanceof AnimatedObject){
            simulationPanel.addObject((AnimatedObject) object);
        }
    }

    public void pause (){
        isPaused = true;
    }

    public void resume (){
        isPaused = false;
    }

    private TimerTask mainLoopTask (){
        return new TimerTask() {
            @Override
            public void run() {
                if (!isPaused){
                    timePassedSeconds +=  DELTA_TIME;
                    changeableObjects.forEach(changeableObject -> changeableObject.update(timePassedSeconds));
                    simulationPanel.repaint();
                    uiEventQueue.publishNewEvent(new UIEvent<>(UIEventType.TIME_VALUE_CHANGE, timePassedSeconds));
                }

            }
        };
    }

    public boolean isPaused() {
        return isPaused;
    }

    public double getTimePassedSeconds() {
        return timePassedSeconds;
    }

    public void setTimePassed(double timePassedSeconds) {
        this.timePassedSeconds = timePassedSeconds;
    }

    public void removeObjects() {
        changeableObjects.clear();
    }
}
