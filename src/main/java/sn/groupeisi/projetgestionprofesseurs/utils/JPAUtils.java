package sn.groupeisi.projetgestionprofesseurs.utils;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


public class JPAUtils {
    private static final String PERSISTENCE_UNIT_NAME = "PERSISTENCE_POSTGRES";
    private static EntityManagerFactory factory;

    public static EntityManagerFactory getEntityManagerFactory() {
        if(factory == null) {
            factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        }

        return factory;
    }
}
