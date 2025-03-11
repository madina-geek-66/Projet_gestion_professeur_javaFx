package sn.groupeisi.projetgestionprofesseurs.dao;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import sn.groupeisi.projetgestionprofesseurs.entities.Cours;
import sn.groupeisi.projetgestionprofesseurs.utils.JPAUtils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public class CourImplement implements ICour {
    private EntityManager entityManager;
    @Override
    public void add(Cours cours) {
        this.entityManager = JPAUtils.getEntityManagerFactory().createEntityManager();
        if (check(cours.getJour(), cours.getHeure_debut(), cours.getHeure_fin())) {
            Platform.runLater(() -> {
                new Alert(Alert.AlertType.ERROR, "Un cours existe déjà à ce créneau !").showAndWait();
            });

            return;
        }
        if (cours.getHeure_fin().isBefore(cours.getHeure_debut()) || cours.getHeure_fin().equals(cours.getHeure_debut())) {
            Platform.runLater(() -> {
                new Alert(Alert.AlertType.ERROR, "L'heure de fin ne peut pas être avant ou égale à l'heure de début !").showAndWait();
            });
            return;
        }
        entityManager.getTransaction().begin();
        entityManager.persist(cours);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Override
    public void update(Cours cours) {
        this.entityManager = JPAUtils.getEntityManagerFactory().createEntityManager();
        if (checkExcludingCurrent(cours)) {
            Platform.runLater(() -> {
                new Alert(Alert.AlertType.ERROR, "Un cours existe déjà à ce créneau !").showAndWait();
            });
            return;
        }
        if (cours.getHeure_fin().isBefore(cours.getHeure_debut()) || cours.getHeure_fin().equals(cours.getHeure_debut())) {
            Platform.runLater(() -> {
                new Alert(Alert.AlertType.ERROR, "L'heure de fin ne peut pas être avant ou égale à l'heure de début !").showAndWait();
            });
            return;
        }
        entityManager.getTransaction().begin();
        entityManager.merge(cours);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Override
    public void delete(Long id) {
        this.entityManager = JPAUtils.getEntityManagerFactory().createEntityManager();
        Cours cours = entityManager.find(Cours.class, id);
        entityManager.getTransaction().begin();
        entityManager.remove(cours);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Override
    public ObservableList<Cours> getAll() {
        this.entityManager = JPAUtils.getEntityManagerFactory().createEntityManager();
        entityManager.getTransaction().begin();
        ObservableList<Cours> listCours = FXCollections.observableArrayList();
        List<Cours> coursList = entityManager.createQuery("from Cours").getResultList();
        listCours.addAll(coursList);
        entityManager.getTransaction().commit();
        entityManager.close();
        return listCours;
    }

    @Override
    public Cours get(Long id) {
        return null;
    }



    public boolean check(String jour, LocalTime heureDebut, LocalTime heureFin) {
        this.entityManager = JPAUtils.getEntityManagerFactory().createEntityManager();
        try {
            Long count = entityManager.createQuery(
                            "SELECT COUNT(c) FROM Cours c WHERE c.jour = :jour " +
                                    "AND (c.heure_debut < :heureFin AND c.heure_fin > :heureDebut)", Long.class)
                    .setParameter("jour", jour)
                    .setParameter("heureDebut", heureDebut)
                    .setParameter("heureFin", heureFin)
                    .getSingleResult();

            if (count > 0) {
                return true;
            }
            return false;
        } catch (NoResultException e) {
            return false;
        }
    }



    private boolean checkExcludingCurrent(Cours cours) {
        this.entityManager = JPAUtils.getEntityManagerFactory().createEntityManager();
        try {
            Long count = entityManager.createQuery(
                            "SELECT COUNT(c) FROM Cours c WHERE c.jour = :jour " +
                                    "AND (c.heure_debut < :heureFin AND c.heure_fin > :heureDebut) " +
                                    "AND c.id != :courseId", Long.class)
                    .setParameter("jour", cours.getJour())
                    .setParameter("heureDebut", cours.getHeure_debut())
                    .setParameter("heureFin", cours.getHeure_fin())
                    .setParameter("courseId", cours.getId())
                    .getSingleResult();

            return count > 0;
        } catch (NoResultException e) {
            return false;
        }
    }


}
