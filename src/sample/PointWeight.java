package sample;

/**
 * Created by Oleh on 22.07.2017.
 */
public class PointWeight {

    public int x;
    public int y;
    public int distance;

    public PointWeight(int x, int y, int distance) {
        this.x = x;
        this.y = y;
        this.distance = distance;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        y = y;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "PointWeight{" +
                "X=" + x +
                ", Y=" + y +
                ", distance=" + distance +
                '}';
    }
}
