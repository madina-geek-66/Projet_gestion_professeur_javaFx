package sn.groupeisi.projetgestionprofesseurs.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sn.groupeisi.projetgestionprofesseurs.controllers.HomeSceneController;
import sn.groupeisi.projetgestionprofesseurs.utils.JPAUtils;

import javax.persistence.EntityManager;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("E-SCHOOL");
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/views/HomeScene.fxml"));
        Parent mainRoot = mainLoader.load();
        Scene mainScene = new Scene(mainRoot);

        FXMLLoader userLoader = new FXMLLoader(getClass().getResource("/views/UserScene.fxml"));
        Parent userRoot = userLoader.load();
        Scene userScene = new Scene(userRoot);

        FXMLLoader coursLoader = new FXMLLoader(getClass().getResource("/views/CourScene.fxml"));
        Parent coursRoot = coursLoader.load();
        Scene coursScene = new Scene(coursRoot);

        HomeSceneController homController = mainLoader.getController();
        homController.load();

        stage.setScene(mainScene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
        //EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager();
    }
}
