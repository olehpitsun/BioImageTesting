package sample.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import sample.Main;
import sample.model.Estimate.Psnr;
import sample.model.Filters.FilterColection;
import sample.model.Filters.FiltersOperations;
import sample.model.Image;
import sample.model.PreProcessing.PreProcessingOperation;
import sample.model.Segmentation.SegmentationOperations;
import sample.tools.ImageOperations;
import sample.util.Estimate;
import sample.util.PreProcessingParam;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Oleh7 on 2/21/2016.
 */
public class PreProcessingController {


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
    private Label mseResLabel;
    @FXML
    private Label psnrResLabel;

    private Stage stage;
    private Stage dialogStage;
    protected List<Mat> planes;

    @FXML
    protected ImageView originalImage;
    @FXML
    protected ImageView preprocImage;

    @FXML
    private ComboBox<FilterColection> comboBox;
    private ObservableList<FilterColection> comboBoxData = FXCollections.observableArrayList();

    private String filterType;

    // the JavaFX file chooser
    private FileChooser fileChooser;
    // support variables
    protected Mat image;
    protected Mat generatedImage;
    protected Main mainApp;

    private String originalImagePath;
    private String generatedImagePath;

    private float meanSquaredError;
    private double psnr;

    public PreProcessingController(){

        comboBoxData.add(new FilterColection("1", "Гаусівський"));
        comboBoxData.add(new FilterColection("2", "Білатеральний"));
        comboBoxData.add(new FilterColection("3", "Адаnтивний біл..."));
        comboBoxData.add(new FilterColection("4", "Медіанний"));
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
        this.image = new Mat();
        this.planes = new ArrayList<>();
        comboBox.setItems(comboBoxData);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }


    /**
     * Called when the user clicks ok.
     */
    @FXML
    public void handleOk() throws ClassNotFoundException {

        PreProcessingParam prparam = new PreProcessingParam();
        String errorMessage = "";
        System.out.println(ContrastField.getText());

        this.mainImageProcessing();

        // called main function for image processing
        //this.mainImageProcessing();

        if (errorMessage.length() != 0) {
            // Show the error message.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Заповніть коректно поля");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            //return false;
        }
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
           // Mat newImage = sample.model.Image.getImageMat();

            this.setOriginalImage(this.image);
            originalImagePath = file.getAbsolutePath();
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Please Select a File");
            alert.showAndWait();
        }
    }


    @FXML
    private void saveChangeImage(){
        sample.model.Image.setImageMat(this.image);
        this.image = sample.model.Image.getImageMat();
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

    private void setOriginalImage(Mat dst ){
        this.originalImage.setImage(ImageOperations.mat2Image(dst));
        this.originalImage.setFitWidth(450.0);
        this.originalImage.setFitHeight(450.0);
        this.originalImage.setPreserveRatio(true);
    }

    private void setPreProcImage(Mat dst ){
        this.preprocImage.setImage(ImageOperations.mat2Image(dst));
        this.preprocImage.setFitWidth(450.0);
        this.preprocImage.setFitHeight(450.0);
        this.preprocImage.setPreserveRatio(true);
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

        generatedImage = filtroperation.getOutputImage();// set generated image in Mat format

        // save image on disk
        String path ="";
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        Highgui.imwrite( path + timeStamp + ".png", generatedImage);
        generatedImagePath = path + timeStamp + ".png";

        this.compareImages();
    }

    private void compareImages(){

        System.out.println(originalImagePath);
        System.out.println(generatedImagePath);

        this.meanSquaredError = Psnr.getmeanSquaredError(originalImagePath, generatedImagePath);
        this.psnr = Psnr.getPsnr(this.meanSquaredError);


        mseResLabel.setText(String.valueOf(this.meanSquaredError));
        psnrResLabel.setText(String.valueOf(this.psnr));


        System.out.println(this.meanSquaredError );
        System.out.println(this.psnr );

    }



}
