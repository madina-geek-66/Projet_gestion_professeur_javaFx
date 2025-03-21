package sn.groupeisi.projetgestionprofesseurs.dao;

import java.util.List;

public interface IEmargement<Emargement> {
    public void ajouterEmargement(Emargement emargement);
    public List<Emargement> getEmargementsParProfesseur(String professeurId);
    public Emargement getEmargementParCoursEtProf(String coursId, String professeurId);
}
