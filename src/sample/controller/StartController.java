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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import sample.model.Filters.FiltersOperations;
import sample.model.PreProcessing.PreProcessingOperation;
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
    public StartController() {

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
    public void setResearchName(){

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

    public void chooseFile(ActionEvent actionEvent) throws java.io.IOException {

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open File");
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files","*.bmp", "*.png", "*.jpg", "*.gif"));
        File file = chooser.showOpenDialog(new Stage());
        if(file != null) {
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