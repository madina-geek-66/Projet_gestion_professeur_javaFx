package sn.groupeisi.projetgestionprofesseurs.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sn.groupeisi.projetgestionprofesseurs.entities.Cours;
import sn.groupeisi.projetgestionprofesseurs.entities.Notification;
import sn.groupeisi.projetgestionprofesseurs.entities.Salle;
import sn.groupeisi.projetgestionprofesseurs.utils.JPAUtils;

import javax.persistence.EntityManager;
import java.util.List;

public class NotificationImplement implements INotification {
    @Override
    public void add(Notification notification) {
        EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        try {
            em.persist(notification);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.close();
            e.printStackTrace();
        }
    }

    @Override
    public void update(Notification notification) {
        EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        em.merge(notification);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void delete(Long id) {
        EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager();
        Notification notification = em.find(Notification.class, id);
        em.getTransaction().begin();
        em.remove(notification);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public ObservableList<Notification> getAll() {
        EntityManager em  = JPAUtils.getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        ObservableList<Notification> listNotif = FXCollections.observableArrayList();
        List<Notification> notifications = em.createQuery("from Notification ",Notification.class ).getResultList();
        em.getTransaction().commit();
        listNotif.addAll(notifications);
        em.close();
        return listNotif;
    }

    @Override
    public Notification get(Long id) {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory().createEntityManager();
        Notification notification = entityManager.find(Notification.class, id);
        entityManager.close();
        return notification;
    }

    public List<Notification> getByUser(int userId) {
        EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT n FROM Notification n WHERE n.destinataire.id = :userId", Notification.class)
                    .setParameter("userId", userId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
