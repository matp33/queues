package events;

import symulation.ClientArrivalEvent;

import java.util.SortedSet;

public interface EventSubscriber {
    void handleNewTimetable(SortedSet<ClientArrivalEvent> clientArrivalEvents);

    void handleRestart (double time);
}
