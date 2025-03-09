module sn.groupeisi.projetgestionprofesseurs {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.persistence;
    requires org.hibernate.orm.core;
    requires java.sql;
    requires de.jensd.fx.glyphs.fontawesome;
    requires java.management;
    requires jbcrypt;


    opens sn.groupeisi.projetgestionprofesseurs.main to javafx.fxml;
    exports sn.groupeisi.projetgestionprofesseurs.main;

    opens sn.groupeisi.projetgestionprofesseurs.controllers to javafx.fxml;
    exports sn.groupeisi.projetgestionprofesseurs.controllers;


    opens sn.groupeisi.projetgestionprofesseurs.entities to org.hibernate.orm.core;
    exports sn.groupeisi.projetgestionprofesseurs.entities;

    opens sn.groupeisi.projetgestionprofesseurs.utils to org.hibernate.orm.core;
    exports sn.groupeisi.projetgestionprofesseurs.utils;
}