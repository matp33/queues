package navmesh;

import java.awt.*;

public class CellArea {

    private Point topLeftPoint;

    private Dimension dimension;

    public CellArea(Point topLeftPoint, Dimension dimension) {
        this.topLeftPoint = topLeftPoint;
        this.dimension = dimension;
    }

    public Point getTopLeftPoint() {
        return topLeftPoint;
    }

    public Dimension getDimension() {
        return dimension;
    }
}
