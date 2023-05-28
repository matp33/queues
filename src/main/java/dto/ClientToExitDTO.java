package dto;

import constants.PositionInQueueToExit;
import visualComponents.Client;

import java.util.Comparator;

public class ClientToExitDTO implements Comparable<ClientToExitDTO> {

    private Client client;

    private PositionInQueueToExit positionInQueueToExit;

    private double estimatedTimeAtDestination;

    private int indexInPosition;

    public ClientToExitDTO(Client client, PositionInQueueToExit positionInQueueToExit, double estimatedTimeAtDestination, int indexInPosition) {
        this.client = client;
        this.positionInQueueToExit = positionInQueueToExit;
        this.estimatedTimeAtDestination = estimatedTimeAtDestination;
        this.indexInPosition = indexInPosition;
    }

    public int getIndexInPosition() {
        return indexInPosition;
    }

    public double getEstimatedTimeAtDestination() {
        return estimatedTimeAtDestination;
    }

    public void setIndexInPosition(int indexInPosition) {
        this.indexInPosition = indexInPosition;
    }

    public Client getClient() {
        return client;
    }

    public PositionInQueueToExit getPositionInQueueToExit() {
        return positionInQueueToExit;
    }

    public void setEstimatedTimeAtDestination(double estimatedTimeAtDestination) {
        this.estimatedTimeAtDestination = estimatedTimeAtDestination;
    }

    @Override
    public int compareTo(ClientToExitDTO o) {
        return Double.compare(estimatedTimeAtDestination, o.estimatedTimeAtDestination);
    }

    @Override
    public String toString() {
        return "ClientToExitDTO{" +
                "client=" + client +
                ", positionInQueueToExit=" + positionInQueueToExit +
                ", estimatedTimeAtDestination=" + estimatedTimeAtDestination +
                '}';
    }

    public void setPositionInQueueToExit(PositionInQueueToExit positionInQueueToExit) {
        this.positionInQueueToExit = positionInQueueToExit;
    }
}
