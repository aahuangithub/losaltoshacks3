package tech.trash.finderssweepers;

/**
 * Created by Aaron on 3/25/2018.
 */

public class Trash {
    String category;
    Coordinate coordinate;
    public Trash(String category, double x, double y){
        this.category = category;
        this.coordinate = new Coordinate(x, y);
    }

    public String getCategory() {
        return category;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }
}
