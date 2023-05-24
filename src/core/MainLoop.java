package core;

import interfaces.AnimatedObject;
import symulation.Painter;
import visualComponents.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainLoop {

    public static final int DELTA_TIME = 20;
    private Timer timer;
    private Painter painter;

    private Client client;

    private List<ChangeableObject> changeableObjects = new ArrayList<>();

    private static MainLoop instance = null;

    private boolean isPaused = true;

    private long timePassedMilliseconds = 0;

    public static MainLoop getInstance() {
        if (instance == null){
            Painter painter = Painter.getInstance();
            instance = new MainLoop(painter);
        }
        return instance;
    }

    private MainLoop(Painter painter) {
        timer = new Timer();
        this.painter = painter;
        timer.scheduleAtFixedRate(mainLoopTask(),0, DELTA_TIME);
    }

    public void addObject (ChangeableObject object){
        changeableObjects.add(object);
        if (object instanceof AnimatedObject){
            painter.addObject((AnimatedObject) object);
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
                    timePassedMilliseconds +=  DELTA_TIME;
                    changeableObjects.forEach(changeableObject -> changeableObject.update(timePassedMilliseconds));
                    painter.repaint();
                    painter.updateTime((double)timePassedMilliseconds/1000);
                }

            }
        };
    }

    public boolean isPaused() {
        return isPaused;
    }

    public long getTimePassedMilliseconds() {
        return timePassedMilliseconds;
    }

    public void setTimePassed(long timePassedMilliseconds) {
        this.timePassedMilliseconds = timePassedMilliseconds;
    }
}
