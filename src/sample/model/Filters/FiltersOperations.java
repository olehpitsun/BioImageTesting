package sample.model.Filters;

import org.opencv.core.Mat;
import sample.tools.ValidateOperations;

/**
 * Created by oleh on 11.02.16.
 */
public class FiltersOperations {

    private Mat outputImage;
    private String kSize;
    private String sigma;
    private String sigmaSpace;
    private String SigmaColor;

    public FiltersOperations(Mat inputImage, String filterType, String kSize, String sigma,
                             String sigmaSpace, String sigmaColor){

        outputImage = new Mat();

        this.kSize = ValidateOperations.filterAndSegValidate(kSize);
        this.sigma = ValidateOperations.filterAndSegValidate(sigma);
        this.sigmaSpace = ValidateOperations.filterAndSegValidate(sigmaSpace);
        this.SigmaColor = ValidateOperations.filterAndSegValidate(sigmaColor);

        if(filterType == "1") {

            outputImage = Filters.gaussianBlur(inputImage, Integer.parseInt(this.kSize),
                    Double.parseDouble(this.sigma) );
        }
        if(filterType == "2"){

            outputImage = Filters.bilateralFilter(inputImage, Integer.parseInt(this.kSize),
                    Double.parseDouble(this.sigmaSpace), Double.parseDouble(this.SigmaColor));
        }
        if(filterType == "3"){
            int sP = Integer.valueOf(this.sigmaSpace);
            outputImage = Filters.adaptiveBilateralFilter(inputImage, Integer.parseInt(this.kSize), sP);
        }
        if(filterType == "4"){
            outputImage = Filters.medianBlur(inputImage,Integer.parseInt(this.kSize));
        }

    }

    public Mat getOutputImage(){
        return outputImage;
    }
}
