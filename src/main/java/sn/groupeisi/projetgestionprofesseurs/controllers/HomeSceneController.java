package sn.groupeisi.projetgestionprofesseurs.controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeSceneController {
    private Stage primaryStage;
    private Scene userScene;
    private Scene courScene;
    public void init(Stage primaryStage, Scene userScene, Scene courScene) {
        this.primaryStage = primaryStage;
        this.userScene = userScene;
        this.courScene = courScene;
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
    private Button usersBtn, coursBtn, emargementBtn, rapportsBtn, logoutBtn;
    @FXML
    private BorderPane contentPane;


    @FXML
    void openViewEtd(ActionEvent event) {
        loadView("/views/UserScene.fxml");
        activateButton(usersBtn);
    }

    @FXML
    void toggleSidebar(ActionEvent event) {

    }

    @FXML
    public void load() {
        sidebar.getStyleClass().add("collapsed");

        activateButton(usersBtn);
        loadView("/views/UserScene.fxml");

        usersBtn.setOnAction(e -> {
            activateButton(usersBtn);
            loadView("/views/UserScene.fxml");
        });

        salleBtn.setOnAction(e -> {
            activateButton(salleBtn);
            loadView("/views/SalleScene.fxml");
        });

        coursBtn.setOnAction(e -> {
            activateButton(coursBtn);
            loadView("/views/CourScene.fxml");
        });

        emargementBtn.setOnAction(e -> {
            activateButton(emargementBtn);
            loadView("/views/EmargementView.fxml");
        });

        rapportsBtn.setOnAction(e -> {
            activateButton(rapportsBtn);
            loadView("/views/RapportsView.fxml");
        });

        logoutBtn.setOnAction(e -> System.out.println("Déconnexion..."));
    }




    private void loadView(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));

            // S'assurer que la vue s'adapte bien
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

        button.getStyleClass().add("active");
    }


}
