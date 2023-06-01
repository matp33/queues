package events;

import symulation.ClientArrivalEvent;

import javax.swing.*;
import java.util.SortedSet;

public interface EventSubscriber {
    void handleNewTimetable(SortedSet<ClientArrivalEvent> clientArrivalEvents);

    void handleRestart (double time);

    void handleResume();

    boolean handlePause();

    void handleNewMessage(String message);

    void handleReinitializeEvent();

    int handleNewDialog(JPanel panel, String title);


}
