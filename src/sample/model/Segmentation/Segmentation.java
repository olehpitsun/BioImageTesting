package sample.model.Segmentation;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import sample.model.PreProcessing.PreProcessing;
import sample.model.PreProcessing.PreProcessingOperation;
import sample.util.Estimate;

import java.util.ArrayList;
import java.util.List;
//import sample.model.Estimate;

/**
 * Created by oleh on 02.01.16.
 */
public class Segmentation{

    /**
     *
     * @param image
     * @param size
     * @return Mat
     */
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

    /**
     *
     * @param source
     * @param size
     * @param delta
     * @return Mat
     */
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

    /**
     *
     * @param source
     * @param delta
     * @return Mat
     */
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

    /**
     *
     * @param inputImg
     * @param minValue
     * @param maxValue
     * @return Mat
     */
    public static Mat thresholding(Mat inputImg, Integer minValue, Integer maxValue){

        Mat frame = inputImg;
// яскравість
        //frame.convertTo(frame , -1, 10d * 33 / 100, 0);
        //Imgproc.medianBlur(frame,frame, 17);

        //Core.bitwise_not(frame,frame );

        //Mat frame = new Mat(image.rows(), image.cols(), image.type());

        //frame.convertTo(frame, -1, 10d * 20 / 100, 0);




        Mat hsvImg = new Mat();
        List<Mat> hsvPlanes = new ArrayList<>();
        Mat thresholdImg = new Mat();

        int thresh_type = Imgproc.THRESH_BINARY_INV;

        //if (this.inverse.isSelected())
        //thresh_type = Imgproc.THRESH_BINARY;

        // threshold the image with the average hue value
        //System.out.println("size " +frame.size());
        hsvImg.create(frame.size(), CvType.CV_8U);
        //Imgproc.cvtColor(frame, hsvImg, Imgproc.COLOR_BGR2HSV);
        Core.split(hsvImg, hsvPlanes);

        // get the average hue value of the image
        //double threshValue = PreProcessingOperation.getHistAverage(hsvImg, hsvPlanes.get(0));
        //System.out.println(threshValue);
/*
        if(threshValue > 40){
            maxValue = 160;
        }else{
            maxValue = 40;
        }*/

//        Imgproc.threshold(hsvPlanes.get(1), thresholdImg, minValue , maxValue , thresh_type);





        Imgproc.blur(thresholdImg, thresholdImg, new Size(27, 27));

        // dilate to fill gaps, erode to smooth edges
        Imgproc.dilate(thresholdImg, thresholdImg, new Mat(), new Point(-1, -1), 1);
        Imgproc.erode(thresholdImg, thresholdImg, new Mat(), new Point(-1, -1), 1);

        Imgproc.threshold(thresholdImg, thresholdImg, minValue, maxValue, Imgproc.THRESH_BINARY);

        // create the new image
        Mat foreground = new Mat(frame.size(), CvType.CV_8UC3, new Scalar(255, 255, 255));
        Core.bitwise_not(thresholdImg,foreground);

        frame.copyTo(foreground, thresholdImg);

        ///////////////////////////////////////////////////////////////////////////////////////
        ///
        ////






        return foreground;
        /*Mat hsvImg = new Mat();
        List<Mat> hsvPlanes = new ArrayList<>();
        Mat thresholdImg = new Mat();
        int thresh_type = Imgproc.THRESH_BINARY_INV;
        // threshold the image with the average hue value
        hsvImg.create(inputImg.size(), CvType.CV_8U);
        Imgproc.cvtColor(inputImg, hsvImg, Imgproc.COLOR_BGR2HSV);
        Core.split(hsvImg, hsvPlanes);
        // get the average hue value of the image
        double threshValue = PreProcessingOperation.getHistAverage(hsvImg, hsvPlanes.get(0));
        Imgproc.threshold(hsvPlanes.get(0), thresholdImg, minValue,
                maxValue, thresh_type);
        Imgproc.blur(thresholdImg, thresholdImg, new Size(3, 3));
        // dilate to fill gaps, erode to smooth edges
        Imgproc.dilate(thresholdImg, thresholdImg, new Mat(), new Point(-1, -1), 3);
        Imgproc.erode(thresholdImg, thresholdImg, new Mat(), new Point(-1, -1), 1);
        Imgproc.threshold(thresholdImg, thresholdImg, minValue,
                maxValue, Imgproc.THRESH_BINARY);
        // create the new image
        Mat foreground = new Mat(inputImg.size(), CvType.CV_8UC3, new Scalar(255, 255, 255));
        inputImg.copyTo(foreground, thresholdImg);
        Core.bitwise_not(foreground,foreground);
        return foreground;*/
    }

