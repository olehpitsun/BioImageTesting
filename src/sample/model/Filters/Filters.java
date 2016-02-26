package sample.model.Filters;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Created by oleh on 02.01.16.
 */
public class Filters {

    public static Mat gaussianBlur(Mat image, int kSize, Double sigmaX){

        if (kSize%2 != 1) {
            kSize -= 1;
        }
        Mat result = new Mat();
        Size s = new Size(kSize, kSize);
        Imgproc.GaussianBlur(image, result, s, sigmaX);
        return result;
    }

    public static Mat bilateralFilter(Mat image, int d, double sigmaColor, double sigmaSpace){
        Mat result = new Mat();
        Imgproc.bilateralFilter(image, result, d, sigmaColor, sigmaSpace);
        return result;
    }

    public static Mat adaptiveBilateralFilter(Mat image, int kSize, int sigmaSpace){
        if (kSize%2 != 1) {
            kSize -= 1;
        }

        Mat result = new Mat();
        Size s = new Size(kSize, kSize);
        Imgproc.adaptiveBilateralFilter(image, result, s, sigmaSpace);
        return result;
    }

    public static Mat medianBlur(Mat image, int kSize){
        if (kSize%2 == 0) kSize -= 1;
        //Mat result = new Mat();
        Imgproc.medianBlur(image, image, kSize);
        return image;
    }
}
