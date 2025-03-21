package sn.groupeisi.projetgestionprofesseurs.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import sn.groupeisi.projetgestionprofesseurs.dao.CourImplement;
import sn.groupeisi.projetgestionprofesseurs.dao.EmargementImpl;
import sn.groupeisi.projetgestionprofesseurs.entities.Cours;
import sn.groupeisi.projetgestionprofesseurs.entities.Emargement;
import sn.groupeisi.projetgestionprofesseurs.entities.User;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EmargementSceneController implements Initializable {
    private Map<Long, String> emargementTemporaire = new HashMap<>();
    @FXML
    private Button actualiserButton;

    @FXML
    private VBox coursContainer;

    @FXML
    private Label dateDuJourLabel;

    @FXML
    private Button historiqueButton;

    @FXML
    void handleActualiser(ActionEvent event) {
        chargerCoursDuJour();
    }

    @FXML
    void handleHistorique(ActionEvent event) {
        // À implémenter selon les besoins de navigation
    }

    private EmargementImpl emargementRepository = new EmargementImpl();
    private CourImplement coursRepository = new CourImplement();
    private User currentUser = new User();
    private String jourActuel;
    private Map<Long, HBox> coursCards = new HashMap<>();
    private ScheduledExecutorService scheduler;

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        // Charger les cours après avoir défini l'utilisateur courant
        if (jourActuel != null) {
            chargerCoursDuJour();
            // Démarrer la vérification des boutons
            demarrerVerificationBoutons();
        }
    }

    private void chargerCoursDuJour() {
        coursContainer.getChildren().clear();
        coursCards.clear();
        if (currentUser == null || currentUser.getId() == null) {
            Label errorLabel = new Label("Aucun professeur connecté");
            errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #e74c3c;");
            coursContainer.getChildren().add(errorLabel);
            return;
        }

        List<Cours> coursJour = coursRepository.getCoursParProfEtJour(currentUser.getId(), jourActuel);

        if (coursJour.isEmpty()) {
            Label noCoursLabel = new Label("Aucun cours programmé pour aujourd'hui");
            noCoursLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d;");
            coursContainer.getChildren().add(noCoursLabel);
            return;
        }

        for (Cours cours : coursJour) {
            HBox coursCard = creerCoursCard(cours);
            coursContainer.getChildren().add(coursCard);
            coursCards.put(cours.getId(), coursCard);
        }

        // Vérifier l'état initial des boutons et statuts
        verifierEtatBoutons();
    }

    private HBox creerCoursCard(Cours cours) {
        HBox card = new HBox();
        card.getStyleClass().add("cours-card");
        card.setPrefWidth(Region.USE_COMPUTED_SIZE);
        card.setSpacing(10);
        VBox infosBox = new VBox();
        infosBox.setSpacing(5);
        infosBox.setPrefWidth(Region.USE_COMPUTED_SIZE);
        VBox.setVgrow(infosBox, Priority.ALWAYS);

        Text nomCours = new Text(cours.getNom());
        nomCours.getStyleClass().add("cours-header");

        Text salleInfo = new Text("Salle: " + cours.getSalle().getLibelle());
        salleInfo.getStyleClass().add("cours-details");

        Text horaireInfo = new Text(cours.getHeure_debut() + " - " + cours.getHeure_fin());
        horaireInfo.getStyleClass().add("cours-horaire");

        Text descriptionInfo = new Text(cours.getDescription());
        descriptionInfo.getStyleClass().add("cours-details");

        Text statutInfo = new Text("");
        statutInfo.setId("statut-" + cours.getId());
        statutInfo.getStyleClass().add("cours-statut");

        infosBox.getChildren().addAll(nomCours, salleInfo, horaireInfo, descriptionInfo, statutInfo);
        VBox boutonsBox = new VBox();
        boutonsBox.setSpacing(10);
        boutonsBox.setAlignment(Pos.CENTER_RIGHT);

//        Button signalerButton = new Button("Me signaler");
//        signalerButton.getStyleClass().add("signaler-button");
//        signalerButton.setId("signaler-" + cours.getId());
//        signalerButton.setOnAction(event -> handleSignaler(cours));

        Button validerButton = new Button("Valider");
        validerButton.getStyleClass().add("valider-button");
        validerButton.setId("valider-" + cours.getId());
        validerButton.setOnAction(event -> handleValider(cours));

        boutonsBox.getChildren().addAll(validerButton);

        card.getChildren().addAll(infosBox, boutonsBox);
        HBox.setHgrow(infosBox, Priority.ALWAYS);

        // Vérifier si un émargement existe déjà pour ce cours
        Emargement emargementExistant = emargementRepository.findByCoursProfesseurAndDate(cours.getId(), currentUser.getId(), LocalDate.now());
        if (emargementExistant != null) {
            // Afficher le statut de l'émargement existant
            statutInfo.setText("Statut: " + emargementExistant.getStatut() + " (Validé)");
            statutInfo.setStyle("-fx-font-weight: bold; -fx-fill: " +
                    getColorForStatut(emargementExistant.getStatut()) + ";");

            validerButton.setDisable(true);
        } else {
            // Vérifier si le cours est terminé depuis plus de 20 minutes
            LocalTime heureActuelle = LocalTime.now();
            LocalTime limiteFin = cours.getHeure_fin().plusMinutes(20);

            if (heureActuelle.isAfter(limiteFin)) {
                // Le cours est terminé depuis plus de 20 minutes, marquer comme absent
                statutInfo.setText("Statut: Absent");
                statutInfo.setStyle("-fx-font-weight: bold; -fx-fill: red;");

                // Désactiver les boutons
                //signalerButton.setDisable(true);
                validerButton.setDisable(true);

                // Enregistrer l'absence automatiquement si ce n'est pas déjà fait
                if (!emargementRepository.existeEmargement(cours.getId(), currentUser.getId(), LocalDate.now())) {
                    enregistrerAbsence(cours);
                }
            }
        }

        return card;
    }

    private String getColorForStatut(String statut) {
        switch (statut) {
            case "A L'Heure":
                return "green";
            case "En Retard":
                return "orange";
            case "Absent":
                return "red";
            default:
                return "black";
        }
    }

    private void enregistrerAbsence(Cours cours) {
        // Créer un nouvel émargement avec statut Absent
        Emargement emargement = new Emargement();
        emargement.setCours(cours);
        emargement.setProfesseur(currentUser);
        emargement.setStatut("Absent");
        emargement.setDate(LocalDate.now());

        // Enregistrer dans la base de données
        try {
            emargementRepository.save(emargement);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'enregistrement de l'absence automatique: " + e.getMessage());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        emargementRepository = new EmargementImpl();
        coursRepository = new CourImplement();
        LocalDate aujourdhui = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy", Locale.FRANCE);
        dateDuJourLabel.setText(aujourdhui.format(formatter));
        jourActuel = aujourdhui.format(DateTimeFormatter.ofPattern("EEEE", Locale.FRANCE));
        jourActuel = jourActuel.substring(0, 1).toUpperCase() + jourActuel.substring(1);
        actualiserButton.setOnAction(this::handleActualiser);
    }

    // Méthode pour arrêter proprement le scheduler lors de la fermeture de la fenêtre
    public void shutdown() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    private void afficherAlerte(String titre, String entete, String contenu) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(entete);
        alert.setContentText(contenu);
        alert.showAndWait();
    }

    private void handleSignaler(Cours cours) {
        LocalTime heureActuelle = LocalTime.now();
        LocalTime heureDebut = cours.getHeure_debut();
        LocalTime limite = heureDebut.plusMinutes(30);

        String statut;
        if (heureActuelle.isAfter(limite)) {
            statut = "En Retard";
        } else {
            statut = "A L'Heure";
        }

        // Enregistrer le statut temporaire
        emargementTemporaire.put(cours.getId(), statut);

        // Mettre à jour l'affichage du statut
        Platform.runLater(() -> {
            Text statutInfo = (Text) coursCards.get(cours.getId()).lookup("#statut-" + cours.getId());
            statutInfo.setText("Statut: " + statut);
            statutInfo.setStyle("-fx-font-weight: bold; -fx-fill: " +
                    getColorForStatut(statut) + ";");

            // Désactiver le bouton Me signaler
            Button signalerButton = (Button) coursCards.get(cours.getId()).lookup("#signaler-" + cours.getId());
            signalerButton.setDisable(true);

            // Activer le bouton Valider si on est dans la période de validation
            Button validerButton = (Button) coursCards.get(cours.getId()).lookup("#valider-" + cours.getId());
            LocalTime debutValidation = cours.getHeure_fin().minusMinutes(10);
            LocalTime finValidation = cours.getHeure_fin().plusMinutes(20);

            if (heureActuelle.isAfter(debutValidation) && heureActuelle.isBefore(finValidation)) {
                validerButton.setDisable(false);
            }
        });

        afficherAlerte("Signalement", "Signalement enregistré",
                "Vous vous êtes signalé comme " + statut + " pour le cours " + cours.getNom());
    }

    private void handleValider(Cours cours) {
        LocalTime heureActuelle = LocalTime.now();
        LocalTime heureDebut = cours.getHeure_debut();
        LocalTime heureFin = cours.getHeure_fin();
        String statut;

        // Déterminer le statut en fonction de l'heure actuelle
        if (heureActuelle.isAfter(heureDebut) && heureActuelle.isBefore(heureDebut.plusMinutes(30))) {
            statut = "A L'Heure";
        } else if (heureActuelle.isAfter(heureFin.minusMinutes(30)) && heureActuelle.isBefore(heureFin.plusMinutes(20))) {
            statut = "En Retard";
        } else {
            // Ce cas ne devrait pas se produire si les boutons sont correctement activés/désactivés
            statut = "Indéterminé";
        }

        // Créer un nouvel émargement
        Emargement emargement = new Emargement();
        emargement.setCours(cours);
        emargement.setProfesseur(currentUser);
        emargement.setStatut(statut);
        emargement.setDate(LocalDate.now());

        try {
            // Enregistrer l'émargement dans la base de données
            emargementRepository.save(emargement);

            Platform.runLater(() -> {
                // Mettre à jour l'affichage du statut
                Text statutInfo = (Text) coursCards.get(cours.getId()).lookup("#statut-" + cours.getId());
                statutInfo.setText("Statut: " + statut + " (Validé)");
                statutInfo.setStyle("-fx-font-weight: bold; -fx-fill: " + getColorForStatut(statut) + ";");

                // Désactiver le bouton Valider définitivement pour ce cours
                Button validerButton = (Button) coursCards.get(cours.getId()).lookup("#valider-" + cours.getId());
                validerButton.setDisable(true);
            });

            afficherAlerte("Validation", "Émargement validé",
                    "Votre émargement a été enregistré avec le statut: " + statut);

        } catch (Exception e) {
            afficherAlerte("Erreur", "Échec de l'émargement",
                    "Une erreur est survenue lors de l'enregistrement de l'émargement: " + e.getMessage());
        }
    }

    private void demarrerVerificationBoutons() {
        // Arrêter le précédent scheduler s'il existe
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }

        // Créer un nouveau scheduler qui vérifie l'état des boutons toutes les minutes
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            Platform.runLater(() -> verifierEtatBoutons());
        }, 0, 1, TimeUnit.MINUTES);
    }

    private void verifierEtatBoutons() {
        LocalTime heureActuelle = LocalTime.now();

        for (Cours cours : coursRepository.getCoursParProfEtJour(currentUser.getId(), jourActuel)) {
            if (!coursCards.containsKey(cours.getId())) continue;

            LocalTime heureDebut = cours.getHeure_debut();
            LocalTime heureFin = cours.getHeure_fin();
            Button validerButton = (Button) coursCards.get(cours.getId()).lookup("#valider-" + cours.getId());
            Text statutInfo = (Text) coursCards.get(cours.getId()).lookup("#statut-" + cours.getId());

            // Vérifier si on a déjà un émargement pour ce cours aujourd'hui
            boolean dejaEmarge = emargementRepository.existeEmargement(cours.getId(), currentUser.getId(), LocalDate.now());
            Emargement emargementExistant = emargementRepository.findByCoursProfesseurAndDate(cours.getId(), currentUser.getId(), LocalDate.now());

            // Si déjà émargé, afficher le statut et désactiver le bouton
            if (dejaEmarge && emargementExistant != null) {
                statutInfo.setText("Statut: " + emargementExistant.getStatut() + " (Validé)");
                statutInfo.setStyle("-fx-font-weight: bold; -fx-fill: " + getColorForStatut(emargementExistant.getStatut()) + ";");
                validerButton.setDisable(true);
                continue; // Passer au cours suivant
            }

            // Si le cours est déjà terminé (heure actuelle > heure_fin + 20min)
            if (heureActuelle.isAfter(heureFin.plusMinutes(20))) {
                // Si pas encore émargé, marquer comme absent
                if (!dejaEmarge) {
                    enregistrerAbsence(cours);
                    statutInfo.setText("Statut: Absent");
                    statutInfo.setStyle("-fx-font-weight: bold; -fx-fill: red;");
                }
                validerButton.setDisable(true);
                continue; // Passer au cours suivant
            }

            // Période 1: De heure_debut à heure_debut + 30min (statut: A L'Heure)
            boolean periode1 = heureActuelle.isAfter(heureDebut) &&
                    heureActuelle.isBefore(heureDebut.plusMinutes(30));

            // Période 2: De heure_fin - 30min à heure_fin + 20min (statut: En Retard)
            boolean periode2 = heureActuelle.isAfter(heureFin.minusMinutes(30)) &&
                    heureActuelle.isBefore(heureFin.plusMinutes(20));

            // Activer le bouton seulement pendant les périodes 1 et 2
            validerButton.setDisable(!(periode1 || periode2));

            // Afficher le statut approprié selon la période
            if (periode1) {
                statutInfo.setText("Statut: En attente (A L'Heure si validé)");
                statutInfo.setStyle("-fx-font-weight: normal; -fx-fill: green;");
            } else if (periode2) {
                statutInfo.setText("Statut: En attente (En Retard si validé)");
                statutInfo.setStyle("-fx-font-weight: normal; -fx-fill: orange;");
            } else if (heureActuelle.isBefore(heureDebut)) {
                statutInfo.setText("Statut: En attente");
                statutInfo.setStyle("-fx-font-weight: normal; -fx-fill: gray;");
            } else {
                statutInfo.setText("Statut: En attente de validation");
                statutInfo.setStyle("-fx-font-weight: normal; -fx-fill: gray;");
            }
        }
    }
}