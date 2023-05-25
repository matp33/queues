package core;

import interfaces.AnimatedObject;
import symulation.Painter;
import visualComponents.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainLoop {

    public static final double DELTA_TIME = 0.02;
    private Timer timer;
    private Painter painter;

    private List<ChangeableObject> changeableObjects = new ArrayList<>();

    private static MainLoop instance = null;

    private boolean isPaused = true;

    private double timePassedSeconds = 0;

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
        timer.scheduleAtFixedRate(mainLoopTask(),0, (int)(DELTA_TIME*1000));
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
                    timePassedSeconds +=  DELTA_TIME;
                    changeableObjects.forEach(changeableObject -> changeableObject.update(timePassedSeconds));
                    painter.repaint();
                    painter.updateTime( timePassedSeconds);
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
