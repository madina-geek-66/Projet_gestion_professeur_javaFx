package sn.groupeisi.projetgestionprofesseurs.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoginScene.fxml"));
        Parent root = loader.load();


        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/styles/login-style.css").toExternalForm());


        stage.setTitle("Interface de Connexion");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
        //EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager();
    }
}
