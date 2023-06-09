package navmesh;

import spring2.Bean;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Bean
public class GridHandler {

    private Map<Cell,Boolean> cellToOccupiedMap = new HashMap<>();

    private Set<Cell> temporarilyOccupiedCells = new HashSet<>();

    private static final Dimension GRID_DIMENSION = new Dimension(15, 15);

    private Dimension panelSize;

    public void initialize (Dimension simulationPanelDimension){
        panelSize = simulationPanelDimension;
        double height = panelSize.getHeight();
        double width = panelSize.getWidth();
        double amountOfCellsHorizontally = width / GRID_DIMENSION.getWidth();
        double amountOfCellsVertically = height / GRID_DIMENSION.getHeight();
        for (int y=0;y <amountOfCellsVertically; y++){
            for (int x =0; x<amountOfCellsHorizontally; x++){
                cellToOccupiedMap.put(new Cell(x, y), false);
            }
        }
    }

    public CellArea getCell (int row, int column){
        Point point = new Point(GRID_DIMENSION.width * column, GRID_DIMENSION.height * row);
        return new CellArea(point, GRID_DIMENSION);
    }

    public synchronized Set<CellArea> getCellsToClear(){
        return temporarilyOccupiedCells.stream().map(cell -> getCell(cell.getRow(),cell.getColumn()))
                .collect(Collectors.toSet());
    }

    public synchronized void clearTemporarilyOccupiedCells(){
        temporarilyOccupiedCells.forEach(cell -> cellToOccupiedMap.put(cell, false));
        temporarilyOccupiedCells.clear();
    }


    public void markCellsOccupied(Point position, Dimension objectDimension, boolean temporarilyOccupied) {
        int column = (int) position.getX()/GRID_DIMENSION.width;
        int row = (int) position.getY()/GRID_DIMENSION.height;
        Cell cell = new Cell(column, row);
        int amountOfCellsOccupiedHorizontally = objectDimension.width/GRID_DIMENSION.width +
                (position.x % GRID_DIMENSION.width != 0? 1: 0) + (objectDimension.width % GRID_DIMENSION.width != 0? 1:0);
        int amountOfCellsOccupiedVertically = objectDimension.height/GRID_DIMENSION.height +
                (position.y % GRID_DIMENSION.height != 0? 1: 0) + (objectDimension.height % GRID_DIMENSION.height != 0? 1:0);
        cellToOccupiedMap.put(cell, true);
        if (temporarilyOccupied){
            temporarilyOccupiedCells.add(cell);
        }

        for (int x=0; x<amountOfCellsOccupiedHorizontally; x++){
            for (int y=0; y<amountOfCellsOccupiedVertically; y++){
                Cell localCell = new Cell(column+x, row+y);
                cellToOccupiedMap.put(localCell, true);
                if (temporarilyOccupied) {
                    temporarilyOccupiedCells.add(localCell);
                }
            }
        }
    }

    public Set<CellArea> getOccupiedCells (){
        return cellToOccupiedMap.entrySet().stream().filter(en->en.getValue().equals(true)).map(Map.Entry::getKey)
                .map(cell->getCell(cell.getRow(), cell.getColumn())).collect(Collectors.toSet());
    }

}
