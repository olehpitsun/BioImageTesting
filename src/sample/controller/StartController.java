package sample.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import org.opencv.core.Point;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import sample.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.opencv.core.*;
//import sample.model.Estimate.Estimate.Psnr;
import sample.model.Estimate.Psnr;
import sample.model.Filters.FilterColection;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import sample.model.Filters.FiltersOperations;
//import sample.model.HistogramEQ;
import sample.model.ImagesCompare;
import sample.model.PixelArray;
import sample.model.PreProcessing.PreProcessingOperation;
import sample.model.PreProcessing.StartImageParams;
import sample.model.Segmentation.SegmentationColection;
import sample.model.Segmentation.SegmentationOperations;
import sample.tools.ImageOperations;
import sample.util.Estimate;
import sample.util.PreProcessingParam;

import javax.imageio.ImageIO;

import static org.opencv.highgui.Highgui.CV_LOAD_IMAGE_COLOR;

public class StartController {

    @FXML
    private Button researchNameButton;
    @FXML
    private Button researchPathButton;
    @FXML
    private Button loadImageButton;
    @FXML
    private Button setPreProcSettingsButton;
    @FXML
    private Button saveChangeButton;
    @FXML
    private Button correctionButton, btnOpenDirectoryChooser;

    @FXML
    private TextField researchNameField;

    @FXML
    private TextField researchPathField;


    @FXML
    private Label researchName;
    @FXML
    private Label researchPathLabel;

    @FXML
    protected ImageView preProcImage;
    @FXML
    protected ImageView segmentationImage;
    @FXML
    protected ImageView originalImage;

    @FXML
    private ComboBox<FilterColection> comboBox;
    @FXML
    private ComboBox<SegmentationColection> SegmentationcomboBox;

    private boolean okClicked = false;
    private ObservableList<FilterColection> comboBoxData = FXCollections.observableArrayList();
    private ObservableList<SegmentationColection> comboBoxSegmentationData = FXCollections.observableArrayList();

    private String filterType;
    private String segType;

    private Stage stage;

    // the JavaFX file chooser
    private FileChooser fileChooser;
    // support variables
    protected Mat image;
    private Stage dialogStage;

    protected List<Mat> planes;
    // Reference to the main application.
    protected Mat changedimage;

    protected Main mainApp;

    private String researchname;
    private String researchPath;

    private String originalImagePath;
    private String generatedImagePath;

    private float meanSquaredError;
    private double psnr;

