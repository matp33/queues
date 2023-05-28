package dto;

import java.awt.Point;

public class PointWithTimeDTO {

    private Point point;

    private double time;

    public PointWithTimeDTO(Point point, double time) {
        this.point = point;
        this.time = time;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }
}
