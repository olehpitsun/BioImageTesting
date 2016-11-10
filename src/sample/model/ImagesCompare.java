package sample.model;

import org.opencv.core.Mat;

/**
 * Created by oleh_pi on 10.11.2016.
 */
public class ImagesCompare {

    private Mat ImgEtalon, ImgSegmented;
    private double picdataEtalon [][];
    private double picdataSegmented [][];

    public double rightClassifiedPixelsCount = 0;

    public ImagesCompare(Mat ImgEtalon, Mat ImgSegmented){
        this.ImgEtalon = ImgEtalon;
        this.ImgSegmented = ImgSegmented;
    }

    public void calculateRightClassifiedPixels(){

        Mat dst = new Mat(this.ImgEtalon.rows(), this.ImgEtalon.cols(), 3);
        byte buff[] = new byte[ (int) (this.ImgEtalon.total() * this.ImgEtalon.channels())];

        int a,b;

        picdataEtalon =  new double[this.ImgEtalon.rows()][this.ImgEtalon.cols()] ;
        picdataSegmented =  new double[this.ImgEtalon.rows()][this.ImgEtalon.cols()] ;

        double[] tempEtalon;
        double[] tempSegmented;

        for (a=0 ; a< this.ImgEtalon.rows();a++)
        {
            for (b=0 ; b<ImgEtalon.cols(); b++)
            {
                tempEtalon = ImgEtalon.get(a, b);
                picdataEtalon[a][b]=tempEtalon[0];

                tempSegmented = this.ImgSegmented.get(a,b);
                picdataSegmented[a][b]=tempSegmented[0];

                if(picdataEtalon[a][b] < 100)
                {
                    if(picdataEtalon[a][b] == picdataSegmented[a][b])
                    {
                        rightClassifiedPixelsCount++;
                    }
                }
            }
        }
    }

    public double getRightClassifiedPixelsCount(){
        return rightClassifiedPixelsCount;
    }
}
