package sample.model;

import org.opencv.core.Mat;

/**
 * Created by oleh_pi on 10.11.2016.
 */
public class PixelArray {

    public int blackPixelCount = 0;
    public int allPixelsCount = 0;
    public Mat src;
    private int imgRows = 0;
    private int imgCols = 0;
    private double picdata[][];

    public PixelArray(Mat src){

        this.src = src;
        imgRows = src.rows();
        imgCols = src.cols();

    }

    public Mat calculatePixels(){

        Mat dst = new Mat(imgRows, imgCols, 3);

        byte buff[] = new byte[ (int) (this.src.total() * this.src.channels())];

        int a,b;

        picdata =  new double[imgRows][imgCols] ;
        double[] temp;

        for (a=0 ; a<imgRows;a++)
        {
            for (b=0 ; b<imgCols;b++)
            {
                temp = src.get(a, b);
                picdata[a][b]=temp[0];
                //System.out.println(picdata[a][b]);
                dst.put(a,b,picdata[a][b]);

                allPixelsCount++;
            }
        }

        return dst;
    }

    public int getBlackPixelCount(){

        for (int i = 0; i< imgRows; i++) {
            for (int j = 0; j < imgCols; j++) {
                if(picdata[i][j] < 100){
                    blackPixelCount++;
                }
            }
        }
        return blackPixelCount;
    }

    public int getAllPixelsCount(){
        return allPixelsCount;
    }

    public Mat getDifference(Mat img1, Mat img2){
        Mat dst = new Mat(img1.rows(), img1.cols(), 3);

        byte buffEtalon[] = new byte[ (int) (img1.total() * img1.channels())];
        byte buffSegmented[] = new byte[ (int) (img2.total() * img2.channels())];

        int a,b;

        double picdataEtalon[][] =  new double[img1.rows()][img1.cols()] ;
        double picdataSegmented[][] =  new double[img2.rows()][img2.cols()] ;

        double[] tempEtalon;
        double[] tempSegmented;

        for (a=0 ; a < img1.rows(); a++)
        {
            for (b=0 ; b < img1.cols(); b++)
            {
                tempEtalon = img1.get(a, b);
                tempSegmented = img2.get(a, b);

                picdataEtalon[a][b]=tempEtalon[0];
                picdataSegmented[a][b]=tempSegmented[0];


                if(picdataEtalon[a][b] == picdataSegmented[a][b]){
                    dst.put(a,b,255);
                }else{
                    dst.put(a,b,0);
                }
            }
        }

        return dst;
    }
}
