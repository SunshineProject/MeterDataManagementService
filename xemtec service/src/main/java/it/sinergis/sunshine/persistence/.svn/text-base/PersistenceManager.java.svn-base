package it.sinergis.sunshine.persistence;

import java.util.HashMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;

/**
 * Classe che gestisce l'Entity Manager Factory per il collegamento con la base dati.
 * 
 * @author Rossana Bambili <br/>
 */
public class PersistenceManager {
  
    /** Struttura contenente i dati del persistence. */
    private static HashMap<String,PersistenceManager> persistenceManagerList = null;

    /** Puntatore all'entity manager factory. */
	private EntityManagerFactory emf = null;

    /** Il logger di log4j. */
    private static Logger logger = null;

    /**
     * Crea una nuova istanza della classe.
     */
    private PersistenceManager(String persistence_unit) {
    	logger = Logger.getLogger(PersistenceManager.class);
		if (logger.isDebugEnabled())
		{
			logger.debug("Inizializzo l'EntityManagerFactory.");
		}
		emf = Persistence.createEntityManagerFactory(persistence_unit);
    }

    /**
     * Restituisce un'istanza singletone della classe.
     * 
     * @return PersistenceManager Restituisce un'istanza singletone della classe.
     */
    public static synchronized PersistenceManager getInstance(String persistence_unit) {
    	if (persistenceManagerList==null) {
    		persistenceManagerList = new HashMap<String,PersistenceManager>();
    	}
        if (persistenceManagerList.get(persistence_unit)==null) {
        	persistenceManagerList.put(persistence_unit, new PersistenceManager(persistence_unit));
            logger.debug("Creata nuova istanza del PersistenceManager.");
        }

        // logger.debug("Restituito il PersistenceManager.");

        return persistenceManagerList.get(persistence_unit);
    }
    
    public static synchronized HashMap<String,PersistenceManager> getAllInstance() {
              // logger.debug("Restituito il PersistenceManager.");

        return persistenceManagerList;
    }

    /**
     * Chiude il collegamento con la base dati.
     */
    public synchronized void closeEntityManagerFactory() {
        if ((emf != null) && (emf.isOpen() == true)) {
            emf.close();
            logger.debug("Chiuso l'EntityManagerFactory.");
        }
    }

    /**
     * Restituisce un entity manager.
     * 
     * @return entity manager factory
     */
    public EntityManager getEntityManager(String persistence_unit) {
        EntityManagerFactory entManFactory = PersistenceManager.getInstance(persistence_unit)
                .getEntityManagerFactory();
        EntityManager entMan = null;

        entMan = entManFactory.createEntityManager();

        return entMan;
    }

    /**
     * Restituisce un puntatore all'entity manager factory.
     * 
     * @return entity manager factory
     */
    public EntityManagerFactory getEntityManagerFactory() {
        // logger.debug("Restituito l'EntityManagerFactory.");

        return emf;
    }
}
