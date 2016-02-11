package it.sinergis.sunshine.database;

import it.sinergis.sunshine.sos40.pojo.TaskStatus;
import it.sinergis.sunshine.util.HibernateUtil;

import java.util.Date;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class DatabaseCreateLog {
	private static Logger logger = Logger.getLogger(DatabaseCreateLog.class);
	
	public static final TaskStatus createLogField(int idprocess) {
		
		Transaction tx = null;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			
			tx = session.beginTransaction();
			
			//criteria.add(Restrictions.eq("identifier", identifier));
			//identifier = identifier.concat("%");
			TaskStatus ts = new TaskStatus();
			ts.setIdProcess(idprocess);
			ts.setStartProcess(new Date());
			ts.setStatus(TaskStatus.START);
			session.persist(ts);
			
			//Commit the transaction
			session.flush();
			session.getTransaction().commit();
			
			System.out.println("sessione:" + ts.getId());
			return ts;
		}
		catch (HibernateException e) {
			System.out.println(e);
			if (tx != null)
				tx.rollback();
			//logger.error(e);
			//return null;
		}
		finally {
			//session.close();
		}
		return null;
	}
	
	public static final TaskStatus updateLog(TaskStatus taskStatus) {
		
		Transaction tx = null;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			//Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			//criteria.add(Restrictions.eq("identifier", identifier));
			//identifier = identifier.concat("%");
			//ts.setIdProcess(idprocess);
			taskStatus.setLastUpdate(new Date());
			
			session.update(taskStatus);
			
			//Commit the transaction
			session.getTransaction().commit();
			
			return taskStatus;
		}
		catch (HibernateException e) {
			System.out.println(e);
			if (tx != null)
				tx.rollback();
			logger.error(e);
			//return null;
		}
		finally {
			//session.close();
		}
		return null;
	}
	
	public static final TaskStatus closeLog(TaskStatus taskStatus) {
		
		Transaction tx = null;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		
		try {
			tx = session.beginTransaction();
			taskStatus.setLastUpdate(new Date());
			taskStatus.setEndProcess(new Date());
			session.update(taskStatus);
			session.getTransaction().commit();
			
			return taskStatus;
		}
		catch (HibernateException e) {
			System.out.println(e);
			if (tx != null)
				tx.rollback();
			logger.error(e);
			//return null;
		}
		finally {
			//session.close();
		}
		return null;
	}
	
	public static void main(String args[]) {
		TaskStatus ts = DatabaseCreateLog.createLogField(TaskStatus.GRENBUTTONID);
		//System.out.println(ts.getId());
		System.out.println("sessione al ritorno:" + ts.getId());
		ts.setDescription("Start import meteo");
		ts.setStatus(TaskStatus.RUNNING);
		DatabaseCreateLog.updateLog(ts);
		ts.setStatus(TaskStatus.COMPLETE);
		ts.setDescription("meteo data import rigthly");
		DatabaseCreateLog.closeLog(ts);
		
	}
}
