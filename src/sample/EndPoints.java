package sample;

/**
 * Created by Oleh on 23.07.2017.
 */
public class EndPoints {

    public int x;
    public int y;

    public EndPoints(int x, int y) {
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
        return "EndPoints{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
