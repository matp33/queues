package symulation;

import constants.SimulationEventType;
import constants.TypeOfTimeEvent;

public class SimulationEvent {

    private TypeOfTimeEvent simulationEventType;

    private double eventTime;

    private int queueNumber;

    public SimulationEvent(TypeOfTimeEvent simulationEventType, double eventTime, int queueNumber) {
        this.simulationEventType = simulationEventType;
        this.eventTime = eventTime;
        this.queueNumber = queueNumber;
    }

    public TypeOfTimeEvent getSimulationEventType() {
        return simulationEventType;
    }

    public int getQueueNumber() {
        return queueNumber;
    }

    public double getEventTime() {
        return eventTime;
    }

}