    /**
     *
     * @param inputImg
     * @return Mat
     */
    public static Mat watershed(Mat inputImg){

        Mat target =  new Mat(inputImg.rows(), inputImg.cols(), CvType.CV_8UC3);
        Imgproc.cvtColor(inputImg, target, Imgproc.COLOR_BGR2RGB);

        //Conversion to 8UC1 grayscale image
        Mat grayScale = new Mat(inputImg.rows(), inputImg.cols(), CvType.CV_32SC1);
        Imgproc.cvtColor(inputImg, grayScale, Imgproc.COLOR_BGR2GRAY);

        //constructing a 3x3 kernel for morphological opening
        Mat openingKernel = Mat.ones(9,9, CvType.CV_8U);

        // яскравість
        //target.convertTo(target, -1, 10d * 12 / 100, 0);
        //Imgproc.dilate(target, target, new Mat(), new Point(-1, -1), 1);

        Size s = new Size(27, 27);
        Imgproc.GaussianBlur(target, target, s, 1.7);

        Imgproc.morphologyEx(target, target, Imgproc.MORPH_OPEN, openingKernel);

        //dilation operation for extracting the background
        //Imgproc.dilate(target, target, openingKernel);
        //Imgproc.erode(target, target, new Mat(), new Point(-1, -1), 1);


        Mat seeds = new Mat(target.rows(), target.cols(), CvType.CV_32SC1);

        for(int i = 0; i < 10; i++) {
            seeds.put(((int) Math.random())%target.rows(), ((int)Math.random())%target.cols(),i);
        }

        Imgproc.watershed(target, seeds);
        //Imgproc.threshold(target,target, 50, 155, Imgproc.THRESH_BINARY );
        return target;
    }

