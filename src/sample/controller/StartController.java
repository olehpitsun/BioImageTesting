package sample.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import sample.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.opencv.core.*;
import sample.core.DB;
import sample.model.Filters.FilterColection;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import sample.model.Filters.FiltersOperations;
import sample.model.HistogramEQ;
import sample.model.PreProcessing.PreProcessingOperation;
import sample.model.PreProcessing.StartImageParams;
import sample.model.Segmentation.SegmentationColection;
import sample.model.Segmentation.SegmentationOperations;
import sample.tools.ImageOperations;
import sample.tools.ValidateOperations;
import sample.util.Estimate;
import sample.util.PreProcessingParam;

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
    private Button correctionButton;

    @FXML
    private TextField researchNameField;

    @FXML
    private TextField researchPathField;
    @FXML
    private TextField ContrastField;
    @FXML
    private TextField BrightField;
    @FXML
    private TextField  DilateField;
    @FXML
    private TextField ErodeField;
    @FXML
    private TextField kSizeField;
    @FXML
    private TextField sigmaField;
    @FXML
    private TextField sigmaSpaceField;
    @FXML
    private TextField sigmaColorField;
    //for Segmentation
    @FXML
    private TextField ValueField;
    @FXML
    private TextField MaxValThresholdField;
    @FXML
    private TextField SegSigmaColor;
    @FXML
    private TextField SegSigmaSpace;
    @FXML
    private TextField SegDelta;

    @FXML
    private Label researchName;
    @FXML
    private Label researchPathLabel;
    @FXML
    private Label ContrastLabel;
    @FXML
    private Label BrightLabel;
    @FXML
    private Label DilateLabel;
    @FXML
    private Label ErodeLabel;
    @FXML
    private Label kSizeLabel;
    @FXML
    private Label MaxValThresholdLabel;
    @FXML
    private Label sigmaColorLabel;
    @FXML
    private Label igmaSpaceLabel;
    @FXML
    private Label HistAverLabel;
    @FXML
    private Label HistAverValueLabel;

    @FXML
    protected ImageView preProcImage;
    @FXML
    protected ImageView segmentationImage;

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
    private String contrast;
    private String bright;
    private String dilate;
    private String erode;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public StartController(){

        comboBoxData.add(new FilterColection("1", "Гаусівський"));
        comboBoxData.add(new FilterColection("2", "Білатеральний"));
        comboBoxData.add(new FilterColection("3", "Адаnтивний біл..."));
        comboBoxData.add(new FilterColection("4", "Медіанний"));

        comboBoxSegmentationData.add(new SegmentationColection("1", "Порогова"));
        comboBoxSegmentationData.add(new SegmentationColection("2", "Водорозподілу"));
        comboBoxSegmentationData.add(new SegmentationColection("3", "k-means"));



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
        comboBox.setItems(comboBoxData);
        SegmentationcomboBox.setItems(comboBoxSegmentationData);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleSegmentationComboBoxAction() {
        SegmentationColection selectedSegMetod = SegmentationcomboBox.getSelectionModel().getSelectedItem();
        this.selectSegmentation(selectedSegMetod.getId());
    }

    private void selectSegmentation(String type){
        if(type == "1"){

            this.segType = "1";

            this.ValueField.setDisable(false);
            this.MaxValThresholdField.setDisable(false);

            this.SegSigmaColor.setDisable(true);
            this.SegSigmaSpace.setDisable(true);
        }else if(type == "2"){
            this.segType = "2";

            this.MaxValThresholdField.setDisable(true);
            this.SegDelta.setDisable(false);

            this.ValueField.setDisable(true);
            this.SegSigmaColor.setDisable(true);
            this.SegSigmaSpace.setDisable(true);

        }else if(type == "3"){
            this.segType = "3";

            this.ValueField.setDisable(false);
            this.SegDelta.setDisable(false);

            this.MaxValThresholdField.setDisable(true);
            this.SegSigmaColor.setDisable(true);
            this.SegSigmaSpace.setDisable(true);
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

    public void chooseFile(ActionEvent actionEvent) throws java.io.IOException {

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open File");
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files","*.bmp", "*.png", "*.jpg", "*.gif"));
        File file = chooser.showOpenDialog(new Stage());
        if(file != null) {

            /** return RGB values, average bright**/
            StartImageParams.getStartValues(file);

            //HistogramEQ.atart(file.getAbsolutePath());

            //this.showHisImage(file.getAbsolutePath());


            this.image = Highgui.imread(file.getAbsolutePath(), Highgui.CV_LOAD_IMAGE_COLOR);

            sample.model.Image.setImageMat(this.image);
            Mat newImage = sample.model.Image.getImageMat();
            // show the image
            this.setPreProcImage(newImage);


            saveChangeButton.setVisible(true);
            ContrastLabel.setVisible(true);
            ContrastField.setVisible(true);
            ContrastField.setText("10");

            BrightLabel.setVisible(true);
            BrightField.setVisible(true);
            BrightField.setText("11");

            DilateLabel.setVisible(true);
            DilateField.setVisible(true);
            DilateField.setText("1");

            ErodeLabel.setVisible(true);
            ErodeField.setVisible(true);
            ErodeField.setText("1");

            setPreProcSettingsButton.setVisible(true);

        }
        else
        {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Please Select a File");
            alert.showAndWait();
        }
    }

    /**
     * next 3 functions used to show image: current, after preprocessing, after segmentation
     */
    @FXML
    private void showCurrentImg(){
        this.setPreProcImage(sample.model.Image.getImageMat());
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
        this.correctionButton.setVisible(true);
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
        this.mainImageProcessing();

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

    /**
     * mainImageProcessing function
     *
     */
    private void mainImageProcessing(){

        // called only OpenCV preprocessing functions
        PreProcessingOperation properation = new PreProcessingOperation(this.image,ContrastField.getText(),
                BrightField.getText(), DilateField.getText(),ErodeField.getText());

        // called only OpenCV filtering functions
        FiltersOperations filtroperation = new FiltersOperations(properation.getOutputImage(), this.filterType,
                kSizeField.getText(), sigmaField.getText(), sigmaSpaceField.getText(), sigmaColorField.getText());

        this.setPreProcImage(filtroperation.getOutputImage());//show image after preprocessing and filtering

        //called only segmentation functions
        if(segType =="1"){
            SegmentationOperations segoperation = new SegmentationOperations(filtroperation.getOutputImage(), this.segType,
                    ValueField.getText(), MaxValThresholdField.getText());

            SegmentationOperations segoperation_1 = new SegmentationOperations(segoperation.getOutputImage(), this.segType,
                    ValueField.getText(), MaxValThresholdField.getText());
            this.setSegmentationImage(segoperation_1.getOutputImage());// show image after segmentation
            this.changedimage = segoperation_1.getOutputImage();
        }else{
            SegmentationOperations segoperation = new SegmentationOperations(filtroperation.getOutputImage(), this.segType,
                    ValueField.getText(), MaxValThresholdField.getText());
            this.setSegmentationImage(segoperation.getOutputImage());// show image after segmentation
            this.changedimage = segoperation.getOutputImage();
        }




    }


    @FXML
    public void autoSetting(){
        this.autoPreProcFiltersSegmentationSetting();
    }





























    @FXML
    public void autoPreProcFiltersSegmentationSetting(){

        Mat dst = new Mat();
        Mat testDst = new Mat();

        this.image.copyTo(dst);
        this.image.copyTo(testDst);

        /** use testing parametrs for getting HistAverValue **/
        PreProcessingOperation properation = new PreProcessingOperation(testDst,"1","15", "1", "1");

        SegmentationOperations segoperation = new SegmentationOperations(properation.getOutputImage(), "3",
                "0", "0");
        testDst.release();//clear memory
        properation.getOutputImage().release();


        float tempBrightValue = Estimate.getBrightVal();

        /** for very blue **/
        if(tempBrightValue > 0.9 && tempBrightValue < 2 && Estimate.getBlueAverage() > 130 && Estimate.getBlueAverage() < 185 && Estimate.getRedAverage() < 100){
            System.out.println ("1");

            this.setImageParam(dst, "1","17","2","2");
        }
        else if(tempBrightValue > 0.9 && tempBrightValue < 2 && Estimate.getBlueAverage() > 130 && Estimate.getBlueAverage() < 200 && Estimate.getRedAverage() < 100){
            System.out.println ("6");

            this.setImageParam(dst, "1","15","15","10");
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

        else if(tempBrightValue <= 0.5 && Estimate.getFirstHistAverageValue()>100 && Estimate.getRedAverage() > 100) {
            System.out.println ("24");
            this.setImageParam(dst, "1","2","1","10");

        }

        else {
            this.setImageParam(dst, "1","15","1","1");
            System.out.println ("else");
        }

    }


    public void setImageParam(Mat dst, String contrast, String bright, String dilate, String erode){

        FiltersOperations filtroperation = new FiltersOperations(dst, "4", "9", "", "", "");
        PreProcessingOperation properation = new PreProcessingOperation(filtroperation.getOutputImage(),contrast,bright,
                dilate, erode);

        filtroperation.getOutputImage().release();

        this.setPreProcImage(properation.getOutputImage());


        SegmentationOperations segoperation = new SegmentationOperations(properation.getOutputImage(), "3",
                "0", "0");

        properation.getOutputImage().release();
        //this.setSegmentationImage(segoperation.getOutputImage());

        SegmentationOperations segoperation_1 = new SegmentationOperations(segoperation.getOutputImage(), "1",
                "200", "255");


        this.setSegmentationImage(segoperation_1.getOutputImage());

        segoperation_1.getOutputImage().release();

        Estimate.setFirstHistAverageValue(null);
        Estimate.setSecondHistAverageValue(null);
        System.out.println("------------------------------------------------------------------------------------------");
    }































    public void checkHistogram(){

            System.out.println("Res " + Estimate.checkHistogramValues());

            if(Estimate.checkHistogramValues() == true){
                System.out.println("??????????????????????????????????????????????????????????????????????????????????????????");


                if(Estimate.getSecondHistAverageValue() >12 && Estimate.getSecondHistAverageValue()<=20){
                    this.auto("11", "15","1","10");
                }
                if(Estimate.getSecondHistAverageValue() >20 && Estimate.getSecondHistAverageValue()<32){
                    this.auto("11", "16","3","1");
                }
                if(Estimate.getSecondHistAverageValue() >35 && Estimate.getSecondHistAverageValue()<41){
                    this.auto("11", "15","1","2");
                }
                if(Estimate.getSecondHistAverageValue() >42 && Estimate.getSecondHistAverageValue()<51){
                    this.auto("11", "5","1","1");
                }
                if(Estimate.getSecondHistAverageValue() >52){
                    this.auto("11", "15","1","1");
                }
                else{
                    this.auto("11", "15","1","1");
                }





            }else{
                System.out.println("//////////////////////////////////////////////////////////////////////////////////////////");



                if(Estimate.getSecondHistAverageValue() > 58 && Estimate.getSecondHistAverageValue() < 68){
                    this.auto("11", "5","2","1");
                }
                if(Estimate.getSecondHistAverageValue() >110 && Estimate.getSecondHistAverageValue() <= 140){
                    this.auto("11", "22","2","1");

                }
                if(Estimate.getSecondHistAverageValue() >140 && Estimate.getSecondHistAverageValue() < 149){
                    this.auto("11", "11","1","5");

                }

                if(Estimate.getSecondHistAverageValue() >= 149){
                    this.auto("11", "9","1","1");

                }


                else{
                    this.auto("11", "9","1","1");

                }

            }

    }


    /**
     public void checkHistogram(){

     System.out.println("Res " + Estimate.checkHistogramValues());

     if(Estimate.checkHistogramValues() == true){
     System.out.println("??????????????????????????????????????????????????????????????????????????????????????????");


     if(Estimate.getSecondHistAverageValue() >12 && Estimate.getSecondHistAverageValue()<=20){
     this.auto("11", "15","1","10");
     }
     if(Estimate.getSecondHistAverageValue() >20 && Estimate.getSecondHistAverageValue()<32){
     this.auto("11", "16","3","1");
     }
     if(Estimate.getSecondHistAverageValue() >35 && Estimate.getSecondHistAverageValue()<41){
     this.auto("11", "15","1","2");
     }
     if(Estimate.getSecondHistAverageValue() >42 && Estimate.getSecondHistAverageValue()<51){
     this.auto("11", "5","1","1");
     }
     if(Estimate.getSecondHistAverageValue() >52){
     this.auto("11", "15","1","1");
     }
     else{
     this.auto("11", "15","1","1");
     }





     }else{
     System.out.println("//////////////////////////////////////////////////////////////////////////////////////////");



     if(Estimate.getSecondHistAverageValue() > 58 && Estimate.getSecondHistAverageValue() < 68){
     this.auto("11", "5","2","1");
     }
     if(Estimate.getSecondHistAverageValue() >110 && Estimate.getSecondHistAverageValue() <= 140){
     this.auto("11", "22","2","1");

     }
     if(Estimate.getSecondHistAverageValue() >140 && Estimate.getSecondHistAverageValue() < 149){
     this.auto("11", "11","1","5");

     }

     if(Estimate.getSecondHistAverageValue() >= 149){
     this.auto("11", "9","1","1");

     }


     else{
     this.auto("11", "9","1","1");

     }

     }

     }
     */


    @FXML
    public void auto(String contrast, String bright, String dilate, String erode){

        /*String contrast ="11";
        String bright ="15";
        String dilate ="1";
        String erode="1";*/
        //this.setSegmentationImage(this.image);
        this.image = sample.model.Image.getImageMat();



        PreProcessingOperation properation = new PreProcessingOperation(this.image,contrast,
                bright, dilate,erode);

        // called only OpenCV filtering functions
        FiltersOperations filtroperation = new FiltersOperations(properation.getOutputImage(), "3",
                "27", "", "", "");
        this.setSegmentationImage(filtroperation.getOutputImage());//show image after preprocessing and filtering





        SegmentationOperations segoperation = new SegmentationOperations(filtroperation.getOutputImage(), "3",
                ValueField.getText(), MaxValThresholdField.getText());

        filtroperation.getOutputImage().release();
////////////////////////////////////////////////////////////////////////////////////////////////////////////
        SegmentationOperations segoperation_1 = new SegmentationOperations(segoperation.getOutputImage(), "1",
                "200", "255");

        segoperation.getOutputImage().release();


        //this.checkHistogram();

        SegmentationOperations segoperation_2 = new SegmentationOperations(segoperation_1.getOutputImage(), "1",
                "220", "255");

        segoperation_1.getOutputImage().release();
////////////////////////////////////////////////////////////////////////////////////////////////////////////

        //this.setPreProcImage(segoperation.getOutputImage());
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////




         //this.setSegmentationImage(segoperation_2.getOutputImage());// show image after segmentation

        this.saveChangeImage();

    }






    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Filter ComboBox listener
     */
    @FXML
    private void handleComboBoxAction() {
        FilterColection selectedFilter = comboBox.getSelectionModel().getSelectedItem();
        this.selectFilter(selectedFilter.getId());
    }

    private void selectFilter(String type){
        if(type == "1"){

            this.filterType = "1";
            this.kSizeField.setDisable(false);
            this.sigmaField.setDisable(false);

            this.sigmaColorField.setDisable(true);
            this.sigmaSpaceField.setDisable(true);
        }else if(type == "2"){

            this.filterType = "2";
            this.sigmaField.setDisable(true);
            this.kSizeField.setDisable(false);
            this.sigmaColorField.setDisable(false);
            this.sigmaSpaceField.setDisable(false);

        }else if(type == "3"){

            this.filterType = "3";
            this.kSizeField.setDisable(false);
            this.sigmaField.setDisable(true);
            this.sigmaColorField.setDisable(true);
            this.sigmaSpaceField.setDisable(false);

        }else if(type == "4"){

            this.filterType = "4";
            this.kSizeField.setDisable(false);
            this.sigmaField.setDisable(true);
            this.sigmaColorField.setDisable(true);
            this.sigmaSpaceField.setDisable(true);

        }
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