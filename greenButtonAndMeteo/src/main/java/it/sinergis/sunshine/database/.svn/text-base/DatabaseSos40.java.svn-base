package it.sinergis.sunshine.database;

import it.sinergis.sunshine.sos40.pojo.Numericvalue;
import it.sinergis.sunshine.sos40.pojo.Observation;
import it.sinergis.sunshine.sos40.pojo.Observationhasoffering;
import it.sinergis.sunshine.util.HibernateUtil;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class DatabaseSos40 {
	/** Logger. */
	private Logger logger = Logger.getLogger(this.getClass());
	
	public final boolean delete(List<String> listIdentifier, Date baseTime) {
		Transaction tx = null;
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			//criteria.add(Restrictions.eq("identifier", identifier));
			//identifier = identifier.concat("%");
			
			for (String identifier : listIdentifier) {
				Criteria criteria = session.createCriteria(Observation.class);
				criteria.add(Restrictions.conjunction()
						.add(Restrictions.like("identifier", identifier, MatchMode.START))
						.add(Restrictions.le("resulttime", baseTime)));
				
				List<?> result = criteria.list();
				List<Observation> resultOb = (List<Observation>) result;
				for (Observation ob : resultOb) {
					criteria = session.createCriteria(Numericvalue.class);
					criteria.add(Restrictions.eq("observationid", ob.getObservationid()));
					List<?> resultNum = criteria.list();
					List<Numericvalue> resultNumeric = (List<Numericvalue>) resultNum;
					
					criteria = session.createCriteria(Observationhasoffering.class);
					criteria.add(Restrictions.eq("observationid", ob.getObservationid()));
					List<?> resultObHo = criteria.list();
					List<Observationhasoffering> resultObHasOff = (List<Observationhasoffering>) resultObHo;
					
					for (Numericvalue numV : resultNumeric) {
						session.delete(numV);
					}
					for (Observationhasoffering obhas : resultObHasOff) {
						session.delete(obhas);
					}
					session.delete(ob);
				}
			}
			
		}
		catch (HibernateException e) {
			System.out.println(e);
			if (tx != null)
				tx.rollback();
			logger.error(e);
			return false;
		}
		return true;
	}
	
	public static void main(String args[]) {
		/*
		 * String like = "http://www.sunshineproject.eu/swe/observation/TRN-F03_TEMP_CEL_1h"; String notLike =
		 * "http://www.sunshineproject.eu/swe/observation/TRN-F03_TEMP_CEL_1h/2015-03-13/"; DatabaseSos40 a = new
		 * DatabaseSos40(); a.delete2(like, notLike);
		 */
		//String s = "http://www.sunshineproject.eu/swe/observation/FER-006_GASR_MCU_";
		String s = "http://www.sunshineproject.eu/swe/observation/TRN-020_ELER_KWH_15";
		DatabaseSos40 a = new DatabaseSos40();
		Date d = a.getLastObservationDate(s);
		System.out.println(d);
		
	}
	
	public Date getLastObservationDate(String identifier) {
		Transaction tx = null;
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			Criteria criteria = session.createCriteria(Observation.class);
			criteria.add(Restrictions.conjunction().add(Restrictions.like("identifier", identifier, MatchMode.START)));
			criteria.addOrder(Order.desc("resulttime"));
			criteria.setMaxResults(1);
			List<?> result = criteria.list();
			List<Observation> resultOb = (List<Observation>) result;
			for (Observation ob : resultOb) {
				tx.commit();
				//session.close();
				
				return ob.getResulttime();
			}
			
		}
		catch (HibernateException e) {
			System.out.println(e);
			if (tx != null)
				tx.rollback();
			logger.error(e);
			return null;
		}
		
		return null;
		
	}
	
	public final boolean delete2(String like, String notLike) {
		Transaction tx = null;
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			Criteria criteria = session.createCriteria(Observation.class);
			criteria.add(Restrictions.conjunction().add(Restrictions.like("identifier", like, MatchMode.START))
					.add(Restrictions.not(Restrictions.like("identifier", notLike, MatchMode.START))));
			List<?> result = criteria.list();
			List<Observation> resultOb = (List<Observation>) result;
			int i = 0;
			for (Observation ob : resultOb) {
				System.out.println(i++ + "," + ob.getIdentifier());
				
				criteria = session.createCriteria(Numericvalue.class);
				criteria.add(Restrictions.eq("observationid", ob.getObservationid()));
				List<?> resultNum = criteria.list();
				List<Numericvalue> resultNumeric = (List<Numericvalue>) resultNum;
				System.out.println(resultNumeric.size());
				
				criteria = session.createCriteria(Observationhasoffering.class);
				criteria.add(Restrictions.eq("observationid", ob.getObservationid()));
				List<?> resultObHo = criteria.list();
				List<Observationhasoffering> resultObHasOff = (List<Observationhasoffering>) resultObHo;
				
				for (Numericvalue numV : resultNumeric) {
					session.delete(numV);
					System.out.println(numV.getObservationid());
				}
				for (Observationhasoffering obhas : resultObHasOff) {
					session.delete(obhas);
				}
				session.delete(ob);
				
			}
			tx.commit();
			
		}
		catch (HibernateException e) {
			System.out.println(e);
			if (tx != null)
				tx.rollback();
			logger.error(e);
			return false;
		}
		
		return true;
	}
}