    @FXML
    private Label mseResLabel;
    @FXML
    private Label psnrResLabel;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public StartController(){
    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param mainApp
     */
    public void setMainApp(Main mainApp) {

        this.mainApp = mainApp;
    }



    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {

        this.fileChooser = new FileChooser();
        this.changedimage = new Mat();
        this.image = new Mat();
        this.planes = new ArrayList<>();

    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }


    @FXML
    public void autoSetting(){


        Mat img1 = Highgui.imread("C:\\Projects\\BioImageTesting\\src\\sample\\1.png", Highgui.CV_LOAD_IMAGE_COLOR);
        Mat img2 = Highgui.imread("C:\\Projects\\BioImageTesting\\src\\sample\\2.png", Highgui.CV_LOAD_IMAGE_COLOR);

        PixelArray pixelArray1 = new PixelArray(img1);
        Mat resImg1Mat = pixelArray1.calculatePixels();


        PixelArray pixelArray2 = new PixelArray(img2);
        Mat resImg2Mat = pixelArray2.calculatePixels();


        double allPixelEtalon = pixelArray1.getAllPixelsCount();
        double C_ki = pixelArray2.getBlackPixelCount();

        double C_ik = pixelArray1.getBlackPixelCount();

        ImagesCompare imagesCompare = new ImagesCompare(img1,img2);
        double rcp = imagesCompare.getRightClassifiedPixelsCount();

        double result = ((C_ki - rcp) / (allPixelEtalon - C_ik)) * 100;

        System.out.println("Extended Incorrectly clasified pixels = " + result);

        //System.out.println("Dlack Pixels 1 " + pixelArray1.getBlackPixelCount());
        //System.out.println("Dlack Pixels 2 " + pixelArray2.getBlackPixelCount());


        /*
        Mat g = new Mat(img2.rows(), img2.cols(), 3);

        byte buff[] = new byte[ (int) (img2.total() * img2.channels())];

        int a,w,q;
        int b;
        double picdata[][] =  new double[img2.rows()][img2.cols()] ;
        double[] temp;

        for (a=0 ; a<img2.rows();a++){
            for (b=0 ; b<img2.cols();b++){
                temp=   img2.get(a, b);
                picdata[a][b]=temp[0];
System.out.println(picdata[a][b]);
                g.put(a,b,picdata[a][b]);
            }
        }*/



        this.setOriginalImage(resImg1Mat);



        /*
        Mat g = img2.clone();
        int size = (int) (img2.total() * img2.channels());
        double[] temp = new double[size];
       //img2.get(0, 0, temp);
        for (int f = 0; f < size; f++) {
            //temp[f] = (temp[f] / 2);
            temp = img2.get(f,f);
            System.out.println(temp[f]);
            //g.put(110,110,temp);

        }*/

       /* Rect rectangle = new Rect(10, 10, img2.cols() - 20, img2.rows() - 20);

        Mat bgdModel = new Mat(); // extracted features for background
        Mat fgdModel = new Mat(); // extracted features for foreground
        Mat source = new Mat(1, 1, CvType.CV_8U, new Scalar(0));

        convertToOpencvValues(img1); // from human readable values to OpenCV values

        int iterCount = 1;
        Imgproc.grabCut(img2, img1, rectangle, bgdModel, fgdModel, iterCount, Imgproc.GC_INIT_WITH_MASK);



        convertToHumanValues(img1); // back to human readable values
        Imgproc.threshold(img1,img1,0,128,Imgproc.THRESH_TOZERO);

        Mat foreground = new Mat(img2.size(), CvType.CV_8UC1, new Scalar(255, 255, 255));
        img2.copyTo(foreground, img1);

        Mat src_gray = new Mat();
        Imgproc.cvtColor(foreground, src_gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.blur(src_gray, src_gray, new Size(3, 3));

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Mat mMaskMat = new Mat();

        Scalar lowerThreshold = new Scalar ( 0, 0, 0 );
        Scalar upperThreshold = new Scalar ( 10, 10, 10 );
        Core.inRange(foreground, lowerThreshold, upperThreshold, mMaskMat);

        Imgproc.findContours(mMaskMat, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        List<Moments> mu = new ArrayList<Moments>(contours.size());
        List<Point> mc = new ArrayList<Point>(contours.size());
        Mat drawing = Mat.zeros( mMaskMat.size(), CvType.CV_8UC3 );

        Rect rect ;





        for( int i = 0; i< contours.size(); i++ ) {
            rect = null;
            rect = Imgproc.boundingRect(contours.get(i));
            Mat crop = foreground.submat(rect);


            Mat rgba = crop;
            Mat tempMat = crop;
            rgba = new Mat(crop.cols(), crop.rows(), CvType.CV_8UC3);
            crop.copyTo(rgba);
            Mat r = crop.clone();

            List<Mat> hsv_planes_temp = new ArrayList<Mat>(3);
            Core.split(tempMat, hsv_planes_temp);





            double contourArea = Imgproc.contourArea(contours.get(i));

            double threshValue1 = PreProcessingOperation.getHistAverage(crop, hsv_planes_temp.get(0));
            System.out.println("thresh " + i + " " + threshValue1 + " contourArea " + contourArea);

            // Core.rectangle(foreground, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 0, 250), 3);
            //Highgui.imwrite("C:\\IMAGES\\test\\"+ iterCount +".jpg", crop);
            iterCount++;
            // Core.putText(foreground, Integer.toString(iterCount) , new Point(rect.x-20,rect.y),
            //    Core.FONT_HERSHEY_TRIPLEX, .7 ,new  Scalar(255,255,255));
            Core.putText(foreground, Double.toString(contourArea) , new Point(rect.x-20,rect.y+10),
                       Core.FONT_HERSHEY_TRIPLEX, .7 ,new  Scalar(255,0,255));
            this.setOriginalImage(foreground);

        }*/


    }

    /**
     * Неправильно класифіковані пікселі
     */
    @FXML
    private void InCorrectrlyClasifiedPixels(){
        Mat img1 = Highgui.imread("C:\\Projects\\BioImageTesting\\src\\sample\\1.png", Highgui.CV_LOAD_IMAGE_COLOR);
        Mat img2 = Highgui.imread("C:\\Projects\\BioImageTesting\\src\\sample\\2.png", Highgui.CV_LOAD_IMAGE_COLOR);

        PixelArray pixelArray1 = new PixelArray(img1);
        Mat resImg1Mat = pixelArray1.calculatePixels();


        PixelArray pixelArray2 = new PixelArray(img2);
        Mat resImg2Mat = pixelArray2.calculatePixels();


        double pixelA_1 = pixelArray1.getBlackPixelCount();
        double pixelA_2 = pixelArray2.getBlackPixelCount();

        double result = ((pixelA_1 - pixelA_2) / pixelA_1) * 100;

        System.out.println("Incorrectly clasified pixels = " + result);

        System.out.println("Dlack Pixels 1 " + pixelArray1.getBlackPixelCount());
        System.out.println("Dlack Pixels 2 " + pixelArray2.getBlackPixelCount());
    }

    @FXML
    private void ExtendedInCorrectrlyClasifiedPixels (){
        Mat img1 = Highgui.imread("C:\\Projects\\BioImageTesting\\src\\sample\\1.png", Highgui.CV_LOAD_IMAGE_COLOR);
        Mat img2 = Highgui.imread("C:\\Projects\\BioImageTesting\\src\\sample\\2.png", Highgui.CV_LOAD_IMAGE_COLOR);

        PixelArray pixelArray1 = new PixelArray(img1);
        Mat resImg1Mat = pixelArray1.calculatePixels();


        PixelArray pixelArray2 = new PixelArray(img2);
        Mat resImg2Mat = pixelArray2.calculatePixels();


        double allPixelEtalon = pixelArray1.getAllPixelsCount();
        double C_ki = pixelArray2.getBlackPixelCount();

        double C_ik = pixelArray1.getBlackPixelCount();

        ImagesCompare imagesCompare = new ImagesCompare(img1,img2);
        double rcp = imagesCompare.getRightClassifiedPixelsCount();

        double result = ((C_ki - rcp) / (allPixelEtalon - C_ik)) * 100;

        System.out.println("Extended Incorrectly clasified pixels = " + result);
    }

    private static void convertToHumanValues(Mat mask) {
        byte[] buffer = new byte[3];
        for (int x = 0; x < mask.rows(); x++) {
            for (int y = 0; y < mask.cols(); y++) {
                mask.get(x, y, buffer);
                int value = buffer[0];
                if (value == Imgproc.GC_BGD) {
                    buffer[0] = (byte) 255 ; // for sure background
                } else if (value == Imgproc.GC_PR_BGD) {
                    buffer[0] = (byte) 170 ; // probably background
                } else if (value == Imgproc.GC_PR_FGD) {
                    buffer[0] = 85; // probably foreground
                } else {
                    buffer[0] = 0; // for sure foreground

                }
                mask.put(x, y, buffer);
            }
        }
    }

    private static void convertToOpencvValues(Mat mask) {
        byte[] buffer = new byte[3];
        for (int x = 0; x < mask.rows(); x++) {
            for (int y = 0; y < mask.cols(); y++) {
                mask.get(x, y, buffer);
                int value = buffer[0];
                if (value >= 0 && value < 64) {
                    buffer[0] = Imgproc.GC_BGD; // for sure background
                } else if (value >= 64 && value < 128) {
                    buffer[0] = Imgproc.GC_PR_BGD; // probably background
                } else if (value >= 128 && value < 192) {
                    buffer[0] = Imgproc.GC_PR_FGD; // probably foreground
                } else {
                    buffer[0] = Imgproc.GC_FGD; // for sure foreground

                }
                mask.put(x, y, buffer);
            }
        }

    }




































    @FXML
    public void setResearchName() throws IOException{


        //HistogramEQ h = new HistogramEQ();

        //h.main1();

        this.researchname = researchNameField.getText();

        researchPathLabel.setVisible(true);
        researchPathField.setVisible(true);
        researchPathButton.setVisible(true);
        this.setResearchPath(this.researchname);
    }

    @FXML
    public void setResearchPath(String rsname){


        File file = new File(rsname);
        String path = file.getAbsolutePath();

        researchPathField.setText(path);
        this.researchPath = path;

    }

    @FXML
    public void setFullPathName(){

        File dir = new File(researchPathField.getText());

        if (!dir.exists()) {
            try {
                dir.mkdirs();

            } catch (SecurityException secEx) {

                // Show the error message.
                Alert alert = new Alert(AlertType.ERROR);
                alert.initOwner(dialogStage);
                alert.setTitle("Помилка");
                alert.setHeaderText("Виникла помилка");
                alert.setContentText("Немає прав доступу");

                alert.showAndWait();
                return;

            }
        }
        loadImageButton.setVisible(true);
    }


    public void showHisImage(String orImage){
        Mat image = Highgui.imread(orImage);

        Mat src = new Mat(image.height(), image.width(), CvType.CV_8UC2);


        Imgproc.cvtColor(image, src, Imgproc.COLOR_RGB2GRAY);



        Vector<Mat> bgr_planes = new Vector<>();
        Core.split(src, bgr_planes);

        MatOfInt histSize = new MatOfInt(256);


        final MatOfFloat histRange = new MatOfFloat(0f, 256f);

        boolean accumulate = false;

        Mat b_hist = new  Mat();

        Imgproc.calcHist(bgr_planes, new MatOfInt(0),new Mat(), b_hist, histSize, histRange, accumulate);

        int hist_w = 512;
        int hist_h = 600;
        long bin_w;
        bin_w = Math.round((double) (hist_w / 256));

        Mat histImage = new Mat(hist_h, hist_w, CvType.CV_8UC1);

        Core.normalize(b_hist, b_hist, 3, histImage.rows(), Core.NORM_MINMAX);



        for (int i = 1; i < 256; i++) {


            Core.line(histImage, new Point(bin_w * (i - 1),hist_h- Math.round(b_hist.get( i-1,0)[0])),
                    new Point(bin_w * (i), hist_h-Math.round(Math.round(b_hist.get(i, 0)[0]))),
                    new  Scalar(255, 0, 0), 2, 8, 0);

        }

        this.setSegmentationImage(histImage);

        //ImageViwer.viewImage(histImage);
    }

    public void chooseDir(ActionEvent event){
        //Button btnOpenDirectoryChooser = new Button();
        //btnOpenDirectoryChooser.setText("Open DirectoryChooser");
        //btnOpenDirectoryChooser.setOnAction(new EventHandler<ActionEvent>() {
           // @Override
           // public void handle(ActionEvent event) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                File selectedDirectory =
                        directoryChooser.showDialog(new Stage());

                if(selectedDirectory == null){
                    //labelSelectedDirectory.setText("No Directory selected");
                }else{
                    System.out.println(selectedDirectory.getAbsolutePath());
                    //labelSelectedDirectory.setText(selectedDirectory.getAbsolutePath());
                }
           // }

    }


    public void chooseFile(ActionEvent actionEvent) throws java.io.IOException {

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open File");
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files","*.bmp", "*.png", "*.jpg", "*.gif", "*.jpeg"));
        File file = chooser.showOpenDialog(new Stage());
        if(file != null) {

           // this.averageColor(file);
            /** return RGB values, average bright**/
            StartImageParams.getStartValues(file);

            //HistogramEQ.atart(file.getAbsolutePath());

            //this.showHisImage(file.getAbsolutePath());


            this.image = Highgui.imread(file.getAbsolutePath(), CV_LOAD_IMAGE_COLOR);

            sample.model.Image.setImageMat(this.image);

            originalImagePath = file.getAbsolutePath();
            Mat newImage = sample.model.Image.getImageMat();
            // show the image
            this.setOriginalImage(newImage);

        }
        else
        {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Please Select a File");
            alert.showAndWait();
        }
    }

    public static void averageColor(File file)throws IOException {
        BufferedImage bi = ImageIO.read(file);

        for (int i = 0; i < 256; i++) {}

        int x0 =0;
        int y0 = 0;
        int w = bi.getWidth();
        int h = bi.getHeight();

        int x1 = x0 + w;
        int y1 = y0 + h;
        long sumr = 0, sumg = 0, sumb = 0;
        for (int x = x0; x < x1; x++) {
            for (int y = y0; y < y1; y++) {
                Color pixel = new Color(bi.getRGB(x, y));
                sumr += pixel.getRed();
                sumg += pixel.getGreen();
                sumb += pixel.getBlue();
            }
        }
        int num = w * h;
        System.out.println(sumr/ num);
        System.out.println(sumg/ num);
        System.out.println(sumb/ num);

        double y = (299 * sumr + 587 * sumg + 114 * sumb) / 1000;
        System.out.println("Bright = " + y);
        //return new Color(sumr / num, sumg / num, sumb / num);
    }

    /**
     * next 3 functions used to show image: current, after preprocessing, after segmentation
     */
    @FXML
    private void showCurrentImg(){
        this.setPreProcImage(sample.model.Image.getImageMat());
    }

    private void setOriginalImage(Mat dst ){
        this.originalImage.setImage(ImageOperations.mat2Image(dst));
        this.originalImage.setFitWidth(450.0);
        this.originalImage.setFitHeight(450.0);
        this.originalImage.setPreserveRatio(true);
    }

    private void setPreProcImage(Mat dst ){
        this.preProcImage.setImage(ImageOperations.mat2Image(dst));
        this.preProcImage.setFitWidth(450.0);
        this.preProcImage.setFitHeight(450.0);
        this.preProcImage.setPreserveRatio(true);
    }

    private void setSegmentationImage(Mat dst ){
        this.segmentationImage.setImage(ImageOperations.mat2Image(dst));
        this.segmentationImage.setFitWidth(450.0);
        this.segmentationImage.setFitHeight(450.0);
        this.segmentationImage.setPreserveRatio(true);
    }

    /**
     * Called when the user clicks ok.
     */
    @FXML
    public void handleOk() throws ClassNotFoundException {

        PreProcessingParam prparam = new PreProcessingParam();
        String errorMessage = "";

        if (researchNameField.getText() == null || researchNameField.getText().length() == 0) {
            errorMessage += "Заповніть коректно поле Назва досліду!\n";
        }else{
            prparam.setResearchName(researchNameField.getText());
        }

        if (researchPathField.getText() == null || researchPathField.getText().length() == 0) {
            errorMessage += "Заповніть коректно шлях до теки!\n";
        }else{
            prparam.setResearchPath(researchPathField.getText());
        }
        // called main function for image processing

        if (errorMessage.length() != 0) {
            // Show the error message.
            Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Заповніть коректно поля");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            //return false;
        }
    }



    @FXML
    public void autoPreProcFiltersSegmentationSetting(){

        Mat dst = new Mat();
        Mat testDst = new Mat();

        this.image.copyTo(dst);
        this.image.copyTo(testDst);

        /** use testing parametrs for getting HistAverValue **/

        //PreProcessingOperation properation = new PreProcessingOperation(testDst,"1","15", "1", "1");
/*
        SegmentationOperations segoperation = new SegmentationOperations(testDst, "3",
                "0", "0");
        testDst.release();//clear memory
        //properation.getOutputImage().release();


        float tempBrightValue = Estimate.getBrightVal();



        if(tempBrightValue > 0.9 && tempBrightValue < 2 && Estimate.getBlueAverage() < 110 && Estimate.getRedAverage() > 140){
            System.out.println ("38");
            this.setImageParam(dst, "1","25","1","2");
        }

        else if(tempBrightValue > 0.9 && tempBrightValue < 2 && Estimate.getBlueAverage() < 110 && Estimate.getRedAverage() > 135){
            System.out.println ("37");
            this.setImageParam(dst, "1","25","1","2");
        }



        else if(tempBrightValue > 0.9 && tempBrightValue < 2 && Estimate.getBlueAverage() < 110 && Estimate.getRedAverage() > 115){
            System.out.println ("36");
            this.setImageParam(dst, "1","10","1","2");
        }

        else if(tempBrightValue > 0.9 && tempBrightValue < 2 && Estimate.getBlueAverage() < 130 && Estimate.getRedAverage() > 90){
            System.out.println ("35");
            this.setImageParam(dst, "1","20","2","2");
        }

        else if(tempBrightValue < 0.9 && Estimate.getBlueAverage() > 130 && Estimate.getRedAverage() < 100
                ){
            System.out.println ("36");

            this.setImageParam(dst, "1","10","2","2");
        }

        else if(tempBrightValue > 0.9 && tempBrightValue < 2 && Estimate.getBlueAverage() > 130 && Estimate.getBlueAverage() < 185 && Estimate.getRedAverage() < 90){
            System.out.println ("29");

            this.setImageParam(dst, "1","27","2","2");
        }

        else if(tempBrightValue > 0.9 && tempBrightValue < 2 && Estimate.getBlueAverage() > 130 && Estimate.getBlueAverage() < 185 && Estimate.getRedAverage() < 100){
            System.out.println ("1");

            this.setImageParam(dst, "1","17","2","2");
        }
        else if(tempBrightValue > 0.9 && tempBrightValue < 2 && Estimate.getBlueAverage() > 130 && Estimate.getBlueAverage() < 200 && Estimate.getRedAverage() < 100){
            System.out.println ("6");

            this.setImageParam(dst, "1","15","10","10");
        }

        else if(tempBrightValue > 1.5 && tempBrightValue < 2 && Estimate.getBlueAverage() > 130 && Estimate.getRedAverage() > 220){
            System.out.println ("13");

            this.setImageParam(dst, "1","16","1","2");

        }

        else if(tempBrightValue > 1.5 && tempBrightValue < 2 && Estimate.getBlueAverage() > 130 && Estimate.getRedAverage() > 100){
            System.out.println ("23");

            this.setImageParam(dst, "1","13","1","5");

        }

        else if(tempBrightValue > 1.1 && tempBrightValue < 2 && Estimate.getBlueAverage() > 130 && Estimate.getRedAverage() > 160){
            System.out.println ("16");

            this.setImageParam(dst, "1","29","23","1");

        }

        else if(tempBrightValue > 1.1 && tempBrightValue < 2 && Estimate.getBlueAverage() > 130 && Estimate.getRedAverage() > 140){
            System.out.println ("21");

            this.setImageParam(dst, "1","19","15","1");

        }

        else if(tempBrightValue > 1.1 && tempBrightValue < 2 && Estimate.getBlueAverage() > 130 && Estimate.getRedAverage() > 100){
            System.out.println ("11");

            this.setImageParam(dst, "1","19","1","1");

        }

        else if(tempBrightValue > 1 && tempBrightValue < 2 && Estimate.getBlueAverage() > 130 && Estimate.getRedAverage() > 100){
            System.out.println ("31");

            this.setImageParam(dst, "1","22","1","1");

        }

        else if(tempBrightValue > 1 && tempBrightValue < 2 && Estimate.getBlueAverage() > 130 && Estimate.getRedAverage() > 130){
            System.out.println ("30");

            this.setImageParam(dst, "1","10","1","3");

        }

        else if(tempBrightValue > 0.9 && tempBrightValue < 2 && Estimate.getBlueAverage() > 120 && Estimate.getRedAverage() > 100
                && Estimate.getSecondHistAverageValue() > 140){
            System.out.println ("32");

            this.setImageParam(dst, "1","22","1","3");

        }

        else if(tempBrightValue > 0.9 && tempBrightValue < 2 && Estimate.getBlueAverage() > 130 && Estimate.getRedAverage() > 100){
            System.out.println ("2");

            this.setImageParam(dst, "1","15","1","3");

        }


        else if(tempBrightValue > 0.9 && tempBrightValue < 2 && Estimate.getBlueAverage() < 130
                && Estimate.getFirstHistAverageValue() >100){
            System.out.println ("20");

            this.setImageParam(dst, "1","20","1","1");

        }

        else if(tempBrightValue > 0.9 && tempBrightValue < 2 && Estimate.getBlueAverage() < 90 && Estimate.getRedAverage() > 130
                && Estimate.getSecondHistAverageValue() >110){
            System.out.println ("17");

            this.setImageParam(dst, "1","11","1","9");

        }


        else if(tempBrightValue > 0.9 && tempBrightValue < 2 && Estimate.getBlueAverage() < 100 && Estimate.getRedAverage() > 130
                && Estimate.getSecondHistAverageValue() >45){
            System.out.println ("27");

            this.setImageParam(dst, "1","18","9","1");
        }

        else if(tempBrightValue > 0.9 && tempBrightValue < 2 && Estimate.getBlueAverage() < 130 && Estimate.getRedAverage() > 130
                && Estimate.getSecondHistAverageValue() >165){
            System.out.println ("22");

            this.setImageParam(dst, "1","32","17","1");

        }

        else if(tempBrightValue > 0.9 && tempBrightValue < 2 && Estimate.getBlueAverage() < 130 && Estimate.getRedAverage() > 130
                && Estimate.getSecondHistAverageValue() >110){
            System.out.println ("15");

            this.setImageParam(dst, "1","33","8","1");

        }


        else if(tempBrightValue > 0.9 && tempBrightValue < 2 && Estimate.getBlueAverage() < 130
                && Estimate.getFirstHistAverageValue() >55){
            System.out.println ("29");

            this.setImageParam(dst, "1","17","6","1");

        }

        else if(tempBrightValue > 0.9 && tempBrightValue < 2 && Estimate.getBlueAverage() < 130
                && Estimate.getFirstHistAverageValue() >55){
            System.out.println ("28");

            this.setImageParam(dst, "1","18","6","1");

        }

        else if(tempBrightValue > 0.9 && tempBrightValue < 2 && Estimate.getBlueAverage() < 130
                && Estimate.getFirstHistAverageValue() >20){
            System.out.println ("18");

            this.setImageParam(dst, "1","18","1","1");

        }


        else if(tempBrightValue > 0.9 && tempBrightValue < 2 && Estimate.getBlueAverage() < 130
                && Estimate.getSecondHistAverageValue() >20){
            System.out.println ("3");

            this.setImageParam(dst, "1","21","3","1");

        }


        else if(tempBrightValue <= 0.9 && Estimate.getFirstHistAverageValue() < 100 && Estimate.getRedAverage() < 80) {
            System.out.println ("8");
            this.setImageParam(dst, "1","9","25","11");

        }

        else if(tempBrightValue <= 0.9 && Estimate.getFirstHistAverageValue() < 100 && Estimate.getRedAverage() >= 110) {
            System.out.println ("25");
            this.setImageParam(dst, "1","13","1","3");//6-br

        }

        else if(tempBrightValue <= 0.9 && Estimate.getFirstHistAverageValue() < 100 && Estimate.getRedAverage() >= 80) {
            System.out.println ("9");
            this.setImageParam(dst, "1","8","23","1");

        }

        else if(tempBrightValue <= 0.9 && Estimate.getFirstHistAverageValue()>100 && Estimate.getRedAverage() < 80) {
            System.out.println ("4");
            this.setImageParam(dst, "1","9","25","11");

        }

        else if(tempBrightValue <= 0.9 && tempBrightValue >= 0.5 && Estimate.getFirstHistAverageValue()>100 && Estimate.getRedAverage() > 100) {
            System.out.println ("19");
            this.setImageParam(dst, "1","15","14","11");

        }


        else if(tempBrightValue <= 0.5 && Estimate.getRedAverage() > 170 && Estimate.getRedAverage()<190 && Estimate.getBlueAverage()>205
                && Estimate.getBlueAverage()<225) {
            System.out.println ("40");
            //thresholdSegmentation(dst);
            this.setImageParam(dst, "1","20","1","1");

        }

        else if(tempBrightValue <= 0.5 && Estimate.getRedAverage() > 140 && Estimate.getBlueAverage()>200
                && Estimate.getFirstHistAverageValue() > 120) {
            System.out.println ("41");
            thresholdSegmentation(dst);
            //this.setImageParam(dst, "1","20","1","1");

        }

        else if(tempBrightValue <= 0.5 && Estimate.getFirstHistAverageValue()>130 && Estimate.getRedAverage() > 170 && Estimate.getBlueAverage()>170) {
            System.out.println ("24");
            thresholdSegmentation(dst);
            //this.setImageParam(dst, "1","1","1","1");

        }



        else if(tempBrightValue > 0.8 && tempBrightValue < 2 && Estimate.getBlueAverage() > 100 && Estimate.getRedAverage() > 100){
            System.out.println ("33");

            this.setImageParam(dst, "1","20","5","1");

        }



        else {
            this.setImageParam(dst, "1","15","1","1");
            System.out.println ("else");
        }*/

        this.setImageParam(dst);

    }


    /**
     * compare 2 images:
     * original and after filtering
     */
    private void compareImages(){
        //System.out.println(originalImagePath);
        //System.out.println(generatedImagePath);
        this.meanSquaredError = Psnr.getmeanSquaredError(originalImagePath, generatedImagePath);
        this.psnr = Psnr.getPsnr(this.meanSquaredError);

        mseResLabel.setText(String.valueOf(this.meanSquaredError));
        psnrResLabel.setText(String.valueOf(this.psnr));
        //System.out.println(this.meanSquaredError );
        //System.out.println(this.psnr );

    }

    public void Histogram() throws IOException{

        Mat img = this.image;

        Mat equ = new Mat();
        img.copyTo(equ);
        Imgproc.blur(equ, equ, new Size(3, 3));

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

        this.changedimage = grayOrig;

        //this.setSegmentationImage(this.thresholding(grayOrig));

        FiltersOperations filtroperation = new FiltersOperations(grayOrig, "4", "9", "", "", "");

        SegmentationOperations segoperation = new SegmentationOperations(filtroperation.getOutputImage(), "3",
                "0", "0");

        filtroperation.getOutputImage().release();
        //this.setSegmentationImage(segoperation.getOutputImage());

        SegmentationOperations segoperation_1 = new SegmentationOperations(segoperation.getOutputImage(), "1",
                "200", "255");


        this.setSegmentationImage(segoperation.getOutputImage());
        String g = "C:\\Projects\\BioImageTesting\\myalgorithm.png";
        try {
            Highgui.imwrite(g, segoperation.getOutputImage());
        }catch (Exception e){
            System.out.print(e);
        }

        segoperation_1.getOutputImage().release();

        Estimate.setFirstHistAverageValue(null);
        Estimate.setSecondHistAverageValue(null);
        System.out.println("------------------------------------------------------------------------------------------");
    }

    /**
     *
     * @param dst
     */
    public void setImageParam(Mat dst ){

        FiltersOperations filtroperation = new FiltersOperations(dst, "4", "5", "", "", ""); // медіанний фільтр

        System.out.println("PSNR " + Imgproc.PSNR( filtroperation.getOutputImage(),dst));
        FiltersOperations filtersOperations_1;
        if(Imgproc.PSNR( filtroperation.getOutputImage(),dst) < 30){
            filtersOperations_1 = filtroperation;
        }else{
            filtersOperations_1 = new FiltersOperations(filtroperation.getOutputImage(), "1", "3",
                    "1.0", "", "" ); // гаусовий фільтр
        }
        dst.release();/** очистка памяті **/

        this.setPreProcImage(filtersOperations_1.getOutputImage());// вивід на екран



        //PreProcessingOperation properation = new PreProcessingOperation(filtroperation.getOutputImage(),contrast,"10",
          //      "0", "3");



        SegmentationOperations segoperation = new SegmentationOperations(filtersOperations_1.getOutputImage(), "3",
                "0", "0");

        filtroperation.getOutputImage().release(); /** очистка памяті **/
        filtersOperations_1.getOutputImage().release(); /** очистка памяті **/

        SegmentationOperations segoperation_1 = new SegmentationOperations(segoperation.getOutputImage(), "1",
                "250", "255");

        segoperation.getOutputImage().release(); /** очистка памяті **/

        this.setSegmentationImage(segoperation_1.getOutputImage());


        Estimate.setFirstHistAverageValue(null);
        Estimate.setSecondHistAverageValue(null);
        System.out.println("------------------------------------------------------------------------------------------");
    }



    private void thresholdSegmentation(Mat dst){

/*
        FiltersOperations filtroperation = new FiltersOperations(dst, "4", "9", "", "", "");
        PreProcessingOperation properation = new PreProcessingOperation(filtroperation.getOutputImage(),"1.1","10",
                "1", "1");

        filtroperation.getOutputImage().release();*/

        SegmentationOperations segoperation_1 = new SegmentationOperations(dst, "1",
                "0", "10");

       // properation.getOutputImage().release();

        this.setSegmentationImage(segoperation_1.getOutputImage());

        segoperation_1.getOutputImage().release();
    }



































    public boolean isOkClicked() {
        return okClicked;
    }





    @FXML
    private void handleDBConnect() {

        boolean okClicked = mainApp.showDbConnectDialog();
        if (okClicked) {
            mainApp.startProcessing();
            //showPersonDetails(selectedPerson);
        }
    }

    @FXML
    private void saveChangeImage(){
        sample.model.Image.setImageMat(this.changedimage);
        this.image = sample.model.Image.getImageMat();
    }

    @FXML
    private void correctionSegmentation(){
        Mat frame = this.image;
        //Imgproc.dilate(frame, frame, new Mat(), new Point(-1, -1), 1);

        Mat hsvImg = new Mat();
        List<Mat> hsvPlanes = new ArrayList<>();
        Mat thresholdImg = new Mat();

        int thresh_type = Imgproc.THRESH_BINARY_INV;
        //if (this.inverse.isSelected())
        // thresh_type = Imgproc.THRESH_BINARY;

        // threshold the image with the average hue value
        hsvImg.create(frame.size(), CvType.CV_8U);
        Imgproc.cvtColor(frame, hsvImg, Imgproc.COLOR_BGR2HSV);
        Core.split(hsvImg, hsvPlanes);

        // get the average hue value of the image
        double threshValue = PreProcessingOperation.getHistAverage(hsvImg, hsvPlanes.get(0));
        System.out.print(threshValue);
        //Imgproc.threshold(hsvPlanes.get(0), thresholdImg, 0.1, 255 , thresh_type);
        Imgproc.threshold(thresholdImg, thresholdImg, 1, 179, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);

        Imgproc.erode(thresholdImg, thresholdImg, new Mat(), new Point(-1, -1), 1);

        //Imgproc.blur(thresholdImg, thresholdImg, new Size(9, 9));



        // dilate to fill gaps, erode to smooth edges
        Imgproc.dilate(thresholdImg, thresholdImg, new Mat(), new Point(-1, -1), 3);
        //Imgproc.erode(thresholdImg, thresholdImg, new Mat(), new Point(-1, -1), 3);

        Size s = new Size(31, 31);
        Imgproc.GaussianBlur(thresholdImg, thresholdImg, s, 2.0);

        //Imgproc.threshold(thresholdImg, thresholdImg, 0.1, 255, Imgproc.THRESH_BINARY);
        Imgproc.threshold(thresholdImg, thresholdImg, 0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);

        // create the new image
        Mat foreground = new Mat(frame.size(), CvType.CV_8UC3, new Scalar(255, 255, 255));
        frame.copyTo(foreground, thresholdImg);
        Imgproc.medianBlur(foreground, foreground, 9);

        this.setSegmentationImage(foreground);// show image after segmentation
        //this.changedimage = foreground;
    }
/*
    @FXML
    public void saveChangeFile()throws
            ClassNotFoundException,SQLException {

        Connection con;
        Statement stmt;
        ResultSet rs;
        Connection c = DB.connect("127.0.0.1","3306","ki","root","oleh123");
        stmt = c.createStatement();

        String query = "select id, name, surname from users";

        rs = stmt.executeQuery(query);

        while (rs.next()) {
            int id = rs.getInt(1);
            String name = rs.getString(2);
            String surname = rs.getString(3);
            System.out.printf("id: %d, name: %s, surname: %s %n", id, name, surname);
        }
        c.close();
        /*this.image= this.changedimage ;
        sample.model.Image.setImageMat(this.image);
*/
    /*}*/

    /*
    private void rangeValues(String field, String fieldType){
        String[] rangeValue = field.split("-");

        if(rangeValue.length == 3){
            this.rangeFlag = fieldType;
            this.firstValue = Integer.parseInt(rangeValue[0]);
            this.lastValue = Integer.parseInt(rangeValue[1]);
            this.step = Integer.parseInt(rangeValue[2]);
        }
        else {

            if(fieldType.compareTo("Contrast") == 0) {
                this.contrast = rangeValue[0];
            }
        }
    }*/

}