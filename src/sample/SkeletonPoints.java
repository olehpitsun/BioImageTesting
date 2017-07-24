package sample;

/**
 * Created by Oleh on 22.07.2017.
 */
public class SkeletonPoints {

    public int x;
    public int y;

    public SkeletonPoints(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "SkeletonPoints{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
