package events;

import symulation.SimulationEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;

public class UIEventQueue {

    private Set<EventSubscriber> observers = new HashSet<>();

    public void addSubscriber(EventSubscriber o){
        observers.add(o);
    }

    public void publishNewTimetableEvent(SortedSet<SimulationEvent> timeTable){
        observers.forEach(subscriber->subscriber.handleNewTimetable(timeTable));
    }

    public void publishRestartEvent(double time){
        observers.forEach(eventSubscriber -> eventSubscriber.handleRestart(time));
    }

}
