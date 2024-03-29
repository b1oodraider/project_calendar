package ru.mai.javafx.javafxcalendarapplication;

import animations.Shake;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ru.mai.javafx.javafxcalendarapplication.modules.DatabaseHandler;
import ru.mai.javafx.javafxcalendarapplication.modules.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InController {

    public int idUser = 0;

    @FXML
    private Button btnLetSGo;

    @FXML
    private TextField enterLogin;

    @FXML
    private PasswordField enterPassword;

    @FXML
    private Label justLabel;

    @FXML
    private Button signUp;

    private void showNotificationAboutSignIn() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sign in");
        alert.setHeaderText("Sign in was successful!");

        alert.showAndWait();
    }

    private void showNotificationAboutSingUp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sign up");
        alert.setHeaderText("Sign up was successful! You've signed in already!");

        alert.showAndWait();
    }

    private void showNotificationIfPasswordOrUsernameIsIncorrect() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Problems with your sign in");
        alert.setHeaderText("Username or password is incorrect");

        alert.showAndWait();
    }

    @FXML
    void openSignup() {
        signUp.setOnMouseClicked((event) -> {
            signUp.getScene().getWindow().hide();
            FXMLLoader loaderUp = new FXMLLoader();
            loaderUp.setLocation(getClass().getResource("up-window.fxml"));
            try {
                loaderUp.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Parent rootUp = loaderUp.getRoot();
            Stage stageUp = new Stage();
            stageUp.setScene(new Scene(rootUp));
            stageUp.show();
        });
    }

    public void loginUser(String loginText, String passwordText, boolean isAfterSignUp) throws FileNotFoundException {
        DatabaseHandler dbHandler = new DatabaseHandler();
        User user = new User();
        user.setUserName(loginText);
        user.setPassword(passwordText);
        ResultSet result = dbHandler.getUser(user);

        String filePath = "src/main/resources/userID.txt";

        try {
            if (result.next()) {
                addIdTOFile(filePath, result);
                try {
                    btnLetSGo.getScene().getWindow().hide();
                } catch (NullPointerException ignore) {

                }
                if (!isAfterSignUp) {
                    showNotificationAboutSignIn();
                } else {
                    showNotificationAboutSingUp();
                }
            } else {
                Shake loginAnimation = new Shake(enterLogin);
                Shake passwordAnimation = new Shake(enterPassword);
                loginAnimation.playAnimation();
                passwordAnimation.playAnimation();
                showNotificationIfPasswordOrUsernameIsIncorrect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addIdTOFile(String path, ResultSet result) throws FileNotFoundException, SQLException {
        File file = new File(path);
        PrintWriter printWriter = new PrintWriter(file);

        try (BufferedWriter bf = Files.newBufferedWriter(Path.of(path),
                StandardOpenOption.TRUNCATE_EXISTING)) {
        } catch (IOException e) {
            e.printStackTrace();
        }

        printWriter.println(result.getInt(1) + "");
        printWriter.close();
    }

    @FXML
    void clickBtnLetsGo(ActionEvent event) throws FileNotFoundException {
        String loginText = enterLogin.getText().trim();
        String passwordText = enterPassword.getText();
        boolean isAfterSignUp = false;
        loginUser(loginText, passwordText, isAfterSignUp);
    }
}