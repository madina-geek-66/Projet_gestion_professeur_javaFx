package sn.groupeisi.projetgestionprofesseurs.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sn.groupeisi.projetgestionprofesseurs.entities.User;
import sn.groupeisi.projetgestionprofesseurs.utils.JPAUtils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
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

    public ObservableList<User> getProfesseurs() {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory().createEntityManager();
        entityManager.getTransaction().begin();
        ObservableList<User> listProfesseurs = FXCollections.observableArrayList();
        List<User> professeurs = entityManager.createQuery("FROM users u WHERE u.role = 'PROFESSEUR'", User.class).getResultList();
        entityManager.getTransaction().commit();
        listProfesseurs.addAll(professeurs);
        entityManager.close();
        return listProfesseurs;
    }

    @Override
    public User get(Long id) {
        return null;
    }

    public boolean authenticate(String email, String password) {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory().createEntityManager();
        User user = null;

        try {
            entityManager.getTransaction().begin();

            // Création d'une requête JPQL pour récupérer l'utilisateur par email et password
            TypedQuery<User> query = entityManager.createQuery(
                    "FROM users u WHERE u.email = :email AND u.password = :password",
                    User.class
            );

            // Définition des paramètres
            query.setParameter("email", email);
            query.setParameter("password", password);

            // Exécution de la requête
            user = query.getSingleResult();

            entityManager.getTransaction().commit();
        } catch (NoResultException e) {
            // Aucun utilisateur trouvé avec ces identifiants
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
        } catch (Exception e) {
            // Gestion des autres exceptions
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            entityManager.close();
        }

        return true;
    }


    public User getUserByEmail(String email) {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory().createEntityManager();
        User user = null;

        try {
            entityManager.getTransaction().begin();

            TypedQuery<User> query = entityManager.createQuery(
                    "FROM users u WHERE u.email = :email",
                    User.class
            );
            query.setParameter("email", email);

            user = query.getSingleResult();

            entityManager.getTransaction().commit();
        } catch (NoResultException e) {
            // Aucun utilisateur trouvé avec cet email
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            entityManager.close();
        }

        return user;
    }

    public User findByEmailAndpassword(String email, String password) {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<User> query = entityManager.createQuery(
                    "SELECT u FROM users u WHERE u.email = :email AND u.password = :password", User.class);
            query.setParameter("email", email);
            query.setParameter("password", password);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null; // Aucun utilisateur trouvé avec cet email
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            entityManager.close();
        }
    }

    public User findByEmail(String email) {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<User> query = entityManager.createQuery(
                    "SELECT u FROM users u WHERE u.email = :email", User.class);
            query.setParameter("email", email);

            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            entityManager.close();
        }
    }

    public User findById(Long id) {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory().createEntityManager();
        User user = entityManager.find(User.class, id);
        entityManager.close();
        return user;
    }

    public List<User> getUsersByRole(String role) {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory().createEntityManager();
        List<User> users = entityManager.createQuery("SELECT u FROM User u WHERE u.role = :role", User.class)
                .setParameter("role", role)
                .getResultList();
        entityManager.close();
        return users;
    }


}
