package sample.model.PreProcessing;

import org.opencv.core.Mat;

/**
 * Created by oleh on 11.02.16.
 */
public class PreProcessingOperation {

    private Mat outputImage;

    public PreProcessingOperation(Mat inputImage, String contrast, String bright, String dilate, String erode){

        outputImage = inputImage;

        if(contrast != null) {
            outputImage = PreProcessing.contrast(outputImage, Integer.parseInt(contrast));
        }

        if(bright != null){
            outputImage = PreProcessing.contrast(outputImage, Integer.parseInt(contrast));
        }

        if(dilate != null){
            outputImage = PreProcessing.Dilate(outputImage,Integer.parseInt(dilate));
        }

        if(erode != null){
            outputImage = PreProcessing.Erode(outputImage,Integer.parseInt(erode));
        }
    }

    public Mat getOutputImage(){
        return outputImage;
    }
}
