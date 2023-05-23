package events;

import otherFunctions.TimeTable;
import symulation.SimulationEvent;

import java.util.SortedSet;

public interface EventSubscriber {
    void handleNewTimetable(SortedSet<SimulationEvent> simulationEvents);

    void handleRestart (double time);
}
