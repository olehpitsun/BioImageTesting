package sample.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import org.opencv.highgui.Highgui;
import sample.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
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

import sample.model.PreProcessing.PreProcessingOperation;
import sample.model.Segmentation.SegmentationColection;
import sample.tools.ImageOperations;
import sample.tools.ValidateOperations;
import sample.util.PreProcessingParam;

public class StartController {

    @FXML
    private Button PrProcButton;
    @FXML
    private Button FilterButton;
    @FXML
    private Button SegmentationButton;
    @FXML
    private Button ObjectButton;

    @FXML
    private Button researchNameButton;

    @FXML
    private Button ContrastRangeButton;

    @FXML
    private Button researchPathButton;

    @FXML
    private Button loadImageButton;
    @FXML
    private Button setPreProcSettingsButton;

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


    @FXML
    private Label pathToDirLabel;
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
    private Label sigmaLabel;
    @FXML
    private Label sigmaColorLabel;
    @FXML
    private Label igmaSpaceLabel;


    @FXML
    private ComboBox<FilterColection> comboBox;
    @FXML
    private ComboBox<SegmentationColection> SegmentationcomboBox;

    @FXML
    private ImageView histogram;

    private boolean okClicked = false;
    private ObservableList<FilterColection> comboBoxData = FXCollections.observableArrayList();

    private ObservableList<SegmentationColection> comboBoxSegmentationData = FXCollections.observableArrayList();

    private String filterType;

    @FXML
    private TextField SegkSizeField;
    @FXML
    private TextField SegSigma;
    @FXML
    private TextField SegSigmaColor;
    @FXML
    private TextField SegSigmaSpace;
    @FXML
    private TextField SegDelta;

    @FXML
    protected TextField LaplacianParametrField;
    private String segType;


    @FXML
    protected ImageView preProcImage;
    @FXML
    protected ImageView segmentationImage;

    private Stage stage;
    private Mat logo;

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

    private String rangeFlag;
    private Integer firstValue;
    private Integer lastValue;
    private Integer step;




    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public StartController() {

        comboBoxData.add(new FilterColection("1", "Гаусівський"));
        comboBoxData.add(new FilterColection("2", "Білатеральний"));
        comboBoxData.add(new FilterColection("3", "Адаnтивний біл..."));
        comboBoxData.add(new FilterColection("4", "Медіанний"));

        comboBoxSegmentationData.add(new SegmentationColection("1", "Кенні"));
        comboBoxSegmentationData.add(new SegmentationColection("2", "Собеля"));
        comboBoxSegmentationData.add(new SegmentationColection("3", "Лапласіан"));

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

            this.SegkSizeField.setDisable(false);
            this.SegSigma.setDisable(true);

            this.SegSigmaColor.setDisable(true);
            this.SegSigmaSpace.setDisable(true);
        }else if(type == "2"){
            this.segType = "2";

            this.SegSigma.setDisable(true);
            this.SegDelta.setDisable(false);

            this.SegkSizeField.setDisable(true);
            this.SegSigmaColor.setDisable(true);
            this.SegSigmaSpace.setDisable(true);

        }else if(type == "3"){
            this.segType = "3";

            this.SegkSizeField.setDisable(false);
            this.SegDelta.setDisable(false);

            this.SegSigma.setDisable(true);
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
        // set a fixed width
        this.preProcImage.setFitWidth(450.0);
        this.preProcImage.setFitHeight(450.0);
        // preserve image ratio
        this.preProcImage.setPreserveRatio(true);
    }

