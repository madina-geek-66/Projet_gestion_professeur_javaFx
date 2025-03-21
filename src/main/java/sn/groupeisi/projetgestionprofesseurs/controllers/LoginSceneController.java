package sn.groupeisi.projetgestionprofesseurs.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.mindrot.jbcrypt.BCrypt;
import sn.groupeisi.projetgestionprofesseurs.dao.UserImplement;
import sn.groupeisi.projetgestionprofesseurs.entities.User;

import java.io.IOException;

public class LoginSceneController {

    private UserImplement userModel = new UserImplement();
    private User user = new User();
    private Stage stage;
    private Scene homeScene;

    public void init(Stage stage, Scene HomeScene) {
        this.stage = stage;
        this.homeScene = HomeScene;
    }
    @FXML
    private Label errorMessage;

    @FXML
    private Hyperlink forgotPasswordLink;

    @FXML
    private Button loginButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField emailField;

    @FXML
    void login(ActionEvent event) {
        //stage.setScene(homeScene);
        loginAction();
    }


    public void loginAction() {
        if (emailField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            showError("Veuillez remplir tous les champs");
            return;
        }

        // Ne pas hacher le mot de passe saisi ici
        String password = passwordField.getText();
        this.user = userModel.findByEmail(emailField.getText());

        if (user != null) {
            // Utiliser BCrypt.checkpw pour vérifier le mot de passe
            if (BCrypt.checkpw(password, user.getPassword())) {
                showSuccessNotification("Connexion réussie!");
                redirectToHomeScene();
            } else {
                errorNotification();
            }
        } else {
            showError("Aucun utilisateur trouvé!");
        }
    }


    private void redirectToHomeScene() {

            this.stage = (Stage) loginButton.getScene().getWindow();
            Stage homeStage = new Stage();


            FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/views/HomeScene.fxml"));
        try {
            Parent mainRoot = mainLoader.load();
            this.homeScene = new Scene(mainRoot);

            HomeSceneController homeController = mainLoader.getController();
            homeController.setUserConnecte(user);
            homeController.load();

            homeStage.setScene(homeScene);
            homeStage.setTitle("SAKKU SCHOOL");
            homeStage.sizeToScene();
            homeStage.centerOnScreen();
            this.stage.close();
            homeStage.show();
            showSuccessNotification("Bienvenue " + user.getNom() + " " + user.getPrenom());

        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors du chargement de la page d'accueil");
        }
    }



    public void errorNotification() {
        Notifications notification = Notifications.create();
        notification.title("Error");
        notification.text("Email ou mot de passe incorrect!");
        notification.hideAfter(Duration.seconds(5));
        notification.position(Pos.BASELINE_RIGHT);
        notification.show();
    }

    private void showSuccessNotification(String message) {
        Notifications notification = Notifications.create()
                .title("Succès")
                .text(message)
                .hideAfter(Duration.seconds(5))
                .position(Pos.BOTTOM_RIGHT);
        notification.showInformation();
    }

    private void showError(String message) {
        errorMessage.setText(message);
        errorMessage.setVisible(true);

        // Afficher également une notification
        Notifications notification = Notifications.create()
                .title("Erreur")
                .text(message)
                .hideAfter(Duration.seconds(5))
                .position(Pos.BOTTOM_RIGHT)
                .darkStyle();
        notification.showError();
    }

}
