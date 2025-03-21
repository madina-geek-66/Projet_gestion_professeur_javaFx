package sn.groupeisi.projetgestionprofesseurs.controllers;

import sn.groupeisi.projetgestionprofesseurs.entities.User;

public class AccueilSceneController {
    private User currentUser = new User();
    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
