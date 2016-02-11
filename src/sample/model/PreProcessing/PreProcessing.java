package sample.model.PreProcessing;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Created by oleh on 02.01.16.
 */
public class PreProcessing {

    public static Mat contrast(Mat image, int sz){

        Mat dst = new Mat(image.rows(), image.cols(), image.type());
        image.convertTo(dst, -1, 10d * sz / 100, 0);
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
