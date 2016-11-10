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
                System.out.println(picdata[a][b]);
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

}
