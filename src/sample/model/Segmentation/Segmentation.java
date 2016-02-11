package sample.model.Segmentation;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Created by oleh on 02.01.16.
 */
public class Segmentation {

    public static Mat cannyDetection(Mat image, int size){

        Mat grayImage = new Mat();
        Mat detectedEdges = new Mat();

        // convert to grayscale
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);

        // reduce noise with a 3x3 kernel
        Imgproc.blur(grayImage, detectedEdges, new Size(3, 3));

        // canny detector, with ratio of lower:upper threshold of 3:1
        Imgproc.Canny(detectedEdges, detectedEdges, size, size/3, 7, false);
        return detectedEdges;
    }

    public static Mat Laplacian(Mat source, int size, int delta){

        int ddepth = CvType.CV_16S;

        Mat abs_dst,dst;

        Imgproc.GaussianBlur(source, source, new Size(3.0, 3.0), 0);

        Imgproc.GaussianBlur(source, source, new Size(3, 3), 0, 0,  Imgproc.BORDER_DEFAULT);
        //cvtColor( src, gray, CV_RGB2GRAY );

        /// Apply Laplace function
        Imgproc.Laplacian(source, source, CvType.CV_16S, size, 1, delta, Imgproc.BORDER_DEFAULT);
        return source;
    }

    public static Mat Sobel(Mat source, int delta ){

        Mat grey = new Mat();
        Imgproc.cvtColor(source, grey, Imgproc.COLOR_BGR2GRAY);
        Mat sobelx = new Mat();
        Imgproc.Sobel(grey, sobelx, CvType.CV_32F, 1, delta);

        double minVal, maxVal;
        Core.MinMaxLocResult minMaxLocResult=Core.minMaxLoc(sobelx);
        minVal=minMaxLocResult.minVal;
        maxVal=minMaxLocResult.maxVal;

        Mat draw = new Mat();
        sobelx.convertTo(draw, CvType.CV_8U, 255.0 / (maxVal - minVal), -minVal * 255.0 / (maxVal - minVal));
        return draw;
    }
}
