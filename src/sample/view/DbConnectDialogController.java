package sample.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import sample.core.DB;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public class DbConnectDialogController {

    @FXML
    private TextField hostField;
    @FXML
    private TextField portField;
    @FXML
    private TextField userField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField dbnameField;

    private Stage dialogStage;
    //private Person person;
    private boolean okClicked = false;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    }

    /**
     * Sets the stage of this dialog.
     *
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Sets the person to be edited in the dialog.
     *
     * @param
     */
    /*public void setPerson(Person person) {
        this.person = person;

        firstNameField.setText(person.getFirstName());
        lastNameField.setText(person.getLastName());
        streetField.setText(person.getStreet());
        postalCodeField.setText(Integer.toString(person.getPostalCode()));
        cityField.setText(person.getCity());
        birthdayField.setText(DateUtil.format(person.getBirthday()));
        birthdayField.setPromptText("dd.mm.yyyy");
    }*/
    public void setConnectField() {
        hostField.setText("localhost");
        portField.setText("3306");
        userField.setText("root");
        passwordField.setText("");
        dbnameField.setText("");
    }

    /**
     * Returns true if the user clicked OK, false otherwise.
     *
     * @return
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Called when the user clicks ok.
     */
    @FXML
    private void handleOk() throws ClassNotFoundException, SQLException {

        Connection c = null;

        if (isInputValid()) {
            try {
                c = DB.connect(hostField.getText(), portField.getText(), dbnameField.getText(), userField.getText(), passwordField.getText());

            } catch (SQLException e) {

                // Show the error message.
                Alert alert = new Alert(AlertType.ERROR);
                alert.initOwner(dialogStage);
                alert.setTitle("БД");
                alert.setHeaderText("Помилка");
                alert.setContentText("Неможливо встановити з'єднання");

                alert.showAndWait();
                return;
            }

            if (c != null) {
                // Show the error message.
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.initOwner(dialogStage);
                alert.setTitle("БД");
                alert.setHeaderText("Повідомлення");
                alert.setContentText("З'єднання встановлено");

                alert.showAndWait();

            } else {
                // Show the error message.
                Alert alert = new Alert(AlertType.ERROR);
                alert.initOwner(dialogStage);
                alert.setTitle("Неможливо встановити з'єднання");
                alert.setHeaderText("Помилка");
                alert.setContentText("Неможливо встановити з'єднання");

                alert.showAndWait();
            }

            okClicked = true;
            dialogStage.close();
            c.close();

        }
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

        if (hostField.getText() == null || hostField.getText().length() == 0) {
            errorMessage += "Заповніть коректно поле ХОСТ!\n";
        }

        if (portField.getText() == null || portField.getText().length() == 0) {
            errorMessage += "Заповніть коректно поле ПОРТ!\n";
        }

            if (userField.getText() == null || userField.getText().length() == 0) {
                errorMessage += "Заповніть коректно поле Користувач!\n";
            }
            if (passwordField.getText() == null || passwordField.getText().length() == 0) {
                errorMessage += "Заповніть коректно поле пароль!\n";
            }
            if (dbnameField.getText() == null || dbnameField.getText().length() == 0) {
                errorMessage += "Заповніть коректно поле Назва БД!\n";
            }

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
}