package symulation;

import constants.TypeOfTimeEvent;

public class ClientArrivalEvent {

    private double arrivalTime;

    private int queueNumber;

    private double timeInCheckout;

    public ClientArrivalEvent(double timeInCheckout, double arrivalTime, int queueNumber) {
        this.arrivalTime = arrivalTime;
        this.queueNumber = queueNumber;
        this.timeInCheckout = timeInCheckout;
    }

    public double getTimeInCheckout() {
        return timeInCheckout;
    }

    public int getQueueNumber() {
        return queueNumber;
    }

    public double getArrivalTime() {
        return arrivalTime;
    }

}
