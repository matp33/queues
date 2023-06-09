package navmesh;

import java.util.Objects;

public class Cell {

    private int column;

    private int row;

    public Cell(int column, int row) {
        this.column = column;
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return column == cell.column && row == cell.row;
    }

    @Override
    public int hashCode() {
        return Objects.hash(column, row);
    }
}
