package sn.groupeisi.projetgestionprofesseurs.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import sn.groupeisi.projetgestionprofesseurs.dao.SalleImplement;
import sn.groupeisi.projetgestionprofesseurs.entities.Salle;
import sn.groupeisi.projetgestionprofesseurs.entities.User;
import sn.groupeisi.projetgestionprofesseurs.utils.JPAUtils;

import javax.persistence.NoResultException;
import java.net.URL;
import java.util.ResourceBundle;

public class SalleSceneController implements Initializable {

    private User currentUser = new User();
    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    private SalleImplement salleImplement = new SalleImplement();

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnClear;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnEdit;

    @FXML
    private TableView<Salle> tableSalle;

    @FXML
    private TableColumn<Salle, Long> colId;

    @FXML
    private TableColumn<Salle, String> colLibelle;

    @FXML
    private TextField fieldLibelle;

    @FXML
    void clearField() {
        ClearField();
    }

    @FXML
    void deleteSalle() {
        Salle selectedSalle = tableSalle.getSelectionModel().getSelectedItem();
        if (selectedSalle != null) {
            salleImplement.delete(selectedSalle.getId());
            load();
            clearField();
        }
    }

    @FXML
    void editSalle() {
        Salle selectedSalle = tableSalle.getSelectionModel().getSelectedItem();
        if (selectedSalle != null) {
            selectedSalle.setLibelle(fieldLibelle.getText());
            salleImplement.update(selectedSalle);
            load();
            clearField();
        }
    }

    @FXML
    void saveSalle() {
        try{
            if(fieldLibelle.getText().isEmpty()){
                new Alert(Alert.AlertType.ERROR, "Veuillez remplir le champ.").showAndWait();
                return;
            }
            Salle salle = new Salle();
            salle.setLibelle(fieldLibelle.getText());
            salleImplement.add(salle);
            load();
            clearField();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colLibelle.setCellValueFactory(new PropertyValueFactory<>("libelle"));
        load();
        tableSalle.setOnMouseClicked(event -> handleTableClick(event));
        //clearField();
    }

    private void load() {
        tableSalle.getItems().setAll(salleImplement.getAll());
        btnEdit.setVisible(false);
        btnDelete.setVisible(false);
        btnClear.setVisible(false);
        btnAdd.setVisible(true);
    }

    private void handleTableClick(MouseEvent event){
        if(event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2){
            btnAdd.setDisable(true);
            btnAdd.setVisible(false);
            Salle selectedSalle = tableSalle.getSelectionModel().getSelectedItem();
            if(selectedSalle != null){
                fieldLibelle.setText(selectedSalle.getLibelle());
                btnEdit.setVisible(true);
                btnDelete.setVisible(true);
                btnClear.setVisible(true);

            }
        }
    }

    private  void ClearField(){
        fieldLibelle.clear();
    }

}