    private void setSegmentationImage(Mat dst ){

        this.segmentationImage.setImage(ImageOperations.mat2Image(dst));
        // set a fixed width
        this.segmentationImage.setFitWidth(450.0);
        this.segmentationImage.setFitHeight(450.0);
        // preserve image ratio
        this.segmentationImage.setPreserveRatio(true);
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Called when the user clicks ok.
     */
    @FXML
    public void handleOk() throws ClassNotFoundException {

        isInputValid();
        //if (isInputValid()) {
            //this.setPreProcSettings(researchNameField.getText(), researchPathField.getText(), ContrastField.getText(),
                   // BrightField.getText(), DilateField.getText(), ErodeField.getText());


            //this.ImagePreprocessing();
        //}
    }

    /**
     * Called when the user clicks cancel@FXML
     * private void handleCancel() {
     * dialogStage.close();
     * }
     * <p>
     * /**
     * Validates the user input in the text fields.
     *
     * @return true if the input is valid
     */
    private boolean isInputValid() {
        String errorMessage = "";
        PreProcessingParam prparam = new PreProcessingParam();

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

        if (ContrastField.getText().length() == 0 || ValidateOperations.isInt(ContrastField.getText()) == false) {
            prparam.setContrast(null);
        }else{
            prparam.setContrast(ContrastField.getText());
        }

        if (BrightField.getText().length() == 0 || ValidateOperations.isInt(BrightField.getText()) == false) {
            prparam.setBright(null);
        }else{
            prparam.setBright(BrightField.getText());
        }

        if ( DilateField.getText().length() == 0 || ValidateOperations.isInt(DilateField.getText()) == false) {
            prparam.setDilate(null);
        }else{
            prparam.setDilate(DilateField.getText());
        }

        if (ErodeField.getText().length() == 0 || ValidateOperations.isInt(ErodeField.getText()) == false) {
            prparam.setErode(null);
        }else{
            prparam.setErode(ErodeField.getText());
        }

        System.out.println(prparam.getContrast());
        System.out.println(prparam.getBright());
        System.out.println(prparam.getDilate());
        System.out.println(prparam.getErode());

        PreProcessingOperation properation = new PreProcessingOperation(this.image,prparam.getContrast(),
                prparam.getBright(),
                prparam.getDilate(),prparam.getErode());



        //this.ImagePreprocessing();

        this.setPreProcImage(properation.getOutputImage());

        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Show the error message.
            Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Заповніть коректно поля");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }






    private void ImagePreprocessing(){

        Mat preProcImage = new Mat();

        // Contrat Unit
        preProcImage = this.image;


        //preProcImage = PreProcessing.contrast(preProcImage,Integer.parseInt(this.contrast));

        //Bright Unit
        //preProcImage = PreProcessing.contrast(preProcImage,Integer.parseInt(this.bright));

        //Dilate Unit
        //preProcImage = PreProcessing.Dilate(preProcImage,Integer.parseInt(this.dilate));

        //Erode Unit
        //preProcImage = PreProcessing.Erode(preProcImage, Integer.parseInt(this.erode));

        /*if(this.filterType == "1") {
            preProcImage = Filters.gaussianBlur(preProcImage, Integer.parseInt(kSizeField.getText()),
                    Double.parseDouble(sigmaField.getText()) );
        }
        if(this.filterType == "2"){
            preProcImage = Filters.bilateralFilter(preProcImage, Integer.parseInt(kSizeField.getText()),
                    Double.parseDouble(sigmaSpaceField.getText()), Double.parseDouble(sigmaColorField.getText()));
        }
        if(this.filterType == "3"){
            int sP = Integer.valueOf(sigmaSpaceField.getText());
            preProcImage = Filters.adaptiveBilateralFilter(preProcImage, Integer.parseInt(kSizeField.getText()), sP);
        }
        if(this.filterType == "4"){
            preProcImage = Filters.medianBlur(preProcImage,Integer.parseInt(kSizeField.getText()));
        }
*/


        ///////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////
/*
        Mat mHSV = new Mat();

        Imgproc.cvtColor(preProcImage, mHSV, Imgproc.COLOR_RGBA2RGB,3);
        Imgproc.cvtColor(preProcImage, mHSV, Imgproc.COLOR_RGB2HSV,3);
        List<Mat> hsv_planes = new ArrayList<Mat>(3);
        Core.split(mHSV, hsv_planes);



        Mat channel = hsv_planes.get(2);
        channel = Mat.zeros(mHSV.rows(),mHSV.cols(),CvType.CV_8UC1);
        hsv_planes.set(2,channel);
        Core.merge(hsv_planes,mHSV);



        Mat clusteredHSV = new Mat();
        mHSV.convertTo(mHSV, CvType.CV_32FC3);
        TermCriteria criteria = new TermCriteria(TermCriteria.EPS + TermCriteria.MAX_ITER,100,0.1);
        Core.kmeans(mHSV, 2, clusteredHSV, criteria, 10, Core.KMEANS_PP_CENTERS);
*/


        if(this.segType == "1") {
            //this.cannyDetection(Integer.parseInt(kSizeField.getText()));
        }
        if(this.segType == "2"){
            //this.SobelDetection(Integer.parseInt(delta.getText()));
        }
        if(this.segType == "3"){
            //this.Laplacian(Integer.parseInt(kSizeField.getText()), Integer.parseInt(delta.getText()));
        }

        this.changedimage = preProcImage;
        this.setPreProcImage(this.changedimage);
        //this.setSegmentationImage(mHSV);
    }


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