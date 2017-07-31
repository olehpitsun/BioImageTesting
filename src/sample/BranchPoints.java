package sample;

/**
 * Created by Oleh on 28.07.2017.
 */
public class BranchPoints {

    public int X_1;
    public int Y_1;
    public int X_2;
    public int Y_2;
    public double area;

    public BranchPoints(int x_1, int y_1, int x_2, int y_2, double area1) {
        X_1 = x_1;
        Y_1 = y_1;
        X_2 = x_2;
        Y_2 = y_2;
        area = area1;
    }

    public int getX_1() {
        return X_1;
    }

    public void setX_1(int x_1) {
        X_1 = x_1;
    }

    public int getY_1() {
        return Y_1;
    }

    public void setY_1(int y_1) {
        Y_1 = y_1;
    }

    public int getX_2() {
        return X_2;
    }

    public void setX_2(int x_2) {
        X_2 = x_2;
    }

    public int getY_2() {
        return Y_2;
    }

    public void setY_2(int y_2) {
        Y_2 = y_2;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    @Override
    public String toString() {
        return "BranchPoints{" +
                "X_1=" + X_1 +
                ", Y_1=" + Y_1 +
                ", X_2=" + X_2 +
                ", Y_2=" + Y_2 +
                '}';
    }
}
