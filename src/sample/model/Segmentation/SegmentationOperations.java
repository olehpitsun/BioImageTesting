package sample.model.Segmentation;

import org.opencv.core.*;
import sample.tools.ValidateOperations;

/**
 * Created by oleh on 11.02.16.
 */
public class SegmentationOperations {

    private Mat outputImage;
    private String value;
    private String maxValueThreshold;

    /**
     *
     * @param inputImage
     * @param segType
     * @param value
     * @param maxValThreshold
     *
     * segType == "1" - thresholding
     * segType == "2" - watershed segmentation
     * segType == "3" - kmeans segmentation
     */
    public SegmentationOperations(Mat inputImage, String segType, String value, String maxValThreshold ){
        this.outputImage = inputImage;
        this.value = ValidateOperations.filterAndSegValidate(value);
        this.maxValueThreshold = ValidateOperations.filterAndSegValidate(maxValThreshold);

        if(segType == "1"){

            this.outputImage = Segmentation.thresholding(this.outputImage,Integer.parseInt(this.value),
                    Integer.parseInt(this.maxValueThreshold));
        }
        if(segType == "2"){
            this.outputImage = Segmentation.watershed(this.outputImage);
        }
        if(segType == "3"){
            this.outputImage = Segmentation.kmeans(this.outputImage);
        }
    }

    public Mat getOutputImage(){
        return this.outputImage;
    }
}
