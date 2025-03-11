package sn.groupeisi.projetgestionprofesseurs.controllers;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;
import sn.groupeisi.projetgestionprofesseurs.dao.CourImplement;
import sn.groupeisi.projetgestionprofesseurs.dao.SalleImplement;
import sn.groupeisi.projetgestionprofesseurs.dao.UserImplement;
import sn.groupeisi.projetgestionprofesseurs.entities.Cours;
import sn.groupeisi.projetgestionprofesseurs.entities.Salle;
import sn.groupeisi.projetgestionprofesseurs.entities.User;

import java.net.URL;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class CourSceneController implements Initializable {

    private CourImplement courImplement = new CourImplement();
    private SalleImplement salleImplement = new SalleImplement();
    private UserImplement userImplement = new UserImplement();

    @FXML
    private Button btnClear;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnEdit;

    @FXML
    private Button btnSave;

    @FXML
    private ComboBox<String> cmbJour;

    @FXML
    private ComboBox<User> cmbProfesseur;

    @FXML
    private ComboBox<Salle> cmbSalle;

    @FXML
    private TableView<Cours> tableCours;

    @FXML
    private TableColumn<Cours, String> colDescription;

    @FXML
    private TableColumn<Cours, LocalTime> colHeureD;

    @FXML
    private TableColumn<Cours, LocalTime> colHeureF;

    @FXML
    private TableColumn<Cours, Integer> colId;

    @FXML
    private TableColumn<Cours, String> colJour;

    @FXML
    private TableColumn<Cours, String> colNom;

    @FXML
    private TableColumn<Cours, String> colProfesseur;

    @FXML
    private TableColumn<Cours, String> colSalle;

    @FXML
    private TextArea fieldDesc;

    @FXML
    private TextField fieldNom;

    @FXML
    private Spinner<Integer> spinHeureD;

    @FXML
    private Spinner<Integer> spinHeureF;

    @FXML
    private Spinner<Integer> spinMinuteD;

    @FXML
    private Spinner<Integer> spinMinuteF;

    @FXML
    void clearField() {
        clear();
    }

    @FXML
    void deleteCours(ActionEvent event) {
        Cours selectedCours = tableCours.getSelectionModel().getSelectedItem();
        if (selectedCours == null) {
            new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un cours à supprimer !").showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous vraiment supprimer ce cours ?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            courImplement.delete(selectedCours.getId());
            load();
            clear();
        }
    }

    @FXML
    void editCours() {
        Cours selectedCours = tableCours.getSelectionModel().getSelectedItem();
        if (selectedCours != null) {
            LocalTime heureDebut = LocalTime.of(spinHeureD.getValue(), spinMinuteD.getValue());
            LocalTime heureFin = LocalTime.of(spinHeureF.getValue(), spinMinuteF.getValue());
            selectedCours.setNom(fieldNom.getText());
            selectedCours.setDescription(fieldDesc.getText());
            selectedCours.setJour(cmbJour.getValue());
            selectedCours.setHeure_debut(heureDebut);
            selectedCours.setHeure_fin(heureFin);
            selectedCours.setSalle(cmbSalle.getSelectionModel().getSelectedItem());
            selectedCours.setProfesseur(cmbProfesseur.getSelectionModel().getSelectedItem());

            courImplement.update(selectedCours);
            load();
            clear();
        }



//        if (heureDebut.isAfter(heureFin) || heureDebut.equals(heureFin)) {
//            new Alert(Alert.AlertType.ERROR, "L'heure de début doit être avant l'heure de fin !").showAndWait();
//            return;
//        }


    }

    @FXML
    void saveCours() {
        LocalTime heureDebut = LocalTime.of(spinHeureD.getValue(), spinMinuteD.getValue());
        LocalTime heureFin = LocalTime.of(spinHeureF.getValue(), spinMinuteF.getValue());
        //new Alert(Alert.AlertType.ERROR, "Veuillez remplir le champ.").showAndWait();
        try{
            if (fieldNom.getText().isEmpty() || fieldDesc.getText().isEmpty() || cmbJour.getValue() == null || cmbProfesseur.getValue() == null || cmbSalle.getValue() == null) {
                new Alert(Alert.AlertType.ERROR, "Veuillez remplir tous les champs !").showAndWait();
                return;
            }

            if (heureDebut.isAfter(heureFin) || heureDebut.equals(heureFin)) {
                new Alert(Alert.AlertType.ERROR, "L'heure de début doit être avant l'heure de fin !").showAndWait();
                return;
            }

            Cours cours = new Cours();
            cours.setNom(fieldNom.getText());
            cours.setDescription(fieldDesc.getText());
            cours.setJour(cmbJour.getValue());
            cours.setHeure_debut(heureDebut);
            cours.setHeure_fin(heureFin);
            cours.setSalle(cmbSalle.getSelectionModel().getSelectedItem());
            cours.setProfesseur(cmbProfesseur.getValue());
            courImplement.add(cours);
            load();
            clear();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colJour.setCellValueFactory(new PropertyValueFactory<>("jour"));
        colHeureD.setCellValueFactory(new PropertyValueFactory<>("heure_debut"));
        colHeureF.setCellValueFactory(new PropertyValueFactory<>("heure_fin"));
        colSalle.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSalle().getLibelle()));

        colProfesseur.setCellValueFactory(cellData -> {
            User prof = cellData.getValue().getProfesseur();
            return new SimpleStringProperty(prof != null ? prof.getNom()+" "+prof.getPrenom() : "");
        });
        loadComboBoxes();
        tableCours.setOnMouseClicked(event -> handleTableClick(event));
        load();
    }

    private void load(){
        tableCours.getItems().setAll(courImplement.getAll());
        btnEdit.setVisible(false);
        btnDelete.setVisible(false);
        btnClear.setVisible(true);
        btnSave.setVisible(true);
    }

    private void loadComboBoxes() {
        cmbJour.getItems().addAll(FXCollections.observableArrayList("Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"));
        ObservableList<User> profs = FXCollections.observableArrayList(userImplement.getProfesseurs());
        cmbProfesseur.getItems().addAll(profs);
        cmbProfesseur.setConverter(new StringConverter<User>() {
            @Override
            public String toString(User prof) {
                return (prof != null) ? prof.getNom() +" "+ prof.getPrenom() : "";
            }

            @Override
            public User fromString(String string) {
                return cmbProfesseur.getItems().stream()
                        .filter(prof -> prof.getNom().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });

        List<Salle> salles = salleImplement.getAll();
        cmbSalle.setItems(FXCollections.observableArrayList(salles));
        cmbSalle.setConverter(new StringConverter<Salle>() {
            @Override
            public String toString(Salle salle) {
                return (salle != null) ? salle.getLibelle() : "";
            }

            @Override
            public Salle fromString(String string) {
                return cmbSalle.getItems().stream()
                        .filter(salle -> salle.getLibelle().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });

        spinHeureD.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(8, 20, 8));
        spinMinuteD.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
        spinHeureF.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(9, 20, 9));
        spinMinuteF.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
    }

    private void handleTableClick(MouseEvent event){
        if(event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2){
            btnSave.setDisable(true);
            btnSave.setVisible(false);
            Cours selectedCours = tableCours.getSelectionModel().getSelectedItem();
            if(selectedCours != null){
                fieldNom.setText(selectedCours.getNom());
                fieldDesc.setText(selectedCours.getDescription());
                cmbJour.setValue(selectedCours.getJour());
                if (selectedCours.getProfesseur() != null) {
                    cmbProfesseur.getSelectionModel().select(selectedCours.getProfesseur());
                }
                if (selectedCours.getSalle() != null) {
                    cmbSalle.getSelectionModel().select(selectedCours.getSalle());
                }
                LocalTime heureDebut = selectedCours.getHeure_debut();
                LocalTime heureFin = selectedCours.getHeure_fin();

                if (heureDebut != null && heureFin != null) {
                    spinHeureD.getValueFactory().setValue(heureDebut.getHour());
                    spinMinuteD.getValueFactory().setValue(heureDebut.getMinute());
                    spinHeureF.getValueFactory().setValue(heureFin.getHour());
                    spinMinuteF.getValueFactory().setValue(heureFin.getMinute());
                }
                btnEdit.setVisible(true);
                btnDelete.setVisible(true);
                btnClear.setVisible(true);
            }
        }
    }

    private void clear(){
        fieldNom.clear();
        fieldDesc.clear();
        cmbJour.setValue(null);
        cmbProfesseur.getSelectionModel().clearSelection();
        cmbSalle.getSelectionModel().clearSelection();
        spinHeureD.getValueFactory().setValue(8);
        spinMinuteD.getValueFactory().setValue(0);
        spinHeureF.getValueFactory().setValue(9);
        spinMinuteF.getValueFactory().setValue(0);
//        btnSave.setDisable(false);
//        btnSave.setVisible(true);
//        btnEdit.setVisible(false);
//        btnDelete.setVisible(false);
    }
}