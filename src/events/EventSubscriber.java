package events;

import otherFunctions.TimeTable;

public interface EventSubscriber {
    void handleNewTimetable(TimeTable event);

    void handleRestart (double time);
}
