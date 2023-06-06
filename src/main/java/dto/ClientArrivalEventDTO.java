package dto;

public class ClientArrivalEventDTO {

    private double arrivalTime;

    private int queueNumber;

    private double timeInCheckout;

    public ClientArrivalEventDTO(double timeInCheckout, double arrivalTime, int queueNumber) {
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
