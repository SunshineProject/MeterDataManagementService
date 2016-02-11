package it.sinergis.sunshine.listener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import it.sinergis.sunshine.persistence.PersistenceManager;

/**
 * Classe che viene invocata in fase di apertura e chiusura dell'applicazione per gestire il collegamento con la base
 * dati.
 *
 * @author  Rossana Bambili <br/>
 */
public class PersistenceContextListener implements ServletContextListener
{
    /** Il logger di log4j. */
    private static Logger logger = Logger.getLogger(PersistenceContextListener.class);

    /**
     * Costruttore Crea un nuovo PersistenceContextListener.java.
     */
    public PersistenceContextListener()
    { }

    /**
     * Chiude l'entity manager Factory.
     *
     * @param  evt  evento associato al contesto
     */
    public void contextDestroyed(ServletContextEvent evt)
    {
        // quando l'applicazione viene chiusa chiudo l'EntityManagerFactory
        /*        EntityManagerFactory emf = Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT);
         *      emf.close();     logger.debug("Chiuso l'EntityManagerFactory in seguito allo shutdown
         * dell'applicazione.");  */

        // quando l'applicazione viene chiusa chiudo l'EntityManagerFactory
    	
    	HashMap<String,PersistenceManager> persistenceManagerList =  PersistenceManager.getAllInstance();
    	Set list  = persistenceManagerList.keySet();
    	Iterator iter = list.iterator();
    				
    	while(iter.hasNext()) {
    	     String key = (String) iter.next();
    	     PersistenceManager pm = persistenceManagerList.get(key);
    	     pm.closeEntityManagerFactory();
    	}
       
        logger.debug("Chiuso l'EntityManagerFactory in seguito allo shutdown dell'applicazione.");
    }

    /**
     * Contesto inizializzato.
     *
     * @param  evt  evento associato al contesto
     */
    public void contextInitialized(ServletContextEvent evt)
    {
        logger.debug("PersistenceContextListener inizializzato correttamente.");
    }
}
