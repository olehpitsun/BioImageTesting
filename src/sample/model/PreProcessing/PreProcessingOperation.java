package sample.model.PreProcessing;

import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgproc.Imgproc;
import sample.tools.ValidateOperations;
import sample.util.PreProcessingParam;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oleh on 11.02.16.
 */
public class PreProcessingOperation {

    private Mat outputImage;

    public PreProcessingOperation(Mat inputImage, String contrast, String bright, String dilate, String erode){

        this.outputImage = inputImage;
        PreProcessingParam prparam = new PreProcessingParam();

        if (contrast.length() == 0 || ValidateOperations.isInt(contrast) == false) {
            prparam.setContrast(null);
        }else{
            prparam.setContrast(contrast);
        }

        if (bright.length() == 0 || ValidateOperations.isInt(bright) == false) {
            prparam.setBright(null);
        }else{
            prparam.setBright(bright);
        }

        if ( dilate.length() == 0 || ValidateOperations.isInt(dilate) == false) {
            prparam.setDilate(null);
        }else{
            prparam.setDilate(dilate);
        }

        if (erode.length() == 0 || ValidateOperations.isInt(erode) == false) {
            prparam.setErode(null);
        }else{
            prparam.setErode(erode);
        }
        this.setPreProcValue(prparam.getContrast(),prparam.getBright(),prparam.getDilate(),prparam.getErode());
    }

    public void setPreProcValue(String contrast, String bright, String dilate, String erode){

        if(contrast != null) {
            outputImage = PreProcessing.contrast(this.outputImage, Integer.parseInt(contrast));
        }

        if(bright != null){
            outputImage = PreProcessing.bright(this.outputImage, Integer.parseInt(bright));
        }

        if(dilate != null){
            outputImage = PreProcessing.Dilate(this.outputImage,Integer.parseInt(dilate));
        }

        if(erode != null){
            outputImage = PreProcessing.Erode(this.outputImage,Integer.parseInt(erode));
        }
    }

    public Mat getOutputImage(){
        return this.outputImage;
    }

    /**
     * Get the average hue value of the image starting from its Hue channel
     * histogram
     *
     * @param hsvImg
     *            the current frame in HSV
     * @param hueValues
     *            the Hue component of the current frame
     * @return the average Hue value
     */
    public static double getHistAverage(Mat hsvImg, Mat hueValues)
    {
        // init
        double average = 0.0;
        Mat hist_hue = new Mat();
        // 0-180: range of Hue values
        MatOfInt histSize = new MatOfInt(180);
        List<Mat> hue = new ArrayList<>();
        hue.add(hueValues);

        // compute the histogram
        Imgproc.calcHist(hue, new MatOfInt(0), new Mat(), hist_hue, histSize, new MatOfFloat(0, 179));

        // get the average Hue value of the image
        // (sum(bin(h)*h))/(image-height*image-width)
        // -----------------
        // equivalent to get the hue of each pixel in the image, add them, and
        // divide for the image size (height and width)
        for (int h = 0; h < 180; h++)
        {
            // for each bin, get its value and multiply it for the corresponding
            // hue
            average += (hist_hue.get(h, 0)[0] * h);
        }

        // return the average hue of the image
        return average = average / hsvImg.size().height / hsvImg.size().width;
    }
}
