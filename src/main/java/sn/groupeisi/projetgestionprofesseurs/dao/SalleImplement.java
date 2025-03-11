package sn.groupeisi.projetgestionprofesseurs.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import sn.groupeisi.projetgestionprofesseurs.entities.Salle;
import sn.groupeisi.projetgestionprofesseurs.entities.User;
import sn.groupeisi.projetgestionprofesseurs.utils.JPAUtils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

public class SalleImplement implements ISalle{
    private EntityManager entityManager;
    @Override
    public void add(Salle salle) {
        this.entityManager = JPAUtils.getEntityManagerFactory().createEntityManager();
        if (existsByLibelle(salle.getLibelle())) {
            new Alert(Alert.AlertType.ERROR, "Cette salle existe déjà ").showAndWait();
            return;
        }
        entityManager.getTransaction().begin();
        entityManager.persist(salle);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Override
    public void update(Salle salle) {
        this.entityManager = JPAUtils.getEntityManagerFactory().createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.merge(salle);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Override
    public void delete(Long id) {
        this.entityManager = JPAUtils.getEntityManagerFactory().createEntityManager();
        entityManager.getTransaction().begin();
        Salle salle = entityManager.find(Salle.class, id);
        entityManager.remove(salle);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Override
    public ObservableList<Salle> getAll() {
        this.entityManager = JPAUtils.getEntityManagerFactory().createEntityManager();
        entityManager.getTransaction().begin();
        ObservableList<Salle> listSalle = FXCollections.observableArrayList();
        List<Salle> salles = entityManager.createQuery("from Salle",Salle.class ).getResultList();
        entityManager.getTransaction().commit();
        listSalle.addAll(salles);
        entityManager.close();
        return listSalle;
    }

    private boolean existsByLibelle(String libelle) {
        this.entityManager = JPAUtils.getEntityManagerFactory().createEntityManager();
        try {
            Salle salle = entityManager.createQuery("SELECT s FROM Salle s WHERE s.libelle = :libelle", Salle.class)
                    .setParameter("libelle", libelle)
                    .getSingleResult();
            return salle != null;
        } catch (NoResultException e) {
            return false;
        }
    }

    @Override
    public Salle get(Long id) {
        return null;
    }
}
