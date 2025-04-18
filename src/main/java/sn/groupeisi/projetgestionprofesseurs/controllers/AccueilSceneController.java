package sn.groupeisi.projetgestionprofesseurs.controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import sn.groupeisi.projetgestionprofesseurs.dao.*;
import sn.groupeisi.projetgestionprofesseurs.entities.Cours;
import sn.groupeisi.projetgestionprofesseurs.entities.User;

import java.net.URL;
import java.time.LocalDate;
import java.util.Map;
import java.util.ResourceBundle;

public class AccueilSceneController implements Initializable {
    @FXML private Label labelTotalSalles;
    @FXML private Label labelTotalProfesseurs;
    @FXML private Label labelTotalCours;

    @FXML private BarChart<String, Number> barChartEmargementsProfesseur;
    @FXML private LineChart<String, Number> lineChartEvolutionEmargements;
    @FXML private PieChart pieChartTauxPresence;
    @FXML private HBox legendContainer;

    private User currentUser = new User();

    // Repositories
    private SalleImplement salleImplement = new SalleImplement();
    private UserImplement userImplement = new UserImplement();
    private CourImplement courImplement = new CourImplement();
    private EmargementImpl emargementImplement = new EmargementImpl();

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        // Charger les données après avoir défini l'utilisateur
        chargerStatistiquesGenerales();
        chargerGraphiqueEmargementsProfesseur();
        chargerGraphiqueEvolutionEmargements();
        chargerGraphiqueTauxPresence();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configuration initiale des graphiques
        configurerBarChartEmargements();
        configurerLineChartEvolutionEmargements();
        configurerPieChartTauxPresence();
    }

//    private void chargerStatistiquesGenerales() {
//        labelTotalSalles.setText(String.valueOf(salleImplement.getAll().size()));
//        labelTotalProfesseurs.setText(String.valueOf(userImplement.getProfesseurs().size()));
//        labelTotalCours.setText(String.valueOf(courImplement.getAll().size()));
//    }

    private void chargerStatistiquesGenerales() {
        // Icône pour les salles
        FontAwesomeIconView iconSalles = new FontAwesomeIconView(FontAwesomeIcon.BUILDING);
        iconSalles.setGlyphSize(20);
        iconSalles.setFill(Color.BLUE);
        labelTotalSalles.setGraphic(iconSalles);
        labelTotalSalles.setText(" " + String.valueOf(salleImplement.getAll().size()));

        // Icône pour les professeurs
        FontAwesomeIconView iconProfesseurs = new FontAwesomeIconView(FontAwesomeIcon.USERS);
        iconProfesseurs.setGlyphSize(20);
        iconProfesseurs.setFill(Color.GREEN);
        labelTotalProfesseurs.setGraphic(iconProfesseurs);
        labelTotalProfesseurs.setText(" " + String.valueOf(userImplement.getProfesseurs().size()));

        // Icône pour les cours
        FontAwesomeIconView iconCours = new FontAwesomeIconView(FontAwesomeIcon.BOOK);
        iconCours.setGlyphSize(20);
        iconCours.setFill(Color.RED);
        labelTotalCours.setGraphic(iconCours);
        labelTotalCours.setText(" " + String.valueOf(courImplement.getAll().size()));
    }

    private void configurerBarChartEmargements() {
        barChartEmargementsProfesseur.setTitle("Nombre d'Émargements par Professeur");
        barChartEmargementsProfesseur.getXAxis().setLabel("Professeurs");
        barChartEmargementsProfesseur.getYAxis().setLabel("Nombre d'Émargements");
    }

    private void chargerGraphiqueEmargementsProfesseur() {
        barChartEmargementsProfesseur.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Émargements");

        Map<String, Long> emargementParProfesseur = emargementImplement.getNombreEmargementParProfesseur();

        for (Map.Entry<String, Long> entry : emargementParProfesseur.entrySet()) {
            String nomProfesseur = userImplement.getNomProfesseurParId(entry.getKey());
            series.getData().add(new XYChart.Data<>(nomProfesseur, entry.getValue()));
        }

        barChartEmargementsProfesseur.getData().add(series);
    }

