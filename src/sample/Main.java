package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.opencv.core.Core;

import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import sample.controller.StartController;
import sample.view.*;

public class Main extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;


    /**
     * Oleh
     * Constructor
     */
    public Main() {

    }


    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Тестування біо зображень");

        // Set the application icon.
        this.primaryStage.getIcons().add(new Image("file:resources/images/address_book_32.png"));

        initRootLayout();
        startProcessing();
        //showPreprocessing();
    }

    /**
     * Initializes the root layout and tries to load the last opened
     * person file.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class
                    .getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            // Give the controller access to the main app.
            RootLayoutController controller = loader.getController();
            controller.setMainApp(this);

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Show start page
     */
    public void startProcessing(){
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/Start.fxml"));
            AnchorPane PreProcessing = (AnchorPane) loader.load();

            // Set person overview into the center of root layout.
            rootLayout.setCenter(PreProcessing);

            // Give the controller access to the main app.
            StartController controller = loader.getController();
            controller.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public boolean showDbConnectDialog() {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/DbConnectDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Під'єднання до БД");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            sample.view.DbConnectDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setConnectField();

            // Set the dialog icon.
            dialogStage.getIcons().add(new Image("file:resources/images/edit.png"));

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Returns the person file preference, i.e. the file that was last opened.
     * The preference is read from the OS specific registry. If no such
     * preference can be found, null is returned.
     *
     * @return
     */
    public File getPersonFilePath() {
        Preferences prefs = Preferences.userNodeForPackage(Main.class);
        String filePath = prefs.get("filePath", null);
        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }

    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(args);
    }
}