package ru.mai.javafx.javafxcalendarapplication;

import animations.Shake;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ru.mai.javafx.javafxcalendarapplication.modules.DatabaseHandler;
import ru.mai.javafx.javafxcalendarapplication.modules.User;

import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpController {

    @FXML
    private Button btnContinue;

    @FXML
    private PasswordField confirmPassword;

    @FXML
    public TextField enterName;

    @FXML
    private Label justLabel;

    @FXML
    private Label justLabel1;

    @FXML
    private TextField newLogin;

    @FXML
    private PasswordField newPassword;

    private void showNotificationIfUserAlreadyExists() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Problems with your sign up");
        alert.setHeaderText("This user already exists");

        alert.showAndWait();
    }

    private void showNotificationIfPasswordsDoNotMatch() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Problems with your sign up");
        alert.setHeaderText("Please make sure your passwords match");

        alert.showAndWait();
    }

    private void showNotificationIfAFieldIsNotFilled() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Problems with your sign up");
        alert.setHeaderText("Please make sure all fields are filled");

        alert.showAndWait();
    }

    @FXML
    void clickContinue () throws FileNotFoundException {
        String userName = newLogin.getText().trim();
        String password = newPassword.getText();

        if (checkUserOnBD(userName, password) == true) {
            showNotificationIfUserAlreadyExists();
        } else {
            signUp();
        }
    }
    @FXML
    void signUp() throws FileNotFoundException {
        DatabaseHandler dbHandler = new DatabaseHandler();

        String firstName = enterName.getText().trim();
        String userName = newLogin.getText().trim();
        String password = newPassword.getText();
        String checkPass = confirmPassword.getText();

        Shake nameAnimation = new Shake(enterName);
        Shake userNameAnimation = new Shake(newLogin);
        Shake passwordAnimation = new Shake(newPassword);
        Shake checkPasswordAnimation = new Shake(confirmPassword);

        User user = new User(firstName, userName, password);

        if (firstName.equals("") | userName.equals("") | password.equals("")) {
            nameAnimation.playAnimation();
            userNameAnimation.playAnimation();
            passwordAnimation.playAnimation();
            checkPasswordAnimation.playAnimation();

            showNotificationIfAFieldIsNotFilled();
        } else if (!checkPass.equals(password)) {
            passwordAnimation.playAnimation();
            checkPasswordAnimation.playAnimation();
            showNotificationIfPasswordsDoNotMatch();
        } else {
            dbHandler.sighUpUser(user);
            btnContinue.getScene().getWindow().hide();

            boolean isAfterSignUp = true;
            InController inController = new InController();
            inController.loginUser(userName, password, isAfterSignUp);
        }
    }
    public boolean checkUserOnBD(String userName, String password) {
        userName = newLogin.getText().trim();
        password = newPassword.getText();
        DatabaseHandler dbHandler = new DatabaseHandler();
        User user = new User();
        user.setUserName(userName);
        user.setPassword(password);
        ResultSet result = dbHandler.getUser(user);
        try {
            if (result.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}