//    private void configurerLineChartEvolutionEmargements() {
//        lineChartEvolutionEmargements.setTitle("Évolution des Émargements");
//        lineChartEvolutionEmargements.getXAxis().setLabel("Date");
//        lineChartEvolutionEmargements.getYAxis().setLabel("Nombre d'Émargements");
//
//        // Configurer l'axe Y pour commencer à 0
//        ((NumberAxis)lineChartEvolutionEmargements.getYAxis()).setAutoRanging(false);
//        ((NumberAxis)lineChartEvolutionEmargements.getYAxis()).setLowerBound(0);
//    }

    private void configurerLineChartEvolutionEmargements() {
        lineChartEvolutionEmargements.setTitle("Évolution des Émargements");
        lineChartEvolutionEmargements.getXAxis().setLabel("Date");
        lineChartEvolutionEmargements.getYAxis().setLabel("Nombre d'Émargements");

        // Configurer l'axe Y pour aller de 0 à 1
        NumberAxis yAxis = (NumberAxis)lineChartEvolutionEmargements.getYAxis();
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(1); // Nouvelle ligne pour limiter à 1
        yAxis.setTickUnit(0.1); // Pour avoir des graduations tous les 0.1
    }

    private void chargerGraphiqueEvolutionEmargements() {
        lineChartEvolutionEmargements.getData().clear();

        // Série pour les émargements présents
        XYChart.Series<String, Number> seriePresents = new XYChart.Series<>();
        seriePresents.setName("Présences");

        // Série pour les émargements absents
        XYChart.Series<String, Number> serieAbsents = new XYChart.Series<>();
        serieAbsents.setName("Absences");

        // Récupérer les émargements du dernier mois
        LocalDate debut = LocalDate.now().minusMonths(1);
        LocalDate fin = LocalDate.now();

        // Récupérer les émargements par date et statut
        Map<LocalDate, Long> emargementsPresentsList = emargementImplement.getNombreEmargementParDateEtStatut(debut, fin, false);
        Map<LocalDate, Long> emargementAbsentsList = emargementImplement.getNombreEmargementParDateEtStatut(debut, fin, true);

        // Ajouter les données aux séries
        for (Map.Entry<LocalDate, Long> entry : emargementsPresentsList.entrySet()) {
            seriePresents.getData().add(new XYChart.Data<>(entry.getKey().toString(), entry.getValue()));
        }

        for (Map.Entry<LocalDate, Long> entry : emargementAbsentsList.entrySet()) {
            serieAbsents.getData().add(new XYChart.Data<>(entry.getKey().toString(), entry.getValue()));
        }

        lineChartEvolutionEmargements.getData().addAll(seriePresents, serieAbsents);

        // Colorier sous la courbe
        seriePresents.getNode().setStyle("-fx-stroke: green; -fx-stroke-width: 2px;");
        serieAbsents.getNode().setStyle("-fx-stroke: red; -fx-stroke-width: 2px;");
    }

    private void configurerPieChartTauxPresence() {
        pieChartTauxPresence.setTitle("Taux de Présence par Cours");
        legendContainer.getChildren().clear();
    }

    private void chargerGraphiqueTauxPresence() {
        pieChartTauxPresence.getData().clear();
        legendContainer.getChildren().clear();

        // Récupérer les taux de présence normalisés
        Map<String, Double> tauxPresence = emargementImplement.getTauxPresenceParCours();

        // Couleurs pour les cours
        Color[] couleurs = {
                Color.BLUE, Color.GREEN, Color.RED, Color.ORANGE,
                Color.PURPLE, Color.BROWN, Color.PINK
        };

        int index = 0;
        for (Map.Entry<String, Double> entry : tauxPresence.entrySet()) {
            // Trouver le nom du cours
            String nomCours = courImplement.getNomCoursParId(entry.getKey());

            // Créer le slice du pie chart
            PieChart.Data slice = new PieChart.Data(
                    nomCours,
                    entry.getValue()
            );
            pieChartTauxPresence.getData().add(slice);

            // Ajouter une légende
            Label legendLabel = new Label(nomCours);
            legendLabel.setTextFill(couleurs[index % couleurs.length]);
            legendContainer.getChildren().add(legendLabel);

            index++;
        }

        // Ajouter des pourcentages aux slices
        pieChartTauxPresence.getData().forEach(data -> {
            data.nameProperty().set(
                    String.format("%s (%.1f%%)", data.getName(), data.getPieValue())
            );
        });
    }
}