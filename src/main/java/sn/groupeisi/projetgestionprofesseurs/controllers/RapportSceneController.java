package sn.groupeisi.projetgestionprofesseurs.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sn.groupeisi.projetgestionprofesseurs.dao.EmargementImpl;
import sn.groupeisi.projetgestionprofesseurs.entities.Emargement;
import sn.groupeisi.projetgestionprofesseurs.entities.User;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;

public class RapportSceneController implements Initializable {

    private EmargementImpl emargementDao = new EmargementImpl();
    private ObservableList<Emargement> emargementList = FXCollections.observableArrayList();
    private ExportService exportService = new ExportService();

    private User currentUser = new User();
    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    @FXML
    private DatePicker dateDebut;

    @FXML
    private DatePicker dateFin;

    @FXML
    private ComboBox<String> periodeCbo;

    @FXML
    private Button exportPdfBtn;

    @FXML
    private Button exportExcelBtn;

    @FXML
    private TabPane emargemenTabs;

    @FXML
    private Tab emargementTab;

    @FXML
    private TableView<Emargement> tableEmargements;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configuration des dates par défaut
        dateDebut.setValue(LocalDate.now().minusMonths(1));
        dateFin.setValue(LocalDate.now());

        // Configuration du ComboBox des périodes
        periodeCbo.setItems(FXCollections.observableArrayList(
                "Tous", "Jour", "Semaine", "Mois", "Trimestre", "Année"
        ));
        periodeCbo.setValue("Jour");

        // Configuration des boutons d'export
        setupExportButtons();

        // Configuration du tableau d'émargements
        setupTableColumns();

        // Chargement initial des données
        chargerEmargements();

        // Ajout des écouteurs pour les filtres par date
        setupListeners();
    }

    private void setupTableColumns() {
        // Configuration des colonnes du tableau
        TableColumn<Emargement, Long> idCol = new TableColumn<>("Num Emargement");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Emargement, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Emargement, String> statutCol = new TableColumn<>("Statut");
        statutCol.setCellValueFactory(new PropertyValueFactory<>("statut"));

        TableColumn<Emargement, String> professeurCol = new TableColumn<>("Professeur");
        professeurCol.setCellValueFactory(cellData -> {
            Emargement emargement = cellData.getValue();
            return javafx.beans.binding.Bindings.createStringBinding(
                    () -> emargement.getProfesseur().getNom() + " " + emargement.getProfesseur().getPrenom()
            );
        });

        TableColumn<Emargement, String> coursCol = new TableColumn<>("Cours");
        coursCol.setCellValueFactory(cellData -> {
            Emargement emargement = cellData.getValue();
            return javafx.beans.binding.Bindings.createStringBinding(
                    () -> emargement.getCours().getNom()
            );
        });


        TableColumn<Emargement, LocalTime> heureDebutCol = new TableColumn<>("Heure Début");
        heureDebutCol.setCellValueFactory(cellData -> {
            Emargement emargement = cellData.getValue();
            return javafx.beans.binding.Bindings.createObjectBinding(
                    () -> emargement.getCours().getHeure_debut()
            );
        });

        TableColumn<Emargement, LocalTime> heureFinCol = new TableColumn<>("Heure Fin");
        heureFinCol.setCellValueFactory(cellData -> {
            Emargement emargement = cellData.getValue();
            return javafx.beans.binding.Bindings.createObjectBinding(
                    () -> emargement.getCours().getHeure_fin()
            );
        });

        // Ajout des colonnes au tableau
        tableEmargements.getColumns().addAll(
                idCol, dateCol, heureDebutCol, heureFinCol, statutCol, professeurCol, coursCol
        );

        // Ajustement automatique de la largeur des colonnes
        tableEmargements.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupExportButtons() {
        exportPdfBtn.setOnAction(e -> exporterPDF());
        exportExcelBtn.setOnAction(e -> exporterExcel());
    }

    private void setupListeners() {
        // Écouter les changements de dates
        dateDebut.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && dateFin.getValue() != null) {
                chargerEmargements();
            }
        });

        dateFin.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && dateDebut.getValue() != null) {
                chargerEmargements();
            }
        });

        // Écouter les changements de période
        periodeCbo.setOnAction(e -> appliquerPeriode());
    }

    private void appliquerPeriode() {
        String periode = periodeCbo.getValue();
        LocalDate fin = LocalDate.now();
        LocalDate debut;

        switch (periode) {
            case "Jour":
                debut = fin;
                break;
            case "Semaine":
                debut = fin.minusWeeks(1);
                break;
            case "Mois":
                debut = fin.minusMonths(1);
                break;
            case "Trimestre":
                debut = fin.minusMonths(3);
                break;
            case "Année":
                debut = fin.minusYears(1);
                break;
            default: // "Tous"
                debut = LocalDate.of(2000, 1, 1); // Date très ancienne pour tout inclure
                break;
        }

        dateDebut.setValue(debut);
        dateFin.setValue(fin);

        // Le chargement sera déclenché par les listeners des DatePickers
    }

    private void chargerEmargements() {
        LocalDate debut = dateDebut.getValue();
        LocalDate fin = dateFin.getValue();

        if (debut != null && fin != null) {
            List<Emargement> emargements = emargementDao.findByDateRange(debut, fin);
            emargementList.clear();
            emargementList.addAll(emargements);
            tableEmargements.setItems(emargementList);
        }
    }

    private void exporterPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter en PDF");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        fileChooser.setInitialFileName("emargements.pdf");

        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            try {
                exportService.exporterEmargementPDF(dateDebut.getValue(), dateFin.getValue(), file.getAbsolutePath());
                afficherAlerte("Export réussi", "Le fichier PDF a été généré avec succès.", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                afficherAlerte("Erreur", "Une erreur est survenue lors de l'export: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    private void exporterExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter en Excel");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel", "*.xlsx"));
        fileChooser.setInitialFileName("emargements.xlsx");

        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            try {
                exportService.exporterEmargementExcel(dateDebut.getValue(), dateFin.getValue(), file.getAbsolutePath());
                afficherAlerte("Export réussi", "Le fichier Excel a été généré avec succès.", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                afficherAlerte("Erreur", "Une erreur est survenue lors de l'export: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    private void afficherAlerte(String titre, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}