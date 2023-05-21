package events;

import otherFunctions.TimeTable;

import java.util.HashSet;
import java.util.Set;

public class UIEventQueue {

    private Set<EventSubscriber> observers = new HashSet<>();

    public void addSubscriber(EventSubscriber o){
        observers.add(o);
    }

    public void publishNewTimetableEvent(TimeTable timeTable){
        observers.forEach(subscriber->subscriber.handleNewTimetable(timeTable));
    }

    public void publishRestartEvent(double time){
        observers.forEach(eventSubscriber -> eventSubscriber.handleRestart(time));
    }

}