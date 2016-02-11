package it.sinergis.sunshine.jpadao;

import it.sinergis.sunshine.util.Constants;
import it.sinergis.sunshine.dao.XemtecDevicesDAO;
import it.sinergis.sunshine.model.XemtecDevices;

import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;

/**
 * Classe per la gestione della JpaDAO dei dispositivi di xemtec. 
 * 
 * @author Rossana Bambili
 */
public class JpaXemtecDevicesDAO extends AbstractJpaDAO implements XemtecDevicesDAO {
	private final static Logger logger = Logger.getLogger(JpaXemtecDevicesDAO.class);

	
   /**
	 * Restituisce il dispositivo associato ad un determinato id
	 * @param  id device
	 * @return  XemtecDevices
   */
   public XemtecDevices findById(Long idDevice) throws SQLException, Exception {

		logger.debug("Eseguo query findById");

		EntityManager em = getEntityManager(Constants.PERSISTENCE_XEMTEC);
		XemtecDevices device = null;
		try {
			Query query = em.createNamedQuery("XemtecDevices.findById");

			query.setParameter("idDevice", idDevice);
			
			device = (XemtecDevices) query.getSingleResult();

		 } catch(NoResultException nre) {
		    	return null;
	     } catch(NonUniqueResultException nure) {
	    	return null;
	     }
	     finally {
	   		em.close();
		 }   
	
		return device;
	}
	
	

}
