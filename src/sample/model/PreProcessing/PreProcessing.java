package sample.model.PreProcessing;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oleh on 02.01.16.
 */
public class PreProcessing {

    public static Mat contrast (Mat image, Integer a){


        /*Mat hsvImg = new Mat();
        List<Mat> hsvPlanes = new ArrayList<>();
        Mat thresholdImg = new Mat();

        int thresh_type = Imgproc.THRESH_BINARY_INV;

        //if (this.inverse.isSelected())
        //thresh_type = Imgproc.THRESH_BINARY;

        // threshold the image with the average hue value
        hsvImg.create(image.size(), CvType.CV_8U);
        Imgproc.cvtColor(image, hsvImg, Imgproc.COLOR_BGR2HSV);
        Core.split(hsvImg, hsvPlanes);

        // get the average hue value of the image
        double threshValue = PreProcessingOperation.getHistAverage(hsvImg, hsvPlanes.get(0));
        System.out.println("Value before " +threshValue);*/



        Scalar modifier;


        double amt = 1.2;
        modifier = new Scalar(0.9,0.9,1.3,1);
        Core.multiply(image, modifier, image);
        return image;
    }

    public static Mat bright(Mat image, int sz){

        Mat dst = new Mat(image.rows(), image.cols(), image.type());
        image.convertTo(dst, -1, 10d * sz / 100, 0);

        Mat hsvImg = new Mat();
        List<Mat> hsvPlanes = new ArrayList<>();
        Mat thresholdImg = new Mat();

        int thresh_type = Imgproc.THRESH_BINARY_INV;

        //if (this.inverse.isSelected())
        //thresh_type = Imgproc.THRESH_BINARY;

        // threshold the image with the average hue value
        hsvImg.create(image.size(), CvType.CV_8U);
        Imgproc.cvtColor(image, hsvImg, Imgproc.COLOR_BGR2HSV);
        Core.split(hsvImg, hsvPlanes);
        // get the average hue value of the image
        double threshValue = PreProcessingOperation.getHistAverage(hsvImg, hsvPlanes.get(0));
        System.out.println("After preproc" + threshValue);

        return dst;
    }

    public static Mat Erode(Mat image, int kernel){

        final Mat dst = new Mat(image.cols(), image.rows(), CvType.CV_8UC3);
        image.copyTo(dst);
        Imgproc.erode(dst, dst, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(kernel,kernel)));
        return dst;
    }

    public static Mat Dilate(Mat image, int kernel){

        final Mat dst = new Mat(image.cols(), image.rows(), CvType.CV_8UC3);
        image.copyTo(dst);
        Imgproc.dilate(dst, dst, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(kernel,kernel)));
        return dst;
    }


}
