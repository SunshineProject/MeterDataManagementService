package it.sinergis.sunshine.jpadao;

import it.sinergis.sunshine.util.Constants;
import it.sinergis.sunshine.dao.XemtecValuesDAO;
import it.sinergis.sunshine.model.XemtecValues;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.log4j.Logger;

/**
 * Classe per la gestione della JpaDAO dei dispositivi di xemtec. 
 * 
 * @author Rossana Bambili
 */
public class JpaXemtecValuesDAO extends AbstractJpaDAO implements XemtecValuesDAO {
	private final static Logger logger = Logger.getLogger(JpaXemtecValuesDAO.class);

	
   /**
	 * Restituisce il dispositivo associato ad un determinato id
	 * @param  id device
	 * @return  XemtecDevices
   */
   public Collection<XemtecValues> findById(Long idDevice) throws SQLException, Exception {

		logger.debug("Eseguo query findById");

		EntityManager em = getEntityManager(Constants.PERSISTENCE_XEMTEC);
		Collection<XemtecValues> result = null;
        
		try
	    {
		   Query query = em.createNamedQuery("XemtecValues.findByIdDevice");

		   query.setParameter("idDevice", idDevice);
			
		   List  l = query.getResultList();
           int   numResult = l.size();
           result = new ArrayList<XemtecValues>(numResult);
   
           for (int i = 0; i < numResult; i++)
           {
        	   XemtecValues xemtecValues = (XemtecValues) l.get(i);
               result.add(xemtecValues);
           }
	    }
		finally
		{
		    em.close();
		}
        return result;		

 
	}
	
   /**
    * Restituisce le letture associate ad un dispositivo maggiori della data
    * passata come parametro
    * @param  id device
    * @param  startdate
    * @return   Collection XemtecValues
    */
   public Collection<XemtecValues> findByDate(Long idDevice,Date startDate) throws SQLException, Exception{
	   
	   logger.debug("Eseguo query findByDate");

		EntityManager em = getEntityManager(Constants.PERSISTENCE_XEMTEC);
		Collection<XemtecValues> result = null;
       
		try
	    {
		  Query query = em.createNamedQuery("XemtecValues.findByDate");

		  query.setParameter("idDevice", idDevice);
		  query.setParameter("startDate", startDate);
			
		  List  l = query.getResultList();
          int   numResult = l.size();
          result = new ArrayList<XemtecValues>(numResult);
  
          for (int i = 0; i < numResult; i++)
          {
       	   XemtecValues xemtecValues = (XemtecValues) l.get(i);
              result.add(xemtecValues);
          }
	    }
		finally
		{
		    em.close();
		}
       return result;		
	   
   }
	

}
