package events;

import spring2.Bean;
import symulation.ClientArrivalEvent;

import javax.swing.*;
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

    public void publishResumeEvent (){
        observers.forEach(EventSubscriber::handleResume);
    }

    public void publishPauseEvent (){
        observers.forEach(EventSubscriber::handlePause);
    }

    public void publishNewMessageEvent (String message){
        observers.forEach(eventSubscriber -> eventSubscriber.handleNewMessage(message));

    }

    public void publishNewReinitializeEvent (){
        observers.forEach(EventSubscriber::handleReinitializeEvent);

    }

    public int publishNewDialogEvent (JPanel panel, String title){
        return observers.iterator().next().handleNewDialog(panel, title);
    }



}
