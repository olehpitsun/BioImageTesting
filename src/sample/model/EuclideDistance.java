package sample.model;

/**
 * Created by oleh_pi on 11.11.2016.
 */
public class EuclideDistance {

    private double euclideDistance = 0;

    public EuclideDistance(){}

    public double getEuclideDistance(double x1, double y1, double x2, double y2){
        euclideDistance = Math.sqrt(Math.pow(x1 - y1, 2) + Math.pow(x2 - y2, 2));
        return euclideDistance;
    }
}
