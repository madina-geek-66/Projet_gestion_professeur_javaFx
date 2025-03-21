package sn.groupeisi.projetgestionprofesseurs.controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import sn.groupeisi.projetgestionprofesseurs.entities.User;

import java.io.IOException;
import java.util.Optional;

public class HomeSceneController {
    private User userConnecte = new User();

    public User getUserConnecte() {
        return userConnecte;
    }

    public void setUserConnecte(User userConnecte) {
        this.userConnecte = userConnecte;
    }

    @FXML
    private Button drawerBtn;
    @FXML
    private FontAwesomeIconView drawerIcon;
    @FXML
    private Pane sidebar;
    @FXML
    private Button salleBtn;

    @FXML
    private FontAwesomeIconView salleIcon;

    @FXML
    private Button homeBtn;

    @FXML
    private FontAwesomeIconView homeIcon;

    @FXML
    private TextField txtUserConnect;

    @FXML
    private FontAwesomeIconView userIcon;


    @FXML
    private Button usersBtn, coursBtn, emargementBtn, rapportsBtn, logoutBtn;
    @FXML
    private BorderPane contentPane;


    @FXML
    void openViewEtd(ActionEvent event) {
        User.Role role = userConnecte.getRole();
        if (User.Role.ADMINISTRATEUR.equals(role)) {
            loadUserScene();
            activateButton(usersBtn);
        } else {
            showAccessDeniedAlert();
        }
    }

    @FXML
    void toggleSidebar(ActionEvent event) {
    }

    @FXML
    public void load() {
        txtUserConnect.setText(userConnecte.getNom() + " " + userConnecte.getPrenom());
        User.Role role = userConnecte.getRole();

        if (User.Role.ADMINISTRATEUR.equals(role)) {
            emargementBtn.setVisible(false);
            emargementBtn.setManaged(false);
        } else if (User.Role.GESTIONNAIRE.equals(role)) {
            usersBtn.setVisible(false);
            usersBtn.setManaged(false);
            emargementBtn.setVisible(false);
            emargementBtn.setManaged(false);
        } else if (User.Role.PROFESSEUR.equals(role)) {
            usersBtn.setVisible(false);
            usersBtn.setManaged(false);
            coursBtn.setVisible(false);
            coursBtn.setManaged(false);
            salleBtn.setVisible(false);
            salleBtn.setManaged(false);
            rapportsBtn.setVisible(false);
            rapportsBtn.setManaged(false);
        }
        activateButton(homeBtn);
        loadAccueilScene();
        configureEventHandlers();
    }

    private void configureEventHandlers() {
        User.Role role = userConnecte.getRole();

        homeBtn.setOnAction(e -> {
            activateButton(homeBtn);
            loadAccueilScene();
        });

        usersBtn.setOnAction(e -> {
            if (User.Role.ADMINISTRATEUR.equals(role)) {
                activateButton(usersBtn);
                loadUserScene();
            } else {
                showAccessDeniedAlert();
            }
        });

        salleBtn.setOnAction(e -> {
            if (!User.Role.PROFESSEUR.equals(role)) {
                activateButton(salleBtn);
                loadSalleScene();
            } else {
                showAccessDeniedAlert();
            }
        });

        coursBtn.setOnAction(e -> {
            if (!User.Role.PROFESSEUR.equals(role)) {
                activateButton(coursBtn);
                loadCoursScene();
            } else {
                showAccessDeniedAlert();
            }
        });

        emargementBtn.setOnAction(e -> {
            if (User.Role.PROFESSEUR.equals(role)) {
                activateButton(emargementBtn);
                loadEmargementScene();
            } else {
                showAccessDeniedAlert();
            }
        });

        rapportsBtn.setOnAction(e -> {
            if (!User.Role.PROFESSEUR.equals(role)) {
                activateButton(rapportsBtn);
                loadRapportsScene();
            } else {
                showAccessDeniedAlert();
            }
        });

        logoutBtn.setOnAction(e -> handleLogout());
    }

    private void loadAccueilScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AccueilScene.fxml"));
            Parent view = loader.load();
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);

            contentPane.setCenter(view);
            AccueilSceneController accueilController = loader.getController();
            accueilController.setCurrentUser(getUserConnecte());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadUserScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UserScene.fxml"));
            Parent view = loader.load();
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);

            contentPane.setCenter(view);
            UserSceneController userController = loader.getController();
            userController.setCurrentUser(getUserConnecte());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadSalleScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/SalleScene.fxml"));
            Parent view = loader.load();
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);

            contentPane.setCenter(view);
            SalleSceneController salleController = loader.getController();
            salleController.setCurrentUser(getUserConnecte());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadCoursScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CourScene.fxml"));
            Parent view = loader.load();
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);

            contentPane.setCenter(view);
            CourSceneController courController = loader.getController();
            courController.setCurrentUser(getUserConnecte());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadEmargementScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/EmargementScene.fxml"));
            Parent view = loader.load();
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);

            contentPane.setCenter(view);
            EmargementSceneController emargementController = loader.getController();
            emargementController.setCurrentUser(getUserConnecte());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadRapportsScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/RapportScene.fxml"));
            Parent view = loader.load();
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);

            contentPane.setCenter(view);
            RapportSceneController rapportController = loader.getController();
            rapportController.setCurrentUser(getUserConnecte());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadView(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);

            contentPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de la vue : " + fxmlPath);
        }
    }

    private void activateButton(Button button) {
        usersBtn.getStyleClass().remove("active");
        coursBtn.getStyleClass().remove("active");
        emargementBtn.getStyleClass().remove("active");
        rapportsBtn.getStyleClass().remove("active");
        logoutBtn.getStyleClass().remove("active");
        salleBtn.getStyleClass().remove("active");
        homeBtn.getStyleClass().remove("active");

        button.getStyleClass().add("active");
    }

    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de déconnexion");
        alert.setHeaderText("Déconnexion");
        alert.setContentText("Voulez-vous vraiment vous déconnecter ?");
        ButtonType buttonTypeOui = new ButtonType("Oui");
        ButtonType buttonTypeNon = new ButtonType("Non", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeOui, buttonTypeNon);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == buttonTypeOui) {
            userConnecte = null;
            Stage currentStage = (Stage) logoutBtn.getScene().getWindow();
            Stage loginStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoginScene.fxml"));
            try {
                Parent root = loader.load();
                Scene scene = new Scene(root);
                loginStage.setScene(scene);
                loginStage.setTitle("Interface de Connexion");
                loginStage.sizeToScene();
                loginStage.centerOnScreen();
                currentStage.close();
                loginStage.show();
                loginStage.setResizable(false);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void showAccessDeniedAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Accès Refusé");
        alert.setHeaderText("Permission Insuffisante");
        alert.setContentText("Vous n'avez pas les droits nécessaires pour accéder à cette fonctionnalité.");
        alert.showAndWait();
    }
}