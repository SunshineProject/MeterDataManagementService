package it.sinergis.sunshine.jpadao;

import javax.persistence.EntityManager;

import it.sinergis.sunshine.persistence.PersistenceManager;

/**
 * Classe astratta che definisce dei metodi di base per le classi DAO.
 *
 * @author  Rossana Bambili
 */
public abstract class AbstractJpaDAO
{
    /*
     * //i metodi di EntityManagerFactory sono thread-safe non ci sono problemi //a dichiararlo static e ad accederlo in
     * modo non synchronized private static EntityManagerFactory emf =
     * Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT);
     *
     * public EntityManager getEntityManager() { return emf.createEntityManager(); }
     */
    /**
     * ritorna l'entity manager.
     *
     * @return  EntityManager da una istanza del PersistenceManager
     */
    public EntityManager getEntityManager(String persistence_unit)
    {
        return PersistenceManager.getInstance(persistence_unit).getEntityManager(persistence_unit);
    }
}
