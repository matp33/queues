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

    private List<AnimatedObject> objectsToPaint = new ArrayList<>();

    private static MainLoop instance = null;

    private boolean isPaused;

    private long timePassedMilliseconds = 0;

    public static MainLoop getInstance() throws Exception {
        if (instance == null){
            Painter painter = Painter.getInstance();
            instance = new MainLoop(painter);
        }
        return instance;
    }

    private MainLoop(Painter painter) throws Exception {
        timer = new Timer();
        this.painter = painter;
        timer.scheduleAtFixedRate(mainLoopTask(),0, DELTA_TIME);
    }

    public void addObject (AnimatedObject object){
        objectsToPaint.add(object);
        painter.addObject(object);
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
                    objectsToPaint.forEach(AnimatedObject::update);
                    painter.repaint();
                    timePassedMilliseconds +=  DELTA_TIME;
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
