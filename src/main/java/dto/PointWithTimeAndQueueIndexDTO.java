package dto;

import java.awt.*;

public class PointWithTimeAndQueueIndexDTO extends PointWithTimeDTO {
    private int indexInQueue;

    public PointWithTimeAndQueueIndexDTO(Point point, double time, int indexInQueue) {
        super(point, time);
        this.indexInQueue = indexInQueue;
    }

    public int getIndexInQueue() {
        return indexInQueue;
    }

    public void setIndexInQueue(int indexInQueue) {
        this.indexInQueue = indexInQueue;
    }
}
