package sample.model.Segmentation;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import sample.model.PreProcessing.PreProcessingOperation;
import sample.tools.ValidateOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oleh on 11.02.16.
 */
public class SegmentationOperations {

    private Mat outputImage;
    private String value;
    private String maxValueThreshold;

    public SegmentationOperations(Mat inputImage, String segType, String value, String maxValThreshold ){
        this.outputImage = inputImage;
        this.value = ValidateOperations.filterAndSegValidate(value);
        this.maxValueThreshold = ValidateOperations.filterAndSegValidate(maxValThreshold);

        System.out.println("type " + segType);

        if(segType == "1"){

            Mat frame = this.outputImage;

            Mat hsvImg = new Mat();
            List<Mat> hsvPlanes = new ArrayList<>();
            Mat thresholdImg = new Mat();

            int thresh_type = Imgproc.THRESH_BINARY_INV;

            // threshold the image with the average hue value
            hsvImg.create(frame.size(), CvType.CV_8U);
            Imgproc.cvtColor(frame, hsvImg, Imgproc.COLOR_BGR2HSV);
            Core.split(hsvImg, hsvPlanes);

            // get the average hue value of the image
            double threshValue = PreProcessingOperation.getHistAverage(hsvImg, hsvPlanes.get(0));

            Imgproc.threshold(hsvPlanes.get(0), thresholdImg, Integer.parseInt(this.value),
                    Integer.parseInt(this.maxValueThreshold), thresh_type);

            Imgproc.blur(thresholdImg, thresholdImg, new Size(3, 3));

            // dilate to fill gaps, erode to smooth edges
            Imgproc.dilate(thresholdImg, thresholdImg, new Mat(), new Point(-1, -1), 1);
            Imgproc.erode(thresholdImg, thresholdImg, new Mat(), new Point(-1, -1), 1);

            Imgproc.threshold(thresholdImg, thresholdImg, Integer.parseInt(this.value),
                    Integer.parseInt(this.maxValueThreshold), Imgproc.THRESH_BINARY);

            // create the new image
            Mat foreground = new Mat(frame.size(), CvType.CV_8UC3, new Scalar(255, 255, 255));
            frame.copyTo(foreground, thresholdImg);

            Core.bitwise_not(foreground,foreground);
            this.outputImage = foreground;
        }

    }

    public Mat getOutputImage(){
        return this.outputImage;
    }
}
