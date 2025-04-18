package sn.groupeisi.projetgestionprofesseurs.dao;

import javafx.collections.FXCollections;
import sn.groupeisi.projetgestionprofesseurs.entities.Cours;
import sn.groupeisi.projetgestionprofesseurs.entities.Emargement;
import sn.groupeisi.projetgestionprofesseurs.utils.JPAUtils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EmargementImpl implements IEmargement{
    public void ajouterEmargement(Emargement emargement) {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory().createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(emargement);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Override
    public void ajouterEmargement(Object o) {

    }

    public List<Emargement> getEmargementsParProfesseur(String professeurId) {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory().createEntityManager();
        List<Emargement> emargements = entityManager.createQuery("SELECT e FROM Emargement e WHERE e.professeur.id = :professeurId", Emargement.class)
                .setParameter("professeurId", professeurId)
                .getResultList();
        entityManager.close();
        return emargements;
    }

    public Emargement getEmargementParCoursEtProf(String coursId, String professeurId) {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory().createEntityManager();
        List<Emargement> result = entityManager.createQuery("SELECT e FROM Emargement e WHERE e.cours.id = :coursId AND e.professeur.id = :professeurId", Emargement.class)
                .setParameter("coursId", coursId)
                .setParameter("professeurId", professeurId)
                .getResultList();
        entityManager.close();
        return result.isEmpty() ? null : result.get(0);
    }

    public Emargement findByCoursProfesseurAndDate(Long coursId, Long professeurId, LocalDate date) {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory().createEntityManager();
        List<Emargement> result = entityManager.createQuery("SELECT e FROM Emargement e WHERE e.cours.id = :coursId AND e.professeur.id = :professeurId AND e.date = :date", Emargement.class)
                .setParameter("coursId", coursId)
                .setParameter("professeurId", professeurId)
                .setParameter("date", date)
                .getResultList();
        entityManager.close();
        return result.isEmpty() ? null : result.get(0);
    }

    public void save(Emargement emargement) {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory().createEntityManager();
        entityManager.getTransaction().begin();
        if (emargement.getId() == null) {
            entityManager.persist(emargement);
        } else {
            entityManager.merge(emargement);
        }
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public boolean existeEmargement(Long coursId, Long professeurId, LocalDate date) {
        EntityManager entityManager = null;
        try {
            entityManager = JPAUtils.getEntityManagerFactory().createEntityManager();
            Long count = entityManager.createQuery(
                            "SELECT COUNT(e) FROM Emargement e WHERE e.cours.id = :coursId " +
                                    "AND e.professeur.id = :professeurId " +
                                    "AND e.date = :date", Long.class)
                    .setParameter("coursId", coursId)
                    .setParameter("professeurId", professeurId)
                    .setParameter("date", date)
                    .getSingleResult();
            return count > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    public List<Emargement> getEmargementsParPeriode(LocalDate dateDebut, LocalDate dateFin) {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory().createEntityManager();
        List<Emargement> emargements = entityManager.createQuery(
                        "SELECT e FROM Emargement e WHERE e.date BETWEEN :dateDebut AND :dateFin", Emargement.class)
                .setParameter("dateDebut", dateDebut)
                .setParameter("dateFin", dateFin)
                .getResultList();
        entityManager.close();
        return emargements;
    }

    public Map<String, Long> getNombreEmargementParProfesseur() {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory().createEntityManager();
        List<Object[]> resultList = entityManager.createQuery(
                        "SELECT e.professeur.id, COUNT(e) FROM Emargement e GROUP BY e.professeur.id", Object[].class)
                .getResultList();
        entityManager.close();

        return resultList.stream()
                .collect(Collectors.toMap(
                        row -> String.valueOf(row[0]), // Conversion explicite de Long en String
                        row -> (Long) row[1]
                ));
    }

    public Map<LocalDate, Long> getNombreEmargementParDate(LocalDate debut, LocalDate fin) {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory().createEntityManager();
        List<Object[]> resultList = entityManager.createQuery(
                        "SELECT e.date, COUNT(e) FROM Emargement e WHERE e.date BETWEEN :debut AND :fin GROUP BY e.date ORDER BY e.date", Object[].class)
                .setParameter("debut", debut)
                .setParameter("fin", fin)
                .getResultList();
        entityManager.close();

        return resultList.stream()
                .collect(Collectors.toMap(
                        row -> (LocalDate) row[0],
                        row -> (Long) row[1]
                ));
    }

//    public Map<LocalDate, Long> getNombreEmargementParDateEtStatut(LocalDate debut, LocalDate fin, boolean estAbsent) {
//        EntityManager entityManager = JPAUtils.getEntityManagerFactory().createEntityManager();
//        List<Object[]> resultList = entityManager.createQuery(
//                        "SELECT e.date, COUNT(e) FROM Emargement e " +
//                                "WHERE e.date BETWEEN :debut AND :fin " +
//                                "AND e.statut " + (estAbsent ? "=" : "!=") + " 'Absent' " +
//                                "GROUP BY e.date ORDER BY e.date", Object[].class)
//                .setParameter("debut", debut)
//                .setParameter("fin", fin)
//                .getResultList();
//        entityManager.close();
//
//        return resultList.stream()
//                .collect(Collectors.toMap(
//                        row -> (LocalDate) row[0],
//                        row -> (Long) row[1]
//                ));
//    }


//    public Map<String, Double> getTauxPresenceParCours() {
//        EntityManager entityManager = JPAUtils.getEntityManagerFactory().createEntityManager();
//
//        // Récupère le nombre total d'émargements par cours
//        List<Object[]> totalEmargements = entityManager.createQuery(
//                        "SELECT e.cours.id, COUNT(e) FROM Emargement e GROUP BY e.cours.id", Object[].class)
//                .getResultList();
//
//        // Récupère le nombre d'émargements avec statut "Présent" par cours
//        List<Object[]> presenceEmargements = entityManager.createQuery(
//                        "SELECT e.cours.id, COUNT(e) FROM Emargement e WHERE e.statut = 'A L''Heure' OR e.statut = 'En Retard' GROUP BY e.cours.id", Object[].class)
//                .getResultList();
//
//        entityManager.close();
//
//        // Modification : Convertir l'ID du cours en String lors de la création de la map
//        Map<String, Long> totalMap = totalEmargements.stream()
//                .collect(Collectors.toMap(
//                        row -> String.valueOf(row[0]),  // Conversion explicite en String
//                        row -> (Long) row[1]
//                ));
//
//        Map<String, Long> presenceMap = presenceEmargements.stream()
//                .collect(Collectors.toMap(
//                        row -> String.valueOf(row[0]),  // Conversion explicite en String
//                        row -> (Long) row[1]
//                ));
//
//        return totalMap.entrySet().stream()
//                .collect(Collectors.toMap(
//                        Map.Entry::getKey,
//                        entry -> {
//                            Long total = entry.getValue();
//                            Long presences = presenceMap.getOrDefault(entry.getKey(), 0L);
//                            return total > 0 ? (double) presences / total : 0.0;
//                        }
//                ));
//    }

    public List<Emargement> findByDateRange(LocalDate dateDebut, LocalDate dateFin) {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Emargement> query = entityManager.createQuery(
                    "SELECT e FROM Emargement e WHERE e.date BETWEEN :dateDebut AND :dateFin ORDER BY e.date",
                    Emargement.class);
            query.setParameter("dateDebut", dateDebut);
            query.setParameter("dateFin", dateFin);

            List<Emargement> emargements = query.getResultList();
            return emargements;
        } catch (NoResultException e) {
            return FXCollections.observableArrayList(); // Retourne une liste vide si aucun résultat
        } finally {
            entityManager.close();
        }
    }

    public Map<LocalDate, Long> getNombreEmargementParDateEtStatut(LocalDate debut, LocalDate fin, boolean estAbsent) {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory().createEntityManager();
        try {
            // Récupérer toutes les dates entre debut et fin
            List<LocalDate> toutesLesDates = debut.datesUntil(fin.plusDays(1))
                    .collect(Collectors.toList());

            // Requête pour obtenir les nombres d'émargements par date
            List<Object[]> resultList = entityManager.createQuery(
                            "SELECT e.date, COUNT(e) FROM Emargement e " +
                                    "WHERE e.date BETWEEN :debut AND :fin " +
                                    "AND e.statut " + (estAbsent ? "=" : "!=") + " 'Absent' " +
                                    "GROUP BY e.date ORDER BY e.date", Object[].class)
                    .setParameter("debut", debut)
                    .setParameter("fin", fin)
                    .getResultList();

            // Convertir en map avec toutes les dates
            Map<LocalDate, Long> emargementParDate = toutesLesDates.stream()
                    .collect(Collectors.toMap(
                            date -> date,
                            date -> 0L
                    ));

            // Mettre à jour avec les résultats réels
            resultList.forEach(row -> {
                LocalDate date = (LocalDate) row[0];
                Long count = (Long) row[1];
                emargementParDate.put(date, count);
            });

            return emargementParDate;

        } finally {
            entityManager.close();
        }
    }

//    public double getTauxPresencePourCours(Long coursId) {
//        EntityManager entityManager = JPAUtils.getEntityManagerFactory().createEntityManager();
//
//        // Total des émargements pour ce cours
//        Long totalEmargements = entityManager.createQuery(
//                        "SELECT COUNT(e) FROM Emargement e WHERE e.cours.id = :coursId", Long.class)
//                .setParameter("coursId", coursId)
//                .getSingleResult();
//
//        // Émargements présents pour ce cours
//        Long presentsEmargements = entityManager.createQuery(
//                        "SELECT COUNT(e) FROM Emargement e " +
//                                "WHERE e.cours.id = :coursId AND (e.statut = 'A L''Heure' OR e.statut = 'En Retard')", Long.class)
//                .setParameter("coursId", coursId)
//                .getSingleResult();
//
//        entityManager.close();
//
//        // Calculer le taux de présence
//        return totalEmargements > 0 ? (double) presentsEmargements / totalEmargements : 0.0;
//    }

    public Map<String, Double> getTauxPresenceParCours() {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory().createEntityManager();

        try {
            // Récupérer tous les cours
            List<Cours> tousCours = entityManager.createQuery("SELECT c FROM Cours c", Cours.class).getResultList();

            // Calculer les taux de présence pour chaque cours
            Map<String, Double> tauxPresenceInitiaux = tousCours.stream()
                    .collect(Collectors.toMap(
                            cours -> cours.getId().toString(),
                            cours -> getTauxPresencePourCours(cours.getId())
                    ));

            // Normaliser pour que le total soit 100%
            double totalTaux = tauxPresenceInitiaux.values().stream()
                    .mapToDouble(Double::doubleValue)
                    .sum();

            return tauxPresenceInitiaux.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> (entry.getValue() / totalTaux) * 100.0
                    ));

        } finally {
            entityManager.close();
        }
    }


    public double getTauxPresencePourCours(Long coursId) {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory().createEntityManager();

        try {
            // Requête pour compter le nombre total d'émargements pour ce cours
            Long totalEmargements = entityManager.createQuery(
                            "SELECT COUNT(e) FROM Emargement e WHERE e.cours.id = :coursId", Long.class)
                    .setParameter("coursId", coursId)
                    .getSingleResult();

            // Requête pour compter le nombre d'émargements présents pour ce cours
            Long presentsEmargements = entityManager.createQuery(
                            "SELECT COUNT(e) FROM Emargement e " +
                                    "WHERE e.cours.id = :coursId " +
                                    "AND (e.statut = 'A L''Heure' OR e.statut = 'En Retard')", Long.class)
                    .setParameter("coursId", coursId)
                    .getSingleResult();

            // Calculer le taux de présence
            // Si pas d'émargements, retourner 0
            if (totalEmargements == 0) {
                return 0.0;
            }

            // Calculer le pourcentage de présence
            return (double) presentsEmargements / totalEmargements * 100.0;

        } catch (Exception e) {
            // Gestion des erreurs
            e.printStackTrace();
            return 0.0;
        } finally {
            // Toujours fermer l'EntityManager
            entityManager.close();
        }
    }
}
