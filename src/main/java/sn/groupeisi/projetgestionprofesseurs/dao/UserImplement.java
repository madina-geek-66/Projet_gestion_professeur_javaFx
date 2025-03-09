package sn.groupeisi.projetgestionprofesseurs.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sn.groupeisi.projetgestionprofesseurs.entities.User;
import sn.groupeisi.projetgestionprofesseurs.utils.JPAUtils;

import javax.persistence.EntityManager;
import java.util.List;

public class UserImplement implements IUser {

    @Override
    public void add(User user) {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory().createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(user);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Override
    public void update(User user) {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory().createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.merge(user);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Override
    public void delete(Long id) {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory().createEntityManager();
        entityManager.getTransaction().begin();
        User user = entityManager.find(User.class, id);
        if(user != null) {
            entityManager.remove(user);
        }
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Override
    public ObservableList<User> getAll() {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory().createEntityManager();
        entityManager.getTransaction().begin();
        ObservableList<User> listUser = FXCollections.observableArrayList();
        List<User> users = entityManager.createQuery("from users ",User.class).getResultList();
        entityManager.getTransaction().commit();
        listUser.addAll(users);
        entityManager.close();
        return listUser;
    }

    @Override
    public User get(Long id) {
        return null;
    }
}
