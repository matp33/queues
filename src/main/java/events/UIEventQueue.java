package events;

import spring2.Bean;
import symulation.ClientArrivalEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;

@Bean
public class UIEventQueue {

    private Set<EventSubscriber> observers = new HashSet<>();

    public void addSubscriber(EventSubscriber o){
        observers.add(o);
    }

    public void publishNewTimetableEvent(SortedSet<ClientArrivalEvent> timeTable){
        observers.forEach(subscriber->subscriber.handleNewTimetable(timeTable));
    }

    public void publishRestartEvent(double time){
        observers.forEach(eventSubscriber -> eventSubscriber.handleRestart(time));
    }

}