    /**
     *
     * @param inputImg
     * @return Mat
     */
    public static Mat kmeans(Mat inputImg){

        Mat rgba =inputImg; Mat tempMat = inputImg;
        rgba = new Mat(inputImg.cols(), inputImg.rows(), CvType.CV_8UC3);
        inputImg.copyTo(rgba);

        List<Mat> hsv_planes_temp = new ArrayList<Mat>(3);
        Core.split(tempMat, hsv_planes_temp);

        double threshValue1 = PreProcessingOperation.getHistAverage(inputImg, hsv_planes_temp.get(0));
        sample.util.Estimate.setFirstHistAverageValue(threshValue1);
        System.out.println("Defore eqau " +  threshValue1);

        System.out.println(Estimate.getBlueAverage() + " ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;");

        if( threshValue1 > 140){
            if(Estimate.getBlueAverage() > 110){
                rgba.convertTo(rgba, -1, 10d * 31 / 100, 0); System.out.println("11");
            }
            else {
                rgba.convertTo(rgba, -1, 10d * 40 / 100, 0); System.out.println("12");
            }
        }
        else if( threshValue1 > 135){
            rgba.convertTo(rgba, -1, 10d * 32 / 100, 0); System.out.println("21");
        }
        else if( threshValue1 > 125){
            if(Estimate.getBlueAverage() > 110){
                rgba.convertTo(rgba, -1, 10d * 30 / 100, 0); rgba = PreProcessing.Dilate(rgba,5) ; System.out.println("31");
            }else {
                rgba.convertTo(rgba, -1, 10d * 37 / 100, 0); System.out.println("32");
            }
        }
        else if( threshValue1 > 120){
            rgba.convertTo(rgba, -1, 10d * 35 / 100, 0); System.out.println("41");
        }
        else if( threshValue1 > 110){
            if(Estimate.getBlueAverage() > 110){
                rgba.convertTo(rgba, -1, 10d * 35 / 100, 0); rgba = PreProcessing.Dilate(rgba,5) ; System.out.println("51");
            }
        }
        else if( threshValue1 > 100){
            if(Estimate.getBlueAverage() > 107){
                rgba.convertTo(rgba, -1, 10d * 24 / 100, 0); rgba = PreProcessing.Dilate(rgba,5) ; System.out.println("61");
            }
            else if(Estimate.getBlueAverage() > 90){
                rgba.convertTo(rgba, -1, 10d * 30 / 100, 0); rgba = PreProcessing.Dilate(rgba,5) ; System.out.println("62");
            }
        }
        else if( threshValue1 > 50){

            if(Estimate.getBlueAverage() > 160){
                rgba.convertTo(rgba, -1, 10d * 30 / 100, 0);  rgba = PreProcessing.Dilate(rgba,3) ; System.out.println("81");
            }
            else if(Estimate.getBlueAverage() > 160){
                rgba.convertTo(rgba, -1, 10d * 27 / 100, 0); rgba = PreProcessing.Dilate(rgba,9) ; System.out.println("82");
            }
            else if(Estimate.getBlueAverage() > 130){
                rgba.convertTo(rgba, -1, 10d * 30 / 100, 0); rgba = PreProcessing.Dilate(rgba,9) ; System.out.println("83");
            }
            else if(Estimate.getBlueAverage() > 70){
                rgba.convertTo(rgba, -1, 10d * 29 / 100, 0); rgba = PreProcessing.Dilate(rgba,9) ; System.out.println("84");
            }
        }
        else if( threshValue1 > 30){
            if(Estimate.getBlueAverage() > 190){
                rgba.convertTo(rgba, -1, 10d * 25 / 100, 0);  System.out.println("91");
            }
            else if(Estimate.getBlueAverage() > 160){
                rgba.convertTo(rgba, -1, 10d * 35 / 100, 0);  System.out.println("92");
            }
        }else{
            if(Estimate.getBlueAverage() > 240){
                rgba.convertTo(rgba, -1, 10d * 24 / 100, 0);  System.out.println("7");
            }else{
                rgba.convertTo(rgba, -1, 10d * 17 / 100, 0);  System.out.println("7");
            }
        }
        tempMat.release();

        Mat mHSV = new Mat();
        Imgproc.cvtColor(rgba, mHSV, Imgproc.COLOR_RGBA2RGB,3);
        Imgproc.cvtColor(rgba, mHSV, Imgproc.COLOR_RGB2HSV,3);
        List<Mat> hsv_planes = new ArrayList<Mat>(3);
        Core.split(mHSV, hsv_planes);




        Mat channel = hsv_planes.get(0);
        channel = Mat.zeros(mHSV.rows(),mHSV.cols(),CvType.CV_8UC1);
        hsv_planes.set(2,channel);
        Core.merge(hsv_planes,mHSV);

        mHSV.convertTo(mHSV, CvType.CV_8UC1);
        mHSV = Histogram(mHSV);




/*
        Mat clusteredHSV = new Mat();
        mHSV.convertTo(mHSV, CvType.CV_32FC3);
        TermCriteria criteria = new TermCriteria(TermCriteria.EPS + TermCriteria.MAX_ITER,100,0.1);
        Core.kmeans(mHSV, 1, clusteredHSV, criteria, 20, Core.KMEANS_PP_CENTERS);
        Mat hsvImg = new Mat();
        List<Mat> hsvPlanes = new ArrayList<>();
        Mat thresholdImg = new Mat();
        int thresh_type = Imgproc.THRESH_BINARY_INV;
        hsvImg.create(mHSV.size(), CvType.CV_8U);
        Imgproc.cvtColor(mHSV, hsvImg, Imgproc.COLOR_BGR2HSV);
        Core.split(hsvImg, hsvPlanes);
        Imgproc.threshold(hsvPlanes.get(1), thresholdImg, 0 , 200 , thresh_type);
        double threshValue = PreProcessingOperation.getHistAverage(hsvImg, hsvPlanes.get(0));
        Estimate.setSecondHistAverageValue(threshValue);
        System.out.println("After equa " + Estimate.getSecondHistAverageValue());*/



        Imgproc.threshold(mHSV,mHSV, 0, 150, Imgproc.THRESH_BINARY_INV );
        //mHSV.convertTo(mHSV, CvType.CV_8UC1);
        return mHSV;
    }



    public static Mat Histogram(Mat im){

        Mat img = im;

        Mat equ = new Mat();
        img.copyTo(equ);
        //Imgproc.blur(equ, equ, new Size(3, 3));

        Imgproc.cvtColor(equ, equ, Imgproc.COLOR_BGR2YCrCb);
        List<Mat> channels = new ArrayList<Mat>();
        Core.split(equ, channels);
        Imgproc.equalizeHist(channels.get(0), channels.get(0));
        Core.merge(channels, equ);
        Imgproc.cvtColor(equ, equ, Imgproc.COLOR_YCrCb2BGR);

        Mat gray = new Mat();
        Imgproc.cvtColor(equ, gray, Imgproc.COLOR_BGR2GRAY);
        Mat grayOrig = new Mat();
        Imgproc.cvtColor(img, grayOrig, Imgproc.COLOR_BGR2GRAY);
        System.out.println("Histogram work ///");
        return grayOrig;


    }

